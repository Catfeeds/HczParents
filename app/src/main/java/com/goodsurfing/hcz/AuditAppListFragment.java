package com.goodsurfing.hcz;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.android.component.constants.What;
import com.goodsurfing.adpter.AppListAdapter;
import com.goodsurfing.app.R;
import com.goodsurfing.base.BaseFragment;
import com.goodsurfing.beans.AppBean;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.hcz.HczLoginActivity;
import com.goodsurfing.main.AddBlackWhiteListActivity;
import com.goodsurfing.server.net.HczGetChildAppsNet;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.view.SlideListView;
import com.goodsurfing.view.pullrefresh.ui.PullToRefreshBase;
import com.goodsurfing.view.pullrefresh.ui.PullToRefreshBase.OnRefreshListener;
import com.goodsurfing.view.pullrefresh.ui.PullToRefreshListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AuditAppListFragment extends BaseFragment  {
	private final static String TAG = "AddWhiteAppActivity";
	protected static final int REFRESH = 100;
	protected static final int REFRESH_DATA = 101;
	protected static final int GET_WEBSINFO = 102;

	private SimpleDateFormat mDateFormat = new SimpleDateFormat("MM-dd HH:mm");

	/** listView 相关 **/
	private AppListAdapter Adapter;
	public static List<AppBean> listAdapter = new ArrayList<AppBean>();
	private SlideListView mlistView;
	private PullToRefreshListView mPullListView;
	private RelativeLayout nodataView;
	//	private EditText searchView;
	private String domainID = "";
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case REFRESH:
					if (listAdapter.size() == 0) {
						nodataView.setVisibility(View.VISIBLE);
					} else {
						mlistView.slideBack();
					}
					Adapter.notifyDataSetChanged();
					break;
				case REFRESH_DATA:
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
	}

	@Override
	public void onResume() {
		super.onResume();
		getAppsInfo();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	private void init() {
		mPullListView.setPullLoadEnabled(false);
		mPullListView.setScrollLoadEnabled(false);
		Adapter = new AppListAdapter(getActivity(), R.layout.item_app_controls, listAdapter);
		Adapter.setHandler(handler);
		mlistView = (SlideListView) mPullListView.getRefreshableView();
		mlistView.setAdapter(Adapter);
		mlistView.setDivider(null);
		mlistView.initSlideMode(SlideListView.MOD_FORBID);
		mlistView.setRightLength(getResources().getDimensionPixelSize(R.dimen.item_right_delete));
		setLastUpdateTime();
		mPullListView.setHasMoreData(false);
		mPullListView.setPullRefreshEnabled(true);
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
		if (Constants.userId.equals("")) {
			HczLoginActivity.gotoLogin(getActivity());
			return;
		}
		if (Constants.child==null) {
			ActivityUtil.showPopWindow4Tips(getActivity(), rootView, false, true, "请绑定孩子手机后操作...", 2000);
			return;
		}
		ActivityUtil.showPopWindow4Tips(getActivity(), rootView, false, true, "正在刷新...", -1);
		HczGetChildAppsNet getChildAppsNet = new HczGetChildAppsNet(getActivity(),new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case What.HTTP_REQUEST_CURD_SUCCESS:
						ActivityUtil.showPopWindow4Tips(getActivity(),rootView, true, true, "刷新成功", 2000);
						listAdapter.clear();
						listAdapter.addAll(((List<AppBean>) msg.obj));
						Adapter.notifyDataSetChanged();
						break;
					case What.HTTP_REQUEST_CURD_FAILURE:
						ActivityUtil.showPopWindow4Tips(getActivity(),rootView, false, true, msg.obj.toString(), 2000);
						break;
				}
			}
		});
		getChildAppsNet.putParams("1");
		getChildAppsNet.sendRequest();

	}
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case What.HTTP_REQUEST_CURD_SUCCESS:
					break;
				case What.HTTP_REQUEST_CURD_FAILURE:
					ActivityUtil.showPopWindow4Tips(getActivity(),rootView, false, true, "设置失败", 2000);
					break;
			}
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


}
