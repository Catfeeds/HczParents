package com.goodsurfing.main;

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
import android.text.TextWatcher;
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
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.component.constants.What;
import com.goodsurfing.addchild.AddChildActivity;
import com.goodsurfing.addchild.ChildListActivity;
import com.goodsurfing.app.R;
import com.goodsurfing.app.wxapi.ShareWeiXinActivity;
import com.goodsurfing.base.BaseFragment;
import com.goodsurfing.beans.Parents;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.fundlock.GestureEditActivity;
import com.goodsurfing.fundlock.GestureVerifyActivity;
import com.goodsurfing.server.CheckServerServer;
import com.goodsurfing.server.GetServerListServer;
import com.goodsurfing.server.LoginServer;
import com.goodsurfing.server.net.HczGetCodeNet;
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
import com.goodsurfing.view.customview.city.LocationDialogBuilder;
import com.goodsurfing.view.customview.city.LocationDialogBuilder.OnSaveLocationLister;
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

import java.security.MessageDigest;

public class MainMyActivity1 extends BaseFragment implements OnClickListener {
    private RelativeLayout myLayout; // 个人信息
    private RelativeLayout xgLayout;// 修改密码
    private RelativeLayout xgssLayout;// 修改手势密码
    private RelativeLayout helpLayout;// 帮助中心
    private RelativeLayout aboutLayout;// 关于好上网
    private RelativeLayout fxLayout;// 分享
    private RelativeLayout xfLayout;// 续费
    private RelativeLayout addChildLayout;// 孩子设备
    private CheckBox ssCheckBox;// 手势密码开启ChceBox
    private Button loginButton;
    private View lineView;
    private View rootView;
    private TextView bindTextView;
    private View loginView;
    private View myView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null ==rootView) {
            rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_my, null);
            ViewUtils.inject(this, rootView);
            initViews(rootView);
            init();
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
        addChildLayout = (RelativeLayout) view.findViewById(R.id.activity_my_add_child);
        ssCheckBox = (CheckBox) view.findViewById(R.id.activity_my_ss_cb);
        loginButton = (Button) view.findViewById(R.id.activity_my_fl);
        lineView = view.findViewById(R.id.activity_my_set_shoushi_pass_line);
        bindTextView = (TextView) view.findViewById(R.id.activity_my_bind_tx);
        loginView = view.findViewById(R.id.login_view);
        myView =view.findViewById(R.id.hcz_my_view);
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
        addChildLayout.setOnClickListener(this);
        bindTextView.setVisibility(View.GONE);

    }

    @Override
    public void onResume() {
        super.onResume();
        if ("".equals(Constants.userId)) {
            SharUtil.saveService(getActivity(), Constants.APP_USER_TYPE);
            myView.setVisibility(View.GONE);
            loginView.setVisibility(View.VISIBLE);
            initLogin();
        } else {
            myView.setVisibility(View.VISIBLE);
            loginView.setVisibility(View.GONE);
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
                    HczLoginActivity.gotoLogin(getActivity());
                    return;
                }
                ActivityUtil.sendEvent4UM(getActivity(), "functionSwitch", "accountInfo", 4);
                Intent intent = new Intent(getActivity(), HczPersonInfoActivity.class);
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
                    HczLoginActivity.gotoLogin(getActivity());
                    return;
                }
                ActivityUtil.sendEvent4UM(getActivity(), "functionSwitch", "ShareWeiXin", 21);
                Intent fx = new Intent(getActivity(), ShareWeiXinActivity.class);
                startActivity(fx);
                break;
            case R.id.activity_my_xufei:
                if ("".equals(Constants.userId)) {
                    HczLoginActivity.gotoLogin(getActivity());
                    return;
                }
                ActivityUtil.sendEvent4UM(getActivity(), "functionSwitch", "ChargeChoices", 22);
                Intent xufei = new Intent(getActivity(), ChargeChoicesActivity.class);
                startActivity(xufei);
                break;
            case R.id.activity_my_set_shoushi_pass:
                if ("".equals(Constants.userId)) {
                    HczLoginActivity.gotoLogin(getActivity());
                    return;
                }
                ActivityUtil.sendEvent4UM(getActivity(), "functionSwitch", "GestureVerify", 23);
                Intent shoushi = new Intent(getActivity(), GestureVerifyActivity.class);
                shoushi.putExtra("edit", true);
                startActivity(shoushi);
                break;
            case R.id.activity_my_set_pass:
                if ("".equals(Constants.userId)) {
                    HczLoginActivity.gotoLogin(getActivity());
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
                    HczLoginActivity.gotoLogin(getActivity());
                    return;
                }
                dialog();
                break;
            case R.id.activity_my_ss_cb:
                onCheckSs(ssCheckBox.isChecked());
                break;
            case R.id.activity_login_item_rl_register:
                RegisterActivity.gotoRegister(getActivity());
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
            case R.id.activity_my_add_child:
                if (Constants.isbindChild) {
                    startActivity(new Intent(getActivity(), ChildListActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), AddChildActivity.class));
                }
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
        title.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dlg.dismiss();
            }
        });
        mCancleTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dlg.dismiss();
            }
        });
        comfirm.setOnClickListener(new OnClickListener() {
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
            HczLoginActivity.gotoLogin(getActivity());
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
        if (!"".equals(Constants.userId)) {
            loginPwdEditText.setText("");
            ActivityUtil.sendEvent4UM(getActivity(), "outLogin", "outLogin", 24);
            Constants.clear(getActivity());
            ((HczMainActivity)getActivity()).mainPageView.setCurrentItem(0);
        } else {
            ((HczMainActivity)getActivity()).mainPageView.setCurrentItem(0);
        }
    }

    /**
     * 好成长登录界面
     */

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
    private  Tencent mTencent;

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

    private void initLogin() {
        context = getActivity();
        mobile = SharUtil.getPhone(context);
        Constants.isShowLogin = true;
        xieyiView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        Constants.clear(getActivity());
        initLoginView(loginTypeNum);
    }


    private void userLogin() {
        if (!Constants.isNetWork) {
            ActivityUtil.showPopWindow4Tips(getActivity(), loginNumEditText, false, "当前网络不可用，请稍后再试...");
            return;
        }
        String account = "";
        String password = "";
        String code = "";
        if (loginTypeNum == 1) {
            account = loginNumEditText.getText().toString();
            password = loginPwdEditText.getText().toString();
            if ("".equals(password)) {
                ActivityUtil.showPopWindow4Tips(getActivity(), loginNumEditText, false, "请输入密码");
                return;
            } else if (!password.matches("[0-9a-zA-z]{6,20}")) {
                ActivityUtil.showPopWindow4Tips(getActivity(), loginNumEditText, false, "密码格式不正确，请输入不少于6位的密码");
                return;
            }
        } else {
            account = codeLoginNumEditText.getText().toString();
            code = codeLoginPwdEditText.getText().toString();
        }
        if ("".equals(account)) {
            ActivityUtil.showPopWindow4Tips(getActivity(), loginNumEditText, false, "请输入手机号");
            return;
        }
        HczLoginNet hczLoginNet = new HczLoginNet(getActivity(), new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case What.HTTP_REQUEST_CURD_SUCCESS:
                        Parents parents = (Parents) msg.obj;
                        if (parents.getPassword() == null || parents.getPassword().equals("")) {
                            startActivity(new Intent(getActivity(), HczSetPwdActivity.class));
                        } else {
                            ActivityUtil.goMainActivity(getActivity());
                        }
                        break;
                    case What.HTTP_REQUEST_CURD_FAILURE:
                        ActivityUtil.showPopWindow4Tips(getActivity(), loginNumEditText, false, msg.obj.toString() + "");
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
        loginNumEditText.setText("");
    }

    @OnClick(R.id.activity_login_code_delete_phone)
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
        Intent intent = new Intent(getActivity(), HczFindPwdActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.goto_hsw_login)
    public void onClickGotoHswLogin(View view) {
        Constants.isShowLogin = false;
        LoginActivity.gotoLogin(getActivity());
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
        new ActivityUtil().weixinLogin(getActivity(),new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case What.HTTP_REQUEST_CURD_SUCCESS:
                        otherLogin(Constants.UserId,"weixin");
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
         mTencent=Tencent.createInstance(Constants.QQ_API_KEY,context);
        mTencent.login(this, "all",iUiListener );
    }

    private void otherLogin(String userId, String type) {
        HczWXLoginNet otherLoginNet = new HczWXLoginNet(getActivity(), new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case What.HTTP_REQUEST_CURD_SUCCESS:
                        int type= (int) msg.obj;
                        if (type == 2) {
                            startActivity(new Intent(getActivity(), HczBindActivity.class));
                        }else{
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
               UserInfo info = new  UserInfo(getActivity(),qqToken);
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
                ActivityUtil.showPopWindow4Tips(getActivity(), codeLoginNumEditText, false, "QQ登录失败");
            }
        }

        @Override
        public void onError(UiError uiError) {
            Log.i(TAG, "onComplete: "+uiError.toString());
            ActivityUtil.showPopWindow4Tips(getActivity(), codeLoginNumEditText, false, "QQ登录失败");
        }

        @Override
        public void onCancel() {
            ActivityUtil.showPopWindow4Tips(getActivity(), codeLoginNumEditText, false, "QQ登录取消");
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
