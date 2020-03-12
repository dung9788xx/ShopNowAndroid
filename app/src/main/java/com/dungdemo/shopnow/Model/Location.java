package com.dungdemo.shopnow.Model;

import com.google.gson.annotations.SerializedName;

public class Location {
    @SerializedName("location_id")
    int location_id;
    @SerializedName("name")
    String name;

    public Location(int location_id, String name) {
        this.location_id = location_id;
        this.name = name;
    }

    public int getLocation_id() {
        return location_id;
    }

    public void setLocation_id(int location_id) {
        this.location_id = location_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
