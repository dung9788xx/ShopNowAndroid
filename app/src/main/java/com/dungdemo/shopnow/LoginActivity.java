package com.dungdemo.shopnow;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dungdemo.shopnow.model.User;
import com.dungdemo.shopnow.admin.AdminActivity;
import com.dungdemo.shopnow.customer.CustomerMainActivity;
import com.dungdemo.shopnow.register.CustomerRegisterActivity;
import com.dungdemo.shopnow.register.StoreRegisterActivity;
import com.dungdemo.shopnow.store.ShopkeeperMainActivity;
import com.dungdemo.shopnow.utils.ResponeFromServer;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements AsyncResponse {
    TextView registerStore,registerCustomer;
    EditText edtUsername, edtPassword;
    Button btnLogin;
    TaskConnect task;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        if (!User.getSavedToken(this).isEmpty()) {
            if (User.getSavedLevel(this) == 1) {
                startActivity(new Intent(this, AdminActivity.class));
            }
            if (User.getSavedLevel(this) == 2) {
                startActivity(new Intent(this, ShopkeeperMainActivity.class));
            }
            if (User.getSavedLevel(this) == 3) {
                startActivity(new Intent(this, CustomerMainActivity.class));
            }

        }
        registerCustomer=findViewById(R.id.registerCustomer);
        registerStore=findViewById(R.id.registerStore);
        edtUsername = findViewById(R.id.username);
        edtPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.login);
        progressBar = findViewById(R.id.progress);
        registerCustomer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, CustomerRegisterActivity.class));
            }
        });
        registerStore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, StoreRegisterActivity.class));
            }
        });
        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isValidate = true;
                if ("".equals(edtUsername.getText().toString())) {
                    edtUsername.setError("Không được bỏ trống");
                    isValidate = false;
                }
                if ("".equals(edtPassword.getText().toString())) {
                    edtUsername.setError("Không được bỏ trống");
                    isValidate = false;
                }
                if (isValidate) {
                    Map<String, String> map = new HashMap<>();
                    map.put("username", edtUsername.getText().toString());
                    map.put("password", edtPassword.getText().toString());
                    map.put("method", "post");
                    task = new TaskConnect(LoginActivity.this, HostName.host + "/login");
                    task.setMap(map);
                    progressBar.setVisibility(View.VISIBLE);
                    btnLogin.setEnabled(false);
                    task.execute();
                }
            }
        });

    }

    @Override
    public void whenfinish(ResponeFromServer output) {
        progressBar.setVisibility(View.INVISIBLE);
        btnLogin.setEnabled(true);

        if (output != null) {   
            if (output.code() == 200) {
                try {
                    String s = output.getBody();
                    JSONObject object = new JSONObject(s);
                    Gson gson = new Gson();
                    User user = gson.fromJson(object + "", User.class);
                    user.saveToken(this);

                    if (user.getLevel() == 1) {
                        Intent intent = new Intent(LoginActivity.this, AdminActivity.class);

                        startActivity(intent);
                    }
                    if (user.getLevel() == 2) {
                        Intent intent = new Intent(LoginActivity.this, ShopkeeperMainActivity.class);
                        startActivity(intent);

                    }
                    if (user.getLevel() == 3) {
                        Intent intent = new Intent(LoginActivity.this, CustomerMainActivity.class);
                        startActivity(intent);

                    }

                } catch (JSONException e) {

                }

            }
            if(output.code()==404){
                Toast.makeText(this, "Kiểm tra lại thông tin tài khoản" , Toast.LENGTH_LONG).show();
            }
            if(output.code()==401){
                AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                builder1.setMessage(new Gson().fromJson(output.getBody(),String.class));
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });


                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
            if (output.code() == 0) {
                Toast.makeText(this, "Kiểm tra lại kết nối" , Toast.LENGTH_LONG).show();
            }


        }
    }

}


