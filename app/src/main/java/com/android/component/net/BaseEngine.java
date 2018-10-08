package com.android.component.net;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.component.constants.What;
import com.android.component.net.client.IHttpClient;
import com.android.component.net.client.IHttpClient.ClientType;
import com.android.component.net.client.IHttpClient.RequestMethod;
import com.android.component.net.parse.JSONKey;
import com.android.component.net.tools.HttpResponseListener;
import com.android.component.utils.StringUtil;

/**
 * 
 * @author 谢志杰
 * @create 2016-10-9 下午2:07:59
 * @version 1.0.0
 * @company 湖南一石科技有限公司 Copyright: 版权所有（c）2016
 */
public abstract class BaseEngine {

	protected Context mContext;
	/**
	 * 服务器IP
	 */
	protected String mServerPath;
	/**
	 * 发送请求的URL地址
	 */
	protected String mRequestUrl;
	/**
	 * 请求超时时间
	 */
	protected int outTime = 30 * 1000;
	/**
	 * 发送请求HttpClient
	 */
	protected IHttpClient mHttpClient;
	/**
	 * 请求方式,post或get
	 */
	protected RequestMethod mMethod;
	/**
	 * 请求方式Volley或AsyncHttpClient或ByteHttpClient
	 */
	protected ClientType mClientType;
	/**
	 * 请求参数
	 */
	protected Map<String, Object> mParams;
	/**
	 * 请求结果回调
	 */
	protected HttpResponseListener mResponseListener;

	protected Handler mHandler;

	protected boolean isByteResponse;

	/**
	 * 使用volley的post方式发送请求
	 * 
	 * @param context
	 *            接口基本路径
	 */
	public BaseEngine(Context context, Handler handler) {
		this(context, null, handler);
	}

	/**
	 * 使用volley的post方式发送请求
	 * 
	 * @param context
	 * @param serverPath
	 *            接口基本路径
	 */
	public BaseEngine(Context context, String serverPath, Handler handler) {
		this(context, serverPath, null, handler);
	}

	/**
	 * 使用volley的post方式发送请求
	 * 
	 * @param context
	 * @param serverPath
	 *            接口基本路径
	 * @param requestUrl
	 *            接口请求地址
	 */
	public BaseEngine(Context context, String serverPath, String requestUrl, Handler handler) {
		this(context, serverPath, requestUrl, ClientType.Volley, RequestMethod.POST, handler);
	}

	/**
	 * 
	 * @param context
	 * @param serverPath
	 *            接口基本路径
	 * @param requestUrl
	 *            接口请求地址
	 * @param clientType
	 *            使用Volley或AsyncHttpClient进行请求
	 * @param rquestMethod
	 *            使用post或get方式请求
	 */
	public BaseEngine(Context context, String serverPath, String requestUrl, ClientType clientType, RequestMethod rquestMethod, Handler handler) {
		this.mContext = context;
		this.mServerPath = serverPath;
		this.mRequestUrl = requestUrl;
		this.mMethod = rquestMethod;
		this.mParams = new LinkedHashMap<String, Object>();
		this.mClientType = clientType;
		this.mHandler = handler;
		this.mResponseListener = new HttpResponseListener() {
			@Override
			public void onSuccess(String response) {
				Log.i(mRequestUrl, response);
				onSuccess(response);
			}

			@Override
			public void onFailure(String error) {
				Log.i(mRequestUrl, error);
				onFailure(error);
			}
			@Override
			public void onSuccess(byte[] response) {
				BaseEngine.this.onSuccess(response);
			};
		};
		if (mMethod == null)
			mMethod = RequestMethod.POST;
		if (mClientType == null)
			mClientType = ClientType.Volley;
		initClient();
	}

	/**
	 * 初始化httpClient
	 * 
	 * @create 2015-6-30 下午4:29:34
	 */
	private void initClient() {
		mHttpClient = mClientType.getClient(mContext);
		mHttpClient.setListener(mResponseListener);
		mHttpClient.setMethod(mMethod);
		mHttpClient.setByteResponse(isByteResponse);
		mHttpClient.setTimeOut(outTime);
	}

	/**
	 * 发送请求
	 * 
	 * @create 2015-6-30 下午4:22:22
	 */
	public void sendRequest() {
		mParams.clear();
		setParams();
		String requestUrl = mServerPath + mRequestUrl;
		Set<Entry<String, Object>> set = mParams.entrySet();
		StringBuffer sb = new StringBuffer();
		for (Entry<String, Object> entry : set) {
			sb.append(entry.getKey()).append("=").append(entry.getValue() == null ? "" : entry.getValue()).append("&");
		}
		if (StringUtil.isEmpty(requestUrl)) {
			onFailure("请求Url错误:" + requestUrl);
		} else {
			mHttpClient.sendRequest(requestUrl, mParams);
		}
	}

	/**
	 * 添加传递参数
	 * 
	 * @author 谢志杰
	 * @create 2015-7-7 下午1:32:18
	 * @param key
	 * @param value
	 */
	protected void put(String key, Object value) {
		mParams.put(key, value);
	}

	public void setServerPath(String mServerPath) {
		this.mServerPath = mServerPath;
	}

	/**
	 * 发送http请求操作数据成功通知
	 * 
	 * @author 谢志杰
	 * @time 2014-10-20下午04:12:03
	 */
	protected void sendCURDSuccess() {
		sendEmptyMessage(What.HTTP_REQUEST_CURD_SUCCESS);
	}

	/**
	 * 发送http请求操作数据失败通知
	 * 
	 * @param result
	 * 
	 * @author 谢志杰
	 * @time 2014-10-20下午04:12:03
	 */
	protected void sendCURDFailure(Object result) {
		try {
			String errorMsg = ((NetResult) result).getErrorMsg();
			if (StringUtil.isEmpty(errorMsg)) {
				sendEmptyMessage(What.HTTP_REQUEST_CURD_FAILURE);
			} else {
				sendMessage(errorMsg, What.HTTP_REQUEST_CURD_FAILURE);
			}
		} catch (Exception e) {
			sendMessage(result, What.HTTP_REQUEST_CURD_FAILURE);
		}
	}

	/**
	 * 发送http请求操作数据成功通知
	 * 
	 * @param result
	 * 
	 * @author 谢志杰
	 * @time 2014-10-20下午04:12:03
	 */
	protected void sendCURDSuccess(Object result) {
		sendMessage(result, What.HTTP_REQUEST_CURD_SUCCESS);
	}

	/**
	 * 发送http请求最新数据失败通知
	 * 
	 * @param result
	 * 
	 * @author 谢志杰
	 * @time 2014-10-20下午04:12:03
	 */
	protected void sendNewFailure(Object result) {
		try {
			String errorMsg = ((NetResult) result).getErrorMsg();
			if (StringUtil.isEmpty(errorMsg)) {
				sendEmptyMessage(What.HTTP_REQUEST_NEW_FAILURE);
			} else {
				sendMessage(errorMsg, What.HTTP_REQUEST_NEW_FAILURE);
			}
		} catch (Exception e) {
			sendMessage(result, What.HTTP_REQUEST_NEW_FAILURE);
		}
	}

	/**
	 * 发送http请求最新数据成功通知
	 * 
	 * @param result
	 * 
	 * @author 谢志杰
	 * @time 2014-10-20下午04:12:03
	 */
	protected void sendNewSuccess(Object result) {
		sendMessage(result, What.HTTP_REQUEST_NEW_SUCCESS);
	}

	/**
	 * 发送http请求更多数据失败通知
	 * 
	 * @param result
	 * 
	 * @author 谢志杰
	 * @time 2014-10-20下午04:12:03
	 */
	protected void sendMoreFailure(Object result) {
		try {
			String errorMsg = ((NetResult) result).getErrorMsg();
			if (StringUtil.isEmpty(errorMsg)) {
				sendEmptyMessage(What.HTTP_REQUEST_MORE_FAILURE);
			} else {
				sendMessage(errorMsg, What.HTTP_REQUEST_MORE_FAILURE);
			}
		} catch (Exception e) {
			sendMessage(result, What.HTTP_REQUEST_MORE_FAILURE);
		}
	}

	/**
	 * 发送http请求更多数据成功通知
	 * 
	 * @param result
	 * 
	 * @author 谢志杰
	 * @time 2014-10-20下午04:12:03
	 */
	protected void sendMoreSuccess(Object result) {
		sendMessage(result, What.HTTP_REQUEST_MORE_SUCCESS);
	}

	protected void sendFailure(Object result, int what) {
		try {
			String errorMsg = ((NetResult) result).getErrorMsg();
			if (StringUtil.isEmpty(errorMsg)) {
				sendEmptyMessage(what);
			} else {
				sendMessage(errorMsg, what);
			}
		} catch (Exception e) {
			sendMessage(result, what);
		}
	}

	protected void sendSuccess(Object result, int what) {
		sendMessage(result, what);
	}

	/**
	 * 发送请求结果至调用处
	 * 
	 * @param obj
	 *            请求结果
	 * @param what
	 *            请求标识
	 */
	protected void sendMessage(Object obj, int what) {
		if (mHandler != null) {
			Message msg = Message.obtain();
			msg.what = what;
			if (obj != null) {
				msg.obj = obj;
			}
			mHandler.sendMessage(msg);
		}
	}

	/**
	 * 发送请求结果至调用处
	 * 
	 * @param what
	 *            请求标识
	 */
	protected void sendEmptyMessage(int what) {
		if (mHandler != null) {
			mHandler.sendEmptyMessage(what);
		}
	}

	public void setRequestUrl(String requestUrl) {
		this.mRequestUrl = requestUrl;
	}

	public void setTimeOut(int outTime) {
		this.outTime = outTime;
		mHttpClient.setTimeOut(outTime);
	}

	public void setMethod(RequestMethod requstMethod) {
		this.mMethod = requstMethod;
		mHttpClient.setMethod(mMethod);
	}

	public void setClientType(ClientType clientType) {
		this.mClientType = clientType;
		initClient();
	}

	protected void onSuccess(String response) {
	}

	protected void onSuccess(byte[] response) {
	}

	protected void onFailure(String error) {
		sendEmptyMessage(What.HTTP_NET_WORK_ERROR);
	}

	protected abstract void setParams();

	public void setByteResponse(boolean isByteResponse) {
		this.isByteResponse = isByteResponse;
		mHttpClient.setByteResponse(isByteResponse);
	}
}
