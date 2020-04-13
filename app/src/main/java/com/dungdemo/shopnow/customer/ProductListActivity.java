package com.dungdemo.shopnow.customer;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

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
    View toolbarView;
    SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.product_list_toolbar);
        toolbarView=getSupportActionBar().getCustomView();
        toolbarView.findViewById(R.id.backImg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewGroup.LayoutParams layoutParams = searchView.getLayoutParams();
                WindowManager mWinMgr = (WindowManager)getSystemService(WINDOW_SERVICE);
                int displayWidth = mWinMgr.getDefaultDisplay().getWidth();
                layoutParams.width = displayWidth;
                searchView.setLayoutParams(layoutParams);
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                ViewGroup.LayoutParams layoutParams = searchView.getLayoutParams();
                layoutParams.width=80;
                searchView.setLayoutParams(layoutParams);
                recycleViewAdapter.getFilter().filter("");
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                recycleViewAdapter.getFilter().filter(query);
                if(recycleViewAdapter.getItemCount()==0){
                    Toast.makeText(ProductListActivity.this, "Không tìm thấy sản phẩm!", Toast.LENGTH_SHORT).show();

                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String query) {
                return false;
            }
        });


        recyclerView=findViewById(R.id.recycleview);
        recycleViewAdapter=new RecycleViewAdapter(products,ProductListActivity.this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));
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
        TextView tvName=toolbarView.findViewById(R.id.tvName);
        OkHttpClient client = new OkHttpClient();
        String url="";
        if(getIntent().getStringExtra("search")!=null){
            url = HostName.host + "/product/getProductByName/"+getIntent().getStringExtra("search");
            tvName.setText("Tìm kiếm: "+getIntent().getStringExtra("search"));
        }else{
            tvName.setText(getIntent().getStringExtra("title"));
            url = HostName.host + "/product/getProductByCategory/"+getIntent().getIntExtra("category_id",-1);
        }


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
                    Type listType = new TypeToken<List<Product>>() {
                    }.getType();
                    products = new Gson().fromJson(json, listType);
                    ProductListActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(products.size()==0){
                                findViewById(R.id.tvNotFound).setVisibility(View.VISIBLE);
                            }else {
                                recycleViewAdapter.setProducts(products);
                                recycleViewAdapter.notifyDataSetChanged();
                            }
                            findViewById(R.id.progress).setVisibility(View.INVISIBLE);

                        }
                    });

                }
            }
        });
    }
}
