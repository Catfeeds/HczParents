package com.goodsurfing.view.customview.city;

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
import com.goodsurfing.view.customview.city.anim.Effectstype;

public class LocationSelectorDialogBuilder extends NiftyDialogBuilder implements
		android.view.View.OnClickListener {

	private Context context;
	private RelativeLayout rlCustomLayout;
	private AreasWheel areasWheel;
	private OnSaveLocationLister saveLocationLister;
	private static LocationSelectorDialogBuilder instance;
	private static int mOrientation = 1;

	public interface OnSaveLocationLister {
		abstract void onSaveLocation(String province, String city,
				String provinceId, String cityId);
	}

	public LocationSelectorDialogBuilder(Context context, int theme) {
		super(context, theme);
		this.context = context;
		initDialog();
	}

	public LocationSelectorDialogBuilder(Context context) {
		super(context);
		this.context = context;
		initDialog();
	}

	public static LocationSelectorDialogBuilder getInstance(Context context) {
		int ort = context.getResources().getConfiguration().orientation;
		if (mOrientation != ort) {
			mOrientation = ort;
			instance = null;
		}

		if (instance == null || ((Activity) context).isFinishing()) {
			synchronized (LocationSelectorDialogBuilder.class) {
				if (instance == null) {
					instance = new LocationSelectorDialogBuilder(context,
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
				R.layout.layout_location_dialog_selector, null);
		areasWheel = (AreasWheel) rlCustomLayout
				.findViewById(R.id.aw_location_selector_wheel);
		setDialogProperties();
	}

	private void setDialogProperties() {
		int width = ActivityUtil.getScreenPixel(context).widthPixels;
		// * 3 / 4;
		this.withDialogWindows(width, LayoutParams.WRAP_CONTENT)
				.withTitle("居住城市").withTitleColor("#000000")
				.withPreviousText("取消").withPreviousTextColor("#007AFF")
				.setPreviousLayoutClick(this).withNextText("保存")
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
			if (null != saveLocationLister) {
				saveLocationLister.onSaveLocation(areasWheel.getProvince(),
						areasWheel.getCity(), areasWheel.getProvinceId(),
						areasWheel.getCityId());
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
	public void setOnSaveLocationLister(OnSaveLocationLister saveLocationLister) {
		this.saveLocationLister = saveLocationLister;
	}

	@Override
	public void dismiss() {
		super.dismiss();
		instance = null;
	}
}
