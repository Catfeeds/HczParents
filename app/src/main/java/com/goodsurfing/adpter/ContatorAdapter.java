package com.goodsurfing.adpter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.goodsurfing.app.R;
import com.goodsurfing.beans.Friend;
import com.goodsurfing.utils.StringMatcher;

/**
 * 联系人
 * 
 * @author 谢志杰
 * 
 *         2016
 */
public class ContatorAdapter extends BaseAdapter implements SectionIndexer,
		OnClickListener {

	private Context contex;
	private List<Friend> users;
	private String mSections = "ABCDEFGHIJKLMNOPQRSTUVWXYZ#";

	public ContatorAdapter(Context contex, List<Friend> users) {
		super();
		this.contex = contex;
		this.users = users;
	}

	@Override
	public int getCount() {
		return users == null ? 0 : users.size();
	}

	@Override
	public Object getItem(int position) {
		return users.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int positon, View convertView, ViewGroup group) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(contex).inflate(
					R.layout.cont_item, null);
			holder.name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.zimu = (TextView) convertView.findViewById(R.id.tv_zimu);
			holder.phone = (TextView) convertView.findViewById(R.id.tv_phone);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Friend user = users.get(positon);
		initContactor(positon, user, 0, holder);

		return convertView;
	}

	private void initContactor(int positon, Friend user, int start,
			ViewHolder holder) {
		holder.name.setText(user.getNikename());
		if (positon == start) {
			holder.zimu.setVisibility(View.VISIBLE);
			holder.zimu.setText(user.getPinyinlastname());
		} else {
			String cur = user.getPinyinlastname();
			if (positon < users.size()) {
				String next = users.get(positon).getPinyinlastname();
				String pre = users.get(positon - 1 < 0 ? 0 : positon - 1)
						.getPinyinlastname();
				if (!cur.equals(next) || !cur.equals(pre)) {
					holder.zimu.setText(user.getPinyinlastname());
					holder.zimu.setVisibility(View.VISIBLE);
				} else {
					holder.zimu.setVisibility(View.GONE);
				}
			} else {
				String pre = users.get(positon - 1 < 0 ? 0 : positon - 1)
						.getPinyinlastname();
				if (!cur.equals(pre)) {
					holder.zimu.setText(user.getPinyinlastname());
					holder.zimu.setVisibility(View.VISIBLE);
				} else {
					holder.zimu.setVisibility(View.GONE);
				}
			}
		}

	}

	class ViewHolder {
		TextView name;
		TextView phone;
		TextView zimu;
	}

	@Override
	public Object[] getSections() {
		String[] sections = new String[mSections.length()];
		for (int i = 0; i < mSections.length(); i++)
			sections[i] = String.valueOf(mSections.charAt(i));
		return sections;
	}

	@Override
	public int getPositionForSection(int section) {
		for (int i = section; i >= 0; i--) {
			for (int j = 0; j < getCount(); j++) {
				if (i == 0) {
					// For numeric section
					Friend user = (Friend) getItem(j);
					for (int k = 0; k <= 9; k++) {
						if (StringMatcher.match(
								String.valueOf(user.getPinyinlastname().charAt(
										0)), String.valueOf(k)))
							return j;
					}
				} else {
					Friend user = (Friend) getItem(j);
					if (StringMatcher.match(
							String.valueOf(user.getPinyinlastname().charAt(0)),
							String.valueOf(mSections.charAt(i))))
						return j;
				}
			}
		}
		return 0;
	}

	@Override
	public int getSectionForPosition(int position) {
		return 0;
	}

	@Override
	public void onClick(View v) {

	}
}
