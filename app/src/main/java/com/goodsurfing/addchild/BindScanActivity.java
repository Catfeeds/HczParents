package com.goodsurfing.addchild;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.component.constants.What;
import com.goodsurfing.app.R;
import com.goodsurfing.base.BaseActivity;
import com.goodsurfing.server.net.HczGetBindCodeNet;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class BindScanActivity extends BaseActivity {

    protected static final int REFRESH = 100;

    @ViewInject(R.id.tv_title)
    private TextView title;

    @ViewInject(R.id.tv_title_right)
    private TextView right;

    @ViewInject(R.id.bind_code_tv)
    private TextView getCodeBtn;


    @ViewInject(R.id.down_load_app_tv)
    private TextView downLoadBtn;


    @ViewInject(R.id.dbv)
    DecoratedBarcodeView mDBV;
    private CaptureManager captureManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_scan);
        ViewUtils.inject(this);
        captureManager = new CaptureManager(this,mDBV);
        captureManager.initializeFromIntent(getIntent(),savedInstanceState);
        captureManager.decode();
        init();
    }
    @Override
    protected void onPause() {
        super.onPause();
        captureManager.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        captureManager.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        captureManager.onDestroy();
    }
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        captureManager.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return mDBV.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }
    private void init() {
        title.setText("关联好上网孩子端");
        right.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.iv_title_left)
    public void onHeadBackClick(View view) {
        onBackPressed();
    }

    @OnClick(R.id.bind_code_tv)
    public void getCodeView(View v) {
        startActivity(new Intent(this,AddChildActivity.class));
    }


    @OnClick(R.id.down_load_app_tv)
    public void gotoDownApp(View view) {
        startActivity(new Intent(this, ShowChildAppLoadCodeActivity.class));
    }
}
