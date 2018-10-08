package com.goodsurfing.main;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.goodsurfing.adpter.BlackWhiteWebListAdapter.SelectDelegate;
import com.goodsurfing.app.R;
import com.goodsurfing.base.BaseActivity;
import com.goodsurfing.base.BaseFragment;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.main.AddBlackWhiteListActivity.DELEGATE;
import com.goodsurfing.server.PutDataServer;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResponder;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResult;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.CommonUtil;
import com.goodsurfing.view.customview.ServiceOrTypeDialog;
import com.goodsurfing.view.customview.ZjWheelView.OnWheelViewListener;
import com.goodsurfing.view.pullrefresh.ui.PullToRefreshListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class AddWhiteWebsActivity extends BaseFragment implements OnClickListener {

	private final static String TAG = "AddWebsActivity";

	protected static final int REFRESH = 100;
	protected static final int SEND = 101;
	private TextView addTextView;
	private EditText websEditText;
	private EditText nameEditText;
	private TextView webDefultTv;
	private String position = "0";
	private static String type = "1";
	private List<String> list;
	private View rootView;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFRESH:

				break;

			case SEND:
				refreshWebsInfo();
				break;
			}
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = LayoutInflater.from(getActivity()).inflate(R.layout.activity_add_white_webs, null);
			initViews(rootView);
			init();
		}
		ViewGroup group = (ViewGroup) rootView.getParent();
		if (group != null) {
			group.removeView(rootView);
		}
		return rootView;
	}

	private void initViews(View view) {
		websEditText = (EditText) view.findViewById(R.id.activity_add_webs_et_web);
		nameEditText = (EditText) view.findViewById(R.id.activity_add_webs_et_name);
		webDefultTv = (TextView) view.findViewById(R.id.activity_add_webs_tv_web_defult);
		addTextView = (TextView) view.findViewById(R.id.activity_add_webs_tv);
		addTextView.setOnClickListener(this);
	}

	private void init() {
		list = new ArrayList<String>();
		if (Constants.typeMap.size() != 0) {
			for (int i = 0; i < Constants.typeMap.size(); i++) {
				list.add(Constants.typeMap.get(String.valueOf(i)));
			}
		}
		getActivity().registerReceiver(receiver, new IntentFilter("AddWhiteWebsActivity"));
	}
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			refreshWebsInfo();
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		AddBlackWhiteListActivity.index = 1;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(receiver);
	}

	public void refreshWebsInfo() {
		if (!Constants.isNetWork) {
			ActivityUtil.showPopWindow4Tips(getActivity(), AddBlackWhiteListActivity.contentView, false, true, "当前网络不可用，请稍后再试...", 2000);
			return;
		}
		// 防止重复点击
		if (CommonUtil.isFastDoubleClick())
			return;
		String web = websEditText.getText().toString();
		if ("".equals(web)) {
			ActivityUtil.showPopWindow4Tips(getActivity(), AddBlackWhiteListActivity.contentView, false, false, "网址不能为空", 2000);
			return;
		}
		String isurl = "^((https|http|ftp|rtsp|mms)?://)" + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" + "(([0-9]{1,3}\\.){3}[0-9]{1,3}" + "|" + "([0-9a-z_!~*'()-]+\\.)*" + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." + "[a-z]{2,6})" + "(:[0-9]{1,4})?" + "((/?)|" + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$";

		if (!web.matches(isurl)) {
			ActivityUtil.showPopWindow4Tips(getActivity(), AddBlackWhiteListActivity.contentView, false, false, "请输入正确的网址", 2000);
			return;
		}

		String name = nameEditText.getText().toString().trim();
		if ("".equals(name)) {
			ActivityUtil.showPopWindow4Tips(getActivity(), AddBlackWhiteListActivity.contentView, false, false, "网址名称不能为空", 2000);
			return;
		}
		String url = Constants.SERVER_URL + "?" + "committype=5&oper=1&type=" + type + "&userid=" + Constants.userId + "&token=" + Constants.tokenID + "&domain=" + web + "&name=" + name + "&statu=1" + "&classid=" + position;
	
		ActivityUtil.showPopWindow4Tips(getActivity(), AddBlackWhiteListActivity.contentView, false, true, "添加中...", -1);
		new PutDataServer(new DataServiceResponder() {

			@Override
			public void onResult(DataServiceResult result) {
				if (null != result && "0".equals(result.code)) {
//					ActivityUtil.showPopWindow4Tips(getActivity(), AddBlackWhiteListActivity.contentView, true, true, "添加成功", 2000);
					((AddBlackWhiteListActivity)getActivity()).onBackPressed4Add();
				} else {
					ActivityUtil.showPopWindow4Tips(getActivity(), AddBlackWhiteListActivity.contentView, false, true, result.extra + "", 2000);
				}
			}

			@Override
			public void onFailure() {

			}
		}, url, getActivity()).execute();
	}

	private void doServiceDialog() {
		ServiceOrTypeDialog locationDialog = ServiceOrTypeDialog.getInstance(getActivity());
		locationDialog.setDialogProperties("网站类型", list);
		locationDialog.setOnSaveServiceLister(new OnWheelViewListener() {

			@Override
			public void onSelected(int selectedIndex, String item) {
				position = String.valueOf(selectedIndex - 1);
				addTextView.setText(item);
			}

		});
		locationDialog.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.activity_add_webs_tv:
			doServiceDialog();
			break;

		}
	}

}
