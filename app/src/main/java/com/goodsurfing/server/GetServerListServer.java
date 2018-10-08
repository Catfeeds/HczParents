package com.goodsurfing.server;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.goodsurfing.constants.Constants;
import com.goodsurfing.server.utils.BaseDataService;
import com.goodsurfing.utils.LogUtil;
import com.goodsurfing.utils.PropertiesUtil;

public class GetServerListServer extends BaseDataService {
	Context context;

	public GetServerListServer(DataServiceResponder responder, String url, Context context) {
		super(responder, url, context, false, false);
		this.context = context;
	}

	@Override
	public DataServiceResult parseResponse(String entity, DataServiceResponder responder, DataServiceResult result) {
		try {
			JSONObject jsonObject = new JSONObject(entity);
			JSONObject resultjson = jsonObject.getJSONObject("result");
			String errCode = resultjson.getString("resultCode");
			if ("0".equals(errCode)) {
				JSONArray pages = jsonObject.getJSONArray("providerlist");
				if (pages.length() > 0) {
					Constants.serverList.clear();
					for (int i = 0; i < pages.length(); i++) {
						Constants.serverList.add(pages.getJSONObject(i).getString("name"));
					}
				}
				result.code = "0";
			} else {
				String errMsg = PropertiesUtil.getProperties(context).getProperty(errCode);
				result.code = "1";
				result.extra = errMsg;
			}

		} catch (Exception e) {
			LogUtil.logError(e);
			result.code = "1";
			result.extra = "服务器忙，请稍候再试";
		}

		return super.parseResponse(entity, responder, result);
	}

}
