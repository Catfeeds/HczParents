package com.goodsurfing.server;

import android.content.Context;

import com.goodsurfing.beans.WebFilterBean;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.server.utils.BaseDataService;
import com.goodsurfing.utils.LogUtil;
import com.goodsurfing.utils.PropertiesUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetAppListServer extends BaseDataService {
	Context context;

	public GetAppListServer(DataServiceResponder responder, String url,
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
				JSONArray listJson = jsonObject.getJSONArray("apps");
				List<WebFilterBean> listAdapter = new ArrayList<WebFilterBean>();
				for (int i = 0; i < listJson.length(); i++) {
					WebFilterBean bean = new WebFilterBean();
					bean.setWebTitle(listJson.getJSONObject(i)
							.getString("name"));
					bean.setIcon(listJson.getJSONObject(i).getString(
							"icon"));
					String type = listJson.getJSONObject(i)
							.getString("cateid");
					String typeTemp = Constants.typeMap.get(type);
					if (typeTemp != null)
						bean.setWebTye(typeTemp);
					else
						bean.setWebTye("其他");
					bean.setWebSite(listJson.getJSONObject(i).getString(
							"type"));
					bean.setId(listJson.getJSONObject(i).getString("id"));
					bean.set_id(i);
					listAdapter.add(bean);
				}
				result.code = "0";
				result.result = listAdapter;
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
