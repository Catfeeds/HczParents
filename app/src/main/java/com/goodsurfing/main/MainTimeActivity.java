package com.goodsurfing.main;

import java.util.HashMap;
import java.util.Map;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goodsurfing.app.R;
import com.goodsurfing.base.BaseFragment;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.server.GetAuthTimeServer;
import com.goodsurfing.server.UserAuthTimeServer;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResponder;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResult;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.CommonUtil;
import com.goodsurfing.view.customview.CircularSeekBar;
import com.umeng.analytics.MobclickAgent;

public class MainTimeActivity extends BaseFragment implements CircularSeekBar.OnSeekChangeListener {

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

	private boolean isClockWise = true;
	private boolean isStartQuick = false;
	// handler类接收数据
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if (Constants.totalSeconds != 0 && Constants.isClockLocked)
					isClockWise = true;
				if (msg.what == 1 && isClockWise) {
					if (Constants.mode != 1) {
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
							isClockWise = false;
							setTimeText(Constants.totalSeconds);
							seekBar.setCircleCounts(0);
							// startLocked(9);
						}
						if (Constants.isClockLocked && Constants.totalSeconds != 0) {
							int seconds = Constants.totalSeconds % 3600 % 60;
							seekBar.setAngle((int) (seconds * 6));
							seekBar.setLineAngle((seconds) * Math.PI / 30);
						}
					} else {
						currentStateImageView.setImageResource(R.drawable.ic_current_lock);
						Constants.isClockLocked = true;
						isClockWise = false;
						Constants.totalSeconds = 0;
						timeLayout.setVisibility(View.INVISIBLE);
						seekBar.setLock(true);
						seekBar.setAngle(360);
						seekBar.setLineAngle(2 * Math.PI);
					}
				}
				break;
			case 2:
				if ("".equals(Constants.userId)) {
					LoginActivity.gotoLogin(getActivity());
					return;
				}
				if (Constants.mode == 1) {
					currentStateImageView.setImageResource(R.drawable.ic_current_lock);
					Constants.isClockLocked = true;
					isClockWise = false;
					Constants.totalSeconds = 0;
					timeLayout.setVisibility(View.INVISIBLE);
					seekBar.setLock(false);
					seekBar.setAngle(360);
					seekBar.setLineAngle(2 * Math.PI);
					ActivityUtil.showPopWindow4Tips(getActivity(), time_tips_layout, false, "可以访问所有网络!");
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
		};
	};
	private View rootView;
	private boolean isStop = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (null == rootView) {
			rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_main_time, null);
			initViews(rootView);
		}
		ViewGroup group = (ViewGroup) rootView.getParent();
		if (group != null) {
			group.removeView(rootView);
		}
		init();
		return rootView;
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
		titleTextview.setText("网络解锁");
		titleRightIv.setVisibility(View.GONE);
		titleRightTv.setVisibility(View.GONE);
		titleLeftIv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				((MainActivity)getActivity()).mainPageView.setCurrentItem(0);
			}
		});
		currentStateImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if ("".equals(Constants.userId)) {
					LoginActivity.gotoLogin(getActivity());
					return;
				}
				if (Constants.mode == 1) {
					ActivityUtil.showPopWindow4Tips(getActivity(), time_tips_layout, false, "可以访问所有网络!");
					return;
				}
				if (isStartQuick)
					return;
				isStartQuick = true;
				isClockWise = false;
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

	private void init() {
		seekBar.setSeekBarChangeListener(this);
		seekBar.setHandler(handler);
		if (Constants.isClockLocked) {
			currentStateImageView.setImageResource(R.drawable.ic_current_lock);
		} else {
			currentStateImageView.setImageResource(R.drawable.ic_current_unlock);
		}
		if (!"".equals(Constants.userId) && Constants.mode != 1) {
			getTimerData();
		}
		if (Constants.totalSeconds > 0) {
			setTimeText(Constants.totalSeconds);
			if (Constants.isClockLocked) {
				Constants.circleCounts = Constants.totalSeconds / 60;
				seekBar.setCircleTimeCounts(Constants.circleCounts);
				// seekBar.setLock(true);
				int seconds = Constants.totalSeconds % 3600 % 60;
				seekBar.setAngle((int) (seconds * 6));
				seekBar.setLineAngle((seconds) * Math.PI / 30);
			} else {
				seekBar.setProgress(Constants.totalSeconds / 360);
			}
		} else {
			currentStateImageView.setImageResource(R.drawable.ic_current_unlock);
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

	private void setTipsText4Mode() {
		if (!"".equals(Constants.userId)) {
			String tipsString = "";
			mTipsModeTv.setVisibility(View.VISIBLE);
			mTipsTv.setVisibility(View.VISIBLE);
			timeLayout.setVisibility(View.VISIBLE);
			switch (Constants.mode) {
			case Constants.MODE_LEARNING:
				Constants.modeStr = "当前模式: 网络畅游模式";
				tipsString = "已为您开启所有网络访问权限";
				currentStateImageView.setImageResource(R.drawable.ic_current_lock);
				Constants.isClockLocked = true;
				timeLayout.setVisibility(View.INVISIBLE);
				isClockWise = false;
				Constants.totalSeconds = 0;
				seekBar.setLock(false);
				seekBar.setAngle(360);
				seekBar.setLineAngle(2 * Math.PI);
				break;
			case Constants.MODE_BAD:
				clearTime();
				Constants.modeStr = "当前模式: 教育资源模式";
				tipsString = "可滑动圆点设置解锁时长";
				break;
			case Constants.MODE_FREE:
				clearTime();
				Constants.modeStr = "当前模式: 不良内容拦截模式";
				tipsString = "可滑动圆点设置解锁时长";
				break;
			case Constants.MODE_REWARDS:
				clearTime();
				Constants.modeStr = "当前模式: 奖励卡模式";
				tipsString = "可滑动圆点设置解锁时长";
				break;
			}
			mTipsModeTv.setText(Constants.modeStr);
			mTipsTv.setText(tipsString);
		} else {
			mTipsModeTv.setVisibility(View.INVISIBLE);
			mTipsTv.setVisibility(View.INVISIBLE);
		}
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
		if ("".equals(Constants.userId)) {
			LoginActivity.gotoLogin(getActivity());
			return;
		}
		if (!Constants.isNetWork) {
			ActivityUtil.showPopWindow4Tips(getActivity(), time_tips_layout, false, "当前网络不可用，请稍后再试...");
			if (Constants.totalSeconds > 0) {
				Constants.isClockLocked = true;
				isClockWise = true;
			}
			isStartQuick = false;
			return;
		}
		/**
		 * 8.用户临时授权 get请求参数：userid=11111& token
		 * =fasfasfa&committype=8&timecount=100 <单位为s>
		 * 
		 * 9.用户临时授权结束 get请求参数：userid=11111& token =fasfasfa&committype=9
		 */
		String url;
		if (type == 8) {
			if (Constants.totalSeconds == 0) {
				Constants.totalSeconds = 8 * 60 * 60;
			}
			url = Constants.SERVER_URL + "?" + "committype=8" + "&userid=" + Constants.userId + "&token=" + Constants.tokenID + "&timecount=" + Constants.totalSeconds;
			ActivityUtil.showPopWindow4Tips(getActivity(), time_tips_layout, false, false, "网络解锁中...", -1);
		} else {
			ActivityUtil.showPopWindow4Tips(getActivity(), time_tips_layout, false, false, "开启网络保护中...", -1);
			url = Constants.SERVER_URL + "?" + "committype=9" + "&userid=" + Constants.userId + "&token=" + Constants.tokenID;
		}

		new UserAuthTimeServer(new DataServiceResponder() {

			@Override
			public void onResult(DataServiceResult result) {
				if (result == null || result.code == null) {
					return;
				}
				if ("0".equals(result.code) && type == 8) {
					if (!isStop)
						ActivityUtil.showPopWindow4Tips(getActivity(), time_tips_layout, true, "网络限制已解除，可以访问所有网络!");
					currentStateImageView.setImageResource(R.drawable.ic_current_lock);
					Constants.isClockLocked = true;
					isClockWise = true;
					timeLayout.setVisibility(View.VISIBLE);
					Constants.circleCounts = Constants.settingTimes * 6;
					createTimerInit();
				} else if ("0".equals(result.code) && type == 9) {
					if (!isStop)
						ActivityUtil.showPopWindow4Tips(getActivity(), time_tips_layout, true, "网络限制已开启，正在保护网络中!");
					currentStateImageView.setImageResource(R.drawable.ic_current_unlock);
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
				} else {
					if (!isStop)
						ActivityUtil.showPopWindow4Tips(getActivity(), time_tips_layout, false, result.extra + "");
				}
				ActivityUtil.sendEvent4UM(getActivity(), "timeClock", type+"", 20);
				isStartQuick = false;
			}

			@Override
			public void onFailure() {
				Constants.isClockLocked = Constants.isClockLocked ? false : true;
				isClockWise = true;
				isStartQuick = false;
			}
		}, url, getActivity()).execute();
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
		isStartQuick = false;
		isStop = false;
		setTipsText4Mode();
		if (!"".equals(Constants.userId)) {
			createTimerInit();
		} else {
			currentStateImageView.setImageResource(R.drawable.ic_current_unlock);
			timeLayout.setVisibility(View.VISIBLE);
			seekBar.setLock(false);
			clearTime();
		}
		ActivityUtil.sendEvent4UM(getActivity(), "functionSwitch", "unlock", 19);
	}

	@Override
	public void onStop() {
		super.onStop();
		if (!CommonUtil.isForeground(getActivity())) {
			Constants.isAPPActive = false;
		}
		isStop = true;
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

	/**
	 * 获取用户 授权时间
	 */
	private void getTimerData() {
		if (Constants.isGetTime)
			return;
		Constants.isGetTime = true;
		if (!Constants.isNetWork) {
			ActivityUtil.showPopWindow4Tips(getActivity(), time_tips_layout, false, "当前网络不可用，请稍后再试...");
			isStartQuick = false;
			return;
		}
		String url = Constants.SERVER_URL + "?" + "requesttype=12" + "&userid=" + Constants.userId + "&token=" + Constants.tokenID;
		// 加载中...
		ActivityUtil.showPopWindow4Tips(getActivity(), time_tips_layout, true, false, "正在加载...", -1);
		new GetAuthTimeServer(new DataServiceResponder() {

			@Override
			public void onResult(DataServiceResult result) {
				ActivityUtil.dismissPopWindow();
				if (null != result && "0".equals(result.code)) {
					int seconds = result.type;
					if (seconds > 0) {
						Constants.totalSeconds = seconds;
						Constants.isClockLocked = true;
						currentStateImageView.setImageResource(R.drawable.ic_current_lock);
						createTimerInit();
					}
				}
			}

			@Override
			public void onFailure() {
				ActivityUtil.dismissPopWindow();
			}
		}, url, getActivity()).execute();
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
