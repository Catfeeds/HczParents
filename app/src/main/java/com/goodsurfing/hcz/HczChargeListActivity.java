package com.goodsurfing.hcz;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.android.component.constants.What;
import com.goodsurfing.app.R;
import com.goodsurfing.base.BaseActivity;
import com.goodsurfing.beans.OrederBean;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.main.ChargeListActivity;
import com.goodsurfing.main.LoginActivity;
import com.goodsurfing.main.PayOkActivity;
import com.goodsurfing.server.PayOkServer;
import com.goodsurfing.server.net.HczPayOrderNet;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResponder;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResult;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.PayResult;
import com.goodsurfing.utils.SignUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import net.sourceforge.simcpux.MD5;
import net.sourceforge.simcpux.Util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class HczChargeListActivity extends BaseActivity {

	private final static String TAG = "ChargeListActivity";

	protected static final int REFRESH = 100;
	protected static final int SEND = 101;

	private static boolean isApayChoice = true;

	@ViewInject(R.id.tv_title)
	private TextView title;

	@ViewInject(R.id.tv_title_right)
	private TextView right;

	@ViewInject(R.id.activity_charge_list_tv_name)
	private TextView chargeName;

	@ViewInject(R.id.activity_charge_list_tv_values)
	private TextView chargeValues;

	@ViewInject(R.id.activity_charge_iv_zhifu)
	private ImageView apayImageView;

	@ViewInject(R.id.activity_charge_iv_weixin)
	private ImageView weixinImageView;
	@ViewInject(R.id.title_layout)
	private View title_layout;
	// 商户PID
	public static final String PARTNER = "2088121009856945";
	// 商户收款账号
	public static final String SELLER = "2088121009856945";
	// 商户私钥，pkcs8格式
	public static final String RSA_PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMes/9apq+DTkl7xQ2f3vtqxCT2TPI4Mjwg1JIvxGmki2VKlkrafvvmKT/xi3ay07x57yjndO3F0J6wWAT6kUNR4TR/jYeXqDKpOuWxPZ8BpOqZrlUzwXIweu2EIas/+flA7Er8oq9e/sWdmqCEgFE930F/HGdRM3IOwVdN0/RedAgMBAAECgYEAhMNDTEB+Vrt17Aiwj9VLIe9qPHXEYpJ5G7Tx+tYxgEw6gVgzp5epjBPpwN8fkzCueO9H85dkabgYlLQA2dy5HI7Z050sOle4CmmkW/ccC1dr5WNyhM9IrezHZIa0yDJzKacFixiGAF6xmoLA6Ife54avTqvSZAwVNOUQfNnnReECQQD0cxpSFnjxr+AXoGg07BEmitustbXphf6Yjh+sELe/8AhgDTYPQUF6aeT4VVTA4kYDnhs//srE2YLS2EJO8y0pAkEA0RxOSxWUNAgnqGcmJVoJR38yGGbn2WkH+/wfGnx+M871CR17Jt0OAmM25pkvJfiZ7rsQNHNtIc3aYH/IRtBxVQJATcMP7G0ZrEi2kM2GWM9/5TLnDtn/NHpbs0wC50mqKnTBNUz+lXu8yKRHIniCrZlNjHkPUhxLhLNs2oXREixpgQJAG6qyFS8at7Oog5h6LJD4D1Sd7SqYXGSQIN/fwaJdFD+6neUfqSmwM9KqreHwogZ9X1+yqi3nb4SL8x6VAgGMLQJAaFSfJgM9X3nyWBxoFNjlu2sQh6ynIfAJM1wr3ITfAitlCkB6DbX5M4iRMIU26G/pYf2TUEm6b3SrP5MDgjkPtw==";
	// 支付宝公钥
	public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
	private static final int SDK_PAY_FLAG = 1;
	private static final int SDK_CHECK_FLAG = 2;

	private  String NonceStr;

	PayReq req;
	final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
	// TextView show;
	Map<String, String> resultunifiedorder;
	StringBuffer sb;

	private IWXAPI api;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG:
				PayResult payResult = new PayResult((String) msg.obj);
				String resultInfo = payResult.getResult();// 同步返回需要验证的信息
				String resultStatus = payResult.getResultStatus();
				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				String payStatus="1";
				if (TextUtils.equals(resultStatus, "9000")) {
					ActivityUtil.showPopWindow4Tips(HczChargeListActivity.this, title, false, "支付成功");
					payStatus="2";
					getPayTime("2","",payStatus);
				} else {
					ActivityUtil.showPopWindow4Tips(HczChargeListActivity.this, title, false, "支付失败");
					// "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						payStatus="4";
					} else {
						payStatus="3";
					}
				}
				break;
			case SDK_CHECK_FLAG:
				Toast.makeText(HczChargeListActivity.this, "检查结果为：" + msg.obj, Toast.LENGTH_SHORT).show();
				break;

			}
		};
	};

	private String packgeId;

	private void getPayTime(String payType,String payOpenId,String payStatus) {
		ActivityUtil.showPopWindow4Tips(HczChargeListActivity.this, title, false, false,"提交支付信息...",-1);
		HczPayOrderNet payOrderNet= new HczPayOrderNet(this,new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
					case What.HTTP_REQUEST_CURD_SUCCESS:
						ActivityUtil.showPopWindow4Tips(HczChargeListActivity.this, title, true, "购买成功");
						Constants.dealTime = (String) msg.obj;
						Intent intent = new Intent(HczChargeListActivity.this, PayOkActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("name", chargeName.getText().toString());
						bundle.putString("money", chargeValues.getText().toString());
						bundle.putString("time", "到期时间" + Constants.dealTime);
						intent.putExtras(bundle);
						startActivity(intent);
						finish();
						break;
					case What.HTTP_REQUEST_CURD_FAILURE:
						ActivityUtil.showPopWindow4Tips(HczChargeListActivity.this, title, false, msg.obj.toString() + "");
						break;
				}
			}
		});
		payOrderNet.putParams(packgeId,payType,payOpenId,payStatus,NonceStr);
		payOrderNet.sendRequest();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_charge_list);
		ViewUtils.inject(this);
		init();
	}

	private void init() {
		title.setText("选择支付");
		right.setVisibility(View.INVISIBLE);
		Intent intent = getIntent();

		if (intent != null) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				chargeName.setText(bundle.getString("CHARGE_TYPE"));
				chargeValues.setText(bundle.getString("CHARGE_SUM"));
				packgeId = bundle.getString("packgeId");
				NonceStr = bundle.getString("NonceStr");
			}
		}
		req = new PayReq();
		sb = new StringBuffer();

		msgApi.registerApp(Constants.APP_ID);

		registerReceiver(mReceiver, new IntentFilter(Constants.WEIX_PAY_BROAD));
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			int errCode = arg1.getExtras().getInt("type");
			String result = "1";
			switch (errCode) {
			case BaseResp.ErrCode.ERR_OK:
				result = "2";
				getPayTime("1","",result);
				break;
			case BaseResp.ErrCode.ERR_USER_CANCEL:
				result = "3";
				ActivityUtil.showPopWindow4Tips(HczChargeListActivity.this, title, false, "取消支付");
				break;
			case BaseResp.ErrCode.ERR_AUTH_DENIED:
				result = "3";
				ActivityUtil.showPopWindow4Tips(HczChargeListActivity.this, title, false, "拒绝支付");
				break;
			}
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		isApayChoice = true;
		if (isApayChoice) {
			apayImageView.setImageResource(R.drawable.ic_charge_select);
			weixinImageView.setImageResource(R.drawable.ic_charge_unselect);
		} else {
			apayImageView.setImageResource(R.drawable.ic_charge_unselect);
			weixinImageView.setImageResource(R.drawable.ic_charge_select);
		}

	}

	@OnClick(R.id.iv_title_left)
	public void onHeadBackClick(View view) {
		onBackPressed();
	}

	/**
	 * call alipay sdk pay. 调用SDK支付
	 * 
	 */
	@OnClick(R.id.activity_setting_loginout_rl)
	public void onPayClik(View v) {
		if ("".equals(Constants.tokenID)) {
			LoginActivity.gotoLogin(this);
			return;
		}
		if (isApayChoice)
			pay(v);
		else
			weiXinPay();
	}

	public void pay(View v) {
		if (TextUtils.isEmpty(PARTNER) || TextUtils.isEmpty(RSA_PRIVATE) || TextUtils.isEmpty(SELLER)) {
			new AlertDialog.Builder(this).setTitle("警告").setMessage("需要配置PARTNER | RSA_PRIVATE| SELLER").setPositiveButton("确定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialoginterface, int i) {
					//
					onBackPressed();
				}
			}).show();
			return;
		}

		String bodyName = chargeName.getText().toString().trim();
		String s = "";

		try {
			s = new String(bodyName.getBytes("UTF-8"), "UTF-8");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String fee = chargeValues.getText().toString().trim();

		String orderInfo = getOrderInfo(s, "商品的详细描述", fee.substring(0, fee.length() - 1));
		// String orderInfo = getOrderInfo(s, "商品的详细描述",
		// "0.01");

		/**
		 * 特别注意，这里的签名逻辑需要放在服务端，切勿将私钥泄露在代码中！
		 */
		String sign = sign(orderInfo);
		try {
			/**
			 * 仅需对sign 做URL编码
			 */
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		/**
		 * 完整的符合支付宝参数规范的订单信息
		 */
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();

		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(HczChargeListActivity.this);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo, true);
				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	/**
	 * check whether the device has authentication alipay account.
	 * 查询终端设备是否存在支付宝认证账户
	 * 
	 */
	public void check(View v) {
		Runnable checkRunnable = new Runnable() {
			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask payTask = new PayTask(HczChargeListActivity.this);
				// 调用查询接口，获取查询结果
				boolean isExist = payTask.checkAccountIfExist();

				Message msg = new Message();
				msg.what = SDK_CHECK_FLAG;
				msg.obj = isExist;
				mHandler.sendMessage(msg);
			}
		};

		Thread checkThread = new Thread(checkRunnable);
		checkThread.start();

	}

	/**
	 * get the sdk version. 获取SDK版本号
	 * 
	 */
	public void getSDKVersion() {
		PayTask payTask = new PayTask(this);
		String version = payTask.getVersion();
		Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
	}

	/**
	 * create the order info. 创建订单信息
	 * 
	 */
	private String getOrderInfo(String subject, String body, String price) {

		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm" + "\"";

		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}

	/**
	 * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
	 * 
	 */
	private String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);

		Random r = new Random();
		key = key + r.nextInt();
		key = key.substring(0, 15);
		return key;
	}

	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	private String sign(String content) {
		return SignUtils.sign(content, RSA_PRIVATE);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	private String getSignType() {
		return "sign_type=\"RSA\"";
	}

	@OnClick(R.id.activity_charge_iv_zhifu)
	public void onApayClick(View v) {
		if (isApayChoice)
			return;
		isApayChoice = true;
		apayImageView.setImageResource(R.drawable.ic_charge_select);
		weixinImageView.setImageResource(R.drawable.ic_charge_unselect);
	}

	@OnClick(R.id.activity_charge_iv_weixin)
	public void onWeiXinClick(View v) {
		if (isApayChoice == false)
			return;
		isApayChoice = false;
		apayImageView.setImageResource(R.drawable.ic_charge_unselect);
		weixinImageView.setImageResource(R.drawable.ic_charge_select);
	}

	private void weiXinPay() {
		GetPrepayIdTask getPrepayId = new GetPrepayIdTask();
		getPrepayId.execute();

	}

	/* 微信支付相关 */
	/**
	 * 生成签名
	 */

	private String genPackageSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(Constants.API_KEY);

		String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
		Log.e("key:", packageSign);
		return packageSign;
	}

	private String genAppSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(Constants.API_KEY);

		this.sb.append("sign str\n" + sb.toString() + "\n\n");
		String appSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
		Log.e("orion", appSign);
		return appSign;
	}

	private String toXml(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();
		sb.append("<xml>");
		for (int i = 0; i < params.size(); i++) {
			sb.append("<" + params.get(i).getName() + ">");

			sb.append(params.get(i).getValue());
			sb.append("</" + params.get(i).getName() + ">");
		}
		sb.append("</xml>");

		Log.e("orion", sb.toString());
		return sb.toString();
	}

	private class GetPrepayIdTask extends AsyncTask<Void, Void, Map<String, String>> {

		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(HczChargeListActivity.this, "提示", "正在获取预支付订单...");
		}

		@Override
		protected void onPostExecute(Map<String, String> result) {
			if (dialog != null) {
				dialog.dismiss();
			}
			if (result == null)
				return;

			sb.append("prepay_id\n" + result.get("prepay_id") + "\n\n");
			// show.setText(sb.toString());

			resultunifiedorder = result;
			genPayReq();
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected Map<String, String> doInBackground(Void... params) {
			// "http://wxpay.weixin.qq.com/pub_v2/app/app_pay.php?plat=android"
			String url = String.format("https://api.mch.weixin.qq.com/pay/unifiedorder");
			String entity = genProductArgs();

			Log.e("orion", entity);

			byte[] buf = Util.httpPost(url, entity);

			if (buf != null && buf.length > 0) {
				String content = new String(buf);
				Log.e("result:", content);
				Map<String, String> xml = decodeXml(content);
				return xml;
			}
			return null;
		}
	}

	public Map<String, String> decodeXml(String content) {

		try {
			Map<String, String> xml = new HashMap<String, String>();
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new StringReader(content));
			int event = parser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {

				String nodeName = parser.getName();
				switch (event) {
				case XmlPullParser.START_DOCUMENT:

					break;
				case XmlPullParser.START_TAG:

					if ("xml".equals(nodeName) == false) {
						// 实例化student对象
						xml.put(nodeName, parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				event = parser.next();
			}

			return xml;
		} catch (Exception e) {
			Log.e("orion", e.toString());
		}
		return null;

	}

	private String genNonceStr() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
	}

	private long genTimeStamp() {
		return System.currentTimeMillis() / 1000;
	}

	private String genOutTradNo() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
	}

	//
	private String genProductArgs() {
		StringBuffer xml = new StringBuffer();

		try {
			String nonceStr = genNonceStr();

			xml.append("</xml>");
			List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
			packageParams.add(new BasicNameValuePair("appid", Constants.APP_ID));
			String bodyName = chargeName.getText().toString().trim();
			String s = new String(bodyName.getBytes("UTF-8"), "UTF-8");

			packageParams.add(new BasicNameValuePair("body", s));//
			packageParams.add(new BasicNameValuePair("mch_id", Constants.MCH_ID));
			packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
			packageParams.add(new BasicNameValuePair("notify_url", "http://121.40.35.3/test"));
			packageParams.add(new BasicNameValuePair("out_trade_no", genOutTradNo()));
			packageParams.add(new BasicNameValuePair("spbill_create_ip", "127.0.0.1"));
			String fee = chargeValues.getText().toString().trim();
			double v = Double.valueOf(fee.substring(0, fee.length() - 1)) * 100;
			String fees = String.valueOf(v);

			packageParams.add(new BasicNameValuePair("total_fee", fees.substring(0, fees.lastIndexOf("."))));
			packageParams.add(new BasicNameValuePair("trade_type", "APP"));

			String sign = genPackageSign(packageParams);
			packageParams.add(new BasicNameValuePair("sign", sign));

			String xmlstring = toXml(packageParams);

			return new String(xmlstring.toString().getBytes(), "ISO8859-1");

		} catch (Exception e) {
			Log.e(TAG, "genProductArgs fail, ex = " + e.getMessage());
			return null;
		}

	}

	// 生成签名参数
	private void genPayReq() {
		/*
		 * req.appId = "wx0e4f2c1890a9a60e"; req.partnerId = "1250886701";
		 * req.prepayId = "wx201507031507437aea42539a0164660158"; //
		 * req.prepayId = "wx2015070314124806d012e3bf0534791906";
		 * req.packageValue = "Sign=WXPay"; req.nonceStr =
		 * "452vql0loyvdgerjbgs91hzrkn9atbjc"; // req.nonceStr =
		 * "19tj9dp8o3jv165xij3cq0fwx712os4a"; req.timeStamp = "1435907263";
		 */
		req.appId = Constants.APP_ID;
		req.partnerId = Constants.MCH_ID;
		String prepayID = resultunifiedorder.get("prepay_id");
		req.prepayId = prepayID;
		req.packageValue = "Sign=WXPay";
		req.nonceStr = genNonceStr();
		req.timeStamp = String.valueOf(genTimeStamp());

		List<NameValuePair> signParams = new LinkedList<NameValuePair>();
		signParams.add(new BasicNameValuePair("appid", req.appId));
		signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
		signParams.add(new BasicNameValuePair("package", req.packageValue));
		signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
		signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
		signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));

		req.sign = genAppSign(signParams);

		sb.append("sign\n" + req.sign + "\n\n");

		// show.setText(sb.toString());
		sendPayReq();
		Log.e("orion", signParams.toString());

	}

	// 调起微信支付
	private void sendPayReq() {
		msgApi.registerApp(Constants.APP_ID);
		msgApi.sendReq(req);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}

}
