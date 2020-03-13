package com.dungdemo.shopnow.Model;

import com.google.gson.annotations.SerializedName;

public class Store {
    @SerializedName("name")
    String name;
    @SerializedName("description")
    String description;
    @SerializedName("location")
    Location location;
    @SerializedName("approval")
    int approval;
    @SerializedName("notification")
    int notification;
    @SerializedName("blocked")
    int blocked;

    public Store(String name, String description, Location location, int approval, int notification, int blocked) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.approval = approval;
        this.notification = notification;
        this.blocked = blocked;
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
