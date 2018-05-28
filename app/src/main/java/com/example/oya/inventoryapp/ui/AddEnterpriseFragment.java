package com.example.oya.inventoryapp.ui;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.oya.inventoryapp.Constants;
import com.example.oya.inventoryapp.R;
import com.example.oya.inventoryapp.data.InventoryDBHelper;
import com.example.oya.inventoryapp.data.InventoryContract.EnterpriseEntry;

public class AddEnterpriseFragment extends Fragment implements View.OnClickListener{

    private EditText enterpriseName_et;
    private EditText enterpriseAddress_et;
    private EditText enterpriseEmail_et;
    private EditText enterprisePhone_et;
    private EditText enterpriseContactPerson_et;
    private String mTypeOfRelationship;

    public AddEnterpriseFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_supplier, container, false);
        Bundle bundle = getArguments();
        mTypeOfRelationship = bundle.getString(Constants.RELATION_TYPE);
        getActivity().setTitle(getString(R.string.add_client_or_suuplier, mTypeOfRelationship));
        enterpriseName_et = rootView.findViewById(R.id.editSupplierName);
        enterpriseAddress_et = rootView.findViewById(R.id.editSupplierAddress);
        enterpriseEmail_et = rootView.findViewById(R.id.editSupplierEMail);
        enterprisePhone_et = rootView.findViewById(R.id.editSupplierPhone);
        enterpriseContactPerson_et = rootView.findViewById(R.id.editContactPerson);
        Button save_supplier_btn = rootView.findViewById(R.id.save_enterprise_btn);
        save_supplier_btn.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        saveSupplier();
    }

    private void saveSupplier(){
        InventoryDBHelper mDbHelper = new InventoryDBHelper(getActivity());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String enterpriseName = enterpriseName_et.getText().toString().trim();
        String enterpriseAddress = enterpriseAddress_et.getText().toString().trim();
        String enterpriseEmail = enterpriseEmail_et.getText().toString().trim();
        String enterprisePhone = enterprisePhone_et.getText().toString().trim();
        String contactPerson = enterpriseContactPerson_et.getText().toString().trim();

        ContentValues values = new ContentValues();
        values.put(EnterpriseEntry.ENTERPRISE_NAME, enterpriseName);
        values.put(EnterpriseEntry.ENTERPRISE_ADDRESS, enterpriseAddress);
        values.put(EnterpriseEntry.ENTERPRISE_EMAIL, enterpriseEmail);
        values.put(EnterpriseEntry.ENTERPRISE_PHONE, enterprisePhone);
        values.put(EnterpriseEntry.ENTERPRISE_CONTACT_PERSON, contactPerson);
        values.put(EnterpriseEntry.RELATION_TYPE, mTypeOfRelationship);

        // Insert a new row for pet in the database, returning the ID of that new row.
        long newRowId = db.insert(EnterpriseEntry.TABLE_NAME, null, values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newRowId == -1) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(getActivity(), "Error with saving supplier", Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(getActivity(), "Supplier saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
        }
    }
}
