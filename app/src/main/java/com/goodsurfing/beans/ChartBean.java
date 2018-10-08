package com.goodsurfing.beans;

import java.io.Serializable;

public class ChartBean implements Serializable{


    /**
     * utime : 21578
     * CateName : 新闻
     * ratio : 1
     */

    private int utime;
    private String CateName;
    private int ratio;

    public int getUtime() {
        return utime;
    }

    public void setUtime(int utime) {
        this.utime = utime;
    }

    public String getCateName() {
        return CateName;
    }

    public void setCateName(String CateName) {
        this.CateName = CateName;
    }

    public int getRatio() {
        return ratio;
    }

    public void setRatio(int ratio) {
        this.ratio = ratio;
    }
}
