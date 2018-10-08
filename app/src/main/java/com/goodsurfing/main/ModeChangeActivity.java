package com.goodsurfing.main;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.goodsurfing.app.R;
import com.goodsurfing.base.BaseActivity;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.server.GetModeDataServer;
import com.goodsurfing.server.PutDataServer;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResponder;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResult;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.EventHandler;
import com.goodsurfing.utils.SharUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.umeng.analytics.MobclickAgent;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class ModeChangeActivity extends BaseActivity implements OnClickListener {

	private final static String TAG = "ModeChangeActivity";
	protected static final int REFRESH = 100;
	protected static final int REFRESH_GET = 101;
	@ViewInject(R.id.mode_title_layout)
	private View mode_title_layout;
	@ViewInject(R.id.tv_title)
	private TextView title;
	@ViewInject(R.id.tv_title_right)
	private TextView rightView;
	private String custemType = "1";

	private int newMode = 0;

	@ViewInject(R.id.activity_mode_tab_1)
	private RelativeLayout tabRl1;
	@ViewInject(R.id.activity_mode_tab_2)
	private RelativeLayout tabRl2;
	@ViewInject(R.id.activity_mode_tab_3)
	private RelativeLayout tabRl3;
	@ViewInject(R.id.activity_mode_tab_4)
	private RelativeLayout tabRl4;

	@ViewInject(R.id.activity_mode_tab1_cb)
	private ImageView modeImageView1;
	@ViewInject(R.id.activity_mode_tab2_cb)
	private ImageView modeImageView2;
	@ViewInject(R.id.activity_mode_tab3_cb)
	private ImageView modeImageView3;
	@ViewInject(R.id.activity_mode_tab4_cb)
	private ImageView modeImageView4;

	private boolean isStartClick = false;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFRESH:
				Constants.totalSeconds = 0;
				setDefaultSelectItem(Constants.mode);
				ActivityUtil.sendEvent4UM(ModeChangeActivity.this, "modeSwitch",Constants.mode + "", 2);
				break;
			case REFRESH_GET:
				setDefaultSelectItem(Constants.mode);
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mode_change);
		ViewUtils.inject(this);
		init();
	}

	@SuppressLint("NewApi")
	private void init() {
		title.setText("模式切换");
		rightView.setVisibility(View.GONE);
		rightView.setTextColor(Color.parseColor("#bbbbbb"));
		tabRl1.setOnClickListener(this);
		tabRl2.setOnClickListener(this);
		tabRl3.setOnClickListener(this);
		tabRl4.setOnClickListener(this);
		tabRl1.setTag(1);
		tabRl2.setTag(2);
		tabRl3.setTag(3);
		tabRl4.setTag(4);
		setDefaultSelectItem(Constants.mode);
	}

	/**
	 * 设置选择模式
	 * 
	 * @param position
	 */
	private void setDefaultSelectItem(int position) {
		modeImageView1.setBackground(getResources().getDrawable(R.drawable.mode_change_unselect));
		modeImageView2.setBackground(getResources().getDrawable(R.drawable.mode_change_unselect));
		modeImageView3.setBackground(getResources().getDrawable(R.drawable.mode_change_unselect));
		modeImageView4.setBackground(getResources().getDrawable(R.drawable.mode_change_unselect));
		switch (position) {
		case 0:
		case 1:
			modeImageView1.setBackground(getResources().getDrawable(R.drawable.mode_change_select));
			break;
		case 2:
			modeImageView2.setBackground(getResources().getDrawable(R.drawable.mode_change_select));
			break;
		case 3:
			modeImageView3.setBackground(getResources().getDrawable(R.drawable.mode_change_select));
			break;
		case 4:
			modeImageView4.setBackground(getResources().getDrawable(R.drawable.mode_change_select));
			break;
		}
	}

	/**
	 * 改变 模式请求
	 * 
	 * @param item
	 */
	private void changeModeByNet(int item) {
		if ("".equals(Constants.userId)) {
			LoginActivity.gotoLogin(this);
			return;
		}
		if (!Constants.isNetWork) {
			ActivityUtil.showPopWindow4Tips(ModeChangeActivity.this, mode_title_layout, false, "当前网络不可用，请稍后再试...");
			return;
		}
		ActivityUtil.showPopWindow4Tips(ModeChangeActivity.this, mode_title_layout, false, false, "模式切换中...", -1);
		String url = null;
		final int position = item;
		isStartClick = true;
		if (position != 6)
			url = Constants.SERVER_URL + "?" + "committype=1" + "&userid=" + Constants.userId + "&token=" + Constants.tokenID + "&mode=" + position;
		else
			url = Constants.SERVER_URL + "?" + "committype=1" + "&userid=" + Constants.userId + "&token=" + Constants.tokenID + "&mode=" + position + "&customtype=" + custemType;

		new PutDataServer(new DataServiceResponder() {

			@Override
			public void onResult(DataServiceResult result) {
				isStartClick = false;
				if ("0".equals(result.code)) {
					ActivityUtil.showPopWindow4Tips(ModeChangeActivity.this, mode_title_layout, true, "模式切换成功");
					Constants.mode = position;
					SharUtil.saveMode(ModeChangeActivity.this, position);
					if (null == mHandler)
						return;
					mHandler.sendEmptyMessage(REFRESH);
				} else {
					ActivityUtil.showPopWindow4Tips(ModeChangeActivity.this, mode_title_layout, false, result.extra + "");
				}
			}

			@Override
			public void onFailure() {
				isStartClick = false;
			}
		}, url, this).execute();

	}

	@OnClick(R.id.iv_title_left)
	public void onHeadBackClick(View view) {
		onBackPressed();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (Constants.userId != null && !"".equals(Constants.userId)) {
			getModeData();
		}
	}

	/**
	 * 获取后台服务器的 模式
	 */
	private void getModeData() {
		// userid=24&token=token145208399124&requesttype=11
		if (!Constants.isNetWork) {
			return;
		}
		String url = Constants.SERVER_URL + "?" + "requesttype=11" + "&userid=" + Constants.userId + "&token=" + Constants.tokenID;

		new GetModeDataServer(new DataServiceResponder() {

			@Override
			public void onResult(DataServiceResult result) {
				if ("0".equals(result.code)) {
					if (Constants.mode != (Integer) result.result) {
						Constants.mode = (Integer) result.result;
						SharUtil.saveMode(ModeChangeActivity.this, Constants.mode);
						mHandler.sendEmptyMessage(REFRESH_GET);
					}
				}
			}

			@Override
			public void onFailure() {

			}
		}, url, this).execute();

	}

	@Override
	public void onClick(View v) {
		if (isStartClick)
			return;
		setDefaultSelectItem(Integer.parseInt(v.getTag() + ""));
		switch (v.getId()) {
		case R.id.activity_mode_tab_1:
			newMode = 1;
			break;
		case R.id.activity_mode_tab_2:
			newMode = 2;
			break;
		case R.id.activity_mode_tab_3:
			newMode = 3;
			break;
		case R.id.activity_mode_tab_4:
			newMode = 4;
			break;
		}
		if (newMode != Constants.mode) {
			changeModeByNet(newMode);
		} else {
			rightView.setTextColor(Color.parseColor("#bbbbbb"));
		}
	}

}
