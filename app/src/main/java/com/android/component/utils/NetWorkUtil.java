package com.android.component.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

/**
 * @description 网络操作工具类
 * 
 * @author 谢志杰 E-mail: xzj2125123@163.com
 * @create 2016-8-18 上午09:22:30
 * @version 1.0.0
 * @company 湖南一石科技有限公司 Copyright: 版权所有 (c) 2016
 */
public class NetWorkUtil {
	/**
	 * 判断网络是否连接
	 * 
	 * @param context
	 * @return true wifi连接或数据连接。false 无网络连接
	 */
	public static boolean hasNetWork(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo mMobile = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if ((mWifi != null && State.CONNECTED == mWifi.getState())
				|| (mMobile != null && State.CONNECTED == mMobile.getState())) {
			return true;
		}
		return false;
	}

	/**
	 * 是否有wifi连接
	 * 
	 * @param context
	 * @return
	 * @author 谢志杰
	 * @time 2014-11-11下午03:53:11
	 */
	public static boolean hasWifi(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (mWifi != null && State.CONNECTED == mWifi.getState()) {
			return true;
		}
		return false;
	}
}
