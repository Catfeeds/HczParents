package com.goodsurfing.adpter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.component.constants.What;
import com.goodsurfing.app.R;
import com.goodsurfing.beans.AppBean;
import com.goodsurfing.beans.AppUseBean;
import com.goodsurfing.beans.WebFilterBean;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.server.net.HczSetAppTimeNet;
import com.goodsurfing.server.net.HczSwitchAppNet;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.view.SeekBarIndicated;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

public class AppListAdapter extends ArrayAdapter<AppBean> {

    private final Context mContext;
    private int itemId;
    private LayoutInflater mInflater;
    private ViewHolder holder = null;
    private Handler handler;

    public static interface SelectDelegate {
        void selectItem(int position, boolean isSelected);

        void startWebControl(int position, String status);
    }

    private SelectDelegate mDelegate;

    public AppListAdapter(Context context, int textViewResourceId, List<AppBean> objects) {
        super(context, textViewResourceId, objects);
        this.mInflater = LayoutInflater.from(context);
        this.itemId = textViewResourceId;
        mContext = context;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public AppBean getItem(int position) {
        return super.getItem(position);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(itemId, null);
            holder.icon = (ImageView) convertView.findViewById(R.id.item_web_filter_web_icon);
            holder.title = (TextView) convertView.findViewById(R.id.item_web_filter_name);
            holder.type = (TextView) convertView.findViewById(R.id.item_web_filter_type);
            holder.status = (CheckBox) convertView.findViewById(R.id.item_web_filter_status);
            holder.timeBar = convertView.findViewById(R.id.item_web_progress);
            holder.appView = convertView;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final AppBean appBean = getItem(position);
        if (!TextUtils.isEmpty(appBean.getImg())) {
            ImageLoader.getInstance().displayImage(appBean.getImg(), holder.icon);
        }else {
            holder.icon.setImageResource(R.drawable.add_app_deful_icon);
        }
        holder.title.setText(appBean.getName());

        if (appBean.getStatus() == 1) {
            holder.status.setChecked(true);
            holder.title.setTextColor(0xff000000);
        } else {
            holder.title.setTextColor(0xffa1a1a1);
            holder.status.setChecked(false);

        }
        if (appBean.getAvailableTime() != 0) {
            holder.type.setText("已使用" + appBean.getUsedtime() / 60 + "分钟/" + appBean.getAvailableTime() / 60 + "分钟");
            holder.timeBar.setProgress((int) ((appBean.getUsedtime() * 1.0f / appBean.getAvailableTime())*100));
            if (appBean.getUsedtime() >= appBean.getAvailableTime() && appBean.getStatus() == 1) {
                appBean.setStatus(2);
                holder.title.setTextColor(0xffa1a1a1);
                holder.status.setChecked(false);
            }
        }
        holder.status.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (appBean.getStatus() != 1) {
                    showAppSetTime(appBean);
                } else {
                    switchApp(appBean);
                }
            }
        });
//        holder.appView.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showAppSetTime(appBean);
//            }
//        });


        return convertView;
    }

    private void switchApp(final AppBean appBean) {
        HczSwitchAppNet hczSwitchAppNet = new HczSwitchAppNet(mContext, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case What.HTTP_REQUEST_CURD_SUCCESS:
                        appBean.setStatus(appBean.getStatus() == 1 ? 2 : 1);
                        handler.sendEmptyMessage(What.HTTP_REQUEST_CURD_SUCCESS);
                        break;
                    case What.HTTP_REQUEST_CURD_FAILURE:
                        handler.sendEmptyMessage(What.HTTP_REQUEST_CURD_FAILURE);
                        break;
                }
                notifyDataSetChanged();
            }
        });
        hczSwitchAppNet.putParams(appBean.getPackage(), appBean.getStatus() == 1 ? 2 : 1);
        hczSwitchAppNet.sendRequest();
    }

    private void showAppSetTime(final AppBean appBean) {
        final Dialog dlg = new Dialog(mContext, R.style.AlertDialogCustom);
        View view = View.inflate(mContext, R.layout.app_set_time_dialog, null);

        final SeekBarIndicated seekBarIndicated = (SeekBarIndicated) view.findViewById(R.id.mSeekBarIndicated);
        final TextView comfirm = (TextView) view.findViewById(R.id.activity_change_mode_comfirm_text);
        TextView countv = (TextView) view.findViewById(R.id.activity_change_mode_cancle_text);
        comfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                final int time = seekBarIndicated.getProgress() * 60;
                if (time > 0&&time>appBean.getUsedtime()) {
                    HczSetAppTimeNet setAppTimeNet = new HczSetAppTimeNet(mContext, new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            switch (msg.what) {
                                case What.HTTP_REQUEST_CURD_SUCCESS:
                                    appBean.setAvailableTime(time);
                                    switchApp(appBean);
//                                    handler.sendEmptyMessage(What.HTTP_REQUEST_CURD_SUCCESS);
                                    notifyDataSetChanged();
                                    break;
                                case What.HTTP_REQUEST_CURD_FAILURE:
                                    handler.sendEmptyMessage(What.HTTP_REQUEST_CURD_FAILURE);
                                    break;
                            }
                        }
                    });
                    setAppTimeNet.putParams(appBean.getClientAppId() + "", time);
                    setAppTimeNet.sendRequest();
                }else {
                    ActivityUtil.showPopWindow4Tips(mContext,comfirm, false, true, "设置失败,可用时长小于已用时长", 2000);
                }
                dlg.dismiss();
                notifyDataSetChanged();
            }
        });
        countv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                notifyDataSetChanged();
            }
        });
        dlg.setContentView(view);
        WindowManager m = ((Activity) mContext).getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dlg.getWindow().getAttributes(); // 获取对话框当前的参数值
        p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.95
        dlg.getWindow().setAttributes(p);
        dlg.show();
    }

    public final class ViewHolder {
        public TextView title;
        public TextView type;
        public CheckBox status;
        public ImageView icon;
        public View appView;
        public SeekBar timeBar;
    }

    public void setDelegate(SelectDelegate delegate) {
        mDelegate = delegate;
    }

}
