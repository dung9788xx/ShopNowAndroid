package com.dungdemo.shopnow.Model;

import com.google.gson.annotations.SerializedName;

public class Store {
    @SerializedName("name")
    String name;
    @SerializedName("description")
    String description;
    @SerializedName("location")
    Location location;
    public Store(String name, String description, Location location) {
        this.name = name;
        this.description = description;
        this.location = location;
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
