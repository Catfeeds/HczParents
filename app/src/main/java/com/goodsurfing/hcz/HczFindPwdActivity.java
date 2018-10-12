package com.goodsurfing.hcz;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.component.constants.What;
import com.goodsurfing.app.R;
import com.goodsurfing.base.BaseActivity;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.server.net.HczFindPwdNet;
import com.goodsurfing.server.net.HczGetCodeNet;
import com.goodsurfing.service.RegisterCodeTimerService;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.RegisterCodeTimer;
import com.goodsurfing.utils.SharUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class HczFindPwdActivity extends BaseActivity {

    private final static String TAG = "FindPasswordActivity";
    private final static String CITY_KEY = "CITY_NAME";
    private final static String TYPE_KEY = "TYPE_NAME";
    protected static final int REFRESH = 100;

    private Context context;

    @ViewInject(R.id.activity_find_password_item_et_num)
    private EditText NumEditText;

    @ViewInject(R.id.activity_find_password_item_new_password)
    private EditText NewPwdEditText;

    @ViewInject(R.id.activity_find_passwrod_item_et_password)
    private EditText PwdEditText;

    @ViewInject(R.id.activity_register_item_et_code)
    private EditText codeEditText;

    @ViewInject(R.id.tv_title)
    private TextView title;

    @ViewInject(R.id.tv_title_right)
    private TextView right;

    @ViewInject(R.id.activity_register_item_tv_code)
    private TextView getCodeTv;

    @ViewInject(R.id.title_layout)
    private View title_layout;
    private Intent timerService;
    private static final int CODE_GET = 1;

    private int time = 60;
    private String mobile;
    private String code;
    private String password;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                // 正在倒计时
                case RegisterCodeTimer.IN_RUNNING:
                    getCodeTv.setEnabled(false);
                    getCodeTv.setBackgroundResource(R.drawable.add_children_gray);
                    getCodeTv.setText("已发送" + time-- + "秒");
                    if (time > 0)
                        mHandler.sendEmptyMessageDelayed(RegisterCodeTimer.IN_RUNNING, 1000);
                    else
                        mHandler.sendEmptyMessage(RegisterCodeTimer.END_RUNNING);
                    break;
                // 完成倒计时
                case RegisterCodeTimer.END_RUNNING:
                    getCodeTv.setBackgroundResource(R.drawable.view_bottom_button_bg);
                    mHandler.removeMessages(RegisterCodeTimer.IN_RUNNING);
                    getCodeTv.setEnabled(true);
                    getCodeTv.setText("重新获取");
                    break;
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hcz_find_pwd);
        ViewUtils.inject(this);
        init();
    }

    private void init() {
        context = this;
        title.setText("找回密码");
        right.setVisibility(View.INVISIBLE);
        timerService = new Intent(this, RegisterCodeTimerService.class);
        RegisterCodeTimerService.setHandler(mHandler);
        mobile = SharUtil.getPhone(context);
        if (mobile != null && !"".equals(mobile))
            NumEditText.setText(mobile);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick(R.id.activity_find_password_item_rl_login)
    public void loginClick(View view) {
            setPwd();
    }

    @OnClick(R.id.iv_title_left)
    public void onHeadBackClick(View view) {
        onBackPressed();
    }

    @OnClick(R.id.activity_register_item_tv_code)
    public void onGetCodeClick(View v) {
        doGetCode();
    }

    /**
     * 获取手机验证码
     */
    private void doGetCode() {
        String phoneNum = NumEditText.getText().toString();
        if (!ActivityUtil.isMobileNO(phoneNum)) {
            ActivityUtil.showPopWindow4Tips(this, title_layout, false, "请输入正确的手机号");
            getCodeTv.setEnabled(true);
            return;
        }
        getCodeTv.setEnabled(false);
        HczGetCodeNet getCodeNet = new HczGetCodeNet(this, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case What.HTTP_REQUEST_CURD_SUCCESS:
                        ActivityUtil.showPopWindow4Tips(HczFindPwdActivity.this, title_layout, true, "发送验证码成功");
                        time = 60;
                        mHandler.sendEmptyMessageDelayed(RegisterCodeTimer.IN_RUNNING, 0);
                        break;
                    case What.HTTP_REQUEST_CURD_FAILURE:
                        time = 60;
                        ActivityUtil.showPopWindow4Tips(HczFindPwdActivity.this, title_layout, false, "发送验证码失败");
                        mHandler.sendEmptyMessageDelayed(RegisterCodeTimer.END_RUNNING, 0);
                        break;
                }
            }
        });
        getCodeNet.putParams(phoneNum,3);
        getCodeNet.sendRequest();
    }

    private void setPwd() {
        mobile = NumEditText.getText().toString();
        code = codeEditText.getText().toString();
        if ("".equals(code) || !code.matches("^[0-9]{6}$")) {
            ActivityUtil.showPopWindow4Tips(this, title_layout, false, "验证码格式不正确");
            return;
        }
        password = NewPwdEditText.getText().toString();
        if (ActivityUtil.checkPassword(password) < 3) {
            ActivityUtil.showPopWindow4Tips(this, title_layout, false, "密码必须包含数字和字母");
            return;
        }
        String passwords = PwdEditText.getText().toString();
        if ("".equals(passwords)) {
            ActivityUtil.showPopWindow4Tips(this, title_layout, false, "请再次输入新密码");
            return;
        }
        if (!password.equals(passwords)) {
            ActivityUtil.showPopWindow4Tips(this, title_layout, false, "两次新密码输入不一致");
            return;
        }
        HczFindPwdNet findPwdNet = new HczFindPwdNet(this
                , new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case What.HTTP_REQUEST_CURD_SUCCESS:
                        ActivityUtil.showPopWindow4Tips(HczFindPwdActivity.this, title_layout, true, "找回密码成功");
                        Constants.isShowLogin =false;
                        HczLoginActivity.gotoLogin(HczFindPwdActivity.this);
                        onBackPressed();
                        break;
                    case What.HTTP_REQUEST_CURD_FAILURE:
                        ActivityUtil.showPopWindow4Tips(HczFindPwdActivity.this, title_layout, false, msg.obj.toString());
                        break;
                }
            }
        });
        findPwdNet.putParams(mobile,password,code);
        findPwdNet.sendRequest();

    }


}
