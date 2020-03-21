package com.dungdemo.shopnow.store;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dungdemo.shopnow.R;

public class OrderManagerFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_order_manager, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Quản lý đơn hàng");

        return  view;
    }

}
