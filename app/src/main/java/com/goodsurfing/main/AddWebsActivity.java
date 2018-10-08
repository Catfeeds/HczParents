package com.goodsurfing.main;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.goodsurfing.app.R;
import com.goodsurfing.base.BaseActivity;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.server.PutDataServer;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResponder;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResult;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.CommonUtil;
import com.goodsurfing.view.customview.ServiceOrTypeDialog;
import com.goodsurfing.view.customview.ZjWheelView.OnWheelViewListener;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class AddWebsActivity extends BaseActivity {

	private final static String TAG = "AddWebsActivity";

	protected static final int REFRESH = 100;
	protected static final int SEND = 101;

	@ViewInject(R.id.tv_title)
	private TextView title;

	@ViewInject(R.id.tv_title_right)
	private TextView right;

	@ViewInject(R.id.activity_add_webs_tv2)
	private TextView addTextView;

	@ViewInject(R.id.activity_add_webs_tv)
	private TextView typeTextView;

	@ViewInject(R.id.activity_add_webs_et_web)
	private EditText websEditText;

	@ViewInject(R.id.activity_add_webs_et_name)
	private EditText nameEditText;

	@ViewInject(R.id.activity_add_webs_tv_web_defult)
	private TextView webDefultTv;
	@ViewInject(R.id.title_layout)
	private View title_layout;
	private String position = "0";
	private static String type = "1";

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFRESH:

				break;

			case SEND:

				break;
			default:
				// closeDialog();
				break;
			}
		};
	};

	private List<String> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_webs);
		ViewUtils.inject(this);
		init();
	}

	private void init() {

		title.setText("添加网址");
		right.setVisibility(View.INVISIBLE);
		list = new ArrayList<String>();
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if (bundle != null)
			type = bundle.getString("TYPE");
		if ("1".equals(type))
			addTextView.setText("确认添加到白名单");
		else if ("2".equals(type))
			addTextView.setText("确认添加到黑名单");

		if (Constants.typeMap.size() != 0) {
			for (int i = 0; i < Constants.typeMap.size(); i++) {
				list.add(Constants.typeMap.get(String.valueOf(i)));
			}
		}
		websEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
//				if(websEditText.getText().toString().length()==0){
//					websEditText.setText("http://"+arg0);
//				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
//				webDefultTv.setVisibility(View.VISIBLE);
			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});
		websEditText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
//				if (websEditText.getText().toString().length() == 0) {
//					websEditText.setText("http://");
//					websEditText.setSelection("http://".length());
//				}
			}
		});

	}

	private void refreshWebsInfo() {
		if (!Constants.isNetWork) {
			ActivityUtil.showPopWindow4Tips(this,title_layout, false, "当前网络不可用，请稍后再试...");
			return;
		}
		// 防止重复点击
		if (CommonUtil.isFastDoubleClick())
			return;

		String web = websEditText.getText().toString().trim();
		if ("".equals(web)) {
			ActivityUtil.showPopWindow4Tips(this,title_layout, false, "网址不能为空");
//			EventHandler.showToast(AddWebsActivity.this, "网址不能为空");
			return;
		}
//		web = webDefultTv.getText().toString() + web;
		String isurl = "^((https|http|ftp|rtsp|mms)?://)" + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" + "(([0-9]{1,3}\\.){3}[0-9]{1,3}" + "|" + "([0-9a-z_!~*'()-]+\\.)*" + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." + "[a-z]{2,6})" + "(:[0-9]{1,4})?" + "((/?)|" + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$";

		if (!web.matches(isurl)) {
			ActivityUtil.showPopWindow4Tips(this,title_layout, false, "请输入正确的网址");
//			EventHandler.showToast(AddWebsActivity.this, "请输入正确的网址");
			return;
		}

		String name = nameEditText.getText().toString().trim();
		if ("".equals(name)) {
			ActivityUtil.showPopWindow4Tips(this,title_layout, false, "网址名称不能为空");
//			EventHandler.showToast(this, "名称不能为空");
			return;
		}
		String url = Constants.SERVER_URL + "?" + "committype=5&oper=1&type=" + type + "&userid=" + Constants.userId + "&token=" + Constants.tokenID + "&domain=" + web + "&name=" + name + "&statu=1" + "&classid=" + position;

		new PutDataServer(new DataServiceResponder() {

			@Override
			public void onResult(DataServiceResult result) {
				if (null != result && "0".equals(result.code)) {
					Intent intent = getIntent();
					setResult(RESULT_OK, intent);
					onBackPressed();
				}else {
					ActivityUtil.showPopWindow4Tips(AddWebsActivity.this, title_layout, false, result.extra+"");
				}
			}

			@Override
			public void onFailure() {

			}
		}, url, this).execute();
	}

	@OnClick(R.id.activity_add_webs_add_rl)
	public void onAddClicks(View v) {
		refreshWebsInfo();
	}

	@OnClick(R.id.iv_title_left)
	public void onHeadBackClick(View view) {
		onBackPressed();
	}

	@OnClick(R.id.activity_add_webs_rl)
	private void doServiceDialog(View view) {
		ServiceOrTypeDialog locationDialog = ServiceOrTypeDialog.getInstance(this);
		locationDialog.setDialogProperties("网站类型", list);
		locationDialog.setOnSaveServiceLister(new OnWheelViewListener() {

			@Override
			public void onSelected(int selectedIndex, String item) {
				position = String.valueOf(selectedIndex - 1);
				typeTextView.setText(item);
			}

		});
		locationDialog.show();
	}

}
