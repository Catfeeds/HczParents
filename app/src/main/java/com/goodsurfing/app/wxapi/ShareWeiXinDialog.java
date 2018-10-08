package com.goodsurfing.app.wxapi;

import java.util.Random;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.goodsurfing.app.R;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.utils.AsyncImageLoader;
import com.goodsurfing.utils.EventHandler;
import com.goodsurfing.utils.LogUtil;
import com.goodsurfing.utils.PropertiesUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class ShareWeiXinDialog {
	protected static final String TAG = "ShareWeiXinDialog";
	// public ProgressDialog progressDialog = null;
	private final static int SHARE_WEIXIN = 1;
	private boolean isServerQuestOK = true;
	private IWXAPI wxApi;
	private Context context;
	private String Url;
	// private String imageUrl;
	private String title;
	private Bitmap bitmap = null;
	private String orderId = null;
	private int shareFlag = -1;

	public ShareWeiXinDialog(Context con, String orderId) {
		// 实例化
		context = con;
		String randoms = getRandNum(6);
		Url = "http://haoup.net/share/share.html? region=" + Constants.cityName
				+ "&provider=" + Constants.prodiver + "&invitecode=" + randoms;
		this.orderId = orderId;
		wxApi = WXAPIFactory.createWXAPI(con, Constants.APP_ID);
		wxApi.registerApp(Constants.APP_ID);
		// requestServerData();
	}

	private void share2Friends(View v) {
		// 在需要分享的地方添加代码：
		if (isServerQuestOK)
			wechatShare(0);// 分享到微信好友
		else
			setShareFlag(0);// 服务器数据未加载请求完成
	}

	private void share2FriendsCircle(View v) {
		// 在需要分享的地方添加代码：
		if (isServerQuestOK)
			wechatShare(1);// 分享到微信朋友圈
		else
			setShareFlag(1);// 服务器数据未加载请求完成
	}

	private void setShareFlag(int flag) {
		shareFlag = flag;
	}

	private boolean isShareFlagValid() {
		if ((shareFlag == 0) || (shareFlag == 1))
			return true;
		else
			return false;
	}

	/**
	 * 微信分享 （这里仅提供一个分享网页的示例，其它请参看官网示例代码）
	 * 
	 * @param flag
	 *            (0:分享到微信好友，1：分享到微信朋友圈)
	 */
	private void wechatShare(int flag) {

		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = Url;
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = title;
		msg.description = "这里填写内容";

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

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHARE_WEIXIN:
				if (isShareFlagValid())
					wechatShare(shareFlag);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	@SuppressLint("NewApi")
	public void showShareWXDialog() {

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.activity_shareweixin, null);

		final ImageView image = (ImageView) layout
				.findViewById(R.id.dialog_share_item_iv_weixin);
		final ImageView image1 = (ImageView) layout
				.findViewById(R.id.dialog_share_item_iv_weixin1);
		// Button button = (Button)
		// layout.findViewById(R.id.dialog_share_item_bt);
		// set a large value put it in bottom
		image.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				share2Friends(v);
			}
		});

		image1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				share2FriendsCircle(v);
			}
		});
		final Dialog dlg = new Dialog(context, R.style.AlertDialogCustom);
		dlg.setContentView(layout);
		// dlg.show();

		Window dialogWindow = dlg.getWindow();
		// 设置位置
		dialogWindow.setGravity(Gravity.BOTTOM);

		// 设置dialog的宽高属性
		dialogWindow.getDecorView().setPadding(0, 10, 0, 20);
		dlg.setContentView(layout);

		dialogWindow.setLayout(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		dlg.show();
		// new
		// AlertDialog.Builder(context,R.style.AlertDialogCustom).setView(layout).show();
	}

	private Bitmap dowmloadImage(String url) {

		if (!"".equals(url)) {
			try {
				AsyncImageLoader loader = new AsyncImageLoader(context);
				loader.setCache2File(true);
				loader.setCachedDir(context.getCacheDir().getAbsolutePath());
				loader.downloadImage(url, true,
						new AsyncImageLoader.ImageCallback() {
							@Override
							public void onImageLoaded(Bitmap bt, String imageUrl) {
								if (bt != null) {
									LogUtil.log("FloorAdapter", imageUrl);
									bitmap = bt;

								} else {
									// 下载失败，设置默认图片
									LogUtil.log("FloorAdapter", "下载失败!");
									bitmap = null;
								}
							}

						});
				// new ImageUtil(image).execute(landBean.getLogourl());
			} catch (Exception e) {
				LogUtil.logError(e);
				bitmap = null;
			}
		}
		return bitmap;
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
}