package com.goodsurfing.server.net;

import android.content.Context;
import android.os.Handler;

import com.alibaba.fastjson.JSON;
import com.android.component.constants.What;
import com.goodsurfing.beans.DynamicBean;
import com.goodsurfing.constants.Constants;

import java.util.List;

/**
 * 获取运动轨迹
 */
public class HczGetDynamicNet extends HczNetUtils {
    public HczGetDynamicNet(Context context, Handler handler) {
        super(context, Constants.HCZ_GETLOCATION_URL, handler);
    }

    /**
     *
     * @param beginTime
     * @param endTime
     * @param type 1只有位置，2使用时间，3安装卸载，4使用动态和安装卸载，5全部
     */
    public void putParams(long beginTime, long endTime,int 	type) {
        maps.put("clientId", Constants.child.getClientDeviceId()+"");
        maps.put("uid", Constants.userId);
        maps.put("beginTime",beginTime+"");
        maps.put("endTime", endTime+"");
        maps.put("type", 	type+"");
    }

    @Override
    protected void onHczFailure(String error) {
        sendMessage(error, What.HTTP_REQUEST_CURD_FAILURE);
    }

    @Override
    protected void onHczSuccess(String result) {
        List<DynamicBean> list = JSON.parseArray(result,DynamicBean.class);
        sendMessage(list, What.HTTP_REQUEST_CURD_SUCCESS);
    }
}
