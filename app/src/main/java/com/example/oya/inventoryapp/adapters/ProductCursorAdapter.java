package com.example.oya.inventoryapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.CursorAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;

import com.bumptech.glide.Glide;
import com.example.oya.inventoryapp.R;
import com.example.oya.inventoryapp.data.InventoryContract;
import com.example.oya.inventoryapp.ui.AddTransactionFragment;
import com.example.oya.inventoryapp.utils.Constants;

public class ProductCursorAdapter extends CursorAdapter implements View.OnClickListener{

    SaleOrderButtonsClickListener mTransactionBtnListener;
    int mCursorPosition;

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

        mCursorPosition = cursor.getPosition();

        TextView productName_tv = view.findViewById(R.id.product_item_product_name);
        TextView price_tv = view.findViewById(R.id.product_item_price);
        TextView quantity_tv = view.findViewById(R.id.product_item_quantity);
        TextView sale_btn = view.findViewById(R.id.product_item_sale_btn);
        TextView command_btn = view.findViewById(R.id.product_item_command_btn);
        ImageView product_iv = view.findViewById(R.id.product_item_image);

        int productNameColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.PRODUCT_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.QUANTITY_IN_STOCK);
        int priceColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.SALE_PRICE);
        int imageColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.IMAGE_FILE_PATH);

        String productName = cursor.getString(productNameColumnIndex);
        int quantity = cursor.getInt(quantityColumnIndex);
        float price = cursor.getFloat(priceColumnIndex);
        String imageFilePath = cursor.getString(imageColumnIndex);

        productName_tv.setText(productName);
        quantity_tv.setText(mContext.getString(R.string.in_stock, quantity));
        price_tv.setText(NumberFormat.getCurrencyInstance().format(price));
        Glide.with(mContext)
                .load(imageFilePath)
                .into(product_iv);

        sale_btn.setOnClickListener(this);
        command_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mTransactionBtnListener.onSaleOrOrderButtonClicked(v, mCursorPosition);
    }


    public interface SaleOrderButtonsClickListener{
        void onSaleOrOrderButtonClicked(View v, int position);
    }

}
