package com.goodsurfing.beans;

import java.io.Serializable;

public class ModeBean implements Serializable {

    /**
     * ModeId : 1
     * Name : 畅游模式
     * Status : 1
     * CreatedAt : 2018-03-19 02:28:27
     * Desc : 畅游模式
     * Img : http://10.10.20.118:285/static/img/wan.png
     * Check : 0
     */

    private int ModeId;
    private String Name;
    private String Status;
    private String CreatedAt;
    private String Desc;
    private String Img;
    private int Check;

    public int getModeId() {
        return ModeId;
    }

    public void setModeId(int ModeId) {
        this.ModeId = ModeId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

    public String getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(String CreatedAt) {
        this.CreatedAt = CreatedAt;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String Desc) {
        this.Desc = Desc;
    }

    public String getImg() {
        return Img;
    }

    public void setImg(String Img) {
        this.Img = Img;
    }

    public int getCheck() {
        return Check;
    }

    public void setCheck(int Check) {
        this.Check = Check;
    }
}
