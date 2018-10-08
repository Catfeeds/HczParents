package com.goodsurfing.main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.goodsurfing.adpter.TimeCardsListAdapter;
import com.goodsurfing.adpter.TimeCardsListAdapter.SelectDelegate;
import com.goodsurfing.app.R;
import com.goodsurfing.base.BaseActivity;
import com.goodsurfing.beans.TimeCardBean;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.server.GetWebsInfoServer;
import com.goodsurfing.server.PutDataServer;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResponder;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResult;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.CommonUtil;
import com.goodsurfing.utils.EventHandler;
import com.goodsurfing.utils.SharUtil;
import com.goodsurfing.view.SlideListView;
import com.goodsurfing.view.pullrefresh.ui.PullToRefreshBase;
import com.goodsurfing.view.pullrefresh.ui.PullToRefreshBase.OnRefreshListener;
import com.goodsurfing.view.pullrefresh.ui.PullToRefreshListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
/**
 * 上网奖励卡
 * @author 谢志杰 
 * @create 2017-12-18 下午2:41:58 
 * @version 1.0.0
 * @company 湖南一石科技有限公司 Copyright: 版权所有（c）2016
 */
public class TimerCountsListActivity extends BaseActivity implements SelectDelegate, OnClickListener {

	private final static String TAG = "TimerCountsListActivity";
	protected static final int REFRESH = 100;
	protected static final int REFRESH_DATA = 101;
	protected static final int GET_WEBSINFO = 102;
	@ViewInject(R.id.activity_allow_webs_lv)
	private PullToRefreshListView mPullListView;
	private SimpleDateFormat mDateFormat = new SimpleDateFormat("MM-dd HH:mm");
	/** listView 相关 **/
	private TimeCardsListAdapter Adapter;
	public static List<TimeCardBean> listAdapter = new ArrayList<TimeCardBean>();

	private SlideListView mlistView;

	@ViewInject(R.id.tv_title)
	private TextView title;

	@ViewInject(R.id.tv_title_right)
	private TextView edit;

	@ViewInject(R.id.activity_allow_webs_select_ll)
	private LinearLayout deleteLinearLayout;

	@ViewInject(R.id.activity_allow_webs_add_ll)
	private LinearLayout addLinearLayout;

	@ViewInject(R.id.title_layout)
	private View title_layout;

	@ViewInject(R.id.activity_allow_webs_select_all)
	private TextView selectAllTv;

	@ViewInject(R.id.activity_allow_webs_delete)
	private TextView deletView;

	@ViewInject(R.id.activity_timer_nodata_iv)
	private ImageView noDataIv;

	private static String domainID = null;
	private Dialog d;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFRESH:
				if (Adapter.getSelectAllFlag()) {
					deleteAll();
					edit.setVisibility(View.INVISIBLE);
				} else {
					deleteSelected();
					if (listAdapter.size() == 0) {
						edit.setVisibility(View.INVISIBLE);
						noDataIv.setVisibility(View.VISIBLE);
					} else {
						noDataIv.setVisibility(View.GONE);
						edit.setText("编辑");
						mlistView.slideBack();
					}
				}
				Constants.isEditing = false;
				domainID = null;
				deleteLinearLayout.setVisibility(View.GONE);
				addLinearLayout.setVisibility(View.VISIBLE);
				Adapter.notifyDataSetChanged();
				Constants.timerList.clear();
				Constants.timerList.addAll(listAdapter);
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timer_cards);
		ViewUtils.inject(this);
		init();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Adapter.setDelegate(this);
		edit.setVisibility(View.INVISIBLE);
		if (!"".equals(Constants.userId)) {
			if (SharUtil.getCacheTimeOut(this, Constants.CAR_TIME, System.currentTimeMillis() + "") || Constants.timerList.size() == 0) {
				mHandler.sendEmptyMessageDelayed(GET_WEBSINFO, 500);
			} else {
				if (Constants.timerList.size() > 0) {
					listAdapter.clear();
					listAdapter.addAll(Constants.timerList);
					Adapter.notifyDataSetChanged();
					edit.setVisibility(View.VISIBLE);
				}
			}
		} else {
			noDataIv.setVisibility(View.VISIBLE);
			edit.setVisibility(View.INVISIBLE);
			listAdapter.clear();
			Adapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		Adapter.setDelegate(null);
		Constants.isEditing = false;
		clearView(false);
		Adapter.setSelectALL(false);
		if (listAdapter.size() > 0) {
			mlistView.slideBack();
		}
	}

	private void init() {
		title.setText("上网奖励卡");
		edit.setVisibility(View.VISIBLE);
		edit.setText("编辑");
		clearView(false);
		mPullListView.setPullLoadEnabled(false);
		mPullListView.setScrollLoadEnabled(false);
		Adapter = new TimeCardsListAdapter(this, R.layout.item_timer_card_cell, listAdapter);
		mlistView = (SlideListView) mPullListView.getRefreshableView();
		mlistView.setAdapter(Adapter);
		mlistView.setDivider(null);
		mlistView.initSlideMode(SlideListView.MOD_RIGHT);
		mlistView.setRightLength(getResources().getDimensionPixelSize(R.dimen.item_right_delete));
		mlistView.setSelector(getResources().getDrawable(R.color.transparent));
		mPullListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				setLastUpdateTime();
				if (!"".equals(Constants.userId)) {
					getWebsInfo();
				}
				mPullListView.onPullUpRefreshComplete();
				mPullListView.onPullDownRefreshComplete();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				mPullListView.onPullUpRefreshComplete();
			}

		});
		setLastUpdateTime();
		mPullListView.setHasMoreData(false);

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

	/**
	 * 编辑按钮事件
	 */
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

	/**
	 * 列表点击事件
	 */
	@Override
	public void selectItem(int position, boolean isSelected) {
		int num = getSelectedNumbers();
		if (num > 0) {
			deletView.setText("删除(" + num + ")");
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
	}

	@OnClick(R.id.activity_allow_webs_select_all)
	public void OnSelectClick(View v) {
		if (Adapter.getSelectAllFlag()) {
			clearView(false);
		} else {
			clearView(true);
		}
		selectItem(-1, false);
	}

	@OnClick(R.id.activity_allow_webs_delete)
	public void OnDeleteClick(View v) {
		if ("".equals(Constants.userId)) {
			LoginActivity.gotoLogin(this);
			return;
		}
		int num = getSelectedNumbers();
		if (num > 0) {
			d = ActivityUtil.getDialog(this, "是否删除", new OnClickListener() {

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
		int num = getSelectedNumbers();
		while (num != 0) {
			for (int i = 0; i < listAdapter.size(); i++) {
				if (listAdapter.get(i).isSelected()) {
					listAdapter.remove(i);
					num--;
					break;
				}
			}
		}
	}

	private int getSelectedNumbers() {
		int count = 0;
		for (int i = 0; i < listAdapter.size(); i++) {
			if (listAdapter.get(i).isSelected()) {
				count++;
				if (domainID == null)
					domainID = listAdapter.get(i).getId() + ",";
				else
					domainID = domainID + listAdapter.get(i).getId() + ",";
			}
		}
		return count;
	}

	private void getWebsInfo() {
		if (!Constants.isNetWork) {
			ActivityUtil.showPopWindow4Tips(this, title_layout, false, "当前网络不可用，请稍后再试...");
			if (listAdapter.size() == 0) {
				edit.setVisibility(View.INVISIBLE);
				noDataIv.setVisibility(View.VISIBLE);
			}
			return;
		}
		String url = null;
		url = Constants.SERVER_URL + "?" + "requesttype=6" + "&userid=" + Constants.userId + "&token=" + Constants.tokenID;
		ActivityUtil.showPopWindow4Tips(TimerCountsListActivity.this, title_layout, false, false, "正在加载....", -1);
	
		new GetWebsInfoServer(new DataServiceResponder() {

			@Override
			public void onResult(DataServiceResult result) {
				ActivityUtil.dismissPopWindow();
				if ("0".equals(result.code)) {
					listAdapter.clear();
					listAdapter.addAll((List<TimeCardBean>) result.result);
					if (listAdapter.size() == 0) {
						edit.setVisibility(View.INVISIBLE);
						noDataIv.setVisibility(View.VISIBLE);
					} else {
						noDataIv.setVisibility(View.GONE);
						edit.setVisibility(View.VISIBLE);
						edit.setText("编辑");
					}
					Constants.timerList.clear();
					Constants.timerList.addAll(listAdapter);
					SharUtil.saveCacheTime(TimerCountsListActivity.this, Constants.CAR_TIME, System.currentTimeMillis() + "");
					mHandler.sendEmptyMessage(REFRESH_DATA);
				}
				mPullListView.onPullUpRefreshComplete();
				mPullListView.onPullDownRefreshComplete();
			}

			@Override
			public void onFailure() {
				mPullListView.onPullUpRefreshComplete();
				mPullListView.onPullDownRefreshComplete();
			}
		}, url, this).execute();
	}

	@Override
	public void startWebControl(int position, String status) {
	}

	private void removeWebInfo() {
		if (!Constants.isNetWork) {
			ActivityUtil.showPopWindow4Tips(this, title_layout, false, "当前网络不可用，请稍后再试...");
			return;
		}
		String url = null;
		url = Constants.SERVER_URL + "?" + "userid=" + Constants.userId + "&committype=6&token=" + Constants.tokenID + "&oper=3" + "&id=" + domainID.substring(0, domainID.length() - 1);

		new PutDataServer(new DataServiceResponder() {

			@Override
			public void onResult(DataServiceResult result) {
				if ("0".equals(result.code)) {
					mHandler.sendEmptyMessage(REFRESH);
					ActivityUtil.showPopWindow4Tips(TimerCountsListActivity.this, title_layout, true, "删除成功");
				} else {
					ActivityUtil.showPopWindow4Tips(TimerCountsListActivity.this, title_layout, false, result.extra + "");
				}
			}

			@Override
			public void onFailure() {

			}
		}, url, this).execute();
	}

	@OnClick(R.id.tv_title_right)
	public void onComfirmClick(View v) {
		if (CommonUtil.isFastDoubleClick())
			return;
		if ("".equals(Constants.userId)) {
			LoginActivity.gotoLogin(this);
			return;
		}
		if (Constants.isEditing) {
			Constants.isEditing = false;
			edit.setText("编辑");
		} else {
			Constants.isEditing = true;
			edit.setText("取消");
		}
		editOnClick();
	}

	@OnClick(R.id.activity_allow_webs_add_tv)
	public void onAddClick(View v) {
		if ("".equals(Constants.userId)) {
			LoginActivity.gotoLogin(this);
			return;
		}
		if (listAdapter.size() == 3) {
			ActivityUtil.showPopWindow4Tips(this, title_layout, false, "当前最多只能添加三张奖励卡");
			return;
		}
		Intent intent = new Intent(TimerCountsListActivity.this, AddTimerCardsActivity.class);
		String name;
		if (listAdapter.size() > 0) {
			try {
				name = String.valueOf(Integer.parseInt(listAdapter.get(listAdapter.size() - 1).getName()) + 1);
			} catch (Exception e) {
				name = "1001";
			}
		} else {
			name = "1001";
		}
		intent.putExtra("name", name);
		startActivityForResult(intent, 101);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		getWebsInfo();
	}

	@OnClick(R.id.iv_title_left)
	public void onHeadBackClick(View view) {
		onBackPressed();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.llayout_right:
			deleteComment((Integer) view.getTag());
			break;
		}
	}

	private void deleteComment(final int i) {
		d = ActivityUtil.getDialog(this, "是否删除", new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				domainID = listAdapter.get(i).getId() + ",";
				listAdapter.remove(i);
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
