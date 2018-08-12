package com.example.oya.inventoryapp.ui;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.Slide;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.oya.inventoryapp.R;
import com.example.oya.inventoryapp.data.InventoryContract;
import com.example.oya.inventoryapp.data.InventoryContract.ProductEntry;
import com.example.oya.inventoryapp.utils.BitmapUtils;
import com.example.oya.inventoryapp.utils.Constants;
import com.example.oya.inventoryapp.utils.DatabaseUtils;
import com.example.oya.inventoryapp.utils.GlideApp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

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
    private ImageView productImage;
    private AlertDialog pickImageDialog;
    private String mTempPhotoPath;
    private Uri mPhotoURI;
    private int mUsersChoice;

    final DialogInterface.OnClickListener mDialogClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int item) {
            mUsersChoice = item;
        }
    };

    public AddProductFragment() {
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_add_product, container, false);
        setHasOptionsMenu(true);
        //Find views
        productName_et = rootView.findViewById(R.id.editProductName);
        salePrice_et = rootView.findViewById(R.id.editSalePrice);
        quantity_et = rootView.findViewById(R.id.editQuantity);
        mSupplierSpin = rootView.findViewById(R.id.supplierSpinner);
        ImageButton add_supplier_btn = rootView.findViewById(R.id.add_supplier_btn);
        productImage = rootView.findViewById(R.id.product_details_gallery_image);

        //Set click listeners on buttons
        add_supplier_btn.setOnClickListener(this);
        productImage.setOnClickListener(this);

        //Set the spinner which shows existing supplier names
        mSupplierSpin.setOnItemSelectedListener(this);
        supplierNames = DatabaseUtils.getEnterpriseNames(getActivity(), Constants.SUPPLIER);
        mSpinAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, supplierNames);
        mSpinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSupplierSpin.setAdapter(mSpinAdapter);

        Bundle bundle = getArguments();
        String uriString = null;
        if (bundle != null) uriString = bundle.getString(Constants.PRODUCT_URI);
        if (uriString != null) mCurrentProductUri = Uri.parse(uriString);

        //Set the title that corresponds to the fragment
        if (mCurrentProductUri == null) {
            getActivity().setTitle(getString(R.string.add_product));
            getActivity().invalidateOptionsMenu();
        } else {
            getActivity().setTitle(getString(R.string.edit_product));
            getLoaderManager().initLoader(Constants.SINGLE_PRODUCT_LOADER, null, this);
        }

        SharedPreferences preferences = getActivity().getSharedPreferences("MyPref", 0);
        final SharedPreferences.Editor editor = preferences.edit();

        //This is for QuickStart. It will be shown only once at the first launch.
        if(preferences.getInt(Constants.THIRD_TAPPROMPT_IS_SHOWN, 0) == 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new MaterialTapTargetPrompt.Builder(AddProductFragment.this)
                            .setTarget(rootView.findViewById(R.id.add_supplier_btn))
                            .setPrimaryText("If the supplier is not already in the list")
                            .setSecondaryText("Tap to add new supplier")
                            .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                                @Override
                                public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state) {
                                    //No need to add something here, that button already has a click listener
                                }
                            })
                            .show();
                    editor.putInt(Constants.THIRD_TAPPROMPT_IS_SHOWN, 1);
                    editor.apply();
                }
            }, 2500);
        }

        return rootView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_with_delete, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_delete:{
                openAlertDialogForDelete();
                break;
            }
            case R.id.action_save:{
                saveProduct();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void openAlertDialogForDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_DayNight_Dialog);
        builder.setMessage("Do you want to delete this item from the database?");
        builder.setPositiveButton("Yes, delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteProduct();
                getActivity().onBackPressed();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.create();
        builder.show();
    }

    private void deleteProduct(){
        if(mCurrentProductUri != null){
            int rowsDeleted = getActivity().getContentResolver().delete(mCurrentProductUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(getActivity(), "Error during delete",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(getActivity(), "product successfully deleted",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.add_supplier_btn: {
                //This button opens a new fragment for adding a new supplier
                AddEnterpriseFragment addSupplierFrag = new AddEnterpriseFragment();
                addSupplierFrag.setEnterTransition(new Slide(Gravity.END));
                addSupplierFrag.setExitTransition(new Slide(Gravity.START));
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
        builder.setSingleChoiceItems(dialogOptions, -1, mDialogClickListener);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (mUsersChoice) {
                    case 0:{
                        if (ContextCompat.checkSelfPermission(getActivity(),
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            // If you do not have permission, request it
                            AddProductFragment.this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    Constants.REQUEST_STORAGE_PERMISSION);
                        } else {
                            // Launch the camera if the permission exists
                            openCamera();
                        }
                        break;
                    }
                    case 1:
                        openGallery();
                        break;
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        pickImageDialog = builder.create();
        pickImageDialog.show();
    }

    private void openGallery(){
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), Constants.PICK_IMAGE_REQUEST);
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

                mPhotoURI = FileProvider.getUriForFile(getActivity(),
                        Constants.FILE_PROVIDER_AUTHORITY,
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoURI);
                startActivityForResult(takePictureIntent, Constants.REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        pickImageDialog.dismiss();
        if (requestCode == Constants.REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                GlideApp.with(AddProductFragment.this)
                        .load(mPhotoURI)
                        .into(productImage);
            } else {
                BitmapUtils.deleteImageFile(getActivity(), mTempPhotoPath);
            }
        } else if (requestCode == Constants.PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                mPhotoURI = data.getData();
                GlideApp.with(AddProductFragment.this)
                        .load(mPhotoURI)
                        .into(productImage);
            }
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

        float salePrice;
        try{
            salePrice = Float.valueOf(salePrice_et.getText().toString().trim());
            if(salePrice < 0){
                Toast.makeText(getActivity(), R.string.price_should_be_number, Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException nfe) {
            Toast.makeText(getActivity(), R.string.price_should_be_number, Toast.LENGTH_SHORT).show();
            return;
        }


        ContentValues values = new ContentValues();
        values.put(ProductEntry.PRODUCT_NAME, productName);
        values.put(ProductEntry.SALE_PRICE, salePrice);
        values.put(ProductEntry.QUANTITY_IN_STOCK, quantityInStock);
        values.put(ProductEntry.SUPPLIER_NAME, chosenSupplierName);
        values.put(ProductEntry.IMAGE_FILE_PATH, mPhotoURI.toString());

        if (mCurrentProductUri == null) {
            //This is new product entry
            Uri newUri = getActivity().getContentResolver().insert(ProductEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(getActivity(), R.string.error_saving_product, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), getString(R.string.product_saved_successfully), Toast.LENGTH_SHORT).show();
                getFragmentManager().popBackStack();
            }
        } else {
            // Otherwise this is an existing product, so update the product
            int rowsAffected = getActivity().getContentResolver().update(mCurrentProductUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(getActivity(), R.string.error_saving_product, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), getString(R.string.product_saved_successfully), Toast.LENGTH_SHORT).show();
                getFragmentManager().popBackStack();
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
        Log.d("AddProduct", "onloadFinished called");
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int productNameColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.PRODUCT_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.QUANTITY_IN_STOCK);
            int priceColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.SALE_PRICE);
            int supplierColumnIndex = cursor.getColumnIndex(ProductEntry.SUPPLIER_NAME);
            int imageColumnIndex = cursor.getColumnIndex(ProductEntry.IMAGE_FILE_PATH);

            String productName = cursor.getString(productNameColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            float price = cursor.getFloat(priceColumnIndex);
            String supplierName = cursor.getString(supplierColumnIndex);
            String imagePath = cursor.getString(imageColumnIndex);
            Uri uriToShow = mPhotoURI == null ? Uri.parse(imagePath) : mPhotoURI;
            productName_et.setText(productName);
            salePrice_et.setText(String.valueOf(price));
            quantity_et.setText(String.valueOf(quantity));
            mSupplierSpin.setSelection(mSpinAdapter.getPosition(supplierName));
            GlideApp.with(getActivity())
                    .load(uriToShow)
                    .placeholder(R.drawable.placeholder)
                    .into(productImage);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    }
}
