package com.goodsurfing.server.net;

import android.content.Context;
import android.os.Handler;

import com.alibaba.fastjson.JSON;
import com.android.component.constants.What;
import com.goodsurfing.beans.City;
import com.goodsurfing.beans.IPList;
import com.goodsurfing.constants.Constants;

import java.util.List;

public class HczCheckCityNet extends HczNetUtils {
    public HczCheckCityNet(Context context, Handler handler) {
        super(context,"", Constants.HCZ_AREALIST_URL, handler);
    }

    public void putParams(String id) {
        maps.put("IspId", id);
    }

    @Override
    protected void onHczFailure(String error) {
        sendMessage(error, What.HTTP_REQUEST_CURD_FAILURE);
    }

    @Override
    protected void onHczSuccess(String result) {
        List<City> cityList = JSON.parseArray(result, City.class);
        Constants.cityStrList.clear();
        for (City city : cityList) {
            Constants.cityStrList.add(city.getProvinceName());
        }
        sendMessage(cityList, What.HTTP_REQUEST_CURD_SUCCESS);
    }
}
