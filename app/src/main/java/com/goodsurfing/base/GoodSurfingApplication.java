package com.goodsurfing.base;
import java.io.File;

import android.app.Application;
import android.os.Environment;
import android.os.Vibrator;

import com.goodsurfing.constants.Constants;
import com.goodsurfing.utils.ActivityUtil;

public class GoodSurfingApplication extends Application {
	
	public static String keyWord = "";
	public static int versionCode = 1;
	public static String apkDownloadUrl = "";
	
	public Vibrator mVibrator;
	
	@Override
	public void onCreate() {
		super.onCreate();
		initFilepath();
	}
	public void initFilepath() {
		if (ActivityUtil.sdCardExist()) {
			File file = new File(Environment.getExternalStorageDirectory(), "jdyg");
			if (!ActivityUtil.checkFolder(file)) {
				file.mkdirs();
			}
			File filei = new File(file, "jdyg");
			if (!ActivityUtil.checkFolder(filei)) {
				filei.mkdirs();
			}
			Constants.IMG_DIR = filei;
		}
	}
	
}
