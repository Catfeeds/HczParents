package com.goodsurfing.server;

import android.content.Context;
import android.util.Log;

import com.goodsurfing.beans.WebFilterBean;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.server.utils.BaseDataService;
import com.goodsurfing.utils.LogUtil;
import com.goodsurfing.utils.PropertiesUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AllowedWebsServer extends BaseDataService {
	Context context;

	public AllowedWebsServer(DataServiceResponder responder, String url, Context context) {
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
				List<WebFilterBean> listAdapter = new ArrayList<WebFilterBean>();
				try {
				JSONArray listJson = jsonObject.getJSONArray("appbwlist");
				Constants.appDao.clearWebFilterBean();
				for (int i = 0; i < listJson.length(); i++) {
					WebFilterBean bean = new WebFilterBean();
					bean.setWebTitle(listJson.getJSONObject(i).getString("name"));
					String type = listJson.getJSONObject(i).getString("classid");
					String typeTemp = Constants.typeMap.get(type);
					if (typeTemp != null)
						bean.setWebTye(typeTemp);
					else
						bean.setWebTye("其他");
					bean.setIcon(listJson.getJSONObject(i).getString("icon"));
					bean.setWebStatus(listJson.getJSONObject(i).getString("statu"));
					bean.setWebClassType(listJson.getJSONObject(i).getString("type"));
					bean.setId(listJson.getJSONObject(i).getString("domainid"));
					Constants.appDao.upData4head(bean.getId(), "1");
					listAdapter.add(bean);
				}
				} catch (Exception e) {
					Log.i("xzj", "黑名单请求");
				}
				JSONArray weblistJson = jsonObject.getJSONArray("bwlist");
				for (int i = 0; i < weblistJson.length(); i++) {
					WebFilterBean bean = new WebFilterBean();
					bean.setWebTitle(weblistJson.getJSONObject(i).getString("name"));
					bean.setWebSite(weblistJson.getJSONObject(i).getString("domain"));
					String type = weblistJson.getJSONObject(i).getString("classid");
					String typeTemp = Constants.typeMap.get(type);
					if (typeTemp != null)
						bean.setWebTye(typeTemp);
					else
						bean.setWebTye("其他");
					bean.setWebStatus(weblistJson.getJSONObject(i).getString("statu"));
					bean.setWebClassType(weblistJson.getJSONObject(i).getString("type"));
					bean.setId(weblistJson.getJSONObject(i).getString("domainid"));
					listAdapter.add(bean);
				}
				result.code = "0";
				result.result = listAdapter;
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
