package com.example.oya.inventoryapp.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.oya.inventoryapp.R;
import com.example.oya.inventoryapp.adapters.TransactionsAdapter;
import com.example.oya.inventoryapp.model.Transaction;
import com.example.oya.inventoryapp.utils.DatabaseUtils;

import java.util.ArrayList;

public class TransactionListFragment extends Fragment {

    public TransactionListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_listview, container, false);
        getActivity().setTitle(getString(R.string.all_transactions));
        ArrayList<Transaction> transactionList = DatabaseUtils.getTransactions(getActivity());
        TransactionsAdapter adapter = new TransactionsAdapter(getActivity(), transactionList);
        ListView listView = rootView.findViewById(R.id.list);
        listView.setAdapter(adapter);
        TextView empty_tv = rootView.findViewById(R.id.empty_view);
        empty_tv.setText("No transactions found");
        listView.setEmptyView(empty_tv);
        return rootView;
    }
}
