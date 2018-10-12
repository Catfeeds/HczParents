package com.goodsurfing.hcz;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.component.constants.What;
import com.goodsurfing.app.R;
import com.goodsurfing.base.BaseFragment;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.hcz.HczLoginActivity;
import com.goodsurfing.hcz.HczMainActivity;
import com.goodsurfing.server.net.HczLockScreenNet;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.CommonUtil;
import com.goodsurfing.utils.SharUtil;
import com.goodsurfing.view.customview.CircularSeekBar;

public class MainTimeActivity1 extends BaseFragment implements CircularSeekBar.OnSeekChangeListener {

    private TextView hTextView;
    private TextView mTextView;
    private TextView sTextView;
    private TextView hTextViewHint;
    private TextView mTextViewHint;
    private TextView sTextViewHint;
    private TextView mTipsModeTv;
    private TextView mTipsTv;

    private ImageView currentStateImageView;
    private ImageView titleLeftIv;
    private TextView titleTextview;
    private ImageView titleRightIv;
    private TextView titleRightTv;
    private LinearLayout timeLayout;
    private View time_tips_layout;

    protected static Thread mThread;
    private CircularSeekBar seekBar;
    private boolean isStartQuick = false;
    private View rootView;
    // handler类接收数据
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (Constants.totalSeconds != 0 && Constants.isClockLocked)
                     {
                        if (Constants.totalSeconds != 0) {
                            Constants.totalSeconds--;
                            if (Constants.isClockLocked) {
                                hTextViewHint.setVisibility(View.VISIBLE);
                                mTextViewHint.setVisibility(View.VISIBLE);
                                sTextViewHint.setVisibility(View.VISIBLE);
                                Constants.circleCounts = Constants.totalSeconds / 60;
                                seekBar.setCircleTimeCounts(Constants.circleCounts);
                                setTimeText(Constants.totalSeconds);
                            }
                        } else {
                            Constants.isClockLocked = false;
                            setTimeText(Constants.totalSeconds);
                            seekBar.setCircleCounts(0);
                        }
                        if (Constants.isClockLocked && Constants.totalSeconds != 0) {
                            int seconds = Constants.totalSeconds % 3600 % 60;
                            seekBar.setAngle((int) (seconds * 6));
                            seekBar.setLineAngle((seconds) * Math.PI / 30);
                        }
                    }
                    break;
                case 2:
                    if ("".equals(Constants.userId)) {
                        HczLoginActivity.gotoLogin(getActivity());
                        return;
                    }
                    if (isStartQuick)
                        return;
                    isStartQuick = true;
                    if (Constants.totalSeconds == 0) {
                        startLocked(9);
                    } else if (!Constants.isClockLocked) {
                        startLocked(8);
                    }
                    break;
                case 3:
                    Constants.isClockLocked = false;
                    Constants.circleCounts = Constants.totalSeconds / 60 / 360;
                    seekBar.setCircleTimeCounts(Constants.circleCounts);
                    break;
                case 4:
                    Constants.isClockLocked = false;
                    break;
            }
        }

        ;
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == rootView) {
            rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_main_time1, null);
            initViews(rootView);
        }
        ViewGroup group = (ViewGroup) rootView.getParent();
        if (group != null) {
            group.removeView(rootView);
        }
        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
            if (isVisibleToUser) {
                isStartQuick = false;
                if (!"".equals(Constants.userId)&&Constants.child!=null) {
                    setData2View();
                } else {
                    currentStateImageView.setImageResource(R.drawable.ic_current_lock);
                    timeLayout.setVisibility(View.INVISIBLE);
                    mTipsModeTv.setVisibility(View.INVISIBLE);
                    mTipsTv.setVisibility(View.INVISIBLE);
                    seekBar.setLock(false);
                    clearTime();
                }
                ActivityUtil.sendEvent4UM(getActivity(), "functionSwitch", "unlock", 19);
            }else {
                isStartQuick = false;
            }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initViews(View view) {
        currentStateImageView = (ImageView) view.findViewById(R.id.activity_current_lock_mode_iv);
        hTextView = (TextView) view.findViewById(R.id.activity_current_timer_h_tv);
        mTextView = (TextView) view.findViewById(R.id.activity_current_timer_m_tv);
        sTextView = (TextView) view.findViewById(R.id.activity_current_timer_s_tv);
        hTextViewHint = (TextView) view.findViewById(R.id.activity_current_timer_h_tv_hint);
        mTextViewHint = (TextView) view.findViewById(R.id.activity_current_timer_m_tv_hint);
        sTextViewHint = (TextView) view.findViewById(R.id.activity_current_timer_s_tv_hint);
        seekBar = (CircularSeekBar) view.findViewById(R.id.circularseekbar);
        timeLayout = (LinearLayout) view.findViewById(R.id.activity_current_timer_ll);
        titleLeftIv = (ImageView) view.findViewById(R.id.iv_title_left);
        mTipsModeTv = (TextView) view.findViewById(R.id.time_mode_text);
        mTipsTv = (TextView) view.findViewById(R.id.time_mode_tips_text);
        titleTextview = (TextView) view.findViewById(R.id.tv_title);
        titleRightIv = (ImageView) view.findViewById(R.id.iv_title_right);
        time_tips_layout = (View) view.findViewById(R.id.time_tips_layout);
        titleRightTv = (TextView) view.findViewById(R.id.tv_title_right);
        titleTextview.setText("一键锁屏");
        titleRightIv.setVisibility(View.GONE);
        titleRightTv.setVisibility(View.GONE);
        seekBar.setSeekBarChangeListener(this);
        seekBar.setHandler(handler);
        titleLeftIv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ((HczMainActivity)getActivity()).mainPageView.setCurrentItem(0);
            }
        });
        currentStateImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if ("".equals(Constants.userId)) {
                    HczLoginActivity.gotoLogin(getActivity());
                    return;
                }
                if (isStartQuick)
                    return;
                if (Constants.isClockLocked) {
                    Constants.isClockLocked = false;
                    startLocked(9);
                } else {
                    Constants.isClockLocked = true;
                    Constants.totalSeconds = Constants.settingTimes * 360;
                    startLocked(8);
                }
            }
        });
    }

    private void setTipsText4Mode() {
        String tipsString = "";
        String mode = "";
        mTipsModeTv.setVisibility(View.VISIBLE);
        mTipsTv.setVisibility(View.VISIBLE);
        timeLayout.setVisibility(View.VISIBLE);
        if(Constants.totalSeconds>0){
            mode = "当前孩子状态: 已锁屏";
        }else {
            mode = "当前孩子状态: 未锁屏";
        }
//
//        switch (Constants.mode) {
//            case Constants.MODE_LEARNING:
//
//                tipsString = "孩子只能使用家长允许的APP";
//                break;
//            case Constants.MODE_FREE:
//                mode ="当前模式: "+ Constants.MODE_FREE_TEXT;
//                tipsString = "孩子可以自由使用所有APP";
//                break;
//        }
        if (!Constants.child.getLockPwd().equals("")) {
            tipsString = "应急解锁密码:" + Constants.child.getLockPwd();
        } else {
            tipsString = "滑动即可设置锁屏时间";
        }
        mTipsModeTv.setText(mode);
        mTipsTv.setText(tipsString);
    }

    private void clearTime() {
        if (!Constants.isClockLocked) {
            Constants.totalSeconds = 0;
            Constants.settingTimes = 0;
            Constants.circleCounts = 0;
            seekBar.setCircleTimeCounts(0);
            seekBar.setCircleCounts(0);
            seekBar.setCurrentprogress(0);
            seekBar.setAngle(0);
            seekBar.setLineAngle(0);
            setTimeText(0);
        }
    }

    /*
     * type 请求的类型 开启 解锁或关闭解锁
     */
    private void startLocked(final int type) {
        if (Constants.child == null) {
            ActivityUtil.showPopWindow4Tips(getActivity(), rootView, false, false, "请绑定孩子手机后操作...", 2000);
            return;
        }
        if (!Constants.isNetWork) {
            ActivityUtil.showPopWindow4Tips(getActivity(), time_tips_layout, false, "当前网络不可用，请稍后再试...");
            if (Constants.totalSeconds > 0) {
                Constants.isClockLocked = true;
            }
            return;
        }
        if (type == 8) {
            setCodeView();
        } else {
            startLock(9);
        }

    }

    private void startLock(final int type) {

        /**
         * 8.用户临时授权 get请求参数：userid=11111& token
         * =fasfasfa&committype=8&timecount=100 <单位为s>
         *
         * 9.用户临时授权结束 get请求参数：userid=11111& token =fasfasfa&committype=9
         */
        long time = 0;
        if (type == 8) {
            if (Constants.totalSeconds == 0) {
                Constants.totalSeconds = 8 * 60 * 60;
            }
            time = (System.currentTimeMillis() / 1000 + Constants.totalSeconds);
            ActivityUtil.showPopWindow4Tips(getActivity(), time_tips_layout, false, false, "开启锁屏...", -1);
        } else {
            Constants.totalSeconds = 0;
            time = 0;
            ActivityUtil.showPopWindow4Tips(getActivity(), time_tips_layout, false, false, "解除锁屏...", -1);
        }
        isStartQuick = true;
        String psd = SharUtil.getLockScreenPsd(getActivity());
        HczLockScreenNet lockScreenNet = new HczLockScreenNet(getActivity(), new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case What.HTTP_REQUEST_CURD_SUCCESS:
                        isStartQuick = false;
                        if (type == 8) {
                            ActivityUtil.showPopWindow4Tips(getActivity(), time_tips_layout, true, "锁屏成功");
                            currentStateImageView.setImageResource(R.drawable.ic_current_unlock);
                            Constants.isClockLocked = true;
                            timeLayout.setVisibility(View.VISIBLE);
                            Constants.circleCounts = Constants.totalSeconds / 60;
                            mTipsTv.setText("应急解锁密码:" + SharUtil.getLockScreenPsd(getActivity()));
                            createTimerInit();
                        } else if (type == 9) {
                            ActivityUtil.showPopWindow4Tips(getActivity(), time_tips_layout, true, "解锁成功");
                            mTipsTv.setText("");
                            currentStateImageView.setImageResource(R.drawable.ic_current_lock);
                            timeLayout.setVisibility(View.VISIBLE);
                            seekBar.setLock(false);
                            Constants.isClockLocked = false;
                            Constants.totalSeconds = 0;
                            Constants.settingTimes = 0;
                            Constants.circleCounts = 0;
                            seekBar.setCircleTimeCounts(0);
                            seekBar.setCircleCounts(0);
                            seekBar.setCurrentprogress(0);
                            seekBar.setAngle(0);
                            seekBar.setLineAngle(0);
                        }
                        ActivityUtil.sendEvent4UM(getActivity(), "timeClock", type + "", 20);
                        isStartQuick = false;
                        break;
                    case What.HTTP_REQUEST_CURD_FAILURE:
                        Constants.isClockLocked = Constants.isClockLocked ? false : true;
                        isStartQuick = false;
                        ActivityUtil.showPopWindow4Tips(getActivity(), time_tips_layout, false, msg.obj + "");
                        break;
                }
                setTipsText4Mode();

            }
        });
        lockScreenNet.putParams(Constants.childs.get(0).getClientDeviceId() + "", time + "");
        lockScreenNet.sendRequest();
    }

    public void setCodeView() {
//        if (Constants.child.getLockPwd().equals("")) {
//            final Dialog dialog = new Dialog(getActivity(), R.style.AlertDialogCustom);
//            View view = View.inflate(getActivity(), R.layout.layout_setlockpswd_dialog, null);
//            TextView leftView = (TextView) view.findViewById(R.id.layout_kefu_left);
//            final TextView rightView = (TextView) view.findViewById(R.id.layout_kefu_right);
//            final EditText editText = view.findViewById(R.id.layout_setlock_et);
//            leftView.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View arg0) {
//                    isStartQuick = false;
//                    isStop = false;
//                    Constants.isClockLocked = false;
//                    dialog.dismiss();
//                }
//            });
//            rightView.setTextColor(getResources().getColor(R.color.gray));
//            rightView.setEnabled(false);
//            rightView.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View arg0) {
//                    SharUtil.saveLockScreenPsd(getActivity(), editText.getText().toString());
//                    startLock(8);
//                    dialog.dismiss();
//                }
//            });
//            editText.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                    if (s.length() >= 6) {
//                        rightView.setTextColor(getResources().getColor(R.color.set_lock_pwd_color));
//                        rightView.setEnabled(true);
//                    } else {
//                        rightView.setTextColor(getResources().getColor(R.color.gray));
//                        rightView.setEnabled(false);
//                    }
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//
//                }
//            });
//            dialog.setContentView(view);
//            WindowManager m = getActivity().getWindowManager();
//            Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
//            WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); // 获取对话框当前的参数值
//            p.width = (int) (d.getWidth() * 0.65); // 宽度设置为屏幕的0.95
//            dialog.getWindow().setAttributes(p);
//            dialog.show();
//        }else {
            SharUtil.saveLockScreenPsd(getActivity(),Constants.child.getLockPwd());
            startLock(8);
//        }
    }

    private void createTimerInit() {
        if (Constants.isThreadDestory) {
            mThread = new Thread(new TimerRunnable());
            Constants.isThreadDestory = false;
            mThread.start();
        }

    }

    class TimerRunnable implements Runnable {

        @Override
        public void run() {
            while (!Constants.isThreadDestory) {
                try {
                    if (Constants.isClockLocked) {
                        Thread.sleep(1000);
                        Message message = handler.obtainMessage();
                        message.what = 1;
                        handler.sendMessage(message);
                    }
                    if (Constants.totalSeconds <= 0) {
                        Constants.isThreadDestory = true;
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

    }


    @Override
    public void onResume() {
        super.onResume();
        CommonUtil.HandLockPassword(getActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (!CommonUtil.isForeground(getActivity())) {
            Constants.isAPPActive = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isStartQuick = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Constants.isThreadDestory = true;
    }

    private void setData2View() {
            Constants.totalSeconds = (int) (Long.parseLong(Constants.child.getModeTime()) - System.currentTimeMillis() / 1000);
            if (Constants.totalSeconds > 0) {
                Constants.isClockLocked = true;
                currentStateImageView.setImageResource(R.drawable.ic_current_unlock);
                createTimerInit();
                SharUtil.saveLockScreenPsd(getActivity(), Constants.child.getLockPwd());
                mTipsTv.setText("锁屏密码:" + Constants.child.getLockPwd());
            } else {
                Constants.isClockLocked = false;
            }
            setTipsText4Mode();
            if (Constants.isClockLocked) {
                currentStateImageView.setImageResource(R.drawable.ic_current_unlock);
            } else {
                currentStateImageView.setImageResource(R.drawable.ic_current_lock);
            }
            if (Constants.totalSeconds <= 0) {
                currentStateImageView.setImageResource(R.drawable.ic_current_lock);
                timeLayout.setVisibility(View.VISIBLE);
                seekBar.setLock(false);
                Constants.isClockLocked = false;
                Constants.totalSeconds = 0;
                Constants.settingTimes = 0;
                Constants.circleCounts = 0;
                seekBar.setCircleTimeCounts(0);
                seekBar.setCircleCounts(0);
                seekBar.setCurrentprogress(0);
                seekBar.setAngle(0);
                seekBar.setLineAngle(0);
                setTimeText(Constants.totalSeconds);
            }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
        }
    }

    private String[] toStandedTimeString(int tem) {
        int hours = tem / 3600;
        int minus = tem % 3600 / 60;
        int seconds = tem % 3600 % 60;
        String[] times = new String[3];
        times[0] = hours + "";
        if (minus / 10 == 0 && tem > 0 && hours != 0) {
            times[1] = "0" + minus;
        } else {
            times[1] = minus + "";
        }
        if (seconds / 10 == 0 && tem > 0 && Constants.isClockLocked && minus > 0) {
            times[2] = "0" + seconds;
        } else {
            times[2] = seconds + "";
        }
        return times;

    }

    @Override
    public void onProgressChange(CircularSeekBar view, int newProgress) {
        if (!Constants.isClockLocked) {
            Constants.settingTimes = newProgress;
            if (Constants.settingTimes > 0) {
                timeLayout.setVisibility(View.VISIBLE);
                hTextViewHint.setVisibility(View.VISIBLE);
                mTextViewHint.setVisibility(View.VISIBLE);
                sTextViewHint.setVisibility(View.VISIBLE);
                if (Constants.totalSeconds == 0 || Constants.isNetWork) {
                    Constants.totalSeconds = Constants.settingTimes * 360;
                }
                setTimeText(Constants.settingTimes * 360);
            } else {
                Constants.totalSeconds = 0;
                setTimeText(0);
            }
        }
    }

    /*
     * 根据时间秒 转换为 显示内容
     */
    private void setTimeText(int time) {
        String[] str = toStandedTimeString(time);

        if (str[0].equals("0")) {
            hTextView.setText("");
            hTextViewHint.setVisibility(View.GONE);
        } else {
            hTextView.setText(str[0]);
            hTextViewHint.setVisibility(View.VISIBLE);
        }
        if (str[1].equals("0")) {
            mTextView.setText("");
            mTextViewHint.setVisibility(View.GONE);
        } else {
            mTextView.setText(str[1]);
            mTextViewHint.setVisibility(View.VISIBLE);
        }
        if (str[2].equals("0")) {
            sTextView.setText("");
            sTextViewHint.setVisibility(View.GONE);
        } else {
            sTextView.setText(str[2]);
            sTextViewHint.setVisibility(View.VISIBLE);
        }
    }
}
