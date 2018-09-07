package com.goodsurfing.main;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.component.constants.What;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Legend;
import com.github.mikephil.charting.utils.Utils;
import com.goodsurfing.addchild.AddChildActivity;
import com.goodsurfing.adpter.MainInfoAdapter;
import com.goodsurfing.app.R;
import com.goodsurfing.base.BaseFragment;
import com.goodsurfing.beans.AppUseBean;
import com.goodsurfing.beans.ChartBean;
import com.goodsurfing.beans.DynamicBean;
import com.goodsurfing.beans.PieEntry;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.map.HczMapActivity;
import com.goodsurfing.map.HczTrajectoryMapActivity;
import com.goodsurfing.server.net.HczGetChildsNet;
import com.goodsurfing.server.net.HczGetStatisticsNet;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.CommonUtil;
import com.goodsurfing.view.IndicateDotView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.github.mikephil.charting.charts.Chart.PAINT_CENTER_TEXT;

public class HczInfoFragment extends BaseFragment implements OnClickListener {
    private RelativeLayout tipsLayout;
    private RelativeLayout addChildLl;
    private View rootView;
    private RecyclerView rvTrace;
    private MainInfoAdapter adapter;
    private TextView helpTextView;
    private TextView tipsLocationTv;
    private DynamicBean fastNewDynamicBean;
    private View headView;
    private View modeView;
    private View chartView;
    private HeadView head;
    private ModeView mode;
    private ChartView chart;
    private PieEntry pieEntry;
    private List<AppUseBean> appUseBeans = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null != rootView) {
            ViewGroup group = (ViewGroup) rootView.getParent();
            if (group != null) {
                group.removeView(rootView);
            }
            return rootView;
        }
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_main_up1, null);
        initViews(rootView);
        init();
        return rootView;
    }

    private void initViews(View view) {
        helpTextView = (TextView) view.findViewById(R.id.main_help_tv);
        headView = LayoutInflater.from(getActivity()).inflate(R.layout.item_info_head, null);
        modeView = LayoutInflater.from(getActivity()).inflate(R.layout.item_info_mode, null);
        chartView = LayoutInflater.from(getActivity()).inflate(R.layout.chart_layout, null);
        head = new HeadView(headView);
        mode = new ModeView(modeView);
        chart = new ChartView(chartView);
        rvTrace = (RecyclerView) view.findViewById(R.id.fragment_main_up1_recyc);
        tipsLayout = view.findViewById(R.id.fragment_main_up1_tips);
        tipsLocationTv = view.findViewById(R.id.fragment_main_up1_tips_text);
        addChildLl = view.findViewById(R.id.add_child_ll);
        addChildLl.setOnClickListener(this);
        tipsLayout.setOnClickListener(this);

    }

    private void init() {
        helpTextView.setOnClickListener(this);
        adapter = new MainInfoAdapter(getActivity(), appUseBeans);
        rvTrace.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.setHeadView(headView);
        rvTrace.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Constants.userId.equals("")) {
            addChildLl.setVisibility(View.GONE);
            tipsLayout.setVisibility(View.GONE);
        } else {
            adapter.notifyDataSetChanged();
            getBindChild();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (this.isVisible()) {
            if (isVisibleToUser) {
                if (Constants.userId.equals("")) {
                    addChildLl.setVisibility(View.GONE);
                    tipsLayout.setVisibility(View.GONE);
                } else {
                    adapter.notifyDataSetChanged();
                    getBindChild();
                }
            }
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    private void setTitleTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String date = formatter.format(new Date());
        getBundleUserTrajectory("2018-09-06");

    }

    @Override
    public void onClick(View v) {
        if (CommonUtil.isFastDoubleClick())
            return;
        switch (v.getId()) {
            case R.id.main_help_tv:
                Intent help = new Intent(getActivity(), WebActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("TYPE_GUIDE", "1");
                help.putExtras(bundle);
                startActivity(help);
                break;
            case R.id.main_mode_ll:
            case R.id.tv_mode_button:
                ActivityUtil.sendEvent4UM(getActivity(), "functionSwitch", "functionSw", 15);
                Intent mode = new Intent(getActivity(), ModeChangeActivity1.class);
                startActivity(mode);
                break;
            case R.id.main_app_ll:
                // 应用管控
                ActivityUtil.sendEvent4UM(getActivity(), "functionSwitch", "more", 16);
                Intent app = new Intent(getActivity(), ApplicationControlsActivity.class);
                startActivity(app);
                break;
            case R.id.main_lock_ll:
                //一键锁屏
                ((HczMainActivity) getActivity()).mainPageView.setCurrentItem(1);
                break;
            case R.id.main_time_ll:
                ActivityUtil.sendEvent4UM(getActivity(), "functionSwitch", "timecontrol", 17);
                Intent time = new Intent(getActivity(), TimeControllerActivity1.class);
                startActivity(time);
                break;
            case R.id.main_dynamic_ll:
                //使用动态
                ActivityUtil.sendEvent4UM(getActivity(), "functionSwitch", "childrenmobile", 18);
                Intent youn = new Intent(getActivity(), DynamicActivity.class);
                startActivity(youn);
                break;
            case R.id.main_location_ll:
                //运动轨迹
                ActivityUtil.sendEvent4UM(getActivity(), "functionSwitch", "HczTrajectoryMapActivity", 18);
                Intent traject = new Intent(getActivity(), HczTrajectoryMapActivity.class);
                startActivity(traject);
                break;
            case R.id.fragment_main_up1_tips:
                //实时定位
                ActivityUtil.sendEvent4UM(getActivity(), "functionSwitch", "HczMapActivity", 19);
                Intent loaction = new Intent(getActivity(), HczMapActivity.class);
                loaction.putExtra("dynamic", fastNewDynamicBean);
                startActivity(loaction);
                break;
            case R.id.add_child_ll:
                startActivity(new Intent(getActivity(), AddChildActivity.class));
                break;
        }
    }


    /**
     * 获取服务器返回数据 userid=24&token=token145208399124&requesttype=14
     */
    private void getBundleUserTrajectory(String date) {
        if (Constants.child == null) {
            addChildLl.setVisibility(View.VISIBLE);
            return;
        }
        HczGetStatisticsNet getStatisticsNet = new HczGetStatisticsNet(getActivity(), handler);
        getStatisticsNet.putParams(date);
        getStatisticsNet.sendRequest();
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case What.HTTP_REQUEST_CURD_SUCCESS:
                    pieEntry = (PieEntry) msg.obj;
                    appUseBeans.clear();
                    if (pieEntry.getCatelist().size() != 0) {
                        appUseBeans.addAll(pieEntry.getApplist());
                        adapter.setChartView(chartView);
                        chart.bindView(pieEntry.getCatelist());
                        setNewLocation();
                    } else {
                        adapter.setChartView(null);
                    }
                    adapter.notifyDataSetChanged();
                    break;
                case What.HTTP_REQUEST_CURD_FAILURE:
                    appUseBeans.clear();
                    adapter.setChartView(null);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    private void getBindChild() {
        if (Constants.userId.equals("")) {
            appUseBeans.clear();
            adapter.setChartView(null);
            adapter.setChartView(null);
            adapter.notifyDataSetChanged();
            tipsLayout.setVisibility(View.GONE);
            return;
        }
        HczGetChildsNet getBindChildNet = new HczGetChildsNet(getActivity(), new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case What.HTTP_REQUEST_CURD_SUCCESS:
                        if (Constants.child != null) {
                            addChildLl.setVisibility(View.GONE);
                            mode.bindView();
                            adapter.setModeView(modeView);
                            adapter.notifyDataSetChanged();
                            setTitleTime();
                        } else {
                            adapter.setModeView(null);
                            appUseBeans.clear();
                            adapter.notifyDataSetChanged();
                            addChildLl.setVisibility(View.VISIBLE);
                            tipsLayout.setVisibility(View.GONE);
                        }
                        break;
                    case What.HTTP_REQUEST_CURD_FAILURE:
                        adapter.setModeView(null);
                        appUseBeans.clear();
                        adapter.notifyDataSetChanged();
                        addChildLl.setVisibility(View.VISIBLE);
                        tipsLayout.setVisibility(View.GONE);
                        break;
                }
            }
        });
        getBindChildNet.putParams();
        getBindChildNet.sendRequest();
    }

    private void setNewLocation() {
        if (appUseBeans.size() > 0) {
//                    tipsLayout.setVisibility(View.VISIBLE);
//                    tipsLocationTv.setText(fastNewDynamicBean.getMsg());
            tipsLayout.setVisibility(View.GONE);
        }
    }

    class HeadView {
        private LinearLayout modeLayout;
        private LinearLayout timeLayout;
        private LinearLayout locationLayout;
        private LinearLayout lockLayout;
        private LinearLayout dynamicLayout;
        private LinearLayout appLayout;

        HeadView(View itemView) {
            modeLayout = (LinearLayout) itemView.findViewById(R.id.main_mode_ll);
            timeLayout = (LinearLayout) itemView.findViewById(R.id.main_time_ll);
            locationLayout = (LinearLayout) itemView.findViewById(R.id.main_location_ll);
            lockLayout = (LinearLayout) itemView.findViewById(R.id.main_lock_ll);
            dynamicLayout = (LinearLayout) itemView.findViewById(R.id.main_dynamic_ll);
            appLayout = (LinearLayout) itemView.findViewById(R.id.main_app_ll);
            modeLayout.setOnClickListener(HczInfoFragment.this);
            timeLayout.setOnClickListener(HczInfoFragment.this);
            locationLayout.setOnClickListener(HczInfoFragment.this);
            lockLayout.setOnClickListener(HczInfoFragment.this);
            dynamicLayout.setOnClickListener(HczInfoFragment.this);
            appLayout.setOnClickListener(HczInfoFragment.this);
        }
    }

    class ModeView {
        private TextView mode_text, mode_tips, mode_button;

        ModeView(View itemView) {
            mode_text = itemView.findViewById(R.id.tv_mode_text);
            mode_tips = itemView.findViewById(R.id.tv_mode_tips);
            mode_button = itemView.findViewById(R.id.tv_mode_button);
        }

        void bindView() {
            String tips = "";
            String mode = "";
            switch (Constants.child.getMode()) {
                case Constants.MODE_LEARNING:
                    mode = Constants.MODE_LEARNING_TEXT;
                    tips = "孩子只能使用家长允许的APP";
                    break;
                case Constants.MODE_FREE:
                    mode = Constants.MODE_FREE_TEXT;
                    tips = "孩子可以自由使用所有APP";
                    break;
            }
            mode_tips.setText(tips);
            mode_text.setText(mode);
            mode_button.setOnClickListener(HczInfoFragment.this);
        }
    }

    class ChartView {
        private PieChart mPieChart;
        List<View> legendViews = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<Integer>();
        ChartView(View view) {
            mPieChart = (PieChart) view.findViewById(R.id.chart_view);
            mPieChart.setUsePercentValues(true);
            //设置中间文件
            mPieChart.setDrawHoleEnabled(true);
            mPieChart.setHoleColor(Color.WHITE);
            mPieChart.setHoleRadius(80f);
            mPieChart.setTransparentCircleRadius(83f);
            mPieChart.setDrawCenterText(true);
            mPieChart.setDrawXValues(false);
            mPieChart.setDrawLegend(false);
            mPieChart.setDescription("好上网");
            mPieChart.setRotationAngle(0);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setColor(Color.parseColor("#5a667d"));
            mPieChart.setPaint(paint,PAINT_CENTER_TEXT);
            mPieChart.setCenterTextSize(18f);
            //设置数据
            mPieChart.animateXY(1000, 1000);
            legendViews.add(view.findViewById(R.id.chart_ll1));
            legendViews.add(view.findViewById(R.id.chart_ll2));
            legendViews.add(view.findViewById(R.id.chart_ll3));
            legendViews.add(view.findViewById(R.id.chart_ll4));
            legendViews.add(view.findViewById(R.id.chart_ll5));
            legendViews.add(view.findViewById(R.id.chart_ll6));
            colors.add(Color.parseColor("#fff8a66a"));
            colors.add(Color.parseColor("#ffa7bbef"));
            colors.add(Color.parseColor("#ff92dbce"));
            colors.add(Color.parseColor("#ffed9595"));
            colors.add(Color.parseColor("#ff85dea8"));
            colors.add(Color.parseColor("#fff8c85b"));
        }

        //设置数据
        public void bindView(List<ChartBean> list) {
            //模拟数据
            int totalTime = 0;
            ArrayList<Entry> yValues = new ArrayList<Entry>();
            ArrayList<String> xValues = new ArrayList<String>();  //xVals用来表示每个饼块上的内容
            for (int i = 0; i < list.size(); i++) {
                xValues.add(list.get(i).getCateName());
                yValues.add(new Entry(list.get(i).getRatio(), i));
                int k = i + 1;
                totalTime += Integer.valueOf(list.get(i).getUtime());
                setLegendView(i, list.get(i).getUtime(), colors.get(i), xValues.get(i));
            }
            mPieChart.setCenterText("总计" + totalTime / 3600 + "小时");
            PieDataSet pieDataSet = new PieDataSet(yValues, null);
            pieDataSet.setSliceSpace(2f);
            pieDataSet.setColors(colors);
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            float px = 3 * (metrics.densityDpi / 180f);
            pieDataSet.setSelectionShift(px); // 选中态多出的长度
            PieData pieData = new PieData(xValues, pieDataSet);
            mPieChart.setData(pieData);
            mPieChart.setDrawYValues(false);
            mPieChart.invalidate();

        }

        private void setLegendView(int i, String ut, int cl, String type) {
            View v = legendViews.get(i);
            v.setVisibility(View.VISIBLE);
            IndicateDotView iv = v.findViewById(R.id.chart_item_iv);
            TextView nv = v.findViewById(R.id.chart_item_name);
            TextView tv = v.findViewById(R.id.chart_item_time);
            iv.setBackGroud(cl);
            nv.setText(type + "类");
            tv.setText((Integer.valueOf(ut) / 60) + "分钟");
        }

    }

}
