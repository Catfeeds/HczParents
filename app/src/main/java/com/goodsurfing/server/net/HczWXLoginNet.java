package com.goodsurfing.server.net;

import android.content.Context;
import android.os.Handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.component.constants.What;
import com.goodsurfing.beans.Parents;
import com.goodsurfing.beans.User;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.CommonUtil;
import com.goodsurfing.utils.SharUtil;

public class HczWXLoginNet extends HczNetUtils {
    public HczWXLoginNet(Context context, Handler handler) {
        super(context, Constants.HCZ_OTHERLOGIN_URL, handler);
        maps.put("deviceToken", ActivityUtil.getDeviceID(context));
    }

    public void putParams(String openid, String type) {
        maps.put("openid", openid);
        maps.put("type", type);
    }

    @Override
    protected void onHczFailure(String error) {
        sendMessage(error, What.HTTP_REQUEST_CURD_FAILURE);
    }

    @Override
    protected void onHczSuccess(String result) {
        JSONObject object = JSON.parseObject(result);
        if(object.getString("type").equals("2")){
            //状态为2 表示还没有绑定手机，必须前去绑定后才可使用。
            Constants.ThreeLoginAuthId=object.getString("Id");
            sendMessage(2,What.HTTP_REQUEST_CURD_SUCCESS);
        }else {
            Parents parents = JSON.parseObject(object.getJSONObject("data").toJSONString(), Parents.class);
            sendMessage(1, What.HTTP_REQUEST_CURD_SUCCESS);
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
        }
    }
}
