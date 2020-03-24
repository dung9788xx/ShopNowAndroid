package com.dungdemo.shopnow;

import com.dungdemo.shopnow.utils.ResponeFromServer;

import okhttp3.Response;

public interface AsyncResponse {


        void  whenfinish(ResponeFromServer output);


}
