package com.dungdemo.shopnow.admin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("lol","rece");
        Intent myIntent = new Intent(context, AdminAlertService.class);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            ContextCompat.startForegroundService(context, myIntent);
        } else {
            context.startService(myIntent);
        }
    }
}
