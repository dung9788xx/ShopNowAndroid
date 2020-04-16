package com.dungdemo.shopnow.model;

import com.google.gson.annotations.SerializedName;

public class Cart_Detail {
    @SerializedName("quantity")
    int quantity;
    @SerializedName("note")
    String note;

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

    public Cart_Detail(int quantity, String note, Product product) {
        this.quantity = quantity;
        this.note = note;
        this.product = product;
    }


}
