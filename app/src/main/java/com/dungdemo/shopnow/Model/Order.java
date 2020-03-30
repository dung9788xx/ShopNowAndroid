package com.dungdemo.shopnow.Model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {
    @SerializedName("order_id")
    int order_id;
    @SerializedName("store")
    Store store;
    @SerializedName("user")
    User user;
    @SerializedName("shipping_address")
    String shipping_address;
    @SerializedName("shipping_phone")
    String shipping_phone;
    @SerializedName("isNotification")
    int isNotification;
    @SerializedName("date")
    String date;
    @SerializedName("status")
    OrderStatus status;
    @SerializedName("detail")
    List<Order_Detail> order_details;

    public Order(int order_id, Store store, User user, String shipping_address, String shipping_phone, int isNotification, String date, OrderStatus status, List<Order_Detail> order_details) {
        this.order_id = order_id;
        this.store = store;
        this.user = user;
        this.shipping_address = shipping_address;
        this.shipping_phone = shipping_phone;
        this.isNotification = isNotification;
        this.date = date;
        this.status = status;
        this.order_details = order_details;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getShipping_address() {
        return shipping_address;
    }

    public void setShipping_address(String shipping_address) {
        this.shipping_address = shipping_address;
    }

    public String getShipping_phone() {
        return shipping_phone;
    }

    public void setShipping_phone(String shipping_phone) {
        this.shipping_phone = shipping_phone;
    }

    public int getIsNotification() {
        return isNotification;
    }

    public void setIsNotification(int isNotification) {
        this.isNotification = isNotification;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<Order_Detail> getOrder_details() {
        return order_details;
    }

    public void setOrder_details(List<Order_Detail> order_details) {
        this.order_details = order_details;
    }

    @Override
    public String toString() {
        return "Order{" +
                "order_id=" + order_id +
                ", store=" + store +
                ", user=" + user +
                ", shipping_address='" + shipping_address + '\'' +
                ", shipping_phone='" + shipping_phone + '\'' +
                ", isNotification=" + isNotification +
                ", date='" + date + '\'' +
                ", status=" + status +
                ", order_details=" + order_details +
                '}';
    }
}
