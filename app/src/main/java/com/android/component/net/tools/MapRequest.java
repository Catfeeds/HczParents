package com.android.component.net.tools;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

/**
 * @description 自定义请求对象,传递参数为一个Map,使用get或post方式请求,默认为post
 * 
 * @author 谢志杰 E-mail: xzj2125123@163.com
 * @create 2016-8-21 上午11:40:27
 * @version 1.0.0
 * @company 湖南一石科技有限公司 Copyright: 版权所有 (c) 2016
 */
public class MapRequest extends Request<String> {
	/**
	 * 传递参数
	 */
	private Map<String, Object> mMap;
	/**
	 * 请求结果回调
	 */
	private Listener<String> mListener;

	private MultipartEntity mEntity;

	public MapRequest(String url, Map<String, Object> map, int method,
			Listener<String> listener, ErrorListener errorListener) {
		super(method, url, errorListener);
		this.mListener = listener;
		this.mMap = map;
		mEntity = new MultipartEntity();
	}

	public MapRequest(String url, Map<String, Object> map,
			Listener<String> listener, ErrorListener errorListener) {
		super(Request.Method.POST, url, errorListener);
		this.mListener = listener;
		this.mMap = map;
		mEntity = new MultipartEntity();
	}

	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		Map<String, String> params = new HashMap<String, String>();
		for (Map.Entry<String, Object> entry : mMap.entrySet()) {
			params.put(entry.getKey(), entry.getValue().toString());
		}
		return params;
	}

	@Override
	public byte[] getBody() throws AuthFailureError {
		if (!hasFile()) {
			return super.getBody();
		} else {
			Map<String, File> uploadParams = new HashMap<String, File>();
			for (Map.Entry<String, Object> entry : mMap.entrySet()) {
				if (entry.getValue() instanceof File) {
					uploadParams.put(entry.getKey(), (File) entry.getValue());
				} else {
					mEntity.addPart(entry.getKey(), entry.getValue().toString());
				}
			}
			for (Entry<String, File> entry : uploadParams.entrySet()) {
				int i = 1;
				int j = uploadParams.size();
				mEntity.addPart(entry.getKey(), entry.getValue(), i == j);
				i++;
			}
			return mEntity.getContent();
		}
	}

	@Override
	public String getBodyContentType() {
		for (Map.Entry<String, Object> entry : mMap.entrySet()) {
			if (entry.getValue() instanceof File) {
				return "multipart/form-data; boundary=" + mEntity.getBoundary();
			}
		}
		return super.getBodyContentType();
	}

	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
		try {
			String jsonString = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));
			return Response.success(jsonString,
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		}
	}

	@Override
	protected void deliverResponse(String response) {
		mListener.onResponse(response);
	}

	/**
	 * 是否有文件参数
	 * 
	 * @author 谢志杰
	 * @create 2015-7-3 下午12:38:29
	 * @return
	 */
	private boolean hasFile() {
		for (Map.Entry<String, Object> entry : mMap.entrySet()) {
			if (entry.getValue() instanceof File) {
				return true;
			}
		}
		return false;
	}
}
