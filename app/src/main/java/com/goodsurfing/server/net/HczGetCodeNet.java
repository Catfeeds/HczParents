package com.goodsurfing.server.net;

import android.content.Context;
import android.os.Handler;

import com.alibaba.fastjson.JSON;
import com.android.component.constants.What;
import com.goodsurfing.beans.Parents;
import com.goodsurfing.beans.User;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.CommonUtil;
import com.goodsurfing.utils.SharUtil;

/**
 * 获取动态码
 */
public class HczGetCodeNet extends HczNetUtils {
    public HczGetCodeNet(Context context, Handler handler) {
        super(context, Constants.HCZ_GETCODE_URL, handler);
    }

    /**
     * 类型1=登录;2=注册
     * @param type
     */
    public void putParams(String mobile,int type) {
        maps.put("mobile",mobile);
        maps.put("type", type+"");
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
