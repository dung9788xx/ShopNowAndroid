package com.dungdemo.shopnow;

import android.os.AsyncTask;
import android.util.Log;

import com.dungdemo.shopnow.Model.User;

import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TaskConnect extends AsyncTask<Void,Void,Response> {
    private Map<String,String> map=new HashMap<>(  );
    AsyncResponse output;

    String url;
    public TaskConnect(AsyncResponse output, String url) {
        this.output=output;
        this.url=url;
    }

    @Override
    protected Response doInBackground(Void... voids) {
        String error;
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        RequestBody formBody = makeBuilderFromMap( map )
                .build();
        Request request=null;
      if(map.get("method")=="post"){
      request= new Request.Builder()
                  .url( url )
                  .post( formBody )
                  .build();
      }
        if(map.get("method")=="get"){
            String api_token= map.get("token");
            request= new Request.Builder()
                    .url( url ).addHeader("Authorization",api_token)
                    .get()
                    .build();
        }
        try {
            Response response = client.newCall(request).execute();
            return response;
        }catch (Exception e){
            e.printStackTrace();
            error=e.toString();
            Log.d( "loi",error );
        }
        return null;

    }

    @Override
    protected void onPostExecute(Response s) {
        super.onPostExecute( s );
        output.whenfinish( s );

    }
    public static FormBody.Builder makeBuilderFromMap(Map<String, String> map) {
        FormBody.Builder  formBody = new FormBody.Builder();
        if(!map.isEmpty())
        for (Map.Entry<String, String> entrySet : map.entrySet()) {
            formBody.add(entrySet.getKey(), entrySet.getValue());
        }
        return formBody;
    }
    public void setMap(Map<String,String> map){
        this.map=map;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
