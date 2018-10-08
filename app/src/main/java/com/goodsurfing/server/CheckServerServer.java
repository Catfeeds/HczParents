package com.goodsurfing.server;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.goodsurfing.beans.User;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.server.utils.BaseDataService;
import com.goodsurfing.utils.CommonUtil;
import com.goodsurfing.utils.LogUtil;
import com.goodsurfing.utils.PropertiesUtil;

public class CheckServerServer extends BaseDataService {
	Context context;

	public CheckServerServer(DataServiceResponder responder, String url, Context context) {
		super(responder, url, context, false, true);
		this.context = context;
		Constants.isbusy = false;
	}

	@Override
	public DataServiceResult parseResponse(String entity, DataServiceResponder responder, DataServiceResult result) {
		try {
			JSONObject jsonObject = new JSONObject(entity);
			JSONObject resultjson = jsonObject.getJSONObject("result");
			String errCode = resultjson.getString("resultCode");
			if ("0".equals(errCode)) {
				JSONArray iplists = jsonObject.getJSONArray("iplist");
				if (iplists.length() > 0) {
					Constants.SERVER_URL = "http://" + iplists.getJSONObject(0).getString("ip");
					User user = User.getUser();
					user.setIP(Constants.SERVER_URL);
					CommonUtil.setUser(context, user);
					result.code = "0";
					Constants.isbusy = true;
				} else {
					result.code = "1";
				}
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
