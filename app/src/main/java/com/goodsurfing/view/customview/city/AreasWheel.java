package com.goodsurfing.view.customview.city;

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
import com.goodsurfing.view.customview.wheelview.WheelView;

/**
 * 类描述: 地址选择器的自定义布局 项目名称: DateSelector 类名称: AreasWheel 创建人: xhl 创建时间: 2015-2-5
 * 上午10:11:53 版本: v1.0
 */
public class AreasWheel extends LinearLayout {
	private WheelView wv_province;
	private WheelView wv_city;
	public int screenheight;
	private Context context;
	private OnWheelChangedListener provinceChangedListener;
	private CityWheelAdapter cityWheelAdapter;
	private ProvinceWheelAdapter provinceWheelAdapter;

	private boolean isSelect = false;

	/**
	 * 城市列表
	 */
	private final int[] ARRAY_CITY = new int[] { R.array.beijin_province_item,
			R.array.heibei_province_item, R.array.shandong_province_item,
			R.array.shanghai_province_item, R.array.guangdong_province_item,
			R.array.anhui_province_item, R.array.fujian_province_item,
			R.array.gansu_province_item, R.array.guangxi_province_item,
			R.array.guizhou_province_item, R.array.hainan_province_item,
			R.array.henan_province_item, R.array.heilongjiang_province_item,
			R.array.hubei_province_item, R.array.hunan_province_item,
			R.array.jilin_province_item, R.array.jiangsu_province_item,
			R.array.jiangxi_province_item, R.array.liaoning_province_item,
			R.array.neimenggu_province_item, R.array.ningxia_province_item,
			R.array.qinghai_province_item, R.array.shanxi1_province_item,
			R.array.shanxi2_province_item, R.array.sichuan_province_item,
			R.array.tianjin_province_item, R.array.xizang_province_item,
			R.array.xinjiang_province_item, R.array.yunnan_province_item,
			R.array.zhejiang_province_item, R.array.chongqing_province_item,
			R.array.taiwan_province_item, R.array.hongkong_province_item,
			R.array.aomen_province_item };

	/**
	 * 城市列表
	 */
	private final int[] ARRAY_CITY_SELECT = new int[] {
			R.array.beijin_province_item_select,
			R.array.heibei_province_item_select,
			R.array.shandong_province_item_select,
			R.array.shanghai_province_item_select,
			R.array.guangdong_province_item_select,
			R.array.anhui_province_item_select,
			R.array.fujian_province_item_select,
			R.array.gansu_province_item_select,
			R.array.guangxi_province_item_select,
			R.array.guizhou_province_item_select,
			R.array.hainan_province_item_select,
			R.array.henan_province_item_select,
			R.array.heilongjiang_province_item_select,
			R.array.hubei_province_item_select,
			R.array.hunan_province_item_select,
			R.array.jilin_province_item_select,
			R.array.jiangsu_province_item_select,
			R.array.jiangxi_province_item_select,
			R.array.liaoning_province_item_select,
			R.array.neimenggu_province_item_select,
			R.array.ningxia_province_item_select,
			R.array.qinghai_province_item_select,
			R.array.shanxi1_province_item_select,
			R.array.shanxi2_province_item_select,
			R.array.sichuan_province_item_select,
			R.array.tianjin_province_item_select,
			R.array.xizang_province_item_select,
			R.array.xinjiang_province_item_select,
			R.array.yunnan_province_item_select,
			R.array.zhejiang_province_item_select,
			R.array.chongqing_province_item_select,
			R.array.taiwan_province_item_select,
			R.array.hongkong_province_item_select,
			R.array.aomen_province_item_select };

	@SuppressLint({ "NewApi", "Recycle" })
	public AreasWheel(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context = context;
		TypedArray arr = context.obtainStyledAttributes(attrs,
				R.styleable.areasWheel);
		isSelect = arr.getBoolean(0, false);
		initView();
	}

	@SuppressLint("Recycle")
	public AreasWheel(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		TypedArray arr = context.obtainStyledAttributes(attrs,
				R.styleable.areasWheel);
		isSelect = arr.getBoolean(0, false);
		initView();
	}

	public AreasWheel(Context context) {
		super(context);
		this.context = context;
		initView();
	}

	private void initView() {
		LayoutInflater.from(context).inflate(
				R.layout.layout_province_city_selector, this, true);
		wv_province = (WheelView) findViewById(R.id.wv_province);
		wv_city = (WheelView) findViewById(R.id.wv_city);

		provinceWheelAdapter = new ProvinceWheelAdapter(context);
		wv_province.setAdapter(provinceWheelAdapter);
		wv_province.setCyclic(false);
		wv_province.setVisibleItems(5);
		wv_province.setCurrentItem(0);

		if (isSelect) {
			cityWheelAdapter = new CityWheelAdapter(context,
					R.array.beijin_province_item_select);
		} else {
			cityWheelAdapter = new CityWheelAdapter(context,
					R.array.beijin_province_item);
		}
		wv_city.setAdapter(cityWheelAdapter);
		wv_city.setCyclic(false);
		wv_city.setVisibleItems(5);

		provinceChangedListener = new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (isSelect) {
					cityWheelAdapter.setCityList(ARRAY_CITY_SELECT[newValue]);
				} else {
					cityWheelAdapter.setCityList(ARRAY_CITY[newValue]);
				}
				wv_city.setAdapter(cityWheelAdapter);
				wv_city.setCurrentItem(0);
			}
		};
		wv_province.addChangingListener(provinceChangedListener);
	}

	/**
	 * 获取省市字符串
	 * 
	 * @return
	 */
	public String getArea() {
		return wv_province.getCurrentItemValue() + " "
				+ wv_city.getCurrentItemValue();
	}

	public String getProvince() {
		return wv_province.getCurrentItemValue();
	}

	public String getCity() {
		return wv_city.getCurrentItemValue();
	}

	/**
	 * 获取省份的Id
	 * 
	 * @return
	 */
	public String getProvinceId() {
		return wv_province.getCurrentItemId();
	}

	/**
	 * 获取城市的Id
	 * 
	 * @return
	 */
	public String getCityId() {
		return wv_city.getCurrentItemId();
	}

}
