package com.goodsurfing.adpter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.goodsurfing.app.R;
import com.goodsurfing.beans.WebFilterBean;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.main.AllowedWebsListActivity;
import com.goodsurfing.main.UnallowedWebsListActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class BlackWhiteWebListAdapter extends ArrayAdapter<WebFilterBean> {

	private int itemId;
	private static boolean isAllSelect = false;
	private LayoutInflater mInflater;
	private ViewHolder holder = null;
	private OnClickListener listener;
	private boolean isWhiteWebList;

	public static interface SelectDelegate {
		void selectItem(int position, boolean isSelected);

		void startWebControl(int position, String status);
	}

	private SelectDelegate mDelegate;

	public BlackWhiteWebListAdapter(Context context, int textViewResourceId, List<WebFilterBean> objects, OnClickListener listener, boolean flag) {
		super(context, textViewResourceId, objects);
		this.mInflater = LayoutInflater.from(context);
		this.itemId = textViewResourceId;
		this.listener = listener;
		this.isWhiteWebList = flag;
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
			holder.title = (TextView) convertView.findViewById(R.id.item_web_filter_name);
			holder.type = (TextView) convertView.findViewById(R.id.item_web_filter_type);
			holder.webs = (TextView) convertView.findViewById(R.id.item_web_filter_web);
			holder.icon = (ImageView) convertView.findViewById(R.id.item_web_filter_web_icon);
			holder.status = (CheckBox) convertView.findViewById(R.id.item_web_filter_status);
			holder.delete = (LinearLayout) convertView.findViewById(R.id.llayout_right);
			holder.ll_info = (LinearLayout) convertView.findViewById(R.id.ll_info);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.iv = (ImageView) convertView.findViewById(R.id.item_web_filter_web_iv);
		WebFilterBean Bean = getItem(position);
		if (Bean.getWebClassType().equals("3")) {
			holder.status.setVisibility(View.INVISIBLE);
			holder.status.setClickable(false);
			ImageAware imageAware = new ImageViewAware(holder.icon, false);
			holder.icon.setTag(Constants.APP_ICON_SERVER_URL + Bean.getIcon());
			ImageLoader.getInstance().displayImage(Constants.APP_ICON_SERVER_URL+ Bean.getIcon(), imageAware, new ImageLoadingListener() {

				@Override
				public void onLoadingStarted(String imageUri, View view) {
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					if (imageUri.equals(holder.icon.getTag())) {
						holder.icon.setImageResource(R.drawable.add_app_deful_icon);
					}
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					if (imageUri.equals(holder.icon.getTag())) {
						holder.icon.setImageBitmap(loadedImage);
					}

				}

				@Override
				public void onLoadingCancelled(String imageUri, View view) {
				}
			});
		} else {
			holder.icon.setImageResource(R.drawable.add_app_web_icon);
		}
		if (Constants.isEditing) {
			holder.iv.setVisibility(View.VISIBLE);
			boolean status = false;
			if (isWhiteWebList) {
				status = AllowedWebsListActivity.listAdapter.get(position).isSelected();
			} else {
				status = UnallowedWebsListActivity.listAdapters.get(position).isSelected();
			}
			if (status) {
				holder.iv.setImageResource(R.drawable.ic_selected);
			} else {
				holder.iv.setImageResource(R.drawable.ic_undelete);
			}
		} else {
			holder.iv.setImageResource(R.drawable.ic_undelete);
			holder.iv.setVisibility(View.GONE);
		}

		holder.iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean status = false;
				if (isWhiteWebList)
					status = AllowedWebsListActivity.listAdapter.get(position).isSelected();
				else
					status = UnallowedWebsListActivity.listAdapters.get(position).isSelected();

				if (status) {
					holder.iv.setImageResource(R.drawable.ic_undelete);
					if (isWhiteWebList)
						AllowedWebsListActivity.listAdapter.get(position).setSelected(false);
					else
						UnallowedWebsListActivity.listAdapters.get(position).setSelected(false);

					if (null != mDelegate) {
						mDelegate.selectItem(position, false);
					}
				} else {
					holder.iv.setImageResource(R.drawable.ic_selected);
					if (isWhiteWebList)
						AllowedWebsListActivity.listAdapter.get(position).setSelected(true);
					else
						UnallowedWebsListActivity.listAdapters.get(position).setSelected(true);

					if (null != mDelegate) {
						mDelegate.selectItem(position, true);
					}
				}
			}
		});

		holder.status.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String status = null;
				if (isWhiteWebList)
					status = AllowedWebsListActivity.listAdapter.get(position).getWebStatus();
				else
					status = UnallowedWebsListActivity.listAdapters.get(position).getWebStatus();
				if ("1".equals(status)) {
					if (null != mDelegate) {
						mDelegate.startWebControl(position, "2");
					}
				} else {

					if (null != mDelegate) {
						mDelegate.startWebControl(position, "1");
					}
				}

			}
		});

		holder.title.setText(Bean.getWebTitle());
		holder.webs.setText(Bean.getWebSite());
		holder.type.setText(Bean.getWebTye());

		// 各个信息填入 status为1表示生效，2表示未生效
		if ("1".equals(Bean.getWebStatus())) {
			// holder.status.setText("生效");
			// holder.status.setBackgroundResource(R.drawable.item_web_filter_cell_state_bg);
			holder.status.setChecked(true);
			holder.title.setTextColor(0xff000000);
			holder.webs.setTextColor(0xff000000);
			holder.type.setTextColor(0xff000000);
			// holder.creatTime.setTextColor(0xff000000);
		} else {
			// holder.status.setText("未生效");
			// holder.status.setBackgroundResource(R.drawable.item_web_filter_cell_state2_bg);
			holder.status.setChecked(false);
			holder.title.setTextColor(0xffa1a1a1);
			holder.webs.setTextColor(0xffa1a1a1);
			holder.type.setTextColor(0xffa1a1a1);
			// holder.creatTime.setTextColor(0xffa1a1a1);
		}
		holder.delete.setOnClickListener(listener);
		holder.delete.setTag(position);
		if (convertView.getPaddingRight() != 0) {
			holder.ll_info.setLayoutParams(new LayoutParams(0, LayoutParams.MATCH_PARENT, 1));
		} else {
			holder.ll_info.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		}
		return convertView;
	}

	public final class ViewHolder {
		public LinearLayout ll_info;
		public LinearLayout delete;
		public ImageView iv;
		public TextView title;
		public TextView webs;
		public TextView type;
		public TextView creatTime;
		public CheckBox status;
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
