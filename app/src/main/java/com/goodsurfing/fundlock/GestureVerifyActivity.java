package com.goodsurfing.fundlock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.goodsurfing.app.R;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.fundlock.widget.GestureContentView;
import com.goodsurfing.fundlock.widget.GestureDrawline.GestureCallBack;
import com.goodsurfing.main.LoginActivity;

/**
 * 
 * 手势绘制/校验界面
 * 
 */
public class GestureVerifyActivity extends Activity implements android.view.View.OnClickListener {
	private FrameLayout mGestureContainer;
	private GestureContentView mGestureContentView;
	private TextView mTextForget;
	private String mConfirmPassword;
	private long mExitTime = 0;
	private int mParamIntentCode;
	private boolean isEdit;
	private ImageView leftView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gesture_verify);
		ObtainExtraData();
		setUpViews();
		setUpListeners();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isEdit) {
			leftView.setVisibility(View.VISIBLE);
			leftView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					onBackPressed();
				}
			});
		} else {
			leftView.setVisibility(View.INVISIBLE);
		}
	}

	private void ObtainExtraData() {
		mConfirmPassword = getSharedPreferences(Constants.LOCK, MODE_PRIVATE).getString(Constants.LOCK_KEY + Constants.userId, "");
	}

	private void setUpViews() {
		mGestureContainer = (FrameLayout) findViewById(R.id.gesture_container);
		mTextForget = (TextView) findViewById(R.id.text_forget_gesture);
		isEdit = getIntent().getExtras().getBoolean("edit");
		leftView = (ImageView) findViewById(R.id.iv_title_left);
		// 初始化一个显示各个点的viewGroup
		mGestureContentView = new GestureContentView(this, true, mConfirmPassword, new GestureCallBack() {

			@Override
			public void onGestureCodeInput(String inputCode) {

			}

			@Override
			public void checkedSuccess() {
				mGestureContentView.clearDrawlineState(0L);
				if (isEdit) {
					Intent shoushi = new Intent(GestureVerifyActivity.this, GestureEditActivity.class);
					shoushi.putExtra("edit", false);
					startActivity(shoushi);
					onBackPressed();
				} else {
					onBackPressed();
				}
			}

			@Override
			public void checkedFail() {
				mGestureContentView.clearDrawlineState(1300L);
				Toast.makeText(GestureVerifyActivity.this, "手势密码错误", 1000).show();
			}
		});
		// 设置手势解锁显示到哪个布局里面
		mGestureContentView.setParentView(mGestureContainer);
	}

	private void setUpListeners() {
		mTextForget.setOnClickListener(this);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, R.anim.base_slide_right_out);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.text_forget_gesture:
			getSharedPreferences(Constants.SP_NAME, 0).edit().putBoolean(Constants.CECKBOX_KEY + Constants.userId, false).commit();
			getSharedPreferences(Constants.LOCK, MODE_PRIVATE).edit().putString(Constants.LOCK_KEY + Constants.userId, "").commit();
			LoginActivity.gotoLogin(this);
			onBackPressed();
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && !isEdit) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
