package com.dungdemo.shopnow.Model;

import android.util.Log;

import com.dungdemo.shopnow.HostName;
import com.google.gson.annotations.SerializedName;

public class SliderItem {
    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    @SerializedName("product_id")
    private int product_id;
    @SerializedName("productName")
    private String productName;
    @SerializedName("imageUrl")
    private String imageUrl;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public SliderItem(int product_id, String productName, String imageUrl) {
        this.product_id = product_id;
        this.productName = productName;
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return HostName.imgurl+product_id+"/"+imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

