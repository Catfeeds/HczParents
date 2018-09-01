package com.goodsurfing.main;

import java.security.MessageDigest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.goodsurfing.app.R;
import com.goodsurfing.app.wxapi.ShareWeiXinActivity;
import com.goodsurfing.base.BaseFragment;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.fundlock.GestureEditActivity;
import com.goodsurfing.fundlock.GestureVerifyActivity;
import com.goodsurfing.server.CheckServerServer;
import com.goodsurfing.server.GetServerListServer;
import com.goodsurfing.server.LoginServer;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResponder;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResult;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.CommonUtil;
import com.goodsurfing.utils.SharUtil;
import com.goodsurfing.view.customview.ServiceOrTypeDialog;
import com.goodsurfing.view.customview.ZjWheelView.OnWheelViewListener;
import com.goodsurfing.view.customview.city.LocationDialogBuilder;
import com.goodsurfing.view.customview.city.LocationDialogBuilder.OnSaveLocationLister;

public class MainMyActivity extends BaseFragment implements OnClickListener {
    private RelativeLayout myLayout; // 个人信息
    private RelativeLayout xgLayout;// 修改密码
    private RelativeLayout xgssLayout;// 修改手势密码
    private RelativeLayout helpLayout;// 帮助中心
    private RelativeLayout aboutLayout;// 关于好上网
    private RelativeLayout fxLayout;// 分享
    private RelativeLayout xfLayout;// 续费
    private CheckBox ssCheckBox;// 手势密码开启ChceBox
    private Button loginButton;
    private View lineView;
    private View rootView;
    private TextView bindTextView;
    private TextView titletTextView;
    private View titleRight;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (!Constants.userId.equals("")) {
            rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_main_my, null);
            initViews(rootView);
            init();
        } else {
            rootView = LayoutInflater.from(getActivity()).inflate(R.layout.activity_login, null);
            initLoginViews(rootView);
            initLogin();
        }
        ViewGroup group = (ViewGroup) rootView.getParent();
        if (group != null) {
            group.removeView(rootView);
        }

        return rootView;
    }

    private void initViews(View view) {
        myLayout = (RelativeLayout) view.findViewById(R.id.activity_my_ation);
        xgLayout = (RelativeLayout) view.findViewById(R.id.activity_my_set_pass);
        xgssLayout = (RelativeLayout) view.findViewById(R.id.activity_my_set_shoushi_pass);
        helpLayout = (RelativeLayout) view.findViewById(R.id.activity_my_help);
        fxLayout = (RelativeLayout) view.findViewById(R.id.activity_my_fx);
        xfLayout = (RelativeLayout) view.findViewById(R.id.activity_my_xufei);
        aboutLayout = (RelativeLayout) view.findViewById(R.id.activity_my_about);
        ssCheckBox = (CheckBox) view.findViewById(R.id.activity_my_ss_cb);
        loginButton = (Button) view.findViewById(R.id.activity_my_fl);
        lineView = view.findViewById(R.id.activity_my_set_shoushi_pass_line);
        bindTextView = (TextView) view.findViewById(R.id.activity_my_bind_tx);
        titletTextView = view.findViewById(R.id.tv_title);
        titleRight = view.findViewById(R.id.tv_title_right);
        titleRight.setVisibility(View.GONE);
        view.findViewById(R.id.activity_qq_help).setOnClickListener(this);
    }

    private void init() {
        myLayout.setOnClickListener(this);
        xgLayout.setOnClickListener(this);
        xgssLayout.setOnClickListener(this);
        helpLayout.setOnClickListener(this);
        fxLayout.setOnClickListener(this);
        xfLayout.setOnClickListener(this);
        aboutLayout.setOnClickListener(this);
        ssCheckBox.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        titletTextView.setText("我的");
        Constants.bind = SharUtil.getBind(getActivity(), Constants.userId);
        if (!Constants.bind && !Constants.userId.equals("")) {
            bindTextView.setVisibility(View.VISIBLE);
            bindTextView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    startActivity(new Intent(getActivity(), BindActivity.class));
                }
            });
        } else {
            bindTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ("".equals(Constants.userId)) {
            if (!Constants.isRegistShow) {
                registerLyout.setVisibility(View.GONE);
                registerTipsTv.setVisibility(View.GONE);
                registerLineView.setVisibility(View.GONE);
            }
        } else {
            loginButton.setBackgroundResource(R.drawable.view_bottom_button_bg_yellow);
            xfLayout.setVisibility(View.VISIBLE);
            loginButton.setText("退出登录");
            ssCheckBox.setChecked(getActivity().getSharedPreferences(Constants.SP_NAME, 0).getBoolean(Constants.CECKBOX_KEY + Constants.userId, false));
            if (ssCheckBox.isChecked()) {
                xgssLayout.setVisibility(View.VISIBLE);
                lineView.setVisibility(View.VISIBLE);
            } else {
                xgssLayout.setVisibility(View.GONE);
                lineView.setVisibility(View.GONE);
            }
            if (!Constants.isRegistShow) {
                xfLayout.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        // 防止重复点击
        if (CommonUtil.isFastDoubleClick())
            return;
        switch (v.getId()) {
            case R.id.activity_my_ation:
                if ("".equals(Constants.userId)) {
                    LoginActivity.gotoLogin(getActivity());
                    return;
                }
                ActivityUtil.sendEvent4UM(getActivity(), "functionSwitch", "accountInfo", 4);
                Intent intent = new Intent(getActivity(), PersonInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.activity_my_about:
                ActivityUtil.sendEvent4UM(getActivity(), "functionSwitch", "About", 3);
                Intent about = new Intent(getActivity(), AboutActivity.class);
                startActivity(about);
                break;
            case R.id.activity_my_help:
                Intent help = new Intent(getActivity(), GuideView.class);
                Bundle bundle = new Bundle();
                bundle.putString("TYPE_GUIDE", "1");
                help.putExtras(bundle);
                startActivity(help);
                break;
            case R.id.activity_my_fx:
                if ("".equals(Constants.userId)) {
                    LoginActivity.gotoLogin(getActivity());
                    return;
                }
                ActivityUtil.sendEvent4UM(getActivity(), "functionSwitch", "ShareWeiXin", 21);
                Intent fx = new Intent(getActivity(), ShareWeiXinActivity.class);
                startActivity(fx);
                break;
            case R.id.activity_my_xufei:
                if ("".equals(Constants.userId)) {
                    LoginActivity.gotoLogin(getActivity());
                    return;
                }
                ActivityUtil.sendEvent4UM(getActivity(), "functionSwitch", "ChargeChoices", 22);
                Intent xufei = new Intent(getActivity(), ChargeChoicesActivity.class);
                startActivity(xufei);
                break;
            case R.id.activity_my_set_shoushi_pass:
                if ("".equals(Constants.userId)) {
                    LoginActivity.gotoLogin(getActivity());
                    return;
                }
                ActivityUtil.sendEvent4UM(getActivity(), "functionSwitch", "GestureVerify", 23);
                Intent shoushi = new Intent(getActivity(), GestureVerifyActivity.class);
                shoushi.putExtra("edit", true);
                startActivity(shoushi);
                break;
            case R.id.activity_my_set_pass:
                if ("".equals(Constants.userId)) {
                    LoginActivity.gotoLogin(getActivity());
                    return;
                } else if (!Constants.userMobile.equals("") && ActivityUtil.isMobileNO(Constants.userMobile)) {
                    ActivityUtil.sendEvent4UM(getActivity(), "functionSwitch", "changepwd", 8);
                    Intent setpass = new Intent(getActivity(), EditPasswordActivity.class);
                    startActivity(setpass);
                } else {
                    ActivityUtil.sendEvent4UM(getActivity(), "functionSwitch", "BindPhone", 9);
                    Intent setpass = new Intent(getActivity(), BindPhoneActivity.class);
                    startActivity(setpass);
                }
                break;
            case R.id.activity_my_fl:
                if ("".equals(Constants.userId)) {
                    LoginActivity.gotoLogin(getActivity());
                    return;
                }
                dialog();
                break;
            case R.id.activity_my_ss_cb:
                onCheckSs(ssCheckBox.isChecked());
                break;
            case R.id.activity_login_item_rl_login:
                if (login_ok) {
                    checkLocationInfo(cityName, serviceName);
                } else {
                    ActivityUtil.showPopWindow4Tips(getActivity(), title_layout, false, "请先勾选用户使用协议");
                }
                break;
            case R.id.activity_login_item_rl_register:
                RegisterActivity.gotoRegister(getActivity());
                break;
            case R.id.activity_login_item_adress_iv:
                doCity();
                break;
            case R.id.activity_login_item_forget_tv:
                ActivityUtil.sendEvent4UM(getActivity(), "functionSwitch", "FindPassword", 25);
                Intent intent1 = new Intent(getActivity(), FindPasswordActivity.class);
                startActivity(intent1);
                break;
            case R.id.activity_login_item_service:
                doServiceDialog();
                break;
            case R.id.activity_add_children_delete_phone:
                loginNumEditText.setText("");
                break;
            case R.id.xieyi_text:
                ActivityUtil.showXyDialog(context);
                break;
            case R.id.activity_qq_help:
            case R.id.tv_title_right:
                ActivityUtil.callQQkefu(getActivity(), rootView);
                break;
            case R.id.iv_title_left:
                Constants.isShowLogin =false;
                HczLoginActivity.gotoLogin(getActivity());
                break;

        }

    }

    // 显示退出登录 dialog
    @SuppressWarnings("deprecation")
    private void dialog() {
        final Dialog dlg = new Dialog(getActivity(), R.style.AlertDialogCustom);
        View view = View.inflate(getActivity(), R.layout.out_login_dialog, null);

        final TextView title = (TextView) view.findViewById(R.id.activity_change_mode_tv);

        final TextView comfirm = (TextView) view.findViewById(R.id.activity_change_mode_comfirm_text);
        TextView mCancleTextView = (TextView) view.findViewById(R.id.activity_change_mode_cancle_text);
        title.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dlg.dismiss();
            }
        });
        mCancleTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dlg.dismiss();
            }
        });
        comfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoinOutClick();
                dlg.dismiss();
            }
        });
        dlg.setContentView(view);
        WindowManager m = getActivity().getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dlg.getWindow().getAttributes(); // 获取对话框当前的参数值
        p.width = (int) (d.getWidth() * 1); // 宽度设置为屏幕的0.65
        dlg.getWindow().setAttributes(p);
        dlg.show();

    }

    // 手势密码 选择开关
    private void onCheckSs(boolean arg1) {
        if ("".equals(Constants.userId)) {
            LoginActivity.gotoLogin(getActivity());
            return;
        }
        Editor editor = getActivity().getSharedPreferences(Constants.SP_NAME, 0).edit();
        if (arg1) {
            xgssLayout.setVisibility(View.VISIBLE);
            lineView.setVisibility(View.VISIBLE);
            @SuppressWarnings("static-access")
            SharedPreferences prefer = getActivity().getSharedPreferences(Constants.LOCK, getActivity().MODE_PRIVATE);
            if (prefer.getString(Constants.LOCK_KEY + Constants.userId, "").equals("")) {
                Intent shoushi = new Intent(getActivity(), GestureEditActivity.class);
                shoushi.putExtra("edit", true);
                startActivity(shoushi);
            } else {
                editor.putBoolean(Constants.CECKBOX_KEY + Constants.userId, true).commit();
            }
            ssCheckBox.setChecked(arg1);
        } else {
            ssCheckBox.setChecked(arg1);
            xgssLayout.setVisibility(View.GONE);
            lineView.setVisibility(View.GONE);
            editor.putBoolean(Constants.CECKBOX_KEY + Constants.userId, false).commit();
        }
    }

    // 退出登录清理数据
    public void onLoinOutClick() {
        if (CommonUtil.isFastDoubleClick())
            return;
        if (!"".equals(Constants.userId)) {
            ActivityUtil.sendEvent4UM(getActivity(), "outLogin", "outLogin", 24);
            Constants.clear(getActivity());
            SharUtil.saveService(getActivity(),Constants.APP_USER_TYPE);
            ActivityUtil.goMainActivity(getActivity());
//            ((MainActivity)getActivity()).mainPageView.setCurrentItem(0);
        }
    }

    // 登录界面

    private TextView title;
    private TextView right;
    private ImageView left;
    protected static final int REFRESH = 100;
    private Context context;
    private EditText loginNumEditText;
    private EditText loginPwdEditText;
    private TextView cityTextView;
    private TextView serviceTextView;
    private ImageView deleteIv;
    private View registerLyout;
    private View registerTipsTv;
    private View registerLineView;
    private String mobile;
    private String cityName;
    private String serviceName;
    private Dialog dialog;
    private View title_layout;
    private CheckBox checkBox;
    private TextView xieyiTextView;
    protected boolean login_ok = true;

    private void initLoginViews(View view) {
        title = (TextView) view.findViewById(R.id.tv_title);
        right = (TextView) view.findViewById(R.id.tv_title_right);
        left = (ImageView) view.findViewById(R.id.iv_title_left);
        loginNumEditText = (EditText) view.findViewById(R.id.activity_login_item_et_num);
        loginPwdEditText = (EditText) view.findViewById(R.id.activity_login_item_et_password);
        cityTextView = (TextView) view.findViewById(R.id.activity_login_item_adress_tv);
        serviceTextView = (TextView) view.findViewById(R.id.activity_login_item_service_tv);
        deleteIv = (ImageView) view.findViewById(R.id.activity_add_children_delete_phone);
        registerLyout = view.findViewById(R.id.activity_login_item_rl_register);
        registerTipsTv = view.findViewById(R.id.activity_login_item_forget_tv1);
        registerLineView = view.findViewById(R.id.activity_login_item_line);
        title_layout = (View) view.findViewById(R.id.title_layout);
        checkBox = (CheckBox) view.findViewById(R.id.check_xieyi);
        xieyiTextView = (TextView) view.findViewById(R.id.xieyi_text);
        xieyiTextView.setOnClickListener(this);
        right.setOnClickListener(this);
        view.findViewById(R.id.activity_login_item_rl_login).setOnClickListener(this);
        view.findViewById(R.id.activity_login_item_rl_register).setOnClickListener(this);
        view.findViewById(R.id.activity_login_item_adress_iv).setOnClickListener(this);
        view.findViewById(R.id.activity_login_item_forget_tv).setOnClickListener(this);
        view.findViewById(R.id.activity_login_item_service).setOnClickListener(this);
        view.findViewById(R.id.activity_add_children_delete_phone).setOnClickListener(this);
        left.setOnClickListener(this);
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
    }

    private void initLogin() {
        context = getActivity();
        title.setText("登录");
        right.setText("QQ客服");
        right.setVisibility(View.VISIBLE);
        left.setVisibility(View.VISIBLE);
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
    }

    // 获取服务商列表
    private void getServerList() {
        String url = null;
        url = Constants.SERVER_URL_GLOBAL + "?" + "requesttype=1002";

        new GetServerListServer(new DataServiceResponder() {

            @Override
            public void onResult(DataServiceResult result) {
            }

            @Override
            public void onFailure() {
            }
        }, url, getActivity()).execute();
    }

    // 选择 运营商 列表
    private void doServiceDialog() {
        ServiceOrTypeDialog dialog = ServiceOrTypeDialog.getInstance(getActivity());
        dialog.setDialogProperties("选择运营商", Constants.serverList);
        dialog.setOnSaveServiceLister(new OnWheelViewListener() {

            @Override
            public void onSelected(int selectedIndex, String item) {
                if (cityName == null || "".equals(cityName)) {
                    ActivityUtil.showPopWindow4Tips(getActivity(), title_layout, false, "请选择所在地");
                    return;
                }
                serviceName = item;
                serviceTextView.setText(serviceName);
                // checkLocationInfo(cityName, serviceName);
                SharUtil.saveService(context, serviceName);

            }
        });
        dialog.show();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    /*
     * 登录请求操作
     */
    @SuppressLint("WrongConstant")
    private void getLoginFeedBack() {
        if (!Constants.isNetWork) {
            ActivityUtil.showPopWindow4Tips(getActivity(), title_layout, false, "当前网络不可用，请稍后再试...");
            return;
        }
        // 防止重复点击
        if (CommonUtil.isFastDoubleClick())
            return;
        final String account = loginNumEditText.getText().toString();
        if ("".equals(account)) {
            ActivityUtil.showPopWindow4Tips(getActivity(), title_layout, false, "请输入宽带账号或绑定手机号");
            return;
        }

        String password = loginPwdEditText.getText().toString();
        if ("".equals(password)) {
            ActivityUtil.showPopWindow4Tips(getActivity(), title_layout, false, "请输入密码");
            return;
        } else if (!password.matches("[0-9a-zA-z]{6,20}")) {
            ActivityUtil.showPopWindow4Tips(getActivity(), title_layout, false, "密码格式不正确，请输入不少于6位的密码");
            return;
        }
        getActivity().getSharedPreferences(Constants.LOGIN_INFO, Activity.MODE_APPEND).edit().putString(Constants.LOGIN_PASS, password).commit();
        String mdPassword = null;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            mdPassword = getString(md.digest(password.getBytes()));
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        int position = 1;// 家庭宽带 用户类型
        String modle = android.os.Build.MODEL;
        if ("".equals(modle) || modle == null)
            modle = "huawei";
        if (Constants.SERVER_URL.equals(Constants.SERVER_URL_GLOBAL)) {
            ActivityUtil.showPopWindow4Tips(getActivity(), title_layout, false, "运营商未开通");
            return;
        }
        String url = Constants.SERVER_URL + "?" + "user=" + account + "&passwd=" + mdPassword + "&usertype=" + position + "&mobile=" + modle + "&deviceID=" + ActivityUtil.getDeviceToken(getActivity());

        new LoginServer(new DataServiceResponder() {

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
                    ((MainActivity)getActivity()).mainPageView.setCurrentItem(0);
                } else {
                    ActivityUtil.showPopWindow4Tips(getActivity(), title_layout, false, result.extra + "");
                }
            }

            @Override
            public void onFailure() {
                ActivityUtil.showPopWindow4Tips(getActivity(), title_layout, false, "服务器忙，请稍候再试");

            }
        }, url, getActivity()).execute();
    }

    /*
     * 密码加密后截取
     */
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

    /*
     * 通过改变 服务地址和 运营商 来拉取 后台服务器地址IP
     */
    private void checkLocationInfo(String province, String service) {
        if (!Constants.isNetWork) {
            ActivityUtil.showPopWindow4Tips(getActivity(), title_layout, false, "当前网络不可用，请稍后再试...");
            return;
        }
        if (null == service || null == province || province.equals("") || service.equals("")) {
            ActivityUtil.showPopWindow4Tips(getActivity(), title_layout, false, "请选择运营商和地址");
            return;
        }
        String url = null;
        url = Constants.SERVER_URL_GLOBAL + "?" + "requesttype=1004" + "&area=" + province + "&provider=" + service;
        ActivityUtil.showPopWindow4Tips(getActivity(), title_layout, false, false, "正在登录...", -1);
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
                    ActivityUtil.showPopWindow4Tips(getActivity(), title_layout, false, result.extra + "");
                }
            }

            @Override
            public void onFailure() {
                Constants.SERVER_URL = Constants.SERVER_URL_GLOBAL;
            }
        }, url, getActivity()).execute();
    }

    private void getSavedDatas() {
        mobile = SharUtil.getPhone(context);
        cityName = SharUtil.getCity(context);
        serviceName = SharUtil.getService(context);
    }

    /*
     * 切换 城市 列表
     */
    protected void doCity() {
        LocationDialogBuilder locationDialog = LocationDialogBuilder.getInstance(getActivity());
        locationDialog.setOnSaveLocationLister(new OnSaveLocationLister() {
            @Override
            public void onSaveLocation(String province, String city, String provinceId, String cityId) {
                cityTextView.setText(city);
                cityName = city;
                SharUtil.saveCity(context, city);
                if (city == null || "".equals(city)) {
                    ActivityUtil.showPopWindow4Tips(getActivity(), title_layout, false, "请选择所在地");
                    return;
                }
                getServerList();
            }
        });
        locationDialog.show();
    }

}
