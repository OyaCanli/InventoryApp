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
import com.example.oya.inventoryapp.model.Enterprise;

import java.util.ArrayList;

public class EnterpriseAdapter extends ArrayAdapter<Enterprise> {

    ArrayList<Enterprise> mEnterpriseList;

    public EnterpriseAdapter(@NonNull Context context, ArrayList<Enterprise> list) {
        super(context, 0, list);
        mEnterpriseList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_supplier, parent, false);
        }

        Enterprise currentEnterprise = getItem(position);

        TextView supplierName_tv = listItemView.findViewById(R.id.list_item_supplier_name);
        supplierName_tv.setText(currentEnterprise.getEnterpriseName());

        TextView contact_person_tv = listItemView.findViewById(R.id.list_item_supplier_contact_person);
        contact_person_tv.setText(String.valueOf(currentEnterprise.getEnterpriseContactPerson()));

        return listItemView;
    }
}
