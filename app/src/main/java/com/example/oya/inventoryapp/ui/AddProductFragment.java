package com.example.oya.inventoryapp.ui;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.oya.inventoryapp.data.InventoryContract;
import com.example.oya.inventoryapp.utils.BitmapUtils;
import com.example.oya.inventoryapp.utils.Constants;
import com.example.oya.inventoryapp.utils.DatabaseUtils;
import com.example.oya.inventoryapp.R;
import com.example.oya.inventoryapp.data.InventoryContract.ProductEntry;
import com.example.oya.inventoryapp.data.InventoryDBHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class AddProductFragment extends Fragment implements View.OnClickListener,
        AdapterView.OnItemSelectedListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private EditText productName_et;
    private EditText salePrice_et;
    private EditText quantity_et;
    private ArrayList<String> supplierNames;
    private String chosenSupplierName = null;
    private Uri mCurrentProductUri;
    private Spinner mSupplierSpin;
    private ArrayAdapter<String> mSpinAdapter;
    ImageView productImage;
    AlertDialog pickImageDialog;
    String mTempPhotoPath;

    public AddProductFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_product, container, false);

        //Find views
        productName_et = rootView.findViewById(R.id.editProductName);
        salePrice_et = rootView.findViewById(R.id.editSalePrice);
        quantity_et = rootView.findViewById(R.id.editQuantity);
        mSupplierSpin = rootView.findViewById(R.id.supplierSpinner);
        Button save_product_btn = rootView.findViewById(R.id.save_btn);
        Button add_supplier_btn = rootView.findViewById(R.id.add_supplier_btn);
        productImage = rootView.findViewById(R.id.product_details_gallery_image);

        //Set click listeners on buttons
        save_product_btn.setOnClickListener(this);
        add_supplier_btn.setOnClickListener(this);
        productImage.setOnClickListener(this);

        //Set the spinner which shows existing supplier names
        mSupplierSpin.setOnItemSelectedListener(this);
        supplierNames = DatabaseUtils.getEnterpriseNames(getActivity(), Constants.SUPPLIER);
        mSpinAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, supplierNames);
        mSpinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSupplierSpin.setAdapter(mSpinAdapter);

        Bundle bundle = getArguments();
        String uriString = null;
        if (bundle != null) uriString = bundle.getString(Constants.PRODUCT_URI);
        if (uriString != null) mCurrentProductUri = Uri.parse(uriString);

        //Set the title that corresponds to the fragment
        if (mCurrentProductUri == null) {
            getActivity().setTitle(getString(R.string.add_product));
        } else {
            getActivity().setTitle(getString(R.string.edit_product));
            getLoaderManager().initLoader(Constants.SINGLE_PRODUCT_LOADER, null, this);
        }

        return rootView;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.save_btn: {
                //This button saves the new product to the database
                saveProduct();
                break;
            }
            case R.id.add_supplier_btn: {
                //This button opens a new fragment for adding a new supplier
                AddEnterpriseFragment addSupplierFrag = new AddEnterpriseFragment();
                Bundle args = new Bundle();
                args.putString(Constants.RELATION_TYPE, Constants.SUPPLIER);
                args.putString(Constants.REQUEST_CODE, Constants.ADD_PRODUCT_FRAGMENT);
                addSupplierFrag.setArguments(args);
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, addSupplierFrag)
                        .addToBackStack(null)
                        .commit();
                break;
            }
            case R.id.product_details_gallery_image: {
                openImageDialog();
                break;
            }
        }
    }

    private void openImageDialog() {
        String[] dialogOptions = getActivity().getResources().getStringArray(R.array.dialog_options);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add image from: ");
        builder.setSingleChoiceItems(dialogOptions, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:{
                        if (ContextCompat.checkSelfPermission(getActivity(),
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            // If you do not have permission, request it
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    Constants.REQUEST_STORAGE_PERMISSION);
                        } else {
                            // Launch the camera if the permission exists
                            openCamera();
                        }
                        break;
                    }
                    case 1:
                        // Your code when 2nd  option seletced
                        break;
                }
            }
        });
        pickImageDialog = builder.create();
        pickImageDialog.show();
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = BitmapUtils.createImageFile(getActivity());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                // Get the path of the temporary file
                mTempPhotoPath = photoFile.getAbsolutePath();

                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        Constants.FILE_PROVIDER_AUTHORITY,
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, Constants.REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        pickImageDialog.dismiss();
        if (requestCode == Constants.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Glide.with(getActivity())
                    .load(mTempPhotoPath)
                    .into(productImage);
        } else {
            BitmapUtils.deleteImageFile(getActivity(), mTempPhotoPath);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // Called when you request permission to read and write to external storage
        switch (requestCode) {
            case Constants.REQUEST_STORAGE_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // If you get permission, launch the camera
                    openCamera();
                } else {
                    // If you do not get permission, show a Toast
                    Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    private void saveProduct() {
        //Make sure that product name is not null
        String productName = productName_et.getText().toString().trim();
        if (TextUtils.isEmpty(productName)) {
            Toast.makeText(getActivity(), R.string.product_name_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        //Make sure quantity is a positive integer
        int quantityInStock;
        try {
            quantityInStock = Integer.valueOf(quantity_et.getText().toString().trim());
            if (quantityInStock < 1) {
                Toast.makeText(getActivity(), R.string.quantity_should_be_positive, Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException nfe) {
            Toast.makeText(getActivity(), R.string.quantity_should_be_positive, Toast.LENGTH_SHORT).show();
            return;
        }

        //Sale price can be null, so not verifying this
        String salePrice = salePrice_et.getText().toString().trim();

        ContentValues values = new ContentValues();
        values.put(ProductEntry.PRODUCT_NAME, productName);
        values.put(ProductEntry.SALE_PRICE, salePrice);
        values.put(ProductEntry.QUANTITY_IN_STOCK, quantityInStock);
        values.put(ProductEntry.SUPPLIER_NAME, chosenSupplierName);
        values.put(ProductEntry.IMAGE_FILE_PATH, mTempPhotoPath);

        if (mCurrentProductUri == null) {
            //This is new product entry
            Uri newUri = getActivity().getContentResolver().insert(ProductEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(getActivity(), R.string.error_saving_product, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), getString(R.string.product_saved_successfully), Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an existing product, so update the product
            int rowsAffected = getActivity().getContentResolver().update(mCurrentProductUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(getActivity(), R.string.error_saving_product, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), getString(R.string.product_saved_successfully), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        chosenSupplierName = supplierNames.get(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(getActivity(), R.string.no_supplier_chosen, Toast.LENGTH_SHORT).show();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(getActivity(), mCurrentProductUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int productNameColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.PRODUCT_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.QUANTITY_IN_STOCK);
            int priceColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.SALE_PRICE);
            int supplierColumnIndex = cursor.getColumnIndex(ProductEntry.SUPPLIER_NAME);

            String productName = cursor.getString(productNameColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            float price = cursor.getFloat(priceColumnIndex);
            String supplierName = cursor.getString(supplierColumnIndex);

            productName_et.setText(productName);
            salePrice_et.setText(String.valueOf(price));
            quantity_et.setText(String.valueOf(quantity));
            mSupplierSpin.setSelection(mSpinAdapter.getPosition(supplierName));
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
