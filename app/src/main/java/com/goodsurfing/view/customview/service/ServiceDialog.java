package com.goodsurfing.view.customview.service;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

import com.goodsurfing.app.R;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.view.customview.city.AreasWheel;
import com.goodsurfing.view.customview.city.NiftyDialogBuilder;
import com.goodsurfing.view.customview.city.anim.Effectstype;

public class ServiceDialog extends NiftyDialogBuilder implements
		android.view.View.OnClickListener {

	private Context context;
	private RelativeLayout rlCustomLayout;
	private ServiceAreasWheel areasWheel;
	private OnSaveServiceLister saveServiceLister;
	private static ServiceDialog instance;
	private static int mOrientation = 1;

	public interface OnSaveServiceLister {
		abstract void onSaveService(String service);
	}

	public ServiceDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
		initDialog();
	}

	public ServiceDialog(Context context) {
		super(context);
		this.context = context;
		initDialog();
	}

	public static ServiceDialog getInstance(Context context) {
		int ort = context.getResources().getConfiguration().orientation;
		if (mOrientation != ort) {
			mOrientation = ort;
			instance = null;
		}

		if (instance == null || ((Activity) context).isFinishing()) {
			synchronized (ServiceDialog.class) {
				if (instance == null) {
					instance = new ServiceDialog(context,
							R.style.dialog_untran);
				}
			}
		}
		return instance;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setGravity(Gravity.BOTTOM);
		// getWindow().setBackgroundDrawableResource(R.drawable.edit_dialog_coner);
	}

	private void initDialog() {
		rlCustomLayout = (RelativeLayout) LayoutInflater.from(context).inflate(
				R.layout.layout_service_dialog, null);
		areasWheel = (ServiceAreasWheel) rlCustomLayout
				.findViewById(R.id.aw_service_wheel);
		setDialogProperties();
	}

	private void setDialogProperties() {
		int width = ActivityUtil.getScreenPixel(context).widthPixels;
		// * 3 / 4;
		this.withDialogWindows(width, LayoutParams.WRAP_CONTENT)
				.withTitle("运营商").withTitleColor("#000000")
				.withNextText("确定")
				.withNextTextColor("#007AFF").setNextLayoutClick(this)
				.withMessageMiss(View.GONE).withEffect(Effectstype.SlideBottom)
				// 动画
				.withDuration(200).setDialogClick(this)
				.setCustomView(rlCustomLayout, context);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fl_dialog_title_previous:
			dismiss();
			break;
		case R.id.fl_dialog_title_next:
			if (null != saveServiceLister) {
				saveServiceLister.onSaveService(areasWheel.getServiceName());
			}
			dismiss();
			break;
		}
	}

	/**
	 * 设置点击保存的监听
	 * 
	 * @param saveLocationLister
	 */
	public void setOnSaveServiceLister(OnSaveServiceLister saveServiceLister) {
		this.saveServiceLister = saveServiceLister;
	}

	@Override
	public void dismiss() {
		super.dismiss();
		instance = null;
	}
}
