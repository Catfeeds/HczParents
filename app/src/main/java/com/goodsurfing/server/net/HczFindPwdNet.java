package com.goodsurfing.server.net;

import android.content.Context;
import android.os.Handler;

import com.android.component.constants.What;
import com.goodsurfing.constants.Constants;

/**
 * 找回家长密码
 */
public class HczFindPwdNet extends HczNetUtils {
    public HczFindPwdNet(Context context, Handler handler) {
        super(context, Constants.HCZ_FINDPWD_URL, handler);
    }

    public void putParams(String mobile,String password, String code) {
        maps.put("mobile",mobile);
        maps.put("password", password);
        maps.put("code", code);
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
