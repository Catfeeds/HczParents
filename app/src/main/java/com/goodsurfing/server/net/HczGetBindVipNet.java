package com.goodsurfing.server.net;

import android.content.Context;
import android.os.Handler;

import com.alibaba.fastjson.JSON;
import com.android.component.constants.What;
import com.goodsurfing.beans.ChildBean;
import com.goodsurfing.beans.ExpireBean;
import com.goodsurfing.beans.User;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.CommonUtil;

public class HczGetBindVipNet extends HczNetUtils {
    public HczGetBindVipNet(Context context, Handler handler) {
        super(context, Constants.HCZ_GETBINDVIP_URL, handler);
        maps.put("deviceToken", ActivityUtil.getDeviceID(context));
    }

    public void putParams() {
        maps.put("uid", Constants.userId);
        maps.put("Mobile",Constants.userMobile);
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
