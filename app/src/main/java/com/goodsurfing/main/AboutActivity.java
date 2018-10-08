package com.goodsurfing.main;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.goodsurfing.app.R;
import com.goodsurfing.base.BaseActivity;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.LogUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.umeng.analytics.MobclickAgent;

public class AboutActivity extends BaseActivity {

	private final static String TAG = "AboutActivity";

	protected static final int REFRESH = 100;

	@ViewInject(R.id.tv_title)
	private TextView title;

	@ViewInject(R.id.tv_title_right)
	private TextView right;

	@ViewInject(R.id.activity_about_tv)
	private TextView versionTv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		ViewUtils.inject(this);
		init();
	}

	private void init() {
		title.setText("关于");
		right.setVisibility(View.INVISIBLE);
		try {
			String pkName = getPackageName();
			String versionName = getPackageManager().getPackageInfo(pkName, 0).versionName;
			versionTv.setText("v" + versionName);
		} catch (Exception e) {
			LogUtil.logError(e);
		}
	}

	@OnClick(R.id.iv_title_left)
	public void onHeadBackClick(View view) {
		onBackPressed();
	}
}
