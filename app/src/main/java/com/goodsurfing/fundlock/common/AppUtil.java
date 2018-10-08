/**
 * 
 */
package com.goodsurfing.fundlock.common;

import java.util.List;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;
import android.view.WindowManager;

public class AppUtil {
		/**
	     * 获取屏幕分辨率
	     * @param context
	     * @return
	     */
    public static int[] getScreenDispaly(Context context) {
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		int width = windowManager.getDefaultDisplay().getWidth();// 屏幕宽
		int height = windowManager.getDefaultDisplay().getHeight();//屏幕高
		int result[] = { width, height };
		return result;
	}
    
}
