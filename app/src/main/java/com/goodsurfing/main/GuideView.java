package com.goodsurfing.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.goodsurfing.adpter.GuidePagerAdapter;
import com.goodsurfing.app.R;
import com.goodsurfing.base.BaseActivity;
import com.goodsurfing.utils.ActivityUtil;
import com.lidroid.xutils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: GuideView
 * @Description: 向导 界面
 * @version: 1.0
 * @author: wangbin
 * @Create: 2015-05-07
 */
public class GuideView extends BaseActivity implements OnPageChangeListener {

	private final static String TAG = "GuideView->";
	private ViewPager vp;
	private GuidePagerAdapter vpAdapter;
	private List<View> views;

	// 底部小点图片
	private int oldPosition = 0;// 记录上一次点的位置
	private ArrayList<View> dots = new ArrayList<View>();

	private LayoutInflater inflater;
	private LinearLayout dot_point;
	private ImageView leftView;

	private static boolean help = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guide);
		ViewUtils.inject(this);
		// 初始化页面
		initViews();
	}

	private void initViews() {
		try {
			Intent intent = getIntent();
			if (intent != null) {
				Bundle bundle = intent.getExtras();
				if (bundle != null) {
					String type = bundle.getString("TYPE_GUIDE");
					if ("1".equals(type)){
						help = true;
					}
				}
			} else
				help = false;
			inflater = LayoutInflater.from(this);
			leftView = (ImageView) findViewById(R.id.iv_title_left);
			leftView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					onBackPressed();
				}
			});
			views = new ArrayList<View>();
			// 初始化引导图片列表
			views.add(inflater.inflate(R.layout.guide_one, null));
			views.add(inflater.inflate(R.layout.guide_two, null));
			views.add(inflater.inflate(R.layout.guide_three, null));
			views.add(inflater.inflate(R.layout.guide_four, null));
			views.add(inflater.inflate(R.layout.guide_filve, null));
			views.add(inflater.inflate(R.layout.guide_six, null));

			views.get(5).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (help == true) {
						onBackPressed();
					} else {
						ActivityUtil.goMainActivity(GuideView.this);
						vpAdapter.setGuided();
						onBackPressed();
					}
				}
			});
			// 初始化Adapter
			vpAdapter = new GuidePagerAdapter(views, this, help);

			vp = (ViewPager) findViewById(R.id.guide_viewpager_layout);
			vp.setAdapter(vpAdapter);
			// 绑定回调
			vp.setOnPageChangeListener(this);
		} catch (Exception e) {
			System.out.println(e);
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (help) {
			leftView.setVisibility(View.VISIBLE);
			ActivityUtil.sendEvent4UM(GuideView.this, "functionSwitch", "Help", 10);
		} else {
			leftView.setVisibility(View.INVISIBLE);
		}
	}

	// 当滑动状态改变时调用
	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	// 当前页面被滑动时调用
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	// 当新的页面被选中时调用
	@Override
	public void onPageSelected(int position) {
		oldPosition = position;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (help != true)
				moveTaskToBack(true);
			else
				return super.onKeyDown(keyCode, event);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (help != true) {
			System.gc();
			System.runFinalization();
		}
	}

}
