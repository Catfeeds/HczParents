package com.goodsurfing.beans;

import java.io.Serializable;

public class Parents implements Serializable {


    /**
     * ServerId : 29
     * UUID : 862949037554032e2de8fe16fb6137a
     * Name : null
     * Password : $2y$10$W3TzOFgrT0WmO.w0pa5RTunYuYe7OjGjJe6Opoytg0gMZ9wM6jm4y
     * Mobile : 18373174023
     * Free : 1
     * CreatedAt : 2018-06-07 13:51:14
     * UpdatedAt : 2018-07-09 06:24:03
     * ExpirationTime : null
     * DeviceToken : 862949037554032e2de8fe16fb6137a
     */

    private int ServerId;
    private String UUID;
    private Object Name;
    private String Password;
    private String Mobile;
    private String Free;
    private String CreatedAt;
    private String UpdatedAt;
    private Object ExpirationTime;
    private String DeviceToken;

    public Parents() {
    }

    public Parents(int serverId, String UUID, Object name, String password, String mobile, String free, String createdAt, String updatedAt, Object expirationTime, String deviceToken) {
        ServerId = serverId;
        this.UUID = UUID;
        Name = name;
        Password = password;
        Mobile = mobile;
        Free = free;
        CreatedAt = createdAt;
        UpdatedAt = updatedAt;
        ExpirationTime = expirationTime;
        DeviceToken = deviceToken;
    }

    public String getServerId() {
        return ServerId+"";
    }

    public void setServerId(int ServerId) {
        this.ServerId = ServerId;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public Object getName() {
        return Name;
    }

    public void setName(Object Name) {
        this.Name = Name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String Mobile) {
        this.Mobile = Mobile;
    }

    public String getFree() {
        return Free;
    }

    public void setFree(String Free) {
        this.Free = Free;
    }

    public String getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(String CreatedAt) {
        this.CreatedAt = CreatedAt;
    }

    public String getUpdatedAt() {
        return UpdatedAt;
    }

    public void setUpdatedAt(String UpdatedAt) {
        this.UpdatedAt = UpdatedAt;
    }

    public Object getExpirationTime() {
        return ExpirationTime;
    }

    public void setExpirationTime(Object ExpirationTime) {
        this.ExpirationTime = ExpirationTime;
    }

    public String getDeviceToken() {
        return DeviceToken;
    }

    public void setDeviceToken(String DeviceToken) {
        this.DeviceToken = DeviceToken;
    }
}
