package com.dungdemo.shopnow.admin;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dungdemo.shopnow.AsyncResponse;
import com.dungdemo.shopnow.HostName;
import com.dungdemo.shopnow.LoginActivity;
import com.dungdemo.shopnow.Model.User;
import com.dungdemo.shopnow.R;
import com.dungdemo.shopnow.TaskConnect;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Response;

import static android.support.v4.content.ContextCompat.getSystemService;


public class UserManagerFragment extends Fragment implements AsyncResponse {
    TaskConnect task;
    List<User> userList=new ArrayList<>();
    ListView userListView;

    ArrayAdapter<User> arrayAdapter;
    public UserManagerFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Map<String,String > map=new HashMap<>(  );

        map.put("method","get");
        task=new TaskConnect(UserManagerFragment.this, HostName.host+"/user");
        task.setMap( map );
//        progressBar.setVisibility( View.VISIBLE );
        task.execute();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_user_manager, container, false);
        userListView=view.findViewById(R.id.userListview);
        return view;
    }

    @Override
    public void whenfinish(Response output) {
      if(output!=null){
          String json= null;
          try {
              json = output.body().string();
          } catch (IOException e) {
              e.printStackTrace();
          }
          Type listType = new TypeToken<List<User>>() {}.getType();

     userList= new Gson().fromJson(json, listType);
          arrayAdapter=new ArrayAdapter<User>(getActivity(),R.layout.custom_listview_user,userList){
              @Override
              public View getView(int position, View convertView,  ViewGroup parent) {
                  LayoutInflater layoutInflater=(LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                  View v=layoutInflater.inflate(R.layout.custom_listview_user,null);
                  TextView name=v.findViewById(R.id.tvName);
                  TextView username=v.findViewById(R.id.tvUsername);
                  TextView active=v.findViewById(R.id.tvActive);
                  User user=userList.get(position);

                      name.setText(user.getName());
                      username.setText(user.getUsername());
                      if(user.getActive()==1){
                          active.setText("Đang hoạt động");
                          active.setTextColor(Color.GREEN);
                      }else {
                          active.setText("Đã bị khóa");
                          active.setTextColor(Color.RED);
                      }


                  return v;

              }
          };
          userListView.setAdapter(arrayAdapter);

      }else{
          Toast.makeText(getContext(), "Kiểm tra lại kết nối", Toast.LENGTH_SHORT).show();
      }
    }
}
