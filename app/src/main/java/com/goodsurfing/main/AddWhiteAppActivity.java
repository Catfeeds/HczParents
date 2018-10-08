package com.goodsurfing.main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.goodsurfing.adpter.AddAppListAdapter;
import com.goodsurfing.adpter.AddAppListAdapter.SelectDelegate;
import com.goodsurfing.app.R;
import com.goodsurfing.base.BaseFragment;
import com.goodsurfing.beans.WebFilterBean;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.database.dao.AppDao;
import com.goodsurfing.server.AllowedWebsServer;
import com.goodsurfing.server.GetAppListServer;
import com.goodsurfing.server.PutDataServer;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResponder;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResult;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.SharUtil;
import com.goodsurfing.view.SlideListView;
import com.goodsurfing.view.pullrefresh.ui.PullToRefreshBase;
import com.goodsurfing.view.pullrefresh.ui.PullToRefreshBase.OnRefreshListener;
import com.goodsurfing.view.pullrefresh.ui.PullToRefreshListView;

public class AddWhiteAppActivity extends BaseFragment implements OnClickListener, SelectDelegate {
	private final static String TAG = "AddWhiteAppActivity";
	protected static final int REFRESH = 100;
	protected static final int REFRESH_DATA = 101;
	protected static final int GET_WEBSINFO = 102;

	private SimpleDateFormat mDateFormat = new SimpleDateFormat("MM-dd HH:mm");

	/** listView 相关 **/
	private AddAppListAdapter Adapter;
	public static List<WebFilterBean> listAdapter = new ArrayList<WebFilterBean>();
	private SlideListView mlistView;
	private PullToRefreshListView mPullListView;
	private RelativeLayout nodataView;
	private EditText searchView;
	private String domainID = "";
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFRESH:
				if (listAdapter.size() == 0) {
					nodataView.setVisibility(View.VISIBLE);
					AddBlackWhiteListActivity.comfirm.setVisibility(View.INVISIBLE);
				} else {
					AddBlackWhiteListActivity.comfirm.setVisibility(View.VISIBLE);
					AddBlackWhiteListActivity.comfirm.setText("确定");
					mlistView.slideBack();
				}
				Adapter.notifyDataSetChanged();
				break;
			case REFRESH_DATA:
				listAdapter.clear();
				listAdapter.addAll(Constants.appDao.getWebFilterBeanList());
				if (listAdapter.size() == 0) {
					nodataView.setVisibility(View.VISIBLE);
					AddBlackWhiteListActivity.comfirm.setVisibility(View.INVISIBLE);
				} else {
					AddBlackWhiteListActivity.comfirm.setVisibility(View.VISIBLE);
					AddBlackWhiteListActivity.comfirm.setText("确定");
				}
				Adapter.notifyDataSetChanged();
				break;
			case GET_WEBSINFO:
				// getWebsInfo();
				break;
			}
		};
	};
	private View rootView;
	private ImageView deletView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		if (rootView == null) {
			rootView = LayoutInflater.from(getActivity()).inflate(R.layout.activity_add_app, null);
			initViews(rootView);
			init();
		}
		ViewGroup group = (ViewGroup) rootView.getParent();
		if (group != null) {
			group.removeView(rootView);
		}
		return rootView;
	}

	private void initViews(View view) {
		nodataView = (RelativeLayout) view.findViewById(R.id.activity_allow_nodata);
		mPullListView = (PullToRefreshListView) view.findViewById(R.id.add_app_search_lv);
		searchView = (EditText) view.findViewById(R.id.add_app_search_et);
		deletView = (ImageView) view.findViewById(R.id.add_app_search_et_delete_iv);
		searchView.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int aId, KeyEvent event) {
				switch (aId) {
				case EditorInfo.IME_ACTION_SEARCH:
					((InputMethodManager) searchView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
					deletView.setVisibility(View.VISIBLE);
					listAdapter.clear();
					listAdapter.addAll(Constants.appDao.getWebFilterBeanList4Name(v.getText().toString().trim()));
					mHandler.sendEmptyMessage(REFRESH);
					return true;
				}
				return false;
			}
		});
		deletView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				deletView.setVisibility(View.GONE);
				searchView.setText("");
				mHandler.sendEmptyMessage(REFRESH_DATA);
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		Adapter.setDelegate(this);
		editOnClick();
		if (!"".equals(Constants.userId)) {
			if (SharUtil.getCacheTimeOut(getActivity(), Constants.ALLOWEDWEB_TIME, System.currentTimeMillis() + "")) {
				mHandler.sendEmptyMessageDelayed(GET_WEBSINFO, 500);
			} else {
				listAdapter.clear();
				listAdapter.addAll(Constants.appDao.getWebFilterBeanList());
				if (listAdapter.size() > 0) {
					Adapter.notifyDataSetChanged();
					AddBlackWhiteListActivity.comfirm.setVisibility(View.VISIBLE);
				} else {
					nodataView.setVisibility(View.VISIBLE);
					AddBlackWhiteListActivity.comfirm.setVisibility(View.INVISIBLE);
				}
			}
		} else {
			nodataView.setVisibility(View.VISIBLE);
			AddBlackWhiteListActivity.comfirm.setVisibility(View.INVISIBLE);
			listAdapter.clear();
			Adapter.notifyDataSetChanged();
		}
		AddBlackWhiteListActivity.index = 0;
	}

	@Override
	public void onPause() {
		super.onPause();
		Adapter.setDelegate(this);
		clearView(false);
		Adapter.setSelectALL(false);
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	private void init() {
		mPullListView.setPullLoadEnabled(false);
		mPullListView.setScrollLoadEnabled(false);
		Adapter = new AddAppListAdapter(getActivity(), R.layout.item_add_app, listAdapter, this, true);
		mlistView = (SlideListView) mPullListView.getRefreshableView();
		mlistView.setAdapter(Adapter);
		mlistView.setDivider(null);
		mlistView.initSlideMode(SlideListView.MOD_FORBID);
		mlistView.setRightLength(getResources().getDimensionPixelSize(R.dimen.item_right_delete));
		setLastUpdateTime();
		mPullListView.setHasMoreData(false);
		mPullListView.setPullRefreshEnabled(true);
		getActivity().registerReceiver(receiver, new IntentFilter("AddWhiteAppActivity"));
		mPullListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				setLastUpdateTime();
				if (!"".equals(Constants.userId)) {
					getAppsInfo();
				}
				mPullListView.onPullDownRefreshComplete();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				mPullListView.onPullDownRefreshComplete();
			}

		});
	}

	/**
	 * 获取所有APP的列表
	 */
	private void getAppsInfo() {
		if ("".equals(Constants.userId)) {
			LoginActivity.gotoLogin(getActivity());
			return;
		}
		String url = null;
		url = Constants.SERVER_URL + "?" + "requesttype=15" + "&userid=" + Constants.userId + "&token=" + Constants.tokenID + "&type=1";
		ActivityUtil.showPopWindow4Tips(getActivity(), AddBlackWhiteListActivity.contentView, false, true, "正在刷新...", -1);

		new GetAppListServer(new DataServiceResponder() {

			@Override
			public void onResult(DataServiceResult result) {
				if (result == null || result.code == null) {
					return;
				}
				ActivityUtil.dismissPopWindow();
				if ("0".equals(result.code)) {
					if (((List<WebFilterBean>) result.result).size() > listAdapter.size()) {
						Constants.appDao.insert((List<WebFilterBean>) result.result);
						mHandler.sendEmptyMessage(REFRESH_DATA);
					}else if(((List<WebFilterBean>) result.result).size() <listAdapter.size()){
						Constants.appDao.deleteAllWebFilterBean();
						Constants.appDao.insert((List<WebFilterBean>) result.result);
						mHandler.sendEmptyMessage(REFRESH_DATA);
					}
				}
			}

			@Override
			public void onFailure() {
			}
		}, url, getActivity()).execute();

	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			refreshAppInfo();
		}
	};

	private void setLastUpdateTime() {
		String text = formatDateTime(System.currentTimeMillis());
		mPullListView.setLastUpdatedLabel(text);
	}

	private String formatDateTime(long time) {
		if (0 == time) {
			return "";
		}
		return mDateFormat.format(new Date(time));
	}

	public void editOnClick() {
		clearView(false);
		Adapter.notifyDataSetChanged();
	}

	@Override
	public void selectItem(int position, boolean isSelected) {
		Adapter.notifyDataSetChanged();
	}

	public void clearView(boolean select) {
		for (int i = 0; i < listAdapter.size(); i++) {
			listAdapter.get(i).setSelected(select);
		}
	}

	private int getSelectedNumbers() {
		int count = 0;
		domainID = "";
		for (int i = 0; i < listAdapter.size(); i++) {
			if (listAdapter.get(i).isSelected()) {
				count++;
				domainID += listAdapter.get(i).getId() + ",";
			}
		}
		return count;
	}

	@Override
	public void startWebControl(int position, String status) {
	}

	/**
	 * 添加到白名单
	 * 
	 * @param position
	 * @param status
	 */
	public void refreshAppInfo() {
		if (!Constants.isNetWork) {
			ActivityUtil.showPopWindow4Tips(getActivity(), AddBlackWhiteListActivity.contentView, false, true, "当前网络不可用，请稍后再试...", 2000);
			return;
		}
		String url = null;
		getSelectedNumbers();
		if (domainID == null || domainID.equals(""))
			return;
		String id = domainID.substring(0, domainID.length() - 1);
		url = Constants.SERVER_URL + "?" + "&userid=" + Constants.userId + "&appIDs=" + id + "&committype=22&token=" + Constants.tokenID + "&oper=1";
		ActivityUtil.showPopWindow4Tips(getActivity(), AddBlackWhiteListActivity.contentView, false, true, "添加中...", -1);
		new PutDataServer(new DataServiceResponder() {

			@Override
			public void onResult(DataServiceResult result) {
				if ("0".equals(result.code)) {
				((AddBlackWhiteListActivity)getActivity()).onBackPressed4Add();
//					String ids[] = domainID.split(",");
//					for (int i = 0; i < ids.length; i++) {
//						Constants.appDao.upData4head(ids[i], "1");
//					}
//					mHandler.sendEmptyMessage(REFRESH_DATA);
//					ActivityUtil.showPopWindow4Tips(getActivity(), AddBlackWhiteListActivity.contentView, true, true, "添加成功", 2000);
				} else {
					ActivityUtil.showPopWindow4Tips(getActivity(), AddBlackWhiteListActivity.contentView, false, true, result.extra + "", 2000);
				}
			}

			@Override
			public void onFailure() {
				mPullListView.onPullUpRefreshComplete();
			}
		}, url, getActivity()).execute();
	}

	@Override
	public void onClick(View view) {

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(receiver);
	}

}
