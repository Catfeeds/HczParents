package com.goodsurfing.server.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.goodsurfing.app.HaoUpApplication;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.main.LoginActivity;
import com.goodsurfing.utils.ActivityUtil;

import net.sourceforge.simcpux.Base64;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 网络请求基类
 * 
 * @author 谢志杰
 * 
 */
public class BaseDataService {
	public DataServiceResponder responder;
	protected DataServiceResult dsResult;
	protected Context context;
	private String action;
	private UploadProgress listener;
	private ProEntity multipartContent;
	public static String PHPSESSID = null;

	public class DataServiceResult {
		public String code; // 接口调用状态
		public Object result; // 接口调用成功返回数据
		public String action; // 接口对应的action
		public String tips; // 接口对应的返回提示
		public String extra; // 额外 预留
		public String extra1; // 额外 预留
		public int type;
		public Object result2; // 接口调用成功返回数据

		public DataServiceResult(String action) {
			this.action = action;
		}
	}

	public interface DataServiceResponder {

		public void onFailure();

		public void onResult(DataServiceResult result);
	}

	public BaseDataService(final DataServiceResponder responder, String url, final Context context, boolean isExtime, boolean isShowDialog) {
		String base_url = url.split("[?]")[0];
		String data = url.split("[?]")[1];
		String all_url;
		if (base_url.equals(Constants.EDIT_PHONE_CODE_URL) || base_url.equals(Constants.SERVER_URL_GLOBAL) || base_url.equals("http://haoup.net/share/share.html")) {
			all_url = url;
		} else {
			data = data + "&timestamp=" + System.currentTimeMillis();
			byte[] b = null;
			try {
				b = data.getBytes("utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			all_url = base_url + "?" + String.valueOf(Base64.encode(b, b.length));
		}
		this.action = all_url.trim();
		this.responder = responder;
		this.context = context;
		this.listener = null;
		if (isExtime) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US);
				if (!Constants.userId.equals("") && !Constants.dealTime.equals("") && sdf.parse(Constants.dealTime).getTime() < System.currentTimeMillis()) {
					ActivityUtil.showDialog(context, "温馨提示", "您的服务已到期，请及时续费");
					ActivityUtil.dismissPopWindow();
					return;
				}
			} catch (ParseException e) {
				ActivityUtil.dismissPopWindow();
				e.printStackTrace();
			}
		}
		Log.i("请求url", "url==" + all_url + "");
		dsResult = new DataServiceResult(action);

		if (HaoUpApplication.getInstance().mRequestQueue == null) {
			HaoUpApplication.getInstance().mRequestQueue = Volley.newRequestQueue(context);
		}
		//创建一个请求
		StringRequest stringRequest =new StringRequest(all_url, new Response.Listener<String>() {
			//正确接收数据回调
			@Override
			public void onResponse(String response) {
				try {
					Log.i("onResponse", "onResponse: "+response);
					JSONObject jsonObject = new JSONObject(response);
					JSONObject resultjson = jsonObject.getJSONObject("result");
					String errCode = resultjson.getString("resultCode");
					if ("2".equals(errCode)) {
						LoginActivity.gotoLogin(context);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				parseResponse(response, responder, dsResult);
				DataServiceResult result = null;
				try {
					result = (DataServiceResult) dsResult;
					responder.onResult(result);
				} catch (Exception e) {
					if (responder != null)
						responder.onResult(result);
				}

			}
		}, new Response.ErrorListener() {//异常后的监听数据
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				responder.onFailure();
			}
		});
		//将get请求添加到队列中
		stringRequest.setRetryPolicy(new DefaultRetryPolicy(25000,0,2));
		HaoUpApplication.getInstance().mRequestQueue.add(stringRequest);
	}

	public BaseDataService(DataServiceResponder responder, String url, Context context) {
		this(responder, url, context, true, false);
	}

	/**
	 * @category 用户中心接口需要覆盖
	 * @return
	 */
	public DataServiceResult parseResponse(String entity, DataServiceResponder responder, DataServiceResult result) {
		return dsResult;
	}

	public void execute() {
	}

	public interface UploadProgress {
		void transferred(float progress, float max);
	}
}
