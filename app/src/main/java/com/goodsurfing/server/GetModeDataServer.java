package com.goodsurfing.server;

import org.json.JSONObject;

import android.content.Context;

import com.goodsurfing.server.utils.BaseDataService;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResponder;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResult;
import com.goodsurfing.utils.LogUtil;
import com.goodsurfing.utils.PropertiesUtil;

/**
 * 添加网络卡片
 * 
 * @author xzj
 * 
 */
public class GetModeDataServer extends BaseDataService {
	Context context;

	public GetModeDataServer(DataServiceResponder responder, String url, Context context) {
		super(responder, url, context);
		this.context = context;
	}

	@Override
	public DataServiceResult parseResponse(String entity, DataServiceResponder responder, DataServiceResult result) {
		try {
			JSONObject jsonObject = new JSONObject(entity);
			JSONObject resultjson = jsonObject.getJSONObject("result");
			String errCode = resultjson.getString("resultCode");
			if ("0".equals(errCode)) {
				result.code = "0";
				// userinfo
				JSONObject userinfo = jsonObject.getJSONObject("userinfo");
				int mode = userinfo.getInt("Mode");
				result.result = mode;
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
