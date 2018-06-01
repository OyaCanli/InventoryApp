package com.example.oya.inventoryapp.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oya.inventoryapp.R;
import com.example.oya.inventoryapp.data.InventoryContract.ProductEntry;
import com.example.oya.inventoryapp.data.InventoryContract.TransactionEntry;
import com.example.oya.inventoryapp.data.InventoryDBHelper;
import com.example.oya.inventoryapp.utils.Constants;
import com.example.oya.inventoryapp.utils.DatabaseUtils;

import java.util.ArrayList;
import java.util.Calendar;

public class RealizeTransactionFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener{

    private String mTransactionType = null;
    private static TextView date_tv; //TODO: find another solution for this. Perhaps an interface?
    private ArrayList<String> enterpriseNames;
    private ArrayList<String> productNames;
    private String enterpriseChosen;
    private String productChosen;
    private EditText quantity_et;
    private EditText price_et;
    private static String mDate;
    private TextView quantity_in_stock_tv;
    private int mQuantityInStock;

    public RealizeTransactionFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_transaction, container, false);
        /*This fragment is used both for acquisitions and deliveries.
        So we need to retrieve the info about the transaction type from the bundle*/
        Bundle bundle = getArguments();
        if(bundle != null) mTransactionType = bundle.getString(Constants.TRANSACTION_TYPE);
        getActivity().setTitle(getString(R.string.new_transaction, mTransactionType));
        //Set clicklisteners to buttons
        Button changeDate_btn = rootView.findViewById(R.id.change_date_btn);
        changeDate_btn.setOnClickListener(this);
        Button saveTransaction_btn = rootView.findViewById(R.id.save_transaction_btn);
        saveTransaction_btn.setOnClickListener(this);
        //Set the date to current date by default
        date_tv = rootView.findViewById(R.id.transaction_date_tv);
        setDateToTodayByDefault();
        //If it is a acquisition we'll write supplier name, if it is a delivery we'll write client name
        TextView supplierOrClient_tv = rootView.findViewById(R.id.transaction_enterprise_tv);
        String relationshipType = null;
        if(mTransactionType.equals(Constants.ACQUISITION)){
            relationshipType = Constants.SUPPLIER;
        } else if(mTransactionType.equals(Constants.DELIVERY)){
            relationshipType = Constants.CLIENT;
        }
        supplierOrClient_tv.setText(getString(R.string.supplierOrEnterPrise, relationshipType));
        quantity_et = rootView.findViewById(R.id.transaction_quantity_et);
        price_et = rootView.findViewById(R.id.transaction_price_et);
        quantity_in_stock_tv = rootView.findViewById(R.id.transaction_quantity_in_stock);
        //Set enterprise spinner
        Spinner enterprise_spin = rootView.findViewById(R.id.enterprise_spinner);
        enterprise_spin.setOnItemSelectedListener(this);
        enterpriseNames = DatabaseUtils.getEnterpriseNames(getActivity(),relationshipType);
        ArrayAdapter<String> enterpriseSpinAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, enterpriseNames);
        enterpriseSpinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        enterprise_spin.setAdapter(enterpriseSpinAdapter);
        //Set product spinner
        Spinner product_spin = rootView.findViewById(R.id.product_spinner);
        product_spin.setOnItemSelectedListener(this);
        productNames = DatabaseUtils.getProductNames(getActivity());
        ArrayAdapter<String> productSpinAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, productNames);
        productSpinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        product_spin.setAdapter(productSpinAdapter);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.change_date_btn){
            DialogFragment newFragment = new DatePickerFragment();
            newFragment.show(getFragmentManager(), "datePicker");
        } else {
            saveTransactionToDatabase();
        }

    }

    private void setDateToTodayByDefault(){
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        mDate = "" + day + "/" + month + "/" + year;
        date_tv.setText(mDate);
    }

    @Override
    public void onItemSelected(AdapterView<?> spinner, View view, int position, long id) {
        switch(spinner.getId()){
            case R.id.enterprise_spinner:{
                enterpriseChosen = enterpriseNames.get(position);
                break;
            }
            case R.id.product_spinner:{
                productChosen = productNames.get(position);
                mQuantityInStock = getQuantityInStockOfTheChosenProduct(productChosen);
                quantity_in_stock_tv.setText(String.valueOf(mQuantityInStock));
                break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private int getQuantityInStockOfTheChosenProduct(String productName){
        InventoryDBHelper mDbHelper = new InventoryDBHelper(getActivity());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //I need only quantity column for this list
        String[] projection = {ProductEntry.QUANTITY_IN_STOCK};
        String[] selectionArgs = {productName};

        Cursor cursor = db.query(
                ProductEntry.TABLE_NAME,   // The table to query
                projection,            // The columns to return
                ProductEntry.PRODUCT_NAME + "=?", // The columns for the WHERE clause
                selectionArgs,                  // The values for the WHERE clause
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // The sort order

        cursor.moveToFirst();

        int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.QUANTITY_IN_STOCK);
        int quantityInStock = cursor.getInt(quantityColumnIndex);
        cursor.close();
        return quantityInStock;
    }

    private void saveTransactionToDatabase(){

        int quantity;
        try{
            quantity = Integer.valueOf(quantity_et.getText().toString().trim());
            if(quantity<1){
                Toast.makeText(getActivity(), R.string.quantity_should_be_positive, Toast.LENGTH_SHORT).show();
                return;
            }
        } catch(NumberFormatException nfe){
            Toast.makeText(getActivity(), R.string.quantity_should_be_positive, Toast.LENGTH_SHORT).show();
            return;
        }

        if(mTransactionType.equals(Constants.DELIVERY) && (quantity > mQuantityInStock)) {
            Toast.makeText(getActivity(), getString(R.string.stock_not_enough_warning, mQuantityInStock), Toast.LENGTH_SHORT).show();
            return;
        }

        float price;
        try{
            price = Float.valueOf(price_et.getText().toString().trim());
        } catch(NumberFormatException nfe) {
            Toast.makeText(getActivity(), R.string.price_should_not_contain_text, Toast.LENGTH_SHORT).show();
            return;
        }

        InventoryDBHelper mDbHelper = new InventoryDBHelper(getActivity());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TransactionEntry.ENTERPRISE_NAME, enterpriseChosen);
        values.put(TransactionEntry.PRODUCT_NAME, productChosen);
        values.put(TransactionEntry.QUANTITY, quantity);
        values.put(TransactionEntry.PRICE, price);
        values.put(TransactionEntry.TRANSACTION_DATE, mDate);
        values.put(TransactionEntry.TRANSACTION_TYPE, mTransactionType);

        // Insert a new row for pet in the database, returning the ID of that new row.
        long newRowId = db.insert(TransactionEntry.TABLE_NAME, null, values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newRowId == -1) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(getActivity(), "Error with saving product", Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(getActivity(), "Product saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
        }

        //Transactions should update the quantity in the products table as well.
        ContentValues valuesForUpdate = new ContentValues();
        int newQuantity;
        if(mTransactionType.equals(Constants.DELIVERY)){
            newQuantity = mQuantityInStock - quantity;
        } else{
            newQuantity = mQuantityInStock + quantity;
        }
        valuesForUpdate.put(ProductEntry.QUANTITY_IN_STOCK, newQuantity);
        String[] selectionArg = {productChosen};
        db.update(ProductEntry.TABLE_NAME, valuesForUpdate, ProductEntry.PRODUCT_NAME + "=?", selectionArg);
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            mDate = "" + dayOfMonth + "/" + (month+1) + "/" + year;
            date_tv.setText(mDate);
        }
    }
}
