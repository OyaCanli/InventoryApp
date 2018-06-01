package com.example.oya.inventoryapp.ui;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.oya.inventoryapp.utils.Constants;
import com.example.oya.inventoryapp.utils.DatabaseUtils;
import com.example.oya.inventoryapp.R;
import com.example.oya.inventoryapp.data.InventoryContract.ProductEntry;
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
        //Set the title that corresponds to the fragment
        getActivity().setTitle(getString(R.string.add_product));
        //Set click listeners on buttons
        Button save_product_btn = rootView.findViewById(R.id.save_btn);
        save_product_btn.setOnClickListener(this);
        Button add_supplier_btn =rootView.findViewById(R.id.add_supplier_btn);
        add_supplier_btn.setOnClickListener(this);
        //Find views
        productName_et = rootView.findViewById(R.id.editProductName);
        salePrice_et = rootView.findViewById(R.id.editSalePrice);
        quantity_et = rootView.findViewById(R.id.editQuantity);
        Spinner supplier_spin = rootView.findViewById(R.id.supplierSpinner);
        //Set the spinner which shows existing supplier names
        supplier_spin.setOnItemSelectedListener(this);
        supplierNames = DatabaseUtils.getEnterpriseNames(getActivity(), Constants.SUPPLIER);
        ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, supplierNames);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        supplier_spin.setAdapter(spinAdapter);
        return rootView;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.save_btn){
            //This button saves the new product to the database
            saveProduct();
        } else if(id == R.id.add_supplier_btn){
            //This button opens a new fragment for adding a new supplier
            AddEnterpriseFragment addSupplierFrag = new AddEnterpriseFragment();
            Bundle args = new Bundle();
            args.putString(Constants.RELATION_TYPE, Constants.SUPPLIER);
            args.putString(Constants.REQUEST_CODE, Constants.ADD_PRODUCT_FRAGMENT);
            addSupplierFrag.setArguments(args);
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, addSupplierFrag)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void saveProduct(){

        //Make sure that product name is not null
        String productName = productName_et.getText().toString().trim();
        if(TextUtils.isEmpty(productName)){
            Toast.makeText(getActivity(), R.string.product_name_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        //Make sure quantity is a positive integer
        int quantityInStock;
        try{
            quantityInStock = Integer.valueOf(quantity_et.getText().toString().trim());
            if(quantityInStock < 1){
                Toast.makeText(getActivity(), R.string.quantity_should_be_positive, Toast.LENGTH_SHORT).show();
                return;
            }
        }catch(NumberFormatException nfe){
            Toast.makeText(getActivity(), R.string.quantity_should_be_positive, Toast.LENGTH_SHORT).show();
            return;
        }

        //Sale price can be null, so not verifying this
        String salePrice = salePrice_et.getText().toString().trim();

        InventoryDBHelper mDbHelper = new InventoryDBHelper(getActivity());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

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
            Toast.makeText(getActivity(), R.string.error_saving_product, Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(getActivity(), getString(R.string.product_saved_successfully) + newRowId, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        chosenSupplierName = supplierNames.get(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(getActivity(), R.string.no_supplier_chosen, Toast.LENGTH_SHORT).show();
    }

}
