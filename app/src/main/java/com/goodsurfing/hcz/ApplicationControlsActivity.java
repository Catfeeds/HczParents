package com.goodsurfing.hcz;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.goodsurfing.app.R;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.CommonUtil;
import com.goodsurfing.view.SwipeBackLayout;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.umeng.analytics.MobclickAgent;

public class ApplicationControlsActivity extends FragmentActivity {

    @ViewInject(R.id.tv_title)
    private TextView title;

    @ViewInject(R.id.tv_title_right)
    public static TextView comfirm;

    public static TextView message;

    public static Context mcontext;
    // 定义FragmentTabHost对象
    public static FragmentTabHost mTabHost;

    // 定义一个布局
    private LayoutInflater layoutInflater;

    // 定义数组来存放Fragment界面
    private Class fragmentArray[] = {AppListFragment.class, AuditAppListFragment.class};
    // Tab选项卡的文字
    private String mTextviewArray[] = {"应用列表", "待审核"};
    private View[] lins = new View[2];
    @ViewInject(R.id.pupwindow_blowe)
    public static View contentView;
    public static int index = 0;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarFullTransparent();
        contentView = LayoutInflater.from(this).inflate(R.layout.activity_app_controls, null);
        setContentView(contentView);
        ViewUtils.inject(this);
        init();
    }

    /**
     * 全透状态栏
     */
    protected void setStatusBarFullTransparent() {
        if (Build.VERSION.SDK_INT >= 21) {//21表示5.0
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= 19) {//19表示4.4
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //虚拟键盘也透明
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void init() {
        title.setText("应用管控");
        comfirm.setText("确定");
        comfirm.setVisibility(View.GONE);
        mcontext = this;
        initTableHost();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        CommonUtil.HandLockPassword(this);
        lins[index].setVisibility(View.VISIBLE);
        ActivityUtil.sendEvent4UM(this, "functionSwitch", "addwhitelist", 28);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!CommonUtil.isForeground(this)) {
            Constants.isAPPActive = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ApplicationControlsActivity.index = 0;
    }

    private void initTableHost() {
        // 实例化布局对象
        layoutInflater = LayoutInflater.from(this);

        // 实例化TabHost对象，得到TabHost
        mTabHost = (FragmentTabHost) findViewById(R.id.app_tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        // 得到fragment的个数
        int count = fragmentArray.length;
        for (int i = 0; i < count; i++) {
            // 为每一个Tab按钮设置图标、文字和内容
            TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
            // 将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
            // 设置Tab按钮的背景
            final int index = i;
            mTabHost.getTabWidget().getChildAt(index).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    switch (index) {
                        case 0:
                            lins[1].setVisibility(View.INVISIBLE);
                            lins[0].setVisibility(View.VISIBLE);
                            mTabHost.setCurrentTab(0);
                            break;
                        case 1:
                            lins[0].setVisibility(View.INVISIBLE);
                            lins[1].setVisibility(View.VISIBLE);
                            mTabHost.setCurrentTab(1);
                            break;
                    }
                }
            });

        }
    }

    private View getTabItemView(int index) {
        View view = layoutInflater.inflate(R.layout.black_white_item_layout, null);
        TextView textView = (TextView) view.findViewById(R.id.black_white_tv);
        textView.setText(mTextviewArray[index]);
        lins[index] = view.findViewById(R.id.black_white_v);
        if (index == 1) {
            TextView tipsView = (TextView) view.findViewById(R.id.black_white_hint_tv);
            if(Constants.child!=null&&Constants.child.isOpenAppStatus()) {
                tipsView.setVisibility(View.VISIBLE);
            } else {
                tipsView.setVisibility(View.INVISIBLE);
            }
        }
        return view;
    }

    public static interface DELEGATE {
        void editOnClick();
    }

    @OnClick(R.id.iv_title_left)
    public void onHeadBackClick(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.base_slide_right_out);
    }

}
