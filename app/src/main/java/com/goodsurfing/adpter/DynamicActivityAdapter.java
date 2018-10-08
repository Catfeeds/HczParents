package com.goodsurfing.adpter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goodsurfing.app.R;
import com.goodsurfing.beans.DynamicBean;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.main.ApplicationControlsActivity;
import com.goodsurfing.map.HczMapActivity;
import com.goodsurfing.utils.ActivityUtil;

import java.util.ArrayList;
import java.util.List;

public class DynamicActivityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    private List<DynamicBean> dynamicBeanList = new ArrayList<>(1);
    private static final int TYPE_TOP = 0x0000;
    private static final int TYPE_NORMAL = 0x0002;
    private View.OnClickListener listener;
    private Context mContext;

    public DynamicActivityAdapter(Context context, List<DynamicBean> dynamicBeanList) {
        inflater = LayoutInflater.from(context);
        this.dynamicBeanList = dynamicBeanList;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.activity_item_dynamic, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder itemHolder = (ViewHolder) holder;
        if (getItemViewType(position) == TYPE_TOP) {
            // 字体颜色加深
            itemHolder.tvAcceptTime1.setVisibility(View.VISIBLE);
        } else if (getItemViewType(position) == TYPE_NORMAL) {
            itemHolder.tvAcceptTime1.setVisibility(View.GONE);
        }
        itemHolder.tvAcceptTime.setTextColor(0x806f6f6f);
        itemHolder.tvAcceptStation.setTextColor(0xff6f6f6f);
        itemHolder.tvDot.setBackgroundResource(R.drawable.dynamic_old_icon);
        itemHolder.bindHolder(dynamicBeanList.get(position));
    }

    @Override
    public int getItemCount() {
        return dynamicBeanList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position != 0 && getItemMoth(position).equals(getItemMoth(position - 1))) {
            return TYPE_NORMAL;
        } else {
            return TYPE_TOP;
        }
    }


    private String getItemMoth(int position) {
        return ActivityUtil.getMoth4Time(dynamicBeanList.get(position).getDate());

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAcceptTime, tvAcceptStation;
        private TextView tvTopLine, tvDot;
        private TextView tvAcceptTime1;
        private TextView tvGoDynamicInfo;

        public ViewHolder(View itemView) {
            super(itemView);
            tvAcceptTime = (TextView) itemView.findViewById(R.id.tvAcceptTime);
            tvAcceptStation = (TextView) itemView.findViewById(R.id.tvAcceptStation);
            tvTopLine = (TextView) itemView.findViewById(R.id.tvTopLine);
            tvDot = (TextView) itemView.findViewById(R.id.tvDot);
            tvAcceptTime1 = (TextView) itemView.findViewById(R.id.tvAcceptTime2);
            tvGoDynamicInfo = itemView.findViewById(R.id.tvDynamicInfo);
        }

        public void bindHolder(DynamicBean dynamicBean) {
            tvAcceptTime.setText(ActivityUtil.getHours4Time(dynamicBean.getDate()));
            tvAcceptStation.setText(dynamicBean.getMsg());
            tvAcceptTime1.setText(dynamicBean.getDate().substring(0, dynamicBean.getDate().lastIndexOf("-") + 3));
            if (dynamicBean.getType() == DynamicBean.TYPE_LOCATION) {
                tvGoDynamicInfo.setVisibility(View.VISIBLE);
                tvGoDynamicInfo.setText("查看位置 >");
            } else if(dynamicBean.getType()==DynamicBean.TYPE_APP){
                tvGoDynamicInfo.setVisibility(View.VISIBLE);
                tvGoDynamicInfo.setText("查看应用 >");
            }else{
                tvGoDynamicInfo.setVisibility(View.GONE);
            }
            tvGoDynamicInfo.setTag(dynamicBean);
            tvGoDynamicInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DynamicBean bean = (DynamicBean) v.getTag();
                    if (bean.getType() == DynamicBean.TYPE_LOCATION) {
                        ActivityUtil.sendEvent4UM(mContext, "functionSwitch", "more", 17);
                        Intent location = new Intent(mContext, HczMapActivity.class);
                        location.putExtra("dynamic",bean);
                        mContext.startActivity(location);
                    } else {
                        ActivityUtil.sendEvent4UM(mContext, "functionSwitch", "more", 16);
                        Intent app = new Intent(mContext, ApplicationControlsActivity.class);
                        mContext.startActivity(app);
                    }
                }
            });
        }
    }

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }
}
