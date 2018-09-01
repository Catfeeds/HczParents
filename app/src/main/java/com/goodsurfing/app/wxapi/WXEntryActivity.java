package com.goodsurfing.app.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.TextView;

import com.goodsurfing.app.R;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.utils.EventHandler;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

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
	}

	// 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
	@Override
	public void onResp(BaseResp resp) {
		String result = null;
		Intent intent = new Intent();
		intent.setAction(Constants.Login_BROAD);
		intent.putExtra("errCode", resp.errCode);
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			if (resp instanceof SendAuth.Resp) {
				SendAuth.Resp resps = (SendAuth.Resp) resp;
				if (!TextUtils.isEmpty(resps.code)) {
					// 获取用户数据
					Constants.WX_CODE = resps.code;
				}
			} else {
				result = "分享成功";
			}
			result = "成功";
			intent.putExtra("type", 0);
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			result = "用户取消";
			intent.putExtra("type", 1);
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			result = "用户拒绝";
			intent.putExtra("type", 2);
			break;
		}
		sendBroadcast(intent);
		this.finish();
	}

}