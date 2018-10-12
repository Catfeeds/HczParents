package com.goodsurfing.server.net;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.component.utils.Log;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.goodsurfing.app.HaoUpApplication;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.SignUtils;

import java.util.HashMap;
import java.util.Map;

public abstract class HczNetUtils {
    protected Context mContext;
    protected Map<String, String> maps = new HashMap<>();
    private String url;
    private Handler mHandler;
    private String data;
    private String action;

    public HczNetUtils(Context context, String serverPath, Handler handler) {
        mContext = context;
        mHandler = handler;
        action = serverPath;
        if (!serverPath.contains("https:")) {
        url = "http://s1.tensafe.net:9292" + serverPath;//线上
//            url = "http://172.16.0.201:9292" + serverPath;//线下
        }else {
            url= serverPath;
        }
    }

    protected void setParams() {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> set : maps.entrySet()) {
            sb.append(set.getKey() + "=" + set.getValue() + "&");
        }
        sb.append("UUID=" + ActivityUtil.getDeviceID(mContext));
        try {
            data = SignUtils.encrypt(sb.toString(), SignUtils.getPublicKey(Constants.HCZ_RSA_PUBLIC));
            android.util.Log.i("Request", url + "?" + sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
            data = "";
        }
    }

    public void sendRequest() {
        if (!Constants.isNetWork) {
            ActivityUtil.showPopWindow4Tips(mContext, new View(mContext), false, "当前网络不可用，请稍后再试...");
            return;
        }
        if (url.contains("http://172.16.0.201:9292")) {
            Toast.makeText(mContext, "线下版本", Toast.LENGTH_LONG).show();
        }
        setParams();
        if (HaoUpApplication.getInstance().mRequestQueue == null) {
            HaoUpApplication.getInstance().mRequestQueue = Volley.newRequestQueue(mContext);
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("onResponse", action + ">>" + response);
                try {
                    JSONObject result = JSON.parseObject(response);
                    if (result.getString("status").equals("ok")) {
                        onHczSuccess(result.getString("data"));
                    } else {
                        String errorCode = result.getString("message");
                        onHczFailure(HttpErrorCode.getCode2String(errorCode));
                    }
                } catch (Exception e) {
                    onHczFailure("服务器忙，请稍候再试");
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                onHczFailure("服务器忙，请稍候再试");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<>();
                map.put("data", data);
                return map;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(25000, 0, 2));
        //将post请求添加到队列中
        HaoUpApplication.getInstance().mRequestQueue.add(stringRequest);
    }

    protected abstract void onHczFailure(String error);

    protected abstract void onHczSuccess(String result);

    protected void sendMessage(Object obj, int what) {
        if (mHandler != null) {
            Message msg = Message.obtain();
            msg.what = what;
            if (obj != null) {
                msg.obj = obj;
            }
            mHandler.sendMessage(msg);
        }
    }
}
