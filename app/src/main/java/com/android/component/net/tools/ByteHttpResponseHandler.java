package com.android.component.net.tools;

import android.os.Handler;
import android.os.Message;

/**
 * @description
 * 
 * @author 谢志杰 E-mail: xzj2125123@163.com
 * @create 2015-10-20上午09:54:44
 * @version 1.0.0
 * @company 北京易微网络技术有限公司 Copyright: 版权所有 (c) 2014
 */
public class ByteHttpResponseHandler {

	private static final int REQUEST_FAILURE = -1;// 连接失败

	private static final int REQUEST_SUCCESS = 0;// 连接成功

	private Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case REQUEST_FAILURE:
				onFailure((String) msg.obj);
				break;
			case REQUEST_SUCCESS:
				if (msg.obj instanceof String) {
					onSuccess((String) msg.obj);
				} else {
					onSuccess((byte[]) msg.obj);
				}
				break;
			}
			return false;
		}
	});

	/**
	 * 连接成功时的回调方法
	 * 
	 * @param response
	 *            服务器返回的数据
	 */
	protected void onSuccess(String response) {
	}

	/**
	 * 连接成功时的回调方法
	 * 
	 * @param response
	 *            服务器返回的数据
	 */
	protected void onSuccess(byte[] response) {
	}

	/**
	 * 连接失败时的回调方法
	 * 
	 * @param response
	 *            导致连接失败的错误信息
	 */
	protected void onFailure(String error) {

	}

	/**
	 * 发送连接成功的Message给主线程
	 * 
	 * @param response
	 *            服务器返回的数据
	 */
	public void sendSuccessMessage(String response) {
		handler.sendMessage(obtainMessage(REQUEST_SUCCESS, response));
	}

	/**
	 * 发送连接成功的Message给主线程
	 * 
	 * @param response
	 *            服务器返回的数据
	 */
	public void sendSuccessMessage(byte[] response) {
		handler.sendMessage(obtainMessage(REQUEST_SUCCESS, response));
	}

	/**
	 * 发送连接失败的Message给主线程
	 * 
	 * @param response
	 *            导致连接失败的错误信息
	 */
	public void sendFailurMessage(String response) {
		handler.sendMessage(obtainMessage(REQUEST_FAILURE, response));
	}

	/**
	 * 获取一个Message
	 * 
	 * @param what
	 *            Message.what
	 * @param response
	 *            Message.obj
	 * @return 返回一个Message,封装了要传递的数据
	 */
	private Message obtainMessage(int what, Object response) {
		Message msg = Message.obtain();
		msg.what = what;
		if (response != null) {
			msg.obj = response;
		}
		return msg;
	}
}
