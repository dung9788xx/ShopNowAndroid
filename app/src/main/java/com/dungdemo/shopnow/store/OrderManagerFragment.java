package com.dungdemo.shopnow.store;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dungdemo.shopnow.AsyncResponse;
import com.dungdemo.shopnow.HostName;
import com.dungdemo.shopnow.Model.Order;
import com.dungdemo.shopnow.Model.Product;
import com.dungdemo.shopnow.Model.User;
import com.dungdemo.shopnow.R;
import com.dungdemo.shopnow.TaskConnect;
import com.dungdemo.shopnow.admin.UserManagerFragment;
import com.dungdemo.shopnow.utils.ResponeFromServer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderManagerFragment extends Fragment implements AsyncResponse {
    List<Order> orders;
    TaskConnect task;
    ListView orderListView;
    TextView tvNoItem;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadData();
    }

    private void loadData() {
        Map<String,String > map=new HashMap<>(  );
        map.put("token", User.getSavedToken(getContext()));
        map.put("method","get");
        task=new TaskConnect(OrderManagerFragment.this, HostName.host+"/order");
        task.setMap( map );
        task.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_order_manager, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Quản lý đơn hàng");
        orderListView=view.findViewById(R.id.lvOrder);
        tvNoItem=view.findViewById(R.id.noItem);


        return  view;
    }

    @Override
    public void whenfinish(ResponeFromServer output) {
        if(output!=null){

            if(output.code()==200){
                String json = null;
                json = output.getBody() + "";
                Type listType = new TypeToken<List<Order>>() {
                }.getType();
                orders = new Gson().fromJson(json, listType);
                if(orders.size()>0){
                    ArrayAdapter<Order> arrayAdapter=new ArrayAdapter<Order>(getContext(),R.layout.order_listview_item,orders){
                        @NonNull
                        @Override
                        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View v = layoutInflater.inflate(R.layout.order_listview_item, null);
                            TextView tvYear=v.findViewById(R.id.tvYear);
                            TextView tvDay=v.findViewById(R.id.tvDay);
                            TextView tvMonth=v.findViewById(R.id.tvMonth);
                            TextView name=v.findViewById(R.id.tvOrderUserName);
                            TextView orderId=v.findViewById(R.id.tvOrderId);
                            TextView phone=v.findViewById(R.id.tvPhone);
                            TextView tvStatus=v.findViewById(R.id.tvStatus);
                            Order order=orders.get(position);
                                tvYear.setText(order.getDate().split("/")[2]);
                                tvDay.setText(order.getDate().split("/")[0]);
                                tvMonth.setText(order.getDate().split("/")[1]);
                                name.setText(order.getUser().getName());
                                orderId.setText("#"+order.getOrder_id());
                                phone.setText(order.getShipping_phone());
                                if(order.getStatus().getStatus_id()==1){
                                    tvStatus.setText("Đơn mới");
                                    tvStatus.setTextColor(Color.parseColor("#FF009688"));
                                }else
                                if(order.getStatus().getStatus_id()==2){
                                    tvStatus.setText("Đang giao");
                                    tvStatus.setTextColor(Color.parseColor("#FFFFC107"));

                                }else
                                if(order.getStatus().getStatus_id()==3){
                                    tvStatus.setText("Đã nhận hàng");
                                    tvStatus.setTextColor(Color.parseColor("#FF673AB7"));
                                }else{
                                    tvStatus.setText("Đã hủy");
                                    tvStatus.setTextColor(Color.parseColor("#FFF44336"));
                                }

                            return v;
                        }
                    };
                orderListView.setAdapter(arrayAdapter);
                }else{
                        tvNoItem.setVisibility(View.VISIBLE);
                }

            }
        }
    }
}
