package com.example.oya.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.oya.inventoryapp.data.InventoryContract.ProductEntry;
import com.example.oya.inventoryapp.data.InventoryContract.EnterpriseEntry;
import com.example.oya.inventoryapp.data.InventoryContract.TransactionEntry;


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
                + ProductEntry.SALE_PRICE + " REAL DEFAULT 0, "
                + ProductEntry.QUANTITY_IN_STOCK + " INTEGER NOT NULL DEFAULT 0, "
                + ProductEntry.IMAGE_FILE_PATH + " TEXT, "
                + ProductEntry.SUPPLIER_NAME + " TEXT);";

        String SQL_CREATE_ENTERPRISE_TABLE = "CREATE TABLE " + EnterpriseEntry.TABLE_NAME + " ("
                + EnterpriseEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + EnterpriseEntry.ENTERPRISE_NAME + " TEXT NOT NULL, "
                + EnterpriseEntry.ENTERPRISE_PHONE + " TEXT, "
                + EnterpriseEntry.ENTERPRISE_ADDRESS + " TEXT, "
                + EnterpriseEntry.ENTERPRISE_EMAIL + " TEXT, "
                + EnterpriseEntry.ENTERPRISE_CONTACT_PERSON + " TEXT, "
                + EnterpriseEntry.RELATION_TYPE + " TEXT);";

        String SQL_CREATE_TRANSACTIONS_TABLE = "CREATE TABLE " + TransactionEntry.TABLE_NAME + " ("
                + TransactionEntry._ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TransactionEntry.ENTERPRISE_NAME + " TEXT NOT NULL, "
                + TransactionEntry.PRODUCT_NAME + " TEXT NOT NULL, "
                + TransactionEntry.QUANTITY + " INTEGER NOT NULL, "
                + TransactionEntry.PRICE + " REAL, "
                + TransactionEntry.TRANSACTION_DATE + " TEXT, "
                + TransactionEntry.TRANSACTION_TYPE + " TEXT);";

                db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
                db.execSQL(SQL_CREATE_ENTERPRISE_TABLE);
                db.execSQL(SQL_CREATE_TRANSACTIONS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
