package com.goodsurfing.map;

import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.goodsurfing.app.R;
import com.goodsurfing.base.BaseActivity;
import com.goodsurfing.base.BasePhotoActivity;
import com.goodsurfing.beans.Friend;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.database.dao.FriendDao;
import com.goodsurfing.server.AddUserServer;
import com.goodsurfing.server.DoGetCodeServer;
import com.goodsurfing.server.PutDataServer;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResponder;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResult;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.EventHandler;
import com.goodsurfing.utils.LogUtil;
import com.goodsurfing.view.RoundImageView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class SettingChildrenActivity extends BasePhotoActivity {

	protected static final int REFRESH = 100;

	private static final int request_Code = 1;

	@ViewInject(R.id.tv_title)
	private TextView title;

	@ViewInject(R.id.tv_title_right)
	private TextView right;

	@ViewInject(R.id.activity_setting_children_et_name)
	private EditText nameEt;

	@ViewInject(R.id.activity_setting_children_save_btn)
	private TextView saveTv;

	@ViewInject(R.id.activity_setting_children_head)
	private RoundImageView headIv;

	@ViewInject(R.id.activity_setting_children_delete_phone)
	private ImageView deleteIv;
	@ViewInject(R.id.title_layout)
	private View title_layout;
	private int id;

	private String phone;

	private FriendDao friendDao;

	private Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_children);
		ViewUtils.inject(this);
		init();
	}

	private void init() {
		title.setText("孩子手机设置");
		right.setVisibility(View.GONE);
		headIv.setmBorderOutsideColor(0xFFcccccc);
		headIv.setmBorderThickness(15);
		phone = getIntent().getExtras().getString("phone");
		friendDao = new FriendDao(this);
		nameEt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence chars, int arg1, int arg2,
					int arg3) {
				if (chars.length() > 0) {
					deleteIv.setVisibility(View.VISIBLE);
					if (id != 0) {
						saveTv.setClickable(true);
						saveTv.setBackgroundResource(R.drawable.view_bottom_button_bg);
					}
				} else {
					deleteIv.setVisibility(View.GONE);
					saveTv.setClickable(false);
					saveTv.setBackgroundResource(R.drawable.view_btn_gray__bg);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});
		saveTv.setClickable(false);
		saveTv.setBackgroundResource(R.drawable.view_btn_gray__bg);
	}

	@OnClick(R.id.activity_setting_children_save_btn)
	private void onClickSave(View view) {
		// 保存
		String name = nameEt.getText().toString();
		if (name.equals("") || null == name) {
			ActivityUtil.showPopWindow4Tips(this,title_layout, false, "您还未设置昵称");
			return;
		}
		if (id == 0) {
			ActivityUtil.showPopWindow4Tips(this,title_layout, false, "您还未设置头像");
			return;
		}
		if (!Constants.isNetWork) {
			ActivityUtil.showPopWindow4Tips(this,title_layout, false, "您的网络已断开，请检查网络");
			return;
		}
		String url = Constants.SERVER_URL + "?" + "token=" + Constants.tokenID
				+ "&userid=" + Constants.userId + "&committype=17" + "&img="
				+ id + "&name=" + name + "&mobile=" + phone;
		// committype=17& userid=24&
		// token=token145208220824&img=1&name=nihao&mobile=18874839234
		new AddUserServer(new DataServiceResponder() {

			@Override
			public void onResult(DataServiceResult result) {
				try {
					if ("0".equals(result.code)) {
						ActivityUtil.showPopWindow4Tips(SettingChildrenActivity.this,title_layout, true, "添加成功");
						onBackPressed();
					}else {
						ActivityUtil.showPopWindow4Tips(SettingChildrenActivity.this, title_layout, false, result.extra+"");
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

	@OnClick(R.id.activity_setting_children_delete_phone)
	private void onClickDeleteName(View view) {
		nameEt.setText("");
	}

	@OnClick(R.id.activity_setting_children_head)
	private void onClickUpHead(View view) {
		ChangePictureActivity.luncherPhoto(this, request_Code);
	}

	@OnClick(R.id.iv_title_left)
	public void onHeadBackClick(View view) {
		back();
	}

	private void back() {
		dialog = ActivityUtil.getDialog(this, "是否添加绑定该手机",
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						onClickSave(arg0);
						dialog.dismiss();
					}
				}, new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						onBackPressed();
						dialog.dismiss();
					}
				});
		dialog.show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			back();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case request_Code:
			if (resultCode == RESULT_OK) {
				headIv.setBackground(null);
				id = data.getExtras().getInt("id");
				headIv.setBackgroundResource(Constants.showIds[id]);
				String name = nameEt.getText().toString();
				if (name != null && !name.equals("") && id != 0) {
					saveTv.setClickable(true);
					saveTv.setBackgroundResource(R.drawable.view_bottom_button_bg);
				}
			}
			break;
		}
	}

}
