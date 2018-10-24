package com.goodsurfing.server.net;

import android.content.Context;
import android.os.Handler;

import com.alibaba.fastjson.JSON;
import com.android.component.constants.What;
import com.goodsurfing.beans.ExpireBean;
import com.goodsurfing.constants.Constants;

/**
 * 获取到期时间提醒
 */
public class HczGetExpiredateNet extends HczNetUtils {
    public HczGetExpiredateNet(Context context, Handler handler) {
        super(context,"", Constants.HCZ_INTERFACES_URL, handler);
    }

    /**
     */
    public void putParams(String mobile) {
        maps.put("mobile",mobile);
    }

    @Override
    protected void onHczFailure(String error) {
//        sendMessage(error, What.HTTP_REQUEST_CURD_FAILURE);
    }

    @Override
    protected void onHczSuccess(String result) {
        sendMessage(JSON.parseObject(result, ExpireBean.class), What.HTTP_REQUEST_CURD_SUCCESS);
    }
}
