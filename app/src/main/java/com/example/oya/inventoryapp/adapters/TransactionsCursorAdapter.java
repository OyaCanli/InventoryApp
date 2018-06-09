package com.example.oya.inventoryapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.oya.inventoryapp.R;
import com.example.oya.inventoryapp.data.InventoryContract;
import com.example.oya.inventoryapp.utils.Constants;

public class TransactionsCursorAdapter extends CursorAdapter{

    public TransactionsCursorAdapter(@NonNull Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_transaction, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        View transactionTypeSignifier = view.findViewById(R.id.viewForTransactionType);
        TextView transactionTitle_tv = view.findViewById(R.id.list_item_transaction_title);
        TextView transactionDate_tv = view.findViewById(R.id.list_item_transaction_date);

        int transactionTypeColumnIndex = cursor.getColumnIndex(InventoryContract.TransactionEntry.TRANSACTION_TYPE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.TransactionEntry.QUANTITY);
        int enterpriseNameColumnIndex = cursor.getColumnIndex(InventoryContract.TransactionEntry.ENTERPRISE_NAME);
        int dateColumnIndex = cursor.getColumnIndex(InventoryContract.TransactionEntry.TRANSACTION_DATE);
        int productNameColumnIndex = cursor.getColumnIndex(InventoryContract.TransactionEntry.PRODUCT_NAME);

        String transactionType = cursor.getString(transactionTypeColumnIndex);
        int quantity = cursor.getInt(quantityColumnIndex);
        String enterpriseName = cursor.getString(enterpriseNameColumnIndex);
        String date = cursor.getString(dateColumnIndex);
        String productName = cursor.getString(productNameColumnIndex);

        String transactionTitle;
        if(transactionType.equals(Constants.ACQUISITION)){
            transactionTypeSignifier.setBackgroundColor(mContext.getResources().getColor(R.color.acquisition));
            transactionTitle = mContext.getString(R.string.acquisition_title, quantity, productName, enterpriseName);
        } else {
            transactionTypeSignifier.setBackgroundColor(mContext.getResources().getColor(R.color.delivery));
            transactionTitle = mContext.getString(R.string.delivery_title, quantity, productName, enterpriseName);
        }
        transactionTitle_tv.setText(transactionTitle);
        transactionDate_tv.setText(date);
    }
}
