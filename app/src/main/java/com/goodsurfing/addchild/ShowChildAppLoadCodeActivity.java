package com.goodsurfing.addchild;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.goodsurfing.app.R;
import com.goodsurfing.base.BaseActivity;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.utils.LogUtil;
import com.goodsurfing.utils.QRCodeUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class ShowChildAppLoadCodeActivity extends BaseActivity {

    protected static final int REFRESH = 100;

    @ViewInject(R.id.tv_title)
    private TextView title;

    @ViewInject(R.id.tv_title_right)
    private TextView right;

    @ViewInject(R.id.down_load_app_iv)
    private ImageView downIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_erweima);
        ViewUtils.inject(this);
        init();
    }

    private void init() {
        title.setText("下载二维码");
        right.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int width = (int) getResources().getDimension(R.dimen.dimen_155dp);
        int height = (int) getResources().getDimension(R.dimen.dimen_155dp);
        downIv.setImageBitmap(BitmapFactory.decodeFile(QRCodeUtil.createQRImage(this, "http://www.haoup.net/d?uid=" + Constants.userId, width, height, BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))));

    }

    @OnClick(R.id.iv_title_left)
    public void onHeadBackClick(View view) {
        onBackPressed();
    }
}
