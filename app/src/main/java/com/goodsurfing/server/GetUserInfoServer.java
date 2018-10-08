package com.goodsurfing.server;

import org.json.JSONObject;

import android.content.Context;

import com.goodsurfing.beans.User;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.main.LoginActivity;
import com.goodsurfing.server.utils.BaseDataService;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResponder;
import com.goodsurfing.utils.CommonUtil;
import com.goodsurfing.utils.LogUtil;
import com.goodsurfing.utils.PropertiesUtil;
import com.goodsurfing.utils.SharUtil;

public class GetUserInfoServer extends BaseDataService {
	Context context;

	public GetUserInfoServer(DataServiceResponder responder, String url,
			Context context) {
		super(responder,url, context,false,false);
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
				JSONObject userjson = jsonObject.getJSONObject("userinfo");
				String mode = userjson.getString("Mode");
				Constants.Account = userjson.getString("Account");
				Constants.dealTime = userjson.getString("ExpirationTime");
				try {
					Constants.userMobile = userjson.getString("Mobile");
				} catch (Exception e) {
				}
				try {
					Constants.adress = userjson.getString("address");
				} catch (Exception e) {
				}
				try {
					Constants.name = userjson.getString("name");
				} catch (Exception e) {
				}
				Constants.mode = Integer.parseInt(mode);
				User user = User.getUser();
				user.setuId(Constants.userId);
				user.setTokenId(Constants.tokenID);
				user.setPhone(Constants.userMobile);
				user.setMode(mode);
				user.setAccount(Constants.Account);
				user.setUserName(Constants.name);
				user.setEditTime(Constants.dealTime);
				user.setUserSex(Constants.sex);
				user.setEmail(Constants.email);
				user.setAvatar(Constants.birthday);
				user.setAddress(Constants.adress);
				CommonUtil.setUser(context, user);
				result.code="0";
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
