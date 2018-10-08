package com.android.component.net.parse;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.component.net.NetResult;

/**
 * @description JSON解析基类
 * 
 * @author 谢志杰 E-mail: xzj2125123@163.com
 * @create 2015-7-7 下午5:01:01
 * @version 1.0.0
 * @company 湖南一石科技有限公司 Copyright: 版权所有 (c) 2016
 */
public class BaseJSonParse implements IJSonParse {

	@Override
	public NetResult parseJSON(String response) {
		try {
			JSONObject jsonObject = new JSONObject(response);
			int state = jsonObject.getInt(JSONKey.STATE);
			String data = "";
			String errorMsg = "";
			int num = 1;
			if (state != JSONKey.STATE_SUCCESS) {
				errorMsg = jsonObject.getString(JSONKey.ERROR_MESSAGE);
			} else {
				try {
					errorMsg = jsonObject.getString(JSONKey.ERROR_MESSAGE);
					data = jsonObject.getString(JSONKey.DATA);
				} catch (JSONException e) {
					data = "";
				}
				try {
					num = jsonObject.getInt("num");
				} catch (Exception e) {
					num = 1;
				}

			}
			NetResult jsonObj = new NetResult(state, errorMsg, data, num);
			return jsonObj;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return new NetResult(JSONKey.STATE_FAILURE, "数据格式错误", null);
	}

	@Override
	public Object parse(String response) {
		return null;
	}

	@Override
	public Object parse(String response, Class<?> clazz) {
		return null;
	}

}
