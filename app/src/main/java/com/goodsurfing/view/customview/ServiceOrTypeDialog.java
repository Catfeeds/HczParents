package com.goodsurfing.view.customview;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.goodsurfing.app.R;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.view.customview.ZjWheelView.OnWheelViewListener;
import com.goodsurfing.view.customview.city.AreasWheel;
import com.goodsurfing.view.customview.city.NiftyDialogBuilder;
import com.goodsurfing.view.customview.city.anim.Effectstype;

public class ServiceOrTypeDialog extends NiftyDialogBuilder implements
		android.view.View.OnClickListener {

	private Context context;
	private LinearLayout rlCustomLayout;
	private ZjWheelView areasWheel;
	private OnWheelViewListener saveServiceLister;
	private static ServiceOrTypeDialog instance;
	private static int mOrientation = 1;

	public interface OnSaveServiceLister {
		abstract void onSaveService(String service);
	}

	public ServiceOrTypeDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
		initDialog();
	}

	public ServiceOrTypeDialog(Context context) {
		super(context);
		this.context = context;
		initDialog();
	}

	public static ServiceOrTypeDialog getInstance(Context context) {
		int ort = context.getResources().getConfiguration().orientation;
		if (mOrientation != ort) {
			mOrientation = ort;
			instance = null;
		}

		if (instance == null || ((Activity) context).isFinishing()) {
			synchronized (ServiceOrTypeDialog.class) {
				if (instance == null) {
					instance = new ServiceOrTypeDialog(context,
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
		rlCustomLayout = (LinearLayout) LayoutInflater.from(context).inflate(
				R.layout.layout_service_or_type_dialog, null);
		areasWheel = (ZjWheelView) rlCustomLayout
				.findViewById(R.id.aw_service_or_type_wheel);
//		setDialogProperties();
	}

	public void setDialogProperties(String title,List<String> list) {
		int width = ActivityUtil.getScreenPixel(context).widthPixels;
		// * 3 / 4;
		this.withDialogWindows(width, LayoutParams.WRAP_CONTENT)
				.withTitle(title).withTitleColor("#000000")
				.withNextText("确定")
				.withNextTextColor("#007AFF").setNextLayoutClick(this)
				.withMessageMiss(View.GONE).withEffect(Effectstype.SlideBottom)
				// 动画
				.withDuration(200).setDialogClick(this)
				.setCustomView(rlCustomLayout, context);
		areasWheel.setOffset(1);
		areasWheel.setItems(list);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fl_dialog_title_previous:
			dismiss();
			break;
		case R.id.fl_dialog_title_next:
			if (null != saveServiceLister) {
				saveServiceLister.onSelected(areasWheel.selectedIndex,areasWheel.getSeletedItem());
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
	public void setOnSaveServiceLister(OnWheelViewListener saveServiceLister) {
		this.saveServiceLister = saveServiceLister;
	}

	@Override
	public void dismiss() {
		super.dismiss();
		instance = null;
	}
}
