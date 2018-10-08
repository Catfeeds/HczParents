package com.android.component.net.parse;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.android.component.net.NetResult;
import com.android.component.utils.StringUtil;
import com.google.gson.Gson;

/**
 * @description 使用Gson解析JSON
 * 
 * @author 谢志杰 E-mail: xzj2125123@163.com
 * @create 2015-7-7 下午2:59:48
 * @version 1.0.0
 * @company 湖南一石科技有限公司 Copyright: 版权所有 (c) 2016
 */
public class GsonParse extends BaseJSonParse {

	@Override
	public Object parse(String response, Class<?> objClass) {
		Gson gson = new Gson();
		try {
			NetResult jsonObj = parseJSON(response);
			if (jsonObj.getState() != JSONKey.STATE_SUCCESS) {
				return jsonObj;
			}
			if (StringUtil.isEmpty(jsonObj.getData())) {
				jsonObj.setErrorMsg("无数据");
				return jsonObj;
			}
			if (jsonObj.getData().startsWith("[")) {
				JSONArray array = new JSONArray(jsonObj.getData());
				List<Object> objList = new ArrayList<Object>();
				for (int i = 0; i < array.length(); i++) {
					JSONObject jsonMsg = array.getJSONObject(i);
					Object obj = gson.fromJson(jsonMsg.toString(), objClass);
					objList.add(obj);
				}
				return objList;
			} else {
				return gson.fromJson(jsonObj.getData(), objClass);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return new NetResult(JSONKey.STATE_FAILURE, "数据错误", null);
		}
	}

}
