package com.dungdemo.shopnow.register;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dungdemo.shopnow.AsyncResponse;
import com.dungdemo.shopnow.HostName;
import com.dungdemo.shopnow.R;
import com.dungdemo.shopnow.TaskConnect;
import com.dungdemo.shopnow.model.District;
import com.dungdemo.shopnow.model.Province;
import com.dungdemo.shopnow.model.User;
import com.dungdemo.shopnow.model.Ward;
import com.dungdemo.shopnow.utils.ResponeFromServer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CustomerRegisterActivity extends AppCompatActivity implements AsyncResponse {
    Spinner spProvince, spDistrict, spWard;
    Button btnRegister;
    EditText edtName, edtUsername, edtPassword, edtRepassword, edtPhone, edtStreet;
    List<Province> provinceList;
    List<District> districtList;
    List<Ward> wardList;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_register);
        edtName = findViewById(R.id.edtName);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtRepassword = findViewById(R.id.edtRepassword);
        edtPhone = findViewById(R.id.edtPhone);
        edtStreet = findViewById(R.id.edtStreet);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progress);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateForm() == 1) {
                    Toast.makeText(CustomerRegisterActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_LONG).show();
                } else if (validateForm() == 2) {
                    Toast.makeText(CustomerRegisterActivity.this, "Hai mật khẩu không trùng khớp!", Toast.LENGTH_SHORT).show();

                } else {
                    Map<String, String> map = new HashMap<>();
                    map.put("name", edtName.getText().toString());
                    map.put("username",edtUsername.getText().toString());
                    map.put("password",edtPassword.getText().toString());
                    map.put("phone",edtPhone.getText().toString());
                    map.put("province_id",provinceList.get(spProvince.getSelectedItemPosition()).getProvince_id()+"");
                    map.put("district_id",districtList.get(spDistrict.getSelectedItemPosition()).getDistrict_id()+"");
                    map.put("ward_id",wardList.get(spWard.getSelectedItemPosition()).getId()+"");
                    map.put("street",edtStreet.getText().toString());
                    map.put("level","3");
                    map.put("active","1");
                    map.put("method", "post");
                    TaskConnect task = new TaskConnect(CustomerRegisterActivity.this, HostName.host + "/user");
                    task.setMap(map);
                    progressBar.setVisibility(View.VISIBLE);
                    btnRegister.setEnabled(false);
                    task.execute();
                }
            }
        });
        loadToolbarInfo();
        loadProvinceSpinner();
    }

    private int validateForm() {
        if (edtName.getText().toString().trim().equals("") ||
                edtUsername.getText().toString().trim().equals("") ||
                edtPassword.getText().toString().trim().equals("") ||
                edtRepassword.getText().toString().trim().equals("") ||
                edtPhone.getText().toString().trim().equals("") ||
                edtStreet.getText().toString().trim().equals("")) {
            return 1;
        }
        if (!edtRepassword.getText().toString().equals(edtPassword.getText().toString())) {
            return 2;
        }
        return 0;
    }

    private void loadProvinceSpinner() {
        spProvince = findViewById(R.id.spProvince);
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
                    CustomerRegisterActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            ArrayAdapter<Province> arrayAdapter = new ArrayAdapter<Province>(CustomerRegisterActivity.this, R.layout.category_item_spinner, provinceList) {
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
                        }
                    });
//
                }
            }
        });
    }

    private void loadDistrict(int province_id) {
        spDistrict = findViewById(R.id.spDistrict);
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
                    CustomerRegisterActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ArrayAdapter<District> arrayAdapter = new ArrayAdapter<District>(CustomerRegisterActivity.this, R.layout.category_item_spinner, districtList) {
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
                        }
                    });
//
                }
            }
        });
    }

    private void loadWard(int id) {
        spWard = findViewById(R.id.spWard);
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
                    CustomerRegisterActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ArrayAdapter<Ward> arrayAdapter = new ArrayAdapter<Ward>(CustomerRegisterActivity.this, R.layout.category_item_spinner, wardList) {
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
                        }
                    });
//
                }
            }
        });
    }

    private void loadToolbarInfo() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.cart_toolbar);
        getSupportActionBar().getCustomView().findViewById(R.id.backImg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        TextView tv = getSupportActionBar().getCustomView().findViewById(R.id.tvProductName);
        tv.setText("Đăng ký thông tin người dùng");
    }

    @Override
    public void whenfinish(ResponeFromServer output) {
        progressBar.setVisibility(View.INVISIBLE);
        btnRegister.setEnabled(true);
        if(output!=null){
            if(output.code()==200){
                Toast.makeText(this, "Đăng ký thành công ", Toast.LENGTH_SHORT).show();
                finish();
            }
            if (output.code()==403){
                Toast.makeText(this, new Gson().fromJson(output.getBody(),String.class), Toast.LENGTH_SHORT).show();
            }
        }

    }
}
