package com.example.oya.inventoryapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.oya.inventoryapp.R;
import com.example.oya.inventoryapp.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends ArrayAdapter<Product> {

    ArrayList<Product> productList;

    public ProductAdapter(@NonNull Context context, ArrayList<Product> list) {
        super(context, 0, list);
        productList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_product, parent, false);
        }

        Product currentProduct = getItem(position);

        TextView productName_tv = listItemView.findViewById(R.id.list_item_product_name);
        productName_tv.setText(currentProduct.getProductName());

        TextView price_tv = listItemView.findViewById(R.id.list_item_price);
        price_tv.setText(String.valueOf(currentProduct.getSalePrice()));

        return listItemView;
    }

    public void refreshAdapter(List<Product> newList){
        productList.clear();
        productList.addAll(newList);
        notifyDataSetChanged();
    }
}
