package com.goodsurfing.base;

import android.content.Intent;
import android.os.Bundle;

import com.goodsurfing.app.R;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.utils.CommonUtil;

/**
 * Created by hailonghan on 15/6/11.
 */
public abstract class BasePhotoActivity extends SwipeBackActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		CommonUtil.HandLockPassword(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (!CommonUtil.isForeground(this)) {
			Constants.isAPPActive = false;
		}
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		overridePendingTransition(R.anim.base_slide_bottom_in, R.anim.base_slide_remain);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, R.anim.base_slide_bottom_out);
	}
}
