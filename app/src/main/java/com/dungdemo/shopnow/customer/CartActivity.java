package com.dungdemo.shopnow.customer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dungdemo.shopnow.AsyncResponse;
import com.dungdemo.shopnow.HostName;
import com.dungdemo.shopnow.LoginActivity;
import com.dungdemo.shopnow.R;
import com.dungdemo.shopnow.TaskConnect;
import com.dungdemo.shopnow.admin.AdminActivity;
import com.dungdemo.shopnow.model.Cart;
import com.dungdemo.shopnow.model.Cart_Detail;
import com.dungdemo.shopnow.model.District;
import com.dungdemo.shopnow.model.Order;
import com.dungdemo.shopnow.model.ProductCategory;
import com.dungdemo.shopnow.model.Province;
import com.dungdemo.shopnow.model.User;
import com.dungdemo.shopnow.model.Ward;
import com.dungdemo.shopnow.register.StoreRegisterActivity;
import com.dungdemo.shopnow.store.ActivityAddProduct;
import com.dungdemo.shopnow.utils.MoneyType;
import com.dungdemo.shopnow.utils.ResponeFromServer;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

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
import okhttp3.RequestBody;
import okhttp3.Response;

public class CartActivity extends AppCompatActivity implements AsyncResponse {
    List<Cart_Detail> cart_details;
    Cart cart;
    ArrayAdapter<Cart_Detail> arrayAdapter;
    ProgressBar progressBar, dialogProgressBar;
    ListView lvCart;
    TextView tvNoItem,tvAmount;
    FrameLayout content;
    JSONObject shippingInfo;
    AlertDialog dialog;
    Spinner spProvince, spDistrict, spWard;
    EditText edtPhone, edtStreet;
    List<Province> provinceList;
    List<District> districtList;
    List<Ward> wardList;

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
        tvAmount=findViewById(R.id.tvAmount);
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
        findViewById(R.id.btnBook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmAddress();
//                progressBar.setVisibility(View.VISIBLE);
//                placeBook();
            }
        });
        loadData();
        loadShippingInfo();

    }

    private void loadShippingInfo() {
        OkHttpClient client = new OkHttpClient();
        String url = HostName.host + "/cart/getShippingInfo";
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
                    try {
                        shippingInfo = new JSONObject(json);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    CartActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (shippingInfo != null) {
                                try {
                                    loadShippingAddressDialog();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
            }
        });

    }

    private void showConfirmAddress() {

        dialog.show();
    }

    private void placeOrder() {
        OkHttpClient client = new OkHttpClient();
        String url = HostName.host + "/cart/placeOrder";
        Map<String, String> map = new HashMap<>();
        map.put("token", User.getSavedToken(getApplication()));
        map.put("phone", edtPhone.getText().toString());
        map.put("address", edtStreet.getText().toString() + ", "
                + wardList.get(spWard.getSelectedItemPosition()).getPrefix() + " " + wardList.get(spWard.getSelectedItemPosition()).getName()
                + ", " + districtList.get(spDistrict.getSelectedItemPosition()).getPrefix() + " " + districtList.get(spDistrict.getSelectedItemPosition()).getName()
                + ", " + provinceList.get(spProvince.getSelectedItemPosition()).getName());
        RequestBody formBody = TaskConnect.makeBuilderFromMap(map)
                .build();
        Request request = new Request.Builder()
                .url(url).addHeader("Authorization", User.getSavedToken(this)).post(formBody)
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
                    String strRespone = new Gson().fromJson(json, String.class);
                    CartActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialogProgressBar.setVisibility(View.INVISIBLE);
                            if (response.code() == 200) {
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(CartActivity.this);
                                builder1.setMessage(strRespone);
                                builder1.setCancelable(true);
                                builder1.setPositiveButton(
                                        "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog1, int id) {
                                                cart_details.clear();
                                                arrayAdapter.notifyDataSetChanged();
                                                progressBar.setVisibility(View.INVISIBLE);
                                                tvNoItem.setVisibility(View.VISIBLE);
                                                content.setVisibility(View.INVISIBLE);
                                                dialog1.cancel();
                                                dialog.dismiss();
                                            }
                                        });
                                AlertDialog alert11 = builder1.create();
                                alert11.show();
                            }
                        }
                    });
                }
            }
        });

    }

    private List<Cart_Detail_Temp> getListJson() {

        List<Cart_Detail_Temp> list = new ArrayList<>();
        for (Cart_Detail cart_detail : cart_details) {
            list.add(new Cart_Detail_Temp(cart_detail.getProduct().getProduct_id(),cart_detail.getPrice(), cart_detail.getQuantity(), cart_detail.getNote()));
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
                } else {
                    CartActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.INVISIBLE);
                            tvNoItem.setVisibility(View.VISIBLE);
                        }
                    });

                }
            }
        });
    }

    private void loadShippingAddressDialog() throws JSONException {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.place_order_dialog_layout, null);
        dialogProgressBar = layout.findViewById(R.id.progress);
        edtPhone = layout.findViewById(R.id.edtPhone);
        edtStreet = layout.findViewById(R.id.edtStreet);
        edtPhone.setText(shippingInfo.getString("phone"));
        edtStreet.setText(shippingInfo.getString("street"));
        spProvince = layout.findViewById(R.id.spProvince);
        spDistrict = layout.findViewById(R.id.spDistrict);
        spWard = layout.findViewById(R.id.spWard);
        layout.findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        layout.findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogProgressBar.setVisibility(View.VISIBLE);
                placeOrder();
            }
        });
        loadProvinceSpinner();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(layout);
        dialog = builder.create();

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
                                caculateAmount();
                                notifyDataSetChanged();
                            }
                        }
                    });
                    btnNegative.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (cart_detail.getQuantity() - 1 > 0) {
                                cart_detail.setQuantity(cart_detail.getQuantity() - 1);
                                caculateAmount();
                                notifyDataSetChanged();
                            }
                        }
                    });
                    return v;
                }
            };
            content.setVisibility(View.VISIBLE);
            caculateAmount();
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
        int price;
        int quantity;
        String note;

        public Cart_Detail_Temp(int product_id, int price, int quantity, String note) {
            this.product_id = product_id;
            this.price = price;
            this.quantity = quantity;
            this.note = note;
        }
    }

    private void loadProvinceSpinner() {
        spProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loadDistrict(provinceList.get(i).getProvince_id());

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        OkHttpClient client = new OkHttpClient();
        String url = HostName.host + "/province/getProvince";
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
                    Type listType = new TypeToken<List<Province>>() {
                    }.getType();
                    provinceList = new Gson().fromJson(json, listType);
                    CartActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            ArrayAdapter<Province> arrayAdapter = new ArrayAdapter<Province>(CartActivity.this, R.layout.category_item_spinner, provinceList) {
                                @NonNull
                                @Override
                                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                    LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    View v = layoutInflater.inflate(R.layout.category_item_spinner, null);
                                    TextView name = v.findViewById(R.id.tvName);
                                    name.setText(provinceList.get(position).getName());
                                    return v;
                                }

                                @Override
                                public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                    LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    View v = layoutInflater.inflate(R.layout.category_item_spinner, null);
                                    TextView name = v.findViewById(R.id.tvName);
                                    name.setText(provinceList.get(position).getName());
                                    return v;
                                }
                            };
                            spProvince.setAdapter(arrayAdapter);
                            for (int i = 0; i < provinceList.size(); i++) {
                                try {
                                    if (provinceList.get(i).getProvince_id() == shippingInfo.getInt("province_id")) {
                                        spProvince.setSelection(i);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    });
//
                }
            }
        });
    }

    private void loadDistrict(int province_id) {
        spDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loadWard(districtList.get(i).getDistrict_id());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        OkHttpClient client = new OkHttpClient();
        String url = HostName.host + "/province/getDistrictByProvince/" + province_id;
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
                    Type listType = new TypeToken<List<District>>() {
                    }.getType();
                    districtList = new Gson().fromJson(json, listType);
                    CartActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ArrayAdapter<District> arrayAdapter = new ArrayAdapter<District>(CartActivity.this, R.layout.category_item_spinner, districtList) {
                                @NonNull
                                @Override
                                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                    LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    View v = layoutInflater.inflate(R.layout.category_item_spinner, null);
                                    TextView name = v.findViewById(R.id.tvName);
                                    name.setText(districtList.get(position).getPrefix() + " " + districtList.get(position).getName());
                                    return v;
                                }

                                @Override
                                public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                    LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    View v = layoutInflater.inflate(R.layout.category_item_spinner, null);
                                    TextView name = v.findViewById(R.id.tvName);
                                    name.setText(districtList.get(position).getPrefix() + " " + districtList.get(position).getName());
                                    return v;
                                }
                            };
                            arrayAdapter.notifyDataSetChanged();
                            spDistrict.setAdapter(arrayAdapter);
                            for (int i = 0; i < districtList.size(); i++) {
                                try {
                                    if (districtList.get(i).getDistrict_id() == shippingInfo.getInt("district_id")) {
                                        spDistrict.setSelection(i);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    });
//
                }
            }
        });
    }

    private void loadWard(int id) {
        spWard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        OkHttpClient client = new OkHttpClient();
        String url = HostName.host + "/province/getWardByDistrict/" + id;
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
                    Type listType = new TypeToken<List<Ward>>() {
                    }.getType();
                    wardList = new Gson().fromJson(json, listType);
                    CartActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ArrayAdapter<Ward> arrayAdapter = new ArrayAdapter<Ward>(CartActivity.this, R.layout.category_item_spinner, wardList) {
                                @NonNull
                                @Override
                                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                    LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    View v = layoutInflater.inflate(R.layout.category_item_spinner, null);
                                    TextView name = v.findViewById(R.id.tvName);
                                    name.setText(wardList.get(position).getPrefix() + " " + wardList.get(position).getName());
                                    return v;
                                }

                                @Override
                                public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                    LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    View v = layoutInflater.inflate(R.layout.category_item_spinner, null);
                                    TextView name = v.findViewById(R.id.tvName);
                                    name.setText(wardList.get(position).getPrefix() + " " + wardList.get(position).getName());
                                    return v;
                                }
                            };
                            arrayAdapter.notifyDataSetChanged();
                            spWard.setAdapter(arrayAdapter);
                            for (int i = 0; i < wardList.size(); i++) {
                                try {
                                    if (wardList.get(i).getId() == shippingInfo.getInt("ward_id")) {
                                        spWard.setSelection(i);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
            }
        });
    }
    public void caculateAmount(){
        long amount=0;
       for(Cart_Detail cart_detail:cart_details){
            amount=amount+cart_detail.getQuantity()*cart_detail.getPrice();
        }
       tvAmount.setText(MoneyType.toMoney(amount)+" VND");
    }
}
