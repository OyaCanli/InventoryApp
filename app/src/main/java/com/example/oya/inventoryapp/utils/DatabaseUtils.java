package com.example.oya.inventoryapp.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.oya.inventoryapp.data.InventoryContract;
import com.example.oya.inventoryapp.data.InventoryDBHelper;
import com.example.oya.inventoryapp.data.InventoryContract.EnterpriseEntry;
import com.example.oya.inventoryapp.data.InventoryContract.ProductEntry;
import com.example.oya.inventoryapp.data.InventoryContract.TransactionEntry;
import com.example.oya.inventoryapp.model.Product;
import com.example.oya.inventoryapp.model.Transaction;


import java.util.ArrayList;

public final class DatabaseUtils {

    public static ArrayList<String> getEnterpriseNames(Context context, String relationshipType){
        ArrayList<String> enterpriseList = new ArrayList<>();
        String[] projection = {InventoryContract.EnterpriseEntry.ENTERPRISE_NAME};
        String[] selectionArgs = {relationshipType};
        Cursor cursor = context.getContentResolver().query(InventoryContract.EnterpriseEntry.CONTENT_URI,
                projection,
                InventoryContract.EnterpriseEntry.RELATION_TYPE + "=?",
                selectionArgs,
                null);
        while (cursor.moveToNext()) {
            int supplierNameColumnIndex = cursor.getColumnIndex(InventoryContract.EnterpriseEntry.ENTERPRISE_NAME);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            enterpriseList.add(supplierName);
        }
        cursor.close();
        return enterpriseList;
    }
}
