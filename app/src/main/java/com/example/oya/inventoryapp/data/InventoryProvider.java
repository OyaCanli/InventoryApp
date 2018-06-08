package com.example.oya.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.oya.inventoryapp.data.InventoryContract.EnterpriseEntry;
import com.example.oya.inventoryapp.data.InventoryContract.ProductEntry;
import com.example.oya.inventoryapp.data.InventoryContract.TransactionEntry;
import com.example.oya.inventoryapp.model.Enterprise;
import com.example.oya.inventoryapp.model.Transaction;

public class InventoryProvider extends ContentProvider {

    private static final int PRODUCTS = 100;
    private static final int PRODUCT_WITH_ID = 101;

    private static final int ENTERPRISES = 200;
    private static final int ENTERPRISES_WITH_ID = 201;

    private static final int TRANSACTIONS = 300;
    private static final int TRANSACTIONS_WITH_ID = 301;

    private InventoryDBHelper mDbHelper;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mDbHelper = new InventoryDBHelper(context);
        return true;
    }

    static {
        sUriMatcher.addURI(InventoryContract.AUTHORITY, InventoryContract.PATH_PRODUCTS, PRODUCTS);
        sUriMatcher.addURI(InventoryContract.AUTHORITY, InventoryContract.PATH_PRODUCTS + "/#", PRODUCT_WITH_ID);
        sUriMatcher.addURI(InventoryContract.AUTHORITY, InventoryContract.PATH_ENTERPRICES, ENTERPRISES);
        sUriMatcher.addURI(InventoryContract.AUTHORITY, InventoryContract.PATH_ENTERPRICES + "/#", ENTERPRISES_WITH_ID);
        sUriMatcher.addURI(InventoryContract.AUTHORITY, InventoryContract.PATH_TRANSACTIONS, TRANSACTIONS);
        sUriMatcher.addURI(InventoryContract.AUTHORITY, InventoryContract.PATH_TRANSACTIONS + "/#", TRANSACTIONS_WITH_ID);
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch(match){
            case PRODUCTS:{
                cursor = database.query(
                        ProductEntry.TABLE_NAME,   // The table to query
                        projection,            // The columns to return
                        null, // The columns for the WHERE clause
                        null,                  // The values for the WHERE clause
                        null,                  // Don't group the rows
                        null,                  // Don't filter by row groups
                        null);                   // The sort order
                break;
            }
            case PRODUCT_WITH_ID:{
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, null);
                break;
            }
            case ENTERPRISES:{
                cursor = database.query(
                        EnterpriseEntry.TABLE_NAME,   // The table to query
                        projection,            // The columns to return
                        EnterpriseEntry.RELATION_TYPE + "=?", // The columns for the WHERE clause
                        selectionArgs,                  // The values for the WHERE clause
                        null,                  // Don't group the rows
                        null,                  // Don't filter by row groups
                        null);                   // The sort order
                break;
            }
            case ENTERPRISES_WITH_ID:{
                selection = EnterpriseEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(EnterpriseEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, null);
                break;
            }
            case TRANSACTIONS:{
                cursor = database.query(
                        TransactionEntry.TABLE_NAME,
                        null, null, null, null, null, null);
                break;
            }
            case TRANSACTIONS_WITH_ID:{
                selection = TransactionEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(TransactionEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, null);
                break;
            }
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        if(cursor != null) cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_WITH_ID:
                return ProductEntry.CONTENT_ITEM_TYPE;
            case ENTERPRISES:
                return EnterpriseEntry.CONTENT_LIST_TYPE;
            case ENTERPRISES_WITH_ID:
                return EnterpriseEntry.CONTENT_ITEM_TYPE;
            case TRANSACTIONS:
                return TransactionEntry.CONTENT_LIST_TYPE;
            case TRANSACTIONS_WITH_ID:
                return TransactionEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id;
        final int match = sUriMatcher.match(uri);
        switch(match){
            case PRODUCTS:{
                id = database.insert(ProductEntry.TABLE_NAME, null, values);
                break;
            }
            case ENTERPRISES:{
                id = database.insert(EnterpriseEntry.TABLE_NAME, null, values);
                break;
            }
            case TRANSACTIONS:{
                id = database.insert(TransactionEntry.TABLE_NAME, null, values);
                break;
            }
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
        if (id == -1) {
            Log.d(LOG_TAG,"Failed to insert row for " + uri);
        }
        // Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);
        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;
        switch(match){
            case PRODUCTS:{
                rowsUpdated = database.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case PRODUCT_WITH_ID:{
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsUpdated = database.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case ENTERPRISES_WITH_ID:{
                selection = EnterpriseEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsUpdated = database.update(EnterpriseEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case TRANSACTIONS_WITH_ID:{
                selection = TransactionEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsUpdated = database.update(TransactionEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
