package com.goodsurfing.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.goodsurfing.app.R;
import com.goodsurfing.constants.Constants;
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
import com.umeng.analytics.MobclickAgent;

public class AddBlackWhiteListActivity extends FragmentActivity {

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
	private Class fragmentArray[] = { AddWhiteAppActivity.class, AddWhiteWebsActivity.class };
	// Tab选项卡的文字
	private String mTextviewArray[] = { "添加应用", "添加网址" };
	private View[] lins = new View[2];
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
		title.setText("添加白名单");
		comfirm.setText("确定");
		mcontext = this;
		initTableHost();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		CommonUtil.HandLockPassword(this);
		lins[index].setVisibility(View.VISIBLE);
		ActivityUtil.sendEvent4UM(this, "functionSwitch", "addwhitelist", 28);
	}
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
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
		AddBlackWhiteListActivity.index = 0;
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
					switch (index) {
					case 0:
						lins[1].setVisibility(View.INVISIBLE);
						lins[0].setVisibility(View.VISIBLE);
						mTabHost.setCurrentTab(0);
						break;
					case 1:
						lins[0].setVisibility(View.INVISIBLE);
						lins[1].setVisibility(View.VISIBLE);
						mTabHost.setCurrentTab(1);
						break;
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
		return view;
	}

	@OnClick(R.id.tv_title_right)
	public void onComfirmClick(View v) {
		if ("".equals(Constants.userId)) {
			LoginActivity.gotoLogin(this);
			return;
		}
		// TODO 上传添加内容
		if (index == 0) {
			sendBroadcast(new Intent("AddWhiteAppActivity"));
		} else {
			sendBroadcast(new Intent("AddWhiteWebsActivity"));
		}
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
	
	public  void onBackPressed4Add() {
		setResult(RESULT_OK);
		onBackPressed();
	}
	
}
