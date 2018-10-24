package com.goodsurfing.server.net;

import android.content.Context;
import android.os.Handler;

import com.alibaba.fastjson.JSON;
import com.android.component.constants.What;
import com.goodsurfing.beans.City;
import com.goodsurfing.beans.IPList;
import com.goodsurfing.constants.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.goodsurfing.constants.Constants.serverList;

public class HczGetServerNet extends HczNetUtils {
    public HczGetServerNet(Context context, Handler handler) {
        super(context,"", Constants.HCZ_ISPLIST_URL, handler);
    }

    public void putParams() {
    }

    @Override
    protected void onHczFailure(String error) {
        sendMessage(error, What.HTTP_REQUEST_CURD_FAILURE);
    }

    @Override
    protected void onHczSuccess(String result) {
        List<IPList> ipLists = JSON.parseArray(result, IPList.class);
        List<String> serverList = new ArrayList<>();
        HashMap<String, List<City>> serverCityMap = new HashMap<>();
        for (IPList ip : ipLists) {
            serverList.add(ip.getName());
            serverCityMap.put(ip.getName(),ip.getIsplist());
        }
        Constants.serverCityMap = serverCityMap;
        Constants.serverList = serverList;
        sendMessage(ipLists, What.HTTP_REQUEST_CURD_SUCCESS);
    }
}
