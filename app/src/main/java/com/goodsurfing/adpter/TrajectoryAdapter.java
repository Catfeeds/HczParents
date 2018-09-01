package com.goodsurfing.adpter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.goodsurfing.app.R;
import com.goodsurfing.beans.DynamicBean;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.utils.ActivityUtil;

import java.util.ArrayList;
import java.util.List;

public class TrajectoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    private List<DynamicBean> dynamicBeanList = new ArrayList<>(1);
    private static final int TYPE_TOP = 0x0000;
    private static final int TYPE_NORMAL = 0x0002;
    private View.OnClickListener listener;
    private float currentZoomLevel;
    protected float maxZoomLevel;
    protected float minZoomLevel;
    private BaiduMap bdMap;
    private MapStatus.Builder builder;
    private  Context mContext;

    public TrajectoryAdapter(Context context, List<DynamicBean> dynamicBeanList) {
        inflater = LayoutInflater.from(context);
        this.dynamicBeanList = dynamicBeanList;
        mContext=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_hcz_trajectory_map, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ViewHolder itemHolder = (ViewHolder) holder;
        if (getItemViewType(position) == TYPE_TOP) {
            itemHolder.mapLayout.setVisibility(View.VISIBLE);
            itemHolder.dynamicLayout.setVisibility(View.GONE);
            itemHolder.mapView.onResume();
            bdMap = itemHolder.mapView.getMap();
            bdMap.clear();
            if (dynamicBeanList.size() != 0) {
                showMarkerOverlay(itemHolder.mapView);
            }
            itemHolder.zoomIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentZoomLevel = bdMap.getMapStatus().zoom;
                    if (currentZoomLevel <= maxZoomLevel) {
                        bdMap.setMapStatus(MapStatusUpdateFactory.zoomIn());
                        itemHolder.zoomOut.setEnabled(true);
                    } else {
                        itemHolder.zoomIn.setEnabled(false);
                    }
                }
            });
            itemHolder.zoomOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentZoomLevel = bdMap.getMapStatus().zoom;
                    if (currentZoomLevel >= minZoomLevel) {
                        bdMap.setMapStatus(MapStatusUpdateFactory.zoomOut());
                        itemHolder.zoomIn.setEnabled(true);
                    } else {
                        itemHolder.zoomOut.setEnabled(false);
                    }
                }
            });
            itemHolder.location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (builder != null && builder.build() != null)
                        bdMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                    else
                        ActivityUtil.showPopWindow4Tips(mContext, itemHolder.mapLayout, false, true, "孩子还没有运动轨迹", 2000);

                }
            });
            if (dynamicBeanList.size() > 0) {
                itemHolder.childrenHead.setImageResource(Constants.showIds[Constants.child.getImg()]);
                itemHolder.childrenName.setText(Constants.child.getName());
                itemHolder.childrenLocation.setText(dynamicBeanList.get(0).getAddress());
                itemHolder.childrenReflashTime.setText(dynamicBeanList.get(0).getDate());
            } else {
                itemHolder.childrenLocation.setText("");
                itemHolder.childrenReflashTime.setText("");
            }
        } else if (getItemViewType(position) == TYPE_NORMAL) {
            itemHolder.mapLayout.setVisibility(View.GONE);
            itemHolder.dynamicLayout.setVisibility(View.VISIBLE);
            itemHolder.tvTopLine.setVisibility(View.VISIBLE);
            itemHolder.tvAcceptTime.setTextColor(0x806f6f6f);
            itemHolder.tvAcceptStation.setTextColor(0xff6f6f6f);
            itemHolder.tvDot.setBackgroundResource(R.drawable.dynamic_old_icon);
            itemHolder.bindHolder(dynamicBeanList.get(position - 1), getItemViewType(position - 1));
        }

    }

    @Override
    public int getItemCount() {
        return dynamicBeanList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_TOP;
        }
        return TYPE_NORMAL;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAcceptTime, tvAcceptStation;
        private TextView tvTopLine, tvDot;
        private RelativeLayout mapLayout;
        private LinearLayout dynamicLayout;
        private MapView mapView;
        private Button zoomIn, zoomOut, location, reflash;
        private ImageView childrenHead;
        private TextView childrenName, childrenLocation, childrenReflashTime;

        public ViewHolder(View itemView) {
            super(itemView);
            mapLayout = itemView.findViewById(R.id.item_trajectory_map);
            dynamicLayout = itemView.findViewById(R.id.item_trajectory_dynamic);
            tvAcceptTime = (TextView) itemView.findViewById(R.id.tvAcceptTime);
            tvAcceptStation = (TextView) itemView.findViewById(R.id.tvAcceptStation);
            tvTopLine = (TextView) itemView.findViewById(R.id.tvTopLine);
            tvDot = (TextView) itemView.findViewById(R.id.tvDot);
            childrenName = itemView.findViewById(R.id.children_num_name_tv);
            childrenLocation = itemView.findViewById(R.id.children_num_juli_tv);
            childrenReflashTime = itemView.findViewById(R.id.children_num_time_tv);
            childrenHead = itemView.findViewById(R.id.children_head_iv);
            mapView = itemView.findViewById(R.id.bmapview);
            zoomIn = itemView.findViewById(R.id.zoomin);
            zoomOut = itemView.findViewById(R.id.zoomout);
            reflash = itemView.findViewById(R.id.reflash_btn);
            location = itemView.findViewById(R.id.locate_btn);

        }

        public void bindHolder(DynamicBean dynamicBean, int type) {
            tvAcceptTime.setText(ActivityUtil.getHours4Time(dynamicBean.getDate()));
            tvAcceptStation.setText(dynamicBean.getMsg());
        }


    }

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    private void showMarkerOverlay(MapView mapView) {
        //始点图层图标
        BitmapDescriptor startBD = BitmapDescriptorFactory
                .fromResource(R.drawable.trajectory_loaction_start);
        //终点图层图标
        BitmapDescriptor finishBD = BitmapDescriptorFactory
                .fromResource(R.drawable.fragment_main_up1_tips_location);
        bdMap.setOnMapStatusChangeListener(mapStatusChangeListener);
        builder = new MapStatus.Builder();

        //地图设置缩放状态
        List<LatLng> latLngs = new ArrayList<>();


        double lanSum = 0;
        double lonSum = 0;
        //我这里设置地图的缩放中心点为所有点的几何中心点

        for (int i = 0; i < dynamicBeanList.size(); i++) {
            if (i == 0) {
                MarkerOptions oStart = new MarkerOptions();//地图标记类型的图层参数配置类
                oStart.position(dynamicBeanList.get(i).getLatLng());//图层位置点，第一个点为起点
                oStart.icon(startBD);//设置图层图片
                oStart.zIndex(1);//设置图层Index
                //添加起点图层
                Marker mMarkerA = (Marker) (bdMap.addOverlay(oStart));
            } else {
                //添加终点图层
                MarkerOptions oFinish = new MarkerOptions().position(dynamicBeanList.get(i).getLatLng()).icon(finishBD).zIndex(2);
                Marker mMarkerB = (Marker) (bdMap.addOverlay(oFinish));
            }
            latLngs.add(dynamicBeanList.get(i).getLatLng());
            lanSum += dynamicBeanList.get(i).getLatLng().latitude;
            lonSum += dynamicBeanList.get(i).getLatLng().longitude;
        }
        LatLng target = new LatLng(lanSum / latLngs.size(), lonSum / latLngs.size());
        builder.target(target).zoom(15f);
        bdMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        /**
         * 配置线段图层参数类： PolylineOptions
         * ooPolyline.width(13)：线宽
         * ooPolyline.color(0xAAFF0000)：线条颜色红色
         * ooPolyline.points(latLngs)：List<LatLng> latLngs位置点，将相邻点与点连成线就成了轨迹了
         */
        if (latLngs.size() > 1) {
            OverlayOptions ooPolyline = new PolylineOptions().width(10).color(0x90ff4a00).points(latLngs);

            //在地图上画出线条图层，mPolyline：线条图层
            Polyline mPolyline = (Polyline) bdMap.addOverlay(ooPolyline);
            mPolyline.setZIndex(3);
        }

    }

    BaiduMap.OnMapStatusChangeListener mapStatusChangeListener = new BaiduMap.OnMapStatusChangeListener() {
        @Override
        public void onMapStatusChange(MapStatus arg0) {
            maxZoomLevel = bdMap.getMaxZoomLevel();
            minZoomLevel = bdMap.getMinZoomLevel();

            currentZoomLevel = arg0.zoom;

            if (currentZoomLevel >= maxZoomLevel) {
                currentZoomLevel = maxZoomLevel;
            } else if (currentZoomLevel <= minZoomLevel) {
                currentZoomLevel = minZoomLevel;
            }
            currentZoomLevel = arg0.zoom;

        }

        @Override
        public void onMapStatusChangeFinish(MapStatus arg0) {
        }

        @Override
        public void onMapStatusChangeStart(MapStatus arg0) {
        }

        @Override
        public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

        }
    };
}
