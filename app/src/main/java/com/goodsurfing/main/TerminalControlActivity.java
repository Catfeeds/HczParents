package com.goodsurfing.main;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;

import com.goodsurfing.app.R;
import com.goodsurfing.base.BaseActivity;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.server.PutDataServer;
import com.goodsurfing.server.TerminalStateServer;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResponder;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResult;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.CommonUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
/**
 * 终端控制
 * @author 谢志杰 
 * @create 2017-12-18 下午2:41:08 
 * @version 1.0.0
 * @company 湖南一石科技有限公司 Copyright: 版权所有（c）2016
 */
public class TerminalControlActivity extends BaseActivity implements OnClickListener {

	private final static String TAG = "TerminalControlActivity";

	protected static final int REFRESH = 100;
	protected static final int SEND = 101;
	protected static final int GET = 102;

	private static String terminalStr = "000000";

	private int position;// 当前设置的ITEM

	private static char[] s = new char[4];

	@ViewInject(R.id.tv_title)
	private TextView title;
	@ViewInject(R.id.tv_title_right)
	private TextView right;

	@ViewInject(R.id.activity_control_tab1_cb)
	private CheckBox terminalCheckBox;

	@ViewInject(R.id.activity_control_tab2_cb)
	private CheckBox terminal1CheckBox;

	@ViewInject(R.id.activity_control_tab3_cb)
	private CheckBox terminal2CheckBox;

	@ViewInject(R.id.activity_control_tab4_cb)
	private CheckBox terminal3CheckBox;

	@ViewInject(R.id.activity_control_tab1_tv)
	private TextView stateTv;

	@ViewInject(R.id.activity_control_tab2_tv)
	private TextView state1Tv;

	@ViewInject(R.id.activity_control_tab3_tv)
	private TextView state2Tv;

	@ViewInject(R.id.activity_control_tab4_tv)
	private TextView state3Tv;
	@ViewInject(R.id.title_layout)
	private View title_layout;
	private CheckBox[] boxs;
	private TextView[] textViews;

	private Dialog dialog;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFRESH:
				refreshSeivicesInfo();
				break;

			case SEND:
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < 32; i++) {
					if (i <= 3) {
						sb.append(s[i]);
					} else
						sb.append('1');
				}
				terminalStr = sb.toString();
				sendTerminalState();
				break;
			case GET:
				getTerminalState();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_terminal_control);
		ViewUtils.inject(this);
		init();
	}

	private void init() {
		title.setText("终端控制");
		right.setVisibility(View.GONE);
		terminalCheckBox.setOnClickListener(this);
		terminal1CheckBox.setOnClickListener(this);
		terminal2CheckBox.setOnClickListener(this);
		terminal3CheckBox.setOnClickListener(this);
		boxs = new CheckBox[] { terminalCheckBox, terminal1CheckBox, terminal2CheckBox, terminal3CheckBox };
		textViews = new TextView[] { stateTv, state1Tv, state2Tv, state3Tv };
	}

	private void setBoxChecked(boolean checked) {
		for (int i = 0; i < boxs.length; i++) {
			boxs[i].setClickable(checked);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!"".equals(Constants.userId))
			mHandler.sendEmptyMessageDelayed(GET, 500);
	}

	private void getTerminalState() {
		if (!Constants.isNetWork) {
			ActivityUtil.showPopWindow4Tips(this, title_layout, false, "当前网络不可用，请稍后再试...");
			mHandler.sendEmptyMessage(REFRESH);
			return;
		}
		String url = Constants.SERVER_URL + "?" + "requesttype=2" + "&userid=" + Constants.userId + "&token=" + Constants.tokenID;
		setBoxChecked(false);
		new TerminalStateServer(new DataServiceResponder() {

			@Override
			public void onResult(DataServiceResult result) {
				if (result == null || result.code == null) {
					return;
				}
				if (result.code.equals("0")) {
					terminalStr = result.extra;
					for (int i = 0; i < 4; i++) {
						if (terminalStr.length() < 4)
							s[i] = '1';
						else
							s[i] = terminalStr.charAt(i);
					}
					mHandler.sendEmptyMessage(REFRESH);
				}
			}

			@Override
			public void onFailure() {

			}
		}, url, this).execute();
	}

	private void sendTerminalState() {
		if (!Constants.isNetWork) {
			ActivityUtil.showPopWindow4Tips(this, title_layout, false, "当前网络不可用，请稍后再试...");
			if (s[position] == '1') {
				s[position] = '0';
			} else {
				s[position] = '1';
			}
			mHandler.sendEmptyMessage(REFRESH);
			return;
		}
		String url = Constants.SERVER_URL + "?" + "committype=3" + "&userid=" + Constants.userId + "&token=" + Constants.tokenID + "&devicestr=" + terminalStr;
		setBoxChecked(false);
		ActivityUtil.showPopWindow4Tips(this, title_layout, false,false, "设置中...", -1);
		new PutDataServer(new DataServiceResponder() {

			@Override
			public void onResult(DataServiceResult result) {
				if ("0".equals(result.code)) {
					mHandler.sendEmptyMessage(REFRESH);
					ActivityUtil.showPopWindow4Tips(TerminalControlActivity.this, title_layout, true, "设置成功");
				} else {
					if (s[position] == '1') {
						s[position] = '0';
					} else {
						s[position] = '1';
					}
					mHandler.sendEmptyMessage(REFRESH);
					ActivityUtil.showPopWindow4Tips(TerminalControlActivity.this, title_layout, false, result.extra + "");
				}
			}

			@Override
			public void onFailure() {
				if (s[position] == '1') {
					s[position] = '0';
				} else {
					s[position] = '1';
				}
				mHandler.sendEmptyMessage(REFRESH);
			}
		}, url, this).execute();
	}

	private void refreshSeivicesInfo() {
		for (int i = 0; i <= 3; i++) {
			if (isControlStart(s[i])) {
				textViews[i].setText("正在控制");
				textViews[i].setTextColor(0xff73c940);
				boxs[i].setChecked(true);
			} else {
				textViews[i].setText("未控制");
				textViews[i].setTextColor(0xfffc5959);
				boxs[i].setChecked(false);
			}
		}
		setBoxChecked(true);
	}

	private boolean isControlStart(char status) {
		if (status == '1')
			return true;
		else if (status == '0')
			return false;
		return false;
	}

	@Override
	public void onClick(View v) {
		if ("".equals(Constants.userId)) {
			((CheckBox) v).setChecked(((CheckBox) v).isChecked() && false);
			LoginActivity.gotoLogin(this);
			return;
		}
		if (CommonUtil.isFastDoubleClick())
			return;

		String title = "";
		switch (v.getId()) {
		case R.id.activity_control_tab1_cb:
			if (s[0] != '1') {
				stateTv.setText("正在控制");
				stateTv.setTextColor(0xff7c3940);
				s[0] = '1';
			} else {
				stateTv.setText("未控制");
				stateTv.setTextColor(0xfffc5959);
				s[0] = '0';
			}
			position = 0;
			title = "确定控制电脑上网";
			break;
		case R.id.activity_control_tab2_cb:
			if (s[1] != '1') {
				state1Tv.setText("正在控制");
				state1Tv.setTextColor(0xff7c3940);
				s[1] = '1';
			} else {
				state1Tv.setText("未控制");
				state1Tv.setTextColor(0xfffc5959);
				s[1] = '0';
			}
			position = 1;
			title = "确定控制手机上网";
			break;
		case R.id.activity_control_tab3_cb:
			if (s[2] != '1') {
				state2Tv.setText("正在控制");
				state2Tv.setTextColor(0xff7c3940);
				s[2] = '1';

			} else {
				state2Tv.setText("未控制");
				state2Tv.setTextColor(0xfffc5959);
				s[2] = '0';
			}
			position = 2;
			title = "确定控制平板上网";
			break;
		case R.id.activity_control_tab4_cb:
			if (s[3] != '1') {
				state3Tv.setText("正在控制");
				state3Tv.setTextColor(0xff7c3940);
				s[3] = '1';

			} else {
				state3Tv.setText("未控制");
				state3Tv.setTextColor(0xfffc5959);
				s[3] = '0';
			}
			position = 3;
			title = "确定控制电视上网";
			break;

		}
		Message msg = new Message();
		msg.what = SEND;
		msg.obj = title;
		mHandler.sendMessage(msg);
	}

	@OnClick(R.id.iv_title_left)
	public void onHeadBackClick(View view) {
		onBackPressed();
	}
}
