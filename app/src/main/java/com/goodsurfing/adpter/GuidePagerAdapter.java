package com.goodsurfing.adpter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.goodsurfing.constants.Constants;
import com.goodsurfing.utils.CommonUtil;

import java.util.List;


public class GuidePagerAdapter extends PagerAdapter {

	  // 界面列表
	private List<View> views;
	private Activity activity;
	private boolean ishelp;
	private static final String SHAREDPREFERENCES_NAME = "first_pref";

	public GuidePagerAdapter(List<View> views, Activity activity,boolean help) {
		this.views = views;
		this.activity = activity;
		ishelp = help;
	}

	// 销毁arg1位置的界面
	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager) arg0).removeView(views.get(arg1));
	}

	@Override
	public void finishUpdate(View arg0) {
	}

	// 获得当前界面数
	@Override
	public int getCount() {
		if (views != null) {
			return views.size();
		}
		return 0;
	}

	// 初始化arg1位置的界面
	@Override
	public Object instantiateItem(View arg0, int arg1) {
		((ViewPager) arg0).addView(views.get(arg1), 0);
		return views.get(arg1);
	}


	/**
	 * 
	 * method desc：设置已经引导过了，下次启动不用再次引导
	 */
	public void setGuided() {
		SharedPreferences preferences = activity.getSharedPreferences(
				Constants.SP_NAME, activity.MODE_PRIVATE);
		Editor editor = preferences.edit();
		// 存入数据
		editor.putInt("versionCode", CommonUtil.getAppVersionCode(activity));
		// 提交修改
		editor.commit();
	}

   // 判断是否由对象生成界面
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return (arg0 == arg1);
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
	}

}
