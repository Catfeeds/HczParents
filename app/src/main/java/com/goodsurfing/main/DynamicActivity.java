package com.goodsurfing.main;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.component.constants.What;
import com.goodsurfing.adpter.DynamicActivityAdapter;
import com.goodsurfing.app.R;
import com.goodsurfing.base.BaseActivity;
import com.goodsurfing.beans.DynamicBean;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.server.net.HczGetDynamicNet;
import com.goodsurfing.utils.ActivityUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 使用动态
 */
public class DynamicActivity extends BaseActivity {

    private final static String TAG = "AboutActivity";

    protected static final int REFRESH = 100;

    @ViewInject(R.id.tv_title)
    private TextView title;

    @ViewInject(R.id.iv_title_right)
    private ImageView titleRight;

    @ViewInject(R.id.tv_title_right)
    private TextView right;
    @ViewInject(R.id.activity_dynamic_calendar)
    private MaterialCalendarView dynamicCalendarView;

    @ViewInject(R.id.activity_dynamic_recyc)
    private RecyclerView dynamicRecyclerView;

    @ViewInject(R.id.activity_dynamic_calendar_ll)
    private View calendarLayout;

    @ViewInject(R.id.activity_dynamic_nodata)
    private View nodataView;

    private List<DynamicBean> traceList = new ArrayList<>(10);

    private Calendar calendar;
    private DynamicActivityAdapter adapter;

    public DynamicActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic);
        ViewUtils.inject(this);
        init();
    }

    private void init() {
        title.setText("使用动态");
        right.setVisibility(View.GONE);
        titleRight.setVisibility(View.VISIBLE);
        titleRight.setImageResource(R.drawable.dynamic_title_right_icon);
        calendar = Calendar.getInstance();
        adapter = new DynamicActivityAdapter(this, traceList);
        dynamicRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dynamicRecyclerView.setAdapter(adapter);
        dynamicCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.MONDAY)   //设置每周开始的第一天
                .setMinimumDate(CalendarDay.from(2018, 1, 1))  //设置可以显示的最早时间
                .setMaximumDate(new Date())//设置可以显示的最晚时间
                .setCalendarDisplayMode(CalendarMode.MONTHS)//设置显示模式，可以显示月的模式，也可以显示周的模式
                .commit();
    }

    private void getBundleUserTrajectory(long startTime, long endTime) {
        if (Constants.userId.equals("")) {
            HczLoginActivity.gotoLogin(this);
            return;
        }
        if (Constants.child == null) {
            ActivityUtil.showPopWindow4Tips(this, title, false, false, "请绑定孩子手机后操作...", 2000);
            return;
        }
        ActivityUtil.showPopWindow4Tips(DynamicActivity.this, title, false, false, "正在获取使用动态...", -1);
        HczGetDynamicNet getLocationNet = new HczGetDynamicNet(this, handler);
        getLocationNet.putParams(startTime, endTime, 4);
        getLocationNet.sendRequest();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                long endTime = calendar.getTimeInMillis() / 1000;
                calendar.add(Calendar.DAY_OF_MONTH, -2);
                long starTime = calendar.getTimeInMillis() / 1000;
                getBundleUserTrajectory(starTime, endTime);
            }
        },20);
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case What.HTTP_REQUEST_CURD_SUCCESS:
                    traceList.clear();
                    traceList.addAll((List<DynamicBean>) msg.obj);
                    adapter.notifyDataSetChanged();
                    ActivityUtil.showPopWindow4Tips(DynamicActivity.this, title, true, "获取成功");
                    break;
                case What.HTTP_REQUEST_CURD_FAILURE:
                    ActivityUtil.showPopWindow4Tips(DynamicActivity.this, title, false, msg.obj.toString() + "");
                    break;
            }
            setNodataVIew();
        }

    };

    private void setNodataVIew(){
        if(traceList.size()==0){
            nodataView.setVisibility(View.VISIBLE);
        }else {
            nodataView.setVisibility(View.GONE);
        }
    }

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
            dynamicCalendarView.clearSelection();
            dynamicCalendarView.setDateSelected(new Date(), true);
        }
    }

    @OnClick(R.id.activity_dynamic_gettime)
    public void onGetTimeForCalendar(View view) {
        calendarLayout.setVisibility(View.GONE);
        calendarLayout.setVisibility(View.GONE);
        calendar = dynamicCalendarView.getSelectedDate().getCalendar();
        if (calendar != null) {
            setTitleTime(calendar);
        }
    }

    private void setTitleTime(Calendar calendar) {
        getBundleUserTrajectory(ActivityUtil.getLong4Data(calendar)[0], ActivityUtil.getLong4Data(calendar)[1]);
    }
}
