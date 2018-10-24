package com.goodsurfing.server.net;

import android.content.Context;
import android.os.Handler;

import com.alibaba.fastjson.JSON;
import com.android.component.constants.What;
import com.goodsurfing.beans.City;
import com.goodsurfing.beans.FuncBean;
import com.goodsurfing.beans.IPList;
import com.goodsurfing.constants.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HczAppFuncNet extends HczNetUtils {
    public HczAppFuncNet(Context context, Handler handler) {
        super(context,"", Constants.HCZ_APPFUNC_URL, handler);
    }

    public void putParams(String ipId) {
        maps.put("IspId", ipId);
    }

    @Override
    protected void onHczFailure(String error) {
        sendMessage(error, What.HTTP_REQUEST_CURD_FAILURE);
    }

    @Override
    protected void onHczSuccess(String result) {
        List<FuncBean> funcBeans = JSON.parseArray(result, FuncBean.class);
        Constants.funcBeans.clear();
        for (FuncBean funcBean : funcBeans) {
            Constants.funcBeans.put(funcBean.getFuncName(), funcBean.isOpen());
        }
        sendMessage("", What.HTTP_REQUEST_CURD_SUCCESS);
    }
}
