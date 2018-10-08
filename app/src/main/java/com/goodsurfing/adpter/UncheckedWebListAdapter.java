package com.goodsurfing.adpter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.goodsurfing.app.R;
import com.goodsurfing.beans.WebFilterBean;
import com.goodsurfing.main.UnCheckedWebsListActivity;

public class UncheckedWebListAdapter extends ArrayAdapter<WebFilterBean> {

	private int itemId;
	private OnClickListener listener;

	private static boolean isAllSelect = false;
	private LayoutInflater mInflater;
	private ViewHolder holder = null;

	// private static boolean isItemSelect = false;
	// private static List<WebFilterBean> list = new ArrayList<WebFilterBean>();

	public static interface SelectDelegate {
		void selectItem(int position, boolean isSelected);

		void startWebControl(int position);
	}

	private SelectDelegate mDelegate;

	public UncheckedWebListAdapter(Context context, int textViewResourceId,
			List<WebFilterBean> objects,OnClickListener listener) {
		super(context, textViewResourceId, objects);
		this.listener =listener;
		this.mInflater = LayoutInflater.from(context);
		this.itemId = textViewResourceId;
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

			holder.title = (TextView) convertView
					.findViewById(R.id.item_web_filter_title);
			holder.status = (ImageView) convertView
					.findViewById(R.id.item_web_filter_status);
			holder.delete = (LinearLayout) convertView
					.findViewById(R.id.llayout_right);
			holder.ll_info = (RelativeLayout) convertView
					.findViewById(R.id.ll_info);
			convertView.setTag(holder);

		} else {

			holder = (ViewHolder) convertView.getTag();
		}
		WebFilterBean Bean = getItem(position);
		holder.title.setText(Bean.getWebSite());

		holder.status.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (null != mDelegate) {
					mDelegate.startWebControl(position);
				}

			}
		});
//		convertView.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View view) {
//				if (null != mDelegate) {
//					mDelegate.selectItem(position, true);
//				}
//			}
//		});
		holder.delete.setOnClickListener(listener);
		holder.delete.setTag(position);
		if (convertView.getPaddingRight() != 0) {
			holder.ll_info.setLayoutParams(new LayoutParams(0,
					LayoutParams.MATCH_PARENT,1));
		} else {
			holder.ll_info.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		}
		return convertView;
	}

	public final class ViewHolder {
		public RelativeLayout ll_info;
		public LinearLayout delete;
		public TextView title;
		public ImageView status;
	}

	public void setDelegate(SelectDelegate delegate) {
		mDelegate = delegate;
	}

	public void setSelectALL(boolean isSelectAll) {
		isAllSelect = isSelectAll;
	}

	public boolean getSelectAllFlag() {
		return isAllSelect;
	}
}
