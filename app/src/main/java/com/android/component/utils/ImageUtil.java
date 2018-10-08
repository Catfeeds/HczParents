package com.android.component.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore.MediaColumns;

/**
 * @description 图片处理工具类
 * 
 * @author 谢志杰 E-mail: xzj2125123@163.com
 * @create 2016-8-21 上午10:26:26
 * @version 1.0.0
 * @company 湖南一石科技有限公司 Copyright: 版权所有 (c) 2016
 */
public class ImageUtil {

	public static final String TAG = "ImageUtil";

	/**
	 * 压缩图片宽
	 */
	private static final int DEFALUT_IMAGE_SIZE = 1024;

	/**
	 * 缩放图片
	 * 
	 * @param bitmap
	 * @param zoomF
	 * @return
	 */
	public static Bitmap zoom(Bitmap bitmap, float zoomF) {
		Matrix matrix = new Matrix();
		matrix.postScale(zoomF, zoomF);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
	}

	/**
	 * 缩放图片
	 * 
	 * @param bitmap
	 * @param widthF
	 * @param heightF
	 * @return
	 */
	public static Bitmap zoom(Bitmap bitmap, float widthF, float heightF) {
		Matrix matrix = new Matrix();
		matrix.postScale(widthF, heightF);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
	}

	/**
	 * 获取图片角度信息
	 * 
	 * @param path
	 * @return
	 */
	public static int getImageDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/**
	 * 图片旋转
	 * 
	 * @param degree
	 * @param bitmap
	 * @return
	 */
	public static Bitmap rotaingImage(int degree, Bitmap bitmap) {
		if (bitmap != null) {
			try {
				// 旋转图片 动作
				Matrix matrix = new Matrix();
				matrix.postRotate(degree);
				// 创建新的图片
				Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
						bitmap.getWidth(), bitmap.getHeight(), matrix, true);
				return resizedBitmap;
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				return bitmap;
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				return bitmap;
			} catch (Exception e) {
				e.printStackTrace();
				return bitmap;
			}
		}
		return null;
	}

	/**
	 * 读取本地图片，进行压缩
	 * 
	 * @param path
	 *            图片路径
	 * @return
	 */
	public static Bitmap compressImage(String path) {
		return compressImage(path, DEFALUT_IMAGE_SIZE);
	}

	/**
	 * 读取本地图片,进行压缩
	 * 
	 * @param path
	 *            图片路径
	 * @param size
	 *            图片的压缩宽
	 * @return
	 */
	public static Bitmap compressImage(String path, int size) {
		try {
			BufferedInputStream bis = new BufferedInputStream(
					new FileInputStream(new File(path)));
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(bis, null, options);
			bis.close();
			final int requiredSize = (size > 0 ? size : DEFALUT_IMAGE_SIZE);
			Bitmap bitmap = null;
			int scale = 1;
			while (true) {
				if ((options.outWidth >> scale <= requiredSize)
						&& (options.outHeight >> scale <= requiredSize)) {
					options.inSampleSize = (int) Math.pow(2.0D, scale);
					options.inPreferredConfig = Bitmap.Config.RGB_565;
					options.inJustDecodeBounds = false;
					BufferedInputStream is = new BufferedInputStream(
							new FileInputStream(new File(path)));
					bitmap = BitmapFactory.decodeStream(is, null, options);
					is.close();
					break;
				}
				scale += 1;
			}
			return bitmap;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// try {
			// if (bis != null)
			// bis.close();
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
		}
		return null;
	}

	/**
	 * 绘制圆角图片 radius小于0时绘制圆形图片
	 * 
	 * @param image
	 * @param background
	 *            外背景
	 * @return
	 */
	public static Bitmap createFramedImage(Bitmap image, Bitmap background,
			int radius) {
		if (image == null) {
			return null;
		}
		int width = 100;
		int height = 100;
		if (background != null) {
			width = background.getWidth();
			height = background.getHeight();
		}
		int roundWidth = radius;
		int roundHeight = radius;
		if (radius < 0) {
			roundWidth = width / 2;
			roundHeight = height / 2;
		}
		// 根据源文件新建一个darwable对象
		Bitmap bitmap = null;
		// 新建一个新的输出图片
		Bitmap output = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		int offset = image.getHeight() - image.getWidth();
		if (offset > 0) {// 高大于宽
			bitmap = Bitmap.createBitmap(image, 0, offset / 2,
					image.getWidth(), image.getWidth());
		} else if (offset < 0) {// 宽大于高
			bitmap = Bitmap.createBitmap(image, Math.abs(offset) / 2, 0,
					image.getHeight(), image.getHeight());
		} else {
			bitmap = image;
		}
		Canvas canvas = new Canvas(output);
		// 新建一个矩形
		RectF outerRect = new RectF(0, 0, width, height);
		// 产生一个红色的圆角矩形
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setAntiAlias(true);
		canvas.drawRoundRect(outerRect, roundWidth, roundHeight, paint);
		// 抗锯齿
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
				| Paint.FILTER_BITMAP_FLAG));
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		// 将源图片绘制到这个圆角矩形上
		if (image != null) {
			float scaleX = Float.parseFloat(width + "") / bitmap.getHeight();
			float scaleY = Float.parseFloat(height + "") / bitmap.getWidth();
			Matrix matrix = new Matrix();
			matrix.postScale(scaleX, scaleY);
			Bitmap sizeBm = Bitmap.createBitmap(bitmap, 0, 0,
					bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			canvas.drawBitmap(sizeBm, 0, 0, paint);
		}
		if (background != null) {
			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
			canvas.drawBitmap(background, 0, 0, paint);
		}
		return output;
	}

	/**
	 * 获取图片
	 * 
	 * @param data
	 * @return 图片本地路径
	 */
	public static String getImageForIntent(Context context, Intent data) {
		String imagePath = null;
		if (data != null) {
			Uri uri = data.getData();
			if (StringUtil.isEmpty(uri.getAuthority())) {
				imagePath = uri.getPath();
			} else {
				Cursor cursor = context.getContentResolver().query(uri,
						new String[] { MediaColumns.DATA }, null, null, null);
				if (cursor == null)
					return null;
				cursor.moveToFirst();
				imagePath = cursor.getString(cursor
						.getColumnIndex(MediaColumns.DATA));
				cursor.close();
			}
		}
		return imagePath;
	}

	/**
	 * 获取视频略缩图
	 * 
	 * @param context
	 * @param path
	 * @return
	 */
	@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
	@SuppressLint("NewApi")
	public static Bitmap getVideoImage(Context context, String path) {
		Bitmap bitmap = null;
		try {
			if (path != null) {
				MediaMetadataRetriever retriever = new MediaMetadataRetriever();
				retriever.setDataSource(path);
				bitmap = retriever.getFrameAtTime();
			}
		} catch (Exception e) {
			return null;
		}
		return bitmap;
	}

	public static Bitmap readBitMap(Resources resources, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		InputStream is = resources.openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

	public static ImageType getImageType(File imageFile) {
		FileInputStream fis = null;
		ImageType imageType = null;
		try {
			fis = new FileInputStream(imageFile);
			byte[] buffer = new byte[20];
			fis.read(buffer, 0, 20);
			byte b0 = buffer[0];
			byte b1 = buffer[1];
			byte b2 = buffer[2];
			byte b3 = buffer[3];
			byte b6 = buffer[6];
			byte b7 = buffer[7];
			byte b8 = buffer[8];
			byte b9 = buffer[9];
			// GIF
			if (b0 == (byte) 'G' && b1 == (byte) 'I' && b2 == (byte) 'F')
				imageType = ImageType.GIF;
			// PNG
			else if (b1 == (byte) 'P' && b2 == (byte) 'N' && b3 == (byte) 'G')
				imageType = ImageType.PNG;
			// JPG
			else if (b6 == (byte) 'J' && b7 == (byte) 'F' && b8 == (byte) 'I'
					&& b9 == (byte) 'F')
				imageType = ImageType.JPG;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return imageType;
	}

	public static boolean isGif(File imageFile) {
		if (getImageType(imageFile) == ImageType.GIF) {
			return true;
		}
		return false;
	}

	public enum ImageType {

		GIF("gif"), JPG("jpg"), PNG("png");

		private String imageType;

		private ImageType(String imageType) {
			this.imageType = imageType;
		}

		public String getImageType() {
			return imageType;
		}
	}
}
