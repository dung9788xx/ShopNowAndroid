package com.dungdemo.shopnow;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dungdemo.shopnow.Model.User;
import com.dungdemo.shopnow.admin.AdminActivity;
import com.dungdemo.shopnow.store.ShopkeeperMainActivity;
import com.dungdemo.shopnow.utils.ResponeFromServer;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements AsyncResponse {
    TextView register;
    EditText edtUsername,edtPassword;
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
        if(!User.getSavedToken(this).isEmpty()){
            if(User.getSavedLevel(this)==1){
                startActivity(new Intent(this,AdminActivity.class));
            }
            if(User.getSavedLevel(this)==2){
                startActivity(new Intent(this, ShopkeeperMainActivity.class));
            }

        }
        edtUsername=findViewById(R.id.username);
        edtPassword=findViewById(R.id.password);
        btnLogin=findViewById(R.id.login);
        progressBar=findViewById(R.id.progress);
        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isValidate=true;
                if("".equals(edtUsername.getText().toString())){
                    edtUsername.setError("Không được bỏ trống");
                    isValidate=false;
                }
                if("".equals(edtPassword.getText().toString())){
                    edtUsername.setError("Không được bỏ trống");
                    isValidate=false;
                }
                if(isValidate){
                    Map<String,String > map=new HashMap<>(  );
                    map.put( "username",edtUsername.getText().toString() );
                    map.put( "password",edtPassword.getText().toString() );
                    map.put("method","post");
                    task=new TaskConnect(LoginActivity.this,HostName.host+"/login");
                    task.setMap( map );
                    progressBar.setVisibility( View.VISIBLE );
                    task.execute();
                }
            }
        });

    }

    @Override
    public void whenfinish(ResponeFromServer output) {
     progressBar.setVisibility(View.INVISIBLE);

        if (output != null) {
            if(output.code()==200){
                try {
                    String s = output.getBody();
                    JSONObject object = new JSONObject(s);
                    Gson gson = new Gson();
                    User user = gson.fromJson(object + "", User.class);
                    user.saveToken(this);

                   if(user.getLevel()==1){
                       Intent intent=new Intent(LoginActivity.this,AdminActivity.class);

                       startActivity(intent);
                   }
                    if(user.getLevel()==2){
                        Intent intent=new Intent(LoginActivity.this,ShopkeeperMainActivity.class);
                        startActivity(intent);

                    }

                } catch (JSONException e) {

                }

            }else{
                Toast.makeText(this, "Kiểm tra lại thông tin tài khoản !", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Kiểm tra lại kết nối", Toast.LENGTH_SHORT).show();
        }
   }

}


