package com.goodsurfing.main;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.goodsurfing.app.R;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.server.GetWebTypeServer;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResponder;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResult;
import com.goodsurfing.utils.CommonUtil;
import com.goodsurfing.view.SwipeBackLayout;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class WebFiltersActivity extends TabActivity {

	@ViewInject(R.id.tabwidget1)
	private RelativeLayout tabeWidge1;

	@ViewInject(R.id.tabwidget2)
	private RelativeLayout tabeWidge2;

	@ViewInject(R.id.tabwidget3)
	private RelativeLayout tabeWidge3;

	@ViewInject(R.id.tabwidget_tv1)
	private TextView tabWidgetTV1;

	@ViewInject(R.id.tabwidget_tv2)
	private TextView tabWidgetTV2;

	@ViewInject(R.id.tabwidget_tv3)
	private TextView tabWidgetTV3;

	@ViewInject(R.id.tabwidget_v1)
	private View tabView1;

	@ViewInject(R.id.tabwidget_v2)
	private View tabView2;

	@ViewInject(R.id.tabwidget_v3)
	private View tabView3;

	@ViewInject(R.id.tv_title)
	private TextView title;

	@ViewInject(R.id.tv_title_right)
	public static TextView comfirm;

	@ViewInject(R.id.tabwidget_tv4)
	public static TextView message;

	public static Context mcontext;
	private final static int TAB1 = 1;
	private final static int TAB2 = 2;
	private final static int TAB3 = 3;
	private TabHost tabHost;

	private final static String TAG = "WebFiltersActivity";

	protected SwipeBackLayout layout;

	// 定义FragmentTabHost对象
		public static FragmentTabHost mTabHost;

		// 定义一个布局
		private LayoutInflater layoutInflater;

		// 定义数组来存放Fragment界面
		private Class fragmentArray[] = { MainUpActivity.class,
				MainTimeActivity.class, MainMyActivity.class };
		// Tab选项卡的文字
		private String mTextviewArray[] = { "白名单管理", "黑名单管理", "待审核网址" };
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_filter);
		layout = (SwipeBackLayout) LayoutInflater.from(this).inflate(
				R.layout.base, null);
		layout.attachToActivity(this);
		ViewUtils.inject(this);
		init();
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		overridePendingTransition(R.anim.base_slide_right_in,
				R.anim.base_slide_remain);
	}

	private void init() {
		title.setText("黑白名单");
		comfirm.setText("编辑");
		mcontext = this;
		tabeWidge1.setOnClickListener(myOnClickListener);
		tabeWidge2.setOnClickListener(myOnClickListener);
		tabeWidge3.setOnClickListener(myOnClickListener);
		initTableHost();

	}

	@Override
	protected void onResume() {
		super.onResume();
		CommonUtil.HandLockPassword(this);
		if (!"".equals(Constants.userId)) {
			getWebsTypeInfo();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (!CommonUtil.isForeground(this)) {
			Constants.isAPPActive = false;
		}
	}

	private void initTableHost() {
		tabHost = this.getTabHost();
		TabHost.TabSpec spec;
		Intent intent;

		intent = new Intent().setClass(this, AllowedWebsListActivity.class);
		spec = tabHost.newTabSpec(TAB1 + "").setIndicator(TAB1 + "")
				.setContent(intent);
		tabHost.addTab(spec);

		// 添加Tabhost
		Bundle data = new Bundle();
		data.putString("enterType", "tabhost");
		intent = new Intent();
		intent.putExtras(data);
		intent.setClass(this, UnallowedWebsListActivity.class);
		spec = tabHost.newTabSpec(TAB2 + "").setIndicator(TAB2 + "")
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, UnCheckedWebsListActivity.class);
		spec = tabHost.newTabSpec(TAB3 + "").setIndicator(TAB3 + "")
				.setContent(intent);
		tabHost.addTab(spec);
		tabHost.setCurrentTab(0); // 设置当前页

		tabWidgetTV1.setTextColor(0xff51bb18);
		tabView1.setVisibility(View.VISIBLE);
		tabWidgetTV2.setTextColor(0xffa1a1a1);
		tabView2.setVisibility(View.INVISIBLE);
		tabWidgetTV3.setTextColor(0xffa1a1a1);
		tabView3.setVisibility(View.INVISIBLE);

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

	OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (Constants.isEditing)
				return;
			switch (v.getId()) {
			case R.id.tabwidget1:
				tabWidgetTV1.setTextColor(0xff51bb18);
				tabView1.setVisibility(View.VISIBLE);
				tabWidgetTV2.setTextColor(0xffa1a1a1);
				tabView2.setVisibility(View.INVISIBLE);
				tabWidgetTV3.setTextColor(0xffa1a1a1);
				tabView3.setVisibility(View.INVISIBLE);
				tabHost.setCurrentTabByTag(TAB1 + "");
				break;
			case R.id.tabwidget2:
				tabWidgetTV1.setTextColor(0xffa1a1a1);
				tabView1.setVisibility(View.INVISIBLE);
				tabWidgetTV2.setTextColor(0xff51bb18);
				tabView2.setVisibility(View.VISIBLE);
				tabWidgetTV3.setTextColor(0xffa1a1a1);
				tabView3.setVisibility(View.INVISIBLE);
				tabHost.setCurrentTabByTag(TAB2 + "");
				break;
			case R.id.tabwidget3:
				tabWidgetTV1.setTextColor(0xffa1a1a1);
				tabView1.setVisibility(View.INVISIBLE);
				tabWidgetTV2.setTextColor(0xffa1a1a1);
				tabView2.setVisibility(View.INVISIBLE);
				tabWidgetTV3.setTextColor(0xff51bb18);
				tabView3.setVisibility(View.VISIBLE);
				tabHost.setCurrentTabByTag(TAB3 + "");
				break;
			}
		}
	};

	private void getWebsTypeInfo() {
		if (!Constants.isNetWork) {
			Toast.makeText(this, "您的网络已断开，请检查网络", Toast.LENGTH_SHORT).show();
			return;
		}
		String url = null;

		url = Constants.SERVER_URL + "?" + "requesttype=3" + "&userid="
				+ Constants.userId + "&token=" + Constants.tokenID;

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
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			overridePendingTransition(0, R.anim.base_slide_right_out);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
