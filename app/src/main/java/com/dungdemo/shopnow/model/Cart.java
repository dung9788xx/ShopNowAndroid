package com.dungdemo.shopnow.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Cart {
    @SerializedName("cart_id")
    String cart_id;
    @SerializedName("user_id")
    int user_id;
    @SerializedName("detail")
    List<Cart_Detail> detail;

    public String getCart_id() {
        return cart_id;
    }

    public void setCart_id(String cart_id) {
        this.cart_id = cart_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public List<Cart_Detail> getDetail() {
        return detail;
    }

    public void setDetail(List<Cart_Detail> detail) {
        this.detail = detail;
    }

    public Cart(String cart_id, int user_id, List<Cart_Detail> detail) {
        this.cart_id = cart_id;
        this.user_id = user_id;
        this.detail = detail;
    }
}
