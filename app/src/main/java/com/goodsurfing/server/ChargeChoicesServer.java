package com.goodsurfing.server;

import android.content.Context;

import com.goodsurfing.beans.ChargeIDBean;
import com.goodsurfing.server.utils.BaseDataService;
import com.goodsurfing.utils.LogUtil;
import com.goodsurfing.utils.PropertiesUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChargeChoicesServer extends BaseDataService {
	Context context;

	public ChargeChoicesServer(DataServiceResponder responder, String url,
			Context context) {
		super(responder, url, context,false,true);
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
					JSONArray pages = jsonObject
							.getJSONArray("package");
					 List<ChargeIDBean> listAdapter = new ArrayList<ChargeIDBean>();
					for (int i = 0; i < pages.length(); i++) {
						ChargeIDBean bean = new ChargeIDBean();
						bean.setId(pages.getJSONObject(i)
								.getString("id"));
						bean.setName(pages.getJSONObject(i)
								.getString("Name"));
						bean.setPrice(pages.getJSONObject(i)
								.getString("Price"));
						bean.setType(pages.getJSONObject(i)
								.getString("Time"));
						listAdapter.add(bean);
					}
				result.code="0";
				result.result = listAdapter;
			}else{
				String errMsg = PropertiesUtil.getProperties(
						context).getProperty(
						errCode);
				result.code="1";
				result.extra = errMsg;
			}

		} catch (Exception e) {
			LogUtil.logError(e);
			result.code="1";
			result.extra = "服务器忙，请稍候再试";
		}

		return super.parseResponse(entity, responder, result);
	}

}
