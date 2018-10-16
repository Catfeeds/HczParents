package com.goodsurfing.main;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import net.sourceforge.simcpux.MD5;

import org.json.JSONObject;

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
import com.goodsurfing.beans.City;
import com.goodsurfing.beans.IPList;
import com.goodsurfing.beans.User;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.server.CheckServerServer;
import com.goodsurfing.server.DoGetCodeServer;
import com.goodsurfing.server.PutDataServer;
import com.goodsurfing.server.net.HczCheckCityNet;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResponder;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResult;
import com.goodsurfing.service.RegisterCodeTimerService;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.CommonUtil;
import com.goodsurfing.utils.LogUtil;
import com.goodsurfing.utils.RegisterCodeTimer;
import com.goodsurfing.utils.SharUtil;
import com.goodsurfing.view.customview.ServiceOrTypeDialog;
import com.goodsurfing.view.customview.ZjWheelView.OnWheelViewListener;
import com.goodsurfing.view.customview.city.LocationDialogBuilder;
import com.goodsurfing.view.customview.city.LocationDialogBuilder.OnSaveLocationLister;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class FindPasswordActivity extends BaseActivity {

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
    // 验证码内容
    private String verificationCode;

    @ViewInject(R.id.activity_find_item_adress_tv)
    private TextView cityTextView;

    @ViewInject(R.id.activity_find_item_service_tv)
    private TextView serviceTextView;

    private String mobile;
    private String cityName;
    private String serviceName;
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 正在倒计时
                case RegisterCodeTimer.IN_RUNNING:
                    getCodeTv.setText(msg.obj.toString());
                    break;

                // 完成倒计时
                case RegisterCodeTimer.END_RUNNING:
                    getCodeTv.setEnabled(true);
                    getCodeTv.setText("重新获取");
                    getCodeTv.setBackgroundResource(R.drawable.view_bottom_button_bg);
                    break;
                // 获取验证码
                case CODE_GET:
                    codeEditText.setText(verificationCode);
                    break;

                default:
                    break;
            }
        }

        ;
    };
    private boolean isGettingCode;

    // private Dialog dialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);
        ViewUtils.inject(this);
        init();
    }

    private void init() {
        context = this;
        title.setText("找回密码");
        right.setVisibility(View.INVISIBLE);
        timerService = new Intent(this, RegisterCodeTimerService.class);
        RegisterCodeTimerService.setHandler(mHandler);
        getSavedDatas();
        cityTextView.setText(cityName);
        serviceTextView.setText(serviceName);
        // checkLocationInfo(cityName, serviceName);
        if (mobile != null && !"".equals(mobile))
            NumEditText.setText(mobile);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick(R.id.activity_find_item_service)
    public void doService(View view) {
        doServiceDialog();
    }

    private void doServiceDialog() {
        ServiceOrTypeDialog dialog = ServiceOrTypeDialog.getInstance(this);
        dialog.setDialogProperties("选择运营商", Constants.serverList);
        dialog.setOnSaveServiceLister(new OnWheelViewListener() {

            @Override
            public void onSelected(int selectedIndex, String item) {
                if (cityName == null || "".equals(cityName)) {
                    ActivityUtil.showPopWindow4Tips(FindPasswordActivity.this, title_layout, false, "请选择所在地");
                    return;
                }
                serviceName = item;
                serviceTextView.setText(serviceName);
                SharUtil.saveService(context, serviceName);
                checkCity(Constants.serviceList.get(selectedIndex - 1));
            }
        });
        dialog.show();
    }

    private void checkCity(IPList ipList) {
        Constants.cityList = Constants.serverCityMap.get(serviceName);
        Constants.cityStrList.clear();
        if (Constants.cityList != null && Constants.cityList.size() > 0) {
            for (City city : Constants.cityList) {
                Constants.cityStrList.add(city.getProvinceName());
            }
        }
        if (Constants.cityStrList.size() > 0) {
            cityTextView.setText(Constants.cityStrList.get(0));
            Constants.SERVER_URL = "http://" + Constants.cityList.get(0).getServerip() + ":" + Constants.cityList.get(0).getServerport();
        }
    }

    @OnClick(R.id.activity_find_item_adress_iv)
    public void onPositionClick(View v) {
        doCity();
    }

    protected void doCity() {
        Constants.cityList = Constants.serverCityMap.get(serviceName);
        Constants.cityStrList.clear();
        if (Constants.cityList != null && Constants.cityList.size() > 0) {
            for (City city : Constants.cityList) {
                Constants.cityStrList.add(city.getProvinceName());
            }
        }
        ServiceOrTypeDialog dialog = ServiceOrTypeDialog.getInstance(this);
        dialog.setDialogProperties("选择地区", Constants.cityStrList);
        dialog.setOnSaveServiceLister(new OnWheelViewListener() {

            @Override
            public void onSelected(int selectedIndex, String item) {
                cityTextView.setText(item);
                cityName = item;
                City city = Constants.cityList.get(selectedIndex - 1);
                Constants.SERVER_URL = "http://" + city.getServerip() + ":" + city.getServerport();
                SharUtil.saveCity(context, item);
                User user = User.getUser();
                user.setIP(Constants.SERVER_URL);
                CommonUtil.setUser(context, user);

            }
        });
        dialog.show();
    }

    private void getFindPasswordFeedBack() {
        if (!Constants.isNetWork) {
            ActivityUtil.showPopWindow4Tips(this, title_layout, false, "当前网络不可用，请稍后再试...");
            return;
        }

        final String account = NumEditText.getText().toString();
        if ("".equals(account)) {
            ActivityUtil.showPopWindow4Tips(FindPasswordActivity.this, title_layout, false, "请输入手机号");
            return;
        }

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
        } else if (!passwords.equals(password)) {
            ActivityUtil.showPopWindow4Tips(this, title_layout, false, "两次密码输入不一致");
            return;
        }

        String mdPassword = null;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            mdPassword = getString(md.digest(password.getBytes()));

        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }

        String url = Constants.SERVER_URL + "?" + "Account=" + account + "&Password=" + mdPassword + "&committype=12&userid=" + Constants.userId;

        new PutDataServer(new DataServiceResponder() {

            @Override
            public void onResult(DataServiceResult result) {
                if (result.code.equals("0")) {
                    ActivityUtil.showPopWindow4Tips(FindPasswordActivity.this, title_layout, true, "找回成功");
                    onBackPressed();
                } else {
                    ActivityUtil.showPopWindow4Tips(FindPasswordActivity.this, title_layout, false, result.extra + "");
                }
            }

            @Override
            public void onFailure() {
            }
        }, url, this).execute();
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

    @OnClick(R.id.activity_find_password_item_rl_login)
    public void loginClick(View view) {
        doSignCode();
    }

    @OnClick(R.id.iv_title_left)
    public void onHeadBackClick(View view) {
        onBackPressed();
    }

    @OnClick(R.id.activity_register_item_tv_code)
    public void onGetCodeClick(View v) {
        if (!isGettingCode)
            doSignPhone();
    }

    private void doSignCode() {
        if (!Constants.isNetWork) {
            ActivityUtil.showPopWindow4Tips(this, title_layout, false, "当前网络不可用，请稍后再试...");
            return;
        }
        String phoneNum = NumEditText.getText().toString();
        if ("".equals(phoneNum) || !phoneNum.matches("^[1-9]\\d{10}$")) {
            ActivityUtil.showPopWindow4Tips(this, title_layout, false, "您输入的电话号码格式不正确或为空");
            return;
        } else {
            LogUtil.log(TAG, phoneNum);
        }
        String url = null;
        String code = codeEditText.getText().toString();
        if ("".equals(code) || !code.matches("^[0-9]{6}$")) {
            ActivityUtil.showPopWindow4Tips(this, title_layout, false, "您输入的验证码格式不正确或为空");
            return;
        } else {
            LogUtil.log(TAG, code);
        }
        url = Constants.EDIT_PHONE_CODE_URL + "?c=index&a=getCode&tel=" + phoneNum + "&code=" + code;
        new DoGetCodeServer(new DataServiceResponder() {

            @Override
            public void onResult(DataServiceResult result) {
                try {
                    JSONObject jsonObject = new JSONObject((String) result.result);
                    String errCode = jsonObject.getString("status");
                    if ("1".equals(errCode)) {
                        getFindPasswordFeedBack();
                    } else {
                        ActivityUtil.showPopWindow4Tips(FindPasswordActivity.this, title_layout, false, "验证码错误");
                    }
                } catch (Exception e) {
                    LogUtil.logError(e);
                }
            }

            @Override
            public void onFailure() {
                ActivityUtil.showPopWindow4Tips(FindPasswordActivity.this, title_layout, false, "服务器忙，请稍后再试");
            }
        }, url, context).execute();
    }

    /**
     * 获取手机验证码
     */
    private void doGetCode() {
        if (!Constants.isNetWork) {
            ActivityUtil.showPopWindow4Tips(this, title_layout, false, "当前网络不可用，请稍后再试...");
            return;
        }
        String phoneNum = NumEditText.getText().toString();
        if ("".equals(phoneNum) || !phoneNum.matches("^[1-9]\\d{10}$")) {
            ActivityUtil.showPopWindow4Tips(this, title_layout, false, "您输入的电话号码格式不正确或为空");
            return;
        } else {
            LogUtil.log(TAG, phoneNum);
        }
        String url = null;
        String times = System.currentTimeMillis() + "";
        long token = Integer.parseInt(times.substring(times.length() - 5, times.length())) * Integer.parseInt(phoneNum.substring(phoneNum.length() - 4, phoneNum.length()));
        String keyString = "shian" + token + "haoup";
        String key = MD5.getMessageDigest(keyString.getBytes());
        url = Constants.EDIT_PHONE_CODE_URL + "?c=index&a=index&tel=" + phoneNum + "&token=" + times + "&key=" + key;
        new DoGetCodeServer(new DataServiceResponder() {

            @Override
            public void onResult(DataServiceResult result) {
                try {
                    JSONObject jsonObject = new JSONObject((String) result.result);
                    String errCode = null;
                    errCode = jsonObject.getString("statusCode");
                    if ("000000".equals(errCode)) {
                        ActivityUtil.showPopWindow4Tips(FindPasswordActivity.this, title_layout, true, "验证码发送成功");
                        getCodeTv.setEnabled(false);
                        getCodeTv.setBackgroundResource(R.drawable.add_children_gray);
                        startService(timerService);
                    } else {
                        String message = jsonObject.getString("message");
                        ActivityUtil.showPopWindow4Tips(FindPasswordActivity.this, title_layout, false, message);
                    }
                } catch (Exception e) {
                    LogUtil.logError(e);
                }
            }

            @Override
            public void onFailure() {
                ActivityUtil.showPopWindow4Tips(FindPasswordActivity.this, title_layout, false, "服务器忙，请稍后再试");
            }
        }, url, context).execute();
    }

    /**
     * 验证手机号码是否在 库里存在
     */
    private void doSignPhone() {
        if (!Constants.isNetWork) {
            ActivityUtil.showPopWindow4Tips(this, title_layout, false, "当前网络不可用，请稍后再试...");
            return;
        }
        final String service = serviceTextView.getText().toString();
        if (null == service || service.equals("")) {
            ActivityUtil.showPopWindow4Tips(this, cityTextView, false, "请选择业务");
            return;
        }
        final String city = cityTextView.getText().toString();
        if (null == city || city.equals("")) {
            ActivityUtil.showPopWindow4Tips(this, cityTextView, false, "请选择地区");
            return;
        }
        String phoneNum = NumEditText.getText().toString();

        if ("".equals(phoneNum) || !phoneNum.matches("^[1-9]\\d{10}$")) {
            ActivityUtil.showPopWindow4Tips(this, title_layout, false, "号码格式不正确或为空");
            return;
        } else {
            LogUtil.log(TAG, phoneNum);
        }
        isGettingCode = true;
        String url = Constants.SERVER_URL + "?" + "mobile=" + phoneNum + "&requesttype=13";
        ActivityUtil.showPopWindow4Tips(this, title_layout, false, false, "获取验证码...", -1);
        getCodeTv.setBackgroundResource(R.drawable.add_children_gray);
        getCodeTv.setText("正在获取");
        new PutDataServer(new DataServiceResponder() {

            @Override
            public void onResult(DataServiceResult result) {
                isGettingCode = false;
                if (result != null && result.code.equals("0")) {
                    doGetCode();
                } else {
                    getCodeTv.setBackgroundResource(R.drawable.view_bottom_button_bg);
                    getCodeTv.setText("重新获取");
                    ActivityUtil.showPopWindow4Tips(FindPasswordActivity.this, title_layout, false, "您的手机号还没注册");
                }
            }

            @Override
            public void onFailure() {
                isGettingCode = false;
                ActivityUtil.showPopWindow4Tips(FindPasswordActivity.this, title_layout, false, "服务器忙，请稍后再试");
            }
        }, url, context).execute();

    }

    private void getSavedDatas() {
        mobile = SharUtil.getPhone(context);
        cityName = SharUtil.getCity(context);
        serviceName = SharUtil.getService(context);
    }

}
