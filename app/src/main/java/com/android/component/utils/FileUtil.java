package com.android.component.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import com.android.component.constants.Constants;

/**
 * @description 文件操作工具类 新建根目录,子目录,删除文件,文件缓存目录
 * 
 * @author 谢志杰 E-mail: xzj2125123@163.com
 * @create 2016-8-13 上午09:35:28
 * @version 1.0.0
 * @company 湖南一石科技有限公司 Copyright: 版权所有 (c) 2016
 */
public class FileUtil {

	public static final String TAG = "FileUtil";

	public static final int WORD = 1;

	public static final int TXT = 2;

	public static final int EXCEL = 3;

	public static final int PPT = 4;

	public static final int PDF = 5;

	public static final int APK = 6;
	/**
	 * SD卡最小空闲大小,若低于此值则认为SD卡不可用，单位MB
	 */
	private static final int SDCARD_MIN_SIZE = 50;
	/**
	 * 文件复制缓存大小
	 */
	public static final int BUFFER_SIZE = 1024;

	/**
	 * 判断SD卡是否可用
	 * 
	 * @return true 挂载SD卡并且剩余空间大于SDCARD_MIN_SIZE，否则false
	 */
	public static boolean isSDCard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)
				&& SDCARD_MIN_SIZE <= getSDCardSize()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取SD卡剩余空间,单位MB
	 * 
	 * @return SD卡剩余空间,单位MB
	 */
	public static long getSDCardSize() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File file = Environment.getExternalStorageDirectory();
			StatFs statFs = new StatFs(file.getPath());
			// 获取单个数据块的大小(Byte)
			long blockSize = statFs.getBlockSize();
			// 空闲的数据块的数量
			long freeBlocks = statFs.getAvailableBlocks();
			// 返回SD卡空闲大小
			// long SDCardSize = freeBlocks * blockSize; //单位Byte
			// long SDCardSize = (freeBlocks * blockSize)/1024; //单位KB
			long SDCardSize = (freeBlocks * blockSize) / 1024 / 1024;
			return SDCardSize; // 单位MB
		}
		return 0;
	}

	/**
	 * 获取SD卡根目录
	 * 
	 * @return 如果挂载SD卡,返回SD卡根目录,否则返回null
	 */
	public static File getSDCard() {
		if (isSDCard()) {
			return Environment.getExternalStorageDirectory();
		}
		return null;
	}

	/**
	 * 获取SD卡私有目录
	 * 
	 * @return 如果挂载SD卡,返回SD卡私有目录,否则返回null
	 */
	public static File getSDCardCache(Context context) {
		if (isSDCard()) {
			return context.getExternalCacheDir();
		}
		return null;
	}

	/**
	 * 获取手机内存根目录
	 * 
	 * @param context
	 * @return 返回手机内存根目录
	 */
	public static File getCacheDir(Context context) {
		return context.getCacheDir();
	}

	/**
	 * 获取并创建项目存储目录,优先创建至SD卡
	 * 
	 * @param context
	 * @param path
	 *            子目录名
	 * @return 项目资源存储的path目录
	 */
	public static File getFileDir(Context context, String dirName) {
		return getFileDir(context, dirName, true, false);
	}

	/**
	 * 获取并创建项目存储目录,优先创建至SD卡
	 * 
	 * @param context
	 * @param path
	 *            子目录名
	 * @return 项目资源存储的path目录
	 */
	public static File getFileDir(Context context, String dirName,
			boolean isDataDir) {
		return getFileDir(context, dirName, true, isDataDir);
	}

	/**
	 * 获取并创建项目存储目录,指定是否创建至SD卡
	 * 
	 * @param context
	 * @param path
	 *            子目录名
	 * @param isSDCard
	 *            true 若SD卡挂载则创建至SD卡,未挂载则创建至手机内存。false 直接创建至手机内存
	 * @return 项目资源存储的path目录
	 */
	public static File getFileDir(Context context, String dirName,
			boolean isSDCard, boolean isDataDir) {
		File rootDir = null;
		if (isSDCard) {
			if (isDataDir)
				rootDir = getSDCardCache(context);
			else
				rootDir = getSDCard();
			if (rootDir == null) {
				rootDir = getCacheDir(context);
			}
		} else {
			rootDir = getCacheDir(context);
		}
		if (dirName == null)
			return rootDir;
		File fileDir = new File(rootDir, dirName);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		return fileDir;
	}

	/**
	 * 获取并创建项目资源存储子目录
	 * 
	 * @param parentsFile
	 *            父目录
	 * @param path
	 *            子目录
	 * @return 资源存储子目录
	 */
	public static File getFileDir(File parentsDir, String dirName) {
		File fileDir = new File(parentsDir, dirName);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		return fileDir;
	}

	/**
	 * 获取缓存文件保存位置
	 * 
	 * @author 谢志杰
	 * @create 2015-6-27 下午4:15:42
	 * @param context
	 * @return
	 */
	public static File getFileCacheDir(Context context) {
		File rootFile = getFileDir(context, null, true);
		return getFileDir(rootFile, Constants.FILE_CACHE_DIR);
	}

	/**
	 * 获取缓存文件保存位置
	 * 
	 * @author 谢志杰
	 * @create 2015-6-27 下午4:15:42
	 * @param context
	 * @param isDataDir
	 * @return
	 */
	public static File getFileCacheDir(Context context, boolean isDataDir) {
		File rootFile = null;
		if (isDataDir) {
			rootFile = getFileDir(context, null, true);
		} else {
			String path = ActivityUtil.getPackegeName(context);
			if (StringUtil.isEmpty(path)) {
				path = ActivityUtil.getPackegeName(context);
			}
			rootFile = getFileDir(context, path, false);
		}
		return getFileDir(rootFile,Constants.FILE_CACHE_DIR);
	}

	/**
	 * 获取缓存文件保存位置
	 * 
	 * @author 谢志杰
	 * @create 2015-6-27 下午4:15:42
	 * @param context
	 * @param dirName
	 * @param isDataDir
	 * @return
	 */
	public static File getFileCacheDir(Context context, String dirName,
			boolean isDataDir) {
		return FileUtil
				.getFileDir(getFileCacheDir(context, isDataDir), dirName);
	}

	/**
	 * 获取缓存文件保存位置
	 * 
	 * @author 谢志杰
	 * @create 2015-6-27 下午4:15:42
	 * @param context
	 * @param dirName
	 * @return
	 */
	public static File getFileCacheDir(Context context, String dirName) {
		return FileUtil.getFileDir(getFileCacheDir(context, false), dirName);
	}

	/**
	 * 文件复制
	 * 
	 * @param is
	 *            复制源文件流
	 * @param os
	 *            复制目标文件流
	 */
	public static void copyFile(InputStream is, OutputStream os) {
		try {
			byte[] buffer = new byte[BUFFER_SIZE];
			while (true) {
				int count = is.read(buffer, 0, BUFFER_SIZE);
				if (count == -1)
					break;
				os.write(buffer, 0, count);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null)
					is.close();
				if (os != null)
					os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 删除文件
	 * 
	 * @param file
	 */
	public static void deleteFile(File file) {
		if (file == null || !file.exists()) {
			return;
		}
		if (file.isFile()) {
			file.delete();
			return;
		}
		if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				file.delete();
				return;
			}
			for (int i = 0; i < childFiles.length; i++) {
				FileUtil.deleteFile(childFiles[i]);
			}
			file.delete();
		}
	}

	/**
	 * 获取文件类型,支持word,txt,excel,ppt,pdf,apk等类型
	 * 
	 * @author 谢志杰
	 * @create 2015-9-15 下午2:40:21
	 * @param file
	 * @return
	 */
	public static int getFileType(File file) {
		if (file.isDirectory()) {
			return -1;
		}
		String fileName = file.getName();
		int index = fileName.lastIndexOf(".");
		if (index == -1) {
			return -1;
		}
		String ex = fileName.substring(index);
		if (ex.equalsIgnoreCase(".xls") || ex.equalsIgnoreCase(".xlsx")) {
			return EXCEL;
		} else if (ex.equalsIgnoreCase(".doc") || ex.equalsIgnoreCase(".docx")) {
			return WORD;
		} else if (ex.equalsIgnoreCase(".ppt") || ex.equalsIgnoreCase(".pptx")) {
			return PPT;
		} else if (ex.equalsIgnoreCase(".pdf")) {
			return PDF;
		} else if (ex.equalsIgnoreCase(".txt")) {
			return TXT;
		} else if (ex.contains(".apk") || ex.contains(".APK")) {
			return APK;
		}
		return -1;
	}

	/**
	 * 打开文件
	 * 
	 * @param context
	 * @param file
	 * 
	 * @author 谢志杰
	 * @time 2014-10-9上午09:24:32
	 */
	public static void openFile(Context context, File file) {
		String path = file.getAbsolutePath();
		int type = FileUtil.getFileType(file);
		if (type == FileUtil.EXCEL) {
			context.startActivity(ActivityUtil.getExcelFileIntent(path));
		} else if (type == FileUtil.WORD) {
			context.startActivity(ActivityUtil.getWordFileIntent(path));
		} else if (type == FileUtil.PPT) {
			context.startActivity(ActivityUtil.getPptFileIntent(path));
		} else if (type == FileUtil.PDF) {
			context.startActivity(ActivityUtil.getPdfFileIntent(path));
		} else if (type == FileUtil.TXT) {
			context.startActivity(ActivityUtil.getTextFileIntent(path, false));
		} else if (type == FileUtil.APK) {
			context.startActivity(ActivityUtil.getApkFileIntent(path));
		}
	}
}
