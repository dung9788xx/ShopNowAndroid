package com.dungdemo.shopnow.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Store implements Serializable {
    @SerializedName("store_id")
    int store_id;
    @SerializedName("name")
    String name;
    @SerializedName("description")
    String description;
    @SerializedName("approval")
    int approval;
    @SerializedName("notification")
    int notification;
    @SerializedName("blocked")
    int blocked;

    public int getStore_id() {
        return store_id;
    }

    public void setStore_id(int store_id) {
        this.store_id = store_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getApproval() {
        return approval;
    }

    public void setApproval(int approval) {
        this.approval = approval;
    }

    public int getNotification() {
        return notification;
    }

    public void setNotification(int notification) {
        this.notification = notification;
    }

    public int getBlocked() {
        return blocked;
    }

    public void setBlocked(int blocked) {
        this.blocked = blocked;
    }

    public Store(int store_id, String name, String description, int approval, int notification, int blocked) {
        this.store_id = store_id;
        this.name = name;
        this.description = description;
        this.approval = approval;
        this.notification = notification;
        this.blocked = blocked;
    }
}
