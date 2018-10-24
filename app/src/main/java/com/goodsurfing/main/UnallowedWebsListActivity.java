package com.goodsurfing.main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.goodsurfing.adpter.BlackWhiteWebListAdapter;
import com.goodsurfing.adpter.UnAllowsWebListAdapter.SelectDelegate;
import com.goodsurfing.app.R;
import com.goodsurfing.base.BaseFragment;
import com.goodsurfing.beans.WebFilterBean;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.main.BlackAndWhiteListActivity.DELEGATE;
import com.goodsurfing.server.AllowedWebsServer;
import com.goodsurfing.server.PutDataServer;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResponder;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResult;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.EventHandler;
import com.goodsurfing.utils.SharUtil;
import com.goodsurfing.view.SlideListView;
import com.goodsurfing.view.pullrefresh.ui.PullToRefreshBase;
import com.goodsurfing.view.pullrefresh.ui.PullToRefreshBase.OnRefreshListener;
import com.goodsurfing.view.pullrefresh.ui.PullToRefreshListView;
import com.lidroid.xutils.view.annotation.ViewInject;

public class UnallowedWebsListActivity extends BaseFragment implements SelectDelegate, DELEGATE, com.goodsurfing.adpter.BlackWhiteWebListAdapter.SelectDelegate, OnClickListener {

	protected static final int REFRESH = 100;
	protected static final int REFRESH_DATA = 101;
	protected static final int GET_WEBSINFO = 102;
	@ViewInject(R.id.activity_allow_webs_lv)
	private PullToRefreshListView mPullListView;
	private SimpleDateFormat mDateFormat = new SimpleDateFormat("MM-dd HH:mm");

	/** listView 相关 **/
	private BlackWhiteWebListAdapter Adapter;
	public static List<WebFilterBean> listAdapters = new ArrayList<WebFilterBean>();

	private SlideListView mlistView;

	@ViewInject(R.id.activity_allow_webs_select_ll)
	private LinearLayout deleteLinearLayout;

	@ViewInject(R.id.activity_allow_webs_add_ll)
	private LinearLayout addLinearLayout;

	@ViewInject(R.id.activity_allow_webs_numbs)
	private TextView numbsTextView;

	@ViewInject(R.id.activity_allow_webs_select_all)
	private TextView selectAllTv;

	@ViewInject(R.id.activity_allow_nodata)
	private RelativeLayout nodataView;

	@ViewInject(R.id.activity_allow_nodata_iv)
	private ImageView nodataImageView;

	@ViewInject(R.id.activity_allow_webs_delete)
	private TextView deletView;

	@ViewInject(R.id.title_layout)
	private View title_layout;
	private View rootView;
	private static String domainID = null;

	// public static Map<Integer,Boolean> list = new LinkedHashMap<Integer,
	// Boolean>();

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFRESH:
				if (Adapter.getSelectAllFlag()) {
					deleteAll();
					if (listAdapters.size() == 0) {
						nodataView.setVisibility(View.VISIBLE);
						BlackAndWhiteListActivity.comfirm.setVisibility(View.INVISIBLE);
					}
				} else {
					deleteSelected();
					if (listAdapters.size() == 0) {
						nodataView.setVisibility(View.VISIBLE);
						BlackAndWhiteListActivity.comfirm.setVisibility(View.INVISIBLE);
					} else {
						BlackAndWhiteListActivity.comfirm.setVisibility(View.VISIBLE);
						BlackAndWhiteListActivity.comfirm.setText("编辑");
						mlistView.slideBack();
					}
				}
				Constants.isEditing = false;
				domainID = null;
				numbsTextView.setText(listAdapters.size() + "个网址");
				deleteLinearLayout.setVisibility(View.GONE);
				addLinearLayout.setVisibility(View.VISIBLE);
				Adapter.notifyDataSetChanged();
				Constants.unAllWebList.clear();
				Constants.unAllWebList.addAll(listAdapters);
				clearView(false);
				break;
			case REFRESH_DATA:
				Adapter.notifyDataSetChanged();
				break;
			case GET_WEBSINFO:
				getWebsInfo();
				break;
			}
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = LayoutInflater.from(getActivity()).inflate(R.layout.activity_allow_webs, null);
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
		mPullListView = (PullToRefreshListView) view.findViewById(R.id.activity_allow_webs_lv);

		deleteLinearLayout = (LinearLayout) view.findViewById(R.id.activity_allow_webs_select_ll);

		addLinearLayout = (LinearLayout) view.findViewById(R.id.activity_allow_webs_add_ll);

		numbsTextView = (TextView) view.findViewById(R.id.activity_allow_webs_numbs);

		selectAllTv = (TextView) view.findViewById(R.id.activity_allow_webs_select_all);
		TextView add_Tv = (TextView) view.findViewById(R.id.activity_allow_webs_add_tv);
		deletView = (TextView) view.findViewById(R.id.activity_allow_webs_delete);
		nodataImageView = (ImageView) view.findViewById(R.id.activity_allow_nodata_iv);
		selectAllTv.setOnClickListener(this);
		addLinearLayout.setOnClickListener(this);
		deletView.setOnClickListener(this);
		add_Tv.setText("添加网址");
	}

	@Override
	public void onResume() {
		super.onResume();
		Adapter.setDelegate(this);
		BlackAndWhiteListActivity.setDelegate(this);
		BlackAndWhiteListActivity.comfirm.setVisibility(View.INVISIBLE);
		BlackAndWhiteListActivity.comfirm.setText("编辑");
		Constants.isEditing = false;
		editOnClick();
		if (!"".equals(Constants.userId)) {
			if (SharUtil.getCacheTimeOut(getActivity(), Constants.UNALLOWEDWED_TIME, System.currentTimeMillis() + "")) {
				mHandler.sendEmptyMessageDelayed(GET_WEBSINFO, 500);
			} else {
				if (Constants.unAllWebList.size() > 0) {
					listAdapters.clear();
					listAdapters.addAll(Constants.unAllWebList);
					Adapter.notifyDataSetChanged();
					BlackAndWhiteListActivity.comfirm.setVisibility(View.VISIBLE);
				} else {
					BlackAndWhiteListActivity.comfirm.setVisibility(View.INVISIBLE);
					nodataView.setVisibility(View.VISIBLE);
					nodataImageView.setImageResource(R.drawable.net_felter_nodata2);
					listAdapters.clear();
					Adapter.notifyDataSetChanged();
				}
			}
		} else {
			BlackAndWhiteListActivity.comfirm.setVisibility(View.INVISIBLE);
			nodataView.setVisibility(View.VISIBLE);
			nodataImageView.setImageResource(R.drawable.net_felter_nodata2);
			listAdapters.clear();
			Adapter.notifyDataSetChanged();
		}
		if (listAdapters.size() > 0) {
			numbsTextView.setVisibility(View.VISIBLE);
			numbsTextView.setText(listAdapters.size() + "个网址");
		} else {
			numbsTextView.setVisibility(View.INVISIBLE);
		}
		BlackAndWhiteListActivity.index = 1;
		ActivityUtil.sendEvent4UM(getActivity(), "functionSwitch", "blacklist", 27);

	}

	@Override
	public void onPause() {
		super.onPause();
		Adapter.setDelegate(null);
		BlackAndWhiteListActivity.setDelegate(null);
		Constants.isEditing = false;
		clearView(false);
		Adapter.setSelectALL(false);
		if (listAdapters.size() > 0) {
			mlistView.slideBack();
		}
	}

	private void init() {
		mPullListView.setPullLoadEnabled(false);
		mPullListView.setScrollLoadEnabled(false);
		Adapter = new BlackWhiteWebListAdapter(getActivity(), R.layout.item_web_filter_cell, listAdapters, this, false);
		mlistView = (SlideListView) mPullListView.getRefreshableView();
		mlistView.setAdapter(Adapter);
		mlistView.setDivider(null);
		mlistView.initSlideMode(SlideListView.MOD_RIGHT);
		mlistView.setRightLength(getResources().getDimensionPixelSize(R.dimen.item_right_delete));
		mPullListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				setLastUpdateTime();
				if (!"".equals(Constants.userId)) {
					getWebsInfo();
				}
				mPullListView.onPullDownRefreshComplete();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				mPullListView.onPullDownRefreshComplete();
			}

		});
		setLastUpdateTime();
		mPullListView.setHasMoreData(false);
		clearView(false);
	}

	private Dialog d;

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
		if (Constants.isEditing) {
			mlistView.slideBack();
			mlistView.initSlideMode(SlideListView.MOD_FORBID);
			deleteLinearLayout.setVisibility(View.VISIBLE);
			addLinearLayout.setVisibility(View.GONE);
		} else {
			mlistView.initSlideMode(SlideListView.MOD_RIGHT);
			deleteLinearLayout.setVisibility(View.GONE);
			addLinearLayout.setVisibility(View.VISIBLE);
		}
		clearView(false);
		Adapter.notifyDataSetChanged();

	}

	@Override
	public void selectItem(int position, boolean isSelected) {
		int num = getSelectedNumbers();
		if (num > 0) {
			deletView.setText("删除（" + num + "）");
			deletView.setTextColor(getResources().getColor(R.color.red));
			if (num == listAdapters.size()) {
				selectAllTv.setText("取消全选");
				Adapter.setSelectALL(true);
			} else {
				selectAllTv.setText("全选");
				Adapter.setSelectALL(false);
			}
		} else {
			selectAllTv.setText("全选");
			Adapter.setSelectALL(false);
			deletView.setText("删除");
			deletView.setTextColor(getResources().getColor(R.color.black));
		}
		Adapter.notifyDataSetChanged();
	}

	public void clearView(boolean select) {
		for (int i = 0; i < listAdapters.size(); i++) {
			listAdapters.get(i).setSelected(select);
		}
		try {
			selectItem(-1, true);
		} catch (Exception e) {
		}
	}

	public void OnSelectClick() {
		if (Adapter.getSelectAllFlag()) {
			clearView(false);
		} else {
			clearView(true);
		}
	}

	public void OnDeleteClick() {
		int num = getSelectedNumbers();
		if (num > 0) {
			d = ActivityUtil.getDialog(getActivity(), "是否删除", new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					removeWebInfo();
					d.dismiss();
				}
			}, null);
			d.show();
		} else {
			mHandler.sendEmptyMessage(REFRESH);
		}
	}

	private void deleteAll() {
		listAdapters.clear();
	}

	private void deleteSelected() {
		int num = getSelectedNumbers();
		while (num != 0) {
			for (int i = 0; i < listAdapters.size(); i++) {
				if (listAdapters.get(i).isSelected()) {
					listAdapters.remove(i);
					num--;
					break;
				}
			}
		}
	}

	private int getSelectedNumbers() {
		int count = 0;
		domainID = "";
		for (int i = 0; i < listAdapters.size(); i++) {
			if (listAdapters.get(i).isSelected()) {
				count++;
				domainID += listAdapters.get(i).getId() + ",";
			}
		}
		return count;
	}

	private void getWebsInfo() {
		if (!Constants.isNetWork) {
			ActivityUtil.showPopWindow4Tips(getActivity(), BlackAndWhiteListActivity.contentView, false, true, "当前网络不可用，请稍后再试...", 2000);
			if (listAdapters.size() == 0) {
				nodataImageView.setImageResource(R.drawable.net_felter_nodata2);
				nodataView.setVisibility(View.VISIBLE);
			} else {
				nodataView.setVisibility(View.GONE);
			}
			return;
		}
		if ("".equals(Constants.userId)) {
			LoginActivity.gotoLogin(getActivity());
			return;
		}

		String url = null;

		url = Constants.SERVER_URL + "?" + "requesttype=4" + "&userid=" + Constants.userId + "&token=" + Constants.tokenID + "&type=2";
		SharUtil.saveCacheTime(getActivity(), Constants.UNALLOWEDWED_TIME, System.currentTimeMillis() + "");
		new AllowedWebsServer(new DataServiceResponder() {

			@Override
			public void onResult(DataServiceResult result) {
				if (result == null || result.code == null) {
					return;
				}
				ActivityUtil.dismissPopWindow();
				if ("0".equals(result.code)) {
					listAdapters.clear();
					listAdapters.addAll((List<WebFilterBean>) result.result);
					if (listAdapters.size() == 0) {
						nodataView.setVisibility(View.VISIBLE);
						nodataImageView.setImageResource(R.drawable.net_felter_nodata2);
						BlackAndWhiteListActivity.comfirm.setVisibility(View.INVISIBLE);
					} else {
						nodataView.setVisibility(View.GONE);
						BlackAndWhiteListActivity.comfirm.setVisibility(View.VISIBLE);
						BlackAndWhiteListActivity.comfirm.setText("编辑");
					}
					Constants.unAllWebList.clear();
					Constants.unAllWebList.addAll(listAdapters);
					if (listAdapters.size() > 0) {
						numbsTextView.setVisibility(View.VISIBLE);
						numbsTextView.setText(listAdapters.size() + "个网址");
					} else {
						numbsTextView.setVisibility(View.INVISIBLE);
					}
					mHandler.sendEmptyMessage(REFRESH_DATA);
				} else {
					ActivityUtil.showPopWindow4Tips(getActivity(), BlackAndWhiteListActivity.contentView, false, true, result.extra + "", 2000);
				}
				mPullListView.onPullUpRefreshComplete();
				mPullListView.onPullDownRefreshComplete();
			}

			@Override
			public void onFailure() {
				mPullListView.onPullUpRefreshComplete();
				mPullListView.onPullDownRefreshComplete();
			}
		}, url, getActivity()).execute();
	}

	@Override
	public void startWebControl(int position, String status) {

		refreshWebInfo(position, status);
	}

	private void refreshWebInfo(final int position, final String status) {
		if (!Constants.isNetWork) {
			ActivityUtil.showPopWindow4Tips(getActivity(), BlackAndWhiteListActivity.contentView, false, true, "当前网络不可用，请稍后再试...", 2000);
			return;
		}
		String url = null;

		String id = listAdapters.get(position).getId();
		// http://58.20.52.242:8888?statu=1&userid=19&domainid=139&committype=5&token=token145068263619&oper=2
		url = Constants.SERVER_URL + "?" + "statu=" + status + "&userid=" + Constants.userId + "&domainid=" + id + "&committype=5&token=" + Constants.tokenID + "&oper=2";

		new PutDataServer(new DataServiceResponder() {

			@Override
			public void onResult(DataServiceResult result) {
				if ("0".equals(result.code)) {
					listAdapters.get(position).setWebStatus(status);
					Adapter.notifyDataSetChanged();
				} else {
					ActivityUtil.showPopWindow4Tips(getActivity(), BlackAndWhiteListActivity.contentView, false, true, result.extra + "", 2000);
				}
			}

			@Override
			public void onFailure() {
				mPullListView.onPullUpRefreshComplete();
			}
		}, url, getActivity()).execute();

	}

	private void removeWebInfo() {
		if (!Constants.isNetWork) {
			ActivityUtil.showPopWindow4Tips(getActivity(), BlackAndWhiteListActivity.contentView, false, true, "当前网络不可用，请稍后再试...", 2000);
			return;
		}
		String url = null;
		// http://58.20.52.242:8888?statu=1&userid=19&domainid=139&committype=5&token=token145068263619&oper=2
		url = Constants.SERVER_URL + "?" + "userid=" + Constants.userId + "&committype=5&token=" + Constants.tokenID + "&oper=3" + "&domainid=" + domainID.substring(0, domainID.length() - 1);

		new PutDataServer(new DataServiceResponder() {

			@Override
			public void onResult(DataServiceResult result) {
				if ("0".equals(result.code)) {
					mHandler.sendEmptyMessage(REFRESH);
					ActivityUtil.showPopWindow4Tips(getActivity(), BlackAndWhiteListActivity.contentView, true, true, "删除成功", 2000);
				} else {
					ActivityUtil.showPopWindow4Tips(getActivity(), BlackAndWhiteListActivity.contentView, false, true, result.extra + "", 2000);
				}
			}

			@Override
			public void onFailure() {

			}
		}, url, getActivity()).execute();

	}

	public void onAddClick() {
		if ("".equals(Constants.userId)) {
			ActivityUtil.showPopWindow4Tips(getContext(),addLinearLayout,false,true,"请登录后再操作",2000);
			return;
		}
		Intent intent = new Intent(getActivity(), AddWebsActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("TYPE", "2");
		intent.putExtras(bundle);
		startActivityForResult(intent, 100);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		getWebsInfo();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.llayout_right:
			deleteComment((Integer) view.getTag());
			break;
		case R.id.activity_allow_webs_add_ll:
			onAddClick();
			break;
		case R.id.activity_allow_webs_select_all:
			OnSelectClick();

			break;
		case R.id.activity_allow_webs_delete:
			OnDeleteClick();
			break;
		}
	}

	private void deleteComment(final int i) {
		d = ActivityUtil.getDialog(getActivity(), "是否删除", new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				domainID = listAdapters.get(i).getId() + ",";
				listAdapters.remove(i);
				removeWebInfo();
				d.dismiss();
			}
		}, new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mlistView.slideBack();
				d.dismiss();
			}
		});
		d.show();
	}
}
