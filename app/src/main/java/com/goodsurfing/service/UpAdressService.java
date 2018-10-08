package com.goodsurfing.service;

import java.util.Timer;
import java.util.TimerTask;

import com.goodsurfing.constants.Constants;
import com.goodsurfing.server.GetBundleUserServer;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResponder;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResult;
import com.goodsurfing.utils.ActivityUtil;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

/**
 * 注册验证码计时服务
 */
public class UpAdressService extends Service {

	private static final long TIME_INTERVAL_MS = 30 * 1000;
	private static Handler mHandler;
	private Timer timer;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("xzj", System.currentTimeMillis()+"");
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		timer = new Timer();
		timer.schedule(timerTask, TIME_INTERVAL_MS, TIME_INTERVAL_MS);
		Log.i("xzj", System.currentTimeMillis()+"");
	}
	

	private TimerTask timerTask = new TimerTask() {

		@Override
		public void run() {
			getBundleUser();
		}
	};

	/**
	 * 获取服务器返回数据 userid=24&token=token145208399124&requesttype=14
	 */
	private void getBundleUser() {
		String url = Constants.SERVER_URL + "?" + "requesttype=14" + "&userid=" + Constants.userId + "&token=" + Constants.tokenID;
		new GetBundleUserServer(new DataServiceResponder() {

			@Override
			public void onResult(DataServiceResult result) {
				if ("0".equals(result.code)) {
					mHandler.sendEmptyMessage(2);
				}
			}

			@Override
			public void onFailure() {

			}
		}, url, this).execute();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		timerTask.cancel();
		timer.cancel();
		timer = null;
	}

	/**
	 * 设置Handler
	 */
	public static void setHandler(Handler handler) {
		mHandler = handler;
	}

}
