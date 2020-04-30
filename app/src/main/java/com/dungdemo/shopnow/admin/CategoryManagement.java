package com.dungdemo.shopnow.admin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dungdemo.shopnow.HostName;
import com.dungdemo.shopnow.R;
import com.dungdemo.shopnow.customer.CustomerMainActivity;
import com.dungdemo.shopnow.customer.CustomerOrderDetail;
import com.dungdemo.shopnow.model.ProductCategory;
import com.dungdemo.shopnow.model.User;
import com.dungdemo.shopnow.store.OrderDetailActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

public class CategoryManagement extends Fragment {
    List<ProductCategory> productCategories = new ArrayList<>();
    ListView lvCategory;
    ArrayAdapter<ProductCategory> arrayAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadCategory();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_management, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Quản lý danh mục");
        lvCategory = view.findViewById(R.id.lvCategory);
        lvCategory.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                new AlertDialog.Builder(getActivity())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("")
                        .setMessage("Xóa danh mục này sẽ xóa tất cả các sản phẩm nằm trong đó. " +
                                "Bạn có muốn xóa danh mục này  ?")
                        .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteCategory(productCategories.get(i).getCategory_id());
                            }

                        })
                        .setNegativeButton("Không", null)
                        .show();
                return false;
            }
        });
        view.findViewById(R.id.addCategoryButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    startActivityForResult(new Intent(getActivity(),AddCategoryActivity.class),1);
            }
        });
        return view;
    }

    private void deleteCategory(int category_id) {
        OkHttpClient client = new OkHttpClient();
        String url = HostName.host + "/category/" +category_id;
        Request request = new Request.Builder()
                .url(url).addHeader("Authorization", User.getSavedToken(getActivity())).delete()
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
                    if (response.code() == 200) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "Xóa danh mục thành công", Toast.LENGTH_LONG).show();
                               loadCategory();
                            }
                        });
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "Có lỗi xảy ra!", Toast.LENGTH_LONG).show();

                            }
                        });
                    }
//
                }
            }
        });
    }

    private void loadCategory() {
        OkHttpClient client = new OkHttpClient();
        String url = HostName.host + "/category";

        Request request = new Request.Builder()
                .url(url).addHeader("Authorization", User.getSavedToken(getActivity()))
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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadCategoryListView();
                        }
                    });
//
                }
            }
        });
    }

    private void loadCategoryListView() {
        arrayAdapter = new ArrayAdapter<ProductCategory>(getActivity(), R.layout.category_listview_item, productCategories) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = layoutInflater.inflate(R.layout.category_listview_item, null);
                TextView tvCategoryName = v.findViewById(R.id.tvCategoryName);
                TextView tvCategoryDetail = v.findViewById(R.id.tvCategoryDetail);
                ImageView imageView = v.findViewById(R.id.imgCategory);
                ProductCategory productCategory = productCategories.get(position);
                tvCategoryName.setText(productCategory.getName());
                tvCategoryDetail.setText(productCategory.getDetail());
                Picasso.get().load(HostName.categoryImageUrl + productCategory.getCategory_id() + ".png").into(imageView);

                return v;

            }
        };
        lvCategory.setAdapter(arrayAdapter);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            loadCategory();
    }
}
