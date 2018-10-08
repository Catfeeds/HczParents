package com.android.component.net.parse;

/**
 * @description
 * 
 * @author 谢志杰 E-mail: xzj2125123@163.com
 * @create 2015-10-18上午11:08:23
 * @version 1.0.0
 * @company 北京易微网络技术有限公司 Copyright: 版权所有 (c) 2014
 */
public class JSONKey {
	/**
	 * 网络请求返回状态名
	 */
	public static String STATE = "status";
	/**
	 * 网络请求返回内容名
	 */
	public static String DATA = "data";
	/**
	 * 网络请求错误返回内容名
	 */
	public static String ERROR_MESSAGE = "msg";
	/**
	 * 网络请求成功状态值
	 */
	public static int STATE_SUCCESS = 1;
	/**
	 * 其他设备登录
	 */
	public static int STATE_OTHERLOGIN =2;
	/**
	 * 房间失效
	 */
	public static int STATE_ROOM_OVER =3;
	/**
	 * 网络请求错误状态值
	 */
	public static int STATE_FAILURE = 0;

	public static void init(String state, String errorMsg, String data,
			int success, int failure) {
		JSONKey.STATE = state;
		JSONKey.DATA = data;
		JSONKey.ERROR_MESSAGE = errorMsg;
		JSONKey.STATE_SUCCESS = success;
		JSONKey.STATE_FAILURE = failure;
	}
}
