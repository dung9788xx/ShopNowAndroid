package com.dungdemo.shopnow;

import android.os.AsyncTask;
import android.util.Log;

import com.dungdemo.shopnow.utils.ResponeFromServer;

import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TaskConnect extends AsyncTask<Void,Void,ResponeFromServer> {
    private Map<String,String> map=new HashMap<>(  );
    AsyncResponse output;
    Response response;
    String url;
    public TaskConnect(AsyncResponse output, String url) {
        this.output=output;
        this.url=url;
    }
    @Override
    protected ResponeFromServer doInBackground(Void... voids) {
        String error;
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        
        RequestBody formBody = makeBuilderFromMap( map )
                .build();
        String api_token= map.get("token");
        Request request=null;
      if(map.get("method")=="post"){
          if(api_token==null) api_token="";
      request= new Request.Builder()
                  .url( url ).addHeader("Content-Type","application/json").addHeader("Authorization",api_token)
                  .post( formBody )
                  .build();
      }
        if(map.get("method")=="get"){
            request= new Request.Builder()
                    .url( url ).addHeader("Accept","application/json").addHeader("Authorization",api_token)
                    .get()
                    .build();
        }
        if(map.get("method")=="put"){
            request= new Request.Builder()
                    .url( url ).addHeader("Authorization",api_token)
                    .put(formBody)
                    .build();
        }
        try {
            response = client.newCall(request).execute();
              return  new ResponeFromServer(response.code(),response.body().string());
        }catch (Exception e){
            e.printStackTrace();
            error=e.toString();
        }
        return  new ResponeFromServer(0,error);

    }
    @Override
    protected void onPostExecute(ResponeFromServer s) {
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
