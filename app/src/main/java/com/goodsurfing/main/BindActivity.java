package com.goodsurfing.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.goodsurfing.app.R;
import com.goodsurfing.base.BaseActivity;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.server.LoginServer;
import com.goodsurfing.server.PutDataServer;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResponder;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResult;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.CommonUtil;
import com.goodsurfing.utils.IDCard;
import com.goodsurfing.utils.SharUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class BindActivity extends BaseActivity {
	private final static String TAG = "BindActivity";
	@ViewInject(R.id.tv_title)
	private TextView title;
	@ViewInject(R.id.tv_title_right)
	private TextView right;
	protected static final int REFRESH = 100;
	protected static final int LOGIN = 101;
	protected static final int SHOW_BIND_OK = 102;
	private Context context;
	@ViewInject(R.id.activity_register_account_et)
	private EditText accountEditText;
	@ViewInject(R.id.activity_register_id_et)
	private EditText idCardEditText;
	@ViewInject(R.id.activity_register_name_et)
	private EditText nameEditText;
	@ViewInject(R.id.activity_register_commit_rl)
	private RelativeLayout signBtnTv;
	@ViewInject(R.id.title_layout)
	private View title_layout;
	private String account = "";
	private String idCard = "";
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFRESH:
				break;
			case LOGIN:
				doLogin();
				break;
			case SHOW_BIND_OK:
				showBindOkDialog(BindActivity.this, mHandler);
				break;
			}
		};
	};
	private String accountName;
	private String mdPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_builder);
		ViewUtils.inject(this);
		init();
	}

	private void init() {
		context = this;
		title.setText("绑定");
		right.setVisibility(View.INVISIBLE);
		accountEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence chars, int arg1, int arg2, int arg3) {
				if (chars.length() > 0) {
					account = chars.toString();
					if (idCard.length() > 0) {
						signBtnTv.setEnabled(true);
						signBtnTv.setBackgroundResource(R.drawable.view_bottom_button_bg);
					}
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
		idCardEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence chars, int arg1, int arg2, int arg3) {
				if (chars.length() > 0) {
					idCard = chars.toString();
					if (account.length() > 0) {
						signBtnTv.setEnabled(true);
						signBtnTv.setBackgroundResource(R.drawable.view_bottom_button_bg);
					}
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
		showDefultPsDialog();
	}

	private void showDefultPsDialog() {
		final Dialog dialog = new Dialog(this, R.style.AlertDialogCustom);
		dialog.setCancelable(false);
		View view = View.inflate(this, R.layout.layout_tips_dialog, null);
		TextView titleView = (TextView) view.findViewById(R.id.layout_title);
		TextView contentView = (TextView) view.findViewById(R.id.layout_content);
		TextView btnView = (TextView) view.findViewById(R.id.layout_btn);
		btnView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		titleView.setText("默认密码是");
		contentView.setText("888888");
		dialog.setContentView(view);
		WindowManager m = this.getWindowManager();
		Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); // 获取对话框当前的参数值
		p.width = (int) (d.getWidth() * 0.65); // 宽度设置为屏幕的0.95
		dialog.getWindow().setAttributes(p);
		dialog.show();
	}

	@OnClick(R.id.activity_register_commit_rl)
	private void onClickSignCode(View view) {
		bindData2Server();
	}

	private void bindData2Server() {
		if (!Constants.isNetWork) {
			ActivityUtil.showPopWindow4Tips(this,title_layout, false, "当前网络不可用，请稍后再试...");
			return;
		}
		// 防止重复点击
		if (CommonUtil.isFastDoubleClick())
			return;
		account = accountEditText.getText().toString();
		if ("".equals(account)) {
			ActivityUtil.showPopWindow4Tips(this,title_layout, false, "请输入宽度账号");
//			EventHandler.showToast(this, "请输入宽度账号");
			return;
		}
		idCard = idCardEditText.getText().toString();
		if ("".equals(idCard)) {
			ActivityUtil.showPopWindow4Tips(this,title_layout, false, "请输入身份证号码");
//			EventHandler.showToast(this, "请输入身份证号码");
			return;
		}
		String idErorrs = IDCard.IDCardValidate(idCard);
		if (!idErorrs.equals("")) {
			ActivityUtil.showPopWindow4Tips(this,title_layout, false, idErorrs);
//			EventHandler.showToast(this, idErorrs);
			return;
		}
		accountName = nameEditText.getText().toString().trim();
		String url = Constants.SERVER_URL + "?" + "committype=21&Account=" + account + "&identitycard=" + idCard + "&name=" + accountName + "&Mobile=" + Constants.userMobile;

		new PutDataServer(new DataServiceResponder() {

			@Override
			public void onResult(DataServiceResult result) {
				if ("0".equals(result.code)) {
					Constants.Account = accountName;
					// TODO
					mHandler.sendEmptyMessage(SHOW_BIND_OK);
					mHandler.sendEmptyMessage(LOGIN);
				}else {
					ActivityUtil.showPopWindow4Tips(BindActivity.this, title_layout, false, result.extra+"");
				}
			}

			@Override
			public void onFailure() {

			}
		}, url, context).execute();
	}

	/**
	 * 注册成功走登录流程
	 */
	protected void doLogin() {
		if (!Constants.isNetWork) {
			ActivityUtil.showPopWindow4Tips(this,title_layout, false, "当前网络不可用，请稍后再试...");
			return;
		}

		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			mdPassword = getString(md.digest("888888".getBytes()));
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		String url = Constants.SERVER_URL + "?" + "user=" + account + "&passwd=" + mdPassword + "&usertype=" + 1 + "&mobile=" + Constants.userMobile + "&usertype=1"+"&deviceID="+ActivityUtil.getDeviceToken(this);

		new LoginServer(new DataServiceResponder() {

			@Override
			public void onResult(DataServiceResult result) {
				if (result == null || result.code == null) {
					return;
				}
				if (result.code.equals("0")) {
					SharUtil.saveMode(context, Constants.mode);
					SharUtil.savePhone(context, Constants.userMobile);
					SharUtil.saveCacheTime(context, Constants.CACHE_TIME, System.currentTimeMillis() + "");
				}else {
					ActivityUtil.showPopWindow4Tips(BindActivity.this, title_layout, false, result.extra+"");
				}
			}

			@Override
			public void onFailure() {
				ActivityUtil.showPopWindow4Tips(BindActivity.this,title_layout, false, "服务器忙，请稍候再试");
			}
		}, url, context).execute();

	}

	private static String getString(byte[] encryption) {
		StringBuffer strBuf = new StringBuffer();
		for (int i = 0; i < encryption.length; i++) {
			if (Integer.toHexString(0xff & encryption[i]).length() == 1) {
				strBuf.append("0").append(Integer.toHexString(0xff & encryption[i]));
			} else {
				strBuf.append(Integer.toHexString(0xff & encryption[i]));
			}
		}

		return strBuf.toString();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@OnClick(R.id.iv_title_left)
	public void onHeadBackClick(View view) {
		onBackPressed();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	public void showBindOkDialog(final Context mycontext, final Handler handler) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mycontext, R.style.CustomDialog);
		builder.setCancelable(false);
		final AlertDialog dialog = builder.show();
		View view = LayoutInflater.from(mycontext).inflate(R.layout.show_bind_dialog_layout, null);
		Window window = dialog.getWindow();
		WindowManager m = ((Activity) mycontext).getWindowManager();
		Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		WindowManager.LayoutParams p = window.getAttributes(); // 获取对话框当前的参数值
		p.height = (int) (d.getHeight() * 0.48); // 高度设置为屏幕的0.6
		p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.65
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
				dialog.dismiss();
				// 登录流程
				ActivityUtil.goMainActivity(BindActivity.this);
				onBackPressed();
			}
		});
	}
}
