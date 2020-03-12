package com.dungdemo.shopnow.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Serializable {
    @SerializedName("id")
    int user_id;
    @SerializedName("name")
    String name;
    @SerializedName("username")
    String username;
    @SerializedName("address")
    String address;
    @SerializedName("phone")
    String phone;
    @SerializedName("level")
    int level;
    @SerializedName("active")
    int active;
    @SerializedName("blocked")
    int blocked;
    @SerializedName("api_token")
    String api_token;
    @SerializedName("store")
    Store store;

    public User(int user_id, String name, String username, String address, String phone, int level, int active, int blocked, String api_token, Store store) {
        this.user_id = user_id;
        this.name = name;
        this.username = username;
        this.address = address;
        this.phone = phone;
        this.level = level;
        this.active = active;
        this.blocked = blocked;
        this.api_token = api_token;
        this.store = store;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public int getBlocked() {
        return blocked;
    }

    public void setBlocked(int blocked) {
        this.blocked = blocked;
    }

    public String getApi_token() {
        return api_token;
    }

    public void setApi_token(String api_token) {
        this.api_token = api_token;
    }
    public void saveToken(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token","Bearer "+this.getApi_token());
        editor.apply();
    }
    public static String  getSavedToken(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String token = preferences.getString("token", "");
        if(!token.equalsIgnoreCase(""))
        {
           return token;
        }
        return "";
    }
}
