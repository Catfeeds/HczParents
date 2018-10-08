package com.goodsurfing.main;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.goodsurfing.app.R;
import com.goodsurfing.base.BaseActivity;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.server.PutDataServer;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResponder;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResult;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.CommonUtil;
import com.goodsurfing.utils.EventHandler;
import com.goodsurfing.utils.LogUtil;
import com.goodsurfing.utils.PropertiesUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class AddTimerCardsActivity extends BaseActivity {

	private final static String TAG = "AddWebsActivity";

	protected static final int REFRESH = 100;
	protected static final int SEND = 101;

	@ViewInject(R.id.tv_title)
	private TextView title;

	@ViewInject(R.id.tv_title_right)
	private TextView right;

	// @ViewInject(R.id.activity_add_webs_et_web)
	// private EditText websEditText ;

	@ViewInject(R.id.activity_add_webs_et_name)
	private EditText nameEditText;

	@ViewInject(R.id.activity_add_webs_et_timer_num)
	private EditText timeEditText;

	@ViewInject(R.id.activity_add_webs_tv2)
	private TextView dateTextView;

	@ViewInject(R.id.title_layout)
	private View title_layout;

	private String position = "0";
	private String name;

	private long setTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_timer_cards);
		ViewUtils.inject(this);
		init();
	}

	@Override
	protected void onResume() {
		super.onResume();
		ActivityUtil.sendEvent4UM(this, "functionSwitch", "AddTimerCards", 29);
	}

	@SuppressLint("ResourceAsColor")
	private void init() {
		title.setText("添加新奖励卡");
		right.setVisibility(View.INVISIBLE);
		name = getIntent().getExtras().getString("name");
	}

	private void refreshWebsInfo() {
		if (!Constants.isNetWork) {
			ActivityUtil.showPopWindow4Tips(this, title_layout, false, "当前网络不可用，请稍后再试...");
			return;
		}
		// 防止重复点击
		if (CommonUtil.isFastDoubleClick())
			return;

		String name = nameEditText.getText().toString();
		if ("".equals(name)) {
			ActivityUtil.showPopWindow4Tips(this, title_layout, false, "计时卡密码不能为空");
			// EventHandler.showToast(this, "计时卡密码不能为空");
			return;
		}
		String mdPassword = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			mdPassword = getString(md.digest(name.getBytes()));
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}

		String times = timeEditText.getText().toString();
		if (!"".equals(times)) {
			int time = Integer.parseInt(times);
			if (time > 24) {
				ActivityUtil.showPopWindow4Tips(this, title_layout, false, "时长不能超过24小时");
				// EventHandler.showToast(AddTimerCardsActivity.this,
				// "时长不能超过24小时");
				return;
			}
		} else {
			ActivityUtil.showPopWindow4Tips(this, title_layout, false, "时长不能为空");
			// EventHandler.showToast(AddTimerCardsActivity.this, "时长不能为空");
			return;
		}

		int totoalTimer = Integer.parseInt(times) * 3600;

		String expires = dateTextView.getText().toString().trim() + " 23:59:59";
		long temp = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			setTime = sdf.parse(expires).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (setTime - temp < totoalTimer * 1000) {
			ActivityUtil.showPopWindow4Tips(this, title_layout, false, "有效时间不能小于设置时长");
			// EventHandler.showToast(AddTimerCardsActivity.this,
			// "有效时间不能小于设置时长");
			return;
		}
		if ("".equals(expires)) {
			ActivityUtil.showPopWindow4Tips(this, title_layout, false, "有效时间不能为空");
			// EventHandler.showToast(AddTimerCardsActivity.this, "有效时间不能为空");
			return;
		}

		// 界面 改版 暂无 卡名字段
		String url = Constants.SERVER_URL + "?" + "committype=6&oper=1" + "&userid=" + Constants.userId + "&token=" + Constants.tokenID + "&name=" + this.name + "&password=" + mdPassword + "&totaltime=" + totoalTimer + "&ExpirationTime=" + expires;

		new PutDataServer(new DataServiceResponder() {

			@Override
			public void onResult(DataServiceResult result) {
				if ("0".equals(result.code)) {
					Intent intent = getIntent();
					setResult(RESULT_OK, intent);
					onBackPressed();
				} else {
					ActivityUtil.showPopWindow4Tips(AddTimerCardsActivity.this, title_layout, false, result.extra + "");
				}
			}

			@Override
			public void onFailure() {

			}
		}, url, this).execute();
	}

	@OnClick(R.id.activity_add_webs_add_rl)
	public void onAddClicks(View v) {
		refreshWebsInfo();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		Calendar calendar = Calendar.getInstance();
		// @android:style/Theme.Holo.Light
		DatePickerDialog dateDialog = new DatePickerDialog(AddTimerCardsActivity.this, R.style.dialog, new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				Date date = new Date(year - 1900, monthOfYear, dayOfMonth);
				// java.text.DateFormat format = DateFormat
				// .getDateFormat(AddTimerCardsActivity.this);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				dateTextView.setText(sdf.format(date));
			}
		}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		DatePicker datePicker = dateDialog.getDatePicker();
		datePicker.setMinDate(new Date(calendar.get(Calendar.YEAR) - 1900, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).getTime());
		datePicker.setMaxDate(new Date(calendar.get(Calendar.YEAR) - 1897, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).getTime());
		return dateDialog;

	}

	@OnClick(R.id.activity_add_webs_tv2)
	public void onDateClick(View v) {
		showDialog(0);
	}

	private static String getString(byte[] encryption) {
		StringBuffer strBuf = new StringBuffer();
		for (int i = 0; i < encryption.length; i++) {
			if (Integer.toHexString(0xff & encryption[i]).length() == 1) {
				strBuf.append("0").append(Integer.toHexString(0xff & encryption[i]));
			} else {
				strBuf.append(Integer.toHexString(0xff & encryption[i]));
			}
		}

		return strBuf.toString();
	}

	@OnClick(R.id.iv_title_left)
	public void onHeadBackClick(View view) {
		onBackPressed();
	}
}
