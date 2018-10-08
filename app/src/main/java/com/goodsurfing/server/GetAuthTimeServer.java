package com.goodsurfing.server;

import android.content.Context;

import com.goodsurfing.constants.Constants;
import com.goodsurfing.server.utils.BaseDataService;
import com.goodsurfing.utils.LogUtil;
import com.goodsurfing.utils.PropertiesUtil;

import org.json.JSONObject;

/**
 * 获取网络授权时间
 * 
 * @author xzj
 * 
 */
public class GetAuthTimeServer extends BaseDataService {
	Context context;

	public GetAuthTimeServer(DataServiceResponder responder, String url,
			Context context) {
		super(responder, url, context);
		this.context = context;
	}

	@Override
	public DataServiceResult parseResponse(String entity,
			DataServiceResponder responder, DataServiceResult result) {
		try {
			JSONObject jsonObject = new JSONObject(entity);
			JSONObject resultjson = jsonObject.getJSONObject("result");
			String errCode = resultjson.getString("resultCode");

			if ("0".equals(errCode)) {
				JSONObject timejson = jsonObject.getJSONObject("authtime");
				String time = timejson.getString("authtime");
				int seconds = Integer.parseInt(time);
				result.type = seconds;
				result.code = "0";
				Constants.totalSeconds = seconds;
				Constants.isClockLocked = true;
			} else {
				String errMsg = PropertiesUtil.getProperties(context)
						.getProperty(errCode);
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
