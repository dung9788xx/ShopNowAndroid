package com.dungdemo.shopnow.customer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dungdemo.shopnow.AsyncResponse;
import com.dungdemo.shopnow.HostName;
import com.dungdemo.shopnow.R;
import com.dungdemo.shopnow.TaskConnect;
import com.dungdemo.shopnow.admin.AdminActivity;
import com.dungdemo.shopnow.model.Cart;
import com.dungdemo.shopnow.model.Cart_Detail;
import com.dungdemo.shopnow.model.Order;
import com.dungdemo.shopnow.model.ProductCategory;
import com.dungdemo.shopnow.model.User;
import com.dungdemo.shopnow.store.ActivityAddProduct;
import com.dungdemo.shopnow.utils.MoneyType;
import com.dungdemo.shopnow.utils.ResponeFromServer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

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

public class CartActivity extends AppCompatActivity implements AsyncResponse {
    List<Cart_Detail> cart_details;
    Cart cart;
    ArrayAdapter<Cart_Detail> arrayAdapter;
    TaskConnect task;
    ProgressBar progressBar;
    ListView lvCart;
    TextView tvNoItem;
    FrameLayout content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.cart_toolbar);
        getSupportActionBar().getCustomView().findViewById(R.id.backImg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        lvCart = findViewById(R.id.lvCart);
        progressBar = findViewById(R.id.progress);
        tvNoItem = findViewById(R.id.tvNoCartItem);
        content = findViewById(R.id.content);
        findViewById(R.id.btnUpdateCart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = new Gson().toJson(getListJson());
                Map<String, String> map = new HashMap<>();
                map.put("token", User.getSavedToken(getApplication()));
                map.put("method", "post");
                map.put("data", s);
                TaskConnect task = new TaskConnect(CartActivity.this, HostName.host + "/cart/updateCart");
                task.setMap(map);
                task.execute();
            }
        });
        loadData();

    }

    private List<Cart_Detail_Temp> getListJson() {

        List<Cart_Detail_Temp> list = new ArrayList<>();
        for (Cart_Detail cart_detail : cart_details) {
            list.add(new Cart_Detail_Temp(cart_detail.getProduct().getProduct_id(), cart_detail.getQuantity(), cart_detail.getNote()));
        }
        return list;
    }

    private void loadData() {
        OkHttpClient client = new OkHttpClient();
        String url = HostName.host + "/cart/getCartByUserId";

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

                    cart = new Gson().fromJson(json, Cart.class);
                    cart_details = cart.getDetail();
                    CartActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadDataToView();
                        }
                    });
//
                }
            }
        });
    }

    private void loadDataToView() {
        progressBar.setVisibility(View.INVISIBLE);
        if (cart_details.size() > 0) {
            arrayAdapter = new ArrayAdapter<Cart_Detail>(CartActivity.this, R.layout.cart_item, cart_details) {
                @NonNull
                @Override
                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View v = layoutInflater.inflate(R.layout.cart_item, null);
                    TextView tvProductName = v.findViewById(R.id.tvProductName);
                    TextView tvPrice = v.findViewById(R.id.tvPrice);
                    TextView tvQuantity = v.findViewById(R.id.tvQuantity);
                    EditText edtNote = v.findViewById(R.id.edtNote);
                    ImageView imgProduct = v.findViewById(R.id.imgProduct);
                    ImageView imgRemove = v.findViewById(R.id.imgRemove);
                    Cart_Detail cart_detail = cart_details.get(position);
                    tvProductName.setText(cart_detail.getProduct().getName());
                    tvPrice.setText(MoneyType.toMoney(cart_detail.getProduct().getPrice()) + " VND");
                    tvQuantity.setText(cart_detail.getQuantity() + "");
                    edtNote.setText(cart_detail.getNote());
                    edtNote.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            cart_details.get(position).setNote(edtNote.getText().toString());
                        }
                    });
                    Picasso.get().load(HostName.imgurl + cart_detail.getProduct().getProduct_id() + "/" + cart_detail.getProduct().getImages().get(0).getImage_name()).into(imgProduct);
                    imgRemove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new AlertDialog.Builder(CartActivity.this)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setTitle("Xóa khỏi giỏ hàng")
                                    .setMessage("Xóa sản phẩm này khỏi giỏ hàng ?")
                                    .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            cart_details.remove(position);
                                            if (cart_details.size() == 0) {
                                                tvNoItem.setVisibility(View.VISIBLE);
                                                content.setVisibility(View.INVISIBLE);
                                            }
                                            notifyDataSetChanged();
                                        }

                                    })
                                    .setNegativeButton("Không", null)
                                    .show();
                        }
                    });
                    Button btnPlus = v.findViewById(R.id.btnPlus);
                    Button btnNegative = v.findViewById(R.id.btnNegative);
                    btnPlus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (cart_detail.getQuantity() + 1 > cart_detail.getProduct().getAmount()) {
                                Toast.makeText(CartActivity.this, "Không đủ hàng!", Toast.LENGTH_SHORT).show();
                            } else {
                                cart_detail.setQuantity(cart_detail.getQuantity() + 1);
                                notifyDataSetChanged();
                            }
                        }
                    });
                    btnNegative.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (cart_detail.getQuantity() - 1 > 0) {
                                cart_detail.setQuantity(cart_detail.getQuantity() - 1);
                                notifyDataSetChanged();
                            }
                        }
                    });
                    return v;
                }
            };
            content.setVisibility(View.VISIBLE);
            lvCart.setAdapter(arrayAdapter);
        } else {
            tvNoItem.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void whenfinish(ResponeFromServer output) {
        if (output.code() == 200) {
            Toast.makeText(this, "Cập nhật giỏ hàng thành công!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Kiểm tra lại kết nối!", Toast.LENGTH_SHORT).show();
        }

    }

    class Cart_Detail_Temp {
        int product_id;
        int quantity;
        String note;

        public Cart_Detail_Temp(int product_id, int quantity, String note) {
            this.product_id = product_id;
            this.quantity = quantity;
            this.note = note;
        }
    }

    ;
}
