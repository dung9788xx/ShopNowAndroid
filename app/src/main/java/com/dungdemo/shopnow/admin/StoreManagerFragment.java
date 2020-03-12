package com.dungdemo.shopnow.admin;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dungdemo.shopnow.AsyncResponse;
import com.dungdemo.shopnow.HostName;
import com.dungdemo.shopnow.Model.User;
import com.dungdemo.shopnow.R;
import com.dungdemo.shopnow.TaskConnect;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Response;

public class StoreManagerFragment extends Fragment implements AsyncResponse {
    TextView tv;
    TaskConnect task;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadData();
    }

    private void loadData() {
        Map<String,String > map=new HashMap<>(  );
        map.put("token", User.getSavedToken(getContext()));
        map.put("method","get");
        task=new TaskConnect(StoreManagerFragment.this, HostName.host+"/store");
        task.setMap( map );
        task.execute();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_store_manager, container, false);
        tv=view.findViewById(R.id.tv);
        return  view;
    }

    @Override
    public void whenfinish(Response output) {
            if(output!=null){
                if (output.code()==200){
                    String json= null;
                    try {
                        json = output.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Type listType = new TypeToken<List<User>>() {}.getType();
                  List<User>  userList= new Gson().fromJson(json, listType);
                    Toast.makeText(getActivity(), userList.get(1).getStore().getName()+"||"+userList.get(1).getStore().getLocation().getName(), Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(getActivity(), "Co loi xay ra", Toast.LENGTH_SHORT).show();
                }
                
            }
            else{
                Toast.makeText(getActivity(), "Kiểm tra lại kết nối!", Toast.LENGTH_SHORT).show();
            }
    }
}
