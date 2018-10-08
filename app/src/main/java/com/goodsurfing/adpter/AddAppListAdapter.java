package com.goodsurfing.adpter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.component.utils.ImageUtil;
import com.goodsurfing.app.R;
import com.goodsurfing.beans.WebFilterBean;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.utils.ActivityUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class AddAppListAdapter extends ArrayAdapter<WebFilterBean> {

	private int itemId;
	private static boolean isAllSelect = false;
	private LayoutInflater mInflater;
	private ViewHolder holder = null;
	private OnClickListener listener;

	public static interface SelectDelegate {
		void selectItem(int position, boolean isSelected);

		void startWebControl(int position, String status);
	}

	private SelectDelegate mDelegate;

	public AddAppListAdapter(Context context, int textViewResourceId, List<WebFilterBean> objects, OnClickListener listener, boolean flag) {
		super(context, textViewResourceId, objects);
		this.mInflater = LayoutInflater.from(context);
		this.itemId = textViewResourceId;
		this.listener = listener;
	}

	@Override
	public int getCount() {
		return super.getCount();
	}

	@Override
	public long getItemId(int position) {
		return super.getItemId(position);
	}

	@Override
	public WebFilterBean getItem(int position) {
		return super.getItem(position);
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(itemId, null);
			holder.icon = (ImageView) convertView.findViewById(R.id.item_web_filter_web_icon);
			holder.title = (TextView) convertView.findViewById(R.id.item_web_filter_name);
			holder.type = (TextView) convertView.findViewById(R.id.item_web_filter_type);
			holder.status = (TextView) convertView.findViewById(R.id.item_web_filter_status);
			holder.iv = (ImageView) convertView.findViewById(R.id.item_web_filter_web_iv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final WebFilterBean Bean = getItem(position);
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Bean.isSelected()) {
					holder.iv.setImageResource(R.drawable.ic_undelete);
					Bean.setSelected(false);
					if (null != mDelegate) {
						mDelegate.selectItem(position, false);
					}
				} else {
					holder.iv.setImageResource(R.drawable.ic_selected);
					Bean.setSelected(true);
					if (null != mDelegate) {
						mDelegate.selectItem(position, true);
					}
				}

			}
		});
		ImageAware imageAware = new ImageViewAware(holder.icon, false);
		holder.icon.setTag(Constants.APP_ICON_SERVER_URL + Bean.getIcon());
		ImageLoader.getInstance().displayImage(Constants.APP_ICON_SERVER_URL + Bean.getIcon(), imageAware, new ImageLoadingListener() {
			
			@Override
			public void onLoadingStarted(String imageUri, View view) {
			}
			
			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				if(imageUri.equals(holder.icon.getTag())){
					holder.icon.setImageResource(R.drawable.add_app_deful_icon);
				}
			}
			
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				if(imageUri.equals(holder.icon.getTag())){
					holder.icon.setImageBitmap(loadedImage);
				}
				
			}
			
			@Override
			public void onLoadingCancelled(String imageUri, View view) {
			}
		});
		holder.title.setText(Bean.getWebTitle());
		holder.type.setText(Bean.getWebTye());
		// 各个信息填入 1表示已经添加，2表示未添加
		if ("1".equals(Bean.getWebStatus())) {
			holder.status.setVisibility(View.VISIBLE);
			holder.iv.setVisibility(View.GONE);
			holder.title.setTextColor(0xffa1a1a1);
		} else {
			holder.status.setVisibility(View.GONE);
			holder.iv.setVisibility(View.VISIBLE);
			holder.title.setTextColor(0xff000000);
			if (Bean.isSelected()) {
				holder.iv.setImageResource(R.drawable.ic_selected);
			} else {
				holder.iv.setImageResource(R.drawable.ic_undelete);
			}
		}
		return convertView;
	}

	public final class ViewHolder {
		public ImageView iv;
		public TextView title;
		public TextView type;
		public TextView status;
		public ImageView icon;
	}

	public void setDelegate(SelectDelegate delegate) {
		mDelegate = delegate;
	}

	public void setSelectALL(boolean isSelectAll) {
		isAllSelect = isSelectAll;
	}

	public static boolean getSelectAllFlag() {
		return isAllSelect;
	}
}
