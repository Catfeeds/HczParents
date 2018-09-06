package com.goodsurfing.beans;

import com.github.mikephil.charting.data.Entry;

import java.io.Serializable;
import java.util.List;

public class PieEntry   implements Serializable{

    private List<ChartBean> Catelist;
    private List<AppUseBean> Applist;

    public List<ChartBean> getCatelist() {
        return Catelist;
    }

    public void setCatelist(List<ChartBean> Catelist) {
        this.Catelist = Catelist;
    }

    public List<AppUseBean> getApplist() {
        return Applist;
    }

    public void setApplist(List<AppUseBean> Applist) {
        this.Applist = Applist;
    }

}
