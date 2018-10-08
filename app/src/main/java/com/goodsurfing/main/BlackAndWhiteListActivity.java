package com.goodsurfing.main;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.goodsurfing.app.R;
import com.goodsurfing.beans.WebFilterBean;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.database.dao.AppDao;
import com.goodsurfing.server.AllowedWebsServer;
import com.goodsurfing.server.GetAppListServer;
import com.goodsurfing.server.GetWebTypeServer;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResponder;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResult;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.CommonUtil;
import com.goodsurfing.view.SwipeBackLayout;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class BlackAndWhiteListActivity extends FragmentActivity {

	@ViewInject(R.id.tv_title)
	private TextView title;

	@ViewInject(R.id.tv_title_right)
	public static TextView comfirm;

	public static TextView message;

	public static Context mcontext;

	protected SwipeBackLayout layout;

	// 定义FragmentTabHost对象
	public static FragmentTabHost mTabHost;

	// 定义一个布局
	private LayoutInflater layoutInflater;

	// 定义数组来存放Fragment界面
	private Class fragmentArray[] = { AllowedWebsListActivity.class, UnallowedWebsListActivity.class, UnCheckedWebsListActivity.class };
	// Tab选项卡的文字
	private String mTextviewArray[] = { "白名单", "黑名单", "待审核网址" };
	private View[] lins = new View[3];
	@ViewInject(R.id.pupwindow_blowe)
	public static View contentView;
	public static int index = 0;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		contentView = LayoutInflater.from(this).inflate(R.layout.black_white_activity, null);
		setContentView(contentView);
		layout = (SwipeBackLayout) LayoutInflater.from(this).inflate(R.layout.base, null);
		layout.attachToActivity(this);
		ViewUtils.inject(this);
		init();
	}

	private void init() {
		title.setText("黑白名单");
		comfirm.setText("编辑");
		mcontext = this;
		initTableHost();
	}

	@Override
	protected void onResume() {
		super.onResume();
		CommonUtil.HandLockPassword(this);
		if (!"".equals(Constants.userId)) {
			getWebsTypeInfo();
			if (Constants.appDao.getWebFilterBeanList().size() == 0) {
				getAppsInfo();
			}
		}
		lins[index].setVisibility(View.VISIBLE);
	}

	/**
	 * 获取所有APP的列表
	 */
	private void getAppsInfo() {
		String url = null;
		url = Constants.SERVER_URL + "?" + "requesttype=15" + "&userid=" + Constants.userId + "&token=" + Constants.tokenID + "&type=1";
		ActivityUtil.showPopWindow4Tips(this, AddBlackWhiteListActivity.contentView, false, true, "正在加载...", -1);

		new GetAppListServer(new DataServiceResponder() {

			@Override
			public void onResult(DataServiceResult result) {
				if (result == null || result.code == null) {
					return;
				}
				ActivityUtil.dismissPopWindow();
				if ("0".equals(result.code)) {
					Constants.appDao.insert((List<WebFilterBean>) result.result);
				}
			}

			@Override
			public void onFailure() {
			}
		}, url, this).execute();

	}

	@Override
	protected void onStop() {
		super.onStop();
		if (!CommonUtil.isForeground(this)) {
			Constants.isAPPActive = false;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		BlackAndWhiteListActivity.index = 0;
	}

	private void initTableHost() {
		// 实例化布局对象
		layoutInflater = LayoutInflater.from(this);

		// 实例化TabHost对象，得到TabHost
		mTabHost = (FragmentTabHost) findViewById(R.id.web_tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		// 得到fragment的个数
		int count = fragmentArray.length;
		for (int i = 0; i < count; i++) {
			// 为每一个Tab按钮设置图标、文字和内容
			TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
			// 将Tab按钮添加进Tab选项卡中
			mTabHost.addTab(tabSpec, fragmentArray[i], null);
			// 设置Tab按钮的背景
			final int index = i;
			mTabHost.getTabWidget().getChildAt(index).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (!Constants.isEditing) {
						switch (index) {
						case 0:
							lins[1].setVisibility(View.INVISIBLE);
							lins[2].setVisibility(View.INVISIBLE);
							lins[0].setVisibility(View.VISIBLE);
							mTabHost.setCurrentTab(0);
							break;
						case 1:
							lins[0].setVisibility(View.INVISIBLE);
							lins[2].setVisibility(View.INVISIBLE);
							lins[1].setVisibility(View.VISIBLE);
							mTabHost.setCurrentTab(1);
							break;
						case 2:
							lins[0].setVisibility(View.INVISIBLE);
							lins[1].setVisibility(View.INVISIBLE);
							lins[2].setVisibility(View.VISIBLE);
							mTabHost.setCurrentTab(2);
							break;
						}
					}
				}
			});

		}
	}

	private View getTabItemView(int index) {
		View view = layoutInflater.inflate(R.layout.black_white_item_layout, null);
		TextView textView = (TextView) view.findViewById(R.id.black_white_tv);
		textView.setText(mTextviewArray[index]);
		lins[index] = view.findViewById(R.id.black_white_v);
		if (index == 2) {
			message = (TextView) view.findViewById(R.id.black_white_hint_tv);
			View hView = view.findViewById(R.id.black_white_vh);
			hView.setVisibility(View.INVISIBLE);
		}
		return view;
	}

	@OnClick(R.id.tv_title_right)
	public void onComfirmClick(View v) {
		if ("".equals(Constants.userId)) {
			LoginActivity.gotoLogin(this);
			return;
		}
		if (Constants.isEditing) {
			Constants.isEditing = false;
			comfirm.setText("编辑");
		} else {
			Constants.isEditing = true;
			comfirm.setText("取消");
		}
		mDelegate.editOnClick();

	}

	private void getWebsTypeInfo() {
		if (Constants.typeMap.size() > 0)
			return;
		String url = null;
		url = Constants.SERVER_URL + "?" + "requesttype=3" + "&userid=" + Constants.userId + "&token=" + Constants.tokenID;
		new GetWebTypeServer(new DataServiceResponder() {

			@Override
			public void onResult(DataServiceResult result) {

			}

			@Override
			public void onFailure() {

			}
		}, url, this).execute();

	}

	public static interface DELEGATE {
		void editOnClick();
	}

	private static DELEGATE mDelegate;

	public static void setDelegate(DELEGATE delegate) {
		mDelegate = delegate;
	}

	@OnClick(R.id.iv_title_left)
	public void onHeadBackClick(View view) {
		onBackPressed();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, R.anim.base_slide_right_out);
	}
}
