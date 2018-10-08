package com.goodsurfing.beans;

import com.baidu.mapapi.model.LatLng;

import java.io.Serializable;

public class LocationBean implements Serializable{


    /**
     * ClientPositionId : 6382
     * ClientId : 28
     * Lat : 28.198
     * Lng : 112.898
     * Address : 中国湖南省长沙市岳麓区麓云路
     * CreatedAt : 2018-08-03 09:36:24
     * Time : 2018-08-03 09:36:24
     */

    private double Lat;
    private double Lng;
    private String Address;
    private String CreatedAt;

    public LatLng getLatLng() {
        return new LatLng(Lat, Lng);
    }

    public double getLat() {
        return Lat;
    }

    public void setLat(double Lat) {
        this.Lat = Lat;
    }

    public double getLng() {
        return Lng;
    }

    public void setLng(double Lng) {
        this.Lng = Lng;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String Address) {
        this.Address = Address;
    }

    public String getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(String CreatedAt) {
        this.CreatedAt = CreatedAt;
    }
}
