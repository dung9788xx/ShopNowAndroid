package com.dungdemo.shopnow.admin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Output;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dungdemo.shopnow.AsyncResponse;
import com.dungdemo.shopnow.HostName;
import com.dungdemo.shopnow.R;
import com.dungdemo.shopnow.TaskConnect;
import com.dungdemo.shopnow.model.User;
import com.dungdemo.shopnow.store.OrderManagerFragment;
import com.dungdemo.shopnow.utils.ImageUtil;
import com.dungdemo.shopnow.utils.ResponeFromServer;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AddCategoryActivity extends AppCompatActivity implements AsyncResponse {
    ImageView img;
    int imageQuality=4;
    Bitmap thumbnail1;
    EditText edtName,edtDetail;
    ProgressBar progressBar;
    Button btnAdd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.cart_toolbar);
        getSupportActionBar().getCustomView().findViewById(R.id.backImg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        TextView toolbarTitle = getSupportActionBar().getCustomView().findViewById(R.id.tvProductName);
        toolbarTitle.setText("Thêm danh mục");

        edtDetail=findViewById(R.id.edtDetail);
        edtName=findViewById(R.id.edtName);
        progressBar=findViewById(R.id.progress);
        btnAdd=findViewById(R.id.buttonAddCategory);
        img=findViewById(R.id.img);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), 1);
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkValidate()){
                    Map<String, String> map = new HashMap<>();
                    map.put("token", User.getSavedToken(AddCategoryActivity.this));
                    map.put("method", "post");
                    map.put("name",edtName.getText().toString());
                    map.put("detail",edtDetail.getText().toString());
                    map.put("img", ImageUtil.convert(thumbnail1));
                   TaskConnect task = new TaskConnect(AddCategoryActivity.this, HostName.host + "/category");
                    task.setMap(map);
                    task.execute();
                    progressBar.setVisibility(View.VISIBLE);
                    btnAdd.setEnabled(false);
                }
            }
        });
    }

    private boolean checkValidate() {
        if(thumbnail1==null){
            Toast.makeText(this, "Vui lòng chọn ảnh !", Toast.LENGTH_SHORT).show();
            return  false;
        }
        if(edtName.getText().toString().isEmpty()||edtDetail.toString().isEmpty()){
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return  false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {

            try {
                Uri selectedImageUri = data.getData();
                InputStream inputStream=getContentResolver().openInputStream(selectedImageUri);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize=imageQuality;
                thumbnail1 = BitmapFactory.decodeStream(inputStream,null,options);
                img.setImageBitmap(thumbnail1);
            } catch (FileNotFoundException e) {

            }

        }
    }

    @Override
    public void whenfinish(ResponeFromServer output) {
        progressBar.setVisibility(View.INVISIBLE);
        btnAdd.setEnabled(true);
        if(output.code()==200){
            Toast.makeText(this, "Thêm thành công", Toast.LENGTH_SHORT).show();
            finish();
        }
        if(output.code()==409){
            Toast.makeText(this, "Tên danh mục đã tồn tại!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Có lỗi xảy ra vui lòng thử lại", Toast.LENGTH_SHORT).show();
        }
    }
}
