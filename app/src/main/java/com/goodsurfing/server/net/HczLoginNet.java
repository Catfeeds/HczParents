package com.goodsurfing.server.net;

import android.content.Context;
import android.os.Handler;

import com.alibaba.fastjson.JSON;
import com.android.component.constants.What;
import com.goodsurfing.beans.Parents;
import com.goodsurfing.beans.User;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.CommonUtil;
import com.goodsurfing.utils.SharUtil;
import com.google.gson.JsonObject;

import org.json.JSONObject;

public class HczLoginNet extends HczNetUtils {
    public HczLoginNet(Context context, Handler handler) {
        super(context, Constants.HCZ_LOGIN_URL, handler);
        maps.put("deviceToken", ActivityUtil.getDeviceID(context));
    }

    public void putParams(String mobile, String code, String password) {
        maps.put("mobile", mobile);
        if (!code.equals("")) {
            maps.put("code", code);
        }
        if (!password.equals("")) {
            maps.put("password", password);
        }
    }

    @Override
    protected void onHczFailure(String error) {
        sendMessage(error, What.HTTP_REQUEST_CURD_FAILURE);
    }

    @Override
    protected void onHczSuccess(String result) {
        Parents parents = JSON.parseObject(result, Parents.class);
        Constants.isShowLogin = false;
        Constants.isGetTime = false;
        Constants.userId =parents.getServerId();
        Constants.tokenID = parents.getDeviceToken();
        Constants.Account = parents.getMobile();
        Constants.dealTime = (String) parents.getExpirationTime()+"";
        Constants.name = (String) parents.getName()+"";
        Constants.userMobile = parents.getMobile();
        Constants.bind = true;
        User user = User.getUser();
        user.setuId(Constants.userId);
        user.setTokenId(Constants.tokenID);
        user.setPhone(Constants.Account);
        user.setAccount(Constants.Account);
        user.setUserName(Constants.name);
        user.setEditTime(Constants.dealTime);
        user.setUserSex(Constants.sex);
        user.setEmail(Constants.email);
        SharUtil.saveBind(mContext, Constants.userId, Constants.bind);
        SharUtil.savePhone(mContext,Constants.userMobile);
        CommonUtil.setUser(mContext, user);
        sendMessage(parents, What.HTTP_REQUEST_CURD_SUCCESS);
    }
}
