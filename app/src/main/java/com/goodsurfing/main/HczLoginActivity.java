package com.goodsurfing.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.component.constants.What;
import com.goodsurfing.app.R;
import com.goodsurfing.base.BaseActivity;
import com.goodsurfing.beans.Parents;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.server.net.HczGetCodeNet;
import com.goodsurfing.server.net.HczLoginNet;
import com.goodsurfing.server.net.HczWXLoginNet;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.CommonUtil;
import com.goodsurfing.utils.RegisterCodeTimer;
import com.goodsurfing.utils.SharUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

public class HczLoginActivity extends BaseActivity {
    private final static String TAG = "LoginActivity";
    protected static final int REFRESH = 100;
    private Context context;
    private boolean login_ok = true;
    private String mobile;
    @ViewInject(R.id.activity_login_item_et_num)
    private EditText loginNumEditText;
    @ViewInject(R.id.activity_login_item_et_password)
    private EditText loginPwdEditText;
    @ViewInject(R.id.activity_login_delete_phone)
    private ImageView deleteIv;
    @ViewInject(R.id.activity_login_code_num)
    private EditText codeLoginNumEditText;
    @ViewInject(R.id.activity_login_code_password)
    private EditText codeLoginPwdEditText;
    @ViewInject(R.id.activity_login_code_delete_phone)
    private ImageView codeDeleteIv;
    @ViewInject(R.id.hcz_pwd_login_ll)
    private LinearLayout pwdLoginLl;
    @ViewInject(R.id.hcz_code_login_ll)
    private LinearLayout codeLoginLl;
    @ViewInject(R.id.activity_login_item_rl_login)
    private View loginBtn;
    @ViewInject(R.id.xieyi_text)
    private TextView xieyiView;
    @ViewInject(R.id.activity_code_login_getcode)
    private TextView codeView;
    @ViewInject(R.id.code_login)
    private TextView loginType;
    private int loginTypeNum = 1;
    private int time = 60;
    private Tencent mTencent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hcz_login);
        ViewUtils.inject(this);
        init();
    }

    public static void gotoLogin(Context context) {
        if (Constants.isShowLogin) {
            ActivityUtil.showPopWindow4Tips(context, new View(context), false, "请登录后再操作");
            return;
        }
        Intent intent = new Intent(context, HczLoginActivity.class);
        intent.putExtra("code", 0);
        (context).startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_remain);
    }

    public static void gotoLogin(Context context, int code) {
        if (Constants.isShowLogin) {
            ActivityUtil.showPopWindow4Tips(context, new View(context), false, "请登录后再操作");
            return;
        }
        Intent intent = new Intent(context, HczLoginActivity.class);
        intent.putExtra("code", code);
        (context).startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_remain);
    }

    private void init() {
        context = this;
        mobile = SharUtil.getPhone(context);
        Constants.isShowLogin = true;
        xieyiView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        Constants.clear(this);
        initLoginView(loginTypeNum);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharUtil.saveService(context, Constants.APP_USER_TYPE);
    }

    private void userLogin() {
        if (!Constants.isNetWork) {
            ActivityUtil.showPopWindow4Tips(this, loginNumEditText, false, "当前网络不可用，请稍后再试...");
            return;
        }
        // 防止重复点击
        if (CommonUtil.isFastDoubleClick())
            return;
        String account = "";
        String password = "";
        String code = "";
        if (loginTypeNum == 1) {
            account = loginNumEditText.getText().toString();
            password = loginPwdEditText.getText().toString();
            if ("".equals(password)) {
                ActivityUtil.showPopWindow4Tips(this, loginNumEditText, false, "请输入密码");
                return;
            } else if (!password.matches("[0-9a-zA-z]{6,20}")) {
                ActivityUtil.showPopWindow4Tips(this, loginNumEditText, false, "密码格式不正确，请输入不少于6位的密码");
                return;
            }
        } else {
            account = codeLoginNumEditText.getText().toString();
            code = codeLoginPwdEditText.getText().toString();
        }
        if ("".equals(account)) {
            ActivityUtil.showPopWindow4Tips(this, loginNumEditText, false, "请输入手机号");
            return;
        }
        HczLoginNet hczLoginNet = new HczLoginNet(this, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case What.HTTP_REQUEST_CURD_SUCCESS:
                        Parents parents = (Parents) msg.obj;
                        if (parents.getPassword() == null || parents.getPassword().equals("")) {
                            startActivity(new Intent(HczLoginActivity.this, HczSetPwdActivity.class));
                        } else {
                            ActivityUtil.goMainActivity(HczLoginActivity.this);
                        }
                        onBackPressed();
                        break;
                    case What.HTTP_REQUEST_CURD_FAILURE:
                        ActivityUtil.showPopWindow4Tips(HczLoginActivity.this, loginNumEditText, false, msg.obj.toString() + "");
                        break;
                }
            }
        });
        hczLoginNet.putParams(account, code, password);
        hczLoginNet.sendRequest();
    }

    /**
     * 根据不同类型显示code登陆或者密码登录
     *
     * @param type =1 密码登录
     */
    private void initLoginView(int type) {
        if (type == 1) {
            codeLoginLl.setVisibility(View.GONE);
            pwdLoginLl.setVisibility(View.VISIBLE);
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
            if (mobile != null && !"".equals(mobile)) {
                loginNumEditText.setText(mobile);
                loginNumEditText.setSelection(mobile.length());
            }
            loginType.setText("手机动态码登录");
        } else {
            pwdLoginLl.setVisibility(View.GONE);
            codeLoginLl.setVisibility(View.VISIBLE);
            codeLoginNumEditText.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence chars, int arg1, int arg2, int arg3) {
                    if (chars.length() > 0) {
                        codeDeleteIv.setVisibility(View.VISIBLE);
                    } else {
                        codeDeleteIv.setVisibility(View.GONE);
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                }

                @Override
                public void afterTextChanged(Editable arg0) {
                }
            });
            codeLoginPwdEditText.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                    if (arg0.length() == 6) {
                        loginBtn.setEnabled(true);
                        loginBtn.setBackgroundResource(R.drawable.view_bottom_button_bg);
                    } else {
                        loginBtn.setEnabled(false);
                        loginBtn.setBackgroundResource(R.drawable.view_btn_gray__bg);
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                }

                @Override
                public void afterTextChanged(Editable arg0) {

                }
            });
            if (mobile != null && !"".equals(mobile)) {
                codeLoginNumEditText.setText(mobile);
                codeLoginNumEditText.setSelection(mobile.length());
            }
            loginType.setText("账号密码登录");
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                // 正在倒计时
                case RegisterCodeTimer.IN_RUNNING:
                    codeView.setEnabled(false);
                    codeView.setBackgroundResource(R.drawable.add_children_gray);
                    codeView.setText("已发送" + time-- + "秒");
                    if (time > 0)
                        mHandler.sendEmptyMessageDelayed(RegisterCodeTimer.IN_RUNNING, 1000);
                    else
                        mHandler.sendEmptyMessage(RegisterCodeTimer.END_RUNNING);
                    break;

                // 完成倒计时
                case RegisterCodeTimer.END_RUNNING:
                    codeView.setBackgroundResource(R.drawable.view_bottom_button_bg);
                    mHandler.removeMessages(RegisterCodeTimer.IN_RUNNING);
                    codeView.setEnabled(true);
                    codeView.setText("重新获取");
                    break;
            }
        }

        ;
    };

    private void doGetCode() {
        if (!Constants.isNetWork) {
            ActivityUtil.showPopWindow4Tips(this, codeLoginNumEditText, false, "当前网络不可用，请稍后再试...");
            codeView.setEnabled(true);
            return;
        }
        String phoneNum = codeLoginNumEditText.getText().toString();
        if ("".equals(phoneNum) || !phoneNum.matches("^[1-9]\\d{10}$")) {
            ActivityUtil.showPopWindow4Tips(this, codeLoginNumEditText, false, "请输入正确的手机号");
            codeView.setEnabled(true);
            return;
        }
        HczGetCodeNet getCodeNet = new HczGetCodeNet(this, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case What.HTTP_REQUEST_CURD_SUCCESS:
                        ActivityUtil.showPopWindow4Tips(HczLoginActivity.this, codeLoginNumEditText, true, "发送验证码成功");
                        time = 60;
                        mHandler.sendEmptyMessageDelayed(RegisterCodeTimer.IN_RUNNING, 0);
                        break;
                    case What.HTTP_REQUEST_CURD_FAILURE:
                        time = 60;
                        ActivityUtil.showPopWindow4Tips(HczLoginActivity.this, codeLoginNumEditText, false, "发送验证码失败");
                        mHandler.sendEmptyMessageDelayed(RegisterCodeTimer.END_RUNNING, 0);
                        break;
                }
            }
        });
        getCodeNet.putParams(phoneNum,1);
        getCodeNet.sendRequest();
    }

    @OnClick(R.id.activity_login_delete_phone)
    public void onClickDeletePhone(View view) {
        loginNumEditText.setText("");
    }

    @OnClick(R.id.activity_login_delete_phone)
    public void onClickDeleteCodePhone(View view) {
        codeLoginNumEditText.setText("");
    }


    @OnClick(R.id.activity_login_item_rl_login)
    public void loginClick(View view) {
        if (login_ok) {
            userLogin();
        }
    }

    @OnClick(R.id.xieyi_text)
    public void onXieyiTextClick(View view) {
        ActivityUtil.showXyDialog(context);
    }

    @OnClick(R.id.activity_login_item_forget_tv)
    public void onFindPasswrodClick(View view) {
        Intent intent = new Intent(this, HczFindPwdActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.goto_hsw_login)
    public void onClickGotoHswLogin(View view) {
        Constants.isShowLogin = false;
      LoginActivity.gotoLogin(this);
    }

    @OnClick(R.id.activity_code_login_getcode)
    public void onClickGetCode(View view) {
        doGetCode();
    }

    @OnClick(R.id.code_login)
    public void onClickLoginType(View view) {
        String text = ((TextView) view).getText().toString();
        if (text.equals("手机动态码登录")) {
            loginTypeNum = 2;
        } else {
            loginTypeNum = 1;
        }
        initLoginView(loginTypeNum);
    }

    @OnClick(R.id.weixin_login_btn)
    public void onWeixinLogin(View v){
        new ActivityUtil().weixinLogin(this,new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case What.HTTP_REQUEST_CURD_SUCCESS:
                        otherLogin(Constants.UserId,"weixin");
                        break;
                    case What.HTTP_REQUEST_CURD_FAILURE:
                        ActivityUtil.showPopWindow4Tips(HczLoginActivity.this, codeLoginNumEditText, false, "微信登录失败");
                        break;
                }
            }
        });
    }

    /**
     * qq登录
     */
    @OnClick(R.id.qq_login_btn)
    public void onQQLogin(View v) {
        mTencent=Tencent.createInstance(Constants.QQ_API_KEY,context);
        mTencent.login(this, "all",iUiListener );
    }

    private void otherLogin(String userId, String type) {
        HczWXLoginNet otherLoginNet = new HczWXLoginNet(this, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case What.HTTP_REQUEST_CURD_SUCCESS:
                        int type= (int) msg.obj;
                        if (type == 2) {
                            startActivity(new Intent(HczLoginActivity.this, HczBindActivity.class));
                        }else{
                            ActivityUtil.showPopWindow4Tips(HczLoginActivity.this, codeLoginNumEditText, true, "登录成功");
                            ActivityUtil.goMainActivity(HczLoginActivity.this);
                        }
                        break;
                    case What.HTTP_REQUEST_CURD_FAILURE:
                        ActivityUtil.showPopWindow4Tips(HczLoginActivity.this, codeLoginNumEditText, false, "登录失败");
                        break;
                }
            }
        });
        otherLoginNet.putParams(userId, type);
        otherLoginNet.sendRequest();
    }

    private IUiListener iUiListener =new IUiListener() {
        @Override
        public void onComplete(Object response) {
            if (null == response) {
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;
            if (null != jsonResponse && jsonResponse.length() == 0) {
                return;
            }
            try {
                Log.i(TAG, "onComplete: "+jsonResponse.toString());
                Constants.UserId= jsonResponse.getString("openid");
                String access_token = jsonResponse.getString("access_token");
                String expires = jsonResponse.getString("expires_in");
                mTencent.setOpenId(Constants.UserId);
                mTencent.setAccessToken(access_token, expires);
                QQToken qqToken = mTencent.getQQToken();
                UserInfo info = new  UserInfo(HczLoginActivity.this,qqToken);
                info.getUserInfo(new IUiListener() {
                    @Override
                    public void onComplete(Object response) {
                        JSONObject jo = (JSONObject) response;
                        try {
                            Constants.UserName = jo.getString("nickname");
                            Constants.UserHeadUrl = jo.getString("figureurl_1");
                            otherLogin(Constants.UserId,"qq");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(UiError uiError) {

                    }

                    @Override
                    public void onCancel() {

                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
                ActivityUtil.showPopWindow4Tips(HczLoginActivity.this, codeLoginNumEditText, false, "QQ登录失败");
            }
        }

        @Override
        public void onError(UiError uiError) {
            Log.i(TAG, "onComplete: "+uiError.toString());
            ActivityUtil.showPopWindow4Tips(HczLoginActivity.this, codeLoginNumEditText, false, "QQ登录失败");
        }

        @Override
        public void onCancel() {
            ActivityUtil.showPopWindow4Tips(HczLoginActivity.this, codeLoginNumEditText, false, "QQ登录取消");
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == com.tencent.connect.common.Constants.REQUEST_LOGIN) {
            Tencent.onActivityResultData(requestCode,resultCode,data,iUiListener);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
