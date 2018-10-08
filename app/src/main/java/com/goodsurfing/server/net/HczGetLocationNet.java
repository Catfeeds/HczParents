package com.goodsurfing.server.net;

import android.content.Context;
import android.os.Handler;

import com.alibaba.fastjson.JSON;
import com.android.component.constants.What;
import com.goodsurfing.beans.DynamicBean;
import com.goodsurfing.beans.LocationBean;
import com.goodsurfing.constants.Constants;

import java.util.List;

/**
 * 获取实时位置
 */
public class HczGetLocationNet extends HczNetUtils {
    public HczGetLocationNet(Context context, Handler handler) {
        super(context, Constants.HCZ_GETPOSITION_URL, handler);
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
        LocationBean locationBean = JSON.parseObject(result,LocationBean.class);
        sendMessage(locationBean, What.HTTP_REQUEST_CURD_SUCCESS);
    }
}
