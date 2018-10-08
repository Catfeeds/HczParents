package com.goodsurfing.main;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.goodsurfing.app.R;
import com.goodsurfing.base.BaseActivity;
import com.goodsurfing.utils.ActivityUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 支付完成界面
 * 
 * @author 谢志杰
 * @create 2017-10-16 上午11:09:58
 * @version 1.0.0
 * @company 湖南一石科技有限公司 Copyright: 版权所有（c）2016
 */
public class PayOkActivity extends BaseActivity {
	protected static final int REFRESH = 100;
	@ViewInject(R.id.tv_title)
	private TextView title;
	@ViewInject(R.id.tv_title_right)
	private TextView right;
	@ViewInject(R.id.activity_pay_oks_base_tv2)
	private TextView nameView;
	@ViewInject(R.id.activity_pay_oks_time_tv)
	private TextView timeView;
	@ViewInject(R.id.activity_pay_oks_money_tv)
	private TextView moneyView;
	@ViewInject(R.id.activity_pay_ok_tv_btn)
	private TextView btnView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay_ok);
		ViewUtils.inject(this);
		init();
	}

	private void init() {
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			nameView.setText(bundle.getString("name"));
			timeView.setText(bundle.getString("time"));
			moneyView.setText(bundle.getString("money"));
		}
		title.setText("支付完成");
		right.setVisibility(View.INVISIBLE);
	}

	@OnClick(R.id.activity_pay_ok_tv_btn)
	public void onClickOk(View v) {
		ActivityUtil.goMainActivity(this);
		onBackPressed();
	}

	@OnClick(R.id.iv_title_left)
	public void onHeadBackClick(View view) {
		onBackPressed();
	}
}
