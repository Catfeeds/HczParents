package com.goodsurfing.utils;

import java.lang.reflect.Field;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.BadTokenException;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.goodsurfing.app.R;

/**
 * @ClassName:EventHandler
 * @Description:事件工具
 * @date:2015-5-5 16:55
 */
public class EventHandler extends Handler {
	private static EventHandler instance;
	private static Toast toast;
	private static Dialog dlg;
	private static int dlg_level = -1;

	private EventHandler() {

	}

	public synchronized static EventHandler getInstance() {
		if (instance == null) {
			instance = new EventHandler();
		}
		return instance;
	}

	private static Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				if (dlg != null) {
					try {
						dlg.dismiss();
					} catch (Exception e) {
						e.printStackTrace();
					}
					dlg_level = -1;
					dlg = null;
				}
				break;

			default:
				break;
			}
		}

	};


	public static void allowDialogDismissed(DialogInterface dialog, boolean allowed) {
		try {
			Class<?> c = dialog.getClass().getSuperclass(); // AlertDialog
			if (dialog instanceof ProgressDialog)
				c = c.getSuperclass();

			Field field = c.getDeclaredField("mShowing");
			field.setAccessible(true);
			// 将mShowing变量设为false，表示对话框已关闭
			field.set(dialog, allowed);
			// dialog.dismiss();
			dlg_level = -1;
			dlg = null;
		} catch (Exception e) {
			LogUtil.logError(e);
		}
	}

}