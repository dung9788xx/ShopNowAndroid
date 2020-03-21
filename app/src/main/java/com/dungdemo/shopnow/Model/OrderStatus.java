package com.dungdemo.shopnow.Model;

import com.google.gson.annotations.SerializedName;

public class OrderStatus {
    @SerializedName("status_id")
    int status_id;
    @SerializedName("name")
    String name;

    public OrderStatus(int status_id, String name) {
        this.status_id = status_id;
        this.name = name;
    }

    public int getStatus_id() {
        return status_id;
    }

    public void setStatus_id(int status_id) {
        this.status_id = status_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
