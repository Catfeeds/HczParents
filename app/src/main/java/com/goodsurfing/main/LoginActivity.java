package com.goodsurfing.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.component.constants.What;
import com.goodsurfing.app.R;
import com.goodsurfing.base.BaseActivity;
import com.goodsurfing.beans.User;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.server.CheckServerServer;
import com.goodsurfing.server.GetServerListServer;
import com.goodsurfing.server.LoginServer;
import com.goodsurfing.server.net.HczLoginNet;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResponder;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResult;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.CommonUtil;
import com.goodsurfing.utils.SharUtil;
import com.goodsurfing.view.customview.ServiceOrTypeDialog;
import com.goodsurfing.view.customview.ZjWheelView.OnWheelViewListener;
import com.goodsurfing.view.customview.city.LocationDialogBuilder;
import com.goodsurfing.view.customview.city.LocationDialogBuilder.OnSaveLocationLister;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class LoginActivity extends BaseActivity implements DataServiceResponder {
    private final static String TAG = "LoginActivity";
    @ViewInject(R.id.tv_title)
    private TextView title;
    @ViewInject(R.id.tv_title_right)
    private TextView right;
    protected static final int REFRESH = 100;
    private static final int WX_USERINFO = 102;
    private static final int WX_LOGIN_FAILURE = 103;
    private Context context;
    @ViewInject(R.id.activity_login_item_et_num)
    private EditText loginNumEditText;
    @ViewInject(R.id.activity_login_item_et_password)
    private EditText loginPwdEditText;
    @ViewInject(R.id.activity_login_item_adress_tv)
    private TextView cityTextView;
    @ViewInject(R.id.activity_login_item_service_tv)
    private TextView serviceTextView;
    @ViewInject(R.id.activity_add_children_delete_phone)
    private ImageView deleteIv;
    @ViewInject(R.id.activity_login_item_rl_register)
    private View registerLyout;
    @ViewInject(R.id.activity_login_item_forget_tv1)
    private View registerTipsTv;
    @ViewInject(R.id.activity_login_item_line)
    private View registerLineView;
    @ViewInject(R.id.check_xieyi)
    private CheckBox checkBox;
    @ViewInject(R.id.title_layout)
    private View title_layout;
    private boolean login_ok = true;
    private String mobile;
    private String cityName;
    private String serviceName;
    private int startCode;
    private Dialog dialog;
    private IWXAPI api;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case REFRESH:
                    break;
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ViewUtils.inject(this);
        init();
        Constants.isShowLogin = true;
    }

    public static void gotoLogin(Context context) {
        Constants.clear(context);
        ActivityUtil.showPopWindow4Tips(context, new View(context), false, "请登录后再操作");
        return;
//        Intent intent = new Intent(context, LoginActivity.class);
//        intent.putExtra("code", 0);
//        (context).startActivity(intent);
//        ((Activity) context).overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_remain);
    }

    public static void gotoLogin(Context context, int code) {
        Constants.clear(context);
        ActivityUtil.showPopWindow4Tips(context, new View(context), false, "请登录后再操作");
        return;
//        Intent intent = new Intent(context, LoginActivity.class);
//        intent.putExtra("code", code);
//        (context).startActivity(intent);
//        ((Activity) context).overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_remain);
    }

    @OnClick(R.id.tv_title_right)
    private void titleRight(View view) {
        ActivityUtil.callQQkefu(this, title_layout);
    }

    @OnClick(R.id.activity_add_children_delete_phone)
    private void onClickDeletePhone(View view) {
        loginNumEditText.setText("");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void init() {
        context = this;
        title.setText("登录");
        right.setText("QQ客服");
        right.setVisibility(View.VISIBLE);
        startCode = getIntent().getExtras().getInt("code");
        if (startCode == 2) {
            ActivityUtil.showPopWindow4Tips(this, title_layout, false, "请重新登录");
        }
        loginNumEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence chars, int arg1, int arg2, int arg3) {
                if (chars.length() > 0) {
                    deleteIv.setVisibility(View.VISIBLE);
                } else {
                    deleteIv.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void afterTextChanged(Editable arg0) {

            }
        });
        getSavedDatas();
        cityTextView.setText(cityName);
        serviceTextView.setText(serviceName);
        // checkLocationInfo(cityName, serviceName);
        if (mobile != null && !"".equals(mobile)) {
            loginNumEditText.setText(mobile);
            loginNumEditText.setSelection(mobile.length());
        }
        checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                if (arg1) {
                    login_ok = true;
                } else {
                    login_ok = false;
                }
            }
        });
        Constants.clear(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!Constants.isRegistShow) {
            registerLyout.setVisibility(View.GONE);
            registerTipsTv.setVisibility(View.GONE);
            registerLineView.setVisibility(View.GONE);
        }
    }

    private void getServerList() {
//        String url = null;
//        url = Constants.SERVER_URL_GLOBAL + "?" + "requesttype=1002";
//
//        new GetServerListServer(new DataServiceResponder() {
//
//            @Override
//            public void onResult(DataServiceResult result) {
//                if (result == null || result.code == null) {
//                    return;
//                }
//                if ("0".equals(result.code)) {
//                } else {
//                    ActivityUtil.showPopWindow4Tips(LoginActivity.this, title_layout, false, result.extra + "");
//                }
//            }
//
//            @Override
//            public void onFailure() {
//            }
//        }, url, this).execute();
    }

    @OnClick(R.id.activity_login_item_service)
    public void doService(View view) {
        doServiceDialog();
    }

    @OnClick(R.id.iv_title_left)
    public void onHeadBackClick(View view) {
        onBackPressed();
//		MainActivity.mTabHost.setCurrentTab(0);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//			MainActivity.mTabHost.setCurrentTab(0);
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    private void doServiceDialog() {
        ServiceOrTypeDialog dialog = ServiceOrTypeDialog.getInstance(this);
        dialog.setDialogProperties("选择运营商", Constants.serverList);
        dialog.setOnSaveServiceLister(new OnWheelViewListener() {

            @Override
            public void onSelected(int selectedIndex, String item) {
                if (cityName == null || "".equals(cityName)) {
                    ActivityUtil.showPopWindow4Tips(LoginActivity.this, title_layout, false, "请选择所在地");
                    return;
                }
                serviceName = item;
                serviceTextView.setText(serviceName);
                SharUtil.saveService(context, serviceName);

            }
        });
        dialog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @SuppressLint("WrongConstant")
    private void getLoginFeedBack() {
        if (!Constants.isNetWork) {
            ActivityUtil.showPopWindow4Tips(this, title_layout, false, "当前网络不可用，请稍后再试...");
            return;
        }
        // 防止重复点击
        if (CommonUtil.isFastDoubleClick())
            return;
        final String account = loginNumEditText.getText().toString();
        if ("".equals(account)) {
            ActivityUtil.showPopWindow4Tips(this, title_layout, false, "请输入宽带账号或绑定手机号");
            return;
        }

        String password = loginPwdEditText.getText().toString();
        if ("".equals(password)) {
            ActivityUtil.showPopWindow4Tips(this, title_layout, false, "请输入密码");
            return;
        } else if (!password.matches("[0-9a-zA-z]{6,20}")) {
            ActivityUtil.showPopWindow4Tips(this, title_layout, false, "密码格式不正确，请输入不少于6位的密码");
            return;
        }
        getSharedPreferences(Constants.LOGIN_INFO, Activity.MODE_APPEND).edit().putString(Constants.LOGIN_PASS, password).commit();
        String mdPassword = null;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            mdPassword = getString(md.digest(password.getBytes()));
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }

        int position = 1;// 家庭宽带 用户类型
        String modle = android.os.Build.MODEL;
        if ("".equals(modle) || modle == null)
            modle = "huawei";
        if (Constants.SERVER_URL.equals(Constants.SERVER_URL_GLOBAL)) {
            ActivityUtil.showPopWindow4Tips(this, title_layout, false, "运营商未开通");
            return;
        }
        if (SharUtil.getService(context).equals(Constants.APP_USER_TYPE)) {
            HczLoginNet hczLoginNet = new HczLoginNet(this, new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case What.HTTP_REQUEST_CURD_SUCCESS:
                            SharUtil.savePhone(context, Constants.Account);
                            SharUtil.saveCacheTime(context, Constants.CACHE_TIME, System.currentTimeMillis() + "");
                            ActivityUtil.goMainActivity(LoginActivity.this);
                            onBackPressed();
                            break;
                        case What.HTTP_REQUEST_CURD_FAILURE:
                            ActivityUtil.showPopWindow4Tips(LoginActivity.this, title_layout, false, msg.obj.toString() + "");
                            break;
                    }
                }
            });
            hczLoginNet.putParams(account, "", password);
            hczLoginNet.sendRequest();
        } else {
            String url = Constants.SERVER_URL + "?" + "user=" + account + "&passwd=" + mdPassword + "&usertype=" + position + "&mobile=" + modle + "&usertype=1" + "&deviceID=" + ActivityUtil.getDeviceToken(this);
            new LoginServer(this, url, this).execute();
        }
    }


    private static String getString(byte[] encryption) {
        StringBuffer strBuf = new StringBuffer();
        for (int i = 0; i < encryption.length; i++) {
            if (Integer.toHexString(0xff & encryption[i]).length() == 1) {
                strBuf.append("0").append(Integer.toHexString(0xff & encryption[i]));
            } else {
                strBuf.append(Integer.toHexString(0xff & encryption[i]));
            }
        }

        return strBuf.toString();
    }

    @OnClick(R.id.activity_login_item_rl_login)
    public void loginClick(View view) {
        if (login_ok) {
            checkLocationInfo(cityName, serviceName);
        } else {
            ActivityUtil.showPopWindow4Tips(this, title_layout, false, "请先勾选用户使用协议");
        }

    }

    @OnClick(R.id.xieyi_text)
    public void onXieyiTextClick(View view) {
        ActivityUtil.showXyDialog(context);
    }

    @OnClick(R.id.activity_login_item_rl_register)
    public void onRegisterClick(View view) {
        RegisterActivity.gotoRegister(this);
    }

    @OnClick(R.id.activity_login_item_adress_iv)
    public void onPositionClick(View v) {
        doCity();
    }

    @OnClick(R.id.activity_login_item_forget_tv)
    public void onFindPasswrodClick(View view) {
        Intent intent = new Intent(this, FindPasswordActivity.class);
        startActivity(intent);
    }

    private void checkLocationInfo(String province, String service) {
        if (!Constants.isNetWork) {
            ActivityUtil.showPopWindow4Tips(this, title_layout, false, "当前网络不可用，请稍后再试...");
            return;
        }
        if (null == service || null == province || province.equals("") || service.equals("")) {
            ActivityUtil.showPopWindow4Tips(this, title_layout, false, "请选择运营商和地址");
            return;
        }
        String url = null;
        url = Constants.SERVER_URL_GLOBAL + "?" + "requesttype=1004" + "&area=" + province + "&provider=" + service;
        ActivityUtil.showPopWindow4Tips(this, title_layout, false, false, "正在登录...", -1);
        new CheckServerServer(new DataServiceResponder() {

            @Override
            public void onResult(DataServiceResult result) {
                if (result == null || result.code == null) {
                    return;
                }
                if (result.code.equals("0")) {
                    getLoginFeedBack();
                } else {
                    Constants.SERVER_URL = Constants.SERVER_URL_GLOBAL;
                    ActivityUtil.showPopWindow4Tips(LoginActivity.this, title_layout, false, result.extra + "");
                }
            }

            @Override
            public void onFailure() {
                Constants.SERVER_URL = Constants.SERVER_URL_GLOBAL;
            }
        }, url, this).execute();
    }

    private void getSavedDatas() {
        mobile = SharUtil.getPhone(context);
        cityName = SharUtil.getCity(context);
        serviceName = SharUtil.getService(context);
    }

    protected void doCity() {
        LocationDialogBuilder locationDialog = LocationDialogBuilder.getInstance(this);
        locationDialog.setOnSaveLocationLister(new OnSaveLocationLister() {
            @Override
            public void onSaveLocation(String province, String city, String provinceId, String cityId) {
                cityTextView.setText(city);
                LoginActivity.this.cityName = city;
                SharUtil.saveCity(context, city);
                if (city == null || "".equals(city)) {
                    ActivityUtil.showPopWindow4Tips(LoginActivity.this, title_layout, false, "请选择所在地");
                    return;
                }
                getServerList();
            }
        });
        locationDialog.show();
    }

    @Override
    public void onFailure() {
        ActivityUtil.showPopWindow4Tips(LoginActivity.this, title_layout, false, "服务器忙，请稍候再试");
    }

    @Override
    public void onResult(DataServiceResult result) {
        if (result == null || result.code == null) {
            return;
        }
        if (result.code.equals("0")) {
            Constants.cityName = cityTextView.getText().toString().trim();
            Constants.prodiver = serviceTextView.getText().toString();
            SharUtil.saveMode(context, Constants.mode);
            SharUtil.savePhone(context, Constants.userMobile);
            SharUtil.saveCacheTime(context, Constants.CACHE_TIME, System.currentTimeMillis() + "");
            ActivityUtil.goMainActivity(LoginActivity.this);
            onBackPressed();
        } else {
            ActivityUtil.showPopWindow4Tips(LoginActivity.this, title_layout, false, result.extra + "");
        }
    }

}
