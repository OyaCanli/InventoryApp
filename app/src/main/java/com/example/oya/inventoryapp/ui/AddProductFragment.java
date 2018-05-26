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
import com.example.oya.inventoryapp.data.InventoryContract.ProductEntry;
import com.example.oya.inventoryapp.data.InventoryDBHelper;

public class AddProductFragment extends Fragment implements View.OnClickListener{

    EditText productName_et;
    EditText salePrice_et;
    EditText quantity_et;
    EditText supplier_et;

    public AddProductFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_product, container, false);
        Button save_product_btn = rootView.findViewById(R.id.save_btn);
        save_product_btn.setOnClickListener(this);
        productName_et = rootView.findViewById(R.id.editSupplierName);
        salePrice_et = rootView.findViewById(R.id.editSupplierAddress);
        quantity_et = rootView.findViewById(R.id.editSupplierEMail);
        supplier_et = rootView.findViewById(R.id.editSupplierPhone);
        return rootView;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.save_btn){
            saveProduct();
        }
    }

    private void saveProduct(){

        InventoryDBHelper mDbHelper = new InventoryDBHelper(getActivity());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String productName = productName_et.getText().toString().trim();
        String salePrice = salePrice_et.getText().toString().trim();
        String quantityInStock = quantity_et.getText().toString().trim();
        String supplierName = supplier_et.getText().toString().trim();

        ContentValues values = new ContentValues();
        values.put(ProductEntry.PRODUCT_NAME, productName);
        values.put(ProductEntry.SALE_PRICE, salePrice);
        values.put(ProductEntry.QUANTITY_IN_STOCK, quantityInStock);
        values.put(ProductEntry.SUPPLIER_NAME, supplierName);

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
}
