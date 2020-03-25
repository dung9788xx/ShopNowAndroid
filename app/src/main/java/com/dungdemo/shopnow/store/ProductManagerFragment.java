package com.dungdemo.shopnow.store;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dungdemo.shopnow.AsyncResponse;
import com.dungdemo.shopnow.HostName;
import com.dungdemo.shopnow.Model.Product;
import com.dungdemo.shopnow.Model.User;
import com.dungdemo.shopnow.R;
import com.dungdemo.shopnow.TaskConnect;
import com.dungdemo.shopnow.admin.UserManagerFragment;
import com.dungdemo.shopnow.utils.ImageUtil;
import com.dungdemo.shopnow.utils.ResponeFromServer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductManagerFragment extends Fragment implements AsyncResponse {
    ListView lvProduct;
    TaskConnect task;
    List<Product> products=new ArrayList<>();
    ArrayAdapter<Product> arrayAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loaddata();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_product_manage, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Quản lý sản phẩm");
        lvProduct=view.findViewById(R.id.lvProduct);

        view.findViewById(R.id.addProductButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(),ActivityAddProduct.class),1);
            }
        });

        return  view;
    }

    @Override
    public void whenfinish(ResponeFromServer output) {
        if(output!=null){
            if(output.code()==200){
                String json = null;
                json = output.getBody() + "";
                Type listType = new TypeToken<List<Product>>() {
                }.getType();
                products = new Gson().fromJson(json, listType);
                arrayAdapter=new ArrayAdapter<Product>(getContext(),R.layout.product_listview_item,products){
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View v = layoutInflater.inflate(R.layout.product_listview_item, null);
                        ImageView thumbnail =v.findViewById(R.id.thumbnail);
                        TextView name=v.findViewById(R.id.tvName);
                        TextView price=v.findViewById(R.id.tvPrive);
                        TextView tvStatus=v.findViewById(R.id.tvStatus);
                        Product product=products.get(position);
                        if(product.getImages().size()>0){
                            thumbnail.setImageBitmap(ImageUtil.convert(product.getImages().get(0).getBase64()));
                        }
                        name.setText(product.getName());
                        price.setText(product.getPrice()+" VND");
                        if(product.getIsSelling()==1){
                            tvStatus.setText("Còn lại :"+product.getAmount()+" sp");
                        }else{
                            tvStatus.setTextColor(Color.RED);
                            tvStatus.setText("Đã tạm dừng bán");
                        }


                        return v;
                    }
                };
                lvProduct.setAdapter(arrayAdapter);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loaddata();
    }

    private void loaddata() {
        Map<String,String > map=new HashMap<>(  );
        map.put("token", User.getSavedToken(getContext()));
        map.put("method","get");
        task=new TaskConnect(ProductManagerFragment.this, HostName.host+"/product");
        task.setMap( map );
        task.execute();
    }
}
