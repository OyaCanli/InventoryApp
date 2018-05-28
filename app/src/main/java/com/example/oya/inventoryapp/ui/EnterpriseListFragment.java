package com.example.oya.inventoryapp.ui;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.oya.inventoryapp.Constants;
import com.example.oya.inventoryapp.R;
import com.example.oya.inventoryapp.adapters.EnterpriseAdapter;
import com.example.oya.inventoryapp.data.InventoryContract.EnterpriseEntry;
import com.example.oya.inventoryapp.data.InventoryDBHelper;
import com.example.oya.inventoryapp.model.Enterprise;

import java.util.ArrayList;

public class EnterpriseListFragment extends Fragment {

    private String mTypeOfRelationship;

    public EnterpriseListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_listview, container, false);
        Bundle bundle = getArguments();
        mTypeOfRelationship = bundle.getString(Constants.RELATION_TYPE);
        //get the list of suppliers from the database
        ArrayList<Enterprise> enterpriseList = getAllSuppliersOrClients(mTypeOfRelationship);
        EnterpriseAdapter adapter = new EnterpriseAdapter(getActivity(), enterpriseList);
        ListView listView = rootView.findViewById(R.id.list);
        listView.setAdapter(adapter);
        TextView empty_tv = rootView.findViewById(R.id.empty_view);
        empty_tv.setText(getString(R.string.no_enterprise_found, mTypeOfRelationship));
        listView.setEmptyView(empty_tv);
        return rootView;
    }

    private ArrayList<Enterprise> getAllSuppliersOrClients(String relation){
        InventoryDBHelper dbHelper = new InventoryDBHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<Enterprise> enterpriseList = new ArrayList<>();
        String[] selectionArgs = {mTypeOfRelationship};
        Cursor cursor = db.query(EnterpriseEntry.TABLE_NAME,
                null,
                EnterpriseEntry.RELATION_TYPE + "=?",
                selectionArgs,
                null, null, null);
        int nameColumnIndex = cursor.getColumnIndex(EnterpriseEntry.ENTERPRISE_NAME);
        int phoneColumnIndex = cursor.getColumnIndex(EnterpriseEntry.ENTERPRISE_PHONE);
        int addressColumnIndex = cursor.getColumnIndex(EnterpriseEntry.ENTERPRISE_ADDRESS);
        int emailColumnIndex = cursor.getColumnIndex(EnterpriseEntry.ENTERPRISE_EMAIL);
        int contactPersonColumnIndex = cursor.getColumnIndex(EnterpriseEntry.ENTERPRISE_CONTACT_PERSON);

        while (cursor.moveToNext()) {
            String supplierName = cursor.getString(nameColumnIndex);
            String phoneNumber = cursor.getString(phoneColumnIndex);
            String address = cursor.getString(addressColumnIndex);
            String email = cursor.getString(emailColumnIndex);
            String contactPerson = cursor.getString(contactPersonColumnIndex);

            enterpriseList.add(new Enterprise(supplierName, phoneNumber, address, email, contactPerson));
        }
        cursor.close();
        return enterpriseList;
    }
}
