package com.dungdemo.shopnow.customer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.dungdemo.shopnow.HostName;
import com.dungdemo.shopnow.R;
import com.dungdemo.shopnow.model.Product;
import com.dungdemo.shopnow.model.ProductCategory;
import com.dungdemo.shopnow.model.User;
import com.dungdemo.shopnow.store.ActivityAddProduct;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProductListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    RecycleViewAdapter recycleViewAdapter;
    List<Product> products=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        recyclerView=findViewById(R.id.recycleview);
        recycleViewAdapter=new RecycleViewAdapter(products);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, true));
        recycleViewAdapter.setOnItemClickedListener(new RecycleViewAdapter.OnItemClickedListener() {
            @Override
            public void onItemClick(int product_id) {
                Intent intent = new Intent(ProductListActivity.this, ProductDetailActivity.class);
                intent.putExtra("product_id", product_id);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(recycleViewAdapter);

        loadProduct();

    }

    private void loadProduct() {
        OkHttpClient client = new OkHttpClient();
        String url = HostName.host + "/product/getProductByCategory/"+getIntent().getIntExtra("category_id",-1);

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
                    Log.d("lol",json+"");
                    Type listType = new TypeToken<List<Product>>() {
                    }.getType();
                    products = new Gson().fromJson(json, listType);
                    ProductListActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("lol",products.size()+"");
                            recycleViewAdapter.setProducts(products);
                            recycleViewAdapter.notifyDataSetChanged();
                        }
                    });

                }
            }
        });
    }
}
