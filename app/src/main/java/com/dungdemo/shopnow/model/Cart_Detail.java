package com.dungdemo.shopnow.model;

import com.google.gson.annotations.SerializedName;

public class Cart_Detail {
    @SerializedName("quantity")
    int quantity;
    @SerializedName("note")
    String note;
    @SerializedName("price")
    int price;
    @SerializedName("product")
    Product product;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Cart_Detail(int quantity, String note, int price, Product product) {
        this.quantity = quantity;
        this.note = note;
        this.price = price;
        this.product = product;
    }
}
