package com.example.oya.inventoryapp.ui;

import android.content.ContentUris;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import com.example.oya.inventoryapp.data.InventoryContract;
import com.example.oya.inventoryapp.utils.Constants;
import com.example.oya.inventoryapp.R;
import com.example.oya.inventoryapp.adapters.EnterpriseCursorAdapter;
import com.example.oya.inventoryapp.data.InventoryContract.EnterpriseEntry;
import com.example.oya.inventoryapp.data.InventoryDBHelper;
import com.example.oya.inventoryapp.model.Enterprise;

import java.util.ArrayList;

public class EnterpriseListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>{

    private String mTypeOfRelationship;
    private EnterpriseCursorAdapter mCursorAdapter;

    public EnterpriseListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_listview, container, false);
        Bundle bundle = getArguments();
        mTypeOfRelationship = bundle.getString(Constants.RELATION_TYPE);
        getActivity().setTitle(getString(R.string.all_enterprises, mTypeOfRelationship));
        //get the list of suppliers from the database

        mCursorAdapter = new EnterpriseCursorAdapter(getActivity(), null);
        ListView listView = rootView.findViewById(R.id.list);
        listView.setAdapter(mCursorAdapter);
        TextView empty_tv = rootView.findViewById(R.id.empty_view);
        empty_tv.setText(getString(R.string.no_enterprise_found, mTypeOfRelationship));
        listView.setEmptyView(empty_tv);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO: check the problem with this
                AddEnterpriseFragment addSupplierFrag = new AddEnterpriseFragment();
                Bundle args = new Bundle();
                args.putString(Constants.RELATION_TYPE, mTypeOfRelationship);
                Uri currentEnterpriseUri = ContentUris.withAppendedId(InventoryContract.ProductEntry.CONTENT_URI, id);
                args.putString(Constants.ENTERPRISE_URI, currentEnterpriseUri.toString());
                addSupplierFrag.setArguments(args);
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, addSupplierFrag)
                        .addToBackStack(null)
                        .commit();
            }
        });
        getLoaderManager().initLoader(Constants.ENTERPRISE_LOADER_ID, null, this);
        return rootView;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {EnterpriseEntry._ID, EnterpriseEntry.ENTERPRISE_NAME, EnterpriseEntry.ENTERPRISE_CONTACT_PERSON, EnterpriseEntry.ENTERPRISE_PHONE};
        String[] selectionArgs = {mTypeOfRelationship};
        return new CursorLoader(getActivity(), EnterpriseEntry.CONTENT_URI,
                projection,
                EnterpriseEntry.RELATION_TYPE + "=?",
                selectionArgs, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
