package com.goodsurfing.adpter;

import java.util.ArrayList;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.goodsurfing.app.R;
import com.goodsurfing.beans.TimeCardBean;
import com.goodsurfing.beans.WebFilterBean;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.main.TimerCountsListActivity;
import com.goodsurfing.main.UnallowedWebsListActivity;

public class TimeCardsListAdapter extends ArrayAdapter<TimeCardBean> {

	private int itemId;
	private Context _context;

	private static boolean isAllSelect = false;
	private LayoutInflater mInflater;
	private ViewHolder holder = null;

	// private static boolean isItemSelect = false;
	private static List<TimeCardBean> list = new ArrayList<TimeCardBean>();

	public static interface SelectDelegate {
		void selectItem(int position, boolean isSelected);

		void startWebControl(int position, String status);
	}

	private SelectDelegate mDelegate;

	public TimeCardsListAdapter(Context context, int textViewResourceId, List<TimeCardBean> objects) {
		super(context, textViewResourceId, objects);
		_context = context;
		this.mInflater = LayoutInflater.from(context);
		this.itemId = textViewResourceId;
		list = objects;
	}

	@Override
	public int getCount() {
		return super.getCount();
	}

	@Override
	public long getItemId(int position) {
		return super.getItemId(position);
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {

			holder = new ViewHolder();

			convertView = mInflater.inflate(itemId, null);

			holder.title = (TextView) convertView.findViewById(R.id.item_timer_card_title);
			holder.time = (TextView) convertView.findViewById(R.id.item_timer_card_time);
			holder.remain = (TextView) convertView.findViewById(R.id.item_timer_card_remain);
			holder.expire = (TextView) convertView.findViewById(R.id.item_timer_card_expires);
			holder.progress = (ProgressBar) convertView.findViewById(R.id.item_timer_card_pb);
			holder.creatTime = (TextView) convertView.findViewById(R.id.item_timer_card_creat);
			holder.delete = (LinearLayout) convertView.findViewById(R.id.llayout_right);
			holder.ll_info = (LinearLayout) convertView.findViewById(R.id.ll_info);
			convertView.setTag(holder);

		} else {

			holder = (ViewHolder) convertView.getTag();
		}

		holder.iv = (ImageView) convertView.findViewById(R.id.item_timer_card_web_iv);
		TimeCardBean Bean = list.get(position);

		if (Constants.isEditing) {
			holder.iv.setVisibility(View.VISIBLE);
			boolean status = false;
			status = TimerCountsListActivity.listAdapter.get(position).isSelected();
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
				status = TimerCountsListActivity.listAdapter.get(position).isSelected();

				if (status) {
					holder.iv.setImageResource(R.drawable.ic_undelete);
					TimerCountsListActivity.listAdapter.get(position).setSelected(false);

					if (null != mDelegate) {
						mDelegate.selectItem(position, false);
					}
				} else {
					holder.iv.setImageResource(R.drawable.ic_selected);
					TimerCountsListActivity.listAdapter.get(position).setSelected(true);

					if (null != mDelegate) {
						mDelegate.selectItem(position, true);
					}
				}
			}
		});
		holder.creatTime.setText(Bean.getCreateTime());
		holder.title.setText("上网卡号:" + Bean.getName());
		int totals = Integer.parseInt(Bean.getTotalTime());
		holder.time.setText(totals / 3600 + "");
		int remain;
		try {
			remain = Integer.parseInt(Bean.getTotalTime()) - Integer.parseInt(Bean.getRemainTime());
		} catch (Exception e) {
			remain = Integer.parseInt(Bean.getTotalTime());
		}
		int hours = remain / 3600;
		int minutes = (remain % 3600) / 60;
		int mm = remain % 3600 - minutes * 60;
		holder.remain.setText(hours + "小时" + minutes + "分" + mm + "秒");
		holder.expire.setText("有效期:    " + Bean.getExpireTime());
		int remainProgress;
		if (remain > 0)
			remainProgress = (int) ((remain * 1.0 / totals) * 100);
		else
			remainProgress = 0;
		holder.progress.setProgress(remainProgress);
		holder.delete.setOnClickListener((TimerCountsListActivity) _context);
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
		public TextView time;
		public TextView remain;
		public TextView creatTime;
		public TextView expire;
		public ProgressBar progress;
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
