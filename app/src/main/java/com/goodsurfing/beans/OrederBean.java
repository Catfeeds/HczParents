package com.goodsurfing.beans;

import java.io.Serializable;

/**
 * The type App bean.
 */
public class OrederBean implements Serializable {


    private int OrderId;
    private String TransId;
    private String NonceStr;
    private String Title;
    private int ProductId;
    private String Price;

    public int getOrderId() {
        return OrderId;
    }

    public void setOrderId(int OrderId) {
        this.OrderId = OrderId;
    }

    public String getTransId() {
        return TransId;
    }

    public void setTransId(String TransId) {
        this.TransId = TransId;
    }

    public String getNonceStr() {
        return NonceStr;
    }

    public void setNonceStr(String NonceStr) {
        this.NonceStr = NonceStr;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public int getProductId() {
        return ProductId;
    }

    public void setProductId(int ProductId) {
        this.ProductId = ProductId;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String Price) {
        this.Price = Price;
    }
}
