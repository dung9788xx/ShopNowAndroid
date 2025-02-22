package com.dungdemo.shopnow.admin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dungdemo.shopnow.AsyncResponse;
import com.dungdemo.shopnow.HostName;
import com.dungdemo.shopnow.LoginActivity;
import com.dungdemo.shopnow.model.ProductCategory;
import com.dungdemo.shopnow.model.User;
import com.dungdemo.shopnow.R;
import com.dungdemo.shopnow.TaskConnect;
import com.dungdemo.shopnow.register.CustomerRegisterActivity;
import com.dungdemo.shopnow.store.ActivityAddProduct;
import com.dungdemo.shopnow.utils.ResponeFromServer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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


public class UserManagerFragment extends Fragment implements AsyncResponse {
    TaskConnect task;
    List<User> userList = new ArrayList<>();
    ListView userListView;
    ArrayAdapter<User> arrayAdapter;

    public UserManagerFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loaddata();

    }

    private void loaddata() {
        Map<String, String> map = new HashMap<>();
        map.put("token", User.getSavedToken(getContext()));
        map.put("method", "get");
        task = new TaskConnect(UserManagerFragment.this, HostName.host + "/user");
        task.setMap(map);
        task.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_manager, container, false);
        view.findViewById(R.id.tvSortByActive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 1; i < userList.size(); i++) {
                    if (userList.get(i).getActive() == 1) {
                        userList.add(0, userList.get(i));
                        userList.remove(i + 1);
                    }
                }
                arrayAdapter.notifyDataSetChanged();
            }
        });
        view.findViewById(R.id.tvSortByBlocked).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 1; i < userList.size(); i++) {
                    if (userList.get(i).getActive() == 0) {
                        userList.add(0, userList.get(i));
                        userList.remove(i + 1);
                    }
                }
                arrayAdapter.notifyDataSetChanged();
            }
        });
        view.findViewById(R.id.addUserButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getContext(), CustomerRegisterActivity.class),1);
            }
        });
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Quản lý người dùng");
        userListView = view.findViewById(R.id.userListview);

        return view;
    }

    private void deleteUser(int user_id) {
        OkHttpClient client = new OkHttpClient();
        String url = HostName.host + "/user/" + user_id;
        Request request = new Request.Builder()
                .url(url).addHeader("Authorization", User.getSavedToken(getContext()))
                .delete().build();

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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Xóa thành công", Toast.LENGTH_LONG).show();
                            loaddata();
                        }
                    });
//
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Kiểm tra lại kết nối!", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void whenfinish(ResponeFromServer output) {
        if (output != null) {
            if (output.code() == 200) {
                String json = null;
                json = output.getBody() + "";

                Type listType = new TypeToken<List<User>>() {
                }.getType();

                userList = new Gson().fromJson(json, listType);
                arrayAdapter = new ArrayAdapter<User>(getActivity(), R.layout.custom_listview_usermanager_item, userList) {
                    @Override
                    public View getView(final int position, View convertView, ViewGroup parent) {
                        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View v = layoutInflater.inflate(R.layout.custom_listview_usermanager_item, null);
                        TextView name = v.findViewById(R.id.tvName);
                        TextView username = v.findViewById(R.id.tvUsername);
                        TextView active = v.findViewById(R.id.tvActive);
                        User user = userList.get(position);

                        name.setText(user.getName());
                        username.setText(user.getUsername());
                        if (user.getActive() == 1) {
                            active.setText("Đang hoạt động");
                            active.setTextColor(Color.BLUE);
                        } else {
                            active.setText("Đã bị khóa");
                            active.setTextColor(Color.RED);
                        }
                        v.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent t = new Intent(getActivity(), UserInfomationActivity.class);
                                t.putExtra("user_id", userList.get(position).getUser_id());
                                startActivityForResult(t, 11);
                            }
                        });
                        v.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                if (userList.get(position).getLevel() != 1) {
                                    new AlertDialog.Builder(getActivity())
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .setTitle("")
                                            .setMessage("Xóa tất cả thông tin liên quan đến nguời dùng này ?")
                                            .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    deleteUser(userList.get(position).getUser_id());
                                                }

                                            })
                                            .setNegativeButton("Không", null)
                                            .show();
                                    return true;
                                } else
                                    return false;
                            }
                        });
                        return v;

                    }
                };
                userListView.setAdapter(arrayAdapter);
            } else {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                builder1.setMessage("Phiên đăng nhập đã hết hạn !.");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Đăng nhập lại",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                User.logout(getActivity());
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                dialog.cancel();
                            }
                        });


                AlertDialog alert11 = builder1.create();
                alert11.show();

            }
        } else {
            Toast.makeText(getContext(), "Kiểm tra lại kết nối", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loaddata();

    }
}
