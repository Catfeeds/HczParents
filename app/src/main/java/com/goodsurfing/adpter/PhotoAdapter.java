package com.goodsurfing.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.goodsurfing.app.R;

public class PhotoAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private ViewHolder holder = null;
	private int[] ids;
	private String name[] = new String[] { "小勇士", "小乖乖", "小贝贝", "小虎子", "小甜甜", "小点点", "小可爱", "小宝贝" };

	public PhotoAdapter(Context context, int[] ids) {
		super();
		this.mInflater = LayoutInflater.from(context);
		this.ids = ids;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_photo, null);
			holder.iv = (ImageView) convertView.findViewById(R.id.item_photo_iv);
			holder.title = (TextView) convertView.findViewById(R.id.item_photo_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.iv.setBackgroundResource(ids[position]);
		holder.title.setText(name[position]);
		return convertView;

	}

	public final class ViewHolder {
		public ImageView iv;
		public TextView title;
	}

	@Override
	public Object getItem(int arg0) {
		return arg0;
	}

	@Override
	public int getCount() {
		return ids.length;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
}
