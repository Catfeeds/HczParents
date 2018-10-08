package com.goodsurfing.main;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.goodsurfing.app.R;
import com.goodsurfing.app.wxapi.ShareWeiXinActivity;
import com.goodsurfing.base.BaseActivity;
import com.goodsurfing.fundlock.GestureEditActivity;
import com.goodsurfing.utils.ActivityUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
public class RegisterNextActivity extends BaseActivity {

	private final static String TAG = "LoginActivity";

	protected static final int REFRESH = 100;
	private Context context;
	@ViewInject(R.id.tv_title)
	private TextView title;
	@ViewInject(R.id.tv_title_right)
	private TextView right;
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFRESH:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_next);
        ViewUtils.inject(this);
        init();
	}

	private void init() {
		context = this;
		title.setText("开通");
		right.setText("分享");
		right.setVisibility(View.GONE);
		ActivityUtil.showDialog(this, "温馨提示", "您的初始密码为6个8");
		}

  
	@OnClick(R.id.activity_login_item_rl_login)
	public void loginClick(View view) {
		Intent intent = new Intent(this,ChargeChoicesActivity.class);
		startActivity(intent);
	}

	@OnClick(R.id.activity_login_item_rl_register)
	public void onRegisterClick(View view) {
		Intent intent = new Intent(this,GestureEditActivity.class);
		intent.putExtra("edit", true);
		intent.putExtra("register", true);
		startActivity(intent);
	}
	
	@OnClick(R.id.tv_title_right) 
	public void onShareClick(View v) {
		startActivity(new Intent(this, ShareWeiXinActivity.class));
	}
	@OnClick(R.id.iv_title_left)
	public void onHeadBackClick(View view) {
		ActivityUtil.goMainActivity(this);
		onBackPressed();
	}
	
}
