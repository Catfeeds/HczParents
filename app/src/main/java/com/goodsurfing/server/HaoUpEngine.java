package com.goodsurfing.server;

import android.content.Context;
import android.os.Handler;

import com.android.component.net.BaseEngine;
import com.android.component.net.client.IHttpClient.ClientType;
import com.android.component.net.client.IHttpClient.RequestMethod;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.utils.ActivityUtil;

public class HaoUpEngine extends BaseEngine {

	public HaoUpEngine(Context context, String url, Handler handler) {
		super(context, "", url, ClientType.Volley, RequestMethod.GET, handler);
	}

	@Override
	protected void setParams() {
	}

	@Override
	protected void onSuccess(String response) {
		super.onSuccess(response);
	}

}
