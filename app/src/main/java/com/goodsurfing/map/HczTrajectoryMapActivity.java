package com.goodsurfing.map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.component.constants.What;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.goodsurfing.adpter.HczTrajectoryAdapter;
import com.goodsurfing.adpter.TrajectoryAdapter;
import com.goodsurfing.app.R;
import com.goodsurfing.beans.DynamicBean;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.main.HczLoginActivity;
import com.goodsurfing.server.net.HczGetDynamicNet;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.view.TrajectoryRecyclerView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 运动轨迹
 */
@SuppressLint("NewApi")
public class HczTrajectoryMapActivity extends Activity {
    protected static final int REFRESH = 1;
    protected static final int UP_REFRESH = 2;
    @ViewInject(R.id.tv_title)
    private TextView title;
    @ViewInject(R.id.tv_title_right)
    private TextView right;
    @ViewInject(R.id.iv_trajectory_right)
    private ImageView trajectoryRight;
    @ViewInject(R.id.iv_trajectory_left)
    private ImageView trajectoryLeft;
    @ViewInject(R.id.tv_trajectory_title)
    private TextView trajectoryTimeView;
    @ViewInject(R.id.title_layout)
    private View title_layout;
    @ViewInject(R.id.iv_title_right)
    private ImageView title_right_iv;
    @ViewInject(R.id.hcz_trajectory_recycler)
    private TrajectoryRecyclerView trajectoryRecyclerView;
    @ViewInject(R.id.activity_trajectory_calendar)
    private MaterialCalendarView trajectoryCalendarView;

    @ViewInject(R.id.activity_trajectory_calendar_ll)
    private View calendarLayout;
    private List<DynamicBean> traceList = new ArrayList<>();
    private HczTrajectoryAdapter adapter;
    private Calendar calendar;
    private HeadView head;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hcz_trajectory_map);
        ViewUtils.inject(this);
        init();
    }

    /**
     * 初始化方法
     */
    @SuppressLint("LongLogTag")
    private void init() {
        title.setText("运动轨迹");
        right.setVisibility(View.GONE);
        title_right_iv.setVisibility(View.VISIBLE);
        title_right_iv.setImageResource(R.drawable.dynamic_title_right_icon);
        trajectoryCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.MONDAY)   //设置每周开始的第一天
                .setMinimumDate(CalendarDay.from(2018, 1, 1))  //设置可以显示的最早时间
                .setMaximumDate(new Date())//设置可以显示的最晚时间
                .setCalendarDisplayMode(CalendarMode.MONTHS)//设置显示模式，可以显示月的模式，也可以显示周的模式
                .commit();
        View headView = LayoutInflater.from(this).inflate(R.layout.item_hcz_trajectory_head, null);
        adapter = new HczTrajectoryAdapter(this, traceList);
        adapter.setHeaderView(headView);
        trajectoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        trajectoryRecyclerView.setAdapter(adapter);
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        head = new HeadView(headView);
        trajectoryRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                // 只有LinearLayoutManager才有查找第一个和最后一个可见view位置的方法
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                    //获取最后一个可见view的位置
                    int lastItemPosition = linearManager.findLastVisibleItemPosition();
                    //获取第一个可见view的位置
                    int firstItemPosition = linearManager.findFirstVisibleItemPosition();
                    switch (newState) {
                        case RecyclerView.SCROLL_STATE_IDLE:
                            if (firstItemPosition == 0) {
//                                head.mapView.onResume();
                            }
                            break;
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        trajectoryRight.setVisibility(View.GONE);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setTitleTime(calendar);
            }
        }, 20);
    }

    /**
     * 获取服务器返回数据 userid=24&token=token145208399124&requesttype=14
     */
    private void getBundleUserTrajectory(long startTime, long endTime) {
        if (Constants.userId.equals("")) {
            HczLoginActivity.gotoLogin(this);
            return;
        }
        if (Constants.child == null) {
            ActivityUtil.showPopWindow4Tips(this, title_layout, false, true, "请绑定孩子手机后操作...", 2000);
            return;
        }
        ActivityUtil.showPopWindow4Tips(HczTrajectoryMapActivity.this, title_layout, false, true, "正在获取运动轨迹...", -1);
        HczGetDynamicNet getLocationNet = new HczGetDynamicNet(this, handler);
        getLocationNet.putParams(startTime, endTime, 1);
        getLocationNet.sendRequest();
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case What.HTTP_REQUEST_CURD_SUCCESS:
                    traceList.clear();
                    traceList.addAll((List<DynamicBean>) msg.obj);
                    head.bindView();
                    adapter.notifyDataSetChanged();
                    ActivityUtil.showPopWindow4Tips(HczTrajectoryMapActivity.this, title_layout, true, true, "获取成功", 2000);
                    break;
                case What.HTTP_REQUEST_CURD_FAILURE:
                    ActivityUtil.showPopWindow4Tips(HczTrajectoryMapActivity.this, title_layout, false, true, msg.obj.toString() + "", 2000);
                    break;
            }
        }

    };

    @OnClick(R.id.iv_title_left)
    public void onHeadBackClick(View view) {
        onBackPressed();
    }

    @OnClick(R.id.iv_title_right)
    public void onHeadRightClick(View view) {
        if (calendarLayout.getVisibility() == View.VISIBLE) {
            calendarLayout.setVisibility(View.GONE);
        } else {
            calendarLayout.setVisibility(View.VISIBLE);
            DateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
            String dateStr = trajectoryTimeView.getText().toString().trim();
            if (dateStr != null && !dateStr.equals("")) {
                try {
                    trajectoryCalendarView.clearSelection();
                    trajectoryCalendarView.setDateSelected(format.parse(dateStr), true);
                } catch (ParseException e) {
                    trajectoryCalendarView.setDateSelected(new Date(), true);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.base_slide_right_out);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_remain);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_remain);
    }

    @OnClick(R.id.activity_trajectory_gettime)
    public void onGetTimeForCalendar(View view) {
        calendarLayout.setVisibility(View.GONE);
        calendar = trajectoryCalendarView.getSelectedDate().getCalendar();
        if (calendar != null) {
            setTitleTime(calendar);
        }
    }

    private void setTitleTime(Calendar calendar) {
        String dateStr = calendar.get(Calendar.YEAR) + "年" + (calendar.get(Calendar.MONTH) + 1) + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日";
        trajectoryTimeView.setText(dateStr);
        getBundleUserTrajectory(ActivityUtil.getLong4Data(calendar)[0], ActivityUtil.getLong4Data(calendar)[1]);
    }

    @OnClick(R.id.iv_trajectory_right)
    public void onRightDateClick(View view) {
        calendar.add(Calendar.DAY_OF_MONTH, +1);
        setTitleTime(calendar);
        if (calendar.compareTo(trajectoryCalendarView.getMaximumDate().getCalendar()) <= 0) {
            trajectoryLeft.setVisibility(View.VISIBLE);
        } else {
            trajectoryRight.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.iv_trajectory_left)
    public void onLeftDateClick(View view) {
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        setTitleTime(calendar);
        if (calendar.compareTo(trajectoryCalendarView.getMinimumDate().getCalendar()) >= 0) {
            trajectoryRight.setVisibility(View.VISIBLE);
        } else {
            trajectoryLeft.setVisibility(View.GONE);
        }
    }

    class HeadView {
        private float currentZoomLevel;
        protected float maxZoomLevel;
        protected float minZoomLevel;
        private BaiduMap bdMap;
        private MapStatus.Builder builder;
        private TextureMapView mapView;
        private Button zoomIn, zoomOut, location, reflash;
        private ImageView childrenHead;
        private TextView childrenName, childrenLocation, childrenReflashTime;

        HeadView(View itemView) {
            childrenName = itemView.findViewById(R.id.children_num_name_tv);
            childrenLocation = itemView.findViewById(R.id.children_num_juli_tv);
            childrenReflashTime = itemView.findViewById(R.id.children_num_time_tv);
            childrenHead = itemView.findViewById(R.id.children_head_iv);
            mapView = itemView.findViewById(R.id.bmapview);
            zoomIn = itemView.findViewById(R.id.zoomin);
            zoomOut = itemView.findViewById(R.id.zoomout);
            reflash = itemView.findViewById(R.id.reflash_btn);
            location = itemView.findViewById(R.id.locate_btn);
        }


        void bindView() {
            bdMap = mapView.getMap();
            bdMap.clear();
            mapView.onResume();
            mapView.showZoomControls(false);
            if (traceList.size() != 0) {
                showMarkerOverlay();
            }
            zoomIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentZoomLevel = bdMap.getMapStatus().zoom;
                    if (currentZoomLevel <= maxZoomLevel) {
                        bdMap.setMapStatus(MapStatusUpdateFactory.zoomIn());
                        zoomOut.setEnabled(true);
                    } else {
                        zoomIn.setEnabled(false);
                    }
                }
            });
            zoomOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentZoomLevel = bdMap.getMapStatus().zoom;
                    if (currentZoomLevel >= minZoomLevel) {
                        bdMap.setMapStatus(MapStatusUpdateFactory.zoomOut());
                        zoomIn.setEnabled(true);
                    } else {
                        zoomOut.setEnabled(false);
                    }
                }
            });
            location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (builder != null && builder.build() != null)
                        bdMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                    else
                        ActivityUtil.showPopWindow4Tips(HczTrajectoryMapActivity.this, title, false, true, "孩子还没有运动轨迹", 2000);

                }
            });
            if (traceList.size() > 0) {
                childrenHead.setImageResource(Constants.showIds[Constants.child.getImg()]);
                childrenName.setText(Constants.child.getName());
                childrenLocation.setText(traceList.get(0).getAddress());
                childrenReflashTime.setText(traceList.get(0).getDate());
            }
        }

        private void showMarkerOverlay() {
            //始点图层图标
            BitmapDescriptor startBD = BitmapDescriptorFactory
                    .fromResource(R.drawable.trajectory_loaction_start);
            //终点图层图标
            BitmapDescriptor finishBD = BitmapDescriptorFactory
                    .fromResource(R.drawable.fragment_main_up1_tips_location);
            bdMap.setOnMapStatusChangeListener(mapStatusChangeListener);
            builder = new MapStatus.Builder();

            //地图设置缩放状态
            List<LatLng> latLngs = new ArrayList<>();
            double lanSum = 0;
            double lonSum = 0;
            //我这里设置地图的缩放中心点为所有点的几何中心点

            for (int i = 0; i < traceList.size(); i++) {
                if (traceList.get(i).getLatLng().latitude == 0.0 && traceList.get(i).getLatLng().longitude == 0.0)
                    continue;

                if (i == 0) {
                    MarkerOptions oStart = new MarkerOptions();//地图标记类型的图层参数配置类
                    oStart.position(traceList.get(i).getLatLng());//图层位置点，第一个点为起点
                    oStart.icon(startBD);//设置图层图片
                    oStart.zIndex(1);//设置图层Index
                    //添加起点图层
                    Marker mMarkerA = (Marker) (bdMap.addOverlay(oStart));
                } else {
                    //添加终点图层
                    MarkerOptions oFinish = new MarkerOptions().position(traceList.get(i).getLatLng()).icon(finishBD).zIndex(2);
                    Marker mMarkerB = (Marker) (bdMap.addOverlay(oFinish));
                }
                latLngs.add(traceList.get(i).getLatLng());
                lanSum += traceList.get(i).getLatLng().latitude;
                lonSum += traceList.get(i).getLatLng().longitude;
            }
            LatLng target = new LatLng(lanSum / latLngs.size(), lonSum / latLngs.size());
            builder.target(target).zoom(15f);
            bdMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            /**
             * 配置线段图层参数类： PolylineOptions
             * ooPolyline.width(13)：线宽
             * ooPolyline.color(0xAAFF0000)：线条颜色红色
             * ooPolyline.points(latLngs)：List<LatLng> latLngs位置点，将相邻点与点连成线就成了轨迹了
             */
            if (latLngs.size() > 1) {
                OverlayOptions ooPolyline = new PolylineOptions().width(10).color(0x90ff4a00).points(latLngs);

                //在地图上画出线条图层，mPolyline：线条图层
                Polyline mPolyline = (Polyline) bdMap.addOverlay(ooPolyline);
                mPolyline.setZIndex(3);
            }

        }

        BaiduMap.OnMapStatusChangeListener mapStatusChangeListener = new BaiduMap.OnMapStatusChangeListener() {
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
    }
}
