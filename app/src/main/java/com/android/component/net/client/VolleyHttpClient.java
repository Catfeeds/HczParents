package com.android.component.net.client;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;

import com.android.component.net.tools.HttpResponseListener;
import com.android.component.net.tools.MapRequest;
import com.android.component.utils.FileUtil;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.goodsurfing.app.HaoUpApplication;

/**
 * 
 * @author 谢志杰
 * @create 2016-10-9 下午2:07:38
 * @version 1.0.0
 * @company 湖南一石科技有限公司 Copyright: 版权所有（c）2016
 */
public class VolleyHttpClient extends IHttpClient {
	/**
	 * 请求连接对象
	 */
	private RequestQueue mRequestQueue;
	/**
	 * 请求参数
	 */
	private MapRequest mParams;

	public VolleyHttpClient(Context context) {
		this(context, RequestMethod.POST);
	}

	public VolleyHttpClient(Context context, HttpResponseListener responseListener) {
		this(context, RequestMethod.POST, responseListener);
	}

	public VolleyHttpClient(Context context, RequestMethod requestMethod) {
		this(context, requestMethod, null);
	}

	public VolleyHttpClient(Context context, RequestMethod requestMethod, HttpResponseListener responseListener) {
		this.mMethod = requestMethod;
		this.mListener = responseListener;
		if (HaoUpApplication.getInstance().mRequestQueue != null) {
			mRequestQueue = HaoUpApplication.getInstance().mRequestQueue;
		} else {
			mRequestQueue = Volley.newRequestQueueInDisk(context, FileUtil.getFileCacheDir(context).getAbsolutePath(), null);
		}
	}

	@Override
	public void sendRequest() {
		// 设置超时时间
		mParams.setRetryPolicy(new DefaultRetryPolicy(outTime, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		mRequestQueue.add(mParams);
	}

	@Override
	protected void initParams(Map<String, Object> requestParams) {
		for (Entry<String, Object> entry : requestParams.entrySet()) {
			if (entry.getValue() == null) {
				requestParams.put(entry.getKey(), "");
			} else if (entry.getValue() instanceof File) {
				requestParams.put(entry.getKey(), (File) entry.getValue());
			} else {
				requestParams.put(entry.getKey(), entry.getValue().toString());
			}
		}
		Listener<String> listener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				if (mListener != null)
					mListener.onSuccess(response);
			}
		};
		ErrorListener errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (mListener != null)
					mListener.onFailure(error.toString());
			}
		};
		mParams = new MapRequest(mUrl, requestParams, mMethod.getMethod(), listener, errorListener);
	}
}
