package com.example.oya.inventoryapp.utils;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.example.oya.inventoryapp.data.InventoryDBHelper;
import com.example.oya.inventoryapp.model.Product;

import java.util.ArrayList;

public class InventoryViewModel extends AndroidViewModel implements MyAsyncTask.OnTaskCompleteListener{

    private LiveData<ArrayList<Product>> products;

    public InventoryViewModel(@NonNull Application application) {
        super(application);
        loadProducts();
    }

    private void loadProducts() {
        if (products == null) {
            InventoryDBHelper dbHelper = new InventoryDBHelper(getApplication());
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            MyAsyncTask mTask = new MyAsyncTask(db, this);
            mTask.execute();
        }
    }

    public LiveData<ArrayList<Product>> getProducts(){
        return products;
    }

    @Override
    public void onTaskCompleted(LiveData<ArrayList<Product>> list) {
        products = list;
    }
}
