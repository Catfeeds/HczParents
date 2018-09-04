package com.goodsurfing.adpter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.mapapi.map.MapView;
import com.goodsurfing.app.R;
import com.goodsurfing.beans.DynamicBean;
import com.goodsurfing.utils.ActivityUtil;

import java.util.List;

public class HczTrajectoryAdapter extends RecyclerView.Adapter<HczTrajectoryAdapter.ViewHolder> {
    private List<DynamicBean> list;
    private static final int HEADER = 0x0001;
    private static final int NORMAL = 0x0002;
    private View headerView;
    private Context mContext;

    public HczTrajectoryAdapter(Context context, List<DynamicBean> dynamicBeanList) {
        this.list = dynamicBeanList;
        mContext = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i("onBindViewHolder", "viewType: "+viewType);
        if (headerView != null && viewType == HEADER) return new ViewHolder(headerView);
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_hcz_trajectory_map, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.i("onBindViewHolder", "onBindViewHolder: "+position);
        if (getItemViewType(position) == HEADER) {
//            MapView mapView = headerView.findViewById(R.id.bmapview);
//            mapView.setVisibility(View.VISIBLE);
//            mapView.onResume();
        } else {
            final int pos = getRealPosition(position);
            holder.bindHolder(list.get(pos));
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (headerView == null) return NORMAL;
        if (position == 0) return HEADER;
        return NORMAL;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
        notifyItemInserted(0);
    }

    private int getRealPosition(int position) {
        return headerView == null ? position : position - 1;
    }

    @Override
    public int getItemCount() {
        return headerView == null ? list.size() : list.size() + 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAcceptTime, tvAcceptStation;
        private TextView tvTopLine, tvDot;


        public ViewHolder(View itemView) {
            super(itemView);
            if (itemView == headerView) return;
            tvAcceptTime = (TextView) itemView.findViewById(R.id.tvAcceptTime);
            tvAcceptStation = (TextView) itemView.findViewById(R.id.tvAcceptStation);
            tvTopLine = (TextView) itemView.findViewById(R.id.tvTopLine);
            tvDot = (TextView) itemView.findViewById(R.id.tvDot);

        }

        public void bindHolder(DynamicBean dynamicBean) {
            tvTopLine.setVisibility(View.VISIBLE);
            tvAcceptTime.setTextColor(0x806f6f6f);
            tvAcceptStation.setTextColor(0xff6f6f6f);
            tvDot.setBackgroundResource(R.drawable.dynamic_old_icon);
            tvAcceptTime.setText(ActivityUtil.getHours4Time(dynamicBean.getDate()));
            tvAcceptStation.setText(dynamicBean.getMsg());
        }


    }


}
