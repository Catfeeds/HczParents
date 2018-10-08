package com.goodsurfing.map;

import net.sourceforge.simcpux.MD5;

import org.json.JSONObject;

import android.R.integer;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.goodsurfing.app.R;
import com.goodsurfing.base.BaseActivity;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.server.DoGetCodeServer;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResponder;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResult;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.EventHandler;
import com.goodsurfing.utils.LogUtil;
import com.goodsurfing.utils.RegisterCodeTimer;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class AddChildrenActivity extends BaseActivity {
	private final static String TAG = "AddChildrenActivity";

	protected static final int REFRESH = 100;

	@ViewInject(R.id.tv_title)
	private TextView title;

	@ViewInject(R.id.tv_title_right)
	private TextView right;

	@ViewInject(R.id.activity_add_children_et_phone)
	private EditText phoneEt;

	@ViewInject(R.id.activity_add_children_et_code)
	private EditText codeEt;

	@ViewInject(R.id.activity_add_children_delete_phone)
	private ImageView deleteIv;

	@ViewInject(R.id.activity_add_children_getcode)
	private TextView codeView;

	@ViewInject(R.id.activity_add_children_sign_btn)
	private TextView signBtnTv;
	@ViewInject(R.id.title_layout)
	private View title_layout;
	private static boolean isGettingCodeFinish = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_children);
		ViewUtils.inject(this);
		init();
	}

	@Override
	protected void onResume() {
		super.onResume();
		signBtnTv.setEnabled(false);
		signBtnTv.setBackgroundResource(R.drawable.view_btn_gray__bg);
	}

	private void init() {
		title.setText("添加手机号");
		right.setVisibility(View.INVISIBLE);
		phoneEt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence chars, int arg1, int arg2, int arg3) {
				if (chars.length() > 0) {
					deleteIv.setVisibility(View.VISIBLE);
				} else {
					deleteIv.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});
		codeEt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				if (arg0.length() == 6) {
					signBtnTv.setEnabled(true);
					signBtnTv.setBackgroundResource(R.drawable.view_bottom_button_bg);
				} else {
					signBtnTv.setEnabled(false);
					signBtnTv.setBackgroundResource(R.drawable.view_btn_gray__bg);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});
	}

	@OnClick(R.id.activity_add_children_getcode)
	private void onClickGetCode(View view) {
		codeView.setEnabled(false);
		doGetCode();
	}

	@OnClick(R.id.activity_add_children_delete_phone)
	private void onClickDeletePhone(View view) {
		phoneEt.setText("");
	}

	@OnClick(R.id.activity_add_children_sign_btn)
	private void onClickSignCode(View view) {
		doSignCode();
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 正在倒计时
			case RegisterCodeTimer.IN_RUNNING:
				codeView.setBackgroundResource(R.drawable.add_children_gray);
				codeView.setText("已发送" + time-- + "秒");
				if (time > 0)
					mHandler.sendEmptyMessageDelayed(RegisterCodeTimer.IN_RUNNING, 1000);
				else
					mHandler.sendEmptyMessage(RegisterCodeTimer.END_RUNNING);
				break;

			// 完成倒计时
			case RegisterCodeTimer.END_RUNNING:
				codeView.setBackgroundResource(R.drawable.view_bottom_button_bg);
				mHandler.removeMessages(RegisterCodeTimer.IN_RUNNING);
				codeView.setEnabled(true);
				codeView.setText("重新获取");
				break;
			}
		};
	};
	private int time = 60;

	/**
	 * 获取手机验证码
	 */
	private void doGetCode() {
		if (!Constants.isNetWork) {
			ActivityUtil.showPopWindow4Tips(this,title_layout, false, "当前网络不可用，请稍后再试...");
			codeView.setEnabled(true);
			return;
		}

		String phoneNum = phoneEt.getText().toString();

		if ("".equals(phoneNum) || !phoneNum.matches("^[1-9]\\d{10}$")) {
			ActivityUtil.showPopWindow4Tips(this,title_layout, false, "请输入正确的手机号");
			codeView.setEnabled(true);
			return;
		}
		String times = System.currentTimeMillis() + "";
		long token = Integer.parseInt(times.substring(times.length() - 5, times.length())) * Integer.parseInt(phoneNum.substring(phoneNum.length() - 4, phoneNum.length()));
		String keyString = "shian" + token + "haoup";

		String key=	MD5.getMessageDigest(keyString.getBytes());
		String url = null;
		url = Constants.EDIT_PHONE_CODE_URL+"?c=index&a=index&tel=" + phoneNum + "&token=" + times + "&key=" + key;
		new DoGetCodeServer(new DataServiceResponder() {

			@Override
			public void onResult(DataServiceResult result) {
				try {
					JSONObject jsonObject = new JSONObject((String) result.result);
					String errCode = null;
					errCode = jsonObject.getString("statusCode");
					if ("000000".equals(errCode)) {
						time = 60;
						ActivityUtil.showPopWindow4Tips(AddChildrenActivity.this,title_layout, true, "发送验证码成功");
						mHandler.sendEmptyMessageDelayed(RegisterCodeTimer.IN_RUNNING, 0);
					} else {
						time = 60;
						ActivityUtil.showPopWindow4Tips(AddChildrenActivity.this,title_layout, false, "发送验证码失败");
						mHandler.sendEmptyMessageDelayed(RegisterCodeTimer.END_RUNNING, 0);
					}
				} catch (Exception e) {
					time = 60;
					ActivityUtil.showPopWindow4Tips(AddChildrenActivity.this,title_layout, false, "发送验证码失败");
					mHandler.sendEmptyMessageDelayed(RegisterCodeTimer.END_RUNNING, 0);
					LogUtil.logError(e);
				}
			}

			@Override
			public void onFailure() {
				time = 60;
				ActivityUtil.showPopWindow4Tips(AddChildrenActivity.this,title_layout, false, "发送验证码失败");
				mHandler.sendEmptyMessageDelayed(RegisterCodeTimer.END_RUNNING, 0);
			}
		}, url, this).execute();

	}

	/**
	 * 验证手机验证码
	 */
	private void doSignCode() {
		if (!Constants.isNetWork) {
			ActivityUtil.showPopWindow4Tips(this,title_layout, false, "当前网络不可用，请稍后再试...");
			return;
		}

		final String phoneNum = phoneEt.getText().toString();
		String url = null;

		String code = codeEt.getText().toString();
		if ("".equals(code) || !code.matches("^[0-9]{6}$")) {
			ActivityUtil.showPopWindow4Tips(this,title_layout, false, "验证码格式不正确");
			return;
		} else {
			LogUtil.log(TAG, code);
		}
		url =Constants.EDIT_PHONE_CODE_URL + "?c=index&a=getCode&tel="+ phoneNum + "&code=" + code;
		new DoGetCodeServer(new DataServiceResponder() {

			@Override
			public void onResult(DataServiceResult result) {
				try {
					JSONObject jsonObject = new JSONObject((String) result.result);
					String errCode = null;
					errCode = jsonObject.getString("status");

					if ("1".equals(errCode)) {
						ActivityUtil.showPopWindow4Tips(AddChildrenActivity.this,title_layout, true, "验证成功");
						Intent intent = new Intent(AddChildrenActivity.this, SettingChildrenActivity.class);
						intent.putExtra("phone", phoneNum);
						startActivity(intent);
						onBackPressed();
					} else if("0".equals(errCode)){
						ActivityUtil.showPopWindow4Tips(AddChildrenActivity.this,title_layout, false, "验证码错误");
					}else if("-1".equals(errCode)){
						ActivityUtil.showPopWindow4Tips(AddChildrenActivity.this,title_layout, false, "验证码过期");
					}
				} catch (Exception e) {
					LogUtil.logError(e);
				}
			}

			@Override
			public void onFailure() {

			}
		}, url, this).execute();
	}

	@OnClick(R.id.iv_title_left)
	public void onHeadBackClick(View view) {
		onBackPressed();
	}

}
