package com.goodsurfing.adpter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.goodsurfing.app.R;
import com.goodsurfing.beans.ModeBean;
import com.goodsurfing.constants.Constants;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class HczModeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    private List<ModeBean> modeBeans = new ArrayList<>();
    private View.OnClickListener listener;
    private Context mContext;

    public HczModeAdapter(Context context, List<ModeBean> modeBeans) {
        inflater = LayoutInflater.from(context);
        this.modeBeans = modeBeans;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.activity_mode_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder itemHolder = (ViewHolder) holder;
        itemHolder.bindHolder(position);
    }

    @Override
    public int getItemCount() {
        return modeBeans.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView modeName, modeDesc;
        private ImageView modeStatus;
        private ImageView modeIv;
        private View modeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            modeName =  itemView.findViewById(R.id.activity_mode_tab1_name);
            modeDesc = itemView.findViewById(R.id.activity_mode_tab1_desc);
            modeStatus =itemView.findViewById(R.id.activity_mode_tab1_cb);
            modeIv = itemView.findViewById(R.id.activity_mode_tab1_iv);
            modeLayout =itemView.findViewById(R.id.activity_mode_tab_1);
        }

        public void bindHolder(int position) {
            ModeBean modeBean = modeBeans.get(position);
            modeName.setText(modeBean.getName());
            modeDesc.setText(modeBean.getDesc());
            if(Constants.mode==modeBean.getModeId()){
                modeBean.setCheck(1);
                modeStatus.setBackgroundResource(R.drawable.mode_change_select);
            }else {
                modeStatus.setBackgroundResource(R.drawable.mode_change_unselect);
            }
            ImageLoader.getInstance().displayImage(modeBean.getImg(),modeIv);
            modeLayout.setTag(position);
            modeLayout.setOnClickListener(listener);
        }
    }

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }
}
