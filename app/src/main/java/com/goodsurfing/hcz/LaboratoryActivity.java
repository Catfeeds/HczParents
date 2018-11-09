package com.goodsurfing.hcz;

import android.app.Dialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.component.constants.What;
import com.goodsurfing.app.R;
import com.goodsurfing.app.R.id;
import com.goodsurfing.base.BaseActivity;
import com.goodsurfing.beans.User;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.server.GetUserInfoServer;
import com.goodsurfing.server.PutDataServer;
import com.goodsurfing.server.net.HczSetDNSNet;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResponder;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResult;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.CommonUtil;
import com.goodsurfing.utils.QRCodeUtil;
import com.goodsurfing.utils.SharUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import org.json.JSONObject;

/**
 * 个人中心界面
 *
 * @author 谢志杰
 * @version 1.0.0
 * @create 2017-10-16 上午11:10:09
 * @company 湖南一石科技有限公司 Copyright: 版权所有（c）2016
 */
public class LaboratoryActivity extends BaseActivity {

    private final static String TAG = "LaboratoryActivity";
    @ViewInject(R.id.tv_title)
    private TextView title;

    @ViewInject(id.laboratory_dns_cb)
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laboratory);
        ViewUtils.inject(this);
        init();
    }

    @OnClick(id.laboratory_dns_cb)
    public void onlaClike(View view) {
        checkBox.setChecked(!checkBox.isChecked());
        if ("".equals(Constants.userId)) {
            HczLoginActivity.gotoLogin(this);
            return;
        }
        if (!Constants.isNetWork) {
            ActivityUtil.showPopWindow4Tips(this, title, false, "当前网络不可用，请稍后再试...");
            return;
        }
        if (Constants.child == null) {
            ActivityUtil.showPopWindow4Tips(this, title, false, "请绑定孩子手机后操作");
            return;
        }
        checkBox.setChecked(!checkBox.isChecked());
        switch (view.getId()) {
            case id.laboratory_dns_cb:
                openDNS();
                break;
        }

    }

    private void openDNS(){
        HczSetDNSNet hczSetDNSNet = new HczSetDNSNet(this,new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case What.HTTP_REQUEST_CURD_SUCCESS:
                        if(msg.obj!=null&&!TextUtils.isEmpty(msg.obj.toString())){
                            ActivityUtil.showPopWindow4Tips(LaboratoryActivity.this, title, true, msg.obj.toString());
                        }else
                        ActivityUtil.showPopWindow4Tips(LaboratoryActivity.this, title, true, "切换成功");
                        break;
                    case What.HTTP_REQUEST_CURD_FAILURE:
                        checkBox.setChecked(!checkBox.isChecked());
                        ActivityUtil.showPopWindow4Tips(LaboratoryActivity.this, title, false, msg.obj.toString() + "");
                        break;
                }
                if(checkBox.isChecked()){
                    Constants.child.setOpenDNS(1);
                }else {
                    Constants.child.setOpenDNS(2);
                }

            }
        });
        if(checkBox.isChecked()){
            hczSetDNSNet.putParams(1);
        }else {
            hczSetDNSNet.putParams(2);
        }
        hczSetDNSNet.sendRequest();
    }


    private void init() {
        title.setText("实验室");
        checkBox.setChecked(Constants.child!=null&&Constants.child.getOpenDNS()==1);
    }

    @OnClick(R.id.iv_title_left)
    public void onHeadBackClick(View view) {
        onBackPressed();
    }
}
