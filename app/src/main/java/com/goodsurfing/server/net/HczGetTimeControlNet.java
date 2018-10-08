package com.goodsurfing.server.net;

import android.content.Context;
import android.os.Handler;

import com.android.component.constants.What;
import com.goodsurfing.constants.Constants;

public class HczGetTimeControlNet extends HczNetUtils {
    public HczGetTimeControlNet(Context context, Handler handler) {
        super(context, Constants.HCZ_GETSCREENTIME_URL, handler);
    }

    public void putParams() {
        maps.put("clientId", Constants.child.getClientDeviceId()+"");
        maps.put("uid", Constants.userId);
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
