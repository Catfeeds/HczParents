package com.android.component.constants;

import android.content.Intent;
import android.net.ConnectivityManager;

/**
 * @description 广播发送动作值
 * 
 * @author 谢志杰 E-mail: xzj2125123@163.com
 * @create 2015-9-23 下午2:57:25
 * @version 1.0.0
 * @company 湖南一石科技有限公司 Copyright: 版权所有 (c) 2016
 */
public class Action {
	/**
	 * 系统网络状态改变
	 */
	public static final String NET_WORK_CONNECTIVITY_CHANGE = ConnectivityManager.CONNECTIVITY_ACTION;
	/**
	 * Home键监听
	 */
	public static final String CLOSE_APPLICATION = Intent.ACTION_CLOSE_SYSTEM_DIALOGS;

}
