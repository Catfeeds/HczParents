package com.android.component.net.client;

import java.util.Map;

import android.content.Context;

import com.android.component.net.tools.HttpResponseListener;

/**
 * @description 网络请求基类
 * 
 * @author 谢志杰 E-mail: xzj2125123@163.com
 * @create 2015-6-30 下午1:35:40
 * @version 1.0.0
 * @company 湖南一石科技有限公司 Copyright: 版权所有 (c) 2016
 */
public abstract class IHttpClient {
	/**
	 * 请求超时时间
	 */
	protected int outTime = 15 * 1000;
	/**
	 * 请求方式,默认post
	 */
	protected RequestMethod mMethod;
	/**
	 * 请求Url
	 */
	protected String mUrl;
	/**
	 * 请求结果回调
	 */
	protected HttpResponseListener mListener;

	protected boolean isByteResponse = false;

	/**
	 * 发送请求
	 * 
	 * @author 谢志杰
	 * @create 2015-6-30 下午3:07:55
	 * @param requestUrl
	 * @param params
	 */
	protected abstract void sendRequest();

	/**
	 * 发送请求
	 * 
	 * @author 谢志杰
	 * @create 2015-6-30 下午3:07:55
	 * @param requestUrl
	 * @param params
	 */
	public void sendRequest(String requestUrl, Map<String, Object> requestParams) {
		this.mUrl = requestUrl;
		initParams(requestParams);
		sendRequest();
	}

	/**
	 * 初始化请求参数
	 * 
	 * @author 谢志杰
	 * @create 2015-6-30 下午3:07:47
	 * @param params
	 */
	protected abstract void initParams(Map<String, Object> requestParams);

	public void setTimeOut(int outTime) {
		this.outTime = outTime;
	}

	public void setMethod(RequestMethod requestMethod) {
		this.mMethod = requestMethod;
	}

	public void setListener(HttpResponseListener mListener) {
		this.mListener = mListener;
	}

	public enum ClientType {
		Volley,  ByteHttpClient;

		private IHttpClient mHttpClient;

		public IHttpClient getClient(Context context) {
			switch (this) {
			case Volley:
				mHttpClient = new VolleyHttpClient(context);
				break;
			case ByteHttpClient:
				mHttpClient = new ByteHttpClient();
				break;
			}
			return mHttpClient;
		}
	}

	public void setByteResponse(boolean isByteResponse) {
		this.isByteResponse = isByteResponse;
	}

	public enum RequestMethod {

		GET(0), POST(1);

		private int method;

		private RequestMethod(int method) {
			this.method = method;
		}

		public int getMethod() {
			return method;
		}
	}
}
