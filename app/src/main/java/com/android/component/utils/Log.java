package com.android.component.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import android.text.TextUtils;

/**
 * @description 日志打印操作
 * 
 * @author 谢志杰 E-mail: xzj2125123@163.com
 * @create 2016-8-21 上午09:32:30
 * @version 1.0.0
 * @company 湖南一石科技有限公司 Copyright: 版权所有 (c) 2016
 */
public class Log {
	/** 是否启用日志 ,默认为启用 */
	private static boolean enable = true;
	/** 是否打印日志到手机 ,默认为关闭 */
	private static boolean saveLocal = false;
	/**
	 * 日志打印级别,默认为INFO
	 */
	private static int level = Level.INFO.getLevel();

	/**
	 * 
	 * 日志常用级别枚举类
	 */
	public enum Level {
		DEBUG(3), INFO(2), WARN(1), ERROR(0);

		private int level;

		private Level(int level) {
			this.level = level;
		}

		public int getLevel() {
			return level;
		}
	}

	/**
	 * 记录日志
	 * 
	 * @param level
	 *            Level.DEBUG,Level.INFO,Level.WARN,Level.ERROR
	 * 
	 * @param text
	 */
	public static void log(Level level, String tag, String text) {
		if (StringUtil.isEmpty(tag))
			tag = "LOG";
		if (enable) {
			if (!TextUtils.isEmpty(text)) {
				switch (level) {
				case DEBUG:
					if (Log.level <= Level.DEBUG.getLevel()) {
						android.util.Log.d(tag, text);
					}
					break;
				case INFO:
					if (Log.level <= Level.INFO.getLevel()) {
						android.util.Log.i(tag, text);
						saveLogToLocal(text);
					}
					break;
				case WARN:
					if (Log.level <= Level.WARN.getLevel()) {
						android.util.Log.w(tag, text);
						saveLogToLocal(text);
					}
					break;
				case ERROR:
					android.util.Log.e(tag, text);
					saveLogToLocal(text);
					break;
				}
			}
		}
	}

	/**
	 * 打印debug信息
	 * 
	 * @param text
	 */
	public static void d(String text) {
		log(Level.DEBUG, null, text);
	}

	/**
	 * 根据tag打印debug信息
	 * 
	 * @param tag
	 * @param text
	 */
	public static void d(String tag, String text) {
		log(Level.DEBUG, tag, text);
	}

	/**
	 * 根据tag打印info信息
	 * 
	 * @param tag
	 * @param text
	 */
	public static void i(String tag, String text) {
		log(Level.INFO, tag, text);
	}

	/**
	 * 打印info信息
	 * 
	 * @param text
	 */
	public static void i(String text) {
		log(Level.INFO, null, text);
	}

	/**
	 * 根据tag打印warn信息
	 * 
	 * @param tag
	 * @param text
	 */
	public static void w(String tag, String text) {
		log(Level.WARN, tag, text);
	}

	/**
	 * 打印warn信息
	 * 
	 * @param text
	 */
	public static void w(String text) {
		log(Level.WARN, null, text);
	}

	/**
	 * 打印error信息
	 * 
	 * @param text
	 */
	public static void e(String text) {
		log(Level.ERROR, null, text);
	}

	/**
	 * 根据tag打印error信息
	 * 
	 * @param tag
	 * @param text
	 */
	public static void e(String tag, String text) {
		log(Level.ERROR, tag, text);
	}

	/**
	 * 设置是否启用日志
	 * 
	 * @param enable
	 */
	public static void setEnable(boolean enable) {
		Log.enable = enable;
	}

	/**
	 * 是否启用打印日志到手机
	 * 
	 * @param saveLocal
	 */
	public static void setSaveLocal(boolean saveLocal) {
		Log.saveLocal = saveLocal;
	}

	/**
	 * 设置日志启动级别
	 * 
	 * @param level
	 *            Level.DEBUG;Level.INFO; Level.WARN;Level.ERROR; 级别由低到高
	 * 
	 */
	public static void setLevel(Level level) {
		Log.enable = true;
		Log.level = level.getLevel();
	}

	/**
	 * 将LOG打印到手机
	 * 
	 * @param e
	 */
	public static void saveLogToLocal(final String log) {
		new Thread() {
			public void run() {
				File saveDir = FileUtil.getSDCard();
				if (saveDir != null && saveLocal) {
					FileWriter fos = null;
					File saveFile = new File(saveDir, "log.txt");
					try {
						fos = new FileWriter(saveFile, true);
						fos.write(log + "\n");
						fos.flush();
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					} finally {
						if (fos != null)
							try {
								fos.close();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
					}

				}
			};
		}.start();

	}

}
