package com.goodsurfing.beans;

import java.io.Serializable;

import android.text.TextUtils;

import com.goodsurfing.utils.CharacterParser;

public class Friend implements Serializable {
	private static final long serialVersionUID = 1L;
	private long _id;
	private String pinyinlastname;
	private String nikename;
	private String phone;
	private double latitude;// 纬度
	private double longitude;// 经度
	private String address;
	private String distance;// 距离
	private int id;
	private long time;

	public String getTime() {
		long t = System.currentTimeMillis();
		long d = (t - time) / 1000;
		String timeStr = "";
		if (time > 0) {
			if (d < 10) {
				timeStr = "刚刚";
			} else if (d < 60) {
				timeStr = d + "秒钟前";
			} else if (d < 3600) {
				timeStr = (d / 60) + "分钟前";
			} else if (d < 3600 * 24) {
				timeStr = (d / 3600) + "小时前";
			} else if (d < 3600 * 24 * 30) {
				timeStr = (d / 3600 * 24) + "天前";
			} else {
				timeStr = (d / 3600 * 24 * 30) + "月前";
			}
		}else{
			timeStr = "刚刚";
		}
		return timeStr;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long get_id() {
		return _id;
	}

	public void set_id(long _id) {
		this._id = _id;
	}

	public String getPinyinlastname() {
		return pinyinlastname;
	}

	public void setPinyinlastname(String pinyinlastname) {
		this.pinyinlastname = pinyinlastname;
	}

	public String getNikename() {
		return nikename;
	}

	public String getPhone() {
		return phone;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public void setNikename(String nikename) {
		this.nikename = nikename;
		String sortString = CharacterParser.getInstance().getSelling(nikename);
		if (!TextUtils.isEmpty(sortString)) {
			sortString = sortString.substring(0, 1).toUpperCase();
		}
		setPinyinlastname(sortString.matches("[A-Z]") ? sortString : "#");
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress() {
		return address;
	}

}
