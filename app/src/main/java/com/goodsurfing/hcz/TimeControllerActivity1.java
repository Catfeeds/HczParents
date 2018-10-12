package com.goodsurfing.hcz;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.component.constants.What;
import com.goodsurfing.app.R;
import com.goodsurfing.base.BaseActivity;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.server.net.HczGetTimeControlNet;
import com.goodsurfing.server.net.HczTimeControlNet;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.CommonUtil;
import com.goodsurfing.utils.SharUtil;
import com.goodsurfing.view.customview.TimerController;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 时段控制
 *
 * @author 谢志杰
 * @version 1.0.0
 * @create 2017-12-18 下午2:41:22
 * @company 湖南一石科技有限公司 Copyright: 版权所有（c）2016
 */
public class TimeControllerActivity1 extends BaseActivity {

    @ViewInject(R.id.activity_timer_controller_l1)
    private LinearLayout linearLayout;

    @ViewInject(R.id.activity_timer_control_sv)
    private ScrollView scrollView;

    @ViewInject(R.id.tv_title)
    private TextView title;

    @ViewInject(R.id.tv_title_right)
    private TextView comfirm;

    public static String timerStr = "";
    @ViewInject(R.id.title_layout)
    private View title_layout;
    @ViewInject(R.id.table_icon)
    private TextView psdTextView;
    private String psdStr;
    private final static String TAG = "TimerControllerActivity";

    private TimerController timer;
    private LinearLayout clearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_control1);
        ViewUtils.inject(this);
        init();
    }

    @SuppressLint("NewApi")
    private void init() {
        title.setText("时段控制");
        comfirm.setText("确定");
        comfirm.setVisibility(View.VISIBLE);
        getSavedDatas();
        TableLayout table = (TableLayout) findViewById(R.id.tablelayout);
        clearLayout = (LinearLayout) findViewById(R.id.table_clear_ll);
        timer = new TimerController(this, table, clearLayout, linearLayout, comfirm);
        timer.initTabBackGround();
        timer.refreshTable(timerStr);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Constants.isbindChild && Constants.child != null) {
            getTimerData();
            psdTextView.setText("应急解锁密码(" + SharUtil.getTimePsd(TimeControllerActivity1.this) + ")");
        }else {
            psdTextView.setText("请绑定孩子手机后操作");
        }
    }

    private void getTimerData() {
        HczGetTimeControlNet getTimeControlNet = new HczGetTimeControlNet(this, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case What.HTTP_REQUEST_CURD_SUCCESS:
                        try {
                            JSONObject data = JSON.parseObject(msg.obj.toString());
                            timerStr = data.getString("UseTime");
                            timer.refreshTable(timerStr);
                            SharUtil.saveTimePwds(TimeControllerActivity1.this, psdStr);
                            Editor editor = getSharedPreferences("TIMER_TABLE", Activity.MODE_PRIVATE).edit(); // 获取编辑器
                            editor.putString(Constants.TIMESTR_KEY + Constants.userId, timerStr);
                            editor.commit();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case What.HTTP_REQUEST_CURD_FAILURE:
                        break;
                }
            }
        });
        getTimeControlNet.putParams();
        getTimeControlNet.sendRequest();
    }

    private void sendTimerData(final String psd) {
        if (!Constants.isNetWork) {
            ActivityUtil.showPopWindow4Tips(this, title_layout, false, "当前网络不可用，请稍后再试...");
            return;
        }
        if (Constants.child == null) {
            ActivityUtil.showPopWindow4Tips(this, title_layout, false, "请绑定孩子手机后操作");
            return;
        }
        ActivityUtil.showPopWindow4Tips(this, title_layout, false, false, "正在修改中....", -1);
        final String timers = TimerController.getSelectResult();
        HczTimeControlNet timeControlNet = new HczTimeControlNet(this, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case What.HTTP_REQUEST_CURD_SUCCESS:
                        ActivityUtil.showPopWindow4Tips(TimeControllerActivity1.this, title_layout, true, "设置成功");
                        Editor editor = getSharedPreferences("TIMER_TABLE", Activity.MODE_PRIVATE).edit(); // 获取编辑器
                        editor.putString(Constants.TIMESTR_KEY + Constants.userId, timers);
                        editor.commit();
                        break;
                    case What.HTTP_REQUEST_CURD_FAILURE:
                        ActivityUtil.showPopWindow4Tips(TimeControllerActivity1.this, title_layout, false, msg.obj.toString() + "");
                        break;
                }
            }
        });
        timeControlNet.putParams(timers);
        timeControlNet.sendRequest();
    }

    @OnClick(R.id.tv_title_right)
    public void onComfirmClick(View v) {
        if (CommonUtil.isFastDoubleClick())
            return;
        if ("".equals(Constants.userId)) {
            HczLoginActivity.gotoLogin(this);
            return;
        }
        sendTimerData(SharUtil.getTimePsd(this));
    }

    private void showSetPswd() {
        final Dialog dialog = new Dialog(this, R.style.AlertDialogCustom);
        View view = View.inflate(this, R.layout.layout_setlockpswd_dialog, null);
        TextView leftView = (TextView) view.findViewById(R.id.layout_kefu_left);
        final TextView rightView = (TextView) view.findViewById(R.id.layout_kefu_right);
        final EditText editText = view.findViewById(R.id.layout_setlock_et);
        leftView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        rightView.setTextColor(getResources().getColor(R.color.gray));
        rightView.setEnabled(false);
        rightView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String psd = editText.getText().toString();
                sendTimerData(psd);
                dialog.dismiss();
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 6) {
                    rightView.setTextColor(getResources().getColor(R.color.set_lock_pwd_color));
                    rightView.setEnabled(true);
                } else {
                    rightView.setTextColor(getResources().getColor(R.color.gray));
                    rightView.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        dialog.setContentView(view);
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); // 获取对话框当前的参数值
        p.width = (int) (d.getWidth() * 0.65); // 宽度设置为屏幕的0.95
        dialog.getWindow().setAttributes(p);
        dialog.show();
    }

    private void getSavedDatas() {
        SharedPreferences preferences = getSharedPreferences("TIMER_TABLE", Activity.MODE_PRIVATE);
        timerStr = preferences.getString(Constants.TIMESTR_KEY + Constants.userId, "");
    }

    @OnClick(R.id.iv_title_left)
    public void onHeadBackClick(View view) {
        onBackPressed();
    }
}
