package com.goodsurfing.main;

import java.security.MessageDigest;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.android.component.constants.What;
import com.goodsurfing.addchild.AddChildActivity;
import com.goodsurfing.addchild.ChildListActivity;
import com.goodsurfing.app.R;
import com.goodsurfing.app.wxapi.ShareWeiXinActivity;
import com.goodsurfing.base.BaseFragment;
import com.goodsurfing.beans.City;
import com.goodsurfing.beans.IPList;
import com.goodsurfing.beans.Parents;
import com.goodsurfing.beans.User;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.fundlock.GestureEditActivity;
import com.goodsurfing.fundlock.GestureVerifyActivity;
import com.goodsurfing.hcz.HczBindActivity;
import com.goodsurfing.hcz.HczChargeChoicesActivity;
import com.goodsurfing.hcz.HczLoginActivity;
import com.goodsurfing.hcz.HczPersonInfoActivity;
import com.goodsurfing.hcz.HczSetPwdActivity;
import com.goodsurfing.hcz.LaboratoryActivity;
import com.goodsurfing.server.LoginServer;
import com.goodsurfing.server.net.HczAppFuncNet;
import com.goodsurfing.server.net.HczGetCodeNet;
import com.goodsurfing.server.net.HczGetServerNet;
import com.goodsurfing.server.net.HczLoginNet;
import com.goodsurfing.server.net.HczWXLoginNet;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResponder;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResult;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.CommonUtil;
import com.goodsurfing.utils.RegisterCodeTimer;
import com.goodsurfing.utils.SharUtil;
import com.goodsurfing.view.customview.ServiceOrTypeDialog;
import com.goodsurfing.view.customview.ZjWheelView.OnWheelViewListener;
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

public class MainMyActivity extends BaseFragment implements OnClickListener {
    private RelativeLayout xgssLayout;// 修改手势密码
    private RelativeLayout xfLayout;// 续费
    private RelativeLayout hczxfLayout;// 手势
    private RelativeLayout hczxgssLayout;// 手势
    private CheckBox ssCheckBox;// 手势密码开启ChceBox
    private CheckBox hczssCheckBox;// 手势密码开启ChceBox
    private Button loginButton;
    private View lineView;
    private View rootView;
    private TextView bindTextView;
    private View hczLayout;
    private View hswLayout;
    private View loginView;
    private View hczMyView;
    private View hswMyView;
    private String registerUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == rootView) {
            rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_my, null);
            ViewUtils.inject(this, rootView);
            initViews(rootView);
            initLoginViews(rootView);
            initLogin();
            initHczLogin();
            init();
        }
        ViewGroup group = (ViewGroup) rootView.getParent();
        if (group != null) {
            group.removeView(rootView);
        }

        return rootView;
    }

    private void initViews(View view) {
        loginView = view.findViewById(R.id.login_view);
        hswMyView = view.findViewById(R.id.hsw_my_view);
        hczMyView = view.findViewById(R.id.hcz_my_view);
        hswMyView.findViewById(R.id.activity_my_ation).setOnClickListener(this);
        hswMyView.findViewById(R.id.activity_my_set_pass).setOnClickListener(this);
        xgssLayout = hswMyView.findViewById(R.id.activity_my_set_shoushi_pass);
        hczssCheckBox = hczMyView.findViewById(R.id.activity_my_ss_cb);
        hczxgssLayout = hczMyView.findViewById(R.id.activity_my_set_shoushi_pass);
        hswMyView.findViewById(R.id.activity_my_help).setOnClickListener(this);
        hswMyView.findViewById(R.id.activity_my_fx).setOnClickListener(this);
        xfLayout = hswMyView.findViewById(R.id.activity_my_xufei);
        hczxfLayout = hczMyView.findViewById(R.id.activity_my_xufei);
        hswMyView.findViewById(R.id.activity_my_about).setOnClickListener(this);
        ssCheckBox = hswMyView.findViewById(R.id.activity_my_ss_cb);
        loginButton = hswMyView.findViewById(R.id.activity_my_fl);
        hswMyView.findViewById(R.id.activity_qq_help).setOnClickListener(this);
        lineView = hswMyView.findViewById(R.id.activity_my_set_shoushi_pass_line);
        bindTextView = (TextView) hswMyView.findViewById(R.id.activity_my_bind_tx);
        hczMyView.findViewById(R.id.activity_my_ation).setOnClickListener(this);
        hczMyView.findViewById(R.id.activity_my_set_pass).setOnClickListener(this);
        hczMyView.findViewById(R.id.activity_my_set_shoushi_pass).setOnClickListener(this);
        hczMyView.findViewById(R.id.activity_my_help).setOnClickListener(this);
        hczMyView.findViewById(R.id.activity_my_fx).setOnClickListener(this);
        hczMyView.findViewById(R.id.activity_my_xufei).setOnClickListener(this);
        hczMyView.findViewById(R.id.activity_my_about).setOnClickListener(this);
        hczMyView.findViewById(R.id.activity_my_ss_cb).setOnClickListener(this);
        hczMyView.findViewById(R.id.activity_my_fl).setOnClickListener(this);
        hczMyView.findViewById(R.id.activity_qq_help).setOnClickListener(this);
        hczMyView.findViewById(R.id.activity_my_open).setOnClickListener(this);

    }

    private void init() {
        xgssLayout.setOnClickListener(this);
        xfLayout.setOnClickListener(this);
        ssCheckBox.setOnClickListener(this);
        loginButton.setOnClickListener(this);
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

    private void initLoginViews(View view) {
        hczLayout = view.findViewById(R.id.hcz_login);
        hswLayout = view.findViewById(R.id.hsw_login);
        loginNumEditText = (EditText) view.findViewById(R.id.activity_login_item_et_num);
        loginPwdEditText = (EditText) view.findViewById(R.id.activity_login_item_et_password);
        cityTextView = (TextView) view.findViewById(R.id.activity_login_item_adress_tv);
        serviceTextView = (TextView) view.findViewById(R.id.activity_login_item_service_tv);
        deleteIv = (ImageView) view.findViewById(R.id.activity_add_children_delete_phone);
        registerLyout = view.findViewById(R.id.activity_login_item_rl_register);
        registerTipsTv = view.findViewById(R.id.activity_login_item_forget_tv1);
        registerLineView = view.findViewById(R.id.activity_login_item_line);
        checkBox = (CheckBox) view.findViewById(R.id.check_xieyi);
        xieyiTextView = (TextView) view.findViewById(R.id.xieyi_text);
        view.findViewById(R.id.main_help_tv).setOnClickListener(this);
        xieyiTextView.setOnClickListener(this);
        view.findViewById(R.id.activity_login_item_rl_login).setOnClickListener(this);
        view.findViewById(R.id.activity_login_item_rl_register).setOnClickListener(this);
        view.findViewById(R.id.activity_login_item_adress_iv).setOnClickListener(this);
        view.findViewById(R.id.activity_login_item_forget_tv).setOnClickListener(this);
        view.findViewById(R.id.activity_login_item_service).setOnClickListener(this);
        view.findViewById(R.id.activity_add_children_delete_phone).setOnClickListener(this);
        view.findViewById(R.id.activity_my_add_child).setOnClickListener(this);
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
        checkCity();
        cityTextView.setText(cityName);
        serviceTextView.setText(serviceName);
        if (mobile != null && !"".equals(mobile)) {
            loginNumEditText.setText(mobile);
            loginNumEditText.setSelection(mobile.length());
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if ("".equals(Constants.userId)) {
            hczMyView.setVisibility(View.GONE);
            hswMyView.setVisibility(View.GONE);
            loginView.setVisibility(View.VISIBLE);
            getServerList();
        } else {
            if (Constants.APP_USER_TYPE.equals(SharUtil.getService(getActivity()))) {
                hczMyView.setVisibility(View.VISIBLE);
                hswMyView.setVisibility(View.GONE);
                loginView.setVisibility(View.GONE);
            } else {
                hczMyView.setVisibility(View.GONE);
                hswMyView.setVisibility(View.VISIBLE);
                loginView.setVisibility(View.GONE);
            }
            loginButton.setBackgroundResource(R.drawable.view_bottom_button_bg_yellow);
            loginButton.setText("退出登录");
            ssCheckBox.setChecked(getActivity().getSharedPreferences(Constants.SP_NAME, 0).getBoolean(Constants.CECKBOX_KEY + Constants.userId, false));
            hczssCheckBox.setChecked(getActivity().getSharedPreferences(Constants.SP_NAME, 0).getBoolean(Constants.CECKBOX_KEY + Constants.userId, false));
            if (ssCheckBox.isChecked()) {
                xgssLayout.setVisibility(View.VISIBLE);
                hczxgssLayout.setVisibility(View.VISIBLE);
                lineView.setVisibility(View.VISIBLE);
            } else {
                hczxgssLayout.setVisibility(View.GONE);
                xgssLayout.setVisibility(View.GONE);
                lineView.setVisibility(View.GONE);
            }
            if (!Constants.isRegistShow) {
                xfLayout.setVisibility(View.GONE);
//                hczxfLayout.setVisibility(View.GONE);
            } else {
                xfLayout.setVisibility(View.VISIBLE);
//                hczxfLayout.setVisibility(View.VISIBLE);
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
                Intent intent;
                if (SharUtil.getService(getActivity()).equals(Constants.APP_USER_TYPE)) {
                    intent = new Intent(getActivity(), HczPersonInfoActivity.class);
                } else {
                    intent = new Intent(getActivity(), PersonInfoActivity.class);
                }
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
                Intent xufei;
                if (SharUtil.getService(getActivity()).equals(Constants.APP_USER_TYPE)) {
                    xufei = new Intent(getActivity(), HczChargeChoicesActivity.class);
                } else {
                    xufei = new Intent(getActivity(), ChargeChoicesActivity.class);
                }
                ActivityUtil.sendEvent4UM(getActivity(), "functionSwitch", "ChargeChoices", 22);
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
                onCheckSs(((CheckBox) v).isChecked());
                break;
            case R.id.activity_login_item_rl_login:
                if (login_ok) {
                    getLoginFeedBack();
                } else {
                    ActivityUtil.showPopWindow4Tips(getActivity(), cityTextView, false, "请先勾选用户使用协议");
                }
                break;
            case R.id.activity_login_item_rl_register:
                if (TextUtils.isEmpty(registerUrl)) {
                    RegisterActivity.gotoRegister(getActivity());
                } else {
                    Intent web = new Intent(getActivity(), WebActivity.class);
                    web.putExtra("url", registerUrl);
                    startActivity(web);
                }
                break;
            case R.id.activity_login_item_adress_iv:
                doCity(cityTextView);
                break;
            case R.id.activity_login_item_forget_tv:
                ActivityUtil.sendEvent4UM(getActivity(), "functionSwitch", "FindPassword", 25);
                Intent intent1 = new Intent(getActivity(), FindPasswordActivity.class);
                startActivity(intent1);
                break;
            case R.id.activity_login_item_service:
                doServiceDialog(serviceTextView);
                break;
            case R.id.activity_add_children_delete_phone:
                loginNumEditText.setText("");
                break;
            case R.id.xieyi_text:
                ActivityUtil.showXyDialog(context);
                break;
            case R.id.activity_qq_help:
            case R.id.main_help_tv:
                ActivityUtil.callQQkefu(getActivity(), rootView);
                break;
            case R.id.iv_title_left:
                Constants.isShowLogin = false;
                HczLoginActivity.gotoLogin(getActivity());
                break;
            case R.id.activity_my_add_child:
                if (Constants.isbindChild) {
                    startActivity(new Intent(getActivity(), ChildListActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), AddChildActivity.class));
                }
                break;
            case  R.id.activity_my_open:
                startActivity(new Intent(getActivity(),LaboratoryActivity.class));
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
            hczxgssLayout.setVisibility(View.VISIBLE);
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
            hczxgssLayout.setVisibility(View.GONE);
            ssCheckBox.setChecked(arg1);
            xgssLayout.setVisibility(View.GONE);
            lineView.setVisibility(View.GONE);
            editor.putBoolean(Constants.CECKBOX_KEY + Constants.userId, false).commit();
        }
    }

    // 退出登录清理数据
    public void onLoinOutClick() {
        if (!"".equals(Constants.userId)) {
            ActivityUtil.sendEvent4UM(getActivity(), "outLogin", "outLogin", 24);
            Constants.clear(getActivity());
            hczloginPwdEditText.setText("");
            loginPwdEditText.setText("");
            SharUtil.saveService(getActivity(), "");
            hczServiceTextView.setText("");
            serviceTextView.setText("");
            hczCodeServiceTextView.setText("");
            ActivityUtil.goMainActivity(getActivity());
        }
    }

    // 好上网登录界面

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
    private CheckBox checkBox;
    private TextView xieyiTextView;
    protected boolean login_ok = true;

    // 获取服务商列表
    private void getServerList() {
        if (Constants.serviceList == null) {
            HczGetServerNet getServerNet = new HczGetServerNet(getActivity(), new Handler() {
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what) {
                        case What.HTTP_REQUEST_CURD_SUCCESS:
                            Constants.serviceList = (List<IPList>) msg.obj;
                            break;
                        case What.HTTP_REQUEST_CURD_FAILURE:
                            break;
                    }
                }
            });
            getServerNet.sendRequest();
        }
    }

    // 选择 运营商 列表
    private void doServiceDialog(final TextView serviceView) {
        if (!Constants.isNetWork) {
            ActivityUtil.showPopWindow4Tips(getActivity(), hczloginNumEditText, false, "当前网络不可用，请稍后再试...");
            return;
        }
        if (Constants.serverCityMap.size() <= 0) {
            ActivityUtil.showPopWindow4Tips(getActivity(), cityTextView, false, "正在获取业务列表...");
            return;
        }
        ServiceOrTypeDialog dialog = ServiceOrTypeDialog.getInstance(getActivity());
        dialog.setDialogProperties("选择业务", Constants.serverList);
        dialog.setOnSaveServiceLister(new OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                serviceName = item;
                checkRigst(selectedIndex);
                checkCity();
                checHint();
                serviceView.setText(serviceName);
                SharUtil.saveService(context, serviceName);
                SharUtil.saveCity(context, cityName);
                serviceTextView.setText(serviceName);
                hczServiceTextView.setText(serviceName);
                hczCodeServiceTextView.setText(serviceName);
                hczCityTextView.setText(cityName);
                hczCodeCityTextView.setText(cityName);
                cityTextView.setText(cityName);
            }
        });
        dialog.show();
    }

    private void checHint() {
        if (serviceName.equals("中国联通")) {
            loginNumEditText.setHint("请输入宽带帐号或手机号");
            loginNumEditText.setKeyListener(DigitsKeyListener.getInstance("0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM"));
            loginNumEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
        } else {
            loginNumEditText.setHint("请输入手机号");
            loginNumEditText.setInputType(InputType.TYPE_CLASS_PHONE);
            loginNumEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
        }
    }

    private void checkRigst(int selectedIndex) {
        Constants.isRegistShow = Constants.serviceList.get(selectedIndex - 1).getShow_btn().equals("1");
        registerUrl = Constants.serviceList.get(selectedIndex - 1).getOpen_url();
        if (!Constants.isRegistShow) {
            registerLyout.setVisibility(View.GONE);
            registerTipsTv.setVisibility(View.GONE);
            registerLineView.setVisibility(View.GONE);
        } else {
            registerLyout.setVisibility(View.VISIBLE);
            registerTipsTv.setVisibility(View.VISIBLE);
            registerLineView.setVisibility(View.VISIBLE);
        }
        SharUtil.saveServiceId(Constants.serviceList.get(selectedIndex - 1).getId());
        HczAppFuncNet appFuncNet = new HczAppFuncNet(getActivity(), new Handler());
        appFuncNet.putParams(Constants.serviceList.get(selectedIndex - 1).getId());
        appFuncNet.sendRequest();
    }

    private void checkCity() {
        if (Constants.APP_USER_TYPE.equals(serviceName)) {
            hczLayout.setVisibility(View.VISIBLE);
            hswLayout.setVisibility(View.GONE);
            Constants.isRegistShow = false;
        } else {
            hczLayout.setVisibility(View.GONE);
            hswLayout.setVisibility(View.VISIBLE);
        }
        Constants.cityList = Constants.serverCityMap.get(serviceName);
        Constants.cityStrList.clear();
        if (Constants.cityList != null && Constants.cityList.size() > 0) {
            for (City city : Constants.cityList) {
                Constants.cityStrList.add(city.getProvinceName());
            }
        }
        if (Constants.cityStrList.size() > 0) {
            cityName = Constants.cityStrList.get(0);
            Constants.SERVER_URL = "http://" + Constants.cityList.get(0).getServerip() + ":" + Constants.cityList.get(0).getServerport();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /*
     * 登录请求操作
     */
    @SuppressLint("WrongConstant")
    private void getLoginFeedBack() {
        if (!Constants.isNetWork) {
            ActivityUtil.showPopWindow4Tips(getActivity(), cityTextView, false, "当前网络不可用，请稍后再试...");
            return;
        }
        final String account = loginNumEditText.getText().toString();
        if ("".equals(account)) {
            ActivityUtil.showPopWindow4Tips(getActivity(), cityTextView, false, "请输入宽带账号或手机号");
            return;
        }
        final String service = serviceTextView.getText().toString();
        if (null == service || service.equals("")) {
            ActivityUtil.showPopWindow4Tips(getActivity(), cityTextView, false, "请选择业务");
            return;
        }
        final String city = cityTextView.getText().toString();
        if (null == city || city.equals("")) {
            ActivityUtil.showPopWindow4Tips(getActivity(), cityTextView, false, "请选择地区");
            return;
        }
        String password = loginPwdEditText.getText().toString();
        if ("".equals(password)) {
            ActivityUtil.showPopWindow4Tips(getActivity(), cityTextView, false, "请输入密码");
            return;
        } else if (!password.matches("[0-9a-zA-z]{6,20}")) {
            ActivityUtil.showPopWindow4Tips(getActivity(), cityTextView, false, "密码格式不正确，请输入不少于6位的密码");
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
        if (TextUtils.isEmpty(Constants.SERVER_URL) || Constants.SERVER_URL.equals(Constants.SERVER_URL_GLOBAL)) {
            ActivityUtil.showPopWindow4Tips(getActivity(), cityTextView, false, "运营商未开通");
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
                    ActivityUtil.goMainActivity(context);
                } else {
                    ActivityUtil.showPopWindow4Tips(getActivity(), cityTextView, false, result.extra + "");
                }
            }

            @Override
            public void onFailure() {
                ActivityUtil.showPopWindow4Tips(getActivity(), cityTextView, false, "服务器忙，请稍候再试");

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

    private void getSavedDatas() {
        mobile = SharUtil.getPhone(context);
        cityName = SharUtil.getCity(context);
        serviceName = SharUtil.getService(context);
    }

    /*
     * 切换 城市 列表
     */
    protected void doCity(final TextView cityView) {
        if (!Constants.isNetWork) {
            ActivityUtil.showPopWindow4Tips(getActivity(), hczloginNumEditText, false, "当前网络不可用，请稍后再试...");
            return;
        }
        if (Constants.cityStrList.size() == 0) {
            ActivityUtil.showPopWindow4Tips(getActivity(), hczloginNumEditText, false, "正在获取地区列表...");
            return;
        }
        ServiceOrTypeDialog dialog = ServiceOrTypeDialog.getInstance(getActivity());
        dialog.setDialogProperties("选择地区", Constants.cityStrList);
        dialog.setOnSaveServiceLister(new OnWheelViewListener() {

            @Override
            public void onSelected(int selectedIndex, String item) {
                cityView.setText(item);
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

    /**
     * 好成长登录界面
     */

    private final static String TAG = "LoginActivity";
    @ViewInject(R.id.hcz_login_item_et_num)
    private EditText hczloginNumEditText;
    @ViewInject(R.id.hcz_login_item_et_password)
    private EditText hczloginPwdEditText;
    @ViewInject(R.id.activity_login_delete_phone)
    private ImageView hczdeleteIv;
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
    @ViewInject(R.id.hcz_login_btn)
    private View loginBtn;
    @ViewInject(R.id.hcz_xieyi_text)
    private TextView xieyiView;
    @ViewInject(R.id.activity_code_login_getcode)
    private TextView codeView;
    @ViewInject(R.id.code_login)
    private TextView loginType;
    @ViewInject(R.id.hcz_pwd_login_adress_tv)
    private TextView hczCityTextView;
    @ViewInject(R.id.hcz_code_login_adress_tv)
    private TextView hczCodeCityTextView;
    @ViewInject(R.id.hcz_pwd_login_tv)
    private TextView hczServiceTextView;
    @ViewInject(R.id.hcz_code_login_tv)
    private TextView hczCodeServiceTextView;
    private int loginTypeNum = 2;
    private int time = 60;
    private Tencent mTencent;


    private void initHczLogin() {
        Constants.isShowLogin = true;
        xieyiView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        initLoginView(loginTypeNum);
    }


    private void userLogin() {
        if (!Constants.isNetWork) {
            ActivityUtil.showPopWindow4Tips(getActivity(), hczloginNumEditText, false, "当前网络不可用，请稍后再试...");
            return;
        }
        SharUtil.saveService(context, Constants.APP_USER_TYPE);
        String account = "";
        String password = "";
        String code = "";
        if (loginTypeNum == 1) {
            account = hczloginNumEditText.getText().toString();
            password = hczloginPwdEditText.getText().toString();
            final String service = hczServiceTextView.getText().toString();
            if (null == service || service.equals("")) {
                ActivityUtil.showPopWindow4Tips(getActivity(), cityTextView, false, "请选择业务");
                return;
            }
            final String city = hczCityTextView.getText().toString();
            if (null == city || city.equals("")) {
                ActivityUtil.showPopWindow4Tips(getActivity(), cityTextView, false, "请选择地区");
                return;
            }
            if ("".equals(account)) {
                ActivityUtil.showPopWindow4Tips(getActivity(), hczloginNumEditText, false, "请输入手机号");
                return;
            }
            if ("".equals(password)) {
                ActivityUtil.showPopWindow4Tips(getActivity(), hczloginNumEditText, false, "请输入密码");
                return;
            } else if (!password.matches("[0-9a-zA-z]{6,20}")) {
                ActivityUtil.showPopWindow4Tips(getActivity(), hczloginNumEditText, false, "密码格式不正确，请输入不少于6位的密码");
                return;
            }
        } else {
            account = codeLoginNumEditText.getText().toString();
            code = codeLoginPwdEditText.getText().toString();
            final String service = hczCodeServiceTextView.getText().toString();
            if (null == service || service.equals("")) {
                ActivityUtil.showPopWindow4Tips(getActivity(), cityTextView, false, "请选择业务");
                return;
            }
            final String city = hczCodeCityTextView.getText().toString();
            if (null == city || city.equals("")) {
                ActivityUtil.showPopWindow4Tips(getActivity(), cityTextView, false, "请选择地区");
                return;
            }
            if ("".equals(account)) {
                ActivityUtil.showPopWindow4Tips(getActivity(), hczloginNumEditText, false, "请输入手机号");
                return;
            }
            if (null == code || "".equals(code)) {
                ActivityUtil.showPopWindow4Tips(getActivity(), hczloginNumEditText, false, "请输入正确的验证码");
                return;
            }

        }
        ActivityUtil.showPopWindow4Tips(getActivity(), hczCityTextView, false, false, "正在登录...", -1);
        HczLoginNet hczLoginNet = new HczLoginNet(getActivity(), new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case What.HTTP_REQUEST_CURD_SUCCESS:
                        ActivityUtil.showPopWindow4Tips(getActivity(), hczloginNumEditText, true, "登录成功");
                        Parents parents = (Parents) msg.obj;
                        if (parents.getPassword() == null || parents.getPassword().equals("")) {
                            startActivity(new Intent(getActivity(), HczSetPwdActivity.class));
                        } else {
                            ActivityUtil.goMainActivity(getActivity());
                        }
                        break;
                    case What.HTTP_REQUEST_CURD_FAILURE:
                        ActivityUtil.showPopWindow4Tips(getActivity(), hczloginNumEditText, false, msg.obj.toString() + "");
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
        hczCityTextView.setText(cityName);
        hczServiceTextView.setText(serviceName);
        hczCodeCityTextView.setText(cityName);
        hczCodeServiceTextView.setText(serviceName);
        if (type == 1) {
            codeLoginLl.setVisibility(View.GONE);
            pwdLoginLl.setVisibility(View.VISIBLE);
            hczloginNumEditText.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence chars, int arg1, int arg2, int arg3) {
                    if (chars.length() > 0) {
                        hczdeleteIv.setVisibility(View.VISIBLE);
                    } else {
                        hczdeleteIv.setVisibility(View.GONE);
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
                hczloginNumEditText.setText(mobile);
                hczloginNumEditText.setSelection(mobile.length());
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
//                    if (arg0.length() == 6) {
//                        loginBtn.setEnabled(true);
//                        loginBtn.setBackgroundResource(R.drawable.view_bottom_button_bg);
//                    } else {
//                        loginBtn.setEnabled(false);
//                        loginBtn.setBackgroundResource(R.drawable.view_btn_gray__bg);
//                    }
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
            ActivityUtil.showPopWindow4Tips(getActivity(), codeLoginNumEditText, false, "当前网络不可用，请稍后再试...");
            codeView.setEnabled(true);
            return;
        }
        String phoneNum = codeLoginNumEditText.getText().toString();
        if ("".equals(phoneNum) || !phoneNum.matches("^[1-9]\\d{10}$")) {
            ActivityUtil.showPopWindow4Tips(getActivity(), codeLoginNumEditText, false, "请输入正确的手机号");
            codeView.setEnabled(true);
            return;
        }
        codeView.setEnabled(false);
        HczGetCodeNet getCodeNet = new HczGetCodeNet(getActivity(), new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case What.HTTP_REQUEST_CURD_SUCCESS:
                        ActivityUtil.showPopWindow4Tips(getActivity(), codeLoginNumEditText, true, "发送验证码成功");
                        time = 60;
                        mHandler.sendEmptyMessageDelayed(RegisterCodeTimer.IN_RUNNING, 0);
                        break;
                    case What.HTTP_REQUEST_CURD_FAILURE:
                        time = 60;
                        ActivityUtil.showPopWindow4Tips(getActivity(), codeLoginNumEditText, false, "发送验证码失败");
                        mHandler.sendEmptyMessageDelayed(RegisterCodeTimer.END_RUNNING, 0);
                        break;
                }
            }
        });
        getCodeNet.putParams(phoneNum, 1);
        getCodeNet.sendRequest();
    }

    @OnClick(R.id.activity_login_delete_phone)
    public void onClickDeletePhone(View view) {
        hczloginNumEditText.setText("");
    }

    @OnClick(R.id.activity_login_code_delete_phone)
    public void onClickDeleteCodePhone(View view) {
        codeLoginNumEditText.setText("");
    }


    @OnClick(R.id.hcz_login_btn)
    public void loginClick(View view) {
        if (login_ok) {
            userLogin();
        }
    }

    @OnClick(R.id.hcz_xieyi_text)
    public void onXieyiTextClick(View view) {
        ActivityUtil.showXyDialog(context);
    }

    @OnClick(R.id.hcz_login_item_forget_tv)
    public void onFindPasswrodClick(View view) {
        Intent intent = new Intent(getActivity(), FindPasswordActivity.class);
        startActivity(intent);
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
    public void onWeixinLogin(View v) {
        ActivityUtil.showPopWindow4Tips(getActivity(), hczCityTextView, false, false, "正在登录...", -1);
        new ActivityUtil().weixinLogin(getActivity(), new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case What.HTTP_REQUEST_CURD_SUCCESS:
                        otherLogin(Constants.UserId, "weixin");
                        break;
                    case What.HTTP_REQUEST_CURD_FAILURE:
                        ActivityUtil.showPopWindow4Tips(getActivity(), codeLoginNumEditText, false, "微信登录失败");
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
        ActivityUtil.showPopWindow4Tips(getActivity(), hczCityTextView, false, false, "正在登录...", -1);
        mTencent = Tencent.createInstance(Constants.QQ_API_KEY, context);
        mTencent.login(this, "all", iUiListener);
    }

    /**
     * @param userId
     * @param type
     */
    private void otherLogin(String userId, String type) {
        ActivityUtil.showPopWindow4Tips(getActivity(), hczCityTextView, false, false, "正在登录...", -1);
        SharUtil.saveService(getActivity(), Constants.APP_USER_TYPE);
        HczWXLoginNet otherLoginNet = new HczWXLoginNet(getActivity(), new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case What.HTTP_REQUEST_CURD_SUCCESS:
                        int type = (int) msg.obj;
                        if (type == 2) {
                            startActivity(new Intent(getActivity(), HczBindActivity.class));
                        } else {
                            ActivityUtil.showPopWindow4Tips(getActivity(), codeLoginNumEditText, true, "登录成功");
                            ActivityUtil.goMainActivity(getActivity());
                        }
                        break;
                    case What.HTTP_REQUEST_CURD_FAILURE:
                        ActivityUtil.showPopWindow4Tips(getActivity(), codeLoginNumEditText, false, "登录失败");
                        break;
                }
            }
        });
        otherLoginNet.putParams(userId, type);
        otherLoginNet.sendRequest();
    }

    private IUiListener iUiListener = new IUiListener() {
        @Override
        public void onComplete(Object response) {
            if (null == response) {
                ActivityUtil.showPopWindow4Tips(getActivity(), codeLoginNumEditText, false, "登录失败");
                return;
            }
            ActivityUtil.showPopWindow4Tips(getActivity(), hczCityTextView, false, false, "正在登录...", -1);
            JSONObject jsonResponse = (JSONObject) response;
            if (null != jsonResponse && jsonResponse.length() == 0) {
                return;
            }
            try {
                Constants.UserId = jsonResponse.getString("openid");
                String access_token = jsonResponse.getString("access_token");
                String expires = jsonResponse.getString("expires_in");
                mTencent.setOpenId(Constants.UserId);
                mTencent.setAccessToken(access_token, expires);
                QQToken qqToken = mTencent.getQQToken();
                UserInfo info = new UserInfo(getActivity(), qqToken);
                info.getUserInfo(new IUiListener() {
                    @Override
                    public void onComplete(Object response) {
                        JSONObject jo = (JSONObject) response;
                        try {
                            Constants.UserName = jo.getString("nickname");
                            Constants.UserHeadUrl = jo.getString("figureurl_1");
                            otherLogin(Constants.UserId, "qq");
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
                ActivityUtil.showPopWindow4Tips(getActivity(), codeLoginNumEditText, false, "QQ登录失败");
            }
        }

        @Override
        public void onError(UiError uiError) {
            Log.i(TAG, "onComplete: " + uiError.toString());
            ActivityUtil.showPopWindow4Tips(getActivity(), codeLoginNumEditText, false, "QQ登录失败");
        }

        @Override
        public void onCancel() {
            ActivityUtil.showPopWindow4Tips(getActivity(), codeLoginNumEditText, false, "QQ登录取消");
        }
    };

    @OnClick(R.id.hcz_code_login_adress_iv)
    public void onCodeCityClick(View view) {
        doCity(hczCodeCityTextView);

    }

    @OnClick(R.id.hcz_code_login_service)
    public void onCodeServerClick(View view) {
        doServiceDialog(hczCodeServiceTextView);
    }

    @OnClick(R.id.hcz_pwd_login_adress_iv)
    public void onPwdCityClick(View view) {
        doCity(hczCityTextView);
    }

    @OnClick(R.id.hcz_pwd_login_service)
    public void onPwdServerClick(View view) {
        doServiceDialog(hczServiceTextView);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == com.tencent.connect.common.Constants.REQUEST_LOGIN) {
            Tencent.onActivityResultData(requestCode, resultCode, data, iUiListener);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}
