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

public class HczGetBindCodeNet extends HczNetUtils {
    public HczGetBindCodeNet(Context context, Handler handler) {
        super(context, Constants.HCZ_GETBINDCODE_URL, handler);
    }

    public void putParams() {
        maps.put("uid", Constants.userId);
    }



    @Override
    protected void onHczSuccess(String result) {
        sendMessage( JSON.parseObject(result).getString("code"), What.HTTP_REQUEST_CURD_SUCCESS);
    }
    @Override
    protected void onHczFailure(String error) {

        sendMessage(error, What.HTTP_REQUEST_CURD_FAILURE);
    }
}
