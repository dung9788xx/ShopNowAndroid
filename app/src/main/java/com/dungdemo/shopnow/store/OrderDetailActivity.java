package com.dungdemo.shopnow.store;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dungdemo.shopnow.HostName;
import com.dungdemo.shopnow.R;
import com.dungdemo.shopnow.model.Cart_Detail;
import com.dungdemo.shopnow.model.Order_Detail;
import com.dungdemo.shopnow.model.Product;
import com.dungdemo.shopnow.model.ProductCategory;
import com.dungdemo.shopnow.model.User;
import com.dungdemo.shopnow.utils.MoneyType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OrderDetailActivity extends AppCompatActivity {
    int order_id;
    ListView orderLv;
    ArrayList<Order_Detail> order_details;
    ArrayAdapter<Order_Detail> arrayAdapter;
    TextView tvAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.cart_toolbar);
        getSupportActionBar().getCustomView().findViewById(R.id.backImg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        TextView toolbarTitle=getSupportActionBar().getCustomView().findViewById(R.id.tvProductName);
        toolbarTitle.setText("Thông tin đơn hàng");


        order_id = getIntent().getIntExtra("order_id", -1);
        orderLv = findViewById(R.id.lv);
        tvAmount=findViewById(R.id.tvAmount);
        loadOrderData();


    }

    private void loadOrderData() {

        OkHttpClient client = new OkHttpClient();
        String url = HostName.host + "/order/getOrderDetail/"+order_id;

        Request request = new Request.Builder()
                .url(url).addHeader("Authorization", User.getSavedToken(this))
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("lol",e.toString()+"");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = null;
                    json = response.body().string() + "";
                    Type listType = new TypeToken<List<Order_Detail>>() {
                    }.getType();
                    order_details = new Gson().fromJson(json, listType);

                    OrderDetailActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            arrayAdapter = new ArrayAdapter<Order_Detail>(OrderDetailActivity.this, R.layout.orderdetail_listview_item, order_details) {
                                @NonNull
                                @Override
                                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                    LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    View v = layoutInflater.inflate(R.layout.orderdetail_listview_item, null);
                                    ImageView thumbnail = v.findViewById(R.id.thumbnail);
                                    TextView name = v.findViewById(R.id.tvName);
                                    TextView price = v.findViewById(R.id.tvPrice);
                                    TextView tvQuantity = v.findViewById(R.id.tvQuantity);
                                    Order_Detail order_detail = order_details.get(position);
                                    Picasso.get().load(HostName.host + "/getThumbImage/" + order_detail.getProduct_id()).into(thumbnail);
                                    name.setText(order_detail.getName());
                                    price.setText(MoneyType.toMoney(order_detail.getPrice()) + " VND");
                                    tvQuantity.setText(order_detail.getQuantity() + "");
                                    return v;
                                }
                            };
                            caculateAmount();
                            orderLv.setAdapter(arrayAdapter);
                        }
                    });
//
                }
            }
        });
    }
    public void caculateAmount(){
        long amount=0;
        for(Order_Detail order_detail:order_details){
            amount=amount+order_detail.getQuantity()*order_detail.getPrice();
        }
        tvAmount.setText(MoneyType.toMoney(amount)+" VND");
    }
}
