package com.goodsurfing.server;

import android.content.Context;

import com.goodsurfing.server.utils.BaseDataService;
import com.goodsurfing.utils.LogUtil;

import org.json.JSONObject;

/**
 * @author xzj
 * 
 */
public class DoGetCodeServer extends BaseDataService {
	Context context;

	public DoGetCodeServer(DataServiceResponder responder, String url,
			Context context) {
		super(responder, url, context);
		this.context = context;
	}

	@Override
	public DataServiceResult parseResponse(String entity,
			DataServiceResponder responder, DataServiceResult result) {
		try {
			JSONObject jsonObject = new JSONObject(entity);
			result.code = "0";
			result.result = entity;

		} catch (Exception e) {
			LogUtil.logError(e);
			result.code = "1";
			result.extra = "服务器忙，请稍候再试";
		}

		return super.parseResponse(entity, responder, result);
	}

}
