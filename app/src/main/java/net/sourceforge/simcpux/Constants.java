package net.sourceforge.simcpux;

public class Constants {

	/*
	 * #define APP_ID @"wx7b49afd96ad69fba" //APPID 
	 * #define APP_SECRET@"d4624c36b6795d1d99dcf0547af5443d" //appsecret //商户号，填写商户对应参数
	 * #defineMCH_ID @"1300508201" //商户API密钥，填写相应参
	 * #define PARTNER_ID @"3d520f827644ef52e732249284d593a1"
	 */

	// appid
	// 请同时修改 androidmanifest.xml里面，.PayActivityd里的属性<data
	// android:scheme="wxb4ba3c02aa476ea1"/>为新设置的appid
	public static final String APP_ID = "wx7b49afd96ad69fba";
	// public static final String APP_ID = "wxf2f565574a968187";

	// 商户号
	public static final String MCH_ID = "1300508201";
	// public static final String MCH_ID = "1233848001";

	// API密钥，在商户平台设置
	public static final String API_KEY = "3d520f827644ef52e732249284d593a1";
	// public static final String API_KEY="412fde4e9c2e2bb619514ecea142e449";

}
