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
import android.widget.TextView;

import com.goodsurfing.app.R;
import com.goodsurfing.beans.WebFilterBean;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.main.UnallowedWebsListActivity;

public class UnAllowsWebListAdapter extends ArrayAdapter<WebFilterBean> {

	private int itemId;
	private Context _context;
	
	private static boolean isAllSelect = false;
	private LayoutInflater mInflater;
	private ViewHolder holder = null;
	//private static boolean isItemSelect = false;
	//private static List<WebFilterBean> list = new ArrayList<WebFilterBean>();
	
	public static interface SelectDelegate {
		void selectItem(int position,boolean isSelected);
		void startWebControl(int position,String status);
	}

	private SelectDelegate mDelegate;
	
	
	public UnAllowsWebListAdapter(Context context, int textViewResourceId,
			List<WebFilterBean> objects) {
		super(context, textViewResourceId, objects);
		_context = context;
		this.mInflater = LayoutInflater.from(context);
		this.itemId = textViewResourceId;
		//list = objects;
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

	@Override
	public WebFilterBean getItem(int position) {
		// TODO Auto-generated method stub
		return super.getItem(position);
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		// final ViewHolder holder = null;
         if (convertView == null) {
              
             holder=new ViewHolder();  
              
             convertView = mInflater.inflate(itemId, null);
             
             holder.title = (TextView)convertView.findViewById(R.id.item_web_filter_title);
             holder.webs = (TextView)convertView.findViewById(R.id.item_web_filter_web);
             holder.type = (TextView)convertView.findViewById(R.id.item_web_filter_type);
//             holder.creatTime = (TextView)convertView.findViewById(R.id.item_web_filter_time);
//             holder.status = (TextView)convertView.findViewById(R.id.item_web_filter_status);
             
             convertView.setTag(holder);
              
         }else {
              
             holder = (ViewHolder)convertView.getTag();
         }
         
        holder.iv = (ImageView)convertView.findViewById(R.id.item_web_filter_web_iv);
  		WebFilterBean Bean = getItem(position);
         //holder.title.setText(Bean.getWebTitle());
         
 		if(Constants.isEditing) {
 			holder.iv.setVisibility(View.VISIBLE);	
 			
 			if(isAllSelect) {
 				holder.iv.setImageResource(R.drawable.ic_selected);
 			}else {	
 					boolean status =false;
 
 					status= UnallowedWebsListActivity.listAdapters.get(position).isSelected();
 					
 					if(status) {
 						holder.iv.setImageResource(R.drawable.ic_selected);
 					}
 					else {
 						holder.iv.setImageResource(R.drawable.ic_undelete);
 					}
 			
 			}

 		}
 		else {
 			holder.iv.setImageResource(R.drawable.ic_undelete);
 			holder.iv.setVisibility(View.GONE);
 		}
 		
 		holder.iv.setOnClickListener(new OnClickListener() {
 			
 			@Override
 			public void onClick(View v) {
 				boolean status =false;
				status= UnallowedWebsListActivity.listAdapters.get(position).isSelected();
					
 				if(status) {
 					holder.iv.setImageResource(R.drawable.ic_undelete);
 					//AllowedWebsListActivity.listAdapter.get(position).setSelected(false);
 					UnallowedWebsListActivity.listAdapters.get(position).setSelected(false);
 					
 					if(null!=mDelegate) {
 						mDelegate.selectItem(position,false);					
 					}
 				}
 				else {
 					holder.iv.setImageResource(R.drawable.ic_selected);
 					//AllowedWebsListActivity.list.put(position, true);
 					UnallowedWebsListActivity.listAdapters.get(position).setSelected(true);
 					if(null!=mDelegate) {
 						mDelegate.selectItem(position,true);
 					}
 				}
 			}
 		});
 		
// 		 holder.status.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				String status =null;
//				status= UnallowedWebsListActivity.listAdapters.get(position).getWebStatus();
//				if("1".equals(status)) {
//
// 					if(null!=mDelegate) {
// 						mDelegate.startWebControl(position, "2");				
// 					}
// 				}
// 				else {
//
// 					if(null!=mDelegate) {
// 						mDelegate.startWebControl(position, "1");	
// 					}
// 				}
//				
//			}
//		});
 		 
		holder.title.setText(Bean.getWebTitle());
		holder.webs.setText(Bean.getWebSite());
		holder.type.setText(Bean.getWebTye());
		//holder.creatTime.setText(Bean.getWebCreateTime());
		
 		//各个信息填入   status为1表示生效，2表示未生效
 		if("1".equals(Bean.getWebStatus())) {
 			holder.status.setText("生效");
 			holder.status.setBackgroundResource(R.drawable.item_web_filter_cell_state_bg);
 			holder.title.setTextColor(0xff000000);
 			holder.webs.setTextColor(0xff000000);
 			holder.type.setTextColor(0xff000000);
 			holder.creatTime.setTextColor(0xff000000);
 		}else {
 			holder.status.setText("未生效");
 			holder.status.setBackgroundResource(R.drawable.item_web_filter_cell_state2_bg);
 			holder.title.setTextColor(0xffa1a1a1);
 			holder.webs.setTextColor(0xffa1a1a1);
 			holder.type.setTextColor(0xffa1a1a1);
 			holder.creatTime.setTextColor(0xffa1a1a1);
 		}
 		
 		return convertView;
		/*LinearLayout listItemLayout = new LinearLayout(getContext());
		String inflater = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
				inflater);
		vi.inflate(itemId, listItemLayout, true);
		
		*/
	}
	
	 public final class ViewHolder{
	        public ImageView iv;
	        public TextView title;
	        public TextView webs;
	        public TextView type;
	        public TextView creatTime;
	        public TextView status;
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
