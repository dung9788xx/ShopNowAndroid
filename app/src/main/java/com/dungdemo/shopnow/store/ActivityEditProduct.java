package com.dungdemo.shopnow.store;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dungdemo.shopnow.AsyncResponse;
import com.dungdemo.shopnow.HostName;
import com.dungdemo.shopnow.Model.Product;
import com.dungdemo.shopnow.Model.ProductCategory;
import com.dungdemo.shopnow.Model.User;
import com.dungdemo.shopnow.R;
import com.dungdemo.shopnow.TaskConnect;
import com.dungdemo.shopnow.admin.StoreInfomationActivity;
import com.dungdemo.shopnow.utils.ImageUtil;
import com.dungdemo.shopnow.utils.ResponeFromServer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

public class ActivityEditProduct extends AppCompatActivity implements AsyncResponse {
    int imageQuality=4;
    ImageView imageView1, imageView2, imageView3, imageView4;
    Bitmap thumbnail1, thumbnail2, thumbnail3, thumbnail4;
    Spinner spinner;
    ArrayAdapter<ProductCategory> arrayAdapter;
    int category_selected_id = 0;
    List<ProductCategory> productCategories;
    EditText edtName,edtDescription,edtPrice,edtAmount;
    TextView tvStatus;
    ProgressBar progressBar;
    Product product;
    Button btnStatus;
    int loadedAllImage=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);
        setTitle("Sửa sản phẩm");
        progressBar=findViewById(R.id.progress);
        edtName=findViewById(R.id.edtName);
        edtDescription=findViewById(R.id.edtDescription);
        edtPrice=findViewById(R.id.edtPrice);
        edtAmount=findViewById(R.id.edtAmount);
        imageView1 = findViewById(R.id.img1);
        imageView2 = findViewById(R.id.img2);
        imageView3 = findViewById(R.id.img3);
        imageView4 = findViewById(R.id.img4);
        tvStatus=findViewById(R.id.tvStatus);
        btnStatus=findViewById(R.id.btnStatus);
        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
        setData();

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
                                progressBar.setVisibility(View.VISIBLE);
                                findViewById(R.id.btnAdd).setEnabled(false);
                                Map<String, String> map = new HashMap<>();
                                map.put("token", User.getSavedToken(getApplication()));
                                map.put("method", "put");
                                map.put("name",edtName.getText().toString());
                                map.put("description",edtDescription.getText().toString());
                                map.put("price",edtPrice.getText().toString());
                                map.put("amount",edtAmount.getText().toString());
                                map.put("category_id",(category_selected_id+1)+"");


                                new AsyncTask<Void,Void,Void>(){
                                    @Override
                                    protected Void doInBackground(Void... voids) {
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
                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(Void aVoid) {
                                        super.onPostExecute(aVoid);
                                        TaskConnect task = new TaskConnect(ActivityEditProduct.this, HostName.host + "/product/"+product.getProduct_id());
                                        task.setMap(map);
                                        task.execute();

                                    }
                                }.execute();



                            }
                        }
                    }
                } else {
                    Toast.makeText(ActivityEditProduct.this, "Vui lòng chọn ít nhất một ảnh !", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               sendActiveRequest();
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

    private void sendActiveRequest() {
        OkHttpClient client = new OkHttpClient();
        String url = HostName.host + "/product/deactive/"+product.getProduct_id();

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
                   if(response.code()==200) {
                       ActivityEditProduct.this.runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                              product.setIsSelling(product.getIsSelling()==1?0:1);
                              loadActiveLayout();
                           }
                       });
                   }
//
                }
            }
        });
    }

    private void setData() {
        product=(Product)getIntent().getSerializableExtra("product");
        edtName.setText(product.getName());
        edtDescription.setText(product.getDescription());
        edtPrice.setText(product.getPrice()+"");
        edtAmount.setText(product.getAmount()+"");
        loadActiveLayout();

        if(product.getImages().size()>0){
            String url=HostName.imgurl+product.getProduct_id()+"/"+product.getImages().get(0).getImage_name();
            Picasso.get().load(url).into(imageView1, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    thumbnail1=((BitmapDrawable)imageView1.getDrawable()).getBitmap();;
                    imageView1.setVisibility(View.VISIBLE);
                    loadedAllImage++;
                    if(loadedAllImage==product.getImages().size()){
                        loadImagePosition();
                    }
                }

                @Override
                public void onError(Exception e) {

                }
            });

        }
        if(product.getImages().size()>1){
            String url=HostName.imgurl+product.getProduct_id()+"/"+product.getImages().get(1).getImage_name();
            Picasso.get().load(url).into(imageView2, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    thumbnail2=((BitmapDrawable)imageView2.getDrawable()).getBitmap();;
                    imageView2.setVisibility(View.VISIBLE);
                    loadedAllImage++;
                    if(loadedAllImage==product.getImages().size()){
                        loadImagePosition();
                    }
                }

                @Override
                public void onError(Exception e) {

                }
            });

        }
        if(product.getImages().size()>2){
            String url=HostName.imgurl+product.getProduct_id()+"/"+product.getImages().get(2).getImage_name();
            Picasso.get().load(url).into(imageView3, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    thumbnail3=((BitmapDrawable)imageView3.getDrawable()).getBitmap();;
                    imageView3.setVisibility(View.VISIBLE);
                    loadedAllImage++;
                    if(loadedAllImage==product.getImages().size()){
                        loadImagePosition();
                    }
                }

                @Override
                public void onError(Exception e) {

                }
            });

        }
        if(product.getImages().size()>3){
            String url=HostName.imgurl+product.getProduct_id()+"/"+product.getImages().get(3).getImage_name();
            Picasso.get().load(url).into(imageView4, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    thumbnail4=((BitmapDrawable)imageView4.getDrawable()).getBitmap();;
                    imageView4.setVisibility(View.VISIBLE);
                    loadedAllImage++;
                    if(loadedAllImage==product.getImages().size()){
                        loadImagePosition();
                    }
                }

                @Override
                public void onError(Exception e) {

                }
            });

        }

    }

    private void loadActiveLayout() {
        if(product.getIsSelling()==1){
            tvStatus.setTextColor(Color.BLUE);
            tvStatus.setText("Đang bán");
            btnStatus.setBackgroundColor(Color.parseColor("#D81B60"));
            btnStatus.setText("Tạm dừng bán");
        }else{
            tvStatus.setTextColor(Color.RED);
            tvStatus.setText("Đã tạm dừng bán");
            btnStatus.setBackgroundColor(Color.parseColor("#00ACC1"));
            btnStatus.setText("Bán lại");
        }
    }

    private void loadSpinner() {
        arrayAdapter = new ArrayAdapter<ProductCategory>(ActivityEditProduct.this, R.layout.category_item_spinner, productCategories) {
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
        spinner.setSelection(product.getCategory().getCategory_id()-1);
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
                    ActivityEditProduct.this.runOnUiThread(new Runnable() {
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

                try {
                    Uri selectedImageUri = data.getData();
                    InputStream inputStream=getContentResolver().openInputStream(selectedImageUri);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize=imageQuality;
                    thumbnail1 = BitmapFactory.decodeStream(inputStream,null,options);
                    Log.d("lol",thumbnail1.getByteCount()+"");
                    imageView1.setImageBitmap(thumbnail1);
                    imageView2.setVisibility(View.VISIBLE);
                } catch (FileNotFoundException e) {

                }

            }
        }
        if (requestCode == 2) {
            if (data != null) {
                try {
                    Uri selectedImageUri = data.getData();
                    InputStream inputStream=getContentResolver().openInputStream(selectedImageUri);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize=imageQuality;
                    thumbnail2 = BitmapFactory.decodeStream(inputStream,null,options);
                    imageView2.setImageBitmap(thumbnail2);
                    imageView3.setVisibility(View.VISIBLE);
                } catch (FileNotFoundException e) {

                }
            }
        }
        if (requestCode == 3) {
            if (data != null) {
                try {
                    Uri selectedImageUri = data.getData();
                    InputStream inputStream=getContentResolver().openInputStream(selectedImageUri);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize=imageQuality;
                    thumbnail3 = BitmapFactory.decodeStream(inputStream,null,options);
                    imageView3.setImageBitmap(thumbnail3);
                    imageView4.setVisibility(View.VISIBLE);
                } catch (FileNotFoundException e) {

                }
            }
        }
        if (requestCode == 4) {
            if (data != null) {
                try {
                    Uri selectedImageUri = data.getData();
                    InputStream inputStream=getContentResolver().openInputStream(selectedImageUri);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize=imageQuality;
                    thumbnail4 = BitmapFactory.decodeStream(inputStream,null,options);
                    imageView4.setImageBitmap(thumbnail4);

                } catch (FileNotFoundException e) {

                }
            }
        }

    }
    @Override
    public void whenfinish(ResponeFromServer output) {
        findViewById(R.id.btnAdd).setEnabled(true);
        progressBar.setVisibility(View.INVISIBLE);
        if (output != null) {
            if (output.code() == 200) {
               finish();

            }else{
                Toast.makeText(this, "Kiểm tra lại kết nối !", Toast.LENGTH_LONG).show();
            }

        }else{
            Toast.makeText(this, "NULL", Toast.LENGTH_SHORT).show();
        }
    }
}

