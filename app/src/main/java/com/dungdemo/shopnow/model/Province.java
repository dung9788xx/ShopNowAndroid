package com.dungdemo.shopnow.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Province implements Serializable {
    @SerializedName("province_id")
    int province_id;
    @SerializedName("name")
    String name;

    public int getProvince_id() {
        return province_id;
    }

    public void setProvince_id(int province_id) {
        this.province_id = province_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Province(int province_id, String name) {
        this.province_id = province_id;
        this.name = name;
    }
}
