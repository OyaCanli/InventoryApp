package com.example.oya.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.oya.inventoryapp.data.InventoryContract.ProductEntry;
import com.example.oya.inventoryapp.data.InventoryContract.SupplierEntry;


public class InventoryDBHelper extends SQLiteOpenHelper {

    /** Name of the database file */
    private static final String DATABASE_NAME = "inventory.db";

    private static final int DATABASE_VERSION = 1;

    public InventoryDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PRODUCTS_TABLE = "CREATE TABLE " + ProductEntry.TABLE_NAME + " ("
                + ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductEntry.PRODUCT_NAME + " TEXT NOT NULL, "
                + ProductEntry.SALE_PRICE + " REAL NOT NULL DEFAULT 0, "
                + ProductEntry.QUANTITY_IN_STOCK + " INTEGER NOT NULL DEFAULT 0, "
                + ProductEntry.SUPPLIER_NAME + " TEXT);";

        String SQL_CREATE_SUPPLIER_TABLE = "CREATE TABLE " + SupplierEntry.TABLE_NAME + " ("
                + SupplierEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SupplierEntry.SUPPLIER_NAME + " TEXT NOT NULL, "
                + SupplierEntry.SUPPLIER_PHONE + " TEXT, "
                + SupplierEntry.SUPPLIER_ADDRESS + " TEXT, "
                + SupplierEntry.SUPPLIER_EMAIL + " TEXT, "
                + SupplierEntry.SUPPLIER_CONTACT_PERSON + " TEXT);";

                db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
                db.execSQL(SQL_CREATE_SUPPLIER_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
