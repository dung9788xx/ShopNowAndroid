package com.dungdemo.shopnow.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProductCategory implements Serializable {
    @SerializedName("category_id")
    int category_id;
    @SerializedName("name")
    String name;
    @SerializedName("detail")
    String detail;

    public ProductCategory(int category_id, String name, String detail) {
        this.category_id = category_id;
        this.name = name;
        this.detail = detail;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
