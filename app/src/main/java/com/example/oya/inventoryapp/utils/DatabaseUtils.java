package com.example.oya.inventoryapp.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.oya.inventoryapp.data.InventoryContract;
import com.example.oya.inventoryapp.data.InventoryDBHelper;
import com.example.oya.inventoryapp.data.InventoryContract.EnterpriseEntry;
import com.example.oya.inventoryapp.data.InventoryContract.ProductEntry;
import com.example.oya.inventoryapp.data.InventoryContract.TransactionEntry;
import com.example.oya.inventoryapp.model.Transaction;


import java.util.ArrayList;

public final class DatabaseUtils {

    public static ArrayList<String> getEnterpriseNames(Context context, String relationshipType){
        InventoryDBHelper dbHelper = new InventoryDBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<String> enterpriseList = new ArrayList<>();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {EnterpriseEntry.ENTERPRISE_NAME};
        String[] selectionArgs = {relationshipType};

        // Perform a query on the pets table
        Cursor cursor = db.query(
                EnterpriseEntry.TABLE_NAME,   // The table to query
                projection,            // The columns to return
                EnterpriseEntry.RELATION_TYPE + "=?", // The columns for the WHERE clause
                selectionArgs,                  // The values for the WHERE clause
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // The sort order

        while (cursor.moveToNext()) {
            int supplierNameColumnIndex = cursor.getColumnIndex(EnterpriseEntry.ENTERPRISE_NAME);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            enterpriseList.add(supplierName);
        }
        cursor.close();
        return enterpriseList;
    }

    public static ArrayList<String> getProductNames(Context context){
        InventoryDBHelper dbHelper = new InventoryDBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<String> productList = new ArrayList<>();

        //I need only product name column for this list
        String[] projection = {ProductEntry.PRODUCT_NAME};

        Cursor cursor = db.query(
                ProductEntry.TABLE_NAME,   // The table to query
                projection,            // The columns to return
                null, // The columns for the WHERE clause
                null,                  // The values for the WHERE clause
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // The sort order

        while (cursor.moveToNext()) {
            int productNameColumnIndex = cursor.getColumnIndex(ProductEntry.PRODUCT_NAME);
            String productName = cursor.getString(productNameColumnIndex);
            productList.add(productName);
        }
        cursor.close();
        return productList;
    }

    public static ArrayList<Transaction> getTransactions(Context context){
        InventoryDBHelper dbHelper = new InventoryDBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<Transaction> transactionList = new ArrayList<>();

        Cursor cursor = db.query(
                TransactionEntry.TABLE_NAME,
                null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            int enterpriseColumnIndex = cursor.getColumnIndex(TransactionEntry.ENTERPRISE_NAME);
            String enterpriseName = cursor.getString(enterpriseColumnIndex);
            int productNameColumnIndex = cursor.getColumnIndex(TransactionEntry.PRODUCT_NAME);
            String productName = cursor.getString(productNameColumnIndex);
            int quantityColumnIndex = cursor.getColumnIndex(TransactionEntry.QUANTITY);
            int quantity = cursor.getInt(quantityColumnIndex);
            int priceColumnIndex = cursor.getColumnIndex(TransactionEntry.PRICE);
            float price = cursor.getFloat(priceColumnIndex);
            int dateColumnIndex = cursor.getColumnIndex(TransactionEntry.TRANSACTION_DATE);
            String date = cursor.getString(dateColumnIndex);
            int typeColumnIndex = cursor.getColumnIndex(TransactionEntry.TRANSACTION_TYPE);
            String transactionType = cursor.getString(typeColumnIndex);

            transactionList.add(new Transaction(enterpriseName, productName, quantity, price, date, transactionType));
        }
        cursor.close();
        return transactionList;
    }
}
