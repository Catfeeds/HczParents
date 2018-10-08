package com.android.component.net.tools;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLHandshakeException;

import org.apache.http.NoHttpResponseException;

/**
 * @description
 * 
 * @author 谢志杰 E-mail: xzj2125123@163.com
 * @create 2015-10-20上午10:07:35
 * @version 1.0.0
 * @company 北京易微网络技术有限公司 Copyright: 版权所有 (c) 2014
 */
public class RetryHandler {

	private static final int RETRY_SLEEP_TIME = 1000;// 重连休息时间
	// 白名单，网络出错继续连接
	private static List<Class<?>> whiteList = new ArrayList<Class<?>>();
	// 黑名单，网络出错不重新连接
	private static List<Class<?>> blackList = new ArrayList<Class<?>>();
	// 最多重复连接次数
	private int maxRetries;

	static {
		// 添加白名单
		whiteList.add(NoHttpResponseException.class);
		whiteList.add(UnknownHostException.class);
		whiteList.add(SocketException.class);
		whiteList.add(SocketTimeoutException.class);
		// 添加黑名单
		blackList.add(InterruptedIOException.class);
		blackList.add(SSLHandshakeException.class);
	}

	public RetryHandler(int maxRetries) {
		super();
		this.maxRetries = maxRetries;
	}

	/**
	 * 判断是否需要重连
	 */
	public boolean retryRequest(IOException exception, int executionCount) {
		boolean retry = false;
		// 是否连接成功
		if (executionCount > maxRetries) {
			// 连接次数超过最大重复连接数
			retry = false;
		} else if (blackList.contains(exception.getClass())) {
			// 黑名单，不重连
			retry = false;
		} else if (whiteList.contains(exception.getClass())) {
			// 白名单，重连
			retry = true;
		}
		// 如果重连，则休眠1秒。如果此步不判断则会影响消息上报
		if (retry) {
			try {
				Thread.sleep(RETRY_SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return retry;
	}

}
