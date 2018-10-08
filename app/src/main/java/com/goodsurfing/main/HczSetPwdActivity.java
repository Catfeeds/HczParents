package com.goodsurfing.main;

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
import com.goodsurfing.server.PutDataServer;
import com.goodsurfing.server.net.HczAlterPwdNet;
import com.goodsurfing.server.net.HczSetPersonInfoNet;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResponder;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResult;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.CommonUtil;
import com.goodsurfing.utils.SharUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 好成长动态码登录注册后设置密码
 */
public class HczSetPwdActivity extends BaseActivity {

    protected static final int REFRESH = 100;

    private Context context;

    @ViewInject(R.id.activity_find_password_item_new_password)
    private EditText NewPwdEditText;

    @ViewInject(R.id.activity_find_passwrod_item_et_password)
    private EditText PwdEditText;

    @ViewInject(R.id.tv_title)
    private TextView title;

    @ViewInject(R.id.tv_title_right)
    private TextView right;
    @ViewInject(R.id.title_layout)
    private View title_layout;

    private static final int CODE_GET = 1;
    // 验证码内容

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hcz_set_password);
        ViewUtils.inject(this);
        init();
    }

    private void init() {
        context = this;
        title.setText("设置密码");
        right.setVisibility(View.INVISIBLE);

    }

    private void setPwd() {
        if (!Constants.isNetWork) {
            ActivityUtil.showPopWindow4Tips(this, title_layout, false, "当前网络不可用，请稍后再试...");
            return;
        }
        // 防止重复点击
        if (CommonUtil.isFastDoubleClick())
            return;
        String passwords = NewPwdEditText.getText().toString();
        if ("".equals(passwords)) {
            ActivityUtil.showPopWindow4Tips(this, title_layout, false, "请输入新密码");
            return;
        } else if (passwords.length() < 6) {
            ActivityUtil.showPopWindow4Tips(this, title_layout, false, "请输入不少于6位的密码");
            return;
        }
        if (ActivityUtil.checkPassword(passwords) < 3) {
            ActivityUtil.showPopWindow4Tips(this, title_layout, false, "密码必须包含数字和字母");
            return;
        }

        String password = PwdEditText.getText().toString();
        if ("".equals(password)) {
            ActivityUtil.showPopWindow4Tips(this, title_layout, false, "请再次输入新密码");
            return;
        }
        if (!passwords.equals(password)) {
            ActivityUtil.showPopWindow4Tips(this, title_layout, false, "两次新密码输入不一致");
            return;
        }
        HczSetPersonInfoNet setPersonInfoNet = new HczSetPersonInfoNet(this, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case What.HTTP_REQUEST_CURD_SUCCESS:
                        ActivityUtil.goMainActivity(HczSetPwdActivity.this);
                        onBackPressed();
                        break;
                    case What.HTTP_REQUEST_CURD_FAILURE:
                        ActivityUtil.showPopWindow4Tips(HczSetPwdActivity.this, title_layout, false, msg.obj.toString());
                        break;
                }
            }
        });
        setPersonInfoNet.putParams("", "",password);
        setPersonInfoNet.sendRequest();
    }


    @OnClick(R.id.activity_find_password_item_rl_login)
    public void loginClick(View view) {
        setPwd();
    }

    @OnClick(R.id.iv_title_left)
    public void onHeadBackClick(View view) {
        onBackPressed();
    }
}
