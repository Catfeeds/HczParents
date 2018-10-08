package com.android.component.net.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.android.component.net.parse.JSONKey;
//import java.io.OutputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;

/**
 * @description
 * 
 * @author 谢志杰 E-mail: xzj2125123@163.com
 * @create 2015-10-20上午10:10:25
 * @version 1.0.0
 * @company 北京易微网络技术有限公司 Copyright: 版权所有 (c) 2014
 */
public class ByteHttpRequest implements Runnable {

	private RequestParams mParams;

	private String mUrl;

	private int outTime = 30 * 1000;

	private int maxRetries = 1;

	private boolean isByteResponse = false;

	private ByteHttpResponseHandler mHandler;

	public ByteHttpRequest() {
	}

	public ByteHttpRequest(String url, RequestParams params,
			ByteHttpResponseHandler handler) {
		this.mUrl = url;
		this.mParams = params;
		this.mHandler = handler;
	}

	@Override
	public void run() {
		sendRequestWithRetries();
	}

	protected void sendRequestWithRetries() {
		boolean retry = true;
		int count = 1;
		RetryHandler retryHandler = new RetryHandler(maxRetries);
		while (retry) {
			try {
				// InputStream responseStream = sendRequest();
				byte[] response = sendRequest();
				// if (responseStream != null) {
				if (response != null) {
					if (mHandler != null) {
						if (isByteResponse)
							// mHandler.sendSuccessMessage(getByteResponse(responseStream));
							mHandler.sendSuccessMessage(response);
						else
							// mHandler.sendSuccessMessage(getResponse(responseStream));
							mHandler.sendSuccessMessage(getResponse(response));
					}
				} else {
					if (mHandler != null) {
						mHandler.sendFailurMessage("连接失败");
					}
				}
				retry = false;
			} catch (IOException e) {
				e.printStackTrace();
				retry = retryHandler.retryRequest(e, count);
				if (!retry) {
					if (mHandler != null) {
						mHandler.sendFailurMessage(e.getMessage());
					}
				}
			}
			count++;
		}
	}

	protected byte[] sendRequest() throws ClientProtocolException, IOException {
		byte[] bytes = mParams.getParams();
		// URL url = new URL(mUrl);
		// HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		// conn.setConnectTimeout(outTime);
		// conn.setReadTimeout(outTime);
		// conn.setUseCaches(false);
		// conn.setRequestMethod("POST");
		// conn.setDoOutput(true);
		// conn.setDoInput(true);
		// conn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
		// conn.setRequestProperty("Content-Length",
		// String.valueOf(bytes.length));
		// OutputStream outStream = conn.getOutputStream();
		// outStream.write(bytes);
		// outStream.flush();
		// outStream.close();
		// if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
		// return conn.getInputStream();
		// } else {
		// return null;
		// }

		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, outTime);
		HttpConnectionParams.setSoTimeout(httpParams, outTime);
		HttpClient client = new DefaultHttpClient(httpParams);

		HttpPost request = new HttpPost(mUrl);
		request.setEntity(new ByteArrayEntity(bytes));
		HttpResponse response = client.execute(request);
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			byte[] res = EntityUtils.toByteArray(entity);
			return res;
		} else {
			return null;
		}
	}

	/**
	 * 返回请求结果
	 * 
	 * @param responseStream
	 * @return
	 * @throws IOException
	 * @author 谢志杰
	 * @time 2014-11-7下午05:17:44
	 */
	protected String getResponse(InputStream responseStream) throws IOException {
		byte[] reponsebyte = getByteResponse(responseStream);
		return getResponse(reponsebyte);
	}

	/**
	 * 返回请求结果
	 * 
	 * @param reponsebyte
	 * @return
	 * @throws IOException
	 * @author 谢志杰
	 * @time 2014-11-7下午05:17:44
	 */
	protected String getResponse(byte[] reponsebyte) throws IOException {
		ByteInputStream bis = new ByteInputStream(reponsebyte);
		JSONObject responseJson = new JSONObject();
		try {
//			bis.readInt();
//			bis.readShort();
			responseJson.put(JSONKey.STATE, bis.readInt());
			responseJson.put(JSONKey.ERROR_MESSAGE, bis.readString());
			try {
				responseJson.put(JSONKey.DATA, bis.readString());
			} catch (Exception e) {
				responseJson.put(JSONKey.DATA, "");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseJson.toString();
	}

	/**
	 * 返回请求结果
	 * 
	 * @param responseStream
	 * @return
	 * @throws IOException
	 * @author 谢志杰
	 * @time 2014-11-7下午05:17:44
	 */
	protected byte[] getByteResponse(InputStream responseStream)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int size = 1024;
		byte[] buffer = new byte[size];
		int n = 0;
		while ((n = responseStream.read(buffer, 0, size)) > 0) {
			baos.write(buffer, 0, n);
		}
		return baos.toByteArray();
	}

	public void setTimeOut(int outTime) {
		this.outTime = outTime;
	}

	public void setUrl(String url) {
		this.mUrl = url;
	}

	public void setParams(RequestParams params) {
		this.mParams = params;
	}

	public void setByteHttpResponseHandler(ByteHttpResponseHandler handler) {
		this.mHandler = handler;
	}

	public void setByteResponse(boolean isByteResponse) {
		this.isByteResponse = isByteResponse;
	}
}
