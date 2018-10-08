package com.goodsurfing.server;

import org.json.JSONObject;

import android.content.Context;

import com.goodsurfing.constants.Constants;
import com.goodsurfing.server.utils.BaseDataService;
import com.goodsurfing.utils.LogUtil;
import com.goodsurfing.utils.PropertiesUtil;

/**
 * 
 * @author xzj
 * 
 */
public class UpDataManagerServer extends BaseDataService {
	Context context;

	public UpDataManagerServer(DataServiceResponder responder, String url, Context context) {
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
				JSONObject appVersion = jsonObject.getJSONObject("appversion");
				result.action = appVersion.getString("appversion");
				result.extra = appVersion.getString("downloadurl");
				result.extra1 = appVersion.getString("updatedesc");
				result.code = "0";
				try {
					Constants.isRegistShow = appVersion.getInt("open_status") == 1 ? true : false;
				} catch (Exception e) {
					e.printStackTrace();
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
