package com.goodsurfing.server.net;

import android.content.Context;
import android.os.Handler;

import com.alibaba.fastjson.JSON;
import com.android.component.constants.What;
import com.goodsurfing.beans.ExpireBean;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.utils.ActivityUtil;

public class HczGetCreatVipNet extends HczNetUtils {
    public HczGetCreatVipNet(Context context, Handler handler) {
        super(context, Constants.HCZ_FIRSTGIVE_URL, handler);
        maps.put("deviceToken", ActivityUtil.getDeviceID(context));
    }

    public void putParams() {
        maps.put("uid", Constants.userId);
    }

    @Override
    protected void onHczFailure(String error) {
        sendMessage(error, What.HTTP_REQUEST_CURD_FAILURE);
    }

    @Override
    protected void onHczSuccess(String result) {
        sendMessage(JSON.parseObject(result, ExpireBean.class), What.HTTP_REQUEST_CURD_SUCCESS);
    }
}
