package com.goodsurfing.server.net;

import android.content.Context;
import android.os.Handler;

import com.alibaba.fastjson.JSON;
import com.android.component.constants.What;
import com.goodsurfing.beans.ChargeIDBean;
import com.goodsurfing.beans.OrederBean;
import com.goodsurfing.constants.Constants;

import java.util.List;

/**
 * 创建订单
 */
public class HczCreatOrderNet extends HczNetUtils {
    public HczCreatOrderNet(Context context, Handler handler) {
        super(context, Constants.HCZ_CREATEORDER_URL, handler);
    }

    /**
     * 'uid' => ['Required'],
     * 'PackageName' => ['Required'],  套餐名称
     * 'PackageId' => ['Required'],	套餐id
     * 'Price' => ['Required'],
     */
    public void putParams(String PackageName, String PackageId, String Price) {
        maps.put("uid", Constants.userId);
        maps.put("PackageName", PackageName);
        maps.put("PackageId", PackageId);
        maps.put("Price", Price);
    }

    @Override
    protected void onHczFailure(String error) {
        sendMessage(error, What.HTTP_REQUEST_CURD_FAILURE);
    }

    @Override
    protected void onHczSuccess(String result) {
        OrederBean orederBean = JSON.parseObject(result, OrederBean.class);
        sendMessage(orederBean, What.HTTP_REQUEST_CURD_SUCCESS);

    }
}
