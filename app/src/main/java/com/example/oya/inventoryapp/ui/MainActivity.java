package com.example.oya.inventoryapp.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.FloatingActionButton;
import android.support.transition.Fade;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.oya.inventoryapp.adapters.ProductAdapter;
import com.example.oya.inventoryapp.R;
import com.example.oya.inventoryapp.model.Product;
import com.example.oya.inventoryapp.utils.InventoryViewModel;
import com.example.oya.inventoryapp.utils.MyAsyncTask;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MyAsyncTask.OnTaskCompleteListener, View.OnClickListener{

    private InventoryViewModel mainViewModel;
    private ArrayList<Product> mProductList = new ArrayList<>();
    private ProductAdapter adapter;
    ConstraintLayout mConstraintLayout;
    ConstraintSet mConstraintSet2;


    final Observer<ArrayList<Product>> productObserver = new Observer<ArrayList<Product>>() {
        @Override
        public void onChanged(@Nullable ArrayList<Product> products) {
            mProductList = products;
            adapter.refreshAdapter(mProductList);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mainViewModel = ViewModelProviders.of(this).get(InventoryViewModel.class);
        adapter = new ProductAdapter(this, mProductList);
        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);
        TextView empty_tv = findViewById(R.id.empty_view);
        empty_tv.setText("No products found");
        listView.setEmptyView(empty_tv);

        ConstraintSet mConstraintSet1 = new ConstraintSet();
        mConstraintSet2 = new ConstraintSet();
        mConstraintLayout = findViewById(R.id.fab_default_state);
        mConstraintSet1.clone(mConstraintLayout);
        mConstraintSet2.clone(this, R.layout.fab_clicked_state);

        FloatingActionButton fab_main =  findViewById(R.id.fab_main);
        FloatingActionButton fab_add_product = findViewById(R.id.fab_add_item);
        FloatingActionButton fab_delivery = findViewById(R.id.fab_delivery);
        FloatingActionButton fab_acquisition = findViewById(R.id.fab_acquisition);

        fab_main.setOnClickListener(this);
        fab_add_product.setOnClickListener(this);
        fab_delivery.setOnClickListener(this);
        fab_acquisition.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id) {
            case R.id.fab_main: {
                TransitionManager.beginDelayedTransition(mConstraintLayout, new MyTransition());
                mConstraintSet2.applyTo(mConstraintLayout);
                break;
            }
            case R.id.fab_add_item:{
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivity(intent);
            }
        }
    }

    static private class MyTransition extends TransitionSet{
        {
            setDuration(1000);
            setOrdering(ORDERING_SEQUENTIAL);
            addTransition(new android.support.transition.ChangeBounds());
            addTransition(new Fade(Fade.IN));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

        @Override
        public void onTaskCompleted(LiveData<ArrayList<Product>> productList) {
            mProductList = mainViewModel.getProducts().getValue();
            adapter.refreshAdapter(mProductList);
            mainViewModel.getProducts().observe(this, productObserver );
        }
}
