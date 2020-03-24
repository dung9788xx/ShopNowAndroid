package com.dungdemo.shopnow.admin;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.dungdemo.shopnow.AsyncResponse;
import com.dungdemo.shopnow.HostName;
import com.dungdemo.shopnow.Model.User;
import com.dungdemo.shopnow.R;
import com.dungdemo.shopnow.TaskConnect;
import com.dungdemo.shopnow.utils.ResponeFromServer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AdminAlertService extends Service implements AsyncResponse {
    Handler handler;
    public static final String CHANNEL_ID = "ForegroundServiceChannel";

    public AdminAlertService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                Map<String,String > map=new HashMap<>(  );
                map.put("token",User.getSavedToken(getApplication()));
                map.put("method","get");
                TaskConnect task=new TaskConnect(AdminAlertService.this, HostName.host+"/store/getNewStoreNotification");
                task.setMap( map );
                task.execute();
                handler.postDelayed(this, 2000);
            }
        };

        handler.postDelayed(r, 1000);
        return START_STICKY;
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
    private void sendNotification(String message, String title, int id) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, AdminActivity.class);
        notificationIntent.putExtra("newStore",1);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_store_black_24dp)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
        }else {
            Intent intent = new Intent(this, AdminActivity.class);
            intent.putExtra("newStore",1);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */,
                    intent, PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_store_black_24dp)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(id /* ID of notification */,
                    notificationBuilder.build());
        }
    }
    @Override
    public void whenfinish(ResponeFromServer output) {
        if(output!=null){
            if(output.code()==200){
                try {
                    JSONObject object=new JSONObject(output.getBody());
                    int count=object.getInt("count");
                    if(count>0){
                       sendNotification("Có "+count+"hàng cần phê duyệt","Shop Now",1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("lol","destory");
    }

}
