package com.goodsurfing.adpter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.goodsurfing.app.R;
import com.goodsurfing.beans.AppUseBean;
import com.goodsurfing.utils.ActivityUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class MainInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    private List<AppUseBean> appUseBeans;
    private static final int TYPE_TOP = 0x0000;
    private static final int TYPE_MODE = 0x0001;
    private static final int TYPE_CHART = 0x0002;
    private static final int TYPE_NORMAL = 0x0003;
    private Context mContext;
    private View headView;
    private View modeView;
    private View chartView;

    public MainInfoAdapter(Context context, List<AppUseBean> appUseBeans) {
        inflater = LayoutInflater.from(context);
        this.appUseBeans = appUseBeans;
        mContext = context;
    }

    public View getHeadView() {
        return headView;
    }

    public void setHeadView(View headView) {
        this.headView = headView;
        notifyItemInserted(0);
    }

    public View getModeView() {
        return modeView;
    }

    public void setModeView(View modeView) {
        this.modeView = modeView;
        notifyItemInserted(1);
    }

    public View getChartView() {
        return chartView;
    }

    public void setChartView(View chartView) {
        this.chartView = chartView;
        notifyItemInserted(2);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_TOP:
                return new ViewHolder(headView);
            case TYPE_MODE:
                return new ViewHolder(modeView);
            case TYPE_CHART:
                return new ViewHolder(chartView);
            case TYPE_NORMAL:
                return new ViewHolder(inflater.inflate(R.layout.app_use_time_layout, parent, false));
        }
        return new ViewHolder(inflater.inflate(R.layout.app_use_time_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder itemHolder = (ViewHolder) holder;
        if (position == 0 && headView != null) {
            return;
        }
        if (position == 1 && modeView != null) {
            return;
        }
        if (position == 2 && chartView != null) {
            return;
        }
        if(appUseBeans.size()>0)
        itemHolder.bindHolder(appUseBeans.get(position - 3));
    }

    @Override
    public int getItemCount() {
        if (chartView != null)
            return appUseBeans.size() + 3;
        if (modeView != null) {
            return appUseBeans.size() + 2;
        }
        if (headView != null) {
            return appUseBeans.size() + 1;
        }
        return appUseBeans.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (headView != null && position == 0) {
            return TYPE_TOP;
        }
        if (modeView != null && position == 1) {
            return TYPE_MODE;
        }
        if (chartView != null && position == 2)
            return TYPE_CHART;
        return TYPE_NORMAL;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTv, typeTv, timeTv;
        private ImageView iconIv;

        public ViewHolder(View itemView) {
            super(itemView);
            if (itemView == headView) return;
            if (itemView == modeView) return;
            if (itemView == chartView) return;
            nameTv = itemView.findViewById(R.id.app_use_name_tv);
            typeTv = itemView.findViewById(R.id.app_use_type_tv);
            timeTv = itemView.findViewById(R.id.app_use_time_tv);
            iconIv = itemView.findViewById(R.id.app_use_icon_iv);
        }

        public void bindHolder(AppUseBean appUseBean) {
            nameTv.setText(appUseBean.getAppname());
            timeTv.setText(ActivityUtil.getAPP4Time(appUseBean.getUtime()));
            typeTv.setText(appUseBean.getCateName());
            if (!TextUtils.isEmpty(appUseBean.getImg())) {
                ImageLoader.getInstance().displayImage(appUseBean.getImg(), iconIv);
            }else {
                iconIv.setImageResource(R.drawable.add_app_deful_icon);
            }
        }
    }
}
