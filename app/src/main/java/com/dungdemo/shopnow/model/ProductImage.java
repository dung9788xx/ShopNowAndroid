package com.dungdemo.shopnow.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProductImage implements Serializable {
    @SerializedName("image_id")
    int image_id;
    @SerializedName("image_name")
    String image_name;

    public ProductImage(int image_id, String image_name) {
        this.image_id = image_id;
        this.image_name = image_name;
    }

    public int getImage_id() {
        return image_id;
    }

    public void setImage_id(int image_id) {
        this.image_id = image_id;
    }

    public String getImage_name() {
        return image_name;
    }

    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }
}
