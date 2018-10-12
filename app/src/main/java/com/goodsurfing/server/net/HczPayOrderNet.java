package com.goodsurfing.server.net;

import android.content.Context;
import android.os.Handler;

import com.alibaba.fastjson.JSON;
import com.android.component.constants.What;
import com.goodsurfing.beans.OrederBean;
import com.goodsurfing.constants.Constants;

/**
 *支付订单
 */
public class HczPayOrderNet extends HczNetUtils {
    public HczPayOrderNet(Context context, Handler handler) {
        super(context, Constants.HCZ_NOTIFYORDER_URL, handler);
    }

    /**
     'TransId' => ['Required'],//订单号
     'PayType' => ['Required'],//支付方式
     'PayOpenid' => ['Required'],//用户标识openid
     'PayStatus' => ['Required'],//2=支付成功;3=支付失败;4=订单超时
     'NonceStr' => ['Required'],//随机字符串 32位
     */
    public void putParams(String TransId, String PayType,String PayOpenid, String PayStatus,String NonceStr) {
        maps.put("uid", Constants.userId);
        maps.put("TransId", TransId);
        maps.put("PayType", PayType);
        maps.put("PayOpenid", PayOpenid);
        maps.put("PayStatus", PayStatus);
        maps.put("NonceStr", NonceStr);
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
