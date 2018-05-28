package com.example.oya.inventoryapp.ui;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.oya.inventoryapp.Constants;
import com.example.oya.inventoryapp.R;
import com.example.oya.inventoryapp.data.InventoryContract.ProductEntry;
import com.example.oya.inventoryapp.data.InventoryContract.EnterpriseEntry;
import com.example.oya.inventoryapp.data.InventoryDBHelper;

import java.util.ArrayList;

public class AddProductFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener{

    EditText productName_et;
    EditText salePrice_et;
    EditText quantity_et;
    ArrayList<String> supplierNames;
    String chosenSupplierName = null;

    public AddProductFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_product, container, false);
        Button save_product_btn = rootView.findViewById(R.id.save_btn);
        save_product_btn.setOnClickListener(this);
        Button add_supplier_btn =rootView.findViewById(R.id.add_supplier_btn);
        add_supplier_btn.setOnClickListener(this);
        productName_et = rootView.findViewById(R.id.editProductName);
        salePrice_et = rootView.findViewById(R.id.editSalePrice);
        quantity_et = rootView.findViewById(R.id.editQuantity);
        Spinner supplier_spin = rootView.findViewById(R.id.supplierSpinner);
        supplier_spin.setOnItemSelectedListener(this);
        supplierNames = getSupplierNames();
        ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, supplierNames);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        supplier_spin.setAdapter(spinAdapter);
        return rootView;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.save_btn){
            saveProduct();
        } else if(id == R.id.add_supplier_btn){
            AddEnterpriseFragment addSupplierFrag = new AddEnterpriseFragment();
            Bundle args = new Bundle();
            args.putString(Constants.RELATION_TYPE, Constants.SUPPLIER);
            addSupplierFrag.setArguments(args);
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, addSupplierFrag)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void saveProduct(){

        InventoryDBHelper mDbHelper = new InventoryDBHelper(getActivity());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String productName = productName_et.getText().toString().trim();
        String salePrice = salePrice_et.getText().toString().trim();
        String quantityInStock = quantity_et.getText().toString().trim();

        ContentValues values = new ContentValues();
        values.put(ProductEntry.PRODUCT_NAME, productName);
        values.put(ProductEntry.SALE_PRICE, salePrice);
        values.put(ProductEntry.QUANTITY_IN_STOCK, quantityInStock);
        values.put(ProductEntry.SUPPLIER_NAME, chosenSupplierName);

        // Insert a new row for pet in the database, returning the ID of that new row.
        long newRowId = db.insert(ProductEntry.TABLE_NAME, null, values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newRowId == -1) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(getActivity(), "Error with saving product", Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(getActivity(), "Product saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        chosenSupplierName = supplierNames.get(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(getActivity(), "You didn't chose a supplier", Toast.LENGTH_SHORT).show();
    }

    private ArrayList<String> getSupplierNames(){
        InventoryDBHelper dbHelper = new InventoryDBHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<String> supplierList = new ArrayList<>();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {EnterpriseEntry.ENTERPRISE_NAME};
        String[] selectionArgs = {Constants.SUPPLIER};

        // Perform a query on the pets table
        Cursor cursor = db.query(
                EnterpriseEntry.TABLE_NAME,   // The table to query
                projection,            // The columns to return
                EnterpriseEntry.RELATION_TYPE + "=?",                  // The columns for the WHERE clause
                selectionArgs,                  // The values for the WHERE clause
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // The sort order

        while (cursor.moveToNext()) {
            int supplierNameColumnIndex = cursor.getColumnIndex(EnterpriseEntry.ENTERPRISE_NAME);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            supplierList.add(supplierName);
        }
        return supplierList;
    }
}
