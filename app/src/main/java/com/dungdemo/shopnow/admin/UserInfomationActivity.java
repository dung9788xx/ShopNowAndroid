package com.dungdemo.shopnow.admin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dungdemo.shopnow.AsyncResponse;
import com.dungdemo.shopnow.HostName;
import com.dungdemo.shopnow.model.ProductCategory;
import com.dungdemo.shopnow.model.User;
import com.dungdemo.shopnow.R;
import com.dungdemo.shopnow.TaskConnect;
import com.dungdemo.shopnow.register.CustomerEditActivity;
import com.dungdemo.shopnow.store.ActivityEditProduct;
import com.dungdemo.shopnow.utils.ResponeFromServer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserInfomationActivity extends AppCompatActivity implements AsyncResponse {
    Button btActive;
    User user;
    int user_id=-1;
    TextView active,name,address,phone,username;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_infomation);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.cart_toolbar);
        getSupportActionBar().getCustomView().findViewById(R.id.backImg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        TextView toolbarTitle = getSupportActionBar().getCustomView().findViewById(R.id.tvProductName);
        toolbarTitle.setText("Thông tin người dùng");
        user_id=getIntent().getIntExtra("user_id",-1);
         name=findViewById(R.id.tvName);
         username=findViewById(R.id.tvUsername);
       address=findViewById(R.id.tvAddress);
       phone=findViewById(R.id.tvPhone);
         active=findViewById(R.id.tvActive);
        btActive=findViewById(R.id.btActive);
        progressBar=findViewById(R.id.progress);
        findViewById(R.id.btActive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,String > map=new HashMap<>(  );
                map.put("token",User.getSavedToken(getApplication()));
                map.put("method","get");
               TaskConnect task=new TaskConnect(UserInfomationActivity.this, HostName.host+"/user/deactive/"+user.getUser_id());
               task.setMap( map );
                task.execute();
            }
        });
        findViewById(R.id.btnEdit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent t=new Intent(UserInfomationActivity.this, CustomerEditActivity.class);
                t.putExtra("user_id",user.getUser_id());
                startActivityForResult(t,1);
            }
        });
        loaddata();
    }

    private void loadActiveLayout() {
        if(user.getActive()==1){
            active.setText("Hoạt động");
            active.setTextColor(Color.BLUE);
            btActive.setBackgroundColor(Color.parseColor("#D81B60"));
            btActive.setText("Khóa");
        }else{
            active.setText("Đã bị khóa");
            active.setTextColor(Color.RED);
            btActive.setBackgroundColor(Color.parseColor("#00ACC1"));
            btActive.setText("Mở khóa");
        }
        if(user.getLevel()==1){
            btActive.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void whenfinish(ResponeFromServer output) {
        if(output!=null){
            if(output.code()==200){
                if(user.getActive()==1){
                    user.setActive(0);
                }else{
                    user.setActive(1);
                }
                loadActiveLayout();
                Toast.makeText(this, "Thay đổi thành công!", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Có lỗi xảy ra!", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Kiểm tra lại kết nối", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loaddata();
    }

    private void loaddata() {
        OkHttpClient client = new OkHttpClient();
        String url = HostName.host + "/user/"+user_id;

        Request request = new Request.Builder()
                .url(url).addHeader("Authorization",User.getSavedToken(this))
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = null;
                    json = response.body().string() + "";

                    user = new Gson().fromJson(json, User.class);
                    UserInfomationActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadDataToView();

                        }
                    });
//
                }
            }
        });
    }

    private void loadDataToView() {

        if(username!=null){
            findViewById(R.id.content).setVisibility(View.VISIBLE);
            name.setText(user.getName());
            username.setText(user.getUsername());
            address.setText(user.getLocation().getStreet()+", "+user.getLocation().getWard().getPrefix()
                    +" "+user.getLocation().getWard().getName()+", "+user.getLocation().getDistrict().getPrefix()+
                    " "+user.getLocation().getDistrict().getName()+", "+user.getLocation().getProvince().getName());
            phone.setText(user.getPhone());
            loadActiveLayout();
        }else{
            findViewById(R.id.error).setVisibility(View.VISIBLE);
        }
        progressBar.setVisibility(View.INVISIBLE);
    }
}
