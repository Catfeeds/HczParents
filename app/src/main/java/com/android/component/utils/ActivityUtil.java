package com.android.component.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.android.component.constants.Constants;

/**
 * @description Activity相关操作 跳转,分辨率,版本号,版本名,设备token,dip转px,系统SDK版本
 * 
 * @author 谢志杰 E-mail: xzj2125123@163.com
 * @create 2016-8-21 上午11:40:27
 * @version 1.0.0
 * @company 湖南一石科技有限公司 Copyright: 版权所有 (c) 2016
 */
public class ActivityUtil {

	public static final String TAG = "ActivityUtil";

	private static DisplayMetrics mDisplayMetrics;

	/**
	 * 获取全局SharedPreferences
	 * 
	 * @author 谢志杰
	 * @create 2015-7-9 下午5:29:07
	 * @param context
	 * @return
	 */
	public static SharedPreferences getSharedPreferences(Context context) {
		return context.getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE);
	}

	/**
	 * 启动activity-standard
	 * 
	 * @param context
	 * @param clazz
	 */
	public static void startActivity(Context context, Class<? extends Activity> clazz) {
		Intent intent = new Intent(context, clazz);
		context.startActivity(intent);
	}

	/**
	 * 启动activity-自定义启动方式
	 * 
	 * @param context
	 * @param clazz
	 */
	public static void startActivity(Context context, Class<? extends Activity> clazz, int flags) {
		Intent intent = new Intent(context, clazz);
		intent.setFlags(flags);
		context.startActivity(intent);
	}

	/**
	 * 启动activity并传值
	 * 
	 * @param context
	 * @param clazz
	 * @param data
	 */
	public static void startActivity(Context context, Class<? extends Activity> clazz, Bundle data) {
		Intent intent = new Intent(context, clazz);
		if (data != null && data.size() > 0)
			intent.putExtras(data);
		context.startActivity(intent);
	}

	/**
	 * 启动activity并传值-standard
	 * 
	 * @param context
	 * @param clazz
	 * @param data
	 */
	public static void startActivity(Context context, Class<? extends Activity> clazz, Bundle data, int flags) {
		Intent intent = new Intent(context, clazz);
		intent.setFlags(flags);
		if (data != null && data.size() > 0)
			intent.putExtras(data);
		context.startActivity(intent);
	}

	/**
	 * 启动activityForResult-standard
	 * 
	 * @param context
	 * @param clazz
	 */
	public static void startActivityForResult(Context context, Class<? extends Activity> clazz, int requestCode) {
		Intent intent = new Intent(context, clazz);
		((Activity) context).startActivityForResult(intent, requestCode);
	}

	/**
	 * 启动activity-standard
	 * 
	 * @param context
	 * @param clazz
	 */
	public static void startActivityForResult(Context context, Class<? extends Activity> clazz, int requestCode, Bundle data) {
		Intent intent = new Intent(context, clazz);
		if (data != null && data.size() > 0) {
			intent.putExtras(data);
		}
		((Activity) context).startActivityForResult(intent, requestCode);
	}

	/**
	 * 获取手机分辨率
	 * 
	 * @return DisplayMetrics
	 */
	public static DisplayMetrics getScreenPixel(Context context) {
		if (mDisplayMetrics == null) {
			mDisplayMetrics = new DisplayMetrics();
			((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(mDisplayMetrics);
		}
		Log.e("DisplayMetrics", "分辨率：" + mDisplayMetrics.widthPixels + "*" + mDisplayMetrics.heightPixels + ",精度：" + mDisplayMetrics.density + ",densityDpi=" + mDisplayMetrics.densityDpi);
		return mDisplayMetrics;
	}

	/**
	 * 获取当前应用版本序号
	 * 
	 * @param context
	 * @return 当前应用版本序号
	 */
	public static int getVersionCode(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * 获取当前应用版本名
	 * 
	 * @param context
	 * @return 当前应用版本名
	 */
	public static String getVersionName(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 获取当前应用包名
	 * 
	 * @param context
	 * @return 当前应用包名
	 */
	public static String getPackegeName(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.packageName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取手机系统版本
	 * 
	 * @return
	 */
	public static int getAndroidSDKVersion() {
		int version;
		try {
			version = android.os.Build.VERSION.SDK_INT;
		} catch (NumberFormatException e) {
			return 0;
		}
		return version;
	}

	/**
	 * dp转px
	 * 
	 * @param context
	 * @param dpValue
	 *            dp值
	 * @return px
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 关闭软键盘
	 */
	public static void closeKeyboard(Context context) {
		InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		View view = ((Activity) context).getCurrentFocus();
		if (view != null) {
			IBinder binder = view.getApplicationWindowToken();
			if (binder != null && im != null && view != null) {
				im.hideSoftInputFromWindow(binder, InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
	}

	/**
	 * 打开软键盘
	 * 
	 * @param editText
	 * @author 谢志杰
	 * @time 2014-12-10下午02:38:54
	 */
	public static void openKeyboard(EditText editText) {
		if (editText != null) {
			editText.setFocusable(true);
			editText.setFocusableInTouchMode(true);
			editText.requestFocus();
			InputMethodManager inputManager = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.showSoftInput(editText, 0);
		}
	}

	/**
	 * 切换软键盘
	 * 
	 * @param context
	 * @return
	 * @author 谢志杰
	 * @time 2014-12-10下午02:45:44
	 */
	public static void toggleKeyboard(Context context) {
		InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * 获取设备token
	 * 
	 * @param tm
	 * @return
	 */
	public static String getDeviceToken(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String tokenId = tm.getDeviceId();

		if (StringUtil.isEmpty(tokenId)) {
			tokenId = tm.getSubscriberId();
		}
		if (tokenId != null && (tokenId.contains("000000") || tokenId.contains("111111") || tokenId.contains("222222") || tokenId.contains("333333") || tokenId.contains("444444") || tokenId.contains("555555") || tokenId.contains("666666") || tokenId.contains("777777") || tokenId.contains("888888") || tokenId.contains("999999"))) {
			BluetoothAdapter m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			tokenId = m_BluetoothAdapter.getAddress().replace(":", "YB");
		}
		return tokenId;
	}

	public static String getMacId() {
		String macSerial = "";
		String str = "";
		try {
			Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address");
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);

			for (; null != str;) {
				str = input.readLine();
				if (str != null) {
					macSerial = str.trim();// 去空格
					break;
				}
			}
		} catch (IOException ex) {
			// 赋予默认值
			ex.printStackTrace();
		}
		return macSerial;
	}

	public static String getBloothMac(Context context) {
		if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			return "";
		}
		// 检查设备是否支持蓝牙
		final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(context.BLUETOOTH_SERVICE);
		BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			// 设备不支持蓝牙
			return "";
		}
		String macString;
		// 打开蓝牙
//		if (!mBluetoothAdapter.isEnabled()) {
//			mBluetoothAdapter.enable();
//			macString = mBluetoothAdapter.getAddress();
//			mBluetoothAdapter.disable();
//		} else {
//			macString = mBluetoothAdapter.getAddress();
//		}
		macString = mBluetoothAdapter.getAddress();
		return macString;
	}

	/**
	 * 拨打电话
	 * 
	 * @author 谢志杰
	 * @create 2015-7-24 下午5:05:10
	 * @param context
	 * @param phoneNumber
	 */
	public static void dialPhone(Context context, String phoneNumber) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.DIAL");
		intent.setData(Uri.parse("tel:" + phoneNumber));
		context.startActivity(intent);
	}

	// Android获取一个用于打开APK文件的intent
	public static Intent getApkFileIntent(String param) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/vnd.android.package-archive");
		return intent;
	}

	// Android获取一个用于打开VIDEO文件的intent
	public static Intent getVideoFileIntent(String param) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("oneshot", 0);
		intent.putExtra("configchange", 0);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "video/*");
		return intent;
	}

	// Android获取一个用于打开AUDIO文件的intent
	public static Intent getAudioFileIntent(String param) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("oneshot", 0);
		intent.putExtra("configchange", 0);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "audio/*");
		return intent;
	}

	// Android获取一个用于打开Html文件的intent
	public static Intent getHtmlFileIntent(String param) {
		Uri uri = Uri.parse(param).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content").encodedPath(param).build();
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.setDataAndType(uri, "text/html");
		return intent;
	}

	// Android获取一个用于打开图片文件的intent
	public static Intent getImageFileIntent(String param) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "image/*");
		return intent;
	}

	// Android获取一个用于打开PPT文件的intent
	public static Intent getPptFileIntent(String param) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
		return intent;
	}

	// Android获取一个用于打开Excel文件的intent
	public static Intent getExcelFileIntent(String param) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/vnd.ms-excel");
		return intent;
	}

	// Android获取一个用于打开Word文件的intent
	public static Intent getWordFileIntent(String param) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/msword");
		return intent;
	}

	// Android获取一个用于打开CHM文件的intent
	public static Intent getChmFileIntent(String param) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/x-chm");
		return intent;
	}

	// Android获取一个用于打开文本文件的intent
	public static Intent getTextFileIntent(String param, boolean paramBoolean) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (paramBoolean) {
			Uri uri1 = Uri.parse(param);
			intent.setDataAndType(uri1, "text/plain");
		} else {
			Uri uri2 = Uri.fromFile(new File(param));
			intent.setDataAndType(uri2, "text/plain");
		}
		return intent;
	}

	/**
	 * 密码为6-16位非纯数字类型
	 * 
	 * @param mobile
	 * @return
	 */
	public static boolean isPass(String pass) {
		return pass.matches("^(?![0-9]+$)(?![a-zA-Z]+$)(?![@#$%`!^&*_]+$)[0-9A-Za-z@#$%`!^&*_]{6,16}$");
	}

	// Android获取一个用于打开PDF文件的intent
	public static Intent getPdfFileIntent(String param) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/pdf");
		return intent;
	}

	/**
	 * 手机标题栏高度
	 * 
	 * @description
	 * 
	 * @return
	 * 
	 * @author 谢志杰
	 * @create 2015年6月12日 上午11:12:30
	 * 
	 */
	public static int getStatusBarHeight(Context context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}
}
