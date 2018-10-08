package com.goodsurfing.addchild;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.component.constants.What;
import com.goodsurfing.app.R;
import com.goodsurfing.base.BaseActivity;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.server.net.HczGetBindCodeNet;
import com.goodsurfing.server.net.HczScanBindNet;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.LogUtil;
import com.goodsurfing.utils.SignUtils;
import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddChildActivity extends BaseActivity {

    protected static final int REFRESH = 100;
    protected static final int SCANCODE = 101;

    @ViewInject(R.id.tv_title)
    private TextView title;

    @ViewInject(R.id.tv_title_right)
    private TextView right;

    @ViewInject(R.id.down_load_app_tv)
    private TextView downLoadBtn;


    @ViewInject(R.id.add_child_code_btn)
    private TextView getCodeBtn;
    @ViewInject(R.id.bind_code_tv)
    private TextView showCodeTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addchild);
        ViewUtils.inject(this);
        init();
    }

    private void init() {
        title.setText("关联好上网孩子端");
        right.setVisibility(View.INVISIBLE);
        downLoadBtn.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        getCodeBtn.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        getCodeView();
    }


    @OnClick(R.id.iv_title_left)
    public void onHeadBackClick(View view) {
        onBackPressed();
    }

    @OnClick(R.id.add_child_code_btn)
    public void gotoBindScan(View v) {
        startActivityForResult(new Intent(this, BindScanActivity.class), SCANCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == SCANCODE) {
            String result = data.getStringExtra(Intents.Scan.RESULT);
            if (result != null) {
                try {
                    String[] strs = result.split("[?]");
                    if (strs != null && strs.length == 2) {
                        Log.i("onActivityResult", "onActivityResult: " + result);
                        String parms = SignUtils.decrypt(strs[1], SignUtils.getPrivateKey(Constants.HCZ_RSA_PRIVATE));
                        Uri uri = Uri.parse(strs[0] + "?" + parms);
                        String system = uri.getQueryParameter("s");
                        String channel = uri.getQueryParameter("c");
                        String device = uri.getQueryParameter("d");
                        String uuid = uri.getQueryParameter("UUID");
                        String mobile = uri.getQueryParameter("m");
                        HczScanBindNet scanBindNet = new HczScanBindNet(this, new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                switch (msg.what) {
                                    case What.HTTP_REQUEST_CURD_SUCCESS:
                                        ActivityUtil.showPopWindow4Tips(AddChildActivity.this, title, true, "关联成功");
                                        startActivity(new Intent(AddChildActivity.this,ChildListActivity.class));
                                        finish();
                                        break;
                                    case What.HTTP_REQUEST_CURD_FAILURE:
                                        ActivityUtil.showPopWindow4Tips(AddChildActivity.this, title, false, msg.obj.toString());
                                        break;
                                }
                            }
                        });
                        scanBindNet.putParams(uuid, mobile, device, system);
                        scanBindNet.sendRequest();
                    }else{
                        ActivityUtil.showPopWindow4Tips(AddChildActivity.this, title, false, "二维码内容错误，请扫描正确二维码");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ActivityUtil.showPopWindow4Tips(AddChildActivity.this, title, false, "二维码内容错误，请扫描正确二维码");
                }
            }else{
                ActivityUtil.showPopWindow4Tips(AddChildActivity.this, title, false, "二维码内容错误，请扫描正确二维码");

            }
        }
    }

    private void getCodeView() {
        HczGetBindCodeNet getBindCodeNet = new HczGetBindCodeNet(this, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case What.HTTP_REQUEST_CURD_SUCCESS:
                        showBindCode(msg.obj.toString());
                        break;
                    case What.HTTP_REQUEST_CURD_FAILURE:
                        break;
                }
            }
        });
        getBindCodeNet.putParams();
        getBindCodeNet.sendRequest();
    }

    private void showBindCode(String code) {
        showCodeTv.setText(code);
    }


    @OnClick(R.id.down_load_app_tv)
    public void gotoDownApp(View view) {
        startActivity(new Intent(this, ShowChildAppLoadCodeActivity.class));
    }
}
