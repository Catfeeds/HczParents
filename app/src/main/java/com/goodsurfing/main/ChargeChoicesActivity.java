package com.goodsurfing.main;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.goodsurfing.adpter.ChargeChoicesAdapter;
import com.goodsurfing.app.R;
import com.goodsurfing.base.BaseActivity;
import com.goodsurfing.beans.ChargeIDBean;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.server.ChargeChoicesServer;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResponder;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResult;
import com.goodsurfing.utils.ActivityUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class ChargeChoicesActivity extends BaseActivity {

	private final static String TAG = "ChargeChoicesActivity";

	protected static final int REFRESH = 100;
	protected static final int SEND = 101;

	@ViewInject(R.id.tv_title)
	private TextView title;

	@ViewInject(R.id.tv_title_right)
	private TextView right;

	@ViewInject(R.id.activity_charge_choices_lv)
	private ListView mPullListView;
	@ViewInject(R.id.title_layout)
	private View title_layout;
	private ChargeChoicesAdapter Adapter;
	public List<ChargeIDBean> listAdapter = new ArrayList<ChargeIDBean>();
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFRESH:

				break;

			case SEND:
				getChoicesInfo();
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_charge_choices);
		ViewUtils.inject(this);
		init();
	}

	private void init() {

		title.setText("套餐服务选择");
		right.setVisibility(View.INVISIBLE);
		Adapter = new ChargeChoicesAdapter(this, R.layout.item_choices_cell, listAdapter);
		mPullListView.setAdapter(Adapter);
		mPullListView.setDivider(null);
		mPullListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				Intent intent = new Intent(ChargeChoicesActivity.this, ChargeListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("packgeId", listAdapter.get(position).getId());
				bundle.putString("CHARGE_TYPE", listAdapter.get(position).getName());
				bundle.putString("CHARGE_SUM", listAdapter.get(position).getPrice() + "元");
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (Constants.isNetWork)
			mHandler.sendEmptyMessageDelayed(SEND, 500);
		else
			Toast.makeText(this, "您的网络已断开，请检查网络", Toast.LENGTH_SHORT).show();
	}

	@OnClick(R.id.iv_title_left)
	public void onHeadBackClick(View view) {
		onBackPressed();
	}

	private void getChoicesInfo() {

		String url = null;
		url = Constants.SERVER_URL + "?" + "requesttype=10";
		ActivityUtil.showPopWindow4Tips(this, title_layout, false, false, "正在加载...", -1);
		new ChargeChoicesServer(new DataServiceResponder() {

			@Override
			public void onResult(DataServiceResult result) {
				listAdapter.clear();
				if (null != result && "0".equals(result.code)) {
					ActivityUtil.dismissPopWindow();
					listAdapter.addAll((List<ChargeIDBean>) result.result);
					Adapter.notifyDataSetChanged();
				} else {
					ActivityUtil.showPopWindow4Tips(ChargeChoicesActivity.this, title_layout, false, result.extra + "");
				}
			}

			@Override
			public void onFailure() {
				ActivityUtil.dismissPopWindow();
			}
		}, url, this).execute();
	}

}
