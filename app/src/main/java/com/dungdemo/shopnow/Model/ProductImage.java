package com.dungdemo.shopnow.Model;

import com.google.gson.annotations.SerializedName;

public class ProductImage {
    @SerializedName("image_id")
    int image_id;
    @SerializedName("base64")
    String base64;

    public ProductImage(int image_id, String base64) {
        this.image_id = image_id;
        this.base64 = base64;
    }

    public int getImage_id() {
        return image_id;
    }

    public void setImage_id(int image_id) {
        this.image_id = image_id;
    }

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }
}
