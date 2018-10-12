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
import com.goodsurfing.app.R.id;
import com.goodsurfing.base.BaseActivity;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.main.EditPhoneActivity;
import com.goodsurfing.server.net.HczSetPersonInfoNet;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.CommonUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 个人中心界面
 *
 * @author 谢志杰
 * @version 1.0.0
 * @create 2017-10-16 上午11:10:09
 * @company 湖南一石科技有限公司 Copyright: 版权所有（c）2016
 */
public class HczPersonInfoActivity extends BaseActivity {

    private final static String TAG = "PersonInfoActivity";

    protected static final int REFRESH = 100;

    protected static final int FLASH_USER = 0;

    private Context context;

    @ViewInject(id.tv_title)
    private TextView title;

    @ViewInject(id.tv_title_right)
    private TextView right;

    @ViewInject(id.activity_settings_name_et)
    private EditText nameEt;

    @ViewInject(id.activity_settings_phone_et)
    private TextView phoneEt;

    @ViewInject(R.id.activity_settings_time_et)
    private  TextView timeTv;

    @ViewInject(id.activity_settings_about_id)
    private EditText idEt;

    @ViewInject(id.title_layout)
    private View title_layout;
    @ViewInject(id.activity_settings_about_id_rl)
    private View id_layout;

    @ViewInject(id.qrc_code_rl)
    private View codeRlView;
    private boolean isEdit = false;

    private String mobile;

    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hcz_person_info);
        ViewUtils.inject(this);
        init();
    }

    @OnClick(id.activity_settings_phone_et)
    private void editPhone(View v) {
        Intent intent = new Intent(HczPersonInfoActivity.this, EditPhoneActivity.class);
        startActivity(intent);
    }

    private void init() {
        context = this;
        title.setText("账户信息");
        right.setText("保存");
    }

    @Override
    protected void onResume() {
        super.onResume();
        nameEt.setText(Constants.name);
        if (ActivityUtil.isMobileNO(Constants.userMobile)) {
            phoneEt.setText(Constants.userMobile);
        } else {
            phoneEt.setText("");
        }
        timeTv.setText(Constants.dealTime);
    }

    @OnClick(id.tv_title_right)
    public void onEditClick(View v) {
        mobile = phoneEt.getText().toString().trim();
        name = nameEt.getText().toString().trim();
        if (mobile.equals(Constants.userMobile) && name.equals(Constants.name) ) {
            ActivityUtil.showPopWindow4Tips(this, title_layout, false, "您没有修改任何数据");
        } else {
            sendUserInfoData();
        }
    }

    /**
     * 修改个人信息
     */
    private void sendUserInfoData() {
        if (!Constants.isNetWork) {
            ActivityUtil.showPopWindow4Tips(this, title_layout, false, "当前网络不可用，请稍后再试...");
            return;
        }
        if ("".equals(mobile) || !mobile.matches("^[1-9]\\d{10}$")) {
            ActivityUtil.showPopWindow4Tips(this, title_layout, false, "请输入正确的手机号");
            return;
        }
        ActivityUtil.showPopWindow4Tips(this, title_layout, false, false, "正在保存...", -1);
        HczSetPersonInfoNet setPersonInfoNet = new HczSetPersonInfoNet(this,new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case What.HTTP_REQUEST_CURD_SUCCESS:
                        ActivityUtil.showPopWindow4Tips(HczPersonInfoActivity.this, title_layout, true, "保存成功");
                        break;
                    case What.HTTP_REQUEST_CURD_FAILURE:
                        ActivityUtil.showPopWindow4Tips(HczPersonInfoActivity.this, title_layout, false, msg.obj.toString());
                        break;
                }
            }
        });
        setPersonInfoNet.putParams("",name,"");
        setPersonInfoNet.sendRequest();
    }

    @OnClick(id.iv_title_left)
    public void onHeadBackClick(View view) {
        onBackPressed();
    }
}
