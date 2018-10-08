package com.android.component.net.client;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.android.component.net.tools.ByteHttpRequest;
import com.android.component.net.tools.ByteHttpResponseHandler;
import com.android.component.net.tools.RequestParams;

/**
 * @description
 * 
 * @author 谢志杰 E-mail: xzj2125123@163.com
 * @create 2015-10-20下午12:03:07
 * @version 1.0.0
 * @company 北京易微网络技术有限公司 Copyright: 版权所有 (c) 2014
 */
public class ByteHttpClient extends IHttpClient {

	private static ThreadPoolExecutor mThreadPool = (ThreadPoolExecutor) Executors
			.newCachedThreadPool();

	private ByteHttpRequest mRequest;

	private RequestParams mParams;

	public ByteHttpClient() {
		mRequest = new ByteHttpRequest();
	}

	@Override
	protected void sendRequest() {
		mRequest.setUrl(mUrl);
		mRequest.setParams(mParams);
		mRequest.setTimeOut(outTime);
		mRequest.setByteHttpResponseHandler(mHandler);
		mRequest.setByteResponse(isByteResponse);
		mThreadPool.execute(mRequest);
	}

	@Override
	protected void initParams(Map<String, Object> requestParams) {
		mParams = new RequestParams();
		Iterator<Entry<String, Object>> it = requestParams.entrySet()
				.iterator();
		while (it.hasNext()) {
			mParams.put(it.next().getValue());
		}
	}

	private ByteHttpResponseHandler mHandler = new ByteHttpResponseHandler() {
		protected void onSuccess(String response) {
			if (mListener != null)
				mListener.onSuccess(response);
		};

		protected void onSuccess(byte[] response) {
			if (mListener != null)
				mListener.onSuccess(response);
		};

		protected void onFailure(String error) {
			if (mListener != null)
				mListener.onFailure(error);
		};
	};
}
