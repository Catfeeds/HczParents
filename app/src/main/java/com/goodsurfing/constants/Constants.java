package com.goodsurfing.constants;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;

import com.alipay.a.a.a;
import com.goodsurfing.app.HaoUpApplication;
import com.goodsurfing.app.R;
import com.goodsurfing.beans.ChildBean;
import com.goodsurfing.beans.City;
import com.goodsurfing.beans.Friend;
import com.goodsurfing.beans.IPList;
import com.goodsurfing.beans.TimeCardBean;
import com.goodsurfing.beans.User;
import com.goodsurfing.beans.WebFilterBean;
import com.goodsurfing.database.dao.AppDao;
import com.goodsurfing.utils.CommonUtil;

/**
 * @Description: 常量类
 * @version: 1.0
 * @tag
 */
@SuppressLint("SdCardPath")
public class Constants {
	public static final String SP_NAME = "goodSurfing";
	public static final String SHARE_BROAD = "SHARE_BROAD";
	public static final String WEIX_PAY_BROAD = "WEIX_PAY_BROAD";
	public static final String CECKBOX_KEY = "CECKBOX_KEY";
	public static final String VERSIONCODE = "versioncode";
	public static final String LOCK = "lock";
	public static final String LOCK_KEY = "lock_key";
	public static final String LOGIN_INFO = "LOGIN_INFO";
	public static final String USER_MODE = "USER_MODE";
	public static final String LOGIN_CITY = "LOGIN_CITY";
	public static final String LOGIN_MOBEL = "LOGIN_MOBEL";
	public static final String LOGIN_PASS = "LOGIN_PASS";
	public static final String LOGIN_SERVICE = "LOGIN_SERVICE";
	public static final String CACHE_TIME = "CACHE_TIME";
	public static final String ALLOWEDWEB_TIME = "ALLOWEDWEB_TIME";
	public static final String UNALLOWEDWED_TIME = "UNALLOWEDWED_TIME";
	public static final String UNCHECKED_TIME = "UNCHECKED_TIME";
	public static final String CAR_TIME = "CAR_TIME";
	public static final String APP_USER_TYPE = "好上网手机";
	public static final String HCZ_LOGIN_URL = "/api/appLogin/login" ;
	public static final String HCZ_GETBINDCODE_URL = "/api/verify/bindChild" ;
	public static final String HCZ_GETCHILD_URL = "/api/client/clients" ;
	public static final String HCZ_GETBINDCHILD_URL = "/api/client/GetClient" ;
	public static final String HCZ_LOCKSCREEN_URL = "/api/lock/lockScreen" ;
	public static final String HCZ_SWITCHMODE_URL = "/api/mode/mode" ;
	public static final String HCZ_TIMECONTROL_URL = "/api/timeControl/screenTimeControl" ;
	public static final String HCZ_GETSCREENTIME_URL = "/api/timeControl/getScreenTime" ;
	public static final String HCZ_EDITCHILD_URL = "/api/client/edit" ;
	public static final String HCZ_EDITPERSON_URL = "/api/server/edit" ;
	public static final String HCZ_GETAPPLIST_URL = "/api/client/appslist" ;
	public static final String HCZ_APPMANAGE_URL = "/api/mode/appManage" ;
	public static final String HCZ_GETLOCATION_URL = "/api/Dynamic/GetData" ;
	public static final String HCZ_GETSTATISTICS_URL = "/Api/Dynamic/GetStatistics" ;
	public static final String HCZ_GETPOSITION_URL = "/api/client/position" ;
	public static final String HCZ_UNBINDCHILD_URL = "/api/client/unBind" ;
	public static final String HCZ_ALTERPWD_URL = "/api/Server/AlterPwd" ;
	public static final String HCZ_GETCODE_URL = "/api/verify/getCode" ;
	public static final String HCZ_FINDPWD_URL = "/api/Server/FindPwd" ;
	public static final String HCZ_GETMODELIST_URL = "/api/mode/getModeList" ;
	public static final String HCZ_BINDCHILD_URL = "/api/Client/BindChild" ;
	public static final String HCZ_OTHERLOGIN_URL = "/api/ThirdLogin/Login" ;
	public static final String HCZ_OTHERBIND_URL = "/api/ThirdLogin/bind" ;
    public static final String HCZ_LOCKSCREEN_KEY = "lockScreen";
    public static final String HCZ_TIMECONTROL_KEY = "timeControl";
    public static final String HCZ_SETAPPTIME_URL = "/Api/Client/SetApptime";
    public static final String HCZ_ISPLIST_URL = "https://www.haoup.net/Interfaces/Isp/IspList";
    public static final String HCZ_AREALIST_URL = "https://www.haoup.net/Interfaces/Isp/AreaList";
    public static boolean isNetWork = true;
	public static boolean canMoveAgin = false;
	public static File IMG_DIR = null;
	public static boolean isbusy = false;
	// 是否有开通功能 标志位
	public static boolean isRegistShow = false;
	public static List<String> serverList = new ArrayList<String>();
	public static List<String> cityStrList = new ArrayList<String>();
	public static  HashMap<String, List<City>> serverCityMap = new HashMap<>();
	// 开发调试模式设置
	// 必须和AndroidManifest.xml中android:debuggable值相同
	public static final boolean isDebugMode = false; // 调rue试模式
	public static final int UNIVALSAL_DEFAULT_WIDHT = 300;
	public static final int UNIVALSAL_DEFAULT_HEIGHT = 600;
	public static int TIME_OUT = 20 * 1000;// 网络超时时间
	public static final String DBG_INFO = "dbgInfo";
	public static final String RSA_PRIVATE = "MIICXgIBAAKBgQC9gXcXB9MDX5yQUDI8Zl437xcn7bqxLbWs1Gsg2di144dbt+JUk/kDUzOGMkgI64y5rHlJfmWexzmaLnnMBo4Kv1eIFl6J3ZkrRHXf38VsPFzqFfLmhBgqHVwsu4xnb/+sNKSXol05l3Y3ilfhdY3TSSuRGTbWsyG4jXKsYAH0UwIDAQABAoGBALX3pTUWLEcm/h2NBE//NCWdjaynmAceDz9f0WaACJnPW2B01/3DAO9yax1HFuiJ1l1MAkLR/h5SyzsDuaxx9t0uTU6UQpcQdV44SWUcz7/bDXRpqDyaXv3484gXCS87p1auVcFGOPumVedP7RGx+B7bSBvSGoev8oCUzh7tVSqRAkEA8VyMMcxa40W4lUsp6JdVJyzNcI3YSvOeumHwkeKJOk9VBp8Z4qv7jDGseaplZXBrZnZQrFLlLK6u6Jb4AGrsnQJBAMj/yexT+mxJFouQrpoCy7DsISQJlt2CI9C1xjZmPYL+uL17cNQxr7DWcJlvPt4gvPuRWd2nfl/FGoVaSTMkea8CQQCPmdCPkFZDAihgKug0SlWPiUhxvvMBJRE7Myo+odmjp6+e0OpFwq1XK/bCLG9HeytrOtmkrW1MuGG4YJoNJZDBAkAXmBcO28jAn0+7ME+emodojD9TqKCB/f3QSRYsWpIL53SuIWhcMDzsaC+tgyaBEpy8dxJyd2/ZNBzp3HBNRZHXAkEAnrh8C26HE+WBj3mcMBiCdrscYbSLT4H7JxEloUv4wZWCYD81bD//OIp1STD8lQhAe2skDQla2TlCyPYUQqYmDg==";
	public static final String HCZ_RSA_PRIVATE = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAO4MrpUXALr3+wPSq03ssUjrWqel4xoMjVss46g6LoQUu4nbu7THDPzV7LW0poozaiQRRakPFeCKxmjyTEPOtSI+KEF6lv1kml0xqkeQ2oajCMT0GdhU824/bir7BTVqP2pEIPzA8UF2a6fzH5zVPhR/0JrmkoG6MOPbRWZ9VqsNAgMBAAECgYEAsHXcMI6FrxNGErZm3/PWE2zLnbzdlSC65FxpQfonIyfPQUkOYtqu6PmXZ7bLQWUNiED5d3HEUNfubjs2h4lCQPrhKYcELCt+DKCzHZHh9U9PlZfeStJqfv1jVEOiimp3v5FLLfeB3BKk1/NLecU68TbvePXncxYAzdp652fBYCECQQD9Kb8WSGQmWB1GFEIrRcDvYeJCt2S0wx4x6dzT0fLGLME48VcLLj8r9jCEPAZKqzfy2dvZjX59jADb2GskFO8FAkEA8LeUPSUzNe6GxUsmX6fHTWLknSkgOIj1z/d/3cJxusSaMEj7X5sD6UXid6/Z7LG3uO9DmrnlMAIfIeJzhGu6aQJBAP0QCPKWRH5L4F9YCtjErlrcJtd4CVPkIz/TvSmEGcFdpXxtSwxjG0SvoMneUo+7XX8FpY1vsjx/gbBBQsBbqJ0CQH84tysd1xQA7c+mKTDTDr2yr5r0Wmgm0qvIZhcBJnSMZeVxHRHpbsbCPa/+C9JSFwSxx+wlpaCa7nNNKLpq6UkCQQDuASmVOgGNK15xVP6RzTxUk7CnYjC/96JqFeQTtsd93zht+ZSge/MOq2iWnWaghBjWFaJdOvKqJ3Rc+RtklCpP";
	public static final String HCZ_RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDuDK6VFwC69/sD0qtN7LFI61qnpeMaDI1bLOOoOi6EFLuJ27u0xwz81ey1tKaKM2okEUWpDxXgisZo8kxDzrUiPihBepb9ZJpdMapHkNqGowjE9BnYVPNuP24q+wU1aj9qRCD8wPFBdmun8x+c1T4Uf9Ca5pKBujDj20VmfVarDQIDAQAB";

	public static String SERVER_URL = "";
	public static String EDIT_PHONE_CODE_URL = "https://www.haoup.net/message/code.php";
	public static final String SERVER_URL_GLOBAL = "http://www.haoup.net:8765";
	public static int mode = 1;// 默认为家长模式


	public static final int MODE_LEARNING = 3;// 学习模式
	public static final int MODE_BAD = 2;// 不良内容模式
	public static final int MODE_FREE = 1;// 畅游模式
	public static final int MODE_REWARDS = 4;// 奖励卡模式
	public static  String MODE_LEARNING_TEXT = "学习模式";// 学习模式
	public static  String MODE_BAD_TEXT = "不良内容模式";// 不良内容模式
	public static  String MODE_FREE_TEXT = "畅游模式";// 畅游模式
	public static  String MODE_REWARDS_TEXT = "奖励卡模式";// 奖励卡模式

	public static String modeStr = "";
	public static boolean isDrawerOut = false;
	/** 屏幕宽度 */
	public static int devWidth = 720;
	/** 屏幕高度 */
	public static int devHeight = 1280;
	// 微信 APP_ID 替换为你的应用从官方网站申请到的合法appId
	public static final String WX_SECRET = "ac9313f68a4e3e474312763643306fb4";
	public static final String APP_ID = "wx7b49afd96ad69fba";// wx7b49afd96ad69fba
	// 友盟
	public static final String UM_KEY = "59a41c108630f5101e000c9e";// 59a41c108630f5101e000c9e
	/** 订金支付订单，用于微信支付成功后传递数据至回调中 */
	public static final String QQ_API_KEY ="101492008" ;
	public static boolean isThreadDestory = true;
	public static boolean isClockLocked = false;
	public static boolean isGetTime = false;
	public static int circleCounts = 0;
	public static int settingTimes = 0;
	public static int totalSeconds = 0;
	public static int settingTimesMax = 60 * 20;
	public static int showHightSize = 0;
	public static String tokenID = isDebugMode ? "" : "";// test t_jk 123456
	public static String userId = "";
	public static boolean bind = false;

	public static String Account = "";
	public static String userMobile = "";
	public static String userName = "";
	public static String orderId = "";
	public static final String PRICE_CENT = "0.01";// "0.01"
	public static final String CLIENTPHONE = "4008867733";
	public static boolean isUpdrade = false;
	public static boolean isAPPActive = true;
	public static String isAuthenicate = null;
	public static String isAccounts = null;
	public static boolean isRememberPassword = false;
	public static boolean isEditing = false;
	public static String cityName = null;
	public static String prodiver = null;
	public static Map<String, String> typeMap = new LinkedHashMap<String, String>();
	public static List<WebFilterBean> allWebList = new ArrayList<WebFilterBean>();
	public static List<WebFilterBean> unAllWebList = new ArrayList<WebFilterBean>();
	public static List<WebFilterBean> unCheckWebList = new ArrayList<WebFilterBean>();
	public static List<TimeCardBean> timerList = new ArrayList<TimeCardBean>();
	public static List<Friend> user = new ArrayList<Friend>();
	public static AppDao appDao;
	public static int selectIds[] = new int[] { R.drawable.children_head_select_1, R.drawable.children_head_select_2, R.drawable.children_head_select_3, R.drawable.children_head_select_4, R.drawable.children_head_select_5, R.drawable.children_head_select_6, R.drawable.children_head_select_7, R.drawable.children_head_select_8, };
	public static int showIds[] = new int[] { R.drawable.setting_children_head_defult_btn, R.drawable.children_head_1, R.drawable.children_head_2, R.drawable.children_head_3, R.drawable.children_head_4, R.drawable.children_head_5, R.drawable.children_head_6, R.drawable.children_head_7, R.drawable.children_head_8, };
	public static int showMapIds[] = new int[] { R.drawable.setting_children_head_defult_btn, R.drawable.show_head_map_1, R.drawable.show_head_map_2, R.drawable.show_head_map_3, R.drawable.show_head_map_4, R.drawable.show_head_map_5, R.drawable.show_head_map_6, R.drawable.show_head_map_7, R.drawable.show_head_map_8, };
	public static int showBottomIds[] = new int[] { R.drawable.setting_children_head_defult_btn, R.drawable.children_head_bottom_1, R.drawable.children_head_bottom_2, R.drawable.children_head_bottom_3, R.drawable.children_head_bottom_4, R.drawable.children_head_bottom_5, R.drawable.children_head_bottom_6, R.drawable.children_head_bottom_7, R.drawable.children_head_bottom_8, };
	public static String DB_NAME_CACHE = "mcache.db";
	public static String DB_NAME = "default.db";
    public static boolean isbindChild = false;
	public static List<ChildBean> childs;
	public static ChildBean child;
	public static List<IPList> serviceList;
	public static List<City> cityList;

	public static void clear(Context app) {
		typeMap.clear();
		allWebList.clear();
		unAllWebList.clear();
		unCheckWebList.clear();
		timerList.clear();
		userId = "";
		tokenID = "";
		userMobile = "";
		isThreadDestory = true;
		isClockLocked = false;
		totalSeconds = 0;
		settingTimes = 0;
		circleCounts = 0;
		childs=null;
		child =null;
		isbindChild = false;
		appDao.deleteAllWebFilterBean();
		User user = User.getUser();
		user.setuId(Constants.userId);
		CommonUtil.setUser(app, user);
	}

	// 用户信息
	public static String dealTime = "";
	public static String name = "";
	public static String sex = "";
	public static String birthday = "";
	public static String email = "";
	public static String adress = "";
	public static String TIMESTR_KEY = "TimerStr";
	public static boolean IS_SHOW_LAYOUT = true;
	public static boolean isShowLogin = false;
	public static String loginTime;
	public static String rewardCode;
	public static String WX_CODE;
	public static String ThreeLoginAuthId;
	public static String UserName;
	public static int UserSex;
	public static String UserHeadUrl;
	public static String UserId;
	// 支付宝商户号
	public static final String MCH_ID = "1300508201";

	// API密钥，在商户平台设置
	public static final String API_KEY = "3d520f827644ef52e732249284d593a1";
	public static final String APP_ICON_SERVER_URL = "http://172.16.0.201";
	public static final String Login_BROAD = "weixin.login";
}
