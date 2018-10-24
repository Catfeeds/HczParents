package com.goodsurfing.main;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.goodsurfing.app.R;
import com.goodsurfing.app.R.id;
import com.goodsurfing.base.BaseActivity;
import com.goodsurfing.beans.User;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.server.GetUserInfoServer;
import com.goodsurfing.server.PutDataServer;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResponder;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResult;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.CommonUtil;
import com.goodsurfing.utils.QRCodeUtil;
import com.goodsurfing.utils.SharUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.umeng.analytics.MobclickAgent;

/**
 * 个人中心界面
 *
 * @author 谢志杰
 * @version 1.0.0
 * @create 2017-10-16 上午11:10:09
 * @company 湖南一石科技有限公司 Copyright: 版权所有（c）2016
 */
public class PersonInfoActivity extends BaseActivity {

    private final static String TAG = "PersonInfoActivity";

    protected static final int REFRESH = 100;

    protected static final int FLASH_USER = 0;

    private Context context;

    @ViewInject(R.id.tv_title)
    private TextView title;

    @ViewInject(R.id.tv_title_right)
    private TextView right;

    @ViewInject(R.id.activity_settings_name_et)
    private EditText nameEt;

    @ViewInject(R.id.activity_settings_phone_et)
    private TextView phoneEt;

    @ViewInject(R.id.activity_settings_adress_et)
    private EditText adressEt;

    @ViewInject(R.id.activity_settings_about_et)
    private EditText accetEt;

    @ViewInject(R.id.activity_settings_mode_et)
    private EditText modeEt;

    @ViewInject(R.id.activity_settings_time_et)
    private EditText timeEt;

    @ViewInject(R.id.activity_settings_about_id)
    private EditText idEt;

    @ViewInject(R.id.title_layout)
    private View title_layout;
    @ViewInject(R.id.activity_settings_about_id_rl)
    private View id_layout;

    @ViewInject(R.id.qrc_code_rl)
    private View codeRlView;
    private boolean isEdit = false;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case REFRESH:

                    break;

                default:
                    break;
            }
        }

        ;
    };

    private String mobile;

    private String name;

    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_info);
        ViewUtils.inject(this);
        init();
    }

    @OnClick(R.id.activity_settings_phone_et)
    private void editPhone(View v) {
//        Intent intent = new Intent(PersonInfoActivity.this, EditPhoneActivity.class);
//        startActivity(intent);
    }

    private void init() {
        context = this;
        title.setText("账户信息");
        right.setText("保存");
        handler.sendEmptyMessage(FLASH_USER);
        String url = Constants.SERVER_URL + "?" + "requesttype=11" + "&userid=" + Constants.userId + "&token=" + Constants.tokenID;
        new GetUserInfoServer(new DataServiceResponder() {

            @Override
            public void onResult(DataServiceResult result) {
                if ("0".equals(result.code)) {
                    handler.sendEmptyMessage(FLASH_USER);
                }
            }

            @Override
            public void onFailure() {

            }
        }, url, context).execute();

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case FLASH_USER:
                    try {
                        accetEt.setText(Constants.Account);
                        timeEt.setText(Constants.dealTime);
                        nameEt.setText(Constants.name);
                        if (ActivityUtil.isMobileNO(Constants.userMobile)) {
                            phoneEt.setText(Constants.userMobile);
                        } else {
                            Constants.userMobile = "";
                            phoneEt.setText("");
                            User user = User.getUser();
                            user.setPhone(Constants.userMobile);
                            CommonUtil.setUser(PersonInfoActivity.this, user);
                            SharUtil.savePhone(PersonInfoActivity.this, Constants.userMobile);
                        }
                        idEt.setText(Constants.rewardCode);
                        adressEt.setText(Constants.adress);
                        switch (Constants.mode) {
                            case Constants.MODE_FREE:
                                modeEt.setText("网络畅游模式");
                                break;
                            case Constants.MODE_BAD:
                                modeEt.setText("教育资源模式");

                                break;
                            case Constants.MODE_LEARNING:
                                modeEt.setText("不良内容拦截模式");

                                break;
                            case Constants.MODE_REWARDS:

                                modeEt.setText("奖励卡模式");
                                break;
                        }
                    } catch (Exception e) {
                    }
                    break;
            }
        }

        ;
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityUtil.isMobileNO(Constants.userMobile)) {
            phoneEt.setText(Constants.userMobile);
        } else {
            Constants.userMobile = "";
            phoneEt.setText("");
            User user = User.getUser();
            user.setPhone(Constants.userMobile);
            CommonUtil.setUser(PersonInfoActivity.this, user);
            SharUtil.savePhone(PersonInfoActivity.this, Constants.userMobile);
        }
        if (!SharUtil.getService(this).equals(Constants.APP_USER_TYPE)) {
            if (System.currentTimeMillis() / 1000 - Long.valueOf(Constants.loginTime) > 48 * 60 * 60) {
                showCodeView = true;
                id_layout.setVisibility(View.GONE);
            }
        } else {
            showCodeView = true;
            id_layout.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.tv_title_right)
    public void onEditClick(View v) {
        mobile = phoneEt.getText().toString().trim();
        name = nameEt.getText().toString().trim();
        address = adressEt.getText().toString().trim();
        if (mobile.equals(Constants.userMobile) && name.equals(Constants.name) && address.equals(Constants.adress)) {
            ActivityUtil.showPopWindow4Tips(this, title_layout, false, "您没有修改任何数据");
        } else {
            sendUserInfoData();
        }
    }

    private long mLastTime = 0;
    private long mCurTime = 0;
    private int click;
    private boolean showCodeView = false;

    @OnClick(R.id.qrc_code_rl)
    public void onCodeClick(View v) {
//        if (showCodeView)
//            return;
//        mLastTime = mCurTime;
//        mCurTime = System.currentTimeMillis();
//        if (mCurTime - mLastTime < 200) {
//            click++;
//        } else {
//            click = 0;
//        }
//        if (click >= 3) {
//            showCodeImagview();
//        }
    }

    /**
     * 显示推广二维码
     */
    private void showCodeImagview() {
        showCodeView = true;

        final Dialog dialog = new Dialog(this, R.style.AlertDialogCustom);
        dialog.setCancelable(false);
        View view = View.inflate(this, R.layout.layout_qrc_code, null);
        ImageView codeImageView = (ImageView) view.findViewById(R.id.qrc_code_iv);
        View codeLl = view.findViewById(R.id.qrc_code_ll);
        codeImageView.setImageBitmap(BitmapFactory.decodeFile(QRCodeUtil.createQRImage(this, "http://www.haoup.net/Weixin/Index/index/state/" + Constants.rewardCode, 300, 300, BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))));
        codeLl.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showCodeView = false;
                dialog.dismiss();
            }
        });
        dialog.setContentView(view);
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); // 获取对话框当前的参数值
        p.width = (int) (d.getWidth() * 1.0); // 宽度设置为屏幕的0.95
        p.height = (int) (d.getHeight() * 1.0); // 宽度设置为屏幕的0.95
        dialog.getWindow().setAttributes(p);
        dialog.show();

    }

    /**
     * 修改个人信息
     */
    private void sendUserInfoData() {
        if (!Constants.isNetWork) {
            ActivityUtil.showPopWindow4Tips(this, title_layout, false, "当前网络不可用，请稍后再试...");
            return;
        }
        /**
         * get请求参数：userid=11111& token =fasfasfa&committype=4&type=value
         * type为对应的字段，value为修改后的值；
         */

        if ("".equals(mobile) || !mobile.matches("^[1-9]\\d{10}$")) {
            ActivityUtil.showPopWindow4Tips(this, title_layout, false, "请输入正确的手机号");
            return;
        }
        String url = Constants.SERVER_URL + "?" + "committype=4" + "&userid=" + Constants.userId + "&token=" + Constants.tokenID + "&name=" + name + "&type=" + "2" + "&model=" + android.os.Build.MODEL + "&Mobile=" + mobile + "&address=" + address;
        ActivityUtil.showPopWindow4Tips(this, title_layout, false, false, "正在保存...", -1);

        new PutDataServer(new DataServiceResponder() {

            @Override
            public void onResult(DataServiceResult result) {
                if ("0".equals(result.code)) {
                    try {
                        JSONObject jsonObject;
                        jsonObject = new JSONObject(result.extra);
                        JSONObject userjson = jsonObject.getJSONObject("user");
                        String mode = userjson.getString("Mode");
                        Constants.Account = userjson.getString("Account");
                        Constants.dealTime = userjson.getString("ExpirationTime");

                        try {
                            Constants.userMobile = userjson.getString("Mobile");
                        } catch (Exception e) {
                        }
                        try {
                            Constants.adress = userjson.getString("address");
                        } catch (Exception e) {
                        }
                        try {
                            Constants.name = userjson.getString("name");
                        } catch (Exception e) {
                        }
                        Constants.mode = Integer.parseInt(mode);
                        User user = User.getUser();
                        user.setuId(Constants.userId);
                        user.setTokenId(Constants.tokenID);
                        user.setPhone(Constants.userMobile);
                        user.setMode(mode);
                        user.setAccount(Constants.Account);
                        user.setUserName(Constants.name);
                        user.setEditTime(Constants.dealTime);
                        user.setUserSex(Constants.sex);
                        user.setEmail(Constants.email);
                        user.setAvatar(Constants.birthday);
                        user.setAddress(Constants.adress);
                        CommonUtil.setUser(context, user);
                        SharUtil.savePhone(context, Constants.userMobile);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    ActivityUtil.showPopWindow4Tips(PersonInfoActivity.this, title_layout, true, "保存成功");
                } else {
                    ActivityUtil.showPopWindow4Tips(PersonInfoActivity.this, title_layout, false, result.extra + "");
                }
            }

            @Override
            public void onFailure() {

            }
        }, url, context, false, true).execute();

    }

    @OnClick(R.id.iv_title_left)
    public void onHeadBackClick(View view) {
        onBackPressed();
    }
}
