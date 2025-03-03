package com.dungdemo.shopnow.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {
    @SerializedName("product_id")
    int product_id;
    @SerializedName("name")
    String name;
    @SerializedName("description")
    String description;
    @SerializedName("promotion_price")
    int promotion_price;
    @SerializedName("price")
    int price;
    @SerializedName("amount")
    int amount;
    @SerializedName("isSelling")
    int isSelling;
    @SerializedName("category")
    ProductCategory category;
    @SerializedName("images")
    List<ProductImage> images;
    @SerializedName("store")
    Store store;

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public Product(){

    };

    public Product(int product_id, String name, String description, int promotion_price, int price, int amount, int isSelling, ProductCategory category, List<ProductImage> images, Store store) {
        this.product_id = product_id;
        this.name = name;
        this.description = description;
        this.promotion_price = promotion_price;
        this.price = price;
        this.amount = amount;
        this.isSelling = isSelling;
        this.category = category;
        this.images = images;
        this.store = store;
    }

    public int getPromotion_price() {
        return promotion_price;
    }

    public void setPromotion_price(int promotion_price) {
        this.promotion_price = promotion_price;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getIsSelling() {
        return isSelling;
    }

    public void setIsSelling(int isSelling) {
        this.isSelling = isSelling;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public List<ProductImage> getImages() {
        return images;
    }

    @Override
    public String toString() {
        return "Product{" +
                "product_id=" + product_id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", amount=" + amount +
                ", isSelling=" + isSelling +
                ", category=" + category +
                ", images=" + images +
                '}';
    }


    public void setImages(List<ProductImage> images) {
        this.images = images;
    }
}