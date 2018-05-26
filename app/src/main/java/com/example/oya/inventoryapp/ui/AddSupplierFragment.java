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

import com.example.oya.inventoryapp.R;
import com.example.oya.inventoryapp.data.InventoryDBHelper;
import com.example.oya.inventoryapp.data.InventoryContract.SupplierEntry;

public class AddSupplierFragment extends Fragment implements View.OnClickListener{

    EditText supplierName_et;
    EditText supplierAddress_et;
    EditText supplierEmail_et;
    EditText supplierPhone_et;
    EditText supplierContactPerson_et;

    public AddSupplierFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_supplier, container, false);
        supplierName_et = rootView.findViewById(R.id.editSupplierName);
        supplierAddress_et = rootView.findViewById(R.id.editSupplierAddress);
        supplierEmail_et = rootView.findViewById(R.id.editSupplierEMail);
        supplierPhone_et = rootView.findViewById(R.id.editSupplierPhone);
        supplierContactPerson_et = rootView.findViewById(R.id.editContactPerson);
        Button save_supplier_btn = rootView.findViewById(R.id.save_supplier_btn);
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

        String supplierName = supplierName_et.getText().toString().trim();
        String supplierAddress = supplierAddress_et.getText().toString().trim();
        String supplierEmail = supplierEmail_et.getText().toString().trim();
        String supplierPhone = supplierPhone_et.getText().toString().trim();
        String contactPerson = supplierContactPerson_et.getText().toString().trim();

        ContentValues values = new ContentValues();
        values.put(SupplierEntry.SUPPLIER_NAME, supplierName);
        values.put(SupplierEntry.SUPPLIER_ADDRESS, supplierAddress);
        values.put(SupplierEntry.SUPPLIER_EMAIL, supplierEmail);
        values.put(SupplierEntry.SUPPLIER_PHONE, supplierPhone);
        values.put(SupplierEntry.SUPPLIER_CONTACT_PERSON, contactPerson);

        // Insert a new row for pet in the database, returning the ID of that new row.
        long newRowId = db.insert(SupplierEntry.TABLE_NAME, null, values);

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
