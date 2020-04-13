package com.dungdemo.shopnow.customer;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dungdemo.shopnow.HostName;
import com.dungdemo.shopnow.R;
import com.dungdemo.shopnow.model.Product;
import com.dungdemo.shopnow.utils.MoneyType;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.RecyclerViewHolder>{
    private List<Product> products = new ArrayList<>();

    public RecycleViewAdapter(List<Product> products) {
        this.products = products;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.product_recycleview_item, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        Product product=products.get(position);
        holder.tvProductName.setText(product.getName());
        holder.tvPrice.setText(MoneyType.toMoney(product.getPrice()) +" VND");
        Picasso.get().load(HostName.imgurl+product.getProduct_id()+"/"+product.getImages().get(0).getImage_name())
        .into(holder.imgProduct);
        holder.viewClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickedListener.onItemClick(product.getProduct_id());
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }


    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName,tvPrice;
        ImageView imgProduct;
        View viewClick;
        public RecyclerViewHolder(View itemView) {
            super(itemView);
            tvProductName = (TextView) itemView.findViewById(R.id.tvProductName);
            tvPrice=itemView.findViewById(R.id.tvPrice);
            imgProduct=itemView.findViewById(R.id.imgProduct);
            viewClick=itemView.findViewById(R.id.view);
        }

    }
    public interface OnItemClickedListener {
        void onItemClick(int product_id);
    }

    private OnItemClickedListener onItemClickedListener;

    public void setOnItemClickedListener(OnItemClickedListener onItemClickedListener) {
        this.onItemClickedListener = onItemClickedListener;
    }

}
