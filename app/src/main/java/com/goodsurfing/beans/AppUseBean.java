package com.goodsurfing.beans;

import java.io.Serializable;

public class AppUseBean implements Serializable{


    /**
     * utime : 21578
     * Appname : 今日头条（新闻阅读）
     * CateName : 新闻
     * Img : http://pp.myapp.com/ma_icon/0/icon_213141_1521020457/96
     */

    private String utime;
    private String Appname;
    private String CateName;
    private String Img;

    public String getUtime() {
        return utime;
    }

    public void setUtime(String utime) {
        this.utime = utime;
    }

    public String getAppname() {
        return Appname;
    }

    public void setAppname(String Appname) {
        this.Appname = Appname;
    }

    public String getCateName() {
        return CateName;
    }

    public void setCateName(String CateName) {
        this.CateName = CateName;
    }

    public String getImg() {
        return Img;
    }

    public void setImg(String Img) {
        this.Img = Img;
    }
}
