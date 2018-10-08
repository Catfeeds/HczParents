package com.goodsurfing.beans;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * The type App bean.
 */
public class AppBean implements Serializable {

    /**
     * ClientAppId : 849
     * ClientId : 24
     * Package : com.tencent.android.qqdownloader
     * Name : 应用宝
     * Status : 1
     * System : 2
     * CreatedAt : null
     * UpdatedAt : 2018-05-30 17:29:10
     * Img : null
     * CateName : null
     */

    private int ClientAppId;
    private int ClientId;
    private String Package;
    private String Name;
    private int Status;
    private int System;
    private String CreatedAt;
    private String UpdatedAt;
    private String Img;
    private String CateName;
    /**
     * AvailableTime : 86400
     * CreatedAt : null
     * usedtime : 0
     */

    private int AvailableTime;
    private int usedtime;

    public int getClientAppId() {
        return ClientAppId;
    }

    public void setClientAppId(int clientAppId) {
        ClientAppId = clientAppId;
    }

    public int getClientId() {
        return ClientId;
    }

    public void setClientId(int clientId) {
        ClientId = clientId;
    }

    public String getPackage() {
        return Package;
    }

    public void setPackage(String aPackage) {
        Package = aPackage;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public int getSystem() {
        return System;
    }

    public void setSystem(int system) {
        System = system;
    }

    public String getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        CreatedAt = createdAt;
    }

    public String getUpdatedAt() {
        return UpdatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        UpdatedAt = updatedAt;
    }

    public String getImg() {
        return Img;
    }

    public void setImg(String img) {
        Img = img;
    }

    public String getCateName() {
        return CateName;
    }

    public void setCateName(String cateName) {
        CateName = cateName;
    }

    public int getAvailableTime() {
        return AvailableTime;
    }

    public void setAvailableTime(int AvailableTime) {
        this.AvailableTime = AvailableTime;
    }


    public int getUsedtime() {
        return usedtime;
    }

    public void setUsedtime(int usedtime) {
        this.usedtime = usedtime;
    }
}
