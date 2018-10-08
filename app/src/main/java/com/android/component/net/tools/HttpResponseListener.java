package com.android.component.net.tools;

/**
 * @description
 * 
 * @author 谢志杰 E-mail: xzj2125123@163.com
 * @create 2015-6-30 下午2:25:08
 * @version 1.0.0
 * @company 湖南一石科技有限公司 Copyright: 版权所有 (c) 2016
 */
public interface HttpResponseListener {

	public void onSuccess(String response);
	
	public void onSuccess(byte[] response);

	public void onFailure(String error);
}
