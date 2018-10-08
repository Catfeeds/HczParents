package com.goodsurfing.main;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.goodsurfing.app.R;
import com.goodsurfing.base.BaseActivity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.umeng.analytics.MobclickAgent;

public class MoreActivity extends BaseActivity {
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
        setContentView(R.layout.activity_more);
        ViewUtils.inject(this);
        init();
	}

	private void init() {
		title.setText("更多");	
		right.setVisibility(View.INVISIBLE);
	}
	@OnClick(R.id.iv_title_left)
	public void onHeadBackClick(View view) {
		onBackPressed();
	}

}
