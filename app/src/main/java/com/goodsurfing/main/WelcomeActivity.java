package com.goodsurfing.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Window;

import com.goodsurfing.app.R;
import com.goodsurfing.base.BaseActivity;
import com.goodsurfing.base.GoodSurfingApplication;
import com.goodsurfing.beans.User;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.service.UpdateManager;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.CommonUtil;

public class WelcomeActivity extends BaseActivity {

	private static final int GO_HOME = 1000;
	private static final int GO_GUIDE = 1001;

	private static final int SPLASH_TIME = 3 * 1000;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		init();
	}

	private void init() {
		SharedPreferences preferences = getSharedPreferences(Constants.SP_NAME, MODE_PRIVATE);
		int oldVersionCode = preferences.getInt(Constants.VERSIONCODE, 0);
		// 判断上次是否已登录
		User user = CommonUtil.getUser(this);
		if (user != null) {
			Constants.userId = user.getuId();
			Constants.tokenID = user.getTokenId();
			Constants.Account = user.getAccount();
			Constants.userMobile = user.getPhone();
			Constants.name = user.getUserName();
			Constants.dealTime = user.getEditTime();
			Constants.sex = user.getUserSex();
			Constants.email = user.getEmail();
			Constants.birthday = user.getAvatar();
			Constants.userMobile = user.getPhone();
			Constants.SERVER_URL = user.getIP();
			Constants.adress = user.getAddress();
			Constants.loginTime = user.getLoginTime();
			Constants.rewardCode = user.getRewardCode();
			String mode = user.getMode();
			if ((mode != null) && !mode.equals(""))
				Constants.mode = Integer.parseInt(mode);
		}
		// 判断程序与第几次运行，如果是第一次运行则跳转到引导界面，否则跳转到主界面
		if (CommonUtil.getAppVersionCode(this) == oldVersionCode) {
			// 使用Handler的postDelayed方法，2秒后执行跳转到MainActivity
			mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_TIME);
		} else {
			preferences.edit().putInt(Constants.VERSIONCODE, GoodSurfingApplication.versionCode).commit();
			mHandler.sendEmptyMessageDelayed(GO_GUIDE, SPLASH_TIME);
		}

	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GO_HOME:
				goMain();
				break;
			case GO_GUIDE:
				goGuide();
				break;
			}
		}
	};

	private void goMain() {
		ActivityUtil.goMainActivity(this);
		onBackPressed();
	}

	private void goGuide() {
		Intent intent = new Intent(this, GuideView.class);
		startActivity(intent);
		onBackPressed();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
