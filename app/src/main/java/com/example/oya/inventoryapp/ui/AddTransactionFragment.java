package com.example.oya.inventoryapp.ui;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.Slide;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oya.inventoryapp.R;
import com.example.oya.inventoryapp.data.InventoryContract;
import com.example.oya.inventoryapp.data.InventoryContract.ProductEntry;
import com.example.oya.inventoryapp.data.InventoryContract.TransactionEntry;
import com.example.oya.inventoryapp.utils.Constants;
import com.example.oya.inventoryapp.utils.DatabaseUtils;

import java.util.ArrayList;
import java.util.Calendar;

public class AddTransactionFragment extends Fragment implements View.OnClickListener,
        AdapterView.OnItemSelectedListener, DatePickerFragment.MyOnDateSetListener {

    private String mTransactionType = null;
    private TextView date_tv;
    private ArrayList<String> enterpriseNames;
    private ArrayList<String> productNames;
    private final ArrayList<Integer> quantitiesList = new ArrayList<>();
    private String enterpriseChosen;
    private String productChosen;
    private EditText quantity_et;
    private EditText price_et;
    private static String mDate;
    private TextView quantity_in_stock_tv;
    private int mQuantityInStock;
    private String mRelationshipType = null;
    private static final String TAG = "AddTransactionFragment";

    public AddTransactionFragment() {
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_transaction, container, false);
        setHasOptionsMenu(true);
        /*This fragment is used both for acquisitions and deliveries.
        So we need to retrieve the info about the transaction type from the bundle*/
        Bundle bundle = getArguments();
        if (bundle != null) {
            mTransactionType = bundle.getString(Constants.TRANSACTION_TYPE);
        }

        //Set the corresponding title: New Acquisition or New Delivery
        getActivity().setTitle(getString(R.string.new_transaction, mTransactionType));

        //Set clickListeners to buttons
        TextView changeDate_btn = rootView.findViewById(R.id.change_date_btn);
        changeDate_btn.setOnClickListener(this);
        ImageButton addEnterprise_btn = rootView.findViewById(R.id.add_enterprise_btn);
        addEnterprise_btn.setOnClickListener(this);

        //Set the date to current date by default
        date_tv = rootView.findViewById(R.id.transaction_date_tv);
        setDateToTodayByDefault();

        //If it is a acquisition we'll write supplier name, if it is a delivery we'll write client name
        TextView supplierOrClient_tv = rootView.findViewById(R.id.transaction_enterprise_tv);
        if (mTransactionType.equals(Constants.ACQUISITION)) {
            mRelationshipType = Constants.SUPPLIER;
        } else if (mTransactionType.equals(Constants.DELIVERY)) {
            mRelationshipType = Constants.CLIENT;
        }
        supplierOrClient_tv.setText(getString(R.string.supplierOrEnterPrise, mRelationshipType));

        //Set some views
        quantity_et = rootView.findViewById(R.id.transaction_quantity_et);
        price_et = rootView.findViewById(R.id.transaction_price_et);
        quantity_in_stock_tv = rootView.findViewById(R.id.transaction_quantity_in_stock);

        //Set enterprise spinner
        Spinner enterprise_spin = rootView.findViewById(R.id.enterprise_spinner);
        enterprise_spin.setOnItemSelectedListener(this);
        enterpriseNames = DatabaseUtils.getEnterpriseNames(getActivity(), mRelationshipType);
        ArrayAdapter<String> enterpriseSpinAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, enterpriseNames);
        enterpriseSpinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        enterprise_spin.setAdapter(enterpriseSpinAdapter);

        //Set product spinner
        Spinner product_spin = rootView.findViewById(R.id.product_spinner);
        product_spin.setOnItemSelectedListener(this);
        productNames = getProductNames();
        ArrayAdapter<String> productSpinAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, productNames);
        productSpinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        product_spin.setAdapter(productSpinAdapter);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.change_date_btn: {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getChildFragmentManager(), "datePicker");
                break;
            }
            case R.id.add_enterprise_btn: {
                AddEnterpriseFragment addSupplierFrag = new AddEnterpriseFragment();
                addSupplierFrag.setEnterTransition(new Slide(Gravity.END));
                addSupplierFrag.setExitTransition(new Slide(Gravity.START));
                Bundle args = new Bundle();
                args.putString(Constants.RELATION_TYPE, mRelationshipType);
                args.putString(Constants.REQUEST_CODE, Constants.ADD_TRANSACTION_FRAGMENT);
                addSupplierFrag.setArguments(args);
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, addSupplierFrag)
                        .addToBackStack(null)
                        .commit();
                break;
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_with_save, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_save: {
                saveTransactionToDatabase();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private ArrayList<String> getProductNames() {
        ArrayList<String> productList = new ArrayList<>();
        //I need only product name column for this list
        String[] projection = {ProductEntry.PRODUCT_NAME, ProductEntry.QUANTITY_IN_STOCK};
        Cursor cursor = getActivity().getContentResolver().query(InventoryContract.ProductEntry.CONTENT_URI, projection, null, null, null);

        int productNameColumnIndex = cursor.getColumnIndex(ProductEntry.PRODUCT_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.QUANTITY_IN_STOCK);

        while (cursor.moveToNext()) {
            //Fill productList array with product names
            String productName = cursor.getString(productNameColumnIndex);
            productList.add(productName);
            //Fill quantitiesOfAllProducts list with the quantities of each product
            int quantityInStock = cursor.getInt(quantityColumnIndex);
            quantitiesList.add(quantityInStock);
        }
        cursor.close();
        return productList;
    }

    private void setDateToTodayByDefault() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        mDate = "" + day + "/" + month + "/" + year;
        date_tv.setText(mDate);
    }

    @Override
    public void onItemSelected(AdapterView<?> spinner, View view, int position, long id) {
        switch (spinner.getId()) {
            case R.id.enterprise_spinner: {
                enterpriseChosen = enterpriseNames.get(position);
                break;
            }
            case R.id.product_spinner: {
                productChosen = productNames.get(position);
                mQuantityInStock = quantitiesList.get(position);
                quantity_in_stock_tv.setText(String.valueOf(mQuantityInStock));
                break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void saveTransactionToDatabase() {
        //Check the validity of quantity
        int quantity;
        try {
            quantity = Integer.valueOf(quantity_et.getText().toString().trim());
            if (quantity < 1) {
                Toast.makeText(getActivity(), R.string.quantity_should_be_positive, Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException nfe) {
            Toast.makeText(getActivity(), R.string.quantity_should_be_positive, Toast.LENGTH_SHORT).show();
            return;
        }
        //If this is a delivery, quantity to be delivered cannot be more than the quantity in stock
        if (mTransactionType.equals(Constants.DELIVERY) && (quantity > mQuantityInStock)) {
            Toast.makeText(getActivity(), getString(R.string.stock_not_enough_warning, mQuantityInStock), Toast.LENGTH_SHORT).show();
            return;
        }
        //Check the validity of price
        float price;
        try {
            price = Float.valueOf(price_et.getText().toString().trim());
        } catch (NumberFormatException nfe) {
            Toast.makeText(getActivity(), R.string.price_should_not_contain_text, Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(TransactionEntry.ENTERPRISE_NAME, enterpriseChosen);
        values.put(TransactionEntry.PRODUCT_NAME, productChosen);
        values.put(TransactionEntry.QUANTITY, quantity);
        values.put(TransactionEntry.PRICE, price);
        values.put(TransactionEntry.TRANSACTION_DATE, mDate);
        values.put(TransactionEntry.TRANSACTION_TYPE, mTransactionType);

        //insert the transaction to database with the help of content provider
        Uri newUri = getActivity().getContentResolver().insert(TransactionEntry.CONTENT_URI, values);
        if (newUri == null) {
            Toast.makeText(getActivity(), "Error saving transaction", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Transaction report saved successfully", Toast.LENGTH_SHORT).show();
        }

        //Transactions should update the quantity in the products table as well.
        ContentValues valuesForUpdate = new ContentValues();
        int newQuantity;
        if (mTransactionType.equals(Constants.DELIVERY)) {
            newQuantity = mQuantityInStock - quantity;
        } else {
            newQuantity = mQuantityInStock + quantity;
        }
        //Update the quantity textview in the current fragment
        quantity_in_stock_tv.setText(String.valueOf(newQuantity));
        //Update the quantity field of that product in the products table
        valuesForUpdate.put(ProductEntry.QUANTITY_IN_STOCK, newQuantity);
        String[] selectionArg = {productChosen};

        int rowsAffected = getActivity().getContentResolver().update(ProductEntry.CONTENT_URI, valuesForUpdate, ProductEntry.PRODUCT_NAME + "=?", selectionArg);
        if (rowsAffected == 0) {
            // If no rows were affected, then there was an error with the update.
            Log.d(TAG, "Error updating quantity");
        } else {
            // Otherwise, the update was successful
            Log.d(TAG, "Quantity updated successfully");
        }

        //Once saved, close AddTransaction fragment and open TransactionListFragment
        TransactionListFragment transactionListFrag = new TransactionListFragment();
        transactionListFrag.setEnterTransition(new Slide(Gravity.END));
        transactionListFrag.setExitTransition(new Slide(Gravity.START));
        getFragmentManager().beginTransaction()
                .replace(R.id.container, transactionListFrag)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void myOnDateChanged(String newDate) {
        mDate = newDate;
        date_tv.setText(newDate);
    }

}
