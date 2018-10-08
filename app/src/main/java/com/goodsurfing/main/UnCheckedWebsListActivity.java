package com.goodsurfing.main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.goodsurfing.adpter.UncheckedWebListAdapter;
import com.goodsurfing.adpter.UncheckedWebListAdapter.SelectDelegate;
import com.goodsurfing.app.R;
import com.goodsurfing.base.BaseFragment;
import com.goodsurfing.beans.WebFilterBean;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.server.PutDataServer;
import com.goodsurfing.server.UnCheckedWebsServer;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResponder;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResult;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.SharUtil;
import com.goodsurfing.view.SlideListView;
import com.goodsurfing.view.pullrefresh.ui.PullToRefreshBase;
import com.goodsurfing.view.pullrefresh.ui.PullToRefreshBase.OnRefreshListener;
import com.goodsurfing.view.pullrefresh.ui.PullToRefreshListView;

public class UnCheckedWebsListActivity extends BaseFragment implements OnClickListener {
	protected static final int REFRESH = 100;
	protected static final int GET_WEBSINFO = 102;
	private PullToRefreshListView mPullListView;
	private SimpleDateFormat mDateFormat = new SimpleDateFormat("MM-dd HH:mm");
	private UncheckedWebListAdapter Adapter;
	public List<WebFilterBean> listAdapter = new ArrayList<WebFilterBean>();
	private SlideListView mlistView;
	private RelativeLayout nodataView;
	private ImageView nodataImageView;
	private View rootView;
	private Dialog d;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFRESH:
				if (listAdapter.size() == 0) {
					nodataView.setVisibility(View.VISIBLE);
				}
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
			rootView = LayoutInflater.from(getActivity()).inflate(R.layout.activity_unchecked_webs, null);
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
		nodataView = (RelativeLayout) view.findViewById(R.id.activity_uncheck_allow_nodata);
		mPullListView = (PullToRefreshListView) view.findViewById(R.id.activity_allow_webs_lv);
		nodataImageView = (ImageView) view.findViewById(R.id.activity_uncheck_allow_nodata_iv);
	}

	@Override
	public void onResume() {
		super.onResume();
		BlackAndWhiteListActivity.comfirm.setVisibility(View.GONE);
		if (!"".equals(Constants.userId)) {
			if (SharUtil.getCacheTimeOut(getActivity(), Constants.UNCHECKED_TIME, System.currentTimeMillis() + "")) {
				mHandler.sendEmptyMessageDelayed(GET_WEBSINFO, 500);
			} else {
				if (Constants.unCheckWebList.size() > 0) {
					listAdapter.clear();
					listAdapter.addAll(Constants.unCheckWebList);
					Adapter.notifyDataSetChanged();
				} else {
					nodataView.setVisibility(View.VISIBLE);
					nodataImageView.setImageResource(R.drawable.net_felter_nodata1);
					listAdapter.clear();
					Adapter.notifyDataSetChanged();
				}
			}
		} else {
			nodataView.setVisibility(View.VISIBLE);
			nodataImageView.setImageResource(R.drawable.net_felter_nodata1);
			listAdapter.clear();
			Adapter.notifyDataSetChanged();
		}
		BlackAndWhiteListActivity.index = 2;
	}

	private void init() {
		mPullListView.setPullLoadEnabled(false);
		mPullListView.setScrollLoadEnabled(false);
		Adapter = new UncheckedWebListAdapter(getActivity(), R.layout.unchecked_item, listAdapter, this);
		mlistView = (SlideListView) mPullListView.getRefreshableView();
		mlistView.setAdapter(Adapter);
		mlistView.setDivider(null);
		mlistView.initSlideMode(SlideListView.MOD_RIGHT);
		mlistView.setRightLength(getResources().getDimensionPixelSize(R.dimen.item_right_delete));
		mlistView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				goWebActivity(listAdapter.get(arg2).getWebSite());
			}
		});
		Adapter.setDelegate(new SelectDelegate() {

			@Override
			public void startWebControl(int position) {
				dialog(position);
			}

			@Override
			public void selectItem(int position, boolean isSelected) {
				// goWebActivity(listAdapter.get(position).getWebSite());
			}
		});
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
	}

	@Override
	public void onPause() {
		super.onPause();
		if (listAdapter.size() > 0) {
			mlistView.slideBack();
		}
	}

	private void dialog(final int position) {
		final Dialog dlg = new Dialog(getActivity(), R.style.AlertDialogCustom);
		View view = View.inflate(getActivity(), R.layout.mode_change_dialog, null);

		final TextView title = (TextView) view.findViewById(R.id.activity_change_mode_tv);

		final TextView comfirm = (TextView) view.findViewById(R.id.activity_change_mode_comfirm_text);
		TextView mCancleTextView = (TextView) view.findViewById(R.id.activity_change_mode_cancle_text);
		title.setText(listAdapter.get(position).getWebTitle() + "\n\n" + listAdapter.get(position).getReason());
		mCancleTextView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dlg.dismiss();
			}
		});
		comfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoCheckWeb(position);
				dlg.dismiss();
			}
		});
		dlg.setContentView(view);
		WindowManager m = getActivity().getWindowManager();
		Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		WindowManager.LayoutParams p = dlg.getWindow().getAttributes(); // 获取对话框当前的参数值
		p.width = (int) (d.getWidth() * 0.65); // 宽度设置为屏幕的0.95
		dlg.getWindow().setAttributes(p);
		dlg.show();

	}

	protected void gotoCheckWeb(final int position) {
		/**
		 * 13 审核名单 (增加oper=1 加入白名单，删除oper=3) userid=11111& token
		 * =fasfasfa&committype
		 * =13&oper=1&domain=www.baidu.com.cn&name=baidu&statu=1&classid=2
		 * name,classid为可选
		 */
		WebFilterBean webFilterBean = listAdapter.get(position);
		String url = null;

		url = Constants.SERVER_URL + "?" + "userid=" + Constants.userId + "&token=" + Constants.tokenID + "&committype=13&oper=1" + "&account=" + Constants.Account + "&domain=" + webFilterBean.getWebSite() + "&statu=1" + "&classid=" + webFilterBean.getId() + "&name=" + webFilterBean.getWebTitle();
		new PutDataServer(new DataServiceResponder() {

			@Override
			public void onResult(DataServiceResult result) {
				if (result.code.equals("0")) {
					listAdapter.remove(position);
					Adapter.notifyDataSetChanged();
					if (listAdapter.size() == 0) {
						nodataView.setVisibility(View.VISIBLE);
						nodataImageView.setImageResource(R.drawable.net_felter_nodata1);
						BlackAndWhiteListActivity.message.setVisibility(View.INVISIBLE);
					} else {
						nodataView.setVisibility(View.GONE);
						BlackAndWhiteListActivity.message.setVisibility(View.VISIBLE);
						if (listAdapter.size() > 99)
							BlackAndWhiteListActivity.message.setText("99+");
						else
							BlackAndWhiteListActivity.message.setText(String.valueOf(listAdapter.size()));

					}
					Constants.unCheckWebList.clear();
					Constants.unCheckWebList.addAll(listAdapter);
				} else {
					ActivityUtil.showPopWindow4Tips(getActivity(), BlackAndWhiteListActivity.contentView, false, true, result.extra + "", 2000);

				}
			}

			@Override
			public void onFailure() {

			}
		}, url, getActivity()).execute();

	}

	private void setLastUpdateTime() {
		String text = formatDateTime(System.currentTimeMillis());
		mPullListView.setLastUpdatedLabel(text);
	}

	protected void goWebActivity(String url) {
		Intent intent = new Intent(getActivity(), WebActivity.class);
		intent.putExtra("url", url);
		startActivity(intent);
	}

	private String formatDateTime(long time) {
		if (0 == time) {
			return "";
		}

		return mDateFormat.format(new Date(time));
	}

	private void getWebsInfo() {
		if (!Constants.isNetWork) {
			ActivityUtil.showPopWindow4Tips(getActivity(), BlackAndWhiteListActivity.contentView, false, true, "当前网络不可用，请稍后再试...", 2000);
			if (listAdapter.size() == 0) {
				nodataView.setVisibility(View.VISIBLE);
				nodataImageView.setImageResource(R.drawable.net_felter_nodata1);
				BlackAndWhiteListActivity.message.setVisibility(View.INVISIBLE);
			}
			return;
		}
		if ("".equals(Constants.userId)) {
			LoginActivity.gotoLogin(getActivity());
			return;
		}
		String url = null;
		url = Constants.SERVER_URL + "?" + "userid=" + Constants.userId + "&token=" + Constants.tokenID + "&requesttype=5" + "&account=" + Constants.Account;
		SharUtil.saveCacheTime(getActivity(), Constants.UNCHECKED_TIME, System.currentTimeMillis() + "");
		ActivityUtil.showPopWindow4Tips(getActivity(), BlackAndWhiteListActivity.contentView, false, true, "正在加载...", -1);
		new UnCheckedWebsServer(new DataServiceResponder() {

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
						nodataImageView.setImageResource(R.drawable.net_felter_nodata1);
						BlackAndWhiteListActivity.message.setVisibility(View.INVISIBLE);
					} else {
						nodataView.setVisibility(View.GONE);
						BlackAndWhiteListActivity.message.setVisibility(View.VISIBLE);
						if (listAdapter.size() > 99)
							BlackAndWhiteListActivity.message.setText("99+");
						else
							BlackAndWhiteListActivity.message.setText(String.valueOf(listAdapter.size()));

					}
					Constants.unCheckWebList.clear();
					Constants.unCheckWebList.addAll(listAdapter);
					Adapter.notifyDataSetChanged();
				} else {
					ActivityUtil.showPopWindow4Tips(getActivity(), BlackAndWhiteListActivity.contentView, false, true, result.extra + "", 2000);
				}
				mPullListView.onPullDownRefreshComplete();
			}

			@Override
			public void onFailure() {
				mPullListView.onPullDownRefreshComplete();

			}
		}, url, getActivity()).execute();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.llayout_right:
			final int i = (Integer) view.getTag();
			d = ActivityUtil.getDialog(getActivity(), "是否删除", new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					deleteComment(i);
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

			break;
		}
	}

	private void deleteComment(final int tag2) {
		/**
		 * 13 审核名单 (增加oper=1 加入白名单，删除oper=3) userid=11111& token
		 * =fasfasfa&committype
		 * =13&oper=1&domain=www.baidu.com.cn&name=baidu&statu=1&classid=2
		 * name,classid为可选
		 */
		WebFilterBean webFilterBean = listAdapter.get(tag2);
		String url = null;

		url = Constants.SERVER_URL + "?" + "userid=" + Constants.userId + "&token=" + Constants.tokenID + "&committype=13&oper=3" + "&account=" + Constants.Account + "&domain=" + webFilterBean.getWebSite() + "&statu=1" + "&classid=" + webFilterBean.getId();
		new PutDataServer(new DataServiceResponder() {

			@Override
			public void onResult(DataServiceResult result) {
				if (result.code.equals("0")) {
					listAdapter.remove(tag2);
					Adapter.notifyDataSetChanged();
					ActivityUtil.showPopWindow4Tips(getActivity(), BlackAndWhiteListActivity.contentView, true, "删除成功");
					if (listAdapter.size() == 0) {
						nodataView.setVisibility(View.VISIBLE);
						nodataImageView.setImageResource(R.drawable.net_felter_nodata1);
						BlackAndWhiteListActivity.message.setVisibility(View.INVISIBLE);

					} else {
						mlistView.slideBack();
						nodataView.setVisibility(View.GONE);
						BlackAndWhiteListActivity.message.setVisibility(View.VISIBLE);
						if (listAdapter.size() > 99)
							BlackAndWhiteListActivity.message.setText("99+");
						else
							BlackAndWhiteListActivity.message.setText(String.valueOf(listAdapter.size()));

					}
					Constants.unCheckWebList.clear();
					Constants.unCheckWebList.addAll(listAdapter);
				} else {
					ActivityUtil.showPopWindow4Tips(getActivity(), BlackAndWhiteListActivity.contentView, false, true, result.extra + "", 2000);

				}
			}

			@Override
			public void onFailure() {

			}
		}, url, getActivity()).execute();
	}

}
