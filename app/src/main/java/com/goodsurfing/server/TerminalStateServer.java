package com.goodsurfing.server;

import android.content.Context;

import com.goodsurfing.server.utils.BaseDataService;
import com.goodsurfing.utils.PropertiesUtil;

import org.json.JSONObject;

/**
 * 获取网络授权时间
 * 
 * @author xzj
 * 
 */
public class TerminalStateServer extends BaseDataService {
	Context context;

	public TerminalStateServer(DataServiceResponder responder, String url,
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
				JSONObject terminaljson = jsonObject.getJSONObject("terminal");
				result.extra = terminaljson.getString("terminalstr");
				result.code = "0";
			} else {
				String errMsg = PropertiesUtil.getProperties(context)
						.getProperty(errCode);
				result.code = "1";
				result.extra = errMsg;
			}

		} catch (Exception e) {
			result.code = "1";
			result.extra = "数据简析出错了";
		}

		return super.parseResponse(entity, responder, result);
	}

}
