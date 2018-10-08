package com.goodsurfing.utils;

import java.util.Collections;

import net.sourceforge.simcpux.MD5;

import org.json.JSONObject;

import com.goodsurfing.app.R;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.main.EditPhoneActivity;
import com.goodsurfing.server.DoGetCodeServer;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResponder;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResult;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class SingeCodeUtil {
//
//	protected void showSign() {
//		final Dialog dialog = new Dialog(this, R.style.AlertDialogCustom);
//		View view = View.inflate(this, R.layout.layout_sign_add_dialog, null);
//		timeView = (TextView) view.findViewById(R.id.layout_content_phone_time);
//		phoneView = (TextView) view.findViewById(R.id.layout_content_phone);
//		codeViews[0] = (TextView) view.findViewById(R.id.layout_content_phone_code_1);
//		codeViews[1] = (TextView) view.findViewById(R.id.layout_content_phone_code_2);
//		codeViews[2] = (TextView) view.findViewById(R.id.layout_content_phone_code_3);
//		codeViews[3] = (TextView) view.findViewById(R.id.layout_content_phone_code_4);
//		codeViews[4] = (TextView) view.findViewById(R.id.layout_content_phone_code_5);
//		codeViews[5] = (TextView) view.findViewById(R.id.layout_content_phone_code_6);
//		TextView leftView = (TextView) view.findViewById(R.id.layout_content_phone_left);
//		rightView = (TextView) view.findViewById(R.id.layout_content_phone_right);
//		phoneView.setText(users.get(mPosition).getPhone());
//		timeView.setEnabled(false);
//		timeView.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				doGetCode();
//			}
//		});
//		leftView.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				dialog.dismiss();
//			}
//		});
//		rightView.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				Constants.user.add(users.get(mPosition));
//				onBackPressed();
//				// doSignCode();
//				dialog.dismiss();
//			}
//		});
//		dialog.setContentView(view);
//		dialog.show();
//		// doGetCode();
//		time = 60;
//		timeView.setEnabled(false);
//		mHandler.sendEmptyMessageDelayed(RegisterCodeTimer.IN_RUNNING, 0);
//	}
//
//	private Thread mThread = new Thread(new Runnable() {
//
//		@Override
//		public void run() {
//			initData();
//			mHandler.sendEmptyMessage(REFRESH);
//		}
//	});
//	private Handler mHandler = new Handler() {
//		@Override
//		public void handleMessage(android.os.Message msg) {
//			switch (msg.what) {
//			case REFRESH:
//				if ((users != null && users.size() > 0)) {
//					Collections.sort(users, new UserComparator());
//				}
//				adapter.notifyDataSetChanged();
//				break;
//			// 正在倒计时
//			case RegisterCodeTimer.IN_RUNNING:
//				timeView.setText(time-- + "秒后再次获取验证码");
//				if (time > 0)
//					mHandler.sendEmptyMessageDelayed(RegisterCodeTimer.IN_RUNNING, 1000);
//				else
//					mHandler.sendEmptyMessage(RegisterCodeTimer.END_RUNNING);
//				break;
//
//			// 完成倒计时
//			case RegisterCodeTimer.END_RUNNING:
//				timeView.setEnabled(true);
//				timeView.setText("没收到验证码？点击再次获取");
//				break;
//			}
//		};
//	};
//	private int time = 60;
//
//	/**
//	 * 获取手机验证码
//	 */
//	private void doGetCode() {
//		if (!Constants.isNetWork) {
//			ActivityUtil.showDialog(this, "温馨提示", "您的网络已断开，请检查网络");
//			return;
//		}
//
//		String phoneNum = phoneView.getText().toString();
//
//		if ("".equals(phoneNum) || !phoneNum.matches("^[1-9]\\d{10}$")) {
//			EventHandler.showToast(this, "您输入的电话号码格式不正确或为空");// ^(13[0-9]|15[0|1|3|6|7|8|9]|18[0-9]|14[7])\\d{8}$
//			return;
//		}
//		String times = System.currentTimeMillis() + "";
//		long token = Integer.parseInt(times.substring(times.length() - 5, times.length())) * Integer.parseInt(phoneNum.substring(phoneNum.length() - 4, phoneNum.length()));
//		String keyString = "shian" + token + "haoup";
//
//		String key = MD5.getMessageDigest(keyString.getBytes());
//		String url = null;
//		url = Constants.CODE_URL + "?c=index&a=index&tel=" + phoneNum + "&token=" + times + "&key=" + key;
//		new DoGetCodeServer(new DataServiceResponder() {
//
//			@Override
//			public void onResult(DataServiceResult result) {
//				try {
//					JSONObject jsonObject = new JSONObject((String) result.result);
//					String errCode = null;
//					errCode = jsonObject.getString("statusCode");
//					if ("000000".equals(errCode)) {
//						time = 60;
//						timeView.setEnabled(false);
//						EventHandler.showToast(EditPhoneActivity.this,  "发送验证码成功", R.drawable.toast_failure, 3);
//						mHandler.sendEmptyMessageDelayed(RegisterCodeTimer.IN_RUNNING, 0);
//						onBackPressed();
//					}else {
//						String message = jsonObject.getString("message");
//						EventHandler.showToast(EditPhoneActivity.this, message, R.drawable.toast_failure, 3);
//					}
//				} catch (Exception e) {
//					LogUtil.logError(e);
//				}
//			}
//
//			@Override
//			public void onFailure() {
//
//			}
//		}, url, this).execute();
//	}
//
//	/**
//	 * 验证手机验证码
//	 */
//	private void doSignCode() {
//		if (!Constants.isNetWork) {
//			ActivityUtil.showDialog(this, "温馨提示", "您的网络已断开，请检查网络");
//			return;
//		}
//
//		String phoneNum = phoneView.getText().toString();
//		String url = null;
//
//		String code = "";
//		for (int i = 0; i < codeViews.length; i++) {
//			code += codeViews[i].getText().toString();
//		}
//		if ("".equals(code) || !code.matches("^[0-9]{6}$")) {
//			EventHandler.showToast(this, "您输入的验证码格式不正确或为空");
//			return;
//		} else {
//			LogUtil.log(TAG, code);
//		}
//		url = Constants.CODE_URL + "?c=index&a=getCode&tel=" + phoneNum + "&code=" + code;
//		new DoGetCodeServer(new DataServiceResponder() {
//
//			@Override
//			public void onResult(DataServiceResult result) {
//				try {
//					JSONObject jsonObject = new JSONObject((String) result.result);
//					String errCode = null;
//					errCode = jsonObject.getString("status");
//
//					if ("1".equals(errCode)) {
//						EventHandler.showToast(EditPhoneActivity.this, "添加成功", R.drawable.toast_ok, 14);
//						Constants.user.add(users.get(mPosition));
//					}
//				} catch (Exception e) {
//					LogUtil.logError(e);
//				}
//			}
//
//			@Override
//			public void onFailure() {
//
//			}
//		}, url, this).execute();
//	}
//

}
