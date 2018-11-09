package com.goodsurfing.hcz;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.component.constants.What;
import com.goodsurfing.addchild.ChildListActivity;
import com.goodsurfing.adpter.MainViewPagerAdapter;
import com.goodsurfing.app.R;
import com.goodsurfing.beans.ExpireBean;
import com.goodsurfing.beans.IPList;
import com.goodsurfing.beans.User;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.main.MainActivity;
import com.goodsurfing.main.MainMyActivity;
import com.goodsurfing.main.WebActivity;
import com.goodsurfing.server.net.HczAppFuncNet;
import com.goodsurfing.server.net.HczGetCreatVipNet;
import com.goodsurfing.server.net.HczGetExpiredateNet;
import com.goodsurfing.server.net.HczGetServerNet;
import com.goodsurfing.service.UpdateManager;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.CommonUtil;
import com.goodsurfing.utils.SharUtil;
import com.goodsurfing.view.MainPageView;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

public class HczMainActivity extends FragmentActivity {
    private static final int READ_CONTACTS_REQUEST = 1;
    // 定义FragmentTabHost对象
//    public static FragmentTabHost mTabHost;
    // 定义一个布局
//    private LayoutInflater layoutInflater;
//    // 定义数组来存放按钮图片
    private int mImageViewArray[] = {R.drawable.tab_home_btn, R.drawable.tab_message_btn, R.drawable.tab_more_btn};
    //    // Tab选项卡的文字
    private String mTextviewArray[] = {"好上网", "一键锁屏", "我的"};    // 定义数组来存放Fragment界面
    private Class fragmentArray[] = {HczInfoFragment.class, MainTimeActivity1.class, MainMyActivity.class};
    // 定义数组来存放按钮图片
//    private int mImageViewArray[] = {R.drawable.tab_home_btn1, R.drawable.tab_message_btn1, R.drawable.tab_more_btn1};

    // Tab选项卡的文字
//    private String mTextviewArray[] = {"首页", "一键锁屏", "我的"};

    public static ImageView iconView;
    public MainPageView mainPageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarFullTransparent();
        setContentView(R.layout.activity_tab_main);
        init();
        initViews();
        getServerList();
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
        }
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    /**
     * 获取服务商列表
     */
    private void getServerList() {
        if (Constants.serviceList == null) {
            HczGetServerNet getServerNet = new HczGetServerNet(this, new Handler() {
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
        if (Constants.funcBeans.size() == 0) {
            if (!Constants.userId.equals("")) {
                HczAppFuncNet appFuncNet = new HczAppFuncNet(this, new Handler());
                appFuncNet.putParams(SharUtil.getServiceId());
                appFuncNet.sendRequest();
            }
        }
        if (!Constants.userId.equals("") && SharUtil.getService(this).equals(Constants.APP_USER_TYPE)) {
            HczGetCreatVipNet getExpiredateNet = new HczGetCreatVipNet(this, new Handler() {
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what) {
                        case What.HTTP_REQUEST_CURD_SUCCESS:
                            ExpireBean bean = (ExpireBean) msg.obj;
                            if (bean.isNotice()) {
                                SharUtil.saveExpireShow();
                                showNoticeDialog(bean);
                            }
                            try {
                                Constants.dealTime = bean.getExpiredate()+"";
                                User user = CommonUtil.getUser(HczMainActivity.this);
                                user.setEditTime(Constants.dealTime);
                                CommonUtil.setUser(HczMainActivity.this, user);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            break;
                        case What.HTTP_REQUEST_CURD_FAILURE:
                            break;
                    }
                }
            });
            getExpiredateNet.putParams();
            getExpiredateNet.sendRequest();
        }

    }


    private void showNoticeDialog(final ExpireBean about) {
        final Dialog dialog = new Dialog(this, R.style.AlertDialogCustom);
        View view = View.inflate(this, R.layout.layout_vip_dialog, null);
        TextView content = (TextView) view.findViewById(R.id.layout_vip_content_tv);
        TextView titleView = (TextView) view.findViewById(R.id.layout_vip_title_tv);
        TextView rightView = (TextView) view.findViewById(R.id.layout_vip_time_tv);
        Button btn = view.findViewById(R.id.layout_vip_btn);
        titleView.setText("注册成功");
        content.setText(about.getMsg());
        rightView.setText("有效期至: "+about.getExpiredate());
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.show();
    }


    private void init() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, filter);
        SharedPreferences preferences = getSharedPreferences(Constants.SP_NAME, MODE_PRIVATE);
        preferences.edit().putInt(Constants.VERSIONCODE, CommonUtil.getAppVersionCode(this)).commit();
        if (!"".equals(Constants.userId)) {
            Constants.mode = SharUtil.getMode(this);
        }
        Constants.devWidth = this.getWindowManager().getDefaultDisplay().getWidth();
        Constants.devHeight = this.getWindowManager().getDefaultDisplay().getHeight();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    READ_CONTACTS_REQUEST);
        } else {
            upVersion();
        }
    }

    /**
     * 获取版本更新信息
     */
    private void upVersion() {
        UpdateManager manager = new UpdateManager(this);
        manager.detectionVersion(this);
    }

    private void initTabHost() {
        final LinearLayout mainll = findViewById(R.id.main_tab_ll);
        final LinearLayout timell = findViewById(R.id.time_tab_ll);
        final LinearLayout myll = findViewById(R.id.my_tab_ll);

        ImageView mainIcon = mainll.findViewById(R.id.tab_imageview);
        TextView mainTv = mainll.findViewById(R.id.tab_textview);
        ImageView timeIcon = timell.findViewById(R.id.tab_imageview);
        TextView timeTv = timell.findViewById(R.id.tab_textview);
        ImageView myIcon = myll.findViewById(R.id.tab_imageview);
        TextView myTv = myll.findViewById(R.id.tab_textview);
        iconView = myll.findViewById(R.id.start_red_icon);

        mainll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainll.setSelected(true);
                timell.setSelected(false);
                myll.setSelected(false);
                mainPageView.setCurrentItem(0);
            }
        });
        timell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainll.setSelected(false);
                timell.setSelected(true);
                myll.setSelected(false);
                mainPageView.setCurrentItem(1);
            }
        });
        myll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainll.setSelected(false);
                timell.setSelected(false);
                myll.setSelected(true);
                mainPageView.setCurrentItem(2);
            }
        });
        mainIcon.setImageResource(mImageViewArray[0]);
        timeIcon.setImageResource(mImageViewArray[1]);
        myIcon.setImageResource(mImageViewArray[2]);
        mainTv.setText(mTextviewArray[0]);
        timeTv.setText(mTextviewArray[1]);
        myTv.setText(mTextviewArray[2]);
        mainPageView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mainll.setSelected(false);
                timell.setSelected(false);
                myll.setSelected(false);
                switch (position) {
                    case 0:
                        mainll.setSelected(true);
                        break;
                    case 1:
                        timell.setSelected(true);
                        break;
                    case 2:
                        myll.setSelected(true);
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mainPageView.setCurrentItem(0);
        mainll.setSelected(true);
        timell.setSelected(false);
        myll.setSelected(false);
    }


    private void initViews() {
        mainPageView = findViewById(R.id.hcz_main_pv);
        MainViewPagerAdapter adapter = new MainViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HczInfoFragment());
        adapter.addFragment(new MainTimeActivity1());
        adapter.addFragment(new MainMyActivity());
        mainPageView.setAdapter(adapter);
        mainPageView.setNoScroll(true);
        initTabHost();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mainPageView.setCurrentItem(0);
        if (Constants.userId.equals("")) {
            iconView.setVisibility(View.VISIBLE);
        } else {
            iconView.setVisibility(View.GONE);
        }
        getServerList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
                for (int i = 0; i < networkInfos.length; i++) {
                    State state = networkInfos[i].getState();
                    if (State.CONNECTED == state) {
                        Constants.isNetWork = true;
                        ActivityUtil.dismissPopWindow();
                        return;
                    }
                }
            }
            Constants.isNetWork = false;
        }

    };

    @Override
    protected void onResume() {
        super.onResume();
        CommonUtil.HandLockPassword(this);
        MobclickAgent.onResume(this);
        if (Constants.userId.equals("")) {
            iconView.setVisibility(View.VISIBLE);
        } else {
            iconView.setVisibility(View.GONE);
        }
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;

        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //确保是我们的请求
        if (requestCode == READ_CONTACTS_REQUEST) {
            if (grantResults.length == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else if (grantResults.length == 3 && grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "获取设备码权限失败,您将无法正常使用APP", Toast.LENGTH_SHORT).show();
            } else if (grantResults.length == 3 && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
            } else {
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
