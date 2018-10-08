package com.android.component.net.parse;

import com.alibaba.fastjson.JSON;
import com.android.component.net.NetResult;
import com.android.component.utils.StringUtil;

/**
 * @description 使用fastJSonParse解析JSON
 * 
 * @author 谢志杰 E-mail: xzj2125123@163.com
 * @create 2016-8-17 下午03:12:04
 * @version 1.0.0
 * @company 湖南一石科技有限公司 Copyright: 版权所有 (c) 2016
 */
public class FastJSonParse extends BaseJSonParse {

	@Override
	public Object parse(String response, Class<?> objClass) {
		try {
			NetResult jsonObj = parseJSON(response);
			if (jsonObj.getState() != JSONKey.STATE_SUCCESS) {
				return jsonObj;
			}
			if (StringUtil.isEmpty(jsonObj.getData())) {
				jsonObj.setErrorMsg("无数据");
				return jsonObj;
			}
			Object obj = null;
			if (jsonObj.getData().startsWith("[")) {
				obj = JSON.parseArray(jsonObj.getData(), objClass);
			} else {
				obj = JSON.parseObject(jsonObj.getData(), objClass);
			}
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			return new NetResult(JSONKey.STATE_FAILURE, "数据错误", null);
		}

	}
	
}
