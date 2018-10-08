package com.goodsurfing.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.goodsurfing.base.GoodSurfingApplication;
import com.goodsurfing.beans.User;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.fundlock.GestureVerifyActivity;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;

/**
 * @ClassName: CommonUtil
 * @Description: 公共工具
 * @version: 1.0
 * @Create: 2015-5-5 14:26
 */
public class CommonUtil {

    private static final int Align_Num = 4;
    private static long lastClickTime;

    /**
     * @return
     * @Title: isFastDoubleClick
     * @Description: 是否多次重复点击
     * @date 2015-5-5 14:26
     */
    public static boolean isFastDoubleClick() {
        long currentTime = System.currentTimeMillis();
        long diffTime = currentTime - lastClickTime;
        if (0 < diffTime && diffTime < 500) {
            return true;
        }
        lastClickTime = currentTime;
        return false;
    }

    /**
     * @return
     * @Title: checkNetWork
     * @Description: 判断是否有网络访问
     * @date 2015-5-5 14:27
     */
    public static boolean checkNetWork(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * @param url
     * @return
     * @Title: returnBitMap
     * @Description: 从网络获取图片(2.3版本以后 ， 改为ImageUtil中图片处理方式)
     * @date 2015-5-5 14:27
     */
    public static Bitmap returnBitMap(String url) {
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {
            myFileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            LogUtil.logError(e);
        }
        return bitmap;
    }

    /**
     * @param count
     * @param pageSize
     * @return
     * @throws Exception
     * @Title: getTotalPage
     * @Description: 获取所有页数
     * @date 2015-5-5 14:27
     */
    public static int getTotalPage(int count, int pageSize) throws Exception {
        return ((count - 1) / pageSize + 1);
    }

    // /**
    // *
    // * @Title: getAppVersionName
    // * @Description: 获取版本号
    // * @return
    // * @date 2015-5-5 14:28
    // */
    // public static String getAppVersionName(Activity activity) {
    // try {
    // String pkName = activity.getPackageName();
    // String versionName = activity.getPackageManager().getPackageInfo(
    // pkName, 0).versionName;
    // GoodSurfingApplication.versionCode = activity.getPackageManager()
    // .getPackageInfo(pkName, 0).versionCode;
    // return versionName;
    // } catch (Exception e) {
    // LogUtil.logError(e);
    // }
    // return "";
    // }

    /**
     * @return
     * @Title: getAppVersionName
     * @Description: 获取版本号
     * @date 2015-5-5 14:28
     */
    public static int getAppVersionCode(Activity activity) {
        try {
            String pkName = activity.getPackageName();
            GoodSurfingApplication.versionCode = activity.getPackageManager().getPackageInfo(pkName, 0).versionCode;
            return GoodSurfingApplication.versionCode;
        } catch (Exception e) {
            LogUtil.logError(e);
        }
        return 1;
    }

    /**
     * /** 跳转到指定界面
     *
     * @param srcActivity
     * @param dstActivityClz 要打开的界面
     */
    public static void openActivity(Activity srcActivity, Class<? extends Activity> dstActivityClz) {
        Intent intent = new Intent();
        intent.setClass(srcActivity, dstActivityClz);
        srcActivity.startActivity(intent);
        // srcActivity.finish();
    }

    /**
     * 将数字字符串按指定长度分节，如用于格式化价格字符串
     *
     * @param price
     * @param divideLen
     * @return
     */
    public static String formatStr(String price, int divideLen) {
        String format = price;
        if (!price.matches("^[-+]?\\b[0-9]+(\\.[0-9]{1,2})?\\b$")) {
            return price;
        }
        int len = price.indexOf('.');
        int intLen = (len > 0) ? len : price.length();
        int mod = intLen % divideLen;
        format = price.substring(0, intLen);
        if (mod != 0) {
            format = format.replaceFirst("(\\A\\d{" + mod + "})", "$1,");
        }
        format = format.replaceAll("(\\d{" + divideLen + "})", "$1,");
        format = format.substring(0, format.length() - 1);
        return format + price.substring(intLen);
    }

    // 屏蔽重要部分信息
    public static String markString(String source) {
        String des = source;
        String first = null;
        if ("".equals(source))
            return source;
        int len = source.indexOf("@");
        int length = len > 0 ? len : source.length();
        int firstlength = 0;
        int endlength = 0;
        if (length > 10) {
            firstlength = 3;
            endlength = length - 3;
        } else if (length > 3) {
            firstlength = 2;
            endlength = length;
        } else {
            firstlength = 1;
            endlength = length;
        }
        first = des.substring(firstlength, endlength);
        des = des.substring(0, firstlength) + first.replaceAll("\\w|[\u4E00-\u9FA5]", "*") + des.substring(endlength, des.length());
        return des;
    }

    /**
     * 获取屏幕的宽度
     *
     * @param act
     * @return
     */
    public static int getScreenWidth(Activity act) {
        DisplayMetrics dm = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * 获取屏幕的高度
     *
     * @param act
     * @return
     */
    public static int getScreenHeight(Activity act) {
        DisplayMetrics dm = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    public static String getPackageName(Activity act) {
        PackageInfo info;
        String packageNames = "";
        try {
            info = act.getPackageManager().getPackageInfo(act.getPackageName(), 0);
            /*
             * // 当前应用的版本名称 String versionName = info.versionName; // 当前版本的版本号
             * int versionCode = info.versionCode;
             */
            // 当前版本的包名
            packageNames = info.packageName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageNames;
    }

    /*
     * 打开设置网络界面
     */
    public static void setNetworkMethod(final Context context) {
        // 提示对话框
        AlertDialog.Builder builder = new Builder(context);
        builder.setTitle("网络设置提示").setMessage("网络连接不可用,是否进行设置?").setPositiveButton("设置", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Intent intent = null;
                // 判断手机系统的版本 即API大于10 就是3.0或以上版本
                if (android.os.Build.VERSION.SDK_INT > 10) {
                    intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                } else {
                    intent = new Intent();
                    ComponentName component = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
                    intent.setComponent(component);
                    intent.setAction("android.intent.action.VIEW");
                }
                context.startActivity(intent);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        }).show();
    }

    /**
     * 获取本机IP地址
     *
     * @return
     */
    public static String getLocalIpAddress() {
        String regexIPv4 = "(?:[0-9]{1,3}\\.){3}[0-9]{1,3}";
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress.getHostAddress().toString().matches(regexIPv4)) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            LogUtil.log("WifiPreference IpAddress", ex.toString());
        }
        return null;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getCurrentMonth() {
        Date date = new java.util.Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        return sdf.format(date);
    }

    @SuppressLint("NewApi")
    public static String getImageSavePath(Context context) {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }

    public static void loadImageFromUrl(Context context, final ImageView iv, String url) {
        BitmapUtils utils = new BitmapUtils(context);
        // 默认图标和下载失败图片
        // utils.configDefaultLoadingImage(R.drawable.icon_default);
        // utils.configDefaultLoadFailedImage(R.drawable.icon_default);
        utils.configThreadPoolSize(3);
        utils.display(iv, url, new BitmapLoadCallBack<View>() {

            @Override
            public void onLoadCompleted(View v, String arg1, Bitmap bitmap, BitmapDisplayConfig arg3, BitmapLoadFrom arg4) {
                // TODO Auto-generated method stub
                iv.setImageBitmap(bitmap);
            }

            @Override
            public void onLoadFailed(View v, String arg1, Drawable arg2) {
                // TODO Auto-generated method stub

            }
        });
        // utils.configDefaultBitmapMaxSize(720, 1280);

    }

    /**
     * 从短信中获取验证码
     *
     * @param sms
     * @return
     */
    public static String getVerificationCode(String sms) {
        String regexVerCode = "(?<!\\d)\\d{4,10}(?!\\d)";

        if (TextUtils.isEmpty(sms)) {
            return null;
        }

        Pattern p = Pattern.compile(regexVerCode);
        Matcher matcher = p.matcher(sms);
        if (matcher.find()) {
            return matcher.group();
        }

        return null;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();

        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));

        ((MarginLayoutParams) params).setMargins(10, 10, 10, 10); // 可删除

        listView.setLayoutParams(params);
    }

    public static void saveRememberPasswordFlag(Context context, boolean flag) {
        Editor editor = context.getSharedPreferences("REMEMBER_PASSWORD", Activity.MODE_PRIVATE).edit(); // 获取编辑器
        editor.putBoolean("rememberpw", flag);
        editor.putString("isAuthenicate", Constants.isAuthenicate);
        editor.putString("isAccounts", Constants.isAccounts);
        editor.commit();// 提交修改
    }

    public static boolean getRememberPasswordFlag(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("REMEMBER_PASSWORD", Activity.MODE_PRIVATE);
        Constants.isAuthenicate = preferences.getString("isAuthenicate", "");
        Constants.isAccounts = preferences.getString("isAccounts", "");

        return preferences.getBoolean("rememberpw", false);
    }

    /**
     * @return
     * @Title: getUser
     * @Description: 获取登录User实体
     * @author
     * @date 2015-6-4上午9:57:45
     */
    public static User getUser(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("USER", Activity.MODE_PRIVATE);
        User user = User.getUser();
        try {
            if (!"".equals(preferences.getString("uid", ""))) {
                user.setuId(new String(Base64.decode(preferences.getString("uid", ""))));
                user.setUserName(new String(Base64.decode(preferences.getString("username", ""))));
                user.setPhone(new String(Base64.decode(preferences.getString("phone", ""))));
                user.setMode(new String(Base64.decode(preferences.getString("mode", ""))));
                user.setAccount(new String(Base64.decode(preferences.getString("account", ""))));
                user.setRewardCode(new String(Base64.decode(preferences.getString("rewardCode", ""))));
                user.setEditTime(new String(Base64.decode(preferences.getString("ExpirationTime", ""))));
                user.setTokenId(new String(Base64.decode(preferences.getString("tokenId", ""))));
                user.setLoginTime(new String(Base64.decode(preferences.getString("loginTime", ""))));
                user.setUserSex(new String(Base64.decode(preferences.getString("sex", ""))));
                user.setAvatar(new String(Base64.decode(preferences.getString("birthday", ""))));
                user.setEmail(new String(Base64.decode(preferences.getString("Email", ""))));
                user.setIP(new String(Base64.decode(preferences.getString("IP", ""))));
                user.setAddress(new String(Base64.decode(preferences.getString("address", ""))));
            }
        }catch (Exception e){

        }finally {
            return user;
        }
    }

    /**
     * @param user
     * @Title: setUser
     * @Description: 设置缓存中USER实体
     * @author
     * @date 2015-6-4上午9:57:45
     */
    public static void setUser(Context context, User user) {
        /** start 缓存系统登录用户信息 **********************************/
        Editor editor = context.getSharedPreferences("USER", Activity.MODE_PRIVATE).edit(); // 获取编辑器
        try {
            editor.putString("uid", (Base64.encode(user.getuId().getBytes())).toString());
            editor.putString("phone", (Base64.encode(user.getPhone().getBytes())).toString());
            editor.putString("account", (Base64.encode(user.getAccount().getBytes())).toString());
            editor.putString("loginTime", (Base64.encode(user.getLoginTime().getBytes())).toString());
            editor.putString("mode", (Base64.encode(user.getMode().getBytes())).toString());
            editor.putString("rewardCode", (Base64.encode(user.getRewardCode().getBytes())).toString());
            editor.putString("username", (Base64.encode(user.getUserName().getBytes())).toString());
            editor.putString("tokenId", (Base64.encode(user.getTokenId().getBytes())).toString());
            editor.putString("ExpirationTime", (Base64.encode(user.getEditTime().getBytes())).toString());
            editor.putString("sex", (Base64.encode(user.getUserSex().getBytes())).toString());
            editor.putString("birthday", (Base64.encode(user.getAvatar().getBytes())).toString());
            editor.putString("Email", (Base64.encode(user.getEmail().getBytes())).toString());
            editor.putString("IP", (Base64.encode(user.getIP().getBytes())).toString());
            editor.putString("address", (Base64.encode(user.getAddress().getBytes())).toString());
        } catch (Exception er) {
        }finally {
            editor.commit();// 提交修改
        }
    }

    public static void setAvatar(Context context, User user) {
        // 获取编辑器
        Editor editor = context.getSharedPreferences("USER", Activity.MODE_PRIVATE).edit();
        editor.putString("avatar", user.getAvatar());
        editor.commit();
    }

    public static void HandLockPassword(Context context) {
        boolean sign = context.getSharedPreferences(Constants.SP_NAME, 0).getBoolean(Constants.CECKBOX_KEY + Constants.userId, false);
        Constants.isAPPActive = context.getSharedPreferences(Constants.SP_NAME, 0).getBoolean(Constants.userId + "isAPPActive", true);
        if (!Constants.isAPPActive && sign) {
            Constants.isAPPActive = true;
            context.getSharedPreferences(Constants.SP_NAME, 0).edit().putBoolean(Constants.userId + "isAPPActive", true).commit();
            SharedPreferences preferences = context.getSharedPreferences(Constants.LOCK, Activity.MODE_PRIVATE);
            String patternString = preferences.getString(Constants.LOCK_KEY + Constants.userId, null);
            if (patternString == null || patternString.equals("")) {
                return;
            } else {
                Intent intent = new Intent(context, GestureVerifyActivity.class);
                intent.putExtra("edit", false);
                context.startActivity(intent);
            }
        }
    }

    public static boolean isForeground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        SharedPreferences preferences = context.getSharedPreferences(Constants.SP_NAME, 0);
        preferences.edit().putBoolean(Constants.userId + "isAPPActive", false).commit();
        return false;
    }

}
