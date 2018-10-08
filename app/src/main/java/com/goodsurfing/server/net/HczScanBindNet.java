package com.goodsurfing.server.net;

import android.content.Context;
import android.os.Handler;

import com.alibaba.fastjson.JSON;
import com.android.component.constants.What;
import com.goodsurfing.beans.ChildBean;
import com.goodsurfing.beans.User;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.CommonUtil;

/**
 * 家长扫码关联孩子
 */
public class HczScanBindNet extends HczNetUtils {
    public HczScanBindNet(Context context, Handler handler) {
        super(context, Constants.HCZ_BINDCHILD_URL, handler);
        maps.put("deviceToken", ActivityUtil.getDeviceID(context));
    }

    /**
     * uid:家长ServerId
     * UUID：  孩子
     * localMobile:孩子手机
     * device 孩子的
     * system 孩子的
     */
    public void putParams(String UUID,String localMobile,String device,String system) {
        maps.put("uid", Constants.userId);
        maps.put("childUUID",UUID);
        maps.put("localMobile",localMobile);
        maps.put("device",device);
        maps.put("system",system);
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
