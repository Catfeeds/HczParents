package com.goodsurfing.server.net;

import android.content.Context;
import android.os.Handler;

import com.android.component.constants.What;
import com.goodsurfing.constants.Constants;

/**
 * 设置孩子手机的DNS使用
 */
public class HczSetDNSNet extends HczNetUtils {
    public HczSetDNSNet(Context context, Handler handler) {
        super(context, Constants.HCZ_SETOPENDNS_URL, handler);
    }

    public void putParams(int openDNS) {
        maps.put("clientId", Constants.child.getClientDeviceId()+"");
        maps.put("uid", Constants.userId);
        maps.put("openDNS",openDNS+"");
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
