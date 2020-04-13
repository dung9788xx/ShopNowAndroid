package com.dungdemo.shopnow.admin;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dungdemo.shopnow.AsyncResponse;
import com.dungdemo.shopnow.HostName;
import com.dungdemo.shopnow.model.User;
import com.dungdemo.shopnow.R;
import com.dungdemo.shopnow.TaskConnect;
import com.dungdemo.shopnow.utils.ResponeFromServer;

import java.util.HashMap;
import java.util.Map;

public class UserInfomationActivity extends Activity implements AsyncResponse {
    Button btActive;
    User user;
    TextView active;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_infomation);
        TextView name=findViewById(R.id.tvName);
        final TextView username=findViewById(R.id.tvUsername);
        TextView address=findViewById(R.id.tvAddress);
        TextView phone=findViewById(R.id.tvPhone);
         active=findViewById(R.id.tvActive);
        btActive=findViewById(R.id.btActive);
      user=(User)getIntent().getSerializableExtra("user");
        name.setText(user.getName());
        username.setText(user.getUsername());
        address.setText(user.getAddress());
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
                Map<String,String > map=new HashMap<>(  );
                map.put("token",User.getSavedToken(getApplication()));
                map.put("method","get");
               TaskConnect task=new TaskConnect(UserInfomationActivity.this, HostName.host+"/user/deactive/"+user.getUser_id());
               task.setMap( map );
                task.execute();
            }
        });
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
}
