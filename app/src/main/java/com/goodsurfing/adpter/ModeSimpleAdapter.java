package com.goodsurfing.adpter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.goodsurfing.constants.Constants;

public class ModeSimpleAdapter extends SimpleAdapter {

	Context context;

	public ModeSimpleAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);
		this.context = context;
		// TODO Auto-generated constructor stub
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = super.getView(position, convertView, parent);

		if (position == (Constants.mode- 1)) {
			LinearLayout ll = (LinearLayout) v;
			if (v != null) {
				TextView view1 = (TextView) ll.getChildAt(1);
				view1.setBackgroundColor(0xff41a814);
				view1.setTextColor(Color.WHITE);
			}
		}

		return v;
	}
}
