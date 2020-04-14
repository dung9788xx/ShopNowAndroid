package com.dungdemo.shopnow.customer;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dungdemo.shopnow.HostName;
import com.dungdemo.shopnow.model.Product;
import com.dungdemo.shopnow.model.ProductImage;
import com.dungdemo.shopnow.model.SliderItem;
import com.dungdemo.shopnow.model.User;
import com.dungdemo.shopnow.R;
import com.dungdemo.shopnow.utils.MoneyType;
import com.google.gson.Gson;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProductDetailActivity extends AppCompatActivity {
    SliderView sliderView;
    SliderAdapter adapter;
    List<SliderItem> sliderItemList = new ArrayList<>();
    Product product;
    View toolbarView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.product_detail_toolbar);
        toolbarView=getSupportActionBar().getCustomView();
        toolbarView.findViewById(R.id.backImg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        loadProductInfo();


        sliderView = findViewById(R.id.imageSlider);
        adapter = new SliderAdapter(this);
        adapter.setOnClickable(false);
        adapter.renewItems(sliderItemList);
        sliderView.setSliderAdapter(adapter);
        sliderView.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
        sliderView.startAutoCycle();
    }

    private void loadProductInfo() {
        OkHttpClient client = new OkHttpClient();
        String url = HostName.host + "/product/"+ getIntent().getIntExtra("product_id",-1);;

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
                    product= new Gson().fromJson(json, Product.class);
                    ProductDetailActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (ProductImage img:product.getImages()) {
                                sliderItemList.add(new SliderItem(product.getProduct_id(),"",img.getImage_name()));
                            }
                            adapter.renewItems(sliderItemList);
                            loadInfoToView();

                        }
                    });
                }
            }
        });
    }

    private void loadInfoToView() {
        TextView tvPrice=findViewById(R.id.tvPrice);
        TextView tvAmount=findViewById(R.id.tvAmount);
        TextView tvDescription=findViewById(R.id.tvDescription);
        TextView tvShopName=findViewById(R.id.tvShopName);
        tvAmount.setText(product.getAmount()+"");
        tvDescription.setText(product.getDescription());
        tvPrice.setText(MoneyType.toMoney(product.getPrice())+" VND");
        tvShopName.setText(product.getStore().getName());
        tvShopName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent t=new Intent(ProductDetailActivity.this,ProductListActivity.class);
                t.putExtra("store_id",product.getStore().getStore_id());
                t.putExtra("title",product.getStore().getName());
                startActivity(t);
            }
        });

        TextView tvProductName=toolbarView.findViewById(R.id.tvProductName);
        tvProductName.setText(product.getName());
    }


}
