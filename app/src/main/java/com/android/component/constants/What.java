package com.android.component.constants;

/**
 * @description 线程交互Handler发送的Message的What值
 * 
 * @author 谢志杰 E-mail: xzj2125123@163.com
 * @create 2015-9-23 下午2:56:26
 * @version 1.0.0
 * @company 湖南一石科技有限公司 Copyright: 版权所有 (c) 2016
 */
public interface What {
	/**
	 * 网络断开
	 */
	public static final int NET_WORK_BREAK = 10001;
	/**
	 * 网络连接
	 */
	public static final int NET_WORK_CONNECTED = 10002;
	/**
	 * HOME键按下
	 */
	public static final int HOME_PRESSED = 10003;
	/**
	 * HOME键长按
	 */
	public static final int HOME_LONGPRESSED = 10004;
	/**
	 * http请求网络错误
	 */
	public static final int HTTP_NET_WORK_ERROR = 10005;
	/**
	 * http请求最新数据成功
	 */
	public static final int HTTP_REQUEST_NEW_SUCCESS = 10006;
	/**
	 * http请求最新数据失败
	 */
	public static final int HTTP_REQUEST_NEW_FAILURE = 10007;
	/**
	 * http请求更多数据成功
	 */
	public static final int HTTP_REQUEST_MORE_SUCCESS = 1008;
	/**
	 * http请求更多数据失败
	 */
	public static final int HTTP_REQUEST_MORE_FAILURE = 10009;
	/**
	 * http请求操作数据成功
	 */
	public static final int HTTP_REQUEST_CURD_SUCCESS = 10010;
	/**
	 * http请求操作数据失败
	 */
	public static final int HTTP_REQUEST_CURD_FAILURE = 10011;
}
