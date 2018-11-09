package com.goodsurfing.hcz;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.android.component.constants.What;
import com.goodsurfing.adpter.HczModeAdapter;
import com.goodsurfing.app.R;
import com.goodsurfing.base.BaseActivity;
import com.goodsurfing.beans.ModeBean;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.server.net.HczGetModeListNet;
import com.goodsurfing.server.net.HczSwitchModeNet;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.SharUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class ModeChangeActivity1 extends BaseActivity implements OnClickListener {

    private final static String TAG = "ModeChangeActivity";
    protected static final int REFRESH = 100;
    @ViewInject(R.id.mode_title_layout)
    private View mode_title_layout;
    @ViewInject(R.id.tv_title)
    private TextView title;
    @ViewInject(R.id.tv_title_right)
    private TextView rightView;
    private int newMode = 1;
    @ViewInject(R.id.activity_mode_rcv)
    private RecyclerView modeRecyclerView;
    private HczModeAdapter modeAdapter;
    private List<ModeBean> modeBeans = new ArrayList<>();
private View rootView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_mode_change1,null);
        setContentView(rootView);
        ViewUtils.inject(this);
        init();
    }

    @SuppressLint("NewApi")
    private void init() {
        title.setText("模式切换");
        rightView.setVisibility(View.GONE);
        newMode  =Constants.mode;
        modeAdapter = new HczModeAdapter(this,modeBeans );
        modeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        modeRecyclerView.setAdapter(modeAdapter);
        modeAdapter.setListener(this);
    }

    /**
     * 改变 模式请求
     *
     * @param item
     */
    private void changeModeByNet(final int item) {
        if ("".equals(Constants.userId)) {
            HczLoginActivity.gotoLogin(this);
            return;
        }
        if (!Constants.isNetWork) {
            ActivityUtil.showPopWindow4Tips(ModeChangeActivity1.this, rootView, false, "当前网络不可用，请稍后再试...");
            return;
        }
        if (Constants.child == null) {
            ActivityUtil.showPopWindow4Tips(this, rootView, false, "请绑定孩子手机后操作");
            return;
        }
        modeAdapter.notifyDataSetChanged();
        ActivityUtil.showPopWindow4Tips(ModeChangeActivity1.this, rootView, false, false, "模式切换中...", -1);
        HczSwitchModeNet switchModeNet = new HczSwitchModeNet(this, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case What.HTTP_REQUEST_CURD_SUCCESS:
                        if(msg.obj!=null&&!TextUtils.isEmpty(msg.obj.toString())){
                            ActivityUtil.showPopWindow4Tips(ModeChangeActivity1.this, rootView, true, msg.obj.toString());
                        }else
                        ActivityUtil.showPopWindow4Tips(ModeChangeActivity1.this, rootView, true, "模式切换成功");
                        Constants.mode = modeBeans.get(item).getModeId();
                        modeAdapter.notifyDataSetChanged();
                        SharUtil.saveMode(ModeChangeActivity1.this, Constants.mode);
                        break;
                    case What.HTTP_REQUEST_CURD_FAILURE:
                        modeBeans.get(item).setStatus("2");
                        modeAdapter.notifyDataSetChanged();
                        ActivityUtil.showPopWindow4Tips(ModeChangeActivity1.this, rootView, false, msg.obj.toString() + "");
                        break;
                }
            }
        });
        switchModeNet.putParams(Constants.child.getClientDeviceId() + "", modeBeans.get(item).getModeId() + "");
        switchModeNet.sendRequest();

    }

    @OnClick(R.id.iv_title_left)
    public void onHeadBackClick(View view) {
        onBackPressed();
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
                getMode4Child();
            }
        },20);
    }
    private void getMode4Child() {
        if ("".equals(Constants.userId)) {
            HczLoginActivity.gotoLogin(this);
            return;
        }
        if (!Constants.isNetWork) {
            ActivityUtil.showPopWindow4Tips(ModeChangeActivity1.this, rootView, false, "当前网络不可用，请稍后再试...");
            return;
        }
        if (Constants.child == null) {
            ActivityUtil.showPopWindow4Tips(this, rootView, false, "请绑定孩子手机后操作");
            return;
        }
        HczGetModeListNet getModeListNet = new HczGetModeListNet(this, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case What.HTTP_REQUEST_CURD_SUCCESS:
                        modeBeans.clear();
                        modeBeans.addAll((Collection<? extends ModeBean>) msg.obj);
                        modeAdapter.notifyDataSetChanged();
                        break;
                    case What.HTTP_REQUEST_CURD_FAILURE:
                        ActivityUtil.showPopWindow4Tips(ModeChangeActivity1.this, rootView, false, msg.obj.toString() + "");
                        break;
                }
            }
        });
        getModeListNet.putParams();
        getModeListNet.sendRequest();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activity_mode_tab_1:
                newMode= (Integer) v.getTag();
                ModeBean bean = modeBeans.get(newMode);
                if(bean.getModeId()==Constants.mode){
                    return;
                }
                changeModeByNet(newMode);
                break;
        }
    }

}
