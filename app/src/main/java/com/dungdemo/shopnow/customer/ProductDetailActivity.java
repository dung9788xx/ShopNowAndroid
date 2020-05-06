package com.dungdemo.shopnow.customer;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dungdemo.shopnow.AsyncResponse;
import com.dungdemo.shopnow.HostName;
import com.dungdemo.shopnow.TaskConnect;
import com.dungdemo.shopnow.admin.StoreManagerFragment;
import com.dungdemo.shopnow.model.Product;
import com.dungdemo.shopnow.model.ProductImage;
import com.dungdemo.shopnow.model.SliderItem;
import com.dungdemo.shopnow.model.User;
import com.dungdemo.shopnow.R;
import com.dungdemo.shopnow.utils.MoneyType;
import com.dungdemo.shopnow.utils.ResponeFromServer;
import com.google.gson.Gson;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProductDetailActivity extends AppCompatActivity implements AsyncResponse {
    SliderView sliderView;
    SliderAdapter adapter;
    List<SliderItem> sliderItemList = new ArrayList<>();
    Product product;
    View toolbarView;
    Button btnAddToCart;
    TaskConnect task;
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
        toolbarView.findViewById(R.id.imgShoppingCart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProductDetailActivity.this,CartActivity.class));
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

        btnAddToCart=findViewById(R.id.btnAddToCart);
        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProductToCart();
            }
        });
    }

    private void addProductToCart() {
        Map<String,String > map=new HashMap<>(  );
        map.put("token", User.getSavedToken(ProductDetailActivity.this));
        map.put("method","post");
        map.put("product_id",product.getProduct_id()+"");
        if(product.getPromotion_price()!=0){
            map.put("price",product.getPromotion_price()+"");
        }else{
            map.put("price",product.getPrice()+"");
        }
        map.put("quantity","1");
        EditText edtNote=findViewById(R.id.edtNote);
        map.put("note",edtNote.getText().toString());
        task=new TaskConnect(ProductDetailActivity.this, HostName.host+"/cart/addProductToCart");
        task.setMap( map );
        task.execute();

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
        TextView tvPromotionPrice=findViewById(R.id.tvPromotionPrice);
        TextView tvAmount=findViewById(R.id.tvAmount);
        TextView tvDescription=findViewById(R.id.tvDescription);
        TextView tvShopName=findViewById(R.id.tvShopName);
        tvAmount.setText(product.getAmount()+"");
        tvDescription.setText(product.getDescription());
        if (product.getPromotion_price()!=0) {
            tvPrice.setText(Color.BLACK);
            tvPrice.setText(MoneyType.toMoney(product.getPrice()) +" VND");
            tvPrice.setPaintFlags(tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tvPrice.setVisibility(View.VISIBLE);
            tvPromotionPrice.setText(MoneyType.toMoney(product.getPromotion_price())+" VND");
        }else{
            tvPromotionPrice.setText(MoneyType.toMoney(product.getPrice()) +" VND");
        }
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


    @Override
    public void whenfinish(ResponeFromServer output) {
        if(output!=null){
            if(output.code()==200){
                Toast.makeText(this, "Thêm vào giỏ hàng thành công", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this, "Có lỗi xảy ra!", Toast.LENGTH_SHORT).show();
            }
        }else{

        }
    }
}
