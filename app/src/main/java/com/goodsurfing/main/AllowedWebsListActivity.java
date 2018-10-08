package com.goodsurfing.main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.a.a.f;
import com.goodsurfing.adpter.BlackWhiteWebListAdapter;
import com.goodsurfing.adpter.BlackWhiteWebListAdapter.SelectDelegate;
import com.goodsurfing.app.R;
import com.goodsurfing.base.BaseFragment;
import com.goodsurfing.beans.WebFilterBean;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.main.BlackAndWhiteListActivity.DELEGATE;
import com.goodsurfing.server.AllowedWebsServer;
import com.goodsurfing.server.PutDataServer;
import com.goodsurfing.server.UnCheckedWebsServer;
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

@SuppressLint("HandlerLeak")
public class AllowedWebsListActivity extends BaseFragment implements SelectDelegate, DELEGATE, OnClickListener {
	protected static final int REFRESH = 100;
	protected static final int REFRESH_DATA = 101;
	protected static final int GET_WEBSINFO = 102;

	private SimpleDateFormat mDateFormat = new SimpleDateFormat("MM-dd HH:mm");

	/** listView 相关 **/
	private BlackWhiteWebListAdapter Adapter;
	public static List<WebFilterBean> listAdapter = new ArrayList<WebFilterBean>();

	private SlideListView mlistView;
	private PullToRefreshListView mPullListView;
	private LinearLayout deleteLinearLayout;
	private LinearLayout addLinearLayout;
	private TextView numbsTextView;
	private TextView selectAllTv;
	private RelativeLayout nodataView;
	private TextView deletView;
	// public View contentView;
	private static String domainID = "";
	private static String appID = "";
	private Dialog d;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFRESH:
				if (Adapter.getSelectAllFlag()) {
					deleteAll();
					if (listAdapter.size() == 0) {
						nodataView.setVisibility(View.VISIBLE);
						BlackAndWhiteListActivity.comfirm.setVisibility(View.INVISIBLE);
					}
				} else {
					deleteSelected();
					if (listAdapter.size() == 0) {
						nodataView.setVisibility(View.VISIBLE);
						BlackAndWhiteListActivity.comfirm.setVisibility(View.INVISIBLE);
					} else {
						BlackAndWhiteListActivity.comfirm.setVisibility(View.VISIBLE);
						BlackAndWhiteListActivity.comfirm.setText("编辑");
						mlistView.slideBack();
					}
				}
				Constants.isEditing = false;
				domainID = "";
				appID = "";
				if (listAdapter.size() > 0) {
					numbsTextView.setVisibility(View.VISIBLE);
					numbsTextView.setText(listAdapter.size() + "个网址和应用");
				} else {
					numbsTextView.setVisibility(View.INVISIBLE);
				}
				deleteLinearLayout.setVisibility(View.GONE);
				addLinearLayout.setVisibility(View.VISIBLE);
				Constants.allWebList.clear();
				Constants.allWebList.addAll(listAdapter);
				Adapter.notifyDataSetChanged();
				clearView(false);
				break;
			case REFRESH_DATA:
				if (listAdapter.size() > 0) {
					numbsTextView.setVisibility(View.VISIBLE);
					numbsTextView.setText(listAdapter.size() + "个网址和应用");
				} else {
					numbsTextView.setVisibility(View.INVISIBLE);
				}
				Adapter.notifyDataSetChanged();
				break;
			case GET_WEBSINFO:
				getWebsInfo();
				break;
			}
		};
	};
	private View rootView;

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
		deletView = (TextView) view.findViewById(R.id.activity_allow_webs_delete);
		selectAllTv.setOnClickListener(this);
		addLinearLayout.setOnClickListener(this);
		deletView.setOnClickListener(this);

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
			if (SharUtil.getCacheTimeOut(getActivity(), Constants.ALLOWEDWEB_TIME, System.currentTimeMillis() + "")) {
				mHandler.sendEmptyMessageDelayed(GET_WEBSINFO, 500);
				if (Constants.unCheckWebList.size() == 0)
					getUncheckWebsInfo();
				else {
					BlackAndWhiteListActivity.message.setVisibility(View.VISIBLE);
					if (Constants.unCheckWebList.size() > 99)
						BlackAndWhiteListActivity.message.setText("99+");
					else
						BlackAndWhiteListActivity.message.setText(String.valueOf(Constants.unCheckWebList.size()));
				}
			} else {
				if (Constants.allWebList.size() > 0) {
					listAdapter.clear();
					listAdapter.addAll(Constants.allWebList);
					Adapter.notifyDataSetChanged();
					BlackAndWhiteListActivity.comfirm.setVisibility(View.VISIBLE);
				} else {
					nodataView.setVisibility(View.VISIBLE);
					BlackAndWhiteListActivity.comfirm.setVisibility(View.INVISIBLE);
					listAdapter.clear();
					Adapter.notifyDataSetChanged();
				}
			}
		} else {
			nodataView.setVisibility(View.VISIBLE);
			BlackAndWhiteListActivity.comfirm.setVisibility(View.INVISIBLE);
			listAdapter.clear();
			Adapter.notifyDataSetChanged();
		}
		if (listAdapter.size() > 0) {
			numbsTextView.setVisibility(View.VISIBLE);
			numbsTextView.setText(listAdapter.size() + "个网址和应用");
		} else {
			numbsTextView.setVisibility(View.INVISIBLE);
		}
		BlackAndWhiteListActivity.index = 0;
		ActivityUtil.sendEvent4UM(getActivity(), "functionSwitch", "whitelist", 26);
	}

	@Override
	public void onPause() {
		super.onPause();
		Adapter.setDelegate(null);
		BlackAndWhiteListActivity.setDelegate(null);
		Constants.isEditing = false;
		clearView(false);
		Adapter.setSelectALL(false);
		if (listAdapter.size() > 0) {
			mlistView.slideBack();
		}
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	private void init() {
		mPullListView.setPullLoadEnabled(false);
		mPullListView.setScrollLoadEnabled(false);
		Adapter = new BlackWhiteWebListAdapter(getActivity(), R.layout.item_web_filter_cell, listAdapter, this, true);
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
				} else {
					LoginActivity.gotoLogin(getActivity());
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
			if (num == listAdapter.size()) {
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
		for (int i = 0; i < listAdapter.size(); i++) {
			listAdapter.get(i).setSelected(select);
		}
		selectItem(-1, true);
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
		listAdapter.clear();
	}

	private void deleteSelected() {
		for (int i = 0; i < listAdapter.size(); i++) {
			if (listAdapter.get(i).isSelected()) {
				listAdapter.remove(i);
			}
		}
	}

	private int getSelectedNumbers() {
		int count = 0;
		appID = "";
		domainID = "";
		for (int i = 0; i < listAdapter.size(); i++) {
			WebFilterBean bean = listAdapter.get(i);
			if (bean.isSelected()) {
				count++;
				if (bean.getWebClassType().equals("3")) {
					appID += listAdapter.get(i).getId() + ",";
				} else {
					domainID += listAdapter.get(i).getId() + ",";
				}
			}
		}
		return count;
	}

	private void getWebsInfo() {
		if (!Constants.isNetWork) {
			ActivityUtil.showPopWindow4Tips(getActivity(), BlackAndWhiteListActivity.contentView, false, true, "当前网络不可用，请稍后再试...", 2000);
			if (listAdapter.size() == 0) {
				nodataView.setVisibility(View.VISIBLE);
				BlackAndWhiteListActivity.comfirm.setVisibility(View.INVISIBLE);
			} else {
				nodataView.setVisibility(View.GONE);
			}
			return;
		}
		String url = null;

		url = Constants.SERVER_URL + "?" + "requesttype=4" + "&userid=" + Constants.userId + "&token=" + Constants.tokenID + "&type=1";
		new AllowedWebsServer(new DataServiceResponder() {

			@Override
			public void onResult(DataServiceResult result) {
				if (result == null || result.code == null) {
					return;
				}
				ActivityUtil.dismissPopWindow();
				if ("0".equals(result.code)) {
					listAdapter.clear();
					listAdapter.addAll((List<WebFilterBean>) result.result);
					if (listAdapter.size() == 0) {
						nodataView.setVisibility(View.VISIBLE);
						BlackAndWhiteListActivity.comfirm.setVisibility(View.INVISIBLE);
					} else {
						BlackAndWhiteListActivity.comfirm.setVisibility(View.VISIBLE);
						BlackAndWhiteListActivity.comfirm.setText("编辑");
						nodataView.setVisibility(View.GONE);
					}
					Constants.allWebList.clear();
					Constants.allWebList.addAll(listAdapter);
					SharUtil.saveCacheTime(getActivity(), Constants.ALLOWEDWEB_TIME, System.currentTimeMillis() + "");
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

		String id = listAdapter.get(position).getId();
		// http://58.20.52.242:8888?statu=1&userid=19&domainid=139&committype=5&token=token145068263619&oper=2
		url = Constants.SERVER_URL + "?" + "statu=" + status + "&userid=" + Constants.userId + "&domainid=" + id + "&committype=5&token=" + Constants.tokenID + "&oper=2";

		new PutDataServer(new DataServiceResponder() {

			@Override
			public void onResult(DataServiceResult result) {
				if ("0".equals(result.code)) {
					listAdapter.get(position).setWebStatus(status);
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
		url = Constants.SERVER_URL + "?" + "userid=" + Constants.userId + "&committype=5&token=" + Constants.tokenID + "&oper=3" + "&domainid=" + (domainID.equals("") ? "" : domainID.substring(0, domainID.length() - 1)) + "&appid=" + (appID.equals("") ? "" : appID.substring(0, appID.length() - 1));
		new PutDataServer(new DataServiceResponder() {

			@Override
			public void onResult(DataServiceResult result) {
				if (null != result && "0".equals(result.code)) {
					ActivityUtil.showPopWindow4Tips(getActivity(), BlackAndWhiteListActivity.contentView, true, true, "删除成功", 2000);
					mHandler.sendEmptyMessage(REFRESH);
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
			LoginActivity.gotoLogin(getActivity());
			return;
		}
		Intent intent = new Intent(getActivity(), AddBlackWhiteListActivity.class);
		startActivityForResult(intent, 110);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK)
			getWebsInfo();
	}

	private void getUncheckWebsInfo() {
		if (!Constants.isNetWork) {
			return;
		}
		String url = null;
		url = Constants.SERVER_URL + "?" + "userid=" + Constants.userId + "&token=" + Constants.tokenID + "&requesttype=5" + "&account=" + Constants.Account;
		SharUtil.saveCacheTime(getActivity(), Constants.UNCHECKED_TIME, System.currentTimeMillis() + "");
		new UnCheckedWebsServer(new DataServiceResponder() {

			@Override
			public void onResult(DataServiceResult result) {
				if (result == null || result.code == null) {
					return;
				}
				if ("0".equals(result.code)) {
					Constants.unCheckWebList.clear();
					Constants.unCheckWebList.addAll((List<WebFilterBean>) result.result);
					if (Constants.unCheckWebList.size() == 0) {
						BlackAndWhiteListActivity.message.setVisibility(View.INVISIBLE);
					} else {
						BlackAndWhiteListActivity.message.setVisibility(View.VISIBLE);
						if (Constants.unCheckWebList.size() > 99)
							BlackAndWhiteListActivity.message.setText("99+");
						else
							BlackAndWhiteListActivity.message.setText(String.valueOf(Constants.unCheckWebList.size()));

					}
				} else {
					ActivityUtil.showPopWindow4Tips(getActivity(), BlackAndWhiteListActivity.contentView, false, true, result.extra + "", 2000);
				}
			}

			@Override
			public void onFailure() {

			}
		}, url, getActivity()).execute();
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

	private void deleteComment(final Integer i) {
		d = ActivityUtil.getDialog(getActivity(), "是否删除", new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (listAdapter.get(i).getWebClassType().equals("3")) {
					appID = listAdapter.get(i).getId() + ",";
				} else {
					domainID = listAdapter.get(i).getId() + ",";
				}
				listAdapter.get(i).setSelected(true);
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
