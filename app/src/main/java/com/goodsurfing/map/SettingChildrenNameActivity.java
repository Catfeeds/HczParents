package com.goodsurfing.map;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.goodsurfing.app.R;
import com.goodsurfing.base.BaseActivity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class SettingChildrenNameActivity extends BaseActivity {

	protected static final int REFRESH = 100;

	private static final int request_Code = 1;

	@ViewInject(R.id.tv_title)
	private TextView title;

	@ViewInject(R.id.tv_title_right)
	private TextView right;

	@ViewInject(R.id.activity_setting_children_et_name)
	private EditText nameEt;

	@ViewInject(R.id.activity_setting_children_delete_name)
	private ImageView deleteIv;

	private String name;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_children_name);
		ViewUtils.inject(this);
		init();
	}

	private void init() {
		title.setText("修改昵称");
		right.setText("保存");
		right.setEnabled(false);
		name=getIntent().getExtras().getString("name");
		nameEt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence chars, int arg1, int arg2, int arg3) {
				if (chars.length() > 0) {
					deleteIv.setVisibility(View.VISIBLE);
					right.setEnabled(true);
				} else {
					deleteIv.setVisibility(View.GONE);
					right.setEnabled(false);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});
		nameEt.setText(name);
		nameEt.setSelection(name.length());
	}

	@OnClick(R.id.tv_title_right)
	private void onClickSaveName(View view) {
		name = nameEt.getText().toString();
		Intent intent = getIntent();
		intent.putExtra("name", name);
		setResult(RESULT_OK, intent);
		onBackPressed();
	}

	@OnClick(R.id.activity_setting_children_delete_name)
	private void onClickDeleteName(View view) {
		nameEt.setText("");
	}
	@OnClick(R.id.iv_title_left)
	public void onHeadBackClick(View view) {
		onBackPressed();
	}

}
