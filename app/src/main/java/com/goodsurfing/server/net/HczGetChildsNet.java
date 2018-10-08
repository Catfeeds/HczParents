package com.goodsurfing.server.net;

import android.content.Context;
import android.os.Handler;

import com.alibaba.fastjson.JSON;
import com.android.component.constants.What;
import com.goodsurfing.beans.ChildBean;
import com.goodsurfing.beans.Parents;
import com.goodsurfing.beans.User;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.CommonUtil;
import com.goodsurfing.utils.SharUtil;

public class HczGetChildsNet extends HczNetUtils {
    public HczGetChildsNet(Context context, Handler handler) {
        super(context, Constants.HCZ_GETCHILD_URL, handler);
        maps.put("deviceToken", ActivityUtil.getDeviceID(context));
    }

    public void putParams() {
        maps.put("uid", Constants.userId);
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
           Constants.mode =Constants.child.getMode();
           SharUtil.saveLockScreenPsd(mContext,Constants.child.getLockPwd());
       }else {
           Constants.isbindChild=false;
           Constants.child =null;
           Constants.mode =1;
       }
        sendMessage(Constants.childs, What.HTTP_REQUEST_CURD_SUCCESS);
    }
}
