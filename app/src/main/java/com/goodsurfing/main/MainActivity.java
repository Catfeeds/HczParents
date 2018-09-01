package com.goodsurfing.main;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.goodsurfing.adpter.MainViewPagerAdapter;
import com.goodsurfing.app.R;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.server.GetServerListServer;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResponder;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResult;
import com.goodsurfing.service.UpdateManager;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.CommonUtil;
import com.goodsurfing.utils.SharUtil;
import com.goodsurfing.view.MainPageView;
import com.umeng.analytics.MobclickAgent;

public class MainActivity extends FragmentActivity {
    private static final int READ_CONTACTS_REQUEST = 1;
    // 定义FragmentTabHost对象
//    public static FragmentTabHost mTabHost;
    // 定义一个布局
    private LayoutInflater layoutInflater;
    // 定义数组来存放Fragment界面
    private Class fragmentArray[] = {MainUpActivity.class, MainTimeActivity.class, MainMyActivity.class};
    // 定义数组来存放按钮图片
    private int mImageViewArray[] = {R.drawable.tab_home_btn, R.drawable.tab_message_btn, R.drawable.tab_more_btn};
    // Tab选项卡的文字
    private String mTextviewArray[] = {"好上网", "网络解锁", "我的"};    // 定义数组来存放Fragment界面

    private ImageView iconView;
    public MainPageView mainPageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_tab_main);
        init();
        initViews();
        getServerList();
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
        if (Constants.serverList.size() == 0) {
            String url = null;
            url = Constants.SERVER_URL_GLOBAL + "?" + "requesttype=1002";

            new GetServerListServer(new DataServiceResponder() {

                @Override
                public void onResult(DataServiceResult result) {
                }

                @Override
                public void onFailure() {
                }
            }, url, this).execute();
        }
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
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
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
        adapter.addFragment(new MainUpActivity());
        adapter.addFragment(new MainTimeActivity());
        adapter.addFragment(new MainMyActivity());
        mainPageView.setAdapter(adapter);
        mainPageView.setNoScroll(true);
        initTabHost();


//
//        // 实例化布局对象
//        layoutInflater = LayoutInflater.from(this);
//        // 实例化TabHost对象，得到TabHost
//        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
//        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
//        if(mTabHost.getTabWidget()!=null){
//            mTabHost.clearAllTabs();
//        }
//        // 得到fragment的个数
//        for (int i = 0; i < 3; i++) {
//            TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
//            mTabHost.addTab(tabSpec, fragmentArray[i], null);
//            mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.color.white);
//        }
//        mTabHost.getTabWidget().setDividerDrawable(R.color.white);
//        mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
//
//            @Override
//            public void onTabChanged(String arg0) {
//                if (Constants.userId.equals("")) {
//                    iconView.setVisibility(View.VISIBLE);
//                } else {
//                    iconView.setVisibility(View.GONE);
//                }
//                ActivityUtil.sendEvent4UM(MainActivity.this, "tabSwitch", arg0, 4);
//            }
//        });
//        mTabHost.setCurrentTab(0);
    }

//    private View getTabItemView(int index) {
//        View view = layoutInflater.inflate(R.layout.tab_item_layout, null);
//        ImageView imageView = (ImageView) view.findViewById(R.id.tab_imageview);
//        imageView.setImageResource(mImageViewArray[index]);
//        TextView textView = (TextView) view.findViewById(R.id.tab_textview);
//        textView.setText(mTextviewArray[index]);
//        ImageView iView = (ImageView) view.findViewById(R.id.start_red_icon);
//        if (index != 2 || !Constants.userId.equals("")) {
//            iView.setVisibility(View.GONE);
//        }
//        if (index == 2)
//            iconView = iView;
//        return view;
//    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mainPageView.setCurrentItem(0);
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
                    if (NetworkInfo.State.CONNECTED == state) {
                        Constants.isNetWork = true;
                        ActivityUtil.dismissPopWindow();
                        return;
                    }
                }
            }
            // 没有执行return,则说明当前无网络连接
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
            // back监听 返回
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
