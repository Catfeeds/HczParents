package com.goodsurfing.server;

import android.content.Context;

import com.goodsurfing.beans.TimeCardBean;
import com.goodsurfing.server.utils.BaseDataService;
import com.goodsurfing.utils.LogUtil;
import com.goodsurfing.utils.PropertiesUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetWebsInfoServer extends BaseDataService {
	Context context;

	public GetWebsInfoServer(DataServiceResponder responder, String url,
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
				JSONArray listJson = jsonObject
						.getJSONArray("timeacct");
				List<TimeCardBean> listAdapter = new ArrayList<TimeCardBean>();
				for (int i = 0; i < listJson.length(); i++) {
					TimeCardBean bean = new TimeCardBean();
					bean.setId(listJson.getJSONObject(i)
							.getString("id"));
					bean.setName(listJson.getJSONObject(i)
							.getString("username"));
					bean.setTotalTime(listJson.getJSONObject(i)
							.getString("totaltime"));
//					bean.setCreateTime(listJson
//							.getJSONObject(i).getString(
//									"edittime"));
					bean.setRemainTime(listJson
							.getJSONObject(i).getString(
									"usetime"));
					bean.setExpireTime(listJson
							.getJSONObject(i).getString(
									"ExpirationTime"));
					bean.setSelected(false);
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
