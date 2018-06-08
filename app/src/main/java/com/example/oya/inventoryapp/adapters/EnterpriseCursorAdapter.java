package com.example.oya.inventoryapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.oya.inventoryapp.R;
import com.example.oya.inventoryapp.data.InventoryContract;

public class EnterpriseCursorAdapter extends CursorAdapter {

    public EnterpriseCursorAdapter(@NonNull Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_enterprise, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView supplierName_tv = view.findViewById(R.id.list_item_supplier_name);
        TextView contact_person_tv = view.findViewById(R.id.list_item_supplier_contact_person);
        ImageButton phone_btn = view.findViewById(R.id.enterprise_item_phone_button);

        int enterpriseNameColumnIndex = cursor.getColumnIndex(InventoryContract.EnterpriseEntry.ENTERPRISE_NAME);
        int contactPersonColumnIndex = cursor.getColumnIndex(InventoryContract.EnterpriseEntry.ENTERPRISE_CONTACT_PERSON);
        int phoneColumnIndex = cursor.getColumnIndex(InventoryContract.EnterpriseEntry.ENTERPRISE_PHONE);

        String enterpriseName = cursor.getString(enterpriseNameColumnIndex);
        String contactPerson = cursor.getString(contactPersonColumnIndex);
        final String phone = cursor.getString(phoneColumnIndex);

        supplierName_tv.setText(enterpriseName);
        contact_person_tv.setText(contactPerson);
        phone_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                mContext.startActivity(intent);
            }
        });

    }
}
