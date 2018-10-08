package com.goodsurfing.map;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroupOverlay;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.goodsurfing.app.R;
import com.goodsurfing.beans.Friend;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.database.dao.FriendDao;
import com.goodsurfing.main.LoginActivity;
import com.goodsurfing.server.GetBundleUserServer;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResponder;
import com.goodsurfing.server.utils.BaseDataService.DataServiceResult;
import com.goodsurfing.utils.ActivityUtil;
import com.goodsurfing.view.RoundImageView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@SuppressLint("NewApi")
public class MapActivity extends Activity implements OnClickListener {
	private static final int requestcode = 101;
	protected static final int REFRESH = 1;
	protected static final int UP_REFRESH = 2;
	private float currentZoomLevel;
	// 百度地图控件
	private MapView mMapView = null;
	// 百度地图对象
	private BaiduMap bdMap;
	// 普通地图
	private Button normalMapBtn;
	// 卫星地图
	private Button satelliteMapBtn;
	// 实时路况交通图
	private Button trafficMapBtn;
	// 热力图
	private Button headMapBtn;
	// 定位按钮
	private Button locateBtn;
	// 定位模式 （普通-跟随-罗盘）
	private LocationMode currentMode;
	// 定位图标描述
	private BitmapDescriptor currentMarker = null;
	//
	private LocationClient locClient;
	// 记录是否第一次定位
	private boolean isFirstLoc = true;
	// 覆盖物按钮
	private Button overlayBtn;
	//
	private double latitude, longitude;
	// 初始化全局 bitmap 信息，不用时及时 recycle
	protected int childrenIndex;
	private ViewGroupOverlay mOverlay;

	// 构建marker图标
	BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_wz);
	// GroundOptions
	private Button zoomInBtn;
	private Button zoomOutBtn;
	protected float maxZoomLevel;
	protected float minZoomLevel;

	@ViewInject(R.id.tv_title)
	private TextView title;

	@ViewInject(R.id.tv_title_right)
	private TextView right;

	@ViewInject(R.id.activity_map_add_iv)
	private ImageView addImageView;

	@ViewInject(R.id.activity_mode_tab_1)
	private RelativeLayout hintRLayout;
	@ViewInject(R.id.children_info_ll)
	private RelativeLayout childrenInfoRLayout;

	@ViewInject(R.id.children_num_rl)
	private RelativeLayout childrenNumRLayout;

	@ViewInject(R.id.children_num_name_tv)
	private TextView childrenNameTv;
	@ViewInject(R.id.children_num_juli_tv)
	private TextView childrenJuliTv;
	@ViewInject(R.id.children_num_address_tv)
	private TextView childrenAddressTv;
	@ViewInject(R.id.children_num_time_tv)
	private TextView childrenTimeTv;
	@ViewInject(R.id.children_num_info_tv)
	private TextView childrenInfoTv;

	@ViewInject(R.id.childen_head_ll)
	private LinearLayout childrenHeadLayout;

	@ViewInject(R.id.activity_children_head_1)
	private RoundImageView headRIv1;
	@ViewInject(R.id.activity_children_head_2)
	private RoundImageView headRIv2;
	@ViewInject(R.id.activity_children_head_3)
	private RoundImageView headRIv3;
	@ViewInject(R.id.activity_children_head_4)
	private RoundImageView headRIv4;

	private RoundImageView[] headViews;

	private FriendDao friendDao;
	
	@ViewInject(R.id.title_layout)
	private View title_layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		ViewUtils.inject(this);
		init();
	}

	/**
	 * 初始化方法
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	private void init() {
		mMapView = (MapView) findViewById(R.id.bmapview);
		title.setText("孩子手机管控");
		right.setVisibility(View.GONE);
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
		bdMap = mMapView.getMap();
		bdMap.setMapStatus(msu);
		headViews = new RoundImageView[] { headRIv1, headRIv2, headRIv3, headRIv4 };
		normalMapBtn = (Button) findViewById(R.id.normal_map_btn);
		satelliteMapBtn = (Button) findViewById(R.id.satellite_map_btn);
		trafficMapBtn = (Button) findViewById(R.id.traffic_map_btn);
		headMapBtn = (Button) findViewById(R.id.heat_map_btn);
		locateBtn = (Button) findViewById(R.id.locate_btn);
		overlayBtn = (Button) findViewById(R.id.overlay_btn);
		normalMapBtn.setOnClickListener(this);
		satelliteMapBtn.setOnClickListener(this);
		trafficMapBtn.setOnClickListener(this);
		headMapBtn.setOnClickListener(this);
		locateBtn.setOnClickListener(this);
		overlayBtn.setOnClickListener(this);
		addImageView.setOnClickListener(this);
		childrenNumRLayout.setOnClickListener(this);
		normalMapBtn.setEnabled(false);
		currentMode = LocationMode.NORMAL;
		locateBtn.setText("普通");
		mMapView.showZoomControls(false);
		mOverlay = mMapView.getOverlay();
		// 后台刷新服务
		// 开启定位图层
		bdMap.setMyLocationEnabled(true);
		locClient = new LocationClient(this);
		locClient.registerLocationListener(locListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll");// 设置坐标类型
		option.setAddrType("all");
		option.setScanSpan(1000);//
		locClient.setLocOption(option);
		locClient.start();
		if (!locClient.isStarted()) {
			locClient.start();
			locClient.requestLocation();
		} else {
			locClient.requestLocation();
		}
		zoomInBtn = (Button) findViewById(R.id.zoomin);
		zoomOutBtn = (Button) findViewById(R.id.zoomout);
		zoomInBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				currentZoomLevel = bdMap.getMapStatus().zoom;
				if (currentZoomLevel <= maxZoomLevel) {
					bdMap.setMapStatus(MapStatusUpdateFactory.zoomIn());
					zoomOutBtn.setEnabled(true);
				} else {
					ActivityUtil.showPopWindow4Tips(MapActivity.this,title_layout, false, "已经放至最大");
					zoomInBtn.setEnabled(false);
				}
			}
		});
		zoomOutBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				currentZoomLevel = bdMap.getMapStatus().zoom;
				if (currentZoomLevel >= minZoomLevel) {
					bdMap.setMapStatus(MapStatusUpdateFactory.zoomOut());
					zoomInBtn.setEnabled(true);
				} else {
					zoomOutBtn.setEnabled(false);
					ActivityUtil.showPopWindow4Tips(MapActivity.this,title_layout, false, "已经缩至最小");
				}
			}
		});
		// 对marker覆盖物添加点击事件
		bdMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {
				// 获得marker中的数据
				int index = marker.getExtraInfo().getInt("index");
				showChildrenInfo(index);
				return false;
			}
		});

		/**
		 * 地图点击事件
		 */
		bdMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				return false;
			}

			@Override
			public void onMapClick(LatLng latLng) {
				childrenNumRLayout.setVisibility(View.GONE);
			}
		});
		bdMap.setOnMapStatusChangeListener(mapStatusChangeListener);
		friendDao = new FriendDao(this);
		Constants.user.clear();
		Constants.user.addAll(friendDao.getFriendList());
	}

	private void reverseGeoCode(LatLng latLng, final int index) {
		// 创建地理编码检索实例
		GeoCoder geoCoder = GeoCoder.newInstance();
		//
		OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
			// 反地理编码查询结果回调函数
			@Override
			public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
				if (result != null && result.error == SearchResult.ERRORNO.NO_ERROR) {
					Constants.user.get(index).setAddress(result.getAddress());
					showChildrenInfo(index);
				}

			}

			// 地理编码查询结果回调函数
			@Override
			public void onGetGeoCodeResult(GeoCodeResult result) {
				if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
					// 没有检测到结果
				}
			}
		};
		// 设置地理编码检索监听者
		geoCoder.setOnGetGeoCodeResultListener(listener);
		//
		geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
		// 释放地理编码检索实例
		// geoCoder.destroy();
	}

	BaiduMap.OnMapStatusChangeListener mapStatusChangeListener = new OnMapStatusChangeListener() {
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
			if (currentZoomLevel == maxZoomLevel) {
				// 设置地图缩放等级为上限
				MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(currentZoomLevel);
				bdMap.animateMapStatus(u);
				zoomInBtn.setEnabled(false);
			} else if (currentZoomLevel == minZoomLevel) {
				// 设置地图缩放等级为下限
				MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(currentZoomLevel);
				bdMap.animateMapStatus(u);
				zoomOutBtn.setEnabled(false);
			} else {
				if (!zoomInBtn.isEnabled() || !zoomOutBtn.isEnabled()) {
					zoomInBtn.setEnabled(true);
					zoomOutBtn.setEnabled(true);
				}
			}
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

	/**
	 * 定位监听器
	 */
	BDLocationListener locListener = new BDLocationListener() {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null || bdMap == null) {
				return;
			}
			// 构造定位数据
			MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())//
					.direction(100)// 方向
					.latitude(location.getLatitude())//
					.longitude(location.getLongitude())//
					.build();
			// 设置定位数据
			bdMap.setMyLocationData(locData);
			// 第一次定位的时候，那地图中心店显示为定位到的位置
			if (isFirstLoc) {
				isFirstLoc = false;
				latitude = location.getLatitude();
				longitude = location.getLongitude();

				LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
				// MapStatusUpdate描述地图将要发生的变化
				// MapStatusUpdateFactory生成地图将要反生的变化
				MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(ll);
				bdMap.animateMapStatus(msu);
				// bdMap.setMyLocationEnabled(false);
			}
		}

	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.normal_map_btn:
			bdMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
			normalMapBtn.setEnabled(false);
			satelliteMapBtn.setEnabled(true);
			break;
		case R.id.satellite_map_btn:
			bdMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
			satelliteMapBtn.setEnabled(false);
			normalMapBtn.setEnabled(true);
			break;
		case R.id.activity_map_add_iv:
			if ("".equals(Constants.userId)) {
				LoginActivity.gotoLogin(this);
				break;
			}
			startActivityForResult(new Intent(this, AddChildrenActivity.class), requestcode);
			break;
		case R.id.children_num_rl:
			if (bdMap.getMapStatus().zoom > 15 || bdMap.getMapStatus().zoom < 13) {
				MapStatusUpdate msu1 = MapStatusUpdateFactory.zoomTo(15.0f);
				bdMap.setMapStatus(msu1);
			}
			LatLng ll = new LatLng(latitude, longitude);
			// MapStatusUpdate描述地图将要发生的变化
			// MapStatusUpdateFactory生成地图将要反生的变化
			MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(ll);
			bdMap.animateMapStatus(msu);
			break;
		case R.id.tv_title_right:
			startActivityForResult(new Intent(this, AddChildrenActivity.class), requestcode);
			break;
		}
	}

	@OnClick(R.id.activity_children_head_1)
	private void onClickHeadOne(View v) {
		showChildrenInfo(0);
	}

	@OnClick(R.id.activity_children_head_2)
	private void onClickHeadTwo(View v) {
		if (Constants.user.size() == 1) {
			startActivityForResult(new Intent(this, AddChildrenActivity.class), requestcode);
		} else {
			showChildrenInfo(1);
		}
	}

	@OnClick(R.id.activity_children_head_3)
	private void onClickHeadThree(View v) {
		if (Constants.user.size() == 2) {
			startActivityForResult(new Intent(this, AddChildrenActivity.class), requestcode);
		} else {
			showChildrenInfo(2);
		}
	}

	@OnClick(R.id.activity_children_head_4)
	private void onClickHeadFour(View v) {
		if (Constants.user.size() == 3) {
			startActivityForResult(new Intent(this, AddChildrenActivity.class), requestcode);
		} else {
			showChildrenInfo(3);
		}
	}

	/**
	 * 添加标注覆盖物
	 */
	private void addMarkerOverlay() {
		bdMap.clear();
		// 定义marker坐标点
		LatLng point = new LatLng(latitude, longitude);

		// 构建markerOption，用于在地图上添加marker
		OverlayOptions options = new MarkerOptions()//
				.position(point)// 设置marker的位置
				.icon(bitmap)// 设置marker的图标
				.zIndex(9)// 設置marker的所在層級
				.draggable(true);// 设置手势拖拽
		// 在地图上添加marker，并显示
		Marker marker = (Marker) bdMap.addOverlay(options);
	}

	/**
	 * 添加标注覆盖物
	 */
	private void addMarkerFriend(int index) {
		// 定义marker坐标点
		Friend friend = Constants.user.get(index);
		LatLng point = new LatLng(friend.getLatitude(), friend.getLongitude());
		View view = LayoutInflater.from(this).inflate(R.layout.item_map_children_marker, null);
		RoundImageView imageView = (RoundImageView) view.findViewById(R.id.activity_marker_children_head);
		imageView.setBackgroundResource(Constants.showMapIds[friend.getId()]);
		// imageView.setImageBitmap(Util.zoomRoundBitmap(""));
		BitmapDescriptor bitmapf = BitmapDescriptorFactory.fromView(view);
		reverseGeoCode(point, index);
		// 构建markerOption，用于在地图上添加marker
		OverlayOptions options = new MarkerOptions()//
				.position(point)// 设置marker的位置
				.icon(bitmapf)// 设置marker的图标
				.zIndex(9)// 設置marker的所在層級
				.draggable(true).title(friend.getNikename());// 设置手势拖拽
		// 在地图上添加marker，并显示
		Marker marker = (Marker) bdMap.addOverlay(options);
		Bundle bundle = new Bundle();
		bundle.putInt("index", index);
		marker.setExtraInfo(bundle);
		// 将地图移到到最后一个经纬度位置
		// MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(point);
		// bdMap.setMapStatus(u);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
		if (!"".equals(Constants.userId)) {
			if (Constants.user.size() == 0) {
				getBundleUser();
				right.setVisibility(View.GONE);
				childrenNumRLayout.setVisibility(View.GONE);
				childrenHeadLayout.setVisibility(View.GONE);
			} else {
				handler.sendEmptyMessage(REFRESH);
			}
			handler.removeMessages(UP_REFRESH);
			handler.sendEmptyMessageDelayed(UP_REFRESH, 30 * 1000);
		} else {
			right.setVisibility(View.GONE);
			childrenNumRLayout.setVisibility(View.GONE);
			childrenHeadLayout.setVisibility(View.GONE);
		}
	}

	/**
	 * 获取服务器返回数据 userid=24&token=token145208399124&requesttype=14
	 */
	private void getBundleUser() {
		if (!Constants.isNetWork) {
//			ActivityUtil.showPopWindow4Tips(this,title_layout, false, "当前网络不可用，请稍后再试...");
			return;
		}
		String url = Constants.SERVER_URL + "?" + "requesttype=14" + "&userid=" + Constants.userId + "&token=" + Constants.tokenID;

		new GetBundleUserServer(new DataServiceResponder() {

			@Override
			public void onResult(DataServiceResult result) {
				if ("0".equals(result.code)) {
					if(null==handler)return;
					handler.sendEmptyMessage(REFRESH);
					if (Constants.user.size() == 0) {
						handler.removeMessages(UP_REFRESH);
					} else {
						handler.removeMessages(UP_REFRESH);
						handler.sendEmptyMessageDelayed(UP_REFRESH, 30 * 1000);
					}
				}else {
					ActivityUtil.showPopWindow4Tips(MapActivity.this, title_layout, false, result.extra+"");
				}
			}

			@Override
			public void onFailure() {

			}
		}, url, this).execute();
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case REFRESH:
				if (Constants.user.size() > 0) {
					hintRLayout.setVisibility(View.GONE);
					childrenNumRLayout.setVisibility(View.VISIBLE);
					childrenHeadLayout.setVisibility(View.VISIBLE);
					bdMap.clear();
					for (int i = 0; i < Constants.user.size(); i++) {
						addMarkerFriend(i);
					}
					showChildrenHead(Constants.user.size());
					showChildrenInfo(0);
				} else {
					hintRLayout.setVisibility(View.VISIBLE);
					right.setVisibility(View.GONE);
					childrenNumRLayout.setVisibility(View.GONE);
					childrenHeadLayout.setVisibility(View.GONE);
				}
				break;
			case UP_REFRESH:
				getBundleUser();
				handler.sendEmptyMessageDelayed(UP_REFRESH, 30 * 1000);
				break;
			}
		}

	};

	private void showChildrenHead(int i) {
		headRIv1.setVisibility(View.VISIBLE);
		headRIv2.setVisibility(View.VISIBLE);
		headRIv3.setVisibility(View.VISIBLE);
		headRIv4.setVisibility(View.VISIBLE);
		switch (i) {
		case 1:
			headRIv1.setBackgroundResource(Constants.showBottomIds[Constants.user.get(0).getId()]);
			headRIv2.setBackgroundResource(R.drawable.map_add_children_iv);
			headRIv3.setVisibility(View.GONE);
			headRIv4.setVisibility(View.GONE);
			break;
		case 2:
			headRIv1.setBackgroundResource(Constants.showBottomIds[Constants.user.get(0).getId()]);
			headRIv2.setBackgroundResource(Constants.showBottomIds[Constants.user.get(1).getId()]);
			headRIv3.setBackgroundResource(R.drawable.map_add_children_iv);
			headRIv4.setVisibility(View.GONE);
			break;
		case 3:
			headRIv1.setBackgroundResource(Constants.showBottomIds[Constants.user.get(0).getId()]);
			headRIv2.setBackgroundResource(Constants.showBottomIds[Constants.user.get(1).getId()]);
			headRIv3.setBackgroundResource(Constants.showBottomIds[Constants.user.get(2).getId()]);
			headRIv4.setBackgroundResource(R.drawable.map_add_children_iv);
			break;
		case 4:
			headRIv1.setBackgroundResource(Constants.showBottomIds[Constants.user.get(0).getId()]);
			headRIv2.setBackgroundResource(Constants.showBottomIds[Constants.user.get(1).getId()]);
			headRIv3.setBackgroundResource(Constants.showBottomIds[Constants.user.get(2).getId()]);
			headRIv4.setBackgroundResource(Constants.showBottomIds[Constants.user.get(3).getId()]);
			break;
		}
	}

	private void showChildrenInfo(final int i) {
		childrenNumRLayout.setVisibility(View.VISIBLE);
		final Friend friend = Constants.user.get(i);
		selectBottomHead(i);
		LatLng point = new LatLng(friend.getLatitude(), friend.getLongitude());
		childrenNameTv.setText(friend.getNikename());
		childrenAddressTv.setText(friend.getAddress());
		String distance = distanceByLatlng(latitude, longitude, friend.getLatitude(), friend.getLongitude());
		Constants.user.get(i).setDistance(distance);
		childrenJuliTv.setText(distance);
		childrenTimeTv.setText(friend.getTime());
		childrenInfoTv.setTag(i);
		childrenInfoTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MapActivity.this, InfoChildrenActivity.class);
				childrenIndex = i;
				intent.putExtra("userIndex", i);
				startActivityForResult(intent, 1);
			}
		});
		// MapStatusUpdateFactory生成地图将要反生的变化
		if (bdMap.getMapStatus().zoom > 15 || bdMap.getMapStatus().zoom < 13) {
			MapStatusUpdate msu1 = MapStatusUpdateFactory.zoomTo(15.0f);
			bdMap.setMapStatus(msu1);
		}
		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(point);
		bdMap.animateMapStatus(msu);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1 && resultCode == RESULT_OK) {
			boolean isdelete = data.getExtras().getBoolean("isdelete");
			if (!isdelete) {
				bdMap.clear();
				handler.sendEmptyMessage(REFRESH);
			} else {
				if (Constants.user.size() == 0) {
					bdMap.clear();
					hintRLayout.setVisibility(View.VISIBLE);
					if (bdMap.getMapStatus().zoom > 15 || bdMap.getMapStatus().zoom < 13) {
						MapStatusUpdate msu1 = MapStatusUpdateFactory.zoomTo(15.0f);
						bdMap.setMapStatus(msu1);
					}
					LatLng ll = new LatLng(latitude, longitude);
					MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(ll);
					bdMap.animateMapStatus(msu);
				} else {
					bdMap.clear();
					handler.sendEmptyMessage(REFRESH);
				}
			}
		}
	}

	private void selectBottomHead(int i) {
		for (int j = 0; j < Constants.user.size(); j++) {
			headViews[j].setSelected(false);
		}
		headViews[i].setSelected(true);
	}

	private String distanceByLatlng(double lat_a, double lng_a, double lat_b, double lng_b) {
		double pk = 180 / 3.1415926;
		double a1 = lat_a / pk;
		double a2 = lng_a / pk;
		double b1 = lat_b / pk;
		double b2 = lng_b / pk;
		double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
		double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
		double t3 = Math.sin(a1) * Math.sin(b1);
		double tt = Math.acos(t1 + t2 + t3);
		int distance = (int) (6366000 * tt);
		int k = distance / 1000;
		if (k > 0) {
			double d = 6366000 * tt / 1000.0;
			return "[相距" + String.format("%.2f", d) + "千米]";
		}
		return "[相距" + distance + "米]";
	}

	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	@Override
	protected void onDestroy() {
		// 退出时销毁定位
		locClient.stop();
		bdMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		// 回收bitmip资源
		bitmap.recycle();
		handler.removeMessages(UP_REFRESH);
		handler = null;
		super.onDestroy();
	}

	@OnClick(R.id.iv_title_left)
	public void onHeadBackClick(View view) {
		onBackPressed();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, R.anim.base_slide_right_out);
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_remain);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
		overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_remain);
	}

}
