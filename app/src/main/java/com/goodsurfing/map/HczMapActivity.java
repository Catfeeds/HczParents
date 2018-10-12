package com.goodsurfing.map;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroupOverlay;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.component.constants.What;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.goodsurfing.app.R;
import com.goodsurfing.beans.DynamicBean;
import com.goodsurfing.beans.Friend;
import com.goodsurfing.beans.LocationBean;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.database.dao.FriendDao;
import com.goodsurfing.main.LoginActivity;
import com.goodsurfing.server.GetBundleUserServer;
import com.goodsurfing.server.net.HczGetLocationNet;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResponder;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResult;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.view.RoundImageView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 实时定位
 */
@SuppressLint("NewApi")
public class HczMapActivity extends Activity implements OnClickListener {
    private static final int requestcode = 101;
    protected static final int REFRESH = 1;
    protected static final int UP_REFRESH = 2;
    private float currentZoomLevel;
    // 百度地图控件
    private MapView mMapView = null;
    // 百度地图对象
    private BaiduMap bdMap;
    // 定位按钮
    private Button locateBtn;
    private Button reflashBtn;
    // 定位图标描述
    private BitmapDescriptor currentMarker = null;
    private LocationClient locClient;
    // 记录是否第一次定位
    private boolean isFirstLoc = true;
    // 初始化全局 bitmap 信息，不用时及时 recycle
    private ViewGroupOverlay mOverlay;

    // 构建marker图标
    BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_wz);
    // GroundOptions
    private Button zoomInBtn;
    private Button zoomOutBtn;
    protected float maxZoomLevel;
    protected float minZoomLevel;
    @ViewInject(R.id.tv_title)
    private TextView title;
    @ViewInject(R.id.tv_title_right)
    private TextView right;
    @ViewInject(R.id.children_head_iv)
    private ImageView childrenNumHeadIv;
    @ViewInject(R.id.children_num_rl)
    private LinearLayout childrenNumRLayout;
    @ViewInject(R.id.children_num_name_tv)
    private TextView childrenNameTv;
    @ViewInject(R.id.children_num_juli_tv)
    private TextView childrenJuliTv;
    @ViewInject(R.id.children_num_address_tv)
    private TextView childrenAddressTv;
    @ViewInject(R.id.children_num_time_tv)
    private TextView childrenTimeTv;
    @ViewInject(R.id.title_layout)
    private View title_layout;
    @ViewInject(R.id.children_battery_tv)
    private TextView batteryTView;
    @ViewInject(R.id.children_battery_iv)
    private ImageView batteryIView;
    @ViewInject(R.id.children_nettype_tv)
    public TextView netTypeTView;
    private DynamicBean dynamicBean = new DynamicBean();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hcz_map);
        ViewUtils.inject(this);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        if (!"".equals(Constants.userId) && Constants.child != null) {
            setDynamicDate();
        } else {
            childrenNumRLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        locClient.stop();
        bdMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        // 回收bitmip资源
        bitmap.recycle();
        super.onDestroy();
    }

    /**
     * 初始化方法
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void init() {
        mMapView = (MapView) findViewById(R.id.bmapview);
        try {
            dynamicBean = (DynamicBean) getIntent().getSerializableExtra("dynamic");
        } catch (Exception e) {

        }
        bdMap = mMapView.getMap();
        title.setText("当前位置");
        right.setVisibility(View.GONE);
        locateBtn = (Button) findViewById(R.id.locate_btn);
        reflashBtn = (Button) findViewById(R.id.reflash_btn);
        reflashBtn.setOnClickListener(this);
        locateBtn.setOnClickListener(this);
        childrenNumRLayout.setOnClickListener(this);
        mMapView.showZoomControls(false);
        mOverlay = mMapView.getOverlay();
        bdMap.setMyLocationEnabled(true);
        zoomInBtn = (Button) findViewById(R.id.zoomin);
        zoomOutBtn = (Button) findViewById(R.id.zoomout);
        zoomInBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                currentZoomLevel = bdMap.getMapStatus().zoom;
                if (currentZoomLevel <= maxZoomLevel) {
                    bdMap.setMapStatus(MapStatusUpdateFactory.zoomIn());
                    zoomOutBtn.setEnabled(true);
                } else {
                    ActivityUtil.showPopWindow4Tips(HczMapActivity.this, title_layout, false, "已经放至最大");
                    zoomInBtn.setEnabled(false);
                }
            }
        });
        zoomOutBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                currentZoomLevel = bdMap.getMapStatus().zoom;
                if (currentZoomLevel >= minZoomLevel) {
                    bdMap.setMapStatus(MapStatusUpdateFactory.zoomOut());
                    zoomInBtn.setEnabled(true);
                } else {
                    zoomOutBtn.setEnabled(false);
                    ActivityUtil.showPopWindow4Tips(HczMapActivity.this, title_layout, false, "已经缩至最小");
                }
            }
        });
        // 对marker覆盖物添加点击事件
        bdMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // 获得marker中的数据
                return false;
            }
        });
        bdMap.setOnMapStatusChangeListener(mapStatusChangeListener);
        startLocation();
    }


    OnMapStatusChangeListener mapStatusChangeListener = new OnMapStatusChangeListener() {
        @Override
        public void onMapStatusChange(MapStatus arg0) {
            maxZoomLevel = bdMap.getMaxZoomLevel();
            minZoomLevel = bdMap.getMinZoomLevel();

            currentZoomLevel = arg0.zoom;

            if (currentZoomLevel >= maxZoomLevel) {
                currentZoomLevel = maxZoomLevel;
            } else if (currentZoomLevel <= minZoomLevel) {
                currentZoomLevel = minZoomLevel;
            }
            currentZoomLevel = arg0.zoom;
            if (currentZoomLevel == maxZoomLevel) {
                // 设置地图缩放等级为上限
                MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(currentZoomLevel);
                bdMap.animateMapStatus(u);
                zoomInBtn.setEnabled(false);
            } else if (currentZoomLevel == minZoomLevel) {
                // 设置地图缩放等级为下限
                MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(currentZoomLevel);
                bdMap.animateMapStatus(u);
                zoomOutBtn.setEnabled(false);
            } else {
                if (!zoomInBtn.isEnabled() || !zoomOutBtn.isEnabled()) {
                    zoomInBtn.setEnabled(true);
                    zoomOutBtn.setEnabled(true);
                }
            }
        }

        @Override
        public void onMapStatusChangeFinish(MapStatus arg0) {
        }

        @Override
        public void onMapStatusChangeStart(MapStatus arg0) {
        }

        @Override
        public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.locate_btn:
            case R.id.children_num_rl:
                setDynamicDate();
                break;
            case R.id.reflash_btn:
                reflashLocation();
                break;
        }
    }

    /**
     * 刷新 实时位置
     */
    private void reflashLocation() {
        if (Constants.child == null) {
            ActivityUtil.showPopWindow4Tips(this, title_layout, false, "请绑定孩子手机后操作");
            return;
        }
        ActivityUtil.showPopWindow4Tips(HczMapActivity.this, title_layout, false, false, "正在刷新孩子位置...", -1);
        HczGetLocationNet getLocationNet = new HczGetLocationNet(this, handler);
        getLocationNet.putParams();
        getLocationNet.sendRequest();
    }

    /**
     * 添加标注覆盖物
     */
    private void addMarkerOverlay() {
        bdMap.clear();
        // 构建markerOption，用于在地图上添加marker
        OverlayOptions options = new MarkerOptions()//
                .position(dynamicBean.getLatLng())// 设置marker的位置
                .icon(bitmap)// 设置marker的图标
                .zIndex(9)// 設置marker的所在層級
                .draggable(true);// 设置手势拖拽
        // 在地图上添加marker，并显示
        Marker marker = (Marker) bdMap.addOverlay(options);
    }

    /**
     * 配置孩子信息
     */
    private void setDynamicDate() {
        if (dynamicBean != null && mMapView != null) {
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(dynamicBean.getLatLng()).zoom(15.0f);
            MapStatusUpdate msu = MapStatusUpdateFactory.newMapStatus(builder.build());
            bdMap.animateMapStatus(msu);
            addMarkerOverlay();
            batteryTView.setText(Constants.child.getBattery() + "%");
            childrenNameTv.setText(Constants.child.getName());
            childrenAddressTv.setText(dynamicBean.getAddress());
            childrenTimeTv.setText(dynamicBean.getDate());
            childrenNumHeadIv.setImageResource(Constants.showIds[Constants.child.getImg()]);
            if (Constants.child.getBattery() > 80) {
                batteryIView.setImageResource(R.drawable.ic_battery_hight);
            } else if (Constants.child.getBattery() < 20) {
                batteryIView.setImageResource(R.drawable.ic_battery_low);
            } else {
                batteryIView.setImageResource(R.drawable.ic_battery_mid);
            }
            if (Constants.child.getNetType() != null && !Constants.child.getNetType().equals("")) {
                netTypeTView.setText("( " + Constants.child.getNetType() + " )");
            } else {
                netTypeTView.setText("( 在线 )");
            }
        } else {
            ActivityUtil.showPopWindow4Tips(HczMapActivity.this, title_layout, false, "暂无孩子位置信息");
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case What.HTTP_REQUEST_CURD_SUCCESS:
                    ActivityUtil.showPopWindow4Tips(HczMapActivity.this, title_layout, true, "刷新成功");
                    if (msg.obj != null) {
                        LocationBean locationBean = (LocationBean) msg.obj;
                        dynamicBean.setAddress(locationBean.getAddress());
                        dynamicBean.setDate(locationBean.getCreatedAt());
                        dynamicBean.setLat(locationBean.getLat() + "");
                        dynamicBean.setLng(locationBean.getLng() + "");
                        setDynamicDate();
                        startLocation();
                    }
                    break;
                case What.HTTP_REQUEST_CURD_FAILURE:
                    ActivityUtil.showPopWindow4Tips(HczMapActivity.this, title_layout, false, msg.obj.toString());
                    break;
            }
        }

    };

    private String distanceByLatlng(double lat_a, double lng_a, double lat_b, double lng_b) {
        double pk = 180 / 3.1415926;
        double a1 = lat_a / pk;
        double a2 = lng_a / pk;
        double b1 = lat_b / pk;
        double b2 = lng_b / pk;
        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);
        int distance = (int) (6366000 * tt);
        int k = distance / 1000;
        if (k > 0) {
            double d = 6366000 * tt / 1000.0;
            return "[相距" + String.format("%.2f", d) + "千米]";
        }
        return "[相距" + distance + "米]";
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

    private void startLocation() {
        locClient = new LocationClient(this);
        locClient.registerLocationListener(locListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll");// 设置坐标类型
        option.setAddrType("all");
        option.setScanSpan(0);//
        locClient.setLocOption(option);
        locClient.start();
        if (!locClient.isStarted()) {
            locClient.start();
            locClient.requestLocation();
        } else {
            locClient.requestLocation();
        }
    }

    /**
     * 定位监听器
     */
    BDLocationListener locListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null || bdMap == null) {
                return;
            }
            if (isFirstLoc) {
                isFirstLoc = false;
                childrenJuliTv.setText(distanceByLatlng(location.getLatitude(), location.getLongitude(), dynamicBean.getLatLng().latitude, dynamicBean.getLatLng().longitude));
            }
        }

    };
}
