package com.goodsurfing.server;

import org.json.JSONObject;

import android.content.Context;

import com.goodsurfing.beans.User;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.main.LoginActivity;
import com.goodsurfing.server.utils.BaseDataService;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResponder;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.CommonUtil;
import com.goodsurfing.utils.LogUtil;
import com.goodsurfing.utils.PropertiesUtil;
import com.goodsurfing.utils.SharUtil;

public class LoginServer extends BaseDataService {
	Context context;

	public LoginServer(DataServiceResponder responder, String url, Context context) {
		super(responder, url, context, true, true);
		url += "&dev" + ActivityUtil.getDeviceToken(context);
		this.context = context;
	}

	@Override
	public DataServiceResult parseResponse(String entity, DataServiceResponder responder, DataServiceResult result) {
		try {
			JSONObject jsonObject = new JSONObject(entity);
			JSONObject resultjson = jsonObject.getJSONObject("result");
			String errCode = resultjson.getString("resultCode");
			if ("0".equals(errCode)) {
				Constants.isShowLogin = false;
				Constants.isGetTime = false;
				JSONObject userjson = jsonObject.getJSONObject("user");
				Constants.userId = userjson.getString("UserId");
				Constants.tokenID = userjson.getString("token");
				String mode = userjson.getString("Mode");
				Constants.Account = userjson.getString("Account");
				Constants.dealTime = userjson.getString("ExpirationTime");
				try {
					Constants.bind = userjson.getInt("bind") == 1 ? true : false;
				} catch (Exception e) {
					Constants.bind = false;
				}
				try {
					Constants.loginTime = userjson.getString("loginTime");
				} catch (Exception e) {
				}
				try {
					Constants.rewardCode = userjson.getString("rewardCode");
				} catch (Exception e) {
				}
				SharUtil.saveBind(context, Constants.userId, Constants.bind);
				try {
					Constants.userMobile = userjson.getString("Mobile");
				} catch (Exception e) {
					Constants.userMobile = "";
				}
				try {
					Constants.adress = userjson.getString("address");
				} catch (Exception e) {
					Constants.adress = "";
				}
				try {
					Constants.name = userjson.getString("name");
				} catch (Exception e) {
					Constants.name = "";
				}
				try {
					Constants.sex = userjson.getString("sex");
				} catch (Exception e) {
					Constants.sex = "";
				}
				try {
					Constants.birthday = userjson.getString("birthday");
				} catch (Exception e) {
					Constants.birthday = "";
				}
				try {
					Constants.email = userjson.getString("Email");
				} catch (Exception e) {
					Constants.email = "";
				}
				Constants.mode = Integer.parseInt(mode);
				User user = User.getUser();
				user.setuId(Constants.userId);
				user.setTokenId(Constants.tokenID);
				user.setPhone(Constants.userMobile);
				user.setLoginTime(Constants.loginTime);
				user.setRewardCode(Constants.rewardCode);
				user.setMode(mode);
				user.setAccount(Constants.Account);
				user.setUserName(Constants.name);
				user.setEditTime(Constants.dealTime);
				user.setUserSex(Constants.sex);
				user.setEmail(Constants.email);
				user.setAvatar(Constants.birthday);
				user.setAddress(Constants.adress);
				CommonUtil.setUser(context, user);
				result.code = "0";
				ActivityUtil.sendEvent4UM(context, "login", "login", 30);
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
