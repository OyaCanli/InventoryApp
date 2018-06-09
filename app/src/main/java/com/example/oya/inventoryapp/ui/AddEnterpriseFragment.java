package com.example.oya.inventoryapp.ui;

import android.content.ContentValues;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.oya.inventoryapp.data.InventoryContract;
import com.example.oya.inventoryapp.utils.Constants;
import com.example.oya.inventoryapp.R;
import com.example.oya.inventoryapp.data.InventoryDBHelper;
import com.example.oya.inventoryapp.data.InventoryContract.EnterpriseEntry;

public class AddEnterpriseFragment extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor>{

    private EditText enterpriseName_et;
    private EditText enterpriseAddress_et;
    private EditText enterpriseEmail_et;
    private EditText enterprisePhone_et;
    private EditText enterpriseContactPerson_et;
    private String mTypeOfRelationship;
    private boolean mUserIsAddingAProduct;
    private Uri mCurrentEnterpriseUri;

    public AddEnterpriseFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_enterpriser, container, false);
        Bundle bundle = getArguments();
        mTypeOfRelationship = bundle.getString(Constants.RELATION_TYPE);
        if(bundle.containsKey(Constants.REQUEST_CODE)){
            mUserIsAddingAProduct = true;
        }
        String uriString = null;
        if(bundle.containsKey(Constants.ENTERPRISE_URI)){
            uriString = bundle.getString(Constants.ENTERPRISE_URI);
        }
        if(uriString != null){
            mCurrentEnterpriseUri = Uri.parse(uriString);
        }

        //Set the title that corresponds to the fragment
        if(mCurrentEnterpriseUri == null){
            getActivity().setTitle(getString(R.string.add_client_or_suuplier, mTypeOfRelationship));
        } else {
            getActivity().setTitle(getString(R.string.edit_client_or_suuplier, mTypeOfRelationship));
            getLoaderManager().initLoader(Constants.SINGLE_ENTERPRISE_LOADER, null, this);
        }

        enterpriseName_et = rootView.findViewById(R.id.editSupplierName);
        enterpriseAddress_et = rootView.findViewById(R.id.editSupplierAddress);
        enterpriseEmail_et = rootView.findViewById(R.id.editSupplierEMail);
        enterprisePhone_et = rootView.findViewById(R.id.editSupplierPhone);
        enterpriseContactPerson_et = rootView.findViewById(R.id.editContactPerson);
        Button save_supplier_btn = rootView.findViewById(R.id.save_enterprise_btn);
        save_supplier_btn.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        saveSupplier();
        if(mUserIsAddingAProduct){
            getActivity().onBackPressed();
        }
    }

    private void saveSupplier(){

        String enterpriseName = enterpriseName_et.getText().toString().trim();
        String enterpriseAddress = enterpriseAddress_et.getText().toString().trim();
        String enterpriseEmail = enterpriseEmail_et.getText().toString().trim();
        String enterprisePhone = enterprisePhone_et.getText().toString().trim();
        String contactPerson = enterpriseContactPerson_et.getText().toString().trim();

        ContentValues values = new ContentValues();
        values.put(EnterpriseEntry.ENTERPRISE_NAME, enterpriseName);
        values.put(EnterpriseEntry.ENTERPRISE_ADDRESS, enterpriseAddress);
        values.put(EnterpriseEntry.ENTERPRISE_EMAIL, enterpriseEmail);
        values.put(EnterpriseEntry.ENTERPRISE_PHONE, enterprisePhone);
        values.put(EnterpriseEntry.ENTERPRISE_CONTACT_PERSON, contactPerson);
        values.put(EnterpriseEntry.RELATION_TYPE, mTypeOfRelationship);

        if (mCurrentEnterpriseUri == null) {
            //This is a new supplier or client
            Uri newUri = getActivity().getContentResolver().insert(EnterpriseEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(getActivity(), R.string.error_saving, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), R.string.enterprise_successfully_saved, Toast.LENGTH_SHORT).show();
            }
        } else{
            // Otherwise this is an existing enterprise, so update the entry
            int rowsAffected = getActivity().getContentResolver().update(mCurrentEnterpriseUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(getActivity(), R.string.error_updating, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), R.string.successfully_updated, Toast.LENGTH_SHORT).show();
            }
        }

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(getActivity(), mCurrentEnterpriseUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int enterpriseNameColumnIndex = cursor.getColumnIndex(InventoryContract.EnterpriseEntry.ENTERPRISE_NAME);
            int contactPersonColumnIndex = cursor.getColumnIndex(InventoryContract.EnterpriseEntry.ENTERPRISE_CONTACT_PERSON);
            int phoneColumnIndex = cursor.getColumnIndex(InventoryContract.EnterpriseEntry.ENTERPRISE_PHONE);
            int addressColumnIndex = cursor.getColumnIndex(EnterpriseEntry.ENTERPRISE_ADDRESS);
            int eMailColumnIndex = cursor.getColumnIndex(EnterpriseEntry.ENTERPRISE_EMAIL);

            String enterpriseName = cursor.getString(enterpriseNameColumnIndex);
            String contactPerson = cursor.getString(contactPersonColumnIndex);
            final String phone = cursor.getString(phoneColumnIndex);
            String address = cursor.getString(addressColumnIndex);
            String eMail = cursor.getString(eMailColumnIndex);

            enterpriseName_et.setText(enterpriseName);
            enterpriseAddress_et.setText(address);
            enterpriseEmail_et.setText(eMail);
            enterprisePhone_et.setText(phone);
            enterpriseContactPerson_et.setText(contactPerson);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
