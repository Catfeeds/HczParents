package com.android.component.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * 
 * @author 谢志杰 
 * @create 2016-10-9 下午2:14:51 
 * @version 1.0.0
 * @company 湖南一石科技有限公司 Copyright: 版权所有（c）2016
 */
public class TimeUtil {
	/**
	 * 获取时间段 凌晨0:00-5:59 |上午6:00-11:59 | 中午12:00-12:59 |下午13:00-17:59
	 * |晚上18:00-23:59
	 * 
	 * @param time
	 * @return
	 */
	public static String getTimePeriod(long milliseconds) {
		Calendar mCalendar = Calendar.getInstance(Locale.CHINA);
		mCalendar.setTimeInMillis(milliseconds);
		int hours = mCalendar.get(Calendar.HOUR);
		int minutes = mCalendar.get(Calendar.MINUTE);
		int seconds = mCalendar.get(Calendar.SECOND);
		long currTime = hours * 3600 + minutes * 60 + seconds;
		long wee = 6 * 3600;// 凌晨0:00-5:59
		long forenoon = 12 * 3600;// 上午6:00-11:59
		long midday = 13 * 3600;// 中午12:00-12:59
		long afternoon = 18 * 3600;// 下午13:00-17:59
		// long evening = 24 * 3600;// 晚上18:00-23:59
		if (currTime < wee) {
			return "凌晨";
		} else if (currTime < forenoon) {
			return "上午";
		} else if (currTime < midday) {
			return "中午";
		} else if (currTime < afternoon) {
			return "下午";
		}
		return "晚上";
	}

	/**
	 * 获取当前格式化日期及时间
	 * 
	 * @author 谢志杰
	 * @create 2015-9-23 下午3:50:48
	 * @param format
	 * @return
	 */
	public static String getDateTime(String format) {
		return new SimpleDateFormat(format, Locale.CHINA).format(System
				.currentTimeMillis());
	}

	/**
	 * 获取相应时间的格式化日期或时间
	 * 
	 * @author 谢志杰
	 * @create 2015-9-23 下午3:50:36
	 * @param milliseconds
	 * @param format
	 * @return
	 */
	public static String getDateTime(long milliseconds, String format) {
		return new SimpleDateFormat(format, Locale.CHINA).format(milliseconds);
	}

	/**
	 * 获取星期,格式:星期一,星期二,星期三,星期四,星期五,星期六,星期日
	 * 
	 * @author 谢志杰
	 * @create 2015-9-23 下午3:41:39
	 * @param milliseconds
	 * @return
	 */
	public static String getWeekOne(long milliseconds) {
		Calendar mCalendar = Calendar.getInstance(Locale.CHINA);
		mCalendar.setTimeInMillis(milliseconds);
		String week = "";
		int dayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK);
		switch (dayOfWeek) {
		case Calendar.SUNDAY:
			week = "星期日";
			break;
		case Calendar.MONDAY:
			week = "星期一";
			break;
		case Calendar.THURSDAY:
			week = "星期二";
			break;
		case Calendar.WEDNESDAY:
			week = "星期三";
			break;
		case Calendar.TUESDAY:
			week = "星期四";
			break;
		case Calendar.FRIDAY:
			week = "星期五";
			break;
		case Calendar.SATURDAY:
			week = "星期六";
			break;
		default:
			break;
		}
		return week;
	}

	/**
	 * 获取星期,格式:周一,周二,周三,周四,周五,周六,周日
	 * 
	 * @author 谢志杰
	 * @create 2015-9-23 下午3:41:39
	 * @param milliseconds
	 * @return
	 */
	public static String getWeekTwo(long milliseconds) {
		Calendar mCalendar = Calendar.getInstance(Locale.CHINA);
		mCalendar.setTimeInMillis(milliseconds);
		String week = "";
		int dayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK);
		switch (dayOfWeek) {
		case Calendar.SUNDAY:
			week = "周日";
			break;
		case Calendar.MONDAY:
			week = "周一";
			break;
		case Calendar.THURSDAY:
			week = "周二";
			break;
		case Calendar.WEDNESDAY:
			week = "周三";
			break;
		case Calendar.TUESDAY:
			week = "周四";
			break;
		case Calendar.FRIDAY:
			week = "周五";
			break;
		case Calendar.SATURDAY:
			week = "周六";
			break;
		default:
			break;
		}
		return week;
	}

	/**
	 * 格式化时间转换成毫秒
	 * 
	 * @author 谢志杰
	 * @create 2015-9-23 下午3:48:56
	 * @param formatTime
	 * @param format
	 * @return
	 */
	public static long dateToTime(String formatTime, String format) {
		try {
			SimpleDateFormat formatObj = new SimpleDateFormat(format,
					Locale.CHINA);
			return formatObj.parse(formatTime).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
