package com.goodsurfing.beans;

import com.baidu.mapapi.model.LatLng;

import java.io.Serializable;

public class DynamicBean implements Serializable{

    public static final int TYPE_APP = 2;
    public static final int TYPE_LOCATION = 1;
    public static final int TYPE_OTHER = 3;
    /**
     * ClientPositionId : 430
     * ClientId : 12
     * Lat : 28.228711
     * Lng : 112.954176
     * Address : 中国湖南省长沙市岳麓区银双路
     * CreatedAt : 2018-06-12 07:02:48
     * Time : 2018-06-12 07:02:46
     */

    private String Lat;
    private String Lng;
    private String Address;
    private String Date;
    private String Msg;
    private int Type;

    public DynamicBean() {
    }

    public String getLat() {
        return Lat;
    }

    public void setLat(String lat) {
        Lat = lat;
    }

    public String getLng() {
        return Lng;
    }

    public void setLng(String lng) {
        Lng = lng;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public LatLng getLatLng() {
        return new LatLng(Double.parseDouble(Lat), Double.parseDouble(Lng));
    }

}
