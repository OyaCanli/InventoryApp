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
import com.example.oya.inventoryapp.model.Transaction;
import com.example.oya.inventoryapp.utils.Constants;

import java.util.ArrayList;

public class TransactionsAdapter extends ArrayAdapter<Transaction>{

    private Context mContext;

    public TransactionsAdapter(@NonNull Context context, ArrayList<Transaction> list) {
        super(context, 0, list);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_transaction, parent, false);
        }

        Transaction currentTransaction = getItem(position);
        //set the color of view according to whether it is a delivery or acquisition
        View transactionTypeSignifier = listItemView.findViewById(R.id.viewForTransactionType);
        String transactionTitle = null;
        String transactionType = currentTransaction.getTransactionType();
        if(transactionType.equals(Constants.ACQUISITION)){
            transactionTypeSignifier.setBackgroundColor(mContext.getResources().getColor(R.color.acquisition));
            transactionTitle = mContext.getString(R.string.acquisition_title, currentTransaction.getQuantity(), currentTransaction.getEnterpriseName());
        } else {
            transactionTypeSignifier.setBackgroundColor(mContext.getResources().getColor(R.color.delivery));
            transactionTitle = mContext.getString(R.string.delivery_title, currentTransaction.getQuantity(), currentTransaction.getEnterpriseName());
        }

        TextView transactionTitle_tv = listItemView.findViewById(R.id.list_item_transaction_title);
        transactionTitle_tv.setText(transactionTitle);
        TextView transactionDate_tv = listItemView.findViewById(R.id.list_item_transaction_date);
        transactionDate_tv.setText(currentTransaction.getDate());

        return listItemView;
    }
}
