package com.goodsurfing.server.net;

import android.content.Context;
import android.os.Handler;

import com.alibaba.fastjson.JSON;
import com.android.component.constants.What;
import com.goodsurfing.beans.ChildBean;
import com.goodsurfing.beans.User;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.CommonUtil;

public class HczGetBindChildNet extends HczNetUtils {
    public HczGetBindChildNet(Context context, Handler handler) {
        super(context, Constants.HCZ_GETBINDCHILD_URL, handler);
        maps.put("deviceToken", ActivityUtil.getDeviceID(context));
    }

    public void putParams() {
        maps.put("uid", Constants.userId);
        maps.put("clientId",Constants.child.getClientDeviceId()+"");
    }

    @Override
    protected void onHczFailure(String error) {
        sendMessage(error, What.HTTP_REQUEST_CURD_FAILURE);
    }

    @Override
    protected void onHczSuccess(String result) {
       Constants.childs= JSON.parseArray(result, ChildBean.class);
       if(Constants.childs!=null&&Constants.childs.size()>0){
           Constants.isbindChild=true;
           Constants.child = Constants.childs.get(0);
           User user = CommonUtil.getUser(mContext);
           Constants.mode =Constants.child.getMode();
           user.setMode(Constants.mode+"");
           user.setStatus(Constants.child.getStatus());
           CommonUtil.setUser(mContext,user);
       }
        sendMessage(result, What.HTTP_REQUEST_CURD_SUCCESS);
    }
}
