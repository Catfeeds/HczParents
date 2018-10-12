package com.goodsurfing.server.net;

import android.content.Context;
import android.os.Handler;

import com.alibaba.fastjson.JSON;
import com.android.component.constants.What;
import com.goodsurfing.beans.ChargeIDBean;
import com.goodsurfing.beans.ChildBean;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.SharUtil;

import java.util.List;

public class HczChargeListNet extends HczNetUtils {
    public HczChargeListNet(Context context, Handler handler) {
        super(context, Constants.HCZ_GETPACKAGE_URL, handler);
    }

    public void putParams() {
    }

    @Override
    protected void onHczFailure(String error) {
        sendMessage(error, What.HTTP_REQUEST_CURD_FAILURE);
    }

    @Override
    protected void onHczSuccess(String result) {
        List<ChargeIDBean> list= JSON.parseArray(result, ChargeIDBean.class);
       if(list!=null&&list.size()>0){
           sendMessage(list, What.HTTP_REQUEST_CURD_SUCCESS);
       }else {
           sendMessage("暂无套餐列表", What.HTTP_REQUEST_CURD_FAILURE);
       }

    }
}
