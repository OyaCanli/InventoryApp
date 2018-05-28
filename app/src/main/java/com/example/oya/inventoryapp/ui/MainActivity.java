package com.example.oya.inventoryapp.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.transition.Fade;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.oya.inventoryapp.Constants;
import com.example.oya.inventoryapp.R;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener{

    private ActionBarDrawerToggle toggle;
    private ConstraintLayout mConstraintLayout;
    private ConstraintSet mConstraintSet2;
    private FloatingActionButton fab_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Set navigation drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Define constraint sets for a constraint set animation between idle and clicked states of fab buttons
        ConstraintSet mConstraintSet1 = new ConstraintSet();
        mConstraintSet2 = new ConstraintSet();
        mConstraintLayout = findViewById(R.id.fab_default_state);
        mConstraintSet1.clone(mConstraintLayout);
        mConstraintSet2.clone(this, R.layout.fab_clicked_state);

        //Find floating actions buttons
        fab_main =  findViewById(R.id.fab_main);
        FloatingActionButton fab_add_product = findViewById(R.id.fab_add_item);
        FloatingActionButton fab_delivery = findViewById(R.id.fab_delivery);
        FloatingActionButton fab_acquisition = findViewById(R.id.fab_acquisition);
        //Set clicklisteners on fabs
        fab_main.setOnClickListener(this);
        fab_add_product.setOnClickListener(this);
        fab_delivery.setOnClickListener(this);
        fab_acquisition.setOnClickListener(this);

        //Add product list fragment as the default fragment
        ProductListFragment productListFrag = new ProductListFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, productListFrag)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id) {
            case R.id.fab_main: {
                TransitionManager.beginDelayedTransition(mConstraintLayout, new MainActivity.MyTransition());
                mConstraintSet2.applyTo(mConstraintLayout);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fab_main.setImageResource(R.drawable.restart_white_32);

                    }
                }, 500);
                break;
            }
            case R.id.fab_add_item:{
                AddProductFragment addProductFrag = new AddProductFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, addProductFrag)
                        .addToBackStack(null)
                        .commit();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.products:{
                ProductListFragment productListFrag = new ProductListFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, productListFrag)
                        .addToBackStack(null)
                        .commit();
                break;
            }
            case R.id.suppliers:{
                EnterpriseListFragment supplierListFrag = new EnterpriseListFragment();
                Bundle args = new Bundle();
                args.putString(Constants.RELATION_TYPE, Constants.SUPPLIER);
                supplierListFrag.setArguments(args);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, supplierListFrag)
                        .addToBackStack(null)
                        .commit();
                break;
            }
            case R.id.clients:{
                EnterpriseListFragment clientListFrag = new EnterpriseListFragment();
                Bundle args = new Bundle();
                args.putString(Constants.RELATION_TYPE, Constants.CLIENT);
                clientListFrag.setArguments(args);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, clientListFrag)
                        .addToBackStack(null)
                        .commit();
                break;
            }
            case R.id.add_product:{
                AddProductFragment addProductFrag = new AddProductFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, addProductFrag)
                        .addToBackStack(null)
                        .commit();
                break;
            }
            case R.id.add_supplier:{
                AddEnterpriseFragment addSupplierFrag = new AddEnterpriseFragment();
                Bundle args = new Bundle();
                args.putString(Constants.RELATION_TYPE, Constants.SUPPLIER);
                addSupplierFrag.setArguments(args);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, addSupplierFrag)
                        .addToBackStack(null)
                        .commit();
                break;
            }
            case R.id.add_client:{
                AddEnterpriseFragment addSupplierFrag = new AddEnterpriseFragment();
                Bundle args = new Bundle();
                args.putString(Constants.RELATION_TYPE, Constants.CLIENT);
                addSupplierFrag.setArguments(args);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, addSupplierFrag)
                        .addToBackStack(null)
                        .commit();
                break;
            }
            case R.id.new_delivery:{

                break;
            }
            case R.id.new_acquisition:{

                break;
            }
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Custom transition used during the transition of constraint sets
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
}
