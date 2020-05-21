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
import android.widget.TextView;
import android.widget.Toast;

import com.dungdemo.shopnow.AsyncResponse;
import com.dungdemo.shopnow.HostName;
import com.dungdemo.shopnow.model.User;
import com.dungdemo.shopnow.R;
import com.dungdemo.shopnow.TaskConnect;
import com.dungdemo.shopnow.register.StoreEditActivity;
import com.dungdemo.shopnow.utils.ResponeFromServer;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StoreInfomationActivity extends AppCompatActivity implements AsyncResponse {
    Button btActive;
    User user;
    TextView active, name, username, address, shopAddress, phone, tvStoreName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_infomation);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.cart_toolbar);
        getSupportActionBar().getCustomView().findViewById(R.id.backImg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        TextView toolbarTitle = getSupportActionBar().getCustomView().findViewById(R.id.tvProductName);
        toolbarTitle.setText("Thông tin cửa hàng");


        tvStoreName = findViewById(R.id.tvStoreName);
        name = findViewById(R.id.tvName);
        shopAddress = findViewById(R.id.tvShopaddress);

        username = findViewById(R.id.tvUsername);
        address = findViewById(R.id.tvAddress);
        phone = findViewById(R.id.tvPhone);
        active = findViewById(R.id.tvActive);
        btActive = findViewById(R.id.btActive);
        user = new User();
        user.setUser_id(getIntent().getIntExtra("user_id", -1));

        loadDataFromServer();


        findViewById(R.id.btActive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getStore().getApproval() == 0) {
                    Map<String, String> map = new HashMap<>();
                    map.put("token", User.getSavedToken(getApplication()));
                    map.put("method", "get");
                    TaskConnect task = new TaskConnect(StoreInfomationActivity.this, HostName.host + "/store/approvalStore/" + user.getStore().getStore_id());
                    task.setMap(map);
                    task.execute();
                } else {
                    Map<String, String> map = new HashMap<>();
                    map.put("token", User.getSavedToken(getApplication()));
                    map.put("method", "get");
                    TaskConnect task = new TaskConnect(StoreInfomationActivity.this, HostName.host + "/store/blockStore/" + user.getStore().getStore_id());
                    task.setMap(map);
                    task.execute();
                }
            }
        });
        findViewById(R.id.btnEdit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent t = new Intent(StoreInfomationActivity.this, StoreEditActivity.class);
                t.putExtra("user_id", user.getUser_id());
                startActivityForResult(t, 1);
            }
        });
    }

    private void loadDataFromServer() {
        OkHttpClient client = new OkHttpClient();
        String url = HostName.host + "/user/" + user.getUser_id();

        Request request = new Request.Builder()
                .url(url).addHeader("Authorization", User.getSavedToken(this))
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
                    StoreInfomationActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            findViewById(R.id.progress).setVisibility(View.INVISIBLE);
                            loadDataToView();


                        }
                    });
//
                } else {
                    StoreInfomationActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            findViewById(R.id.progress).setVisibility(View.INVISIBLE);
                            findViewById(R.id.error).setVisibility(View.VISIBLE);


                        }
                    });
                }
            }
        });
    }

    public void loadDataToView() {
        tvStoreName.setText(user.getStore().getName());
        name.setText(user.getName());
        username.setText(user.getUsername());
        shopAddress.setText(user.getLocation().getStreet() + ", " + user.getLocation().getWard().getPrefix() + " " + user.getLocation().getWard().getName()
                + ", " + user.getLocation().getDistrict().getPrefix() + " " + user.getLocation().getDistrict().getName()
                + ", " + user.getLocation().getProvince().getName());
        phone.setText(user.getPhone());
        loadActiveLayout();

        findViewById(R.id.content).setVisibility(View.VISIBLE);
    }

    private void loadActiveLayout() {
        if (user.getStore().getApproval() == 0) {
            active.setText("Đang chờ phê duyệt");
            active.setTextColor(Color.parseColor("#D35400"));
            btActive.setBackgroundColor(Color.parseColor("#00ACC1"));
            btActive.setText("Duyệt");
        } else if (user.getStore().getBlocked() == 1) {
            active.setText("Đã bị khóa");
            active.setTextColor(Color.RED);
            btActive.setBackgroundColor(Color.parseColor("#00ACC1"));
            btActive.setText("Mở khóa");
        } else {
            active.setText("Đang hoạt động");
            active.setTextColor(Color.BLUE);
            btActive.setBackgroundColor(Color.parseColor("#D81B60"));
            btActive.setText("Khóa");
        }
    }

    @Override
    public void whenfinish(ResponeFromServer output) {
        if (output != null) {
            if (output.code() == 200) {
                String responeBody = output.getBody();
                if (responeBody.contains("approval")) {
                    user.getStore().setApproval(1);
                    Toast.makeText(this, "Phê duyệt thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    if (user.getStore().getBlocked() == 1) {
                        user.getStore().setBlocked(0);
                    } else {
                        user.getStore().setBlocked(1);
                    }
                    Toast.makeText(this, "Thay đổi thành công!", Toast.LENGTH_SHORT).show();
                }
                loadActiveLayout();


            } else {

                Toast.makeText(this, "Có lỗi xảy ra!", Toast.LENGTH_SHORT).show();

            }
        } else {
            Toast.makeText(this, "Kiểm tra lại kết nối", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            loadDataFromServer();
    }
}
