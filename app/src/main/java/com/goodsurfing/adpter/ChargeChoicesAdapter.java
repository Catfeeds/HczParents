package com.goodsurfing.adpter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.goodsurfing.app.R;
import com.goodsurfing.beans.ChargeIDBean;

public class ChargeChoicesAdapter extends ArrayAdapter<ChargeIDBean> {

	private int itemId;
	private Context _context;
	private LayoutInflater mInflater;
	private ViewHolder holder = null;

	public ChargeChoicesAdapter(Context context, int textViewResourceId, List<ChargeIDBean> objects) {
		super(context, textViewResourceId, objects);
		_context = context;
		this.mInflater = LayoutInflater.from(context);
		this.itemId = textViewResourceId;
		// list = objects;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return super.getCount();
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return super.getItemId(position);
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// final ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(itemId, null);
			holder.title = (TextView) convertView.findViewById(R.id.activity_price_name);
			holder.webs = (TextView) convertView.findViewById(R.id.activity_price_money);
			holder.checkBox = (CheckBox) convertView.findViewById(R.id.activity_price_cb);
			holder.type = (TextView) convertView.findViewById(R.id.activity_price_type);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ChargeIDBean bean = getItem(position);
		holder.title.setText(bean.getName()+":");
		holder.webs.setText(bean.getPrice() + "元");
		holder.type.setText(bean.getType()+"个月");
		holder.checkBox.setChecked(bean.isChecked());
		return convertView;

	}

	public final class ViewHolder {
		public TextView title;
		public TextView webs;
		public TextView type;
		public TextView creatTime;
		public TextView status;
		CheckBox checkBox;
	}
}
