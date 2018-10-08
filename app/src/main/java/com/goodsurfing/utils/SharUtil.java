package com.goodsurfing.utils;

import com.goodsurfing.constants.Constants;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;

public class SharUtil {
	public static void saveMode(Context context, int mode) {
		context.getSharedPreferences(Constants.LOGIN_INFO, Activity.MODE_APPEND)
				.edit().putInt(Constants.USER_MODE, mode).commit();
	}

	public static int getMode(Context context) {
		return context.getSharedPreferences(Constants.LOGIN_INFO,
				Activity.MODE_APPEND).getInt(Constants.USER_MODE, 1);
	}

	public static void savePhone(Context context, String phone) {
		context.getSharedPreferences(Constants.LOGIN_INFO, Activity.MODE_APPEND)
				.edit().putString(Constants.LOGIN_MOBEL, phone).commit();
	}
	public static void saveBind(Context context, String phone,boolean bind) {
		context.getSharedPreferences(Constants.LOGIN_INFO, Activity.MODE_APPEND)
		.edit().putBoolean(phone, bind).commit();
	}

	public static boolean getBind(Context context, String phone) {
		return context.getSharedPreferences(Constants.LOGIN_INFO,
				Activity.MODE_APPEND).getBoolean(phone, false);
	}
	public static String getPhone(Context context) {
		return context.getSharedPreferences(Constants.LOGIN_INFO,
				Activity.MODE_APPEND).getString(Constants.LOGIN_MOBEL, "");
	}

	public static void saveCity(Context context, String city) {
		context.getSharedPreferences(Constants.LOGIN_INFO, Activity.MODE_APPEND)
				.edit().putString(Constants.LOGIN_CITY, city).commit();
	}

	public static String getCity(Context context) {
		return context.getSharedPreferences(Constants.LOGIN_INFO,
				Activity.MODE_APPEND).getString(Constants.LOGIN_CITY, "长沙");
	}

	public static void saveService(Context context, String service) {
		context.getSharedPreferences(Constants.LOGIN_INFO, Activity.MODE_APPEND)
				.edit().putString(Constants.LOGIN_SERVICE, service).commit();
	}

	public static String getService(Context context) {
		return context.getSharedPreferences(Constants.LOGIN_INFO,
				Activity.MODE_APPEND).getString(Constants.LOGIN_SERVICE,"");
	}

	public static void saveCacheTime(Context context,String key, String time) {
		if(context==null)return;
		context.getSharedPreferences(Constants.LOGIN_INFO, Activity.MODE_APPEND)
				.edit().putString(key, time).commit();
	}

	public static boolean getCacheTimeOut(Context context,String key, String time) {
		String oldTime = context.getSharedPreferences(Constants.LOGIN_INFO,
				Activity.MODE_APPEND).getString(key, "0");
		long old = Long.valueOf(oldTime);
		long newTime = Long.valueOf(time);
		long cache = 30 * 60 * 1000;
		if ((newTime - old) > cache) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 设置一键锁屏密码
	 * @param context
	 * @param s
	 */
    public static void saveLockScreenPsd(Context context, String s) {
		if(Constants.child ==null)return;
		context.getSharedPreferences(Constants.LOGIN_INFO, Activity.MODE_APPEND)
				.edit().putString(Constants.HCZ_LOCKSCREEN_KEY+Constants.child.getClientDeviceId(), s).commit();
    }

	/**
	 * 获取一键锁屏密码
	 * @param context
	 * @return
	 */
    public static String getLockScreenPsd(Context context){
		if(Constants.child ==null)return "";
		return  context.getSharedPreferences(Constants.LOGIN_INFO,
				Activity.MODE_APPEND).getString(Constants.HCZ_LOCKSCREEN_KEY+Constants.child.getClientDeviceId()+"", "");
	}

	/**
	 * 设置时段 密码
	 * @param context
	 * @param psd
	 */
    public static void saveTimePwds(Context context,String psd) {
		if(Constants.child ==null)return;
		context.getSharedPreferences(Constants.LOGIN_INFO, Activity.MODE_APPEND)
				.edit().putString(Constants.HCZ_LOCKSCREEN_KEY+Constants.child.getClientDeviceId(), psd).commit();
    }

	/**
	 * 获取时段密码
	 * @param context
	 * @return
	 */
	public static String getTimePsd(Context context){
		if(Constants.child ==null)return "";
		return  context.getSharedPreferences(Constants.LOGIN_INFO,
				Activity.MODE_APPEND).getString(Constants.HCZ_LOCKSCREEN_KEY+Constants.child.getClientDeviceId()+"", "");
	}
}
