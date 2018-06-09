package com.example.oya.inventoryapp.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.oya.inventoryapp.data.InventoryContract;
import com.example.oya.inventoryapp.data.InventoryDBHelper;


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

    public static Cursor mergeTables(Context context, long id){
        InventoryDBHelper dbHelper = new InventoryDBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] selectionArgs = {String.valueOf(id)};
        return db.rawQuery("SELECT products.productName, products.supplierName, enterprises.enterprisePhone FROM products INNER JOIN enterprises ON (products.supplierName = enterprises.enterpriseName AND products._ID=?)", selectionArgs);
    }
}
