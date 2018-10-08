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

/**
 * 好成长 第三方绑定接口
 */
public class HczThreeBindNet extends HczNetUtils {
    public HczThreeBindNet(Context context, Handler handler) {
        super(context, Constants.HCZ_OTHERBIND_URL, handler);
    }

    public void putParams(String mobile, String code) {
        maps.put("mobile", mobile);
        maps.put("authid", Constants.ThreeLoginAuthId);
        maps.put("code", code);
    }

    @Override
    protected void onHczFailure(String error) {
        sendMessage(error, What.HTTP_REQUEST_CURD_FAILURE);
    }

    @Override
    protected void onHczSuccess(String result) {
        Parents parents = JSON.parseObject(result, Parents.class);
        sendMessage(parents, What.HTTP_REQUEST_CURD_SUCCESS);
        Constants.isShowLogin = false;
        Constants.isGetTime = false;
        Constants.userId = parents.getServerId();
        Constants.tokenID = parents.getDeviceToken();
        Constants.Account = parents.getMobile();
        Constants.dealTime = (String) parents.getExpirationTime() + "";
        Constants.name = (String) parents.getName() + "";
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
        SharUtil.savePhone(mContext, Constants.userMobile);
        CommonUtil.setUser(mContext, user);
    }
}
