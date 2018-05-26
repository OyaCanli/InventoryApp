package com.example.oya.inventoryapp.utils;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.example.oya.inventoryapp.data.InventoryContract.ProductEntry;
import com.example.oya.inventoryapp.model.Product;

import java.util.ArrayList;

public class MyAsyncTask extends AsyncTask<Void, Void, ArrayList<Product>> {

    private SQLiteDatabase mDb;
    private OnTaskCompleteListener mCallback;

    public MyAsyncTask(SQLiteDatabase database, OnTaskCompleteListener listener) {
        mDb = database;
        mCallback = listener;
    }

    @Override
    protected void onPostExecute(ArrayList<Product> products) {
        MutableLiveData<ArrayList<Product>> product_livedata = new MutableLiveData<>();
        product_livedata.setValue(products);
        mCallback.onTaskCompleted(product_livedata);
        super.onPostExecute(products);
    }

    @Override
    protected ArrayList<Product> doInBackground(Void... voids) {
        ArrayList<Product> productList = loadProducts();
        return productList;
    }

    private ArrayList<Product> loadProducts() {
        ArrayList<Product> productList = new ArrayList<>();
        Cursor cursor = mDb.query(ProductEntry.TABLE_NAME,
                null, null, null, null, null, null);
        int nameColumnIndex = cursor.getColumnIndex(ProductEntry.PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.SALE_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.QUANTITY_IN_STOCK);
        int supplierColumnIndex = cursor.getColumnIndex(ProductEntry.SUPPLIER_NAME);

        while (cursor.moveToNext()) {
            String productName = cursor.getString(nameColumnIndex);
            float salePrice = cursor.getFloat(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierColumnIndex);

            productList.add(new Product(productName, salePrice, quantity, supplierName));
        }
        cursor.close();
        return productList;
    }

    public interface OnTaskCompleteListener{
        void onTaskCompleted(LiveData<ArrayList<Product>> productList);
    }
}