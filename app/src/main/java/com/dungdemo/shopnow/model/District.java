package com.dungdemo.shopnow.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class District implements Serializable {
    @SerializedName("district_id")
    int district_id;
    @SerializedName("name")
    String name;
    @SerializedName("prefix")
    String prefix;
    public int getDistrict_id() {
        return district_id;
    }

    public void setDistrict_id(int district_id) {
        this.district_id = district_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public District(int district_id, String name, String prefix) {
        this.district_id = district_id;
        this.name = name;
        this.prefix = prefix;
    }
}
