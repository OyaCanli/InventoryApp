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

    private final SaleOrderButtonsClickListener mTransactionBtnListener;

    public ProductCursorAdapter(@NonNull Context context, Cursor cursor, SaleOrderButtonsClickListener listener) {
        super(context, cursor, 0);
        mTransactionBtnListener = listener;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_product, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView productName_tv = view.findViewById(R.id.product_item_product_name);
        TextView price_tv = view.findViewById(R.id.product_item_price);
        TextView quantity_tv = view.findViewById(R.id.product_item_quantity);
        TextView sale_btn = view.findViewById(R.id.product_item_sale_btn);
        TextView command_btn = view.findViewById(R.id.product_item_command_btn);
        ImageView product_iv = view.findViewById(R.id.product_item_image);

        final long id = cursor.getLong(cursor.getColumnIndex(InventoryContract.ProductEntry._ID));
        final String productName = cursor.getString(cursor.getColumnIndex(InventoryContract.ProductEntry.PRODUCT_NAME));
        int quantity = cursor.getInt(cursor.getColumnIndex(InventoryContract.ProductEntry.QUANTITY_IN_STOCK));
        float price = cursor.getFloat(cursor.getColumnIndex(InventoryContract.ProductEntry.SALE_PRICE));
        String imageFilePath = cursor.getString(cursor.getColumnIndex(InventoryContract.ProductEntry.IMAGE_FILE_PATH));

        productName_tv.setText(productName);
        quantity_tv.setText(mContext.getString(R.string.in_stock, quantity));
        price_tv.setText(NumberFormat.getCurrencyInstance().format(price));
        GlideApp.with(context)
                .load(imageFilePath)
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .into(product_iv);

        sale_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTransactionBtnListener.onSaleOrOrderButtonClicked(v, id, productName);
            }
        });
        command_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTransactionBtnListener.onSaleOrOrderButtonClicked(v, id, productName);
            }
        });
    }

    public interface SaleOrderButtonsClickListener{
        void onSaleOrOrderButtonClicked(View v, long id, String productName);
    }

}
