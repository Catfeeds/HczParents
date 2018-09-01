package com.goodsurfing.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.component.constants.What;
import com.goodsurfing.addchild.AddChildActivity;
import com.goodsurfing.adpter.DynamicAdapter;
import com.goodsurfing.app.R;
import com.goodsurfing.base.BaseFragment;
import com.goodsurfing.beans.DynamicBean;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.map.HczMapActivity;
import com.goodsurfing.map.HczTrajectoryMapActivity;
import com.goodsurfing.server.net.HczGetChildsNet;
import com.goodsurfing.server.net.HczGetDynamicNet;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.CommonUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainUpActivity1 extends BaseFragment implements OnClickListener {
    public static final int SHOW_LAYOUT = 101;
    public static final int DISMISS_LAYOUT = 102;
    private RelativeLayout tipsLayout;
    private RelativeLayout addChildLl;
    private View rootView;
    private RecyclerView rvTrace;
    private List<DynamicBean> traceList = new ArrayList<>(10);
    private DynamicAdapter adapter;
    private TextView helpTextView;
    private TextView tipsLocationTv;
    private Calendar calendar;
    private DynamicBean fastNewDynamicBean;

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
        rvTrace = (RecyclerView) view.findViewById(R.id.fragment_main_up1_recyc);
        tipsLayout = view.findViewById(R.id.fragment_main_up1_tips);
        tipsLocationTv = view.findViewById(R.id.fragment_main_up1_tips_text);
        addChildLl = view.findViewById(R.id.add_child_ll);
        addChildLl.setOnClickListener(this);
        tipsLayout.setOnClickListener(this);

    }

    private void init() {
        helpTextView.setOnClickListener(this);
        adapter = new DynamicAdapter(getActivity(), traceList);
        adapter.setListener(this);
        rvTrace.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTrace.setAdapter(adapter);
        calendar = Calendar.getInstance();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Constants.userId.equals("")) {
            addChildLl.setVisibility(View.GONE);
            tipsLayout.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
        getBindChild();
    }

    private void setTitleTime(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long start = calendar.getTimeInMillis() / 1000;
        calendar = Calendar.getInstance();
        long end = calendar.getTimeInMillis() / 1000;
        getBundleUserTrajectory(start, end);

    }

    @Override
    public void onClick(View v) {
        if (CommonUtil.isFastDoubleClick())
            return;
        switch (v.getId()) {
            case R.id.main_help_tv:
                Intent help = new Intent(getActivity(), GuideView.class);
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
                ((HczMainActivity)getActivity()).mainPageView.setCurrentItem(1);
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
    private void getBundleUserTrajectory(long startTime, long endTime) {
        if (Constants.child == null) {
            addChildLl.setVisibility(View.VISIBLE);
            return;
        }
        HczGetDynamicNet getLocationNet = new HczGetDynamicNet(getActivity(), handler);
        getLocationNet.putParams(startTime, endTime, 5);
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
                    setNewLocation();
                    adapter.notifyDataSetChanged();
                    break;
                case What.HTTP_REQUEST_CURD_FAILURE:
                    break;
            }
        }
    };

    private void getBindChild() {
        if (Constants.userId.equals("")) {
            traceList.clear();
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
                            setTitleTime(calendar);
                        } else {
                            traceList.clear();
                            adapter.notifyDataSetChanged();
                            addChildLl.setVisibility(View.VISIBLE);
                            tipsLayout.setVisibility(View.GONE);
                        }
                        break;
                    case What.HTTP_REQUEST_CURD_FAILURE:
                        break;
                }
            }
        });
        getBindChildNet.putParams();
        getBindChildNet.sendRequest();
    }

    private void setNewLocation() {
        if (traceList.size() > 0) {
            for (DynamicBean dynamicBean : traceList) {
                if (dynamicBean.getType() == DynamicBean.TYPE_LOCATION&&!dynamicBean.getMsg().equals("")) {
                    fastNewDynamicBean = dynamicBean;
                    tipsLayout.setVisibility(View.VISIBLE);
                    tipsLocationTv.setText(fastNewDynamicBean.getMsg());
                    return;
                }
            }
            tipsLayout.setVisibility(View.GONE);
        }
    }

}
