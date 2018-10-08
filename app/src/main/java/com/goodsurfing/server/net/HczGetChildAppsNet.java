package com.goodsurfing.server.net;

import android.content.Context;
import android.os.Handler;

import com.alibaba.fastjson.JSON;
import com.android.component.constants.What;
import com.goodsurfing.beans.AppBean;
import com.goodsurfing.constants.Constants;

import java.util.List;

/**
 * 获取孩子手机安装的APP
 */
public class HczGetChildAppsNet extends HczNetUtils {
    public HczGetChildAppsNet(Context context, Handler handler) {
        super(context, Constants.HCZ_GETAPPLIST_URL, handler);
    }

    /**
     * apply =1 申请使用的APP =0 是全部列表
     *
     * @param apply
     */
    public void putParams(String apply) {
        maps.put("clientId", Constants.child.getClientDeviceId() + "");
        maps.put("uid", Constants.userId);
        if(!apply.equals(""))
        maps.put("apply", apply);
    }

    @Override
    protected void onHczFailure(String error) {
        sendMessage(error, What.HTTP_REQUEST_CURD_FAILURE);
    }

    @Override
    protected void onHczSuccess(String result) {
        List<AppBean> data = JSON.parseArray(result, AppBean.class);
        sendMessage(data, What.HTTP_REQUEST_CURD_SUCCESS);
    }
}
