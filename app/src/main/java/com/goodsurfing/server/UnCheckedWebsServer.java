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

public class UnCheckedWebsServer extends BaseDataService {
	Context context;

	public UnCheckedWebsServer(DataServiceResponder responder, String url, Context context) {
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
				JSONArray listJson = jsonObject.getJSONArray("checklist");
				List<WebFilterBean> listAdapter = new ArrayList<WebFilterBean>();
				for (int i = 0; i < listJson.length(); i++) {
					WebFilterBean bean = new WebFilterBean();
					String titleString=listJson.getJSONObject(i).getString("remark");
					String reasonString=listJson.getJSONObject(i).getString("reason");
					if(titleString.equals("")){
						bean.setWebTitle(listJson.getJSONObject(i).getString("domain"));
					}else {
						bean.setWebTitle(titleString);
					}
					if(reasonString.equals("")){
						bean.setReason("");
					}else {
						bean.setReason(reasonString);
					}
					bean.setWebSite(listJson.getJSONObject(i).getString("domain"));
					String type = listJson.getJSONObject(i).getString("audit");
					String typeTemp = Constants.typeMap.get(type);
					if (typeTemp != null)
						bean.setWebTye(typeTemp);
					else
						bean.setWebTye("其他");
					try {
						bean.setWebCreateTime(listJson.getJSONObject(i).getString("ctime"));
					} catch (Exception e) {
						bean.setWebCreateTime("");
					}
				
					bean.setWebClassType(listJson.getJSONObject(i).getString("audit"));
					bean.setId(listJson.getJSONObject(i).getString("id"));
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
