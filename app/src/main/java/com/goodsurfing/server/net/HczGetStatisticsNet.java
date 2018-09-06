package com.goodsurfing.server.net;

import android.content.Context;
import android.os.Handler;

import com.alibaba.fastjson.JSON;
import com.android.component.constants.What;
import com.goodsurfing.beans.DynamicBean;
import com.goodsurfing.beans.PieEntry;
import com.goodsurfing.constants.Constants;

import java.util.List;

/**
 * 获取首页 统计数据
 */
public class HczGetStatisticsNet extends HczNetUtils {
    public HczGetStatisticsNet(Context context, Handler handler) {
        super(context, Constants.HCZ_GETSTATISTICS_URL, handler);
    }

    /**
     * @param date 查询日期
     */
    public void putParams(String date) {
        maps.put("clientId", Constants.child.getClientDeviceId() + "");
        maps.put("uid", Constants.userId);
        maps.put("date", date);
    }

    @Override
    protected void onHczFailure(String error) {
        sendMessage(error, What.HTTP_REQUEST_CURD_FAILURE);
    }

    @Override
    protected void onHczSuccess(String result) {
        PieEntry pieEntry = JSON.parseObject(result, PieEntry.class);
        sendMessage(pieEntry, What.HTTP_REQUEST_CURD_SUCCESS);
    }
}
