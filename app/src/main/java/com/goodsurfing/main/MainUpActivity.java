package com.goodsurfing.main;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goodsurfing.app.R;
import com.goodsurfing.base.BaseFragment;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.map.MapActivity;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.CommonUtil;
import com.goodsurfing.view.ReboundScrollView;

public class MainUpActivity extends BaseFragment implements OnClickListener {
    public static final int SHOW_LAYOUT = 101;
    public static final int DISMISS_LAYOUT = 102;
    private TextView helpTextView;
    private LinearLayout modeLayout;
    private LinearLayout timeLayout;
    private LinearLayout younLayout;
    private LinearLayout endLayout;
    private LinearLayout netLayout;
    private LinearLayout blackLayout;
    private LinearLayout carLayout;
    private LinearLayout kefuLayout;
    private LinearLayout moreLayout;
    private LinearLayout scollLayout;
    private View creat_serverLayout;
    private View rootView;
    private ReboundScrollView reboundScrollView;
    private ImageView toUpImageView;
    private ImageView toDownImageView;
    private TextView modeTextView;
    private AlphaAnimation animation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null != rootView) {
            ViewGroup group = (ViewGroup) rootView.getParent();
            if (group != null) {
                group.removeView(rootView);
            }
            return rootView;
        }
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_main_up, null);
        initViews(rootView);
        init();
        return rootView;
    }

    private void initViews(View view) {
        modeLayout = (LinearLayout) view.findViewById(R.id.main_mode_ll);
        timeLayout = (LinearLayout) view.findViewById(R.id.main_time_ll);
        younLayout = (LinearLayout) view.findViewById(R.id.main_youn_ll);
        carLayout = (LinearLayout) view.findViewById(R.id.main_car_ll);
        kefuLayout = (LinearLayout) view.findViewById(R.id.main_kefu_ll);
        blackLayout = (LinearLayout) view.findViewById(R.id.main_blackwirter_ll);
        endLayout = (LinearLayout) view.findViewById(R.id.main_end_ll);
        netLayout = (LinearLayout) view.findViewById(R.id.main_net_ll);
        moreLayout = (LinearLayout) view.findViewById(R.id.main_more_ll);
        scollLayout = (LinearLayout) view.findViewById(R.id.main_up_scroll_loading);
        reboundScrollView = (ReboundScrollView) view.findViewById(R.id.main_up_scroll_v);
        reboundScrollView.setmHandler(handler);
        helpTextView = (TextView) view.findViewById(R.id.main_help_tv);
        creat_serverLayout = view.findViewById(R.id.main_up_creat_server);
        toUpImageView = (ImageView) view.findViewById(R.id.to_up_jiantou);
        toDownImageView = (ImageView) view.findViewById(R.id.to_down_jiantou);
        modeTextView = (TextView) view.findViewById(R.id.main_up_scroll_mode_text);
    }

    private void init() {
        modeLayout.setOnClickListener(this);
        timeLayout.setOnClickListener(this);
        younLayout.setOnClickListener(this);
        carLayout.setOnClickListener(this);
        kefuLayout.setOnClickListener(this);
        blackLayout.setOnClickListener(this);
        endLayout.setOnClickListener(this);
        netLayout.setOnClickListener(this);
        moreLayout.setOnClickListener(this);
        helpTextView.setOnClickListener(this);
        animation = (AlphaAnimation) AnimationUtils.loadAnimation(getActivity(), R.anim.alpha_anim);
    }

    @Override
    public void onResume() {
        super.onResume();
        onViewResume();
    }

    private void onViewResume() {
        if (!"".equals(Constants.userId)) {
            MainActivity.iconView.setVisibility(View.GONE);
            switch (Constants.mode) {
                case 1:
                    Constants.modeStr = "当前模式: 网络畅游模式";
                    break;
                case 2:
                    Constants.modeStr = "当前模式: 教育资源模式";
                    break;
                case 3:
                    Constants.modeStr = "当前模式: 不良内容拦截模式";
                    break;
                case 4:
                    Constants.modeStr = "当前模式: 奖励卡模式";
                    break;
            }
            if (Constants.IS_SHOW_LAYOUT)
                handler.sendEmptyMessage(DISMISS_LAYOUT);
        } else {
            MainActivity.iconView.setVisibility(View.VISIBLE);
            handler.sendEmptyMessage(SHOW_LAYOUT);
            Constants.modeStr = "";
        }
        modeTextView.setText(Constants.modeStr);
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if(this.isVisible()) {
            if (isVisibleToUser) {
                onViewResume();
            }
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onClick(View v) {
        if (CommonUtil.isFastDoubleClick())
            return;
        switch (v.getId()) {
            case R.id.main_blackwirter_ll:
                ActivityUtil.sendEvent4UM(getActivity(), "functionSwitch", "blackwirterlist", 11);
                Intent black = new Intent(getActivity(), BlackAndWhiteListActivity.class);
                startActivity(black);
                break;
            case R.id.main_car_ll:
                ActivityUtil.sendEvent4UM(getActivity(), "functionSwitch", "rewards", 12);
                Intent car = new Intent(getActivity(), TimerCountsListActivity.class);
                startActivity(car);
                break;
            case R.id.main_end_ll:
                ActivityUtil.sendEvent4UM(getActivity(), "functionSwitch", "terminal", 13);
                Intent end = new Intent(getActivity(), TerminalControlActivity.class);
                startActivity(end);
                break;
            case R.id.main_help_tv:
                Intent help = new Intent(getActivity(), GuideView.class);
                Bundle bundle = new Bundle();
                bundle.putString("TYPE_GUIDE", "1");
                help.putExtras(bundle);
                startActivity(help);
                break;
            case R.id.main_kefu_ll:
                ActivityUtil.sendEvent4UM(getActivity(), "functionSwitch", "custormSerice", 14);
                showKeFuDialog();
                break;
            case R.id.main_mode_ll:
                ActivityUtil.sendEvent4UM(getActivity(), "functionSwitch", "functionSw", 15);
                Intent mode = new Intent(getActivity(), ModeChangeActivity.class);
                startActivity(mode);
                break;
            case R.id.main_more_ll:
                ActivityUtil.sendEvent4UM(getActivity(), "functionSwitch", "more", 16);
                Intent more = new Intent(getActivity(), MoreActivity.class);
                startActivity(more);
                break;
            case R.id.main_net_ll:
                ((MainActivity) getActivity()).mainPageView.setCurrentItem(1);
                break;
            case R.id.main_time_ll:
                ActivityUtil.sendEvent4UM(getActivity(), "functionSwitch", "timecontrol", 17);
                Intent time = new Intent(getActivity(), TimeControllerActivity.class);
                startActivity(time);
                break;
            case R.id.main_youn_ll:
                ActivityUtil.sendEvent4UM(getActivity(), "functionSwitch", "childrenmobile", 18);
                Intent youn = new Intent(getActivity(), MapActivity.class);
                startActivity(youn);
                break;
        }
    }

    /*
     * 显示客服dialog
     */
    private void showKeFuDialog() {
        final Dialog dialog = new Dialog(getActivity(), R.style.AlertDialogCustom);
        View view = View.inflate(getActivity(), R.layout.layout_kefu_dialog, null);
        TextView leftView = (TextView) view.findViewById(R.id.layout_kefu_left);
        TextView rightView = (TextView) view.findViewById(R.id.layout_kefu_right);
        leftView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        rightView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent phoneIntent = new Intent("android.intent.action.CALL", Uri.parse("tel:4006138137"));
                // 启动
                startActivity(phoneIntent);
                dialog.dismiss();
            }
        });
        dialog.setContentView(view);
        WindowManager m = getActivity().getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); // 获取对话框当前的参数值
        p.width = (int) (d.getWidth() * 0.65); // 宽度设置为屏幕的0.95
        dialog.getWindow().setAttributes(p);
        dialog.show();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Constants.isRegistShow) {
                switch (msg.what) {
                    case SHOW_LAYOUT:
                        if ("".equals(Constants.userId)) {
                            Constants.IS_SHOW_LAYOUT = true;
                            scollLayout.setVisibility(View.GONE);
                            toDownImageView.setVisibility(View.GONE);
                            toDownImageView.clearAnimation();
                            creat_serverLayout.setVisibility(View.VISIBLE);
                            creat_serverLayout.setOnClickListener(new OnClickListener() {

                                @Override
                                public void onClick(View arg0) {
                                    RegisterActivity.gotoRegister(getActivity());
                                    // startActivity(new Intent(getActivity(),
                                    // RegisterActivity.class));
                                }
                            });
                            toUpImageView.startAnimation(animation);
                        } else {
                            toDownImageView.setVisibility(View.GONE);
                            scollLayout.setVisibility(View.VISIBLE);
                        }
                        break;
                    case DISMISS_LAYOUT:
                        Constants.IS_SHOW_LAYOUT = false;
                        if ("".equals(Constants.userId)) {
                            toDownImageView.setVisibility(View.VISIBLE);
                            toDownImageView.startAnimation(animation);
                        } else {
                            toDownImageView.setVisibility(View.INVISIBLE);
                            toDownImageView.clearAnimation();
                        }
                        creat_serverLayout.setVisibility(View.GONE);
                        scollLayout.setVisibility(View.VISIBLE);
                        break;
                }
            } else {
                toUpImageView.setVisibility(View.GONE);
                toDownImageView.setVisibility(View.GONE);
                creat_serverLayout.setVisibility(View.GONE);
                scollLayout.setVisibility(View.VISIBLE);
            }
        }

        ;
    };

}
