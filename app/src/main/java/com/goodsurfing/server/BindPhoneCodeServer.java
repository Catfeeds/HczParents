package com.goodsurfing.server;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.android.component.net.BaseEngine;
import com.android.component.net.client.IHttpClient.ClientType;
import com.android.component.net.client.IHttpClient.RequestMethod;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.server.utils.BaseDataService;
import com.goodsurfing.utils.LogUtil;
import com.goodsurfing.utils.SignUtils;

public class BindPhoneCodeServer extends BaseDataService {
	Context context;
	String code;
	String mobile;

	public BindPhoneCodeServer(Context context, String url, DataServiceResponder responder) {
		super( responder, url,context);
		this.context = context;
		this.code=code;
		this.mobile = mobile;
	}
	@Override
	public DataServiceResult parseResponse(String entity,
										   DataServiceResponder responder, DataServiceResult result) {
		try {
			JSONObject jsonObject = new JSONObject(entity);
			String errCode = jsonObject.getString("status");
			if ("1".equals(errCode)) {
				result.code = "0";
				result.result = entity;
			} else {
				result.code = "1";
				result.extra = "验证失败";
			}

		} catch (Exception e) {
			result.code = "1";
			result.extra = "服务器忙，请稍候再试";
		}
		return super.parseResponse(entity, responder, result);
	}

}
