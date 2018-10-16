package com.goodsurfing.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.goodsurfing.app.R;
import com.goodsurfing.base.BaseActivity;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.server.GetTimerServer;
import com.goodsurfing.server.PutDataServer;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResponder;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResult;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.CommonUtil;
import com.goodsurfing.view.customview.TimerController;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
/**
 * 时段控制
 * @author 谢志杰 
 * @create 2017-12-18 下午2:41:22 
 * @version 1.0.0
 * @company 湖南一石科技有限公司 Copyright: 版权所有（c）2016
 */
public class TimeControllerActivity extends BaseActivity {

	@ViewInject(R.id.activity_timer_controller_l1)
	private LinearLayout linearLayout;

	@ViewInject(R.id.activity_timer_control_sv)
	private ScrollView scrollView;

	@ViewInject(R.id.tv_title)
	private TextView title;

	@ViewInject(R.id.tv_title_right)
	private TextView comfirm;

	public static String timerStr = "";
	@ViewInject(R.id.title_layout)
	private View title_layout;
	private final static String TAG = "TimerControllerActivity";

	private TimerController timer;
	private LinearLayout clearLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timer_control);
		ViewUtils.inject(this);
		init();
	}

	@SuppressLint("NewApi")
	private void init() {
		title.setText("时段控制");
		comfirm.setText("确定");
		comfirm.setVisibility(View.VISIBLE);
		getSavedDatas();
		TableLayout table = (TableLayout) findViewById(R.id.tablelayout);
		clearLayout = (LinearLayout) findViewById(R.id.table_clear_ll);
		timer = new TimerController(this, table, clearLayout, linearLayout, comfirm);
		timer.setColor("#B2E310");
		timer.initTabBackGround();
		timer.refreshTable(timerStr);

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!"".equals(Constants.userId))
			getTimerData();
	}

	private void getTimerData() {
		String url = Constants.SERVER_URL + "?" + "requesttype=1" + "&userid=" + Constants.userId + "&token=" + Constants.tokenID;
		new GetTimerServer(new DataServiceResponder() {

			@Override
			public void onResult(DataServiceResult result) {
				if ("0".equals(result.code)) {
					if (!timerStr.equals(result.extra)) {
						timerStr = result.extra;
						timer.refreshTable(timerStr);
						Editor editor = getSharedPreferences("TIMER_TABLE", Activity.MODE_PRIVATE).edit(); // 获取编辑器
						editor.putString(Constants.TIMESTR_KEY + Constants.userId, timerStr);
						editor.commit();
					}
				}
			}

			@Override
			public void onFailure() {

			}
		}, url, this).execute();
	}

	private void sendTimerData() {
		if (!Constants.isNetWork) {
			ActivityUtil.showPopWindow4Tips(this, title_layout, false, "当前网络不可用，请稍后再试...");
			return;
		}
		final String timers = TimerController.getSelectResult();
		String url = Constants.SERVER_URL + "?" + "committype=2" + "&userid=" + Constants.userId + "&token=" + Constants.tokenID + "&timestr=" + timers;
		ActivityUtil.showPopWindow4Tips(this, title_layout, false,false, "正在修改中....", -1);
		new PutDataServer(new DataServiceResponder() {

			@Override
			public void onResult(DataServiceResult result) {
				if ("0".equals(result.code)) {
					timerStr = timers;
					Editor editor = getSharedPreferences("TIMER_TABLE", Activity.MODE_PRIVATE).edit(); // 获取编辑器
					editor.putString(Constants.TIMESTR_KEY + Constants.userId, timers);
					editor.commit();// 提交修改
					timer.refreshTable(timerStr);
					ActivityUtil.showPopWindow4Tips(TimeControllerActivity.this, title_layout, true, "设置成功");
				} else {
					ActivityUtil.showPopWindow4Tips(TimeControllerActivity.this, title_layout, false, result.extra + "");
				}
			}

			@Override
			public void onFailure() {

			}
		}, url, this).execute();

	}

	@OnClick(R.id.tv_title_right)
	public void onComfirmClick(View v) {
		if (CommonUtil.isFastDoubleClick())
			return;
		if ("".equals(Constants.userId)) {
			LoginActivity.gotoLogin(this);
			return;
		}
		// if (Constants.mode != 2) {
		// ActivityUtil.showDialog(this, "温馨提示", "仅教育资源模式有效");
		// return;
		// }
		sendTimerData();
	}

	private void getSavedDatas() {
		SharedPreferences preferences = getSharedPreferences("TIMER_TABLE", Activity.MODE_PRIVATE);
		timerStr = preferences.getString(Constants.TIMESTR_KEY + Constants.userId, "");
	}

	@OnClick(R.id.iv_title_left)
	public void onHeadBackClick(View view) {
		onBackPressed();
	}
}
