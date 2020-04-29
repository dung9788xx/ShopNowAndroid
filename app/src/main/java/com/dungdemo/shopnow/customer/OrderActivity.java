package com.dungdemo.shopnow.customer;

import android.content.Context;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dungdemo.shopnow.AsyncResponse;
import com.dungdemo.shopnow.HostName;
import com.dungdemo.shopnow.R;
import com.dungdemo.shopnow.TaskConnect;
import com.dungdemo.shopnow.model.Order;
import com.dungdemo.shopnow.model.User;
import com.dungdemo.shopnow.store.OrderManagerFragment;
import com.dungdemo.shopnow.utils.MoneyType;
import com.dungdemo.shopnow.utils.ResponeFromServer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderActivity extends AppCompatActivity implements AsyncResponse {
    ListView lv;
    ArrayAdapter<Order> orderArrayAdapter;
     ArrayList<Order> orders;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.cart_toolbar);
        getSupportActionBar().getCustomView().findViewById(R.id.backImg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        TextView toolbarTitle = getSupportActionBar().getCustomView().findViewById(R.id.tvProductName);
        toolbarTitle.setText("Đơn hàng");

        lv=findViewById(R.id.lvOrder);
        loadData();
        findViewById(R.id.tvSortByDate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i=0;i<orders.size();i++){
                    for(int j=i+1;j<orders.size();j++){
                    if(!compareData(orders.get(i).getDate(),orders.get(j).getDate())) {
                           Order t=orders.get(j);
                           orders.set(j,orders.get(i));
                           orders.set(i,t);
                    }
                    }
                }
                orderArrayAdapter.notifyDataSetChanged();
            }
        });
        findViewById(R.id.tvSortProgressing).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (orders.size() > 0) {
                    for (int i = 0; i < orders.size(); i++) {
                        if (orders.get(i).getStatus().getStatus_id() == 1) {
                            orders.add(0, orders.get(i));
                            orders.remove(i + 1);
                        }
                    }
                    orderArrayAdapter.notifyDataSetChanged();
                }
            }
        });
        findViewById(R.id.tvSortByShipping).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (orders.size() > 0) {
                    for (int i = 0; i < orders.size(); i++) {
                        if (orders.get(i).getStatus().getStatus_id() == 2) {
                            orders.add(0, orders.get(i));
                            orders.remove(i + 1);
                        }
                    }
                    orderArrayAdapter.notifyDataSetChanged();
                }
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent t=new Intent(OrderActivity.this,CustomerOrderDetail.class);
                t.putExtra("order",orders.get(i));
                startActivityForResult(t,1);
            }
        });
    }

    private void loadData() {
        Map<String, String> map = new HashMap<>();
        map.put("token", User.getSavedToken(OrderActivity.this));
        map.put("method", "get");
       TaskConnect task = new TaskConnect(OrderActivity.this, HostName.host + "/order/getOrderByUser");
        task.setMap(map);
        task.execute();
    }

    @Override
    public void whenfinish(ResponeFromServer output) {
        if (output.code() == 200) {
            String json = null;
            json = output.getBody() + "";
            Type listType = new TypeToken<List<Order>>() {
            }.getType();
            orders = new Gson().fromJson(json, listType);
            if (orders.size() > 0) {
                orderArrayAdapter = new ArrayAdapter<Order>(OrderActivity.this, R.layout.customer_order_item, orders) {
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View v = layoutInflater.inflate(R.layout.customer_order_item, null);
                        TextView tvDate = v.findViewById(R.id.tvDate);
                        TextView tvStatus = v.findViewById(R.id.tvStatus);
                        TextView tvOrderId = v.findViewById(R.id.tvOrderId);
                        TextView tvAmount = v.findViewById(R.id.tvAmount);

                        Order order = orders.get(position);
                        tvDate.setText(order.getDate());
                        tvOrderId.setText(order.getOrder_id() + "");
                        tvAmount.setText(MoneyType.toMoney(order.getUser().getUser_id())+" VND");
                        tvStatus.setText(order.getStatus().getName());
                        if (order.getStatus().getStatus_id() == 1) {
                            tvStatus.setTextColor(Color.parseColor("#FF009688"));
                        } else if (order.getStatus().getStatus_id() == 2) {
                            tvStatus.setTextColor(Color.parseColor("#FFFFC107"));
                        } else if (order.getStatus().getStatus_id() == 3) {
                            tvStatus.setTextColor(Color.parseColor("#FF673AB7"));
                        } else if (order.getStatus().getStatus_id() == 4) {
                            tvStatus.setTextColor(Color.parseColor("#FFF44336"));
                        } else {
                            tvStatus.setTextColor(Color.parseColor("#FFF44336"));
                        }
                        return v;
                    }
                };
                lv.setAdapter(orderArrayAdapter);
            }
        }
    }
    public boolean compareData(String d1,String d2){
        String date1[]=d1.split("/");
        String date2[]=d2.split("/");
        if(Integer.parseInt(date1[2])>Integer.parseInt(date2[2])) return true;
        if(Integer.parseInt(date1[2])==Integer.parseInt(date2[2])){
            if(Integer.parseInt(date1[1])>Integer.parseInt(date2[1])) return true;
            else if(Integer.parseInt(date1[1])==Integer.parseInt(date2[1])){
                if (Integer.parseInt(date1[0])>Integer.parseInt(date2[0])) return true;
            }
        }

    return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadData();
    }
}
