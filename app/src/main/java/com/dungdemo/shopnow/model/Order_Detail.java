package com.dungdemo.shopnow.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Order_Detail implements Serializable {
    @SerializedName("order_id")
    int order_id;
    @SerializedName("product_id")
    int product_id;
    @SerializedName("name")
    String name;
    @SerializedName("price")
    int price;
    @SerializedName("quantity")
    int quantity;

    public Order_Detail(int order_id, int product_id, String name, int price, int quantity) {
        this.order_id = order_id;
        this.product_id = product_id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
