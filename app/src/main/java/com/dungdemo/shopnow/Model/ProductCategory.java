package com.dungdemo.shopnow.Model;

import com.google.gson.annotations.SerializedName;

public class ProductCategory {
    @SerializedName("category_id")
    int category_id;
    @SerializedName("name")
    String name;

    public ProductCategory(int category_id, String name) {
        this.category_id = category_id;
        this.name = name;
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
}
