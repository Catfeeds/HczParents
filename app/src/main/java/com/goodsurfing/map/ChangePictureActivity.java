package com.goodsurfing.map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;

import com.goodsurfing.adpter.PhotoAdapter;
import com.goodsurfing.app.R;
import com.goodsurfing.base.BasePhotoActivity;
import com.goodsurfing.constants.Constants;

/**
 * 设置 头像
 * 
 * @author Administrator
 * 
 */
public class ChangePictureActivity extends BasePhotoActivity implements OnClickListener, OnItemClickListener {
	private Button ibCancel;
	private PhotoAdapter adapter;
	private GridView photoGridView;
	private View view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_photo);
		ibCancel = (Button) findViewById(R.id.ib_change_photo_cancel);
		photoGridView = (GridView) findViewById(R.id.photo_gv);
		view = findViewById(R.id.none);
		adapter = new PhotoAdapter(this, Constants.selectIds);
		ibCancel.setOnClickListener(this);
		photoGridView.setAdapter(adapter);
		photoGridView.setOnItemClickListener(this);
		view.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.none:
		case R.id.ib_change_photo_cancel:
			onBackPressed();
			break;
		}

	}

	public static void luncherPhoto(Context context, int request_Code) {
		((Activity) context).startActivityForResult(new Intent(context, ChangePictureActivity.class), request_Code);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
		overridePendingTransition(R.anim.base_slide_bottom_in, R.anim.base_slide_remain);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, R.anim.base_slide_bottom_out);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent intent = getIntent();
		intent.putExtra("id", arg2 + 1);
		setResult(RESULT_OK, intent);
		onBackPressed();
	}
}
