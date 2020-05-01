package com.dungdemo.shopnow.customer;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dungdemo.shopnow.HostName;
import com.dungdemo.shopnow.R;
import com.dungdemo.shopnow.model.Product;
import com.dungdemo.shopnow.utils.MoneyType;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.RecyclerViewHolder>
implements Filterable {
    Context context;
    private List<Product> products = new ArrayList<>();
    private List<Product> productsFiltered = new ArrayList<>();
    public RecycleViewAdapter(List<Product> products,Context context)
    {
        this.products = products;
        productsFiltered=products;
        this.context=context;

    }


    public void setProducts(List<Product> products) {
        this.products = products;
        productsFiltered=products;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.product_recycleview_item, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        Product product=productsFiltered.get(position);
        holder.tvProductName.setText(product.getName());
        if (product.getPromotion_price()!=0) {
            holder.tvPrice.setTextColor(Color.rgb(105,105,105));
            holder.tvPrice.setText(MoneyType.toMoney(product.getPrice()) +" VND");
            holder.tvPrice.setPaintFlags(holder.tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvPromotionPrice.setVisibility(View.VISIBLE);
            holder.tvPromotionPrice.setText(MoneyType.toMoney(product.getPromotion_price())+" VND");
        }else{
            holder.tvPrice.setText(MoneyType.toMoney(product.getPrice()) +" VND");
        }

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
        return productsFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    productsFiltered = products;
                } else {
                    List<Product> filteredList = new ArrayList<>();
                    for (Product row : products) {
                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) ) {
                            filteredList.add(row);
                        }
                    }

                        productsFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = productsFiltered;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                productsFiltered = (ArrayList<Product>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName,tvPrice,tvPromotionPrice;
        ImageView imgProduct;
        View viewClick;
        public RecyclerViewHolder(View itemView) {
            super(itemView);
            tvProductName = (TextView) itemView.findViewById(R.id.tvProductName);
            tvPrice=itemView.findViewById(R.id.tvPrice);
            tvPromotionPrice=itemView.findViewById(R.id.tvPromotionPrice);
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
