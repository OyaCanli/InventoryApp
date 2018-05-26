package com.example.oya.inventoryapp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.oya.inventoryapp.ui.AddProductFragment;
import com.example.oya.inventoryapp.ui.AddSupplierFragment;

public class InventoryPagerAdapter extends FragmentPagerAdapter {

    public InventoryPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new AddProductFragment();
        } else {
            return new AddSupplierFragment();
        }
    }

    @Override
    public int getCount(){
        return 2;
    }
}
