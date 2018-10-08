package com.goodsurfing.server.net;

import android.content.Context;
import android.os.Handler;

import com.alibaba.fastjson.JSON;
import com.android.component.constants.What;
import com.goodsurfing.beans.Parents;
import com.goodsurfing.beans.User;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.utils.CommonUtil;
import com.goodsurfing.utils.SharUtil;

/**
 * 修改家长密码
 */
public class HczAlterPwdNet extends HczNetUtils {
    public HczAlterPwdNet(Context context, Handler handler) {
        super(context, Constants.HCZ_ALTERPWD_URL, handler);
    }

    public void putParams(String oldPwd, String newPwd) {
        maps.put("uid", Constants.userId);
        maps.put("oldPwd", oldPwd);
        maps.put("newPwd", newPwd);
    }

    @Override
    protected void onHczFailure(String error) {
        sendMessage(error, What.HTTP_REQUEST_CURD_FAILURE);
    }

    @Override
    protected void onHczSuccess(String result) {
        sendMessage(result, What.HTTP_REQUEST_CURD_SUCCESS);
    }
}
