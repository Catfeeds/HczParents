package com.goodsurfing.server.net;

import android.content.Context;
import android.os.Handler;

import com.android.component.constants.What;
import com.goodsurfing.constants.Constants;

/**
 * 设置孩子手机的APP使用时间
 */
public class HczSetAppTimeNet extends HczNetUtils {
    public HczSetAppTimeNet(Context context, Handler handler) {
        super(context, Constants.HCZ_SETAPPTIME_URL, handler);
    }

    public void putParams(String appid,int time) {
        maps.put("clientId", Constants.child.getClientDeviceId()+"");
        maps.put("uid", Constants.userId);
        maps.put("ClientAppId",appid);
        maps.put("utime",time+"");
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
