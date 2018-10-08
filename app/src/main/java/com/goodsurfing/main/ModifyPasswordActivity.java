package com.goodsurfing.main;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.goodsurfing.app.R;
import com.goodsurfing.base.BaseActivity;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.server.PutDataServer;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResponder;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResult;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.CommonUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class ModifyPasswordActivity extends BaseActivity {

	private final static String TAG = "FindPasswordActivity";
	protected static final int REFRESH = 100;

	private Context context;

	@ViewInject(R.id.activity_find_password_item_current_password)
	private EditText currentEditText;

	@ViewInject(R.id.activity_find_password_item_new_password)
	private EditText NewPwdEditText;

	@ViewInject(R.id.activity_find_passwrod_item_et_password)
	private EditText PwdEditText;

	@ViewInject(R.id.tv_title)
	private TextView title;

	@ViewInject(R.id.tv_title_right)
	private TextView right;

	@ViewInject(R.id.title_layout)
	private View title_layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_password);
		ViewUtils.inject(this);
		init();
	}

	private void init() {
		context = this;
		title.setText("修改密码");
		right.setVisibility(View.INVISIBLE);

	}

	/**
	 * 请求修改密码
	 */
	private void getModifyPasswordFeedBack() {
		if (!Constants.isNetWork) {
			ActivityUtil.showPopWindow4Tips(this,title_layout, false, "当前网络不可用，请稍后再试...");
			return;
		}
		// 防止重复点击
		if (CommonUtil.isFastDoubleClick())
			return;

		final String account = currentEditText.getText().toString();
		if ("".equals(account)) {
			ActivityUtil.showPopWindow4Tips(this,title_layout, false, "请输入原始密码");
			return;
		}

		String passwords = NewPwdEditText.getText().toString();
		if ("".equals(passwords)) {
			ActivityUtil.showPopWindow4Tips(this,title_layout, false, "请输入密码");
			return;
		} else if (!passwords.matches("[0-9a-zA-z]{1,6}")) {
			ActivityUtil.showPopWindow4Tips(this,title_layout, false, "密码格式不正确，请小于6位的密码或数字");
		}

		String password = PwdEditText.getText().toString();
		if ("".equals(password)) {
			ActivityUtil.showPopWindow4Tips(this,title_layout, false, "请输入密码");
			return;
		} else if (!passwords.equals(password)) {
			ActivityUtil.showPopWindow4Tips(this,title_layout, false, "两次密码输入不一致");
		}

		String mdPassword = null;
		String oldPassword = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			// md.update(password.getBytes());
			mdPassword = getString(md.digest(password.getBytes()));
			oldPassword = getString(md.digest(account.getBytes()));

		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String url = Constants.SERVER_URL + "?" + "token=" + Constants.tokenID + "&passwd=" + mdPassword + "&committype=7&userid=" + Constants.userId + "&oldpasswd=" + oldPassword;

		new PutDataServer(new DataServiceResponder() {

			@Override
			public void onResult(DataServiceResult result) {
				if ("0".equals(result.code)) {
					ActivityUtil.showPopWindow4Tips(ModifyPasswordActivity.this,title_layout, true, "修改成功");
					onBackPressed();
				}else {
					ActivityUtil.showPopWindow4Tips(ModifyPasswordActivity.this,title_layout,false, result.extra+"");
				}
			}

			@Override
			public void onFailure() {

			}
		}, url, context,false).execute();

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

	@OnClick(R.id.activity_find_password_item_rl_login)
	public void loginClick(View view) {
		getModifyPasswordFeedBack();
	}

	@OnClick(R.id.iv_title_left)
	public void onHeadBackClick(View view) {
		onBackPressed();
	}

}
