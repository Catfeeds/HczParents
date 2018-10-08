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
import com.android.component.utils.StringUtil;
import com.goodsurfing.app.R;
import com.goodsurfing.base.BaseActivity;
import com.goodsurfing.beans.Parents;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.server.net.HczGetCodeNet;
import com.goodsurfing.server.net.HczLoginNet;
import com.goodsurfing.server.net.HczThreeBindNet;
import com.goodsurfing.server.net.HczWXLoginNet;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.CommonUtil;
import com.goodsurfing.utils.RegisterCodeTimer;
import com.goodsurfing.utils.SharUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 好成长第三方登录绑定手机
 */
public class HczBindActivity extends BaseActivity {
    private final static String TAG = "HczBindActivity";
    protected static final int REFRESH = 100;
    private Context context;
    private boolean login_ok = true;
    private String mobile;
    @ViewInject(R.id.tv_title)
    private TextView title;
    @ViewInject(R.id.tv_title_right)
    private TextView right;
    @ViewInject(R.id.activity_login_code_num)
    private EditText codeLoginNumEditText;
    @ViewInject(R.id.activity_login_code_password)
    private EditText codeLoginPwdEditText;
    @ViewInject(R.id.activity_login_code_delete_phone)
    private ImageView codeDeleteIv;
    @ViewInject(R.id.activity_code_login_getcode)
    private TextView codeView;

    @ViewInject(R.id.hcz_bind_name_tv)
    private TextView nameTv;
    private int time = 60;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hcz_bind);
        ViewUtils.inject(this);
        init();
    }

    private void init() {
        context = this;
        title.setText("绑定手机");
        right.setVisibility(View.GONE);
        mobile = SharUtil.getPhone(context);
        nameTv.setText(Constants.UserName + ",请完善您的资料");
        codeLoginNumEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String code = codeLoginNumEditText.getText().toString();
                if (code != null && code.length() > 0) {
                    codeDeleteIv.setVisibility(View.VISIBLE);
                }else {
                    codeDeleteIv.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void bindPhone() {
        if (!Constants.isNetWork) {
            ActivityUtil.showPopWindow4Tips(this, title, false, "当前网络不可用，请稍后再试...");
            return;
        }
        // 防止重复点击
        if (CommonUtil.isFastDoubleClick())
            return;
        String account = "";
        String code = "";
        account = codeLoginNumEditText.getText().toString();
        code = codeLoginPwdEditText.getText().toString();
        if ("".equals(account)) {
            ActivityUtil.showPopWindow4Tips(this, title, false, "请输入手机号");
            return;
        }
        HczThreeBindNet hczLoginNet = new HczThreeBindNet(this, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case What.HTTP_REQUEST_CURD_SUCCESS:
                        Parents parents = (Parents) msg.obj;
                        if (parents.getPassword() == null || parents.getPassword().equals("")) {
                            startActivity(new Intent(HczBindActivity.this, HczSetPwdActivity.class));
                        } else {
                            startActivity(new Intent(HczBindActivity.this, HczMainActivity.class));
                        }
                        onBackPressed();
                        break;
                    case What.HTTP_REQUEST_CURD_FAILURE:
                        ActivityUtil.showPopWindow4Tips(HczBindActivity.this, title, false, msg.obj.toString() + "");
                        break;
                }
            }
        });
        hczLoginNet.putParams(account, code);
        hczLoginNet.sendRequest();
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
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
            ActivityUtil.showPopWindow4Tips(this, title, false, "当前网络不可用，请稍后再试...");
            codeView.setEnabled(true);
            return;
        }
        String phoneNum = codeLoginNumEditText.getText().toString();
        if ("".equals(phoneNum) || !phoneNum.matches("^[1-9]\\d{10}$")) {
            ActivityUtil.showPopWindow4Tips(this, title, false, "请输入正确的手机号");
            codeView.setEnabled(true);
            return;
        }
        codeView.setEnabled(false);
        HczGetCodeNet getCodeNet = new HczGetCodeNet(this, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case What.HTTP_REQUEST_CURD_SUCCESS:
                        ActivityUtil.showPopWindow4Tips(HczBindActivity.this, title, true, "发送验证码成功");
                        time = 60;
                        mHandler.sendEmptyMessageDelayed(RegisterCodeTimer.IN_RUNNING, 0);
                        break;
                    case What.HTTP_REQUEST_CURD_FAILURE:
                        time = 60;
                        ActivityUtil.showPopWindow4Tips(HczBindActivity.this, title, false, "发送验证码失败");
                        mHandler.sendEmptyMessageDelayed(RegisterCodeTimer.END_RUNNING, 0);
                        break;
                }
            }
        });
        getCodeNet.putParams(phoneNum, 3);
        getCodeNet.sendRequest();
    }


    @OnClick(R.id.iv_title_left)
    public void onHeadBackClick(View view) {
        onBackPressed();
    }

    @OnClick(R.id.activity_login_code_delete_phone)
    public void onClickDeleteCodePhone(View view) {
        codeLoginNumEditText.setText("");
    }


    @OnClick(R.id.activity_code_login_getcode)
    public void onClickGetCode(View view) {
        doGetCode();
    }

    @OnClick(R.id.activity_hcz_bind_send_btn)
    public void bindClick(View v) {
        bindPhone();
    }

}
