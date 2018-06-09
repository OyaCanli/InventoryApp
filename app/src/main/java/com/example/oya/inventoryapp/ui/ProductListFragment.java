package com.example.oya.inventoryapp.ui;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.oya.inventoryapp.R;
import com.example.oya.inventoryapp.adapters.ProductCursorAdapter;
import com.example.oya.inventoryapp.data.InventoryContract.ProductEntry;
import com.example.oya.inventoryapp.utils.Constants;

public class ProductListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>, ProductCursorAdapter.SaleOrderButtonsClickListener{

    private ProductCursorAdapter mCursorAdapter;

    public ProductListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_listview, container, false);
        getActivity().setTitle(getString(R.string.all_products));

        mCursorAdapter = new ProductCursorAdapter(getActivity(), null, this);
        ListView listView = rootView.findViewById(R.id.list);
        listView.setAdapter(mCursorAdapter);
        TextView empty_tv = rootView.findViewById(R.id.empty_view);
        empty_tv.setText(R.string.no_products_found);
        listView.setEmptyView(empty_tv);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AddProductFragment addProductFrag = new AddProductFragment();
                Bundle args = new Bundle();
                Uri currentProductUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);
                args.putString(Constants.PRODUCT_URI, currentProductUri.toString());
                addProductFrag.setArguments(args);
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, addProductFrag)
                        .addToBackStack(null)
                        .commit();
            }
        });
        getLoaderManager().initLoader(Constants.PRODUCT_LOADER_ID, null, this);
        return rootView;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                ProductEntry._ID, ProductEntry.PRODUCT_NAME, ProductEntry.QUANTITY_IN_STOCK, ProductEntry.SALE_PRICE, ProductEntry.IMAGE_FILE_PATH};
        return new CursorLoader(getActivity(), ProductEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    @Override
    public void onSaleOrOrderButtonClicked(View v, long id, String productName) {
        Bundle args = new Bundle();
        if(v.getId() == R.id.product_item_sale_btn){
            args.putString(Constants.TRANSACTION_TYPE, Constants.DELIVERY);
            args.putString(Constants.PRODUCT_NAME, productName);
        } else {
            args.putString(Constants.TRANSACTION_TYPE, Constants.ACQUISITION);
            args.putLong(Constants.PRODUCT_ID, id);
        }
        AddTransactionFragment transactionFrag = new AddTransactionFragment();
        transactionFrag.setArguments(args);
        getFragmentManager().beginTransaction()
                .replace(R.id.container, transactionFrag)
                .addToBackStack(null)
                .commit();
    }
}

