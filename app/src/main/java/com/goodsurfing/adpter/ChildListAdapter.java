package com.goodsurfing.adpter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goodsurfing.app.R;
import com.goodsurfing.beans.ChildBean;
import com.goodsurfing.beans.DynamicBean;
import com.goodsurfing.beans.Friend;
import com.goodsurfing.constants.Constants;

import java.util.ArrayList;
import java.util.List;

public class ChildListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    private List<ChildBean> childBeans = new ArrayList<>(1);
    private static final int TYPE_TOP = 0x0000;
    private static final int TYPE_NORMAL = 0x0002;
    private View.OnClickListener listener;

    public ChildListAdapter(Context context, List<ChildBean> childBeans) {
        inflater = LayoutInflater.from(context);
        this.childBeans = childBeans;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.layout_item_child, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder itemHolder = (ViewHolder) holder;
        itemHolder.bindHolder(position);
    }

    @Override
    public int getItemCount() {
        return childBeans.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView childName, childPhone,childDevice;
        private ImageView childHead;
        private LinearLayout childLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            childName = (TextView) itemView.findViewById(R.id.item_child_name);
            childPhone = (TextView) itemView.findViewById(R.id.item_child_phone);
            childDevice = (TextView) itemView.findViewById(R.id.item_child_device);
            childHead = (ImageView) itemView.findViewById(R.id.item_child_icon);
            childLayout =  itemView.findViewById(R.id.layout_item_child_ll);
        }

        public void bindHolder(int position) {
            ChildBean childBean= childBeans.get(position);
            childName.setText(childBean.getName());
            childDevice.setText(childBean.getDevice());
            childPhone.setText(childBean.getMobile());
            childHead.setImageResource(Constants.showIds[childBean.getImg()]);
            childLayout.setTag(position);
            childLayout.setOnClickListener(listener);
        }
    }

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }
}
