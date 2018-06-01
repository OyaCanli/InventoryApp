package com.example.oya.inventoryapp.ui;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.oya.inventoryapp.R;
import com.example.oya.inventoryapp.adapters.ProductAdapter;
import com.example.oya.inventoryapp.data.InventoryContract.ProductEntry;
import com.example.oya.inventoryapp.data.InventoryDBHelper;
import com.example.oya.inventoryapp.model.Product;

import java.util.ArrayList;

public class ProductListFragment extends Fragment {

    public ProductListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_listview, container, false);
        getActivity().setTitle(getString(R.string.all_products));
        //get the list of products from the database
        ArrayList<Product> mProductList = getAllProducts();
        ProductAdapter adapter = new ProductAdapter(getActivity(), mProductList);
        ListView listView = rootView.findViewById(R.id.list);
        listView.setAdapter(adapter);
        TextView empty_tv = rootView.findViewById(R.id.empty_view);
        empty_tv.setText("No products found");
        listView.setEmptyView(empty_tv);
        return rootView;
    }


    private ArrayList<Product> getAllProducts(){
        InventoryDBHelper dbHelper = new InventoryDBHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<Product> productList = new ArrayList<>();
        Cursor cursor = db.query(ProductEntry.TABLE_NAME,
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
}

