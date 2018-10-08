/*******************************************************************************
 * Copyright 2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.nostra13.universalimageloader.core.imageaware;

import java.io.File;
import java.lang.reflect.Field;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.assist.ViewScaleType;
import com.nostra13.universalimageloader.utils.L;
import com.nostra13.universalimageloader.utils.PhotoProcessing;

/**
 * Wrapper for Android {@link android.widget.ImageView ImageView}. Keeps weak
 * reference of ImageView to prevent memory leaks.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.9.0
 */
public class ImageViewAware extends ViewAware {

	/**
	 * Constructor. <br />
	 * References {@link #ImageViewAware(android.widget.ImageView, boolean)
	 * ImageViewAware(imageView, true)}.
	 * 
	 * @param imageView
	 *            {@link android.widget.ImageView ImageView} to work with
	 */
	public ImageViewAware(ImageView imageView) {
		super(imageView);
	}

	/**
	 * Constructor
	 * 
	 * @param imageView
	 *            {@link android.widget.ImageView ImageView} to work with
	 * @param checkActualViewSize
	 *            <b>true</b> - then {@link #getWidth()} and
	 *            {@link #getHeight()} will check actual size of ImageView. It
	 *            can cause known issues like <a href=
	 *            "https://github.com/nostra13/Android-Universal-Image-Loader/issues/376"
	 *            >this</a>. But it helps to save memory because memory cache
	 *            keeps bitmaps of actual (less in general) size.
	 *            <p/>
	 *            <b>false</b> - then {@link #getWidth()} and
	 *            {@link #getHeight()} will <b>NOT</b> consider actual size of
	 *            ImageView, just layout parameters. <br />
	 *            If you set 'false' it's recommended 'android:layout_width' and
	 *            'android:layout_height' (or 'android:maxWidth' and
	 *            'android:maxHeight') are set with concrete values. It helps to
	 *            save memory.
	 *            <p/>
	 */
	public ImageViewAware(ImageView imageView, boolean checkActualViewSize) {
		super(imageView, checkActualViewSize);
	}

	/**
	 * {@inheritDoc} <br />
	 * 3) Get <b>maxWidth</b>.
	 */
	@Override
	public int getWidth() {
		int width = super.getWidth();
		if (width <= 0) {
			ImageView imageView = (ImageView) viewRef.get();
			if (imageView != null) {
				width = getImageViewFieldValue(imageView, "mMaxWidth"); // Check
																		// maxWidth
																		// parameter
			}
		}
		return width;
	}

	/**
	 * {@inheritDoc} <br />
	 * 3) Get <b>maxHeight</b>
	 */
	@Override
	public int getHeight() {
		int height = super.getHeight();
		if (height <= 0) {
			ImageView imageView = (ImageView) viewRef.get();
			if (imageView != null) {
				height = getImageViewFieldValue(imageView, "mMaxHeight"); // Check
																			// maxHeight
																			// parameter
			}
		}
		return height;
	}

	@Override
	public ViewScaleType getScaleType() {
		ImageView imageView = (ImageView) viewRef.get();
		if (imageView != null) {
			return ViewScaleType.fromImageView(imageView);
		}
		return super.getScaleType();
	}

	@Override
	public ImageView getWrappedView() {
		return (ImageView) super.getWrappedView();
	}

	@Override
	protected void setImageDrawableInto(Drawable drawable, View view, String path) {
		((ImageView) view).setImageDrawable(drawable);
	}

	@Override
	protected void setImageBitmapInto(Bitmap bitmap, View view, String path) {

		if (null != bitmap && (bitmap.getHeight() > 4000 || bitmap.getWidth() > 4000)) {
			try {
				if (path != null && path.startsWith("http://")) {
					Bitmap bmp = PhotoProcessing.makeBitmapMutable(bitmap);
					if (bmp != null) {
						((ImageView) view).setImageBitmap(bmp);
					}
				} else {
					Bitmap mp = PhotoProcessing.loadPhoto(path);
					((ImageView) view).setImageBitmap(mp);
				}
			} catch (OutOfMemoryError e) {
				if (path.startsWith("file://")) {
					path = path.substring(7);
					Bitmap bmp = fitSizeImg(path);
					if (bmp != null) {
						((ImageView) view).setImageBitmap(bmp);
					} else {
						((ImageView) view).setImageBitmap(bitmap);

					}
				}
			}
		} else {
			((ImageView) view).setImageBitmap(bitmap);
		}
	}

	public static Bitmap fitSizeImg(String path) {
		if (path == null || path.length() < 1)
			return null;
		File file = new File(path);
		Bitmap resizeBmp = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		// 数字越大读出的图片占用的heap越小 不然总是溢出
		if (file.length() < 20480) { // 0-20k
			opts.inSampleSize = 1;
		} else if (file.length() < 51200) { // 20-50k
			opts.inSampleSize = 2;
		} else if (file.length() < 307200) { // 50-300k
			opts.inSampleSize = 4;
		} else if (file.length() < 819200) { // 300-800k
			opts.inSampleSize = 6;
		} else if (file.length() < 1048576) { // 800-1024k
			opts.inSampleSize = 8;
		} else {
			opts.inSampleSize = 10;
		}
		try {
			resizeBmp = BitmapFactory.decodeFile(file.getPath(), opts);
		} catch (OutOfMemoryError e) {
		}
		return resizeBmp;
	}

	public static Bitmap decodeSampledBitmapFromResource(byte[] data, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, 0, data.length, options);
		// BitmapFactory.decodeResource(res, resId, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeByteArray(data, 0, data.length, options);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			} else {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			}
		}
		return inSampleSize;
	}

	private static int getImageViewFieldValue(Object object, String fieldName) {
		int value = 0;
		try {
			Field field = ImageView.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			int fieldValue = (Integer) field.get(object);
			if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
				value = fieldValue;
			}
		} catch (Exception e) {
			L.e(e);
		}
		return value;
	}
}
