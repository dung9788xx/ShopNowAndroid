package com.dungdemo.shopnow.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Location implements Serializable {
    @SerializedName("location_id")
    int location_id;
    @SerializedName("province")
    Province province;
    @SerializedName("district")
    District district;
    @SerializedName("ward")
    Ward ward;
    @SerializedName("street")
    String street;

    public int getLocation_id() {
        return location_id;
    }

    public void setLocation_id(int location_id) {
        this.location_id = location_id;
    }

    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
    }

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public Ward getWard() {
        return ward;
    }

    public void setWard(Ward ward) {
        this.ward = ward;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Location(int location_id, Province province, District district, Ward ward, String street) {
        this.location_id = location_id;
        this.province = province;
        this.district = district;
        this.ward = ward;
        this.street = street;
    }
}
