package com.goodsurfing.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.Manifest;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.component.constants.What;
import com.goodsurfing.app.HaoUpApplication;
import com.goodsurfing.app.R;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.hcz.HczMainActivity;
import com.goodsurfing.main.MainActivity;
import com.goodsurfing.main.UserAgreementActivity;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author 谢志杰 E-mail: tanjie9012@163.com
 * @version 1.0.0
 * @description Activity相关操作 跳转,分辨率,版本号,版本名,设备token,dip转px,系统SDK版本
 * @create 2014-2-21 上午11:40:27
 */
public class ActivityUtil {

    public static final String TAG = "ActivityUtil";

    private static DisplayMetrics mDisplayMetrics;

    private static Dialog dlg;

    private static PopupWindow window;
    private static View view;
    private static boolean isStart;

    /**
     * 获取全局SharedPreferences
     *
     * @param context
     * @return
     * @author 谢志杰
     * @create 2014-7-9 下午5:29:07
     */
    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 启动activity-standard
     *
     * @param context
     * @param clazz
     */
    public static void startActivity(Context context, Class<? extends Activity> clazz) {
        Intent intent = new Intent(context, clazz);
        context.startActivity(intent);
    }

    /**
     * 启动activity-自定义启动方式
     *
     * @param context
     * @param clazz
     */
    public static void startActivity(Context context, Class<? extends Activity> clazz, int flags) {
        Intent intent = new Intent(context, clazz);
        intent.setFlags(flags);
        context.startActivity(intent);
    }

    /**
     * 启动activity并传值
     *
     * @param context
     * @param clazz
     * @param data
     */
    public static void startActivity(Context context, Class<? extends Activity> clazz, Bundle data) {
        Intent intent = new Intent(context, clazz);
        if (data != null && data.size() > 0)
            intent.putExtras(data);
        context.startActivity(intent);
    }

    /**
     * 启动activity并传值-standard
     *
     * @param context
     * @param clazz
     * @param data
     */
    public static void startActivity(Context context, Class<? extends Activity> clazz, Bundle data, int flags) {
        Intent intent = new Intent(context, clazz);
        intent.setFlags(flags);
        if (data != null && data.size() > 0)
            intent.putExtras(data);
        context.startActivity(intent);
    }

    /**
     * 启动activityForResult-standard
     *
     * @param context
     * @param clazz
     */
    public static void startActivityForResult(Context context, Class<? extends Activity> clazz, int requestCode) {
        Intent intent = new Intent(context, clazz);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    /**
     * 启动activity-standard
     *
     * @param context
     * @param clazz
     */
    public static void startActivityForResult(Context context, Class<? extends Activity> clazz, int requestCode, Bundle data) {
        Intent intent = new Intent(context, clazz);
        if (data != null && data.size() > 0) {
            intent.putExtras(data);
        }
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    /**
     * 获取手机分辨率
     *
     * @return DisplayMetrics
     */
    public static DisplayMetrics getScreenPixel(Context context) {
        if (mDisplayMetrics == null) {
            mDisplayMetrics = new DisplayMetrics();
            ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(mDisplayMetrics);
        }
        Log.e("DisplayMetrics", "分辨率：" + mDisplayMetrics.widthPixels + "*" + mDisplayMetrics.heightPixels + ",精度：" + mDisplayMetrics.density + ",densityDpi=" + mDisplayMetrics.densityDpi);
        return mDisplayMetrics;
    }

    /**
     * 获取当前应用版本序号
     *
     * @param context
     * @return 当前应用版本序号
     */
    public static int getVersionCode(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 获取当前应用版本名
     *
     * @param context
     * @return 当前应用版本名
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取手机号运营商
     */
    public static String getPhoneYunyin(String phone) {
        String dxPattern = "(189|153|133|180)[0-9]{8}";
        String ydPattern = "(134|136|138|151|157|187|135|137|139|152|158|188)[0-9]{8}";
        String ltPattern = "(130|132|185|156|131|186|170)[0-9]{8}";
        if (phone.matches(dxPattern)) {
            return "电信";
        }
        if (phone.matches(ydPattern)) {
            return "移动";
        }
        if (phone.matches(ltPattern)) {
            return "联通";
        }
        return "未知运营商";
    }

    /**
     * 获取当前应用包名
     *
     * @param context
     * @return 当前应用包名
     */
    public static String getPackegeName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.packageName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取手机系统版本
     *
     * @return
     */
    public static int getAndroidSDKVersion() {
        int version;
        try {
            version = android.os.Build.VERSION.SDK_INT;
        } catch (NumberFormatException e) {
            return 0;
        }
        return version;
    }

    /**
     * dp转px
     *
     * @param context
     * @param dpValue dp值
     * @return px
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 关闭软键盘
     */
    public static void closeKeyboard(Context context) {
        InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = ((Activity) context).getCurrentFocus();
        if (view != null) {
            IBinder binder = view.getApplicationWindowToken();
            if (binder != null && im != null && view != null) {
                im.hideSoftInputFromWindow(binder, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * 打开软键盘
     *
     * @param editText
     * @author 谢志杰
     * @time 2014-12-10下午02:38:54
     */
    public static void openKeyboard(EditText editText) {
        if (editText != null) {
            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
            editText.requestFocus();
            InputMethodManager inputManager = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(editText, 0);
        }
    }

    /**
     * 切换软键盘
     *
     * @param context
     * @return
     * @author 谢志杰
     * @time 2014-12-10下午02:45:44
     */
    public static void toggleKeyboard(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 获取设备token
     *
     * @return
     */
    public static String getDeviceToken(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "", Toast.LENGTH_LONG).show();
            return UUID.randomUUID().toString();
        }
        String tokenId = tm.getDeviceId();

        if (tokenId == null || tokenId.equals("")) {
            tokenId = tm.getSubscriberId();
        }
        if (tokenId != null && (tokenId.contains("000000") || tokenId.contains("111111") || tokenId.contains("222222") || tokenId.contains("333333") || tokenId.contains("444444") || tokenId.contains("555555") || tokenId.contains("666666") || tokenId.contains("777777") || tokenId.contains("888888") || tokenId.contains("999999"))) {
            BluetoothAdapter m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            tokenId = m_BluetoothAdapter.getAddress().replace(":", "HSW");
        }
        return tokenId;

    }

    /**
     * 组合方式获取一个设备唯一ID
     *
     * @param mContext
     * @return
     */
    public static final String getDeviceID(Context mContext) {
        String android_id = "";
        try {
            TelephonyManager telManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            @SuppressLint("MissingPermission") String mDeviceId = telManager.getDeviceId();
            String mAndroidId = Settings.System.getString(mContext.getContentResolver(), "android_id");

            android_id = (mDeviceId != null ? mDeviceId : "") + (mAndroidId != null ? mAndroidId : "");
            if (android_id.equals("")) {
                throw new RuntimeException("未获取唯一ID");
            }
        } catch (Exception e) {
            android_id = UUID.randomUUID().toString();
        }
        return android_id;
    }

    /**
     * 拨打电话
     *
     * @param context
     * @param phoneNumber
     * @author 谢志杰
     * @create 2014-7-24 下午5:05:10
     */
    public static void dialPhone(Context context, String phoneNumber) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.DIAL");
        intent.setData(Uri.parse("tel:" + phoneNumber));
        context.startActivity(intent);
    }

    // Android获取一个用于打开APK文件的intent
    public static Intent getApkFileIntent(String param) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        return intent;
    }

    // Android获取一个用于打开VIDEO文件的intent
    public static Intent getVideoFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "video/*");
        return intent;
    }

    // Android获取一个用于打开AUDIO文件的intent
    public static Intent getAudioFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "audio/*");
        return intent;
    }

    // Android获取一个用于打开Html文件的intent
    public static Intent getHtmlFileIntent(String param) {
        Uri uri = Uri.parse(param).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content").encodedPath(param).build();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(uri, "text/html");
        return intent;
    }

    // Android获取一个用于打开图片文件的intent
    public static Intent getImageFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "image/*");
        return intent;
    }

    // Android获取一个用于打开PPT文件的intent
    public static Intent getPptFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        return intent;
    }

    // Android获取一个用于打开Excel文件的intent
    public static Intent getExcelFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        return intent;
    }

    // Android获取一个用于打开Word文件的intent
    public static Intent getWordFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/msword");
        return intent;
    }

    // Android获取一个用于打开CHM文件的intent
    public static Intent getChmFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/x-chm");
        return intent;
    }

    // Android获取一个用于打开文本文件的intent
    public static Intent getTextFileIntent(String param, boolean paramBoolean) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (paramBoolean) {
            Uri uri1 = Uri.parse(param);
            intent.setDataAndType(uri1, "text/plain");
        } else {
            Uri uri2 = Uri.fromFile(new File(param));
            intent.setDataAndType(uri2, "text/plain");
        }
        return intent;
    }

    /**
     * 密码为6-16位非纯数字类型
     *
     * @return
     */
    public static boolean isPass(String pass) {
        return pass.matches("^(?![0-9]+$)(?![a-zA-Z]+$)(?![@#$%`!^&*_]+$)[0-9A-Za-z@#$%`!^&*_]{6,16}$");
    }

    // Android获取一个用于打开PDF文件的intent
    public static Intent getPdfFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/pdf");
        return intent;
    }

    /**
     * 手机标题栏高度
     *
     * @return
     * @description
     * @author 谢志杰
     * @create 2015年6月12日 上午11:12:30
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 获取当前格式化日期及时间
     *
     * @param format
     * @return
     * @author 谢志杰
     * @create 2014-9-23 下午3:50:48
     */
    public static String getDateTime(String format) {
        return new SimpleDateFormat(format, Locale.CHINA).format(System.currentTimeMillis());
    }

    /**
     * 验证手机号码
     *
     * @param mobiles
     * @return [0-9]{5,9}
     */
    public static boolean isMobileNO(String mobiles) {
        boolean flag = false;
        try {
            Pattern p = Pattern.compile("^[1][3-8]+\\d{9}");
            Matcher m = p.matcher(mobiles);
            flag = m.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    public static boolean sdCardExist() {
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            return true;
        }
        return false;
    }

    public static boolean checkFolder(File file) {
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }

    public static Dialog getDialog(Context context, String name, OnClickListener right, OnClickListener left) {
        final Dialog dlg = new Dialog(context, R.style.AlertDialogCustom);
        View view = View.inflate(context, R.layout.mode_change_dialog, null);

        final TextView title = (TextView) view.findViewById(R.id.activity_change_mode_tv);

        final TextView comfirm = (TextView) view.findViewById(R.id.activity_change_mode_comfirm_text);
        TextView mCancleTextView = (TextView) view.findViewById(R.id.activity_change_mode_cancle_text);
        title.setText(name);
        if (null != left) {
            mCancleTextView.setOnClickListener(left);
        } else {
            mCancleTextView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    dlg.dismiss();
                }
            });
        }
        if (null != right) {
            comfirm.setOnClickListener(right);
        } else {
            comfirm.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    dlg.dismiss();
                }
            });
        }
        dlg.setContentView(view);
        WindowManager m = ((Activity) context).getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dlg.getWindow().getAttributes(); // 获取对话框当前的参数值
        p.width = (int) (d.getWidth() * 0.65); // 宽度设置为屏幕的0.95
        dlg.getWindow().setAttributes(p);
        return dlg;
    }

    public static void showDialog(Context context, String name, String count) {
        if (context == null)
            return;
        if (dlg != null) {
            dlg.dismiss();
            dlg = null;
        }
        dlg = new Dialog(context, R.style.AlertDialogCustom);
        View view = View.inflate(context, R.layout.hint_dialog, null);

        final TextView title = (TextView) view.findViewById(R.id.activity_hint_title_tv);

        final TextView comfirm = (TextView) view.findViewById(R.id.activity_hint_tv_btn);
        TextView countv = (TextView) view.findViewById(R.id.activity_hint_count_tv);
        title.setText(name);
        countv.setText(count);
        comfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dlg.dismiss();
            }
        });
        dlg.setContentView(view);
        WindowManager m = ((Activity) context).getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dlg.getWindow().getAttributes(); // 获取对话框当前的参数值
        p.width = (int) (d.getWidth() * 0.65); // 宽度设置为屏幕的0.95
        dlg.getWindow().setAttributes(p);
        dlg.show();
    }

    public static void showPopWindow4Tips(Context context, View parent, boolean isSuccess, String count) {
        showPopWindow4Tips(context, parent, isSuccess, false, count, 2000);
    }

    public static void showPopWindow4Tips(Context context, View parent, boolean isSuccess, boolean showTabhost, String count, int showTime) {
        if (context == null)
            return;
        try {
            if (window != null) {
                view = window.getContentView();
            } else {
                view = View.inflate(context, R.layout.activity_head_log, null);
                window = new PopupWindow(view, LayoutParams.FILL_PARENT, context.getResources().getDimensionPixelSize(R.dimen.activity_tips_height), false);
            }
            int y=0;
            if (showTabhost) {
                y=context.getResources().getDimensionPixelSize(R.dimen.activity_head_title) + context.getResources().getDimensionPixelSize(R.dimen.activity_add_ll_head_45dp)+getStatusBarHeight(context);
            } else {
                y=context.getResources().getDimensionPixelSize(R.dimen.activity_head_title)+getStatusBarHeight(context);
            }
            window.showAtLocation(parent, Gravity.TOP | Gravity.START, 0,y);
            final TextView title = (TextView) view.findViewById(R.id.tv_top_msg);
            final ImageView left = (ImageView) view.findViewById(R.id.iv_top_left);
            if (showTime > 0) {
                left.setVisibility(View.VISIBLE);
                if (isSuccess) {
                    view.setBackgroundColor(Color.parseColor("#E5F7DC"));
                    left.setImageResource(R.drawable.top_title_tips_ok);
                } else {
                    view.setBackgroundColor(Color.parseColor("#FFEAC8"));
                    left.setImageResource(R.drawable.top_title_tips_failure);
                }
            } else {
                view.setBackgroundColor(Color.parseColor("#E5E5E5"));
                left.setImageResource(R.drawable.top_title_tips_ok);
                left.setVisibility(View.INVISIBLE);
            }
            title.setText(count);
            view.invalidate();
            if (showTime > 0) {
                ObjectAnimator anim = ObjectAnimator.ofFloat(view, "alpha", 1f, 1.0f);
                anim.setDuration(showTime);// 动画持续时间

                // 这里是一个回调监听，获取属性动画在执行期间的具体值
                anim.addListener(new AnimatorListener() {

                    @Override
                    public void onAnimationStart(Animator arg0) {
                        isStart = true;
                    }

                    @Override
                    public void onAnimationRepeat(Animator arg0) {

                    }

                    @Override
                    public void onAnimationEnd(Animator arg0) {
                        dismissPopWindow();
                    }

                    @Override
                    public void onAnimationCancel(Animator arg0) {
                        dismissPopWindow();
                    }
                });
                anim.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            dismissPopWindow();
        }
    }

    public static void dismissPopWindow() {
        try {
            isStart = false;
            if (window != null && window.isShowing()) {
                window.dismiss();
                window = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 埋点事件监听
     *
     * @param context
     * @param id
     * @param type
     * @param du
     */
    public static void sendEvent4UM(Context context, String id, String type, int du) {
        Map<String, String> m = new HashMap<String, String>();
        m.clear();
        m.put("type", type);
        MobclickAgent.onEvent(context, id, m);
        m = null;
    }

    /**
     * 显示 协议 弹窗
     */
    public static void showXyDialog(Context ctx) {
        startActivity(ctx, UserAgreementActivity.class);
    }

    /**
     * 密码强度
     *
     * @return Z = 字母 S = 数字 T = 特殊字符
     */
    public static int checkPassword(String passwordStr) {
        String regexZ = "\\d*";
        String regexS = "[a-zA-Z]+";
        String regexT = "\\W+$";
        String regexZT = "\\D*";
        String regexST = "[\\d\\W]*";
        String regexZS = "\\w*";
        String regexZST = "[\\w\\W]*";
        if (passwordStr == null || passwordStr.length() < 6) {
            return 1;
        }
        if (passwordStr.matches(regexZ)) {
            return 1;
        }
        if (passwordStr.matches(regexS)) {
            return 1;
        }
        if (passwordStr.matches(regexT)) {
            return 1;
        }
        if (passwordStr.matches(regexZT)) {
            return 2;
        }
        if (passwordStr.matches(regexST)) {
            return 2;
        }
        if (passwordStr.matches(regexZS)) {
            return 3;
        }
        if (passwordStr.matches(regexZST)) {
            return 4;
        }
        return 1;
    }

    /**
     * 判断qq是否可用
     *
     * @param context
     * @return
     */
    public static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void callQQkefu(Context context, View view) {
        if (isQQClientAvailable(context)) {
            final String qqUrl = "mqqwpa://im/chat?chat_type=wpa&uin=3471434847&version=1";
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(qqUrl)));
        } else {
            ActivityUtil.showPopWindow4Tips(context, view, false, "您还未安装QQ，请先安装");
        }
    }


    public static long[] getLong4Data(Calendar c) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.MILLISECOND));
        long[] temp = new long[2];
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        temp[0] = calendar.getTimeInMillis() / 1000;
        calendar.add(Calendar.DAY_OF_MONTH, +1);
        temp[1] = calendar.getTimeInMillis() / 1000;
        Log.i(TAG, "getLong4Data: " + ((temp[1] - temp[0]) / 60 / 60));
        calendar = null;
        return temp;
    }

    public static String getHours4Time(String date) {
        int last = date.lastIndexOf(":");
        int fast = date.lastIndexOf("-");
        return date.substring(fast + 4, last);
    }

    public static String getMoth4Time(String time) {
        int moth = Integer.parseInt(time.substring(5, 7));
        int day = Integer.parseInt(time.substring(8, 10));
        return moth + "月" + day + "日";
    }

    public static String getHHmm4Time(String time) {
        int tem = Integer.valueOf(time);
        int hours = tem / 3600;
        int minus = tem % 3600 / 60;
        int seconds = tem % 3600 % 60;
        String[] times = new String[3];
        times[0] = hours + "";
        if (minus / 10 == 0 && tem > 0 && hours != 0) {
            times[1] = "0" + minus;
        } else {
            times[1] = minus + "";
        }
        if (seconds / 10 == 0 && tem > 0 && Constants.isClockLocked && minus > 0) {
            times[2] = "0" + seconds;
        } else {
            times[2] = seconds + "";
        }
        String HhMm = "";
        if (!times[0].equals("0")) {
            HhMm = times[0] + "小时";
        }
        if (!times[1].equals("0")) {
            HhMm += times[1] + "分钟";
        }
        return HhMm;

    }


    /**
     * qq登录
     *
     * @param context
     */
    public void qqLogin(Context context, final Handler handler) {
        Tencent tt = Tencent.createInstance(Constants.QQ_API_KEY, context);
        tt.login((Activity) context, "all", new IUiListener() {
            @Override
            public void onComplete(Object response) {
                if (null == response) {
                    handler.sendEmptyMessage(What.HTTP_REQUEST_CURD_FAILURE);
                    return;
                }
                JSONObject jsonResponse = (JSONObject) response;
                if (null != jsonResponse && jsonResponse.length() == 0) {
                    handler.sendEmptyMessage(What.HTTP_REQUEST_CURD_FAILURE);
                    return;
                }
                try {
                    Constants.UserId = jsonResponse.getString("openid");
                    handler.sendEmptyMessage(What.HTTP_REQUEST_CURD_SUCCESS);
                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(What.HTTP_REQUEST_CURD_FAILURE);
                }
            }

            @Override
            public void onError(UiError uiError) {
                Log.i(TAG, "onComplete: " + uiError.toString());
                handler.sendEmptyMessage(What.HTTP_REQUEST_CURD_FAILURE);
            }

            @Override
            public void onCancel() {
                handler.sendEmptyMessage(What.HTTP_REQUEST_CURD_FAILURE);
            }
        });
    }

    /**
     * 微信登录
     */
    public void weixinLogin(final Context context, final Handler handler) {
        IWXAPI api = WXAPIFactory.createWXAPI(context, Constants.APP_ID, false);
        api.registerApp(Constants.APP_ID);
        if (!api.isWXAppInstalled()) {
            // 提醒用户没有按照微信
            Toast.makeText(context, "您没有安装微信,请先安装微信!", Toast.LENGTH_SHORT).show();
            return;
        }
        BroadcastReceiver receiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context c, Intent intent) {
                int type = intent.getIntExtra("type", 1);
                if (type == 0) {
                    new GetWechatUserInfoThread(context, handler).start();
                } else {
                    handler.sendEmptyMessage(What.HTTP_REQUEST_CURD_FAILURE);
                }
            }
        };
        context.registerReceiver(receiver, new IntentFilter(Constants.Login_BROAD));
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo_test";
        api.sendReq(req);
    }

    class GetWechatUserInfoThread extends Thread {

        private static final String TAG = "hezb";
        /**
         * 获取token等信息的地址 传递参数: appid, secret, code, grant_type =
         * authorization_code 返回参数：access_token, expires_in, refresh_token,
         * openid, scope, unionid
         */
        private static final String TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";

        /**
         * 获取用户信息的地址 传递参数：access_token, openid 返回参数：openid, nickname, sex,
         * province, city, country, headimgurl, privilege, unionid
         */
        private static final String USERINFO_URL = "https://api.weixin.qq.com/sns/userinfo";

        private Context context;
        private Handler mHandler;

        /**
         * @author hezb
         * @date 2015年5月28日下午2:26:23
         */
        public GetWechatUserInfoThread(Context context, Handler mHandler) {
            this.context = context;
            this.mHandler = mHandler;
        }

        @Override
        public void run() {
            super.run();
            try {
                String authorUrl = TOKEN_URL + "?appid=" + Constants.APP_ID + "&secret=" + Constants.WX_SECRET + "&code=" + Constants.WX_CODE + "&grant_type=" + "authorization_code";
                String jsonResult = getJsonResultByUrlPath(context, authorUrl);
                JSONObject jsonObject = new JSONObject(jsonResult);
                String accessToken = jsonObject.optString("access_token", "");
                String openid = jsonObject.optString("openid", "");
                if (!TextUtils.isEmpty(accessToken) && !TextUtils.isEmpty(openid)) {
                    String userInfoUrl = USERINFO_URL + "?access_token=" + accessToken + "&openid=" + openid;
                    String userInfo = getJsonResultByUrlPath(context, userInfoUrl);
                    if (!TextUtils.isEmpty(userInfo)) {
                        JSONObject userObject = new JSONObject(userInfo);
                        Constants.UserId = userObject.getString("openid");
                        Constants.UserName = userObject.getString("nickname");
                        Log.i(TAG, "onComplete: " + userObject.toString());
                        if (mHandler != null) {
                            mHandler.sendEmptyMessage(What.HTTP_REQUEST_CURD_SUCCESS);
                        }
                    }
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        /**
         * @throws IOException
         * @throws ClientProtocolException
         */
        private String getJsonResultByUrlPath(Context context, String urlPath) throws ClientProtocolException, IOException {

            String jsonResult = "";
            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(urlPath);
            HttpResponse response = client.execute(httpGet);
            InputStream inputStream = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while (null != (line = reader.readLine())) {
                jsonResult += line;
            }
            return jsonResult;
        }
    }

    public static void goMainActivity(Context context) {
        if (Constants.APP_USER_TYPE.equals(SharUtil.getService(context))) {
            context.startActivity(new Intent(context, HczMainActivity.class));
        } else {
            context.startActivity(new Intent(context, MainActivity.class));
        }

    }

    public static String getAPP4Time(int tem) {
        int minus = tem  / 60;
        if (minus>0) {
          return   minus + "分钟";
        } else {
          return  tem+ "秒";
        }
    }
    public static int getStatusBarHeight() {
        Class<?> c = null;

        Object obj = null;

        Field field = null;

        int x = 0, sbar = 0;

        try {

            c = Class.forName("com.android.internal.R$dimen");

            obj = c.newInstance();

            field = c.getField("status_bar_height");

            x = Integer.parseInt(field.get(obj).toString());

            sbar = HaoUpApplication.getInstance().getResources().getDimensionPixelSize(x);

        } catch (Exception e1) {

            e1.printStackTrace();

        }

        return sbar;
    }

}
