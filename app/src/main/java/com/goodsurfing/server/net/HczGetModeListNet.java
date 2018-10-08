package com.goodsurfing.server.net;

import android.content.Context;
import android.os.Handler;

import com.alibaba.fastjson.JSON;
import com.android.component.constants.What;
import com.goodsurfing.beans.DynamicBean;
import com.goodsurfing.beans.ModeBean;
import com.goodsurfing.constants.Constants;

import java.util.List;

/**
 *获取家长模式列表
 */
public class HczGetModeListNet extends HczNetUtils {
    public HczGetModeListNet(Context context, Handler handler) {
        super(context, Constants.HCZ_GETMODELIST_URL, handler);
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
        List<ModeBean> list = JSON.parseArray(result,ModeBean.class);
        for (ModeBean bean :list){
           switch (bean.getModeId()){
               case Constants.MODE_BAD:
                   Constants.MODE_BAD_TEXT = bean.getName();
                   break;
               case Constants.MODE_FREE:
                   Constants.MODE_FREE_TEXT = bean.getName();
                   break;
               case Constants.MODE_LEARNING:
                   Constants.MODE_LEARNING_TEXT = bean.getName();
                   break;
               case Constants.MODE_REWARDS:
                   Constants.MODE_REWARDS_TEXT = bean.getName();
                   break;
           }
        }
        sendMessage(list, What.HTTP_REQUEST_CURD_SUCCESS);
    }
}
