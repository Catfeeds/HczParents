package com.goodsurfing.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.goodsurfing.app.R;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.server.UpDataManagerServer;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResponder;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResult;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.utils.LogUtil;

/**
 * 
 * @ClassName: UpdateManager
 * @Description: 版本更新管理
 * @version: 1.0
 * @Create: 2015-5-5 14:23
 */
@SuppressLint("HandlerLeak")
public class UpdateManager {
	private Context mContext;
	private static String savePath = "/hsw/download/";
	private static String saveFileName = "";
	private static final int DOWN_UPDATE = 1;
	private static final int DOWN_OVER = 2;
	private File downloadFile;


	public UpdateManager(Context context) {
		this.mContext = context;
		receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)&&intent.getPackage().equals("com.goodsurfing.app")) {
					installApk();
				}
			}
		};
	}

	BroadcastReceiver receiver;


	/**
	 * 
	 * @param serverVersionName
	 * @Title: showNoticeDialog
	 * @Description: 检测有新版本，提示更新窗口
	 * @date 2015-5-5 14:23
	 */
	private void showNoticeDialog(String serverVersionName, String about) {
		final Dialog dialog = new Dialog(mContext, R.style.AlertDialogCustom);
		View view = View.inflate(mContext, R.layout.layout_kefu_dialog, null);
		TextView leftView = (TextView) view.findViewById(R.id.layout_kefu_left);
		TextView rightView = (TextView) view.findViewById(R.id.layout_kefu_right);
		TextView content = (TextView) view.findViewById(R.id.layout_content_kefu);
		TextView title = (TextView) view.findViewById(R.id.layout_title_kefu);
		title.setText("发现新版本" + serverVersionName);
		content.setText(about);
		leftView.setText("忽略");
		rightView.setText("更新");
		leftView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		rightView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				downloadApk();
			}
		});
		dialog.setContentView(view);
		WindowManager m = ((Activity) mContext).getWindowManager();
		Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); // 获取对话框当前的参数值
		p.width = (int) (d.getWidth() * 0.65); // 宽度设置为屏幕的0.95
		dialog.getWindow().setAttributes(p);
		dialog.show();
	}

	protected String downUrl;
	protected Uri apkUri;

	public void detectionVersion(final Context context) {
		if (!Constants.isNetWork) {
			return;
		}
		String url = Constants.SERVER_URL_GLOBAL + "?requesttype=1006&apptype=2";

		new UpDataManagerServer(new DataServiceResponder() {

			@Override
			public void onResult(DataServiceResult result) {
				if (result == null || result.code == null) {
					return;
				}
				if ("0".equals(result.code)) {
					String name = result.action;
					downUrl = result.extra;
					String about = result.extra1;
					if (!name.equals("")) {
						float version = Float.parseFloat(name);
						float oldVersion = Float.parseFloat(ActivityUtil.getVersionName(mContext));
						if (version > oldVersion) {
							showNoticeDialog(name, about);
						}
					}
				}
			}

			@Override
			public void onFailure() {

			}
		}, url, context).execute();
	}

	/**
	 * 下载apk
	 * 
	 */

	private void downloadApk() {
		// 下载路径，如果路径无效了，可换成你的下载路径
		if ((Environment.getExternalStorageState() != Environment.MEDIA_REMOVED) || (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED)) {
			savePath = Environment.getExternalStorageDirectory().getPath() + savePath;
		}
		saveFileName = savePath + "HCZChild.apk";
		downloadFile = new File(saveFileName);
		if (downloadFile != null && downloadFile.exists() && downloadFile.isFile()) {
			downloadFile.delete();
		}
		// 创建下载任务,downloadUrl就是下载链接
		DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downUrl));
		// 指定下载路径和下载文件名

		apkUri = Uri.fromFile(downloadFile);
		request.setDestinationUri(apkUri);
		request.setMimeType("application/vnd.android.package-archive"); //修改
		request.setDestinationUri(Uri.fromFile(downloadFile)); //修改
		request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		request.setTitle("下载新版本");
		request.setVisibleInDownloadsUi(true);
		// 获取下载管理器
		DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
		// 将下载任务加入下载队列，否则不会进行下载
		downloadManager.enqueue(request);
		mContext.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
	}

	/**
	 * 安装apk
	 */
	private void installApk() {
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
			Uri uri = FileProvider.getUriForFile(mContext, "com.goodsurfing.app.fileprovider", downloadFile); //修改  downloadFile 来源于上面下载文件时保存下来的
			//  BuildConfig.APPLICATION_ID + ".fileprovider" 是在manifest中 Provider里的authorities属性定义的值
			Intent installIntent = new Intent(Intent.ACTION_VIEW);
			installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //临时授权
			installIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
			installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
			mContext.startActivity(installIntent);
		} else {
			// 提示用户安装
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.setDataAndType(apkUri, "application/vnd.android.package-archive");
			mContext.startActivity(i);
		}
	}
}
