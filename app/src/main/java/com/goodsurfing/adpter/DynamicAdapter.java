package com.goodsurfing.adpter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goodsurfing.app.R;
import com.goodsurfing.beans.DynamicBean;
import com.goodsurfing.hcz.ApplicationControlsActivity;
import com.goodsurfing.map.HczMapActivity;
import com.goodsurfing.utils.ActivityUtil;

import java.util.ArrayList;
import java.util.List;

public class DynamicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    private List<DynamicBean> dynamicBeanList = new ArrayList<>(1);
    private static final int TYPE_TOP = 0x0000;
    private static final int TYPE_MODE = 0x0001;
    private static final int TYPE_NORMAL = 0x0002;
    private View.OnClickListener listener;
    private Context mContext;

    public DynamicAdapter(Context context, List<DynamicBean> dynamicBeanList) {
        inflater = LayoutInflater.from(context);
        this.dynamicBeanList = dynamicBeanList;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_dynamic, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder itemHolder = (ViewHolder) holder;
        if (getItemViewType(position) == TYPE_TOP) {

        } else if (getItemViewType(position) == TYPE_MODE) {

        } else if (getItemViewType(position) == TYPE_NORMAL) {
            itemHolder.linearLayout2.setVisibility(View.VISIBLE);
            itemHolder.linearLayout1.setVisibility(View.GONE);
            itemHolder.linearLayout0.setVisibility(View.GONE);
            itemHolder.tvTopLine.setVisibility(View.VISIBLE);
            itemHolder.tvAcceptTime.setTextColor(0x806f6f6f);
            itemHolder.tvAcceptStation.setTextColor(0xff6f6f6f);
            itemHolder.tvDot.setBackgroundResource(R.drawable.dynamic_old_icon);
            itemHolder.bindHolder(dynamicBeanList.get(position - 2));
        }

    }

    @Override
    public int getItemCount() {
        return dynamicBeanList.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_TOP;
        }
        if (position == 1) {
            return TYPE_MODE;
        }
        return TYPE_NORMAL;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAcceptTime, tvAcceptStation;
        private TextView tvTopLine, tvDot;
        private LinearLayout linearLayout0;
        private LinearLayout linearLayout1;
        private LinearLayout linearLayout2;
        private TextView tvAcceptTime1;
        private TextView tvTopLine1, tvDot1, tvGoDynamicInfo;


        public ViewHolder(View itemView) {
            super(itemView);
            linearLayout0 = itemView.findViewById(R.id.item_dynamic_ll0);
            linearLayout1 = itemView.findViewById(R.id.item_dynamic_ll1);
            linearLayout2 = itemView.findViewById(R.id.item_dynamic_ll2);
            tvAcceptTime = (TextView) itemView.findViewById(R.id.tvAcceptTime);
            tvAcceptStation = (TextView) itemView.findViewById(R.id.tvAcceptStation);
            tvTopLine = (TextView) itemView.findViewById(R.id.tvTopLine);
            tvDot = (TextView) itemView.findViewById(R.id.tvDot);

            tvAcceptTime1 = (TextView) itemView.findViewById(R.id.tvAcceptTime2);
            tvTopLine1 = (TextView) itemView.findViewById(R.id.tvTopLine2);
            tvDot1 = (TextView) itemView.findViewById(R.id.tvDot2);
            tvGoDynamicInfo = itemView.findViewById(R.id.tvDynamicInfo);
        }

        public void bindHolder(DynamicBean dynamicBean) {
            tvAcceptTime.setText(ActivityUtil.getHours4Time(dynamicBean.getDate()));
            tvAcceptStation.setText(dynamicBean.getMsg());
            if (dynamicBean.getType() == DynamicBean.TYPE_LOCATION) {
                tvGoDynamicInfo.setVisibility(View.VISIBLE);
                tvGoDynamicInfo.setText("查看位置 >");
            } else if (dynamicBean.getType() == DynamicBean.TYPE_APP) {
                tvGoDynamicInfo.setVisibility(View.VISIBLE);
                tvGoDynamicInfo.setText("查看应用 >");
            } else{
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
                        location.putExtra("dynamic", bean);
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
