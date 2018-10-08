package com.goodsurfing.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Xml;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.goodsurfing.adpter.ChargeChoicesAdapter;
import com.goodsurfing.app.R;
import com.goodsurfing.base.BaseActivity;
import com.goodsurfing.beans.ChargeIDBean;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.server.ChargeChoicesServer;
import com.goodsurfing.server.CheckServerServer;
import com.goodsurfing.server.DoGetCodeServer;
import com.goodsurfing.server.PayOkServer;
import com.goodsurfing.server.RegisterUserServer;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResponder;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResult;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.LogUtil;
import com.goodsurfing.utils.PayResult;
import com.goodsurfing.utils.RegisterCodeTimer;
import com.goodsurfing.utils.SignUtils;
import com.goodsurfing.view.customview.ServiceOrTypeDialog;
import com.goodsurfing.view.customview.ZjWheelView.OnWheelViewListener;
import com.goodsurfing.view.customview.city.LocationDialogBuilder;
import com.goodsurfing.view.customview.city.LocationDialogBuilder.OnSaveLocationLister;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import net.sourceforge.simcpux.MD5;
import net.sourceforge.simcpux.Util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 注册界面
 * 
 * @author 谢志杰
 * @create 2017-10-16 上午11:10:53
 * @version 1.0.0
 * @company 湖南一石科技有限公司 Copyright: 版权所有（c）2016
 */
public class RegisterActivity extends BaseActivity {

	private final static String TAG = "RegisterActivity";

	protected static final int REFRESH = 100;
	protected static final int GETCHOICESINFO = 102;
	protected static final int ZFB_PAY = 103;
	protected static final int WX_PAY = 104;
	private static final int INITMEDIA = 105;

	private final static String CITY_KEY = "CITY_NAME";
	private final static String TYPE_KEY = "TYPE_NAME";
	private final static String SERVICE_KEY = "SERICE_NAME";

	private Context context;

	@ViewInject(R.id.tv_title)
	private TextView title;

	@ViewInject(R.id.tv_title_right)
	private TextView right;

	@ViewInject(R.id.title_layout)
	private View title_layout;
	@ViewInject(R.id.activity_register_adress_tv)
	private TextView cityTextView;

	@ViewInject(R.id.activity_register_service_tv)
	private TextView serviceTextView;

	@ViewInject(R.id.activity_add_children_et_phone)
	private EditText phoneEt;

	@ViewInject(R.id.activity_add_children_et_code)
	private EditText codeEt;

	@ViewInject(R.id.activity_add_children_delete_phone)
	private ImageView deleteIv;

	@ViewInject(R.id.activity_add_children_getcode)
	private TextView codeView;
	@ViewInject(R.id.activity_register_tips_text)
	private TextView tipsTextView;

	@ViewInject(R.id.activity_register_commit_rl)
	private RelativeLayout signBtnTv;

	@ViewInject(R.id.activity_charge_choices_lv)
	private ListView mPullListView;

	@ViewInject(R.id.activity_register_id_rl)
	private LinearLayout chargeLayout;

	private String serviceName;
	private String cityName;
	private ChargeChoicesAdapter Adapter;
	public List<ChargeIDBean> listAdapter = new ArrayList<ChargeIDBean>();
	private String bodyName;
	private String fee;
	private String packgeId;
	@ViewInject(R.id.check_xieyi)
	private CheckBox checkBox;
	private boolean login_ok = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		ViewUtils.inject(this);
		init();
		initPayData();
		// 视频播放
		initView();
		initController();
		// 动态注册广播接受者
		receiver = new MyVideoBroadcastReceiver();
		registerReceiver(receiver, new IntentFilter("com.amy.day43_03_SurfaceViewMediaPlayer"));
	}

	private void init() {
		context = this;
		title.setText("开通");
		right.setVisibility(View.INVISIBLE);

		phoneEt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence chars, int arg1, int arg2, int arg3) {
				if (chars.length() > 0) {
					deleteIv.setVisibility(View.VISIBLE);
				} else {
					deleteIv.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});
		codeEt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				if (arg0.length() == 6) {
					signBtnTv.setEnabled(true);
					signBtnTv.setBackgroundResource(R.drawable.view_bottom_button_bg);
				} else {
					signBtnTv.setEnabled(false);
					signBtnTv.setBackgroundResource(R.drawable.view_btn_gray__bg);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});
		Adapter = new ChargeChoicesAdapter(RegisterActivity.this, R.layout.item_choices_cell, listAdapter);
		tipsTextView.setVisibility(View.GONE);
		chargeLayout.setVisibility(View.GONE);
		mPullListView.setAdapter(Adapter);
		mPullListView.setDivider(null);
		mPullListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				bodyName = listAdapter.get(position).getName();
				fee = listAdapter.get(position).getPrice();
				packgeId = listAdapter.get(position).getId();
				for (ChargeIDBean bean : listAdapter) {
					bean.setChecked(false);
				}
				listAdapter.get(position).setChecked(true);
				Adapter.notifyDataSetChanged();
			}
		});
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (arg1) {
					login_ok = true;
				} else {
					login_ok = false;
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		ActivityUtil.sendEvent4UM(this, "functionSwitch", "register", 26);
		initMediaPlayer();
	}

	/**
	 * 获取服务商列表
	 */
	private void getServerList() {
//		String url = null;
//		url = Constants.SERVER_URL_GLOBAL + "?" + "requesttype=1002";
//
//		new GetServerListServer(new DataServiceResponder() {
//
//			@Override
//			public void onResult(DataServiceResult result) {
//			}
//
//			@Override
//			public void onFailure() {
//			}
//		}, url, this).execute();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mediaPlayer != null) {
			try {
				mediaPlayer.stop();
				mediaPlayer.release();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@OnClick(R.id.activity_add_children_getcode)
	private void onClickGetCode(View view) {
		codeView.setEnabled(false);
		doGetCode();
	}

	public static void gotoRegister(Context ctx) {
		Intent intent = new Intent(ctx, RegisterActivity.class);
		intent.putExtra("code", 0);
		(ctx).startActivity(intent);
		((Activity) ctx).overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_remain);

	}

	@OnClick(R.id.activity_add_children_delete_phone)
	private void onClickDeletePhone(View view) {
		phoneEt.setText("");
	}

	@OnClick(R.id.activity_register_commit_rl)
	private void onClickSignCode(View view) {
		if (login_ok) {
			doSignCode();
		} else {
			ActivityUtil.showPopWindow4Tips(this, title_layout, false, "请先勾选用户使用协议");
		}
	}

	@OnClick(R.id.xieyi_text)
	public void onXieyiTextClick(View view) {
		ActivityUtil.showXyDialog(context);
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 正在倒计时
			case RegisterCodeTimer.IN_RUNNING:
				codeView.setBackgroundResource(R.drawable.add_children_gray);
				codeView.setText("已发送" + time-- + "秒");
				if (time > 0)
					mHandler.sendEmptyMessageDelayed(RegisterCodeTimer.IN_RUNNING, 1000);
				else
					mHandler.sendEmptyMessage(RegisterCodeTimer.END_RUNNING);
				break;

			// 完成倒计时
			case RegisterCodeTimer.END_RUNNING:
				codeView.setBackgroundResource(R.drawable.view_bottom_button_bg);
				mHandler.removeMessages(RegisterCodeTimer.IN_RUNNING);
				codeView.setEnabled(true);
				codeView.setText("重新获取");
				break;
			case GETCHOICESINFO:
				getChoicesInfo();
				break;
			case SDK_PAY_FLAG:
				PayResult payResult = new PayResult((String) msg.obj);
				/**
				 * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
				 * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
				 * docType=1) 建议商户依赖异步通知
				 */
				String resultInfo = payResult.getResult();// 同步返回需要验证的信息

				String resultStatus = payResult.getResultStatus();
				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					Toast.makeText(RegisterActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
					registerUser();
				} else {
					// 判断resultStatus 为非"9000"则代表可能支付失败
					// "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						Toast.makeText(RegisterActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();
					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						Toast.makeText(RegisterActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
					}
				}
				break;
			case SDK_CHECK_FLAG:
				Toast.makeText(RegisterActivity.this, "检查结果为：" + msg.obj, Toast.LENGTH_SHORT).show();
				break;
			case ZFB_PAY:
				pay();
				break;
			case WX_PAY:
				weiXinPay();
				break;
			}
		};
	};

	/**
	 * 注册请求
	 */
	private void registerUser() {
		String url = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");// 设置日期格式
		String ordernum = "HSW003" + df.format(new Date()) + (int) (Math.random() * 1000000);
		url = Constants.SERVER_URL + "?" + "committype=20" + "&Mobile=" + Constants.userMobile + "&packageid=" + packgeId + "&ordernum=" + ordernum;

		new RegisterUserServer(new DataServiceResponder() {

			@Override
			public void onResult(DataServiceResult result) {
				listAdapter.clear();
				if (null != result && "0".equals(result.code)) {
					startActivity(new Intent(RegisterActivity.this, BindActivity.class));
					onBackPressed();
				} else {
					ActivityUtil.showPopWindow4Tips(RegisterActivity.this, title_layout, false, result.extra + "");
				}
			}

			@Override
			public void onFailure() {

			}
		}, url, this).execute();
	}

	/**
	 * 获取套餐列表请求
	 */
	private void getChoicesInfo() {

		String url = null;

		url = Constants.SERVER_URL + "?" + "requesttype=10";
		new ChargeChoicesServer(new DataServiceResponder() {

			@Override
			public void onResult(DataServiceResult result) {
				listAdapter.clear();
				if (null != result && "0".equals(result.code)) {
					ActivityUtil.dismissPopWindow();
					tipsTextView.setVisibility(View.VISIBLE);
					chargeLayout.setVisibility(View.VISIBLE);
					listAdapter.addAll((List<ChargeIDBean>) result.result);
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, listAdapter.size() * getResources().getDimensionPixelSize(R.dimen.activity_price_ll_height));
					chargeLayout.setLayoutParams(params);
					listAdapter.get(0).setChecked(true);
					bodyName = listAdapter.get(0).getName();
					fee = listAdapter.get(0).getPrice();
					packgeId = listAdapter.get(0).getId();
					Adapter.notifyDataSetChanged();
				} else {
					ActivityUtil.showPopWindow4Tips(RegisterActivity.this, title_layout, false, result.extra + "");
				}
			}

			@Override
			public void onFailure() {
				ActivityUtil.dismissPopWindow();
			}
		}, url, this).execute();

	}

	private int time = 60;

	private String phoneNum;

	/**
	 * 获取手机验证码
	 */
	private void doGetCode() {
		if (!Constants.isNetWork) {
			ActivityUtil.showPopWindow4Tips(this, title_layout, false, "当前网络不可用，请稍后再试...");
			codeView.setEnabled(true);
			return;
		}

		String phoneNum = phoneEt.getText().toString();

		if ("".equals(phoneNum) || !phoneNum.matches("^[1-9]\\d{10}$")) {
			ActivityUtil.showPopWindow4Tips(this, title_layout, false, "请输入正确的手机号");
			codeView.setEnabled(true);
			return;
		}
		String times = System.currentTimeMillis() + "";
		long token = Integer.parseInt(times.substring(times.length() - 5, times.length())) * Integer.parseInt(phoneNum.substring(phoneNum.length() - 4, phoneNum.length()));
		String keyString = "shian" + token + "haoup";

		String key = MD5.getMessageDigest(keyString.getBytes());
		String url = null;
		url = Constants.EDIT_PHONE_CODE_URL + "?c=index&a=index&tel=" + phoneNum + "&token=" + times + "&key=" + key;
		new DoGetCodeServer(new DataServiceResponder() {

			@Override
			public void onResult(DataServiceResult result) {
				try {
					JSONObject jsonObject = new JSONObject((String) result.result);
					String errCode = null;
					errCode = jsonObject.getString("statusCode");
					if ("000000".equals(errCode)) {
						ActivityUtil.showPopWindow4Tips(RegisterActivity.this, title_layout, true, "发送验证码成功");
						mHandler.sendEmptyMessageDelayed(RegisterCodeTimer.IN_RUNNING, 0);
					} else {
						String message = jsonObject.getString("message");
						time = 60;
						ActivityUtil.showPopWindow4Tips(RegisterActivity.this, title_layout, false, message);
						mHandler.sendEmptyMessageDelayed(RegisterCodeTimer.END_RUNNING, 0);
					}
				} catch (Exception e) {
					ActivityUtil.showPopWindow4Tips(RegisterActivity.this, title_layout, false, "发送验证码失败");
					mHandler.sendEmptyMessageDelayed(RegisterCodeTimer.END_RUNNING, 0);
					LogUtil.logError(e);
				}
			}

			@Override
			public void onFailure() {
				time = 60;
				ActivityUtil.showPopWindow4Tips(RegisterActivity.this, title_layout, false, "发送验证码失败");
				mHandler.sendEmptyMessageDelayed(RegisterCodeTimer.END_RUNNING, 0);
			}
		}, url, this).execute();

	}

	/**
	 * 验证手机验证码
	 */
	private void doSignCode() {
		if (!Constants.isNetWork) {
			ActivityUtil.showPopWindow4Tips(this, title_layout, false, "当前网络不可用，请稍后再试...");
			return;
		}

		phoneNum = phoneEt.getText().toString();
		String url = null;

		String code = codeEt.getText().toString();
		if ("".equals(code) || !code.matches("^[0-9]{6}$")) {
			ActivityUtil.showPopWindow4Tips(this, title_layout, false, "验证码格式不正确");
			return;
		}
		if (cityName == null || "".equals(cityName)) {
			ActivityUtil.showPopWindow4Tips(this, title_layout, false, "请选择所在地");
			return;
		}
		if (serviceName == null || "".equals(serviceName)) {
			ActivityUtil.showPopWindow4Tips(this, title_layout, false, "请选择所在地");
			return;
		}
		if (packgeId == null || "".equals(packgeId)) {
			ActivityUtil.showPopWindow4Tips(this, title_layout, false, "请先选择一款套餐");
			return;
		}
		if (!Constants.isbusy) {
			ActivityUtil.showPopWindow4Tips(this, title_layout, false, "当前区暂未开通服务");
			return;
		}

		url = Constants.EDIT_PHONE_CODE_URL + "?c=index&a=getCode&tel=" + phoneNum + "&code=" + code;
		new DoGetCodeServer(new DataServiceResponder() {

			@Override
			public void onResult(DataServiceResult result) {
				try {
					JSONObject jsonObject = new JSONObject((String) result.result);
					String errCode = null;
					errCode = jsonObject.getString("status");

					if ("1".equals(errCode)) {
						ActivityUtil.showPopWindow4Tips(RegisterActivity.this, title_layout, true, "验证成功");
						Constants.cityName = cityName;
						Constants.prodiver = serviceName;
						Constants.userMobile = phoneNum;
						payMentDialog(RegisterActivity.this, mHandler);
					} else if ("0".equals(errCode)) {
						ActivityUtil.showPopWindow4Tips(RegisterActivity.this, title_layout, false, "验证码错误");
					} else if ("-1".equals(errCode)) {
						ActivityUtil.showPopWindow4Tips(RegisterActivity.this, title_layout, false, "验证码过期");
					}
				} catch (Exception e) {
					LogUtil.logError(e);
				}
			}

			@Override
			public void onFailure() {

			}
		}, url, this).execute();
	}

	@OnClick(R.id.activity_register_charge_rl)
	public void onCityClick(View v) {
		doCity();
	}

	/**
	 * 切换城市 dialog
	 */
	protected void doCity() {
		LocationDialogBuilder locationDialog = LocationDialogBuilder.getInstance(this);
		locationDialog.setOnSaveLocationLister(new OnSaveLocationLister() {
			@Override
			public void onSaveLocation(String province, String city, String provinceId, String cityId) {
				cityName = city;
				cityTextView.setText(city);
				saveInfo2Local(CITY_KEY, city);
				if (city == null || "".equals(city)) {
					ActivityUtil.showPopWindow4Tips(RegisterActivity.this, title_layout, false, "请选择所在地");
					return;
				}
				checkLocationInfo(city, serviceName);
				getServerList();
			}
		});
		locationDialog.show();
	}

	@OnClick(R.id.activity_register_service_rl)
	public void onServiceClick(View v) {
		showServiceDialog();
	}

	/**
	 * 切换 服务商 dialog
	 */
	private void showServiceDialog() {
		ServiceOrTypeDialog dialog = ServiceOrTypeDialog.getInstance(this);
		dialog.setDialogProperties("选择运营商", Constants.serverList);
		dialog.setOnSaveServiceLister(new OnWheelViewListener() {

			@Override
			public void onSelected(int selectedIndex, String item) {
				if (cityName == null || "".equals(cityName)) {
					ActivityUtil.showPopWindow4Tips(RegisterActivity.this, title_layout, false, "请选择所在地");
					return;
				}
				serviceName = item;
				serviceTextView.setText(serviceName);
				checkLocationInfo(cityName, serviceName);
				saveInfo2Local(SERVICE_KEY, serviceName);

			}
		});
		dialog.show();
	}

	/**
	 * 切换 地址或服务器后 更新服务器地址IP
	 * 
	 * @param province
	 * @param service
	 */
	private void checkLocationInfo(String province, String service) {
		if (!Constants.isNetWork) {
			Toast.makeText(this, "您的网络已断开，请检查网络", Toast.LENGTH_SHORT).show();
			return;
		}
		if (null == service || null == province || province.equals("") || service.equals("")) {
			return;
		}
		String url = null;
		url = Constants.SERVER_URL_GLOBAL + "?" + "requesttype=1004" + "&area=" + province + "&provider=" + service;
		ActivityUtil.showPopWindow4Tips(this, title_layout, false, false, "正在获取服务套餐...", -1);
		new CheckServerServer(new DataServiceResponder() {

			@Override
			public void onResult(DataServiceResult result) {
				if (result == null || result.code == null) {
					return;
				}
				if ("0".equals(result.code)) {
					Constants.isbusy = true;
					mHandler.sendEmptyMessage(GETCHOICESINFO);
				} else {
					Constants.isbusy = false;
					Constants.SERVER_URL = Constants.SERVER_URL_GLOBAL;
					ActivityUtil.showPopWindow4Tips(RegisterActivity.this, title_layout, false, result.extra + "");
				}
			}

			@Override
			public void onFailure() {
				Constants.isbusy = false;
				Constants.SERVER_URL = Constants.SERVER_URL_GLOBAL;
			}
		}, url, context).execute();
	}

	private void saveInfo2Local(String key, String value) {
		Editor editor = getSharedPreferences("LOGIN_INFO", Activity.MODE_PRIVATE).edit(); // 获取编辑器
		editor.putString(key, value);
		editor.commit();// 提交修改
	}

	@OnClick(R.id.iv_title_left)
	public void onHeadBackClick(View view) {
		onBackPressed();
	}

	/**
	 * 显示支付 dialog
	 * 
	 * @param mycontext
	 * @param handler
	 */
	public void payMentDialog(final Context mycontext, final Handler handler) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mycontext, R.style.CustomDialog);
		builder.setCancelable(false);
		final AlertDialog dialog = builder.show();
		View view = LayoutInflater.from(mycontext).inflate(R.layout.payment_dialog_layout, null);
		Window window = dialog.getWindow();
		window.setGravity(Gravity.BOTTOM);
		WindowManager m = ((Activity) mycontext).getWindowManager();
		Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		WindowManager.LayoutParams p = window.getAttributes(); // 获取对话框当前的参数值
		p.height = (int) (d.getHeight() * 0.4); // 高度设置为屏幕的0.6
		p.width = (int) (d.getWidth() * 1.0); // 宽度设置为屏幕的0.65
		window.setAttributes(p);
		window.setContentView(view);
		WindowManager.LayoutParams llp = ((Activity) mycontext).getWindow().getAttributes();
		llp.alpha = 0.5f;
		((Activity) mycontext).getWindow().setAttributes(llp);
		((Activity) mycontext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		view.findViewById(R.id.pay_ment_zfb_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				WindowManager.LayoutParams llp = ((Activity) mycontext).getWindow().getAttributes();
				llp.alpha = 1.0f;
				((Activity) mycontext).getWindow().setAttributes(llp);
				((Activity) mycontext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
				handler.sendEmptyMessage(RegisterActivity.ZFB_PAY);
				dialog.dismiss();
			}
		});
		view.findViewById(R.id.pay_ment_wx_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				WindowManager.LayoutParams llp = ((Activity) mycontext).getWindow().getAttributes();
				llp.alpha = 1.0f;
				((Activity) mycontext).getWindow().setAttributes(llp);
				((Activity) mycontext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
				handler.sendEmptyMessage(RegisterActivity.WX_PAY);
				dialog.dismiss();
			}
		});
	}

	// 商户PID
	public static final String PARTNER = "2088121009856945";
	// 商户收款账号
	public static final String SELLER = "2088121009856945";
	// 商户私钥，pkcs8格式
	public static final String RSA_PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMes/9apq+DTkl7xQ2f3vtqxCT2TPI4Mjwg1JIvxGmki2VKlkrafvvmKT/xi3ay07x57yjndO3F0J6wWAT6kUNR4TR/jYeXqDKpOuWxPZ8BpOqZrlUzwXIweu2EIas/+flA7Er8oq9e/sWdmqCEgFE930F/HGdRM3IOwVdN0/RedAgMBAAECgYEAhMNDTEB+Vrt17Aiwj9VLIe9qPHXEYpJ5G7Tx+tYxgEw6gVgzp5epjBPpwN8fkzCueO9H85dkabgYlLQA2dy5HI7Z050sOle4CmmkW/ccC1dr5WNyhM9IrezHZIa0yDJzKacFixiGAF6xmoLA6Ife54avTqvSZAwVNOUQfNnnReECQQD0cxpSFnjxr+AXoGg07BEmitustbXphf6Yjh+sELe/8AhgDTYPQUF6aeT4VVTA4kYDnhs//srE2YLS2EJO8y0pAkEA0RxOSxWUNAgnqGcmJVoJR38yGGbn2WkH+/wfGnx+M871CR17Jt0OAmM25pkvJfiZ7rsQNHNtIc3aYH/IRtBxVQJATcMP7G0ZrEi2kM2GWM9/5TLnDtn/NHpbs0wC50mqKnTBNUz+lXu8yKRHIniCrZlNjHkPUhxLhLNs2oXREixpgQJAG6qyFS8at7Oog5h6LJD4D1Sd7SqYXGSQIN/fwaJdFD+6neUfqSmwM9KqreHwogZ9X1+yqi3nb4SL8x6VAgGMLQJAaFSfJgM9X3nyWBxoFNjlu2sQh6ynIfAJM1wr3ITfAitlCkB6DbX5M4iRMIU26G/pYf2TUEm6b3SrP5MDgjkPtw==";
	// 支付宝公钥
	public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
	private static final int SDK_PAY_FLAG = 1;
	private static final int SDK_CHECK_FLAG = 2;

	PayReq req;
	final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
	StringBuffer sb;
	Map<String, String> resultunifiedorder;

	private void initPayData() {
		req = new PayReq();
		sb = new StringBuffer();

		msgApi.registerApp(Constants.APP_ID);

		registerReceiver(mReceiver, new IntentFilter(Constants.WEIX_PAY_BROAD));
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			String result = "";
			try {
				int errCode = arg1.getExtras().getInt("type");
				switch (errCode) {
				case BaseResp.ErrCode.ERR_OK:
					registerUser();
					result = "支付成功";
					break;
				case BaseResp.ErrCode.ERR_USER_CANCEL:
					result = "用户取消";
					break;
				case BaseResp.ErrCode.ERR_AUTH_DENIED:
					result = "用户拒绝";
					break;
				}
			} catch (Exception e) {
				result = "支付失败";
			}
			Toast.makeText(RegisterActivity.this, result, Toast.LENGTH_SHORT).show();
		}
	};

	public void pay() {
		if (TextUtils.isEmpty(PARTNER) || TextUtils.isEmpty(RSA_PRIVATE) || TextUtils.isEmpty(SELLER)) {
			new AlertDialog.Builder(this).setTitle("警告").setMessage("需要配置PARTNER | RSA_PRIVATE| SELLER").setPositiveButton("确定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialoginterface, int i) {
					//
					onBackPressed();
				}
			}).show();
			return;
		}

		String s = "";

		try {
			s = new String(bodyName.getBytes("UTF-8"), "UTF-8");
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		String orderInfo = getOrderInfo(s, "商品的详细描述", fee);
		// String orderInfo = getOrderInfo(s, "商品的详细描述",
		// "0.01");

		/**
		 * 特别注意，这里的签名逻辑需要放在服务端，切勿将私钥泄露在代码中！
		 */
		String sign = sign(orderInfo);
		try {
			/**
			 * 仅需对sign 做URL编码
			 */
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		/**
		 * 完整的符合支付宝参数规范的订单信息
		 */
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();

		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(RegisterActivity.this);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo, true);
				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	/**
	 * check whether the device has authentication alipay account.
	 * 查询终端设备是否存在支付宝认证账户
	 * 
	 */
	public void check(View v) {
		Runnable checkRunnable = new Runnable() {
			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask payTask = new PayTask(RegisterActivity.this);
				// 调用查询接口，获取查询结果
				boolean isExist = payTask.checkAccountIfExist();

				Message msg = new Message();
				msg.what = SDK_CHECK_FLAG;
				msg.obj = isExist;
				mHandler.sendMessage(msg);
			}
		};

		Thread checkThread = new Thread(checkRunnable);
		checkThread.start();

	}

	/**
	 * get the sdk version. 获取SDK版本号
	 * 
	 */
	public void getSDKVersion() {
		PayTask payTask = new PayTask(this);
		String version = payTask.getVersion();
		Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
	}

	/**
	 * create the order info. 创建订单信息
	 * 
	 */
	private String getOrderInfo(String subject, String body, String price) {

		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm" + "\"";

		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}

	/**
	 * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
	 * 
	 */
	private String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);

		Random r = new Random();
		key = key + r.nextInt();
		key = key.substring(0, 15);
		return key;
	}

	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	private String sign(String content) {
		return SignUtils.sign(content, RSA_PRIVATE);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	private String getSignType() {
		return "sign_type=\"RSA\"";
	}

	private void weiXinPay() {
		GetPrepayIdTask getPrepayId = new GetPrepayIdTask();
		getPrepayId.execute();

	}

	/* 微信支付相关 */
	/**
	 * 生成签名
	 */

	private String genPackageSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(Constants.API_KEY);

		String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
		Log.e("key:", packageSign);
		return packageSign;
	}

	private String genAppSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(Constants.API_KEY);

		this.sb.append("sign str\n" + sb.toString() + "\n\n");
		String appSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
		Log.e("orion", appSign);
		return appSign;
	}

	private String toXml(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();
		sb.append("<xml>");
		for (int i = 0; i < params.size(); i++) {
			sb.append("<" + params.get(i).getName() + ">");

			sb.append(params.get(i).getValue());
			sb.append("</" + params.get(i).getName() + ">");
		}
		sb.append("</xml>");

		Log.e("orion", sb.toString());
		return sb.toString();
	}

	private class GetPrepayIdTask extends AsyncTask<Void, Void, Map<String, String>> {

		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(RegisterActivity.this, "提示", "正在获取预支付订单...");
		}

		@Override
		protected void onPostExecute(Map<String, String> result) {
			if (dialog != null) {
				dialog.dismiss();
			}
			if (result == null)
				return;
			sb.append("prepay_id\n" + result.get("prepay_id") + "\n\n");
			// show.setText(sb.toString());

			resultunifiedorder = result;
			genPayReq();
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected Map<String, String> doInBackground(Void... params) {
			Map<String, String> xml = null;
			try {
				String url = String.format("https://api.mch.weixin.qq.com/pay/unifiedorder");
				String entity = genProductArgs();

				Log.e("orion", entity);

				byte[] buf = Util.httpPost(url, entity);

				String content = new String(buf);
				Log.e("result:", content);
				xml = decodeXml(content);
			} catch (Exception e) {
				Log.i("Exception", "微信确认订单失败");
				e.printStackTrace();
			}
			return xml;
		}
	}

	public Map<String, String> decodeXml(String content) {

		try {
			Map<String, String> xml = new HashMap<String, String>();
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new StringReader(content));
			int event = parser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {

				String nodeName = parser.getName();
				switch (event) {
				case XmlPullParser.START_DOCUMENT:

					break;
				case XmlPullParser.START_TAG:

					if ("xml".equals(nodeName) == false) {
						// 实例化student对象
						xml.put(nodeName, parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				event = parser.next();
			}

			return xml;
		} catch (Exception e) {
			Log.e("orion", e.toString());
		}
		return null;

	}

	private String genNonceStr() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
	}

	private long genTimeStamp() {
		return System.currentTimeMillis() / 1000;
	}

	private String genOutTradNo() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
	}

	//
	private String genProductArgs() {
		StringBuffer xml = new StringBuffer();

		try {
			String nonceStr = genNonceStr();

			xml.append("</xml>");
			List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
			packageParams.add(new BasicNameValuePair("appid", Constants.APP_ID));
			String s = new String(bodyName.getBytes("UTF-8"), "UTF-8");

			packageParams.add(new BasicNameValuePair("body", s));//
			packageParams.add(new BasicNameValuePair("mch_id", Constants.MCH_ID));
			packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
			packageParams.add(new BasicNameValuePair("notify_url", "http://121.40.35.3/test"));
			packageParams.add(new BasicNameValuePair("out_trade_no", genOutTradNo()));
			packageParams.add(new BasicNameValuePair("spbill_create_ip", "121.40.35.3"));
			double v = Double.valueOf(fee) * 100;
			String fees = String.valueOf(v);

			packageParams.add(new BasicNameValuePair("total_fee", fees.substring(0, fees.lastIndexOf("."))));
			packageParams.add(new BasicNameValuePair("trade_type", "APP"));

			String sign = genPackageSign(packageParams);
			packageParams.add(new BasicNameValuePair("sign", sign));

			String xmlstring = toXml(packageParams);

			return new String(xmlstring.toString().getBytes(), "ISO8859-1");

		} catch (Exception e) {
			Log.e(TAG, "genProductArgs fail, ex = " + e.getMessage());
			return null;
		}

	}

	// 生成签名参数
	private void genPayReq() {
		/*
		 * req.appId = "wx0e4f2c1890a9a60e"; req.partnerId = "1250886701";
		 * req.prepayId = "wx201507031507437aea42539a0164660158"; //
		 * req.prepayId = "wx2015070314124806d012e3bf0534791906";
		 * req.packageValue = "Sign=WXPay"; req.nonceStr =
		 * "452vql0loyvdgerjbgs91hzrkn9atbjc"; // req.nonceStr =
		 * "19tj9dp8o3jv165xij3cq0fwx712os4a"; req.timeStamp = "1435907263";
		 */
		req.appId = Constants.APP_ID;
		req.partnerId = Constants.MCH_ID;
		String prepayID = resultunifiedorder.get("prepay_id");
		req.prepayId = prepayID;
		req.packageValue = "Sign=WXPay";
		req.nonceStr = genNonceStr();
		req.timeStamp = String.valueOf(genTimeStamp());

		List<NameValuePair> signParams = new LinkedList<NameValuePair>();
		signParams.add(new BasicNameValuePair("appid", req.appId));
		signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
		signParams.add(new BasicNameValuePair("package", req.packageValue));
		signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
		signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
		signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));

		req.sign = genAppSign(signParams);

		sb.append("sign\n" + req.sign + "\n\n");

		// show.setText(sb.toString());
		sendPayReq();
		Log.e("orion", signParams.toString());

	}

	// 调起微信支付
	private void sendPayReq() {
		msgApi.registerApp(Constants.APP_ID);
		msgApi.sendReq(req);
	}

	/**
	 * 获取 用户到期时间 已无用
	 */
	private void getPayTime() {

		/*
		 * 编号（固定HSW001）+时间(年月日时分秒)+随机数（6位）
		 * 
		 * web使用HSW001， ios: HSW002, andorid:HSW003
		 * 
		 * 示例: HSW00120160314112211234567
		 * 
		 * 在支付时自动生成订单编号，支付成功后传给appserver
		 * 
		 * 17 支付成功结果提交（3.17加入订单编号）
		 * GET格式：userid=24&committype=16&token=token145208220824
		 * &packageid=5&account=ppp111&ordernum=HSW00120160317093411123456"
		 * packageid为请求消息10中提到的唯一标识；
		 */
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");// 设置日期格式
		String ordernum = "HSW003" + df.format(new Date()) + (int) (Math.random() * 1000000);
		String url = null;
		url = Constants.SERVER_URL + "?" + "committype=16" + "&userid=" + Constants.userId + "&token=" + Constants.tokenID + "&account=" + Constants.Account + "&packageid=" + packgeId + "&ordernum=" + ordernum;

		new PayOkServer(new DataServiceResponder() {

			@Override
			public void onResult(DataServiceResult result) {
				if ("0".equals(result.code)) {
					// Intent intent = new Intent(RegisterActivity.this,
					// PayOkActivity.class);
					// Bundle bundle = new Bundle();
					// bundle.putString("name",
					// chargeName.getText().toString());
					// bundle.putString("money",
					// chargeValues.getText().toString());
					// bundle.putString("time", "到期时间" + result.extra);
					// Constants.dealTime = result.extra;
					// intent.putExtras(bundle);
					// startActivity(intent);
					// onBackPressed();
				}
			}

			@Override
			public void onFailure() {

			}
		}, url, this).execute();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			unregisterReceiver(mReceiver);
			unregisterReceiver(receiver);
			timer.cancel();
			if (mediaPlayer != null) {
				mediaPlayer.release();
				mediaPlayer = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		handler.removeCallbacksAndMessages(null);
	}

	/**
	 * 视频播放 相关
	 */
	private Context mContext = this;
	private SurfaceView surfaceView = null;
	private SurfaceHolder surfaceHolder = null;
	private MediaPlayer mediaPlayer = null;
	private ImageView imageView_main_show = null;

	// 自定义的控制条及其上的控件
	private View controllerView;
	// private PopupWindow popupWindow;

	private ImageView imageView_play;
	private ImageView imageView_fullscreen;
	private SeekBar seekBar;
	private TextView textView_playTime;
	private TextView textView_duration;
	private String filePath = null;

	private float densityRatio = 1.0f; // 密度比值系数（密度比值：一英寸中像素点除以160）

	private Runnable r = new Runnable() {
		@Override
		public void run() {
			// 又回到了主线程
			// showOrHiddenController();
			controllerView.setVisibility(View.GONE);
		}
	};

	private MyVideoBroadcastReceiver receiver = null;

	// 设置定时器
	private Timer timer = null;
	private final static int WHAT = 0;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case WHAT:
				if (mediaPlayer != null) {
					try {
						int currentPlayer = mediaPlayer.getCurrentPosition();
						if (currentPlayer > 0) {
							mediaPlayer.getCurrentPosition();
							textView_playTime.setText(formatTime(currentPlayer));

							// 让seekBar也跟随改变
							int progress = (int) ((currentPlayer / (float) mediaPlayer.getDuration()) * 100);

							seekBar.setProgress(progress);
						} else {
							textView_playTime.setText("00:00");
							seekBar.setProgress(0);
						}
					} catch (Exception e) {
					}
				}
				break;
			}
		};
	};

	// 自动隐藏自定义播放器控制条的时间
	private static final int HIDDEN_TIME = 3000;

	private String formatTime(long time) {
		SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
		return formatter.format(new Date(time));
	}

	private void initController() {

		// controllerView =
		// getLayoutInflater().inflate(R.layout.activity_register_bind, null);
		controllerView = findViewById(R.id.control_register_video);

		// 初始化popopWindow

		// popupWindow = new PopupWindow(controllerView,
		// LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);

		imageView_play = (ImageView) controllerView.findViewById(R.id.imageView_play);
		imageView_fullscreen = (ImageView) controllerView.findViewById(R.id.imageView_fullscreen);

		seekBar = (SeekBar) controllerView.findViewById(R.id.seekbar);

		textView_playTime = (TextView) controllerView.findViewById(R.id.textView_playtime);
		textView_duration = (TextView) controllerView.findViewById(R.id.textView_totaltime);

		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			// 表示手指拖动seekbar完毕，手指离开屏幕会触发以下方法
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// 让计时器延时执行
				handler.postDelayed(r, HIDDEN_TIME);
			}

			// 在手指正在拖动seekBar，而手指未离开屏幕触发的方法
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// 让计时器取消计时
				handler.removeCallbacks(r);
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (fromUser) {
					try {
						int playtime = progress * mediaPlayer.getDuration() / 100;
						mediaPlayer.seekTo(playtime);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
		});

		// 点击播放的时候,判断是播放还是暂停
		imageView_play.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					if (mediaPlayer.isPlaying()) {
						mediaPlayer.pause();
						imageView_play.setImageResource(R.drawable.video_btn_down);
					} else {
						mediaPlayer.start();
						imageView_play.setImageResource(R.drawable.video_btn_on);

					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		// 实现全屏和退出全屏(内容物横竖屏,不是屏幕的横竖屏)
		imageView_fullscreen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
					findViewById(R.id.title_layout).setVisibility(View.VISIBLE);
					findViewById(R.id.regist_countent_layout).setVisibility(View.VISIBLE);
					imageView_fullscreen.setImageResource(R.drawable.video_full_screen);

					// 重新设置surfaceView的高度和宽度
					surfaceView.getLayoutParams().width = LayoutParams.MATCH_PARENT;
					surfaceView.getLayoutParams().height = (int) (200 * densityRatio);
				} else if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
					findViewById(R.id.title_layout).setVisibility(View.GONE);
					findViewById(R.id.regist_countent_layout).setVisibility(View.GONE);
					imageView_fullscreen.setImageResource(R.drawable.video_inner_screen);
					surfaceView.getLayoutParams().width = getWindowManager().getDefaultDisplay().getWidth();
					surfaceView.getLayoutParams().height = getWindowManager().getDefaultDisplay().getHeight();
				}
				surfaceView.setLayoutParams(surfaceView.getLayoutParams());
			}
		});
	}

	private void showOrHiddenController() {
		if (controllerView.getVisibility() == View.VISIBLE) {
			controllerView.setVisibility(View.GONE);
		} else {
			controllerView.setVisibility(View.VISIBLE);
			// 延时执行
			handler.removeCallbacks(r);
			handler.postDelayed(r, HIDDEN_TIME);
		}
	}

	private void initMediaPlayer() {
		if (mediaPlayer == null) {
			// 1,创建MediaPlay对象
			mediaPlayer = new MediaPlayer();
			mediaPlayer.reset();
			Uri uri = Uri.parse("http://haoup.net/Public/Wap/jwplayer/haoup.mp4");
			try {
				mediaPlayer.setDataSource(mContext, uri);
				mediaPlayer.prepareAsync();
				mediaPlayer.setLooping(false);
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				try {
					mediaPlayer.setDisplay(surfaceHolder);
				} catch (Exception e) {
				}
				mediaPlayer.start();
				// 表示准备完成，设置总的时长，使用时间格式化工具
				findViewById(R.id.activity_register_video_loding).setVisibility(View.GONE);
				// String duration = mediaPlayer.getDuration() ;
				textView_duration.setText(formatTime(mediaPlayer.getDuration()));
				// 初始化定时器
				timer = new Timer();
				timer.schedule(new TimerTask() {

					@Override
					public void run() {
						handler.sendEmptyMessage(WHAT);
					}
				}, 0, 1000);
				handler.postAtTime(r, HIDDEN_TIME);
			}
		});

		mediaPlayer.setOnErrorListener(new OnErrorListener() {

			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				mp.reset();
				return false;
			}
		});

		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				Intent intent = new Intent();
				intent.setAction("com.amy.day43_03_SurfaceViewMediaPlayer");
				sendBroadcast(intent);
			}
		});
	}

	private void initView() {
		densityRatio = getResources().getDisplayMetrics().density; // 表示获取真正的密度
		surfaceView = (SurfaceView) findViewById(R.id.activity_register_video);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(new SurfaceHolder.Callback() {

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				if (mediaPlayer != null) {
					try {
						mediaPlayer.stop();
						mediaPlayer.release();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				// if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
				// mediaPlayer.setDisplay(surfaceHolder);
				// }
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

			}
		});

		// 设置屏幕的触摸监听
		surfaceView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// 表示在点击的瞬间就显示控制条
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					showOrHiddenController();
					break;
				}
				return true;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	class MyVideoBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("com.amy.day43_03_SurfaceViewMediaPlayer")) {
				imageView_play.setImageResource(R.drawable.video_btn_down);
			}
		}

	}

}
