package com.goodsurfing.view.customview.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.goodsurfing.app.R;
import com.goodsurfing.view.customview.wheelview.CityWheelAdapter;
import com.goodsurfing.view.customview.wheelview.OnWheelChangedListener;
import com.goodsurfing.view.customview.wheelview.ProvinceWheelAdapter;
import com.goodsurfing.view.customview.wheelview.ServiceWheelView;
import com.goodsurfing.view.customview.wheelview.WheelView;

/**
 * 类描述: 地址选择器的自定义布局 项目名称: DateSelector 类名称: AreasWheel 创建人: xhl 创建时间: 2015-2-5
 * 上午10:11:53 版本: v1.0
 */
public class WebAreasWheel extends LinearLayout {
	private ServiceWheelView wv_service;
	public int screenheight;
	private Context context;
	private CityWheelAdapter cityWheelAdapter;


	@SuppressLint({ "NewApi", "Recycle" })
	public WebAreasWheel(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context = context;
		initView();
	}

	@SuppressLint("Recycle")
	public WebAreasWheel(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initView();
	}

	public WebAreasWheel(Context context) {
		super(context);
		this.context = context;
		initView();
	}

	private void initView() {
		LayoutInflater.from(context).inflate(
				R.layout.layout_province_service_selector, this, true);
		wv_service = (ServiceWheelView) findViewById(R.id.wv_service);

//		if (isSelect) {
//			cityWheelAdapter = new CityWheelAdapter(context,
//					R.array.beijin_province_item_select);
//		} else {
			cityWheelAdapter = new CityWheelAdapter(context,
					R.array.service_item);
//		}
//		wv_service.setAdapter(cityWheelAdapter);
		wv_service.setCyclic(false);
		wv_service.setVisibleItems(3);
		wv_service.setAdapter(cityWheelAdapter);
//		provinceChangedListener = new OnWheelChangedListener() {
//			@Override
//			public void onChanged(WheelView wheel, int oldValue, int newValue) {
//				if (isSelect) {
//					cityWheelAdapter.setCityList(ARRAY_SERVICE[0]);
//				} else {
//					cityWheelAdapter.setCityList(ARRAY_CITY[newValue]);
//				}
//				wv_service.setAdapter(cityWheelAdapter);
//				wv_service.setCurrentItem(0);
//			}
//		};
	}

	public String getServiceName() {
		return wv_service.getCurrentItemValue();
	}
	

}
