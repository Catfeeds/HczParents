package com.goodsurfing.app.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.goodsurfing.app.R;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.utils.EventHandler;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

	// IWXAPI 是第三方app和微信通信的openapi接口
	private IWXAPI api;

	private TextView shareView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// 通过WXAPIFactory工厂，获取IWXAPI的实例
		super.onCreate(savedInstanceState);
		api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	// 微信发送请求到第三方应用时，会回调到该方法
	@Override
	public void onReq(BaseReq req) {
		switch (req.getType()) {
		case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
			break;
		case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
			break;
		default:
			break;
		}
	}

	// 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
	@Override
	public void onResp(BaseResp resp) {
		String result = null;
		Intent intent = new Intent();
		intent.setAction(Constants.WEIX_PAY_BROAD);
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			result = "支付成功";
			intent.putExtra("type", 0);
//			EventHandler.showToast(this, result, R.drawable.toast_ok,1);
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			result = "用户取消";
			intent.putExtra("type", 1);
//			EventHandler.showToast(this, result, R.drawable.toast_failure,1);
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			result = "用户拒绝";
			intent.putExtra("type", 2);
//			EventHandler.showToast(this, result, R.drawable.toast_failure,1);
			break;
		}
		sendBroadcast(intent);
		this.finish();
	}

}