package com.goodsurfing.main;

import net.sourceforge.simcpux.MD5;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.component.constants.What;
import com.goodsurfing.app.R;
import com.goodsurfing.base.BaseActivity;
import com.goodsurfing.beans.User;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.server.BindPhoneCodeServer;
import com.goodsurfing.server.DoGetCodeServer;
import com.goodsurfing.server.PutDataServer;
import com.goodsurfing.server.net.HczSetPersonInfoNet;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResponder;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResult;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.CommonUtil;
import com.goodsurfing.utils.LogUtil;
import com.goodsurfing.utils.RegisterCodeTimer;
import com.goodsurfing.utils.SharUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class BindPhoneActivity extends BaseActivity {
    private final static String TAG = "BindPhoneActivity";

    protected static final int REFRESH = 100;

    @ViewInject(R.id.tv_title)
    private TextView title;

    @ViewInject(R.id.tv_title_right)
    private TextView right;

    @ViewInject(R.id.activity_add_children_et_phone)
    private EditText phoneEt;

    @ViewInject(R.id.activity_add_children_et_code)
    private EditText codeEt;

    @ViewInject(R.id.activity_add_children_delete_phone)
    private ImageView deleteIv;

    @ViewInject(R.id.activity_add_children_getcode)
    private TextView codeView;

    @ViewInject(R.id.activity_add_children_sign_btn)
    private TextView signBtnTv;

    @ViewInject(R.id.title_layout)
    private View title_layout;
    private boolean isOnclick = true;
    private static boolean isGettingCodeFinish = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_phone);
        ViewUtils.inject(this);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        signBtnTv.setEnabled(false);
        signBtnTv.setBackgroundResource(R.drawable.view_btn_gray__bg);
    }

    private void init() {
        title.setText("绑定手机号");
        right.setVisibility(View.INVISIBLE);
        phoneEt.addTextChangedListener(new TextWatcher() {

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
        codeEt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                if (arg0.length() == 6) {
                    signBtnTv.setEnabled(true);
                    signBtnTv.setBackgroundResource(R.drawable.view_bottom_button_bg);
                } else {
                    signBtnTv.setEnabled(false);
                    signBtnTv.setBackgroundResource(R.drawable.view_btn_gray__bg);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void afterTextChanged(Editable arg0) {

            }
        });
    }

    @OnClick(R.id.activity_add_children_getcode)
    private void onClickGetCode(View view) {
        codeView.setEnabled(false);
        doGetCode();
    }

    @OnClick(R.id.activity_add_children_delete_phone)
    private void onClickDeletePhone(View view) {
        phoneEt.setText("");
    }

    @OnClick(R.id.activity_add_children_sign_btn)
    private void onClickSignCode(View view) {
        if (isOnclick) {
            doSignCode();
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
    private int time = 60;

    /**
     * 获取手机验证码
     */
    private void doGetCode() {
        if (!Constants.isNetWork) {
            ActivityUtil.showPopWindow4Tips(this, title_layout, false, "当前网络不可用，请稍后再试...");
            codeView.setEnabled(true);
            return;
        }
        String phoneNum = phoneEt.getText().toString();
        if ("".equals(phoneNum) || !phoneNum.matches("^[1-9]\\d{10}$")) {
            ActivityUtil.showPopWindow4Tips(this, title_layout, false, "请输入正确的手机号");
            // EventHandler.showToast(this, "请输入正确的手机号");
            codeView.setEnabled(true);
            return;
        }
        String times = System.currentTimeMillis() + "";
        long token = Integer.parseInt(times.substring(times.length() - 5, times.length())) * Integer.parseInt(phoneNum.substring(phoneNum.length() - 4, phoneNum.length()));
        String keyString = "shian" + token + "haoup";

        String key = MD5.getMessageDigest(keyString.getBytes());
        String url = Constants.EDIT_PHONE_CODE_URL + "?c=index&a=index&tel=" + phoneNum + "&token=" + times + "&key=" + key;
        new DoGetCodeServer(new DataServiceResponder() {

            @Override
            public void onResult(DataServiceResult result) {
                try {
                    JSONObject jsonObject = new JSONObject((String) result.result);
                    String errCode = null;
                    errCode = jsonObject.getString("statusCode");
                    if ("000000".equals(errCode)) {
                        ActivityUtil.showPopWindow4Tips(BindPhoneActivity.this, title_layout, true, "发送验证码成功");
                        time = 60;
                        mHandler.sendEmptyMessageDelayed(RegisterCodeTimer.IN_RUNNING, 0);
                    } else {
                        time = 60;
                        ActivityUtil.showPopWindow4Tips(BindPhoneActivity.this, title_layout, false, "发送验证码失败");
                        mHandler.sendEmptyMessageDelayed(RegisterCodeTimer.END_RUNNING, 0);
                    }
                } catch (Exception e) {
                    LogUtil.logError(e);
                }
            }

            @Override
            public void onFailure() {

            }
        }, url, this).execute();

    }

    /**
     * 验证手机验证码
     */
    private void doSignCode() {
        if (!Constants.isNetWork) {
            ActivityUtil.showPopWindow4Tips(this, title_layout, false, "当前网络不可用，请稍后再试...");
            return;
        }
        final String phoneNum = phoneEt.getText().toString();
        String code = codeEt.getText().toString();
        if ("".equals(code) || !code.matches("^[0-9]{6}$")) {
            ActivityUtil.showPopWindow4Tips(this, title_layout, false, "验证码格式不正确");
            return;
        } else {
            LogUtil.log(TAG, code);
        }
        isOnclick = false;
        String url = Constants.EDIT_PHONE_CODE_URL + "?c=index&a=getCode&tel=" + phoneNum + "&code=" + code;
        new BindPhoneCodeServer(this, url, new DataServiceResponder() {
            @Override
            public void onFailure() {
                isOnclick = true;
                ActivityUtil.showPopWindow4Tips(BindPhoneActivity.this, title_layout, false, "验证失败");
            }

            @Override
            public void onResult(DataServiceResult result) {
                isOnclick = true;
                if (result.code.equals("0")) {
                    sendUserInfoData(phoneNum);
                } else {
                    ActivityUtil.showPopWindow4Tips(BindPhoneActivity.this, title_layout, false, "验证失败");
                }
            }
        }).execute();
    }

    private void sendUserInfoData(final String phoneNum) {
        if (Constants.APP_USER_TYPE.equals(SharUtil.getService(this))) {
            HczSetPersonInfoNet setPersonInfoNet = new HczSetPersonInfoNet(this, new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case What.HTTP_REQUEST_CURD_SUCCESS:
                            isOnclick = true;
                            gotoSetpass();
                            break;
                        case What.HTTP_REQUEST_CURD_FAILURE:
                            isOnclick = true;
                            ActivityUtil.showPopWindow4Tips(BindPhoneActivity.this, title_layout, false, msg.obj.toString());
                            break;
                    }
                }
            });
            setPersonInfoNet.putParams(phoneNum, "","");
            setPersonInfoNet.sendRequest();
        } else {
            String url = Constants.SERVER_URL + "?" + "committype=4" + "&userid=" + Constants.userId + "&token=" + Constants.tokenID + "&name=" + Constants.name + "&type=" + "2" + "&model=" + android.os.Build.MODEL + "&Mobile=" + phoneNum + "&address=" + Constants.adress;
            new PutDataServer(new DataServiceResponder() {

                @Override
                public void onResult(DataServiceResult result) {
                    if ("0".equals(result.code)) {
                        ActivityUtil.showPopWindow4Tips(BindPhoneActivity.this, title_layout, true, "绑定成功");
                        Constants.userMobile = phoneNum;
                        User user = User.getUser();
                        user.setPhone(Constants.userMobile);
                        CommonUtil.setUser(BindPhoneActivity.this, user);
                        SharUtil.savePhone(BindPhoneActivity.this, phoneNum);
                        gotoSetpass();
                    } else {
                        ActivityUtil.showPopWindow4Tips(BindPhoneActivity.this, title_layout, false, "验证失败");
                    }
                }

                @Override
                public void onFailure() {

                }
            }, url, this, false).execute();
        }
    }

    @OnClick(R.id.iv_title_left)
    public void onHeadBackClick(View view) {
        onBackPressed();
    }

    private void gotoSetpass() {
        Intent intent = new Intent(BindPhoneActivity.this, EditPasswordActivity.class);
        startActivity(intent);
        onBackPressed();
    }
}
