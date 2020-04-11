package com.dungdemo.shopnow.admin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.dungdemo.shopnow.AsyncResponse;
import com.dungdemo.shopnow.HostName;
import com.dungdemo.shopnow.LoginActivity;
import com.dungdemo.shopnow.Model.User;
import com.dungdemo.shopnow.R;
import com.dungdemo.shopnow.TaskConnect;
import com.dungdemo.shopnow.customer.CustomerMainActivity;
import com.dungdemo.shopnow.utils.ResponeFromServer;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Response;

public class AdminActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AsyncResponse {
    User user;
    NavigationView navigationView;
    int checkExit=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Intent myIntent = new Intent(AdminActivity.this, AdminAlertService.class);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            ContextCompat.startForegroundService(this, myIntent);
            startService(myIntent);
        } else {
            startService(myIntent);
        }
//        user=(User)getIntent().getSerializableExtra("user");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
     navigationView = (NavigationView) findViewById(R.id.nav_view);
        loadData();
        navigationView.setNavigationItemSelectedListener(this);
        if(getIntent().getIntExtra("newStore",0)==1){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content,new StoreManagerFragment()).commit();
        }else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content, new UserManagerFragment()).commit();
        }
    }

    private void loadData() {
        Map<String,String > map=new HashMap<>(  );
        map.put("token",User.getSavedToken(getApplication()));
        map.put("method","get");
        TaskConnect task=new TaskConnect(AdminActivity.this, HostName.host+"/user/"+User.getSavedUserId(this));
        task.setMap( map );
        task.execute();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            super.onBackPressed();
            checkExit++;
            if(checkExit==2){
                finishAffinity();
            }
            Toast.makeText(this, "Ấn lại để thoát !", Toast.LENGTH_SHORT).show();

        }
    }




    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
             int id = item.getItemId();

        if (id == R.id.nav_userlist) {
           getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content,new UserManagerFragment()).commit();

        }
        if(id==R.id.nav_stores){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content,new StoreManagerFragment()).commit();
        }
        if(id==R.id.nav_logout){
            User.logout(this);
            Intent intent = new Intent(AdminActivity.this,LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void whenfinish(ResponeFromServer output) {
        if(output!=null){
            if(output.code()==200){

                String json="";
                json=output.getBody();
                Gson gson=new Gson();
                user=gson.fromJson(json,User.class);
                View headerView=navigationView.getHeaderView(0);
                TextView name=headerView.findViewById(R.id.tvName);
                name.setText(user.getName());
                TextView phone=headerView.findViewById(R.id.tvPhone);
                phone.setText(user.getPhone());
            }
            if(output.code()==401){
                AlertDialog.Builder builder1 = new AlertDialog.Builder(AdminActivity.this);
                builder1.setMessage("Phiên đăng nhập đã hết hạn !.");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Đăng nhập lại",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                User.logout(getApplicationContext());
                                Intent intent = new Intent(getApplication(), LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                dialog.cancel();
                            }
                        });


                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        if (output.code()==0){
            Toast.makeText(this, "Kiểm tra lại kết nối !", Toast.LENGTH_SHORT).show();
        }

        }

    }

}
