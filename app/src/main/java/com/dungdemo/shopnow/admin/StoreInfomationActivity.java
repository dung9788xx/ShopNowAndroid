package com.dungdemo.shopnow.admin;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dungdemo.shopnow.AsyncResponse;
import com.dungdemo.shopnow.HostName;
import com.dungdemo.shopnow.Model.User;
import com.dungdemo.shopnow.R;
import com.dungdemo.shopnow.TaskConnect;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Response;

public class StoreInfomationActivity extends Activity implements AsyncResponse {
    Button btActive;
    User user;
    TextView active;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_infomation);
        TextView tvStoreName=findViewById(R.id.tvStoreName);
        TextView name=findViewById(R.id.tvName);
        TextView shopAddress=findViewById(R.id.tvShopaddress);

        final TextView username=findViewById(R.id.tvUsername);
        TextView address=findViewById(R.id.tvAddress);
        TextView phone=findViewById(R.id.tvPhone);
        active=findViewById(R.id.tvActive);
        btActive=findViewById(R.id.btActive);
        user=(User)getIntent().getSerializableExtra("user");
        tvStoreName.setText(user.getStore().getName());
        name.setText(user.getName());
        username.setText(user.getUsername());
        address.setText(user.getAddress());
        shopAddress.setText(user.getStore().getLocation().getName());
        phone.setText(user.getPhone());
        loadActiveLayout();
        findViewById(R.id.btBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.btActive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if(user.getStore().getApproval()==0){
                  Map<String,String > map=new HashMap<>(  );
                  map.put("token",User.getSavedToken(getApplication()));
                  map.put("method","get");
                  TaskConnect task=new TaskConnect(StoreInfomationActivity.this, HostName.host+"/store/approvalStore/"+user.getStore().getStore_id());
                  task.setMap( map );
                  task.execute();
              }else{
                  Map<String,String > map=new HashMap<>(  );
                  map.put("token",User.getSavedToken(getApplication()));
                  map.put("method","get");
                  TaskConnect task=new TaskConnect(StoreInfomationActivity.this, HostName.host+"/store/blockStore/"+user.getStore().getStore_id());
                  task.setMap( map );
                  task.execute();
              }
            }
        });
    }
    private void loadActiveLayout() {
        if(user.getStore().getApproval()==0){
            active.setText("Đang chờ phê duyệt");
            active.setTextColor(Color.parseColor("#D35400"));
            btActive.setBackgroundColor(Color.parseColor("#00ACC1"));
            btActive.setText("Duyệt");
        }else if(user.getStore().getBlocked()==1){
            active.setText("Đã bị khóa");
            active.setTextColor(Color.RED);
            btActive.setBackgroundColor(Color.parseColor("#00ACC1"));
            btActive.setText("Mở khóa");
        }else{
            active.setText("Đang hoạt động");
            active.setTextColor(Color.BLUE);
            btActive.setBackgroundColor(Color.parseColor("#D81B60"));
            btActive.setText("Khóa");
        }
    }
    @Override
    public void whenfinish(Response output) {
        if(output!=null){
            if(output.code()==200){
                try {
                    String responeBody=output.body().string();
                    if(responeBody.contains("approval")){
                        user.getStore().setApproval(1);
                        Toast.makeText(this, "Phê duyệt thành công!", Toast.LENGTH_SHORT).show();
                    }else{
                        if(user.getStore().getBlocked()==1){
                            user.getStore().setBlocked(0);
                        }else{
                            user.getStore().setBlocked(1);
                        }
                        Toast.makeText(this, "Thay đổi thành công!", Toast.LENGTH_SHORT).show();
                    }
                    loadActiveLayout();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }else {

                    Toast.makeText(this, "Có lỗi xảy ra!", Toast.LENGTH_SHORT).show();

            }
        }else{
            Toast.makeText(this, "Kiểm tra lại kết nối", Toast.LENGTH_SHORT).show();
        }

    }

}
