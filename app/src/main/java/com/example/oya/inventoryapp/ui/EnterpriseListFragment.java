package com.example.oya.inventoryapp.ui;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.transition.Slide;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.oya.inventoryapp.data.InventoryContract;
import com.example.oya.inventoryapp.utils.Constants;
import com.example.oya.inventoryapp.R;
import com.example.oya.inventoryapp.adapters.EnterpriseCursorAdapter;
import com.example.oya.inventoryapp.data.InventoryContract.EnterpriseEntry;

public class EnterpriseListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>, EnterpriseCursorAdapter.ItemClickListener{

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

        mCursorAdapter = new EnterpriseCursorAdapter(getActivity(), null, this);
        ListView listView = rootView.findViewById(R.id.list);
        listView.setAdapter(mCursorAdapter);
        ConstraintLayout empty_screen = rootView.findViewById(R.id.empty_view);
        TextView empty_tv = rootView.findViewById(R.id.empty_text);
        empty_tv.setText(getString(R.string.no_enterprise_found, mTypeOfRelationship));
        listView.setEmptyView(empty_screen);
        getLoaderManager().initLoader(Constants.ENTERPRISE_LOADER_ID, null, this);
        return rootView;
    }

    @Override
    public void onItemClicked(long id) {
        AddEnterpriseFragment addSupplierFrag = new AddEnterpriseFragment();
        addSupplierFrag.setEnterTransition(new Slide(Gravity.END));
        addSupplierFrag.setExitTransition(new Slide(Gravity.START));
        Bundle args = new Bundle();
        args.putString(Constants.RELATION_TYPE, mTypeOfRelationship);
        Uri currentEnterpriseUri = ContentUris.withAppendedId(InventoryContract.EnterpriseEntry.CONTENT_URI, id);
        args.putString(Constants.ENTERPRISE_URI, currentEnterpriseUri.toString());
        addSupplierFrag.setArguments(args);
        getFragmentManager().beginTransaction()
                .replace(R.id.container, addSupplierFrag)
                .addToBackStack(null)
                .commit();
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
