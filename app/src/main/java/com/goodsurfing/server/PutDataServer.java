package com.goodsurfing.server;

import org.json.JSONObject;

import android.content.Context;

import com.goodsurfing.beans.User;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.server.utils.BaseDataService;
import com.goodsurfing.utils.CommonUtil;
import com.goodsurfing.utils.LogUtil;
import com.goodsurfing.utils.PropertiesUtil;
import com.goodsurfing.utils.SharUtil;

/**
 * 添加网络卡片
 * 
 * @author xzj
 * 
 */
public class PutDataServer extends BaseDataService {
	Context context;

	public PutDataServer(DataServiceResponder responder, String url, Context context) {
		super(responder, url, context, true,false);
		this.context = context;
	}

	public PutDataServer(DataServiceResponder responder, String url, Context context, boolean isExtime) {
		super(responder, url, context, isExtime,false);
		this.context = context;
	}

	public PutDataServer(DataServiceResponder responder, String url, Context context, boolean isExtime, boolean isShow) {
		super(responder, url, context,isExtime,isShow);
		this.context = context;
	}

	@Override
	public DataServiceResult parseResponse(String entity, DataServiceResponder responder, DataServiceResult result) {
		try {
			JSONObject jsonObject = new JSONObject(entity);
			JSONObject resultjson = jsonObject.getJSONObject("result");
			String errCode = resultjson.getString("resultCode");
			if ("0".equals(errCode)) {
				result.extra=entity;
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
