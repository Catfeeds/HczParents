package com.goodsurfing.main;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ScrollView;
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

/**
 * 用户协议
 * 
 * @author 谢志杰
 * @create 2017-12-28 下午3:51:41
 * @version 1.0.0
 * @company 湖南一石科技有限公司 Copyright: 版权所有（c）2016
 */
public class UserAgreementActivity extends BaseActivity {

	private final static String TAG = "AboutActivity";

	protected static final int REFRESH = 100;

	@ViewInject(R.id.tv_title)
	private TextView title;

	@ViewInject(R.id.tv_title_right)
	private TextView right;

	@ViewInject(R.id.activity_toast_count_tv)
	private TextView versionTv;

	@ViewInject(R.id.user_agreement_sv)
	private ScrollView agreement_sv;

	@ViewInject(R.id.user_agreement_wv)
	private WebView agreement_wv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_xieyi_dialog);
		ViewUtils.inject(this);
		init();
	}

	private void init() {
		title.setText("好上网-用户协议");
		right.setVisibility(View.GONE);
		agreement_wv.getSettings().setJavaScriptEnabled(true);
		agreement_wv.setWebViewClient(new WebViewClient() {
			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				loadNavData();
			}
		});
		agreement_wv.loadUrl("https://www.haoup.net/Home/Index/user_agreement");
	}

	private void loadNavData() {

		try {
			agreement_wv.setVisibility(View.GONE);
			agreement_sv.setVisibility(View.VISIBLE);
			versionTv.setText(getResources().getString(R.string.user_xieyi));
		} catch (Exception e) {
			LogUtil.logError(e);
		}
	}

	@OnClick(R.id.iv_title_left)
	public void onHeadBackClick(View view) {
		onBackPressed();
	}
}
