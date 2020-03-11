package com.dungdemo.shopnow.admin;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dungdemo.shopnow.Model.User;
import com.dungdemo.shopnow.R;

import java.util.List;

public class UserCustomListViewAdapter extends BaseAdapter {
    private Context context;
    private int idLayout;
    private List<User> userLists;
    private int positionSelect = -1;

    public UserCustomListViewAdapter(Context context, int idLayout, List<User> userLists) {
        this.context = context;
        this.idLayout = idLayout;
        this.userLists = userLists;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return userLists.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

            convertView = LayoutInflater.from(parent.getContext()).inflate(idLayout, parent, false);

        TextView name=convertView.findViewById(R.id.tvName);
        TextView username=convertView.findViewById(R.id.tvUsername);
        TextView active=convertView.findViewById(R.id.tvActive);
        User user=userLists.get(position);
        if(user.getLevel()!=2){
            name.setText(user.getName());
            username.setText(user.getUsername());
            if(user.getActive()==1){
                active.setText("Đang hoạt động");
                active.setTextColor(Color.RED);
            }else {
                active.setText("Đã bị khóa");
                active.setTextColor(Color.BLUE);
            }

        }
        return convertView;
    }
}
