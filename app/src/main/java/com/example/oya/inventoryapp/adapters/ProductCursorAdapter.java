package com.example.oya.inventoryapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.oya.inventoryapp.R;
import com.example.oya.inventoryapp.data.InventoryContract;
import com.example.oya.inventoryapp.utils.GlideApp;

import java.text.NumberFormat;

public class ProductCursorAdapter extends CursorAdapter{

    public ProductCursorAdapter(@NonNull Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_item_product, parent, false);
        ViewHolder holder = new ViewHolder(v);
        v.setTag(holder);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();

        final String productName = cursor.getString(cursor.getColumnIndex(InventoryContract.ProductEntry.PRODUCT_NAME));
        int quantity = cursor.getInt(cursor.getColumnIndex(InventoryContract.ProductEntry.QUANTITY_IN_STOCK));
        float price = cursor.getFloat(cursor.getColumnIndex(InventoryContract.ProductEntry.SALE_PRICE));
        String imageFilePath = cursor.getString(cursor.getColumnIndex(InventoryContract.ProductEntry.IMAGE_FILE_PATH));

        holder.productName_tv.setText(productName);
        holder.quantity_tv.setText(mContext.getString(R.string.in_stock, quantity));
        holder.price_tv.setText(NumberFormat.getCurrencyInstance().format(price));
        GlideApp.with(context)
                .load(imageFilePath)
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .into(holder.product_iv);
    }

    private class ViewHolder{

        final TextView productName_tv;
        final TextView price_tv;
        final TextView quantity_tv;
        final ImageView product_iv;

        ViewHolder(View view){
            this.productName_tv = view.findViewById(R.id.product_item_product_name);
            this.price_tv = view.findViewById(R.id.product_item_price);
            this.quantity_tv = view.findViewById(R.id.product_item_quantity);
            this.product_iv = view.findViewById(R.id.product_item_image);
        }
    }
}
