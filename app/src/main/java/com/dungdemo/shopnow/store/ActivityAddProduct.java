package com.dungdemo.shopnow.store;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dungdemo.shopnow.AsyncResponse;
import com.dungdemo.shopnow.HostName;
import com.dungdemo.shopnow.Model.ProductCategory;
import com.dungdemo.shopnow.Model.User;
import com.dungdemo.shopnow.R;
import com.dungdemo.shopnow.TaskConnect;
import com.dungdemo.shopnow.utils.ImageUtil;
import com.dungdemo.shopnow.utils.ResponeFromServer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ActivityAddProduct extends AppCompatActivity implements AsyncResponse {
    ImageView imageView1, imageView2, imageView3, imageView4;
    Bitmap thumbnail1, thumbnail2, thumbnail3, thumbnail4;
    Spinner spinner;
    ArrayAdapter<ProductCategory> arrayAdapter;
    int category_selected_id = -1;
    List<ProductCategory> productCategories;
    EditText edtName,edtDescription,edtPrice,edtAmount;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        setTitle("Thêm sản phẩm");
        progressBar=findViewById(R.id.progress);
        edtName=findViewById(R.id.edtName);
        edtDescription=findViewById(R.id.edtDescription);
        edtPrice=findViewById(R.id.edtPrice);
        edtAmount=findViewById(R.id.edtAmount);
        imageView1 = findViewById(R.id.img1);
        imageView2 = findViewById(R.id.img2);
        imageView3 = findViewById(R.id.img3);
        imageView4 = findViewById(R.id.img4);
        spinner = findViewById(R.id.spinner_category);

        productCategories = new ArrayList<>();
        loadCategory();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category_selected_id = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (thumbnail1 != null) {
                    if(edtName.getText().toString().isEmpty()){
                       edtName.setError("Tên không được bỏ trống!");
                    }else{
                        if(edtPrice.getText().toString().isEmpty()){
                            edtPrice.setError("Giá không được bỏ trống!");
                        }else{
                            if(edtAmount.getText().toString().isEmpty()){
                                edtAmount.setError("Số lượng sản phẩm không được bỏ trống!");
                            }else{
                                Map<String, String> map = new HashMap<>();
                                map.put("token", User.getSavedToken(getApplication()));
                                map.put("method", "post");
                                map.put("name",edtName.getText().toString());
                                map.put("description",edtDescription.getText().toString());
                                map.put("price",edtPrice.getText().toString());
                                map.put("amount",edtAmount.getText().toString());
                                map.put("category_id",category_selected_id+"");

                                map.put("img1", ImageUtil.convert(thumbnail1));
                                if (thumbnail2 != null) {
                                    map.put("img2", ImageUtil.convert(thumbnail2));
                                }
                                if (thumbnail3 != null) {
                                    map.put("img3", ImageUtil.convert(thumbnail3));
                                }
                                if (thumbnail4 != null) {
                                    map.put("img4", ImageUtil.convert(thumbnail4));
                                }
                                TaskConnect task = new TaskConnect(ActivityAddProduct.this, HostName.host + "/product");
                                task.setMap(map);
                                task.execute();
                                progressBar.setVisibility(View.VISIBLE);
                                findViewById(R.id.btnAdd).setEnabled(false);
                            }
                        }
                    }
                } else {
                    Toast.makeText(ActivityAddProduct.this, "Vui lòng chọn ít nhất một ảnh !", Toast.LENGTH_SHORT).show();
                }

            }
        });
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), 1);
            }
        });
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), 2);
            }
        });
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), 3);
            }
        });
        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), 4);
            }
        });
        imageView1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (thumbnail1 != null) {
                    thumbnail1 = thumbnail2;
                    thumbnail2 = thumbnail3;
                    thumbnail3 = thumbnail4;
                    thumbnail4 = null;
                    loadImagePosition();
                }
                return true;
            }
        });
        imageView2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (thumbnail2 != null) {
                    thumbnail2 = thumbnail3;
                    thumbnail3 = thumbnail4;
                    thumbnail4 = null;
                    loadImagePosition();
                }
                return true;
            }
        });
        imageView3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (thumbnail3 != null) {
                    thumbnail3 = thumbnail4;
                    thumbnail4 = null;
                    loadImagePosition();
                }
                return true;
            }
        });
        imageView4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (thumbnail4 != null) {
                    thumbnail4 = null;
                    loadImagePosition();
                }
                return true;
            }
        });
    }

    private void loadSpinner() {
        arrayAdapter = new ArrayAdapter<ProductCategory>(ActivityAddProduct.this, R.layout.category_item_spinner, productCategories) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = layoutInflater.inflate(R.layout.category_item_spinner, null);
                TextView name = v.findViewById(R.id.tvName);
                name.setText(productCategories.get(position).getName());
                return v;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = layoutInflater.inflate(R.layout.category_item_spinner, null);
                TextView name = v.findViewById(R.id.tvName);
                name.setText(productCategories.get(position).getName());
                return v;
            }
        };
        spinner.setAdapter(arrayAdapter);
        spinner.setSelection(1);
    }


    private void loadCategory() {
        OkHttpClient client = new OkHttpClient();
        String url = HostName.host + "/category";

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
                    Type listType = new TypeToken<List<ProductCategory>>() {
                    }.getType();
                    productCategories = new Gson().fromJson(json, listType);
                    ActivityAddProduct.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadSpinner();
                        }
                    });
//
                }
            }
        });
    }

    private void loadImagePosition() {
        if (thumbnail1 == null) {
            imageView1.setImageResource(R.drawable.icon_add_image);
            imageView2.setVisibility(View.INVISIBLE);
            imageView3.setVisibility(View.INVISIBLE);
            imageView4.setVisibility(View.INVISIBLE);
        } else if (thumbnail2 == null) {
            imageView1.setImageBitmap(thumbnail1);
            imageView2.setImageResource(R.drawable.icon_add_image);
            imageView2.setVisibility(View.VISIBLE);
            imageView3.setVisibility(View.INVISIBLE);
            imageView4.setVisibility(View.INVISIBLE);
        } else if (thumbnail3 == null) {
            imageView1.setImageBitmap(thumbnail1);
            imageView2.setImageBitmap(thumbnail2);
            imageView3.setImageResource(R.drawable.icon_add_image);
            imageView3.setVisibility(View.VISIBLE);
            imageView4.setVisibility(View.INVISIBLE);
        } else if (thumbnail4 == null) {
            imageView1.setImageBitmap(thumbnail1);
            imageView2.setImageBitmap(thumbnail2);
            imageView3.setImageBitmap(thumbnail3);
            imageView4.setImageResource(R.drawable.icon_add_image);
            imageView4.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                String filestring = selectedImageUri.getPath();
                thumbnail1 = BitmapFactory.decodeFile(filestring, new BitmapFactory.Options());
                imageView1.setImageBitmap(thumbnail1);
                imageView2.setVisibility(View.VISIBLE);
            }
        }
        if (requestCode == 2) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                String filestring = selectedImageUri.getPath();
                thumbnail2 = BitmapFactory.decodeFile(filestring, new BitmapFactory.Options());
                imageView2.setImageBitmap(thumbnail2);
                imageView3.setVisibility(View.VISIBLE);
            }
        }
        if (requestCode == 3) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                String filestring = selectedImageUri.getPath();
                thumbnail3 = BitmapFactory.decodeFile(filestring, new BitmapFactory.Options());
                imageView3.setImageBitmap(thumbnail3);
                imageView4.setVisibility(View.VISIBLE);
            }
        }
        if (requestCode == 4) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                String filestring = selectedImageUri.getPath();
                thumbnail4 = BitmapFactory.decodeFile(filestring, new BitmapFactory.Options());
                imageView4.setImageBitmap(thumbnail4);
            }
        }
    }

    @Override
    public void whenfinish(ResponeFromServer output) {
        findViewById(R.id.btnAdd).setEnabled(true);
        progressBar.setVisibility(View.INVISIBLE);
        if (output != null) {
            if (output.code() == 200) {
                Toast.makeText(this, "LOL" + output.getBody(), Toast.LENGTH_LONG).show();

            }else{
                Toast.makeText(this, "Kiểm tra lại kết nối !", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
