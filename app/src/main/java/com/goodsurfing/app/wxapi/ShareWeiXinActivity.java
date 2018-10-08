package com.goodsurfing.app.wxapi;

import java.util.Random;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.goodsurfing.app.R;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.server.PutDataServer;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResponder;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResult;
import com.goodsurfing.view.SwipeBackLayout;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class ShareWeiXinActivity extends Activity {
	private IWXAPI wxApi;

	@ViewInject(R.id.tv_title)
	private TextView title;

	@ViewInject(R.id.tv_title_right)
	private TextView right;

	private String Url;
	protected SwipeBackLayout layout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shareweixin);
		ViewUtils.inject(this);
		layout = (SwipeBackLayout) LayoutInflater.from(this).inflate(
				R.layout.base, null);
		layout.attachToActivity(this);
		registerReceiver(receiver, new IntentFilter(Constants.SHARE_BROAD));
		init();
	}

	private void init() {
		String randoms = getRandNum(6);
		Url = "http://haoup.net/share/share.html? region=" + Constants.cityName
				+ "&provider=" + Constants.prodiver + "&invitecode=" + randoms;
		// 实例化
		wxApi = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
		wxApi.registerApp(Constants.APP_ID);
		title.setText("分享");
		right.setText("取消");
		right.setVisibility(View.VISIBLE);
	}

	public String getRandNum(int charCount) {
		String charValue = "";
		for (int i = 0; i < charCount; i++) {
			char c = (char) (randomInt(0, 10) + '0');
			charValue += String.valueOf(c);
		}
		return charValue;

	}

	public int randomInt(int from, int to) {
		Random r = new Random();
		return from + r.nextInt(to - from);
	}

	@OnClick(R.id.dialog_item_rl_confirm)
	private void share2Friends(View v) {
		// 在需要分享的地方添加代码：
		wechatShare(0);// 分享到微信好友
	}

	@OnClick(R.id.dialog_item_rl_confirm1)
	private void share2FriendsCircle(View v) {
		// 在需要分享的地方添加代码：
		wechatShare(1);// 分享到微信朋友圈
	}

	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			int type = intent.getIntExtra("type", 0);
			if (type == 0 && Constants.isNetWork) {
//				getSharDate();
			}
		}
	};

	/**
	 * 微信分享
	 * 
	 * @param flag
	 *            (0:分享到微信好友，1：分享到微信朋友圈)
	 */
	private void wechatShare(int flag) {
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = Url;
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = "好上网";
		msg.description = "好好上网，天天向上";

		// 图片资源
		// Bitmap thumb = BitmapFactory.decodeResource(getResources(),
		// R.drawable.ic_loading);
		// msg.setThumbImage(thumb);
		// bitmap = dowmloadImage(imageUrl);
		// if(bitmap!=null)
		// msg.setThumbImage(bitmap);

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.message = msg;
		req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession
				: SendMessageToWX.Req.WXSceneTimeline;
		wxApi.sendReq(req);

	}

	/**
	 * 获取分享成功的 15天奖励 11.分享获取15天使用
	 * userid=register&committype=11&Account=01314252535
	 */
	protected void getSharDate() {
		String url = Constants.SERVER_URL + "?" + "committype=11" + "&userid="
				+ Constants.userId + "&Account=" + Constants.Account;

		new PutDataServer(new DataServiceResponder() {

			@Override
			public void onResult(DataServiceResult result) {
				if (result == null || result.code == null) {
					return;
				}
				if ("0".equals(result.code)) {
					Log.i("xzj", "分享成功，获取15天");
				} else {
				}
			}

			@Override
			public void onFailure() {

			}
		}, url, this,false).execute();
	}

	@OnClick(R.id.iv_title_left)
	public void onHeadBackClick(View view) {
		onBackPressed();
	}

	@OnClick(R.id.tv_title_right)
	public void onHeadRightBackClick(View view) {
		onBackPressed();
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		overridePendingTransition(R.anim.base_slide_right_in,
				R.anim.base_slide_remain);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, R.anim.base_slide_right_out);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}
}