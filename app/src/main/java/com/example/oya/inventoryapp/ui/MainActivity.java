package com.example.oya.inventoryapp.ui;

import android.content.SharedPreferences;
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
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.oya.inventoryapp.R;
import com.example.oya.inventoryapp.utils.Constants;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener{

    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;
    private ConstraintLayout mConstraintLayout;
    private ConstraintSet mConstraintSet2;
    private ConstraintSet mConstraintSet1;
    private FloatingActionButton fab_main, fab_add_product, fab_delivery, fab_acquisition;
    private boolean fabsInClickedState = false;
    private TextView hint_main_tv, hint_add_item_tv, hint_acquisition_tv, hint_delivery_tv;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Set navigation drawer
        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Define constraint sets for a constraint set animation between idle and clicked states of fab buttons
        mConstraintSet1 = new ConstraintSet();
        mConstraintSet2 = new ConstraintSet();
        mConstraintLayout = findViewById(R.id.fab_default_state);
        mConstraintSet1.clone(mConstraintLayout);
        mConstraintSet2.clone(this, R.layout.fab_clicked_state);

        //Find floating actions buttons and hint textviews
        fab_main =  findViewById(R.id.fab_main);
        fab_add_product = findViewById(R.id.fab_add_item);
        fab_delivery = findViewById(R.id.fab_delivery);
        fab_acquisition = findViewById(R.id.fab_acquisition);
        hint_main_tv = findViewById(R.id.hint_cancel);
        hint_acquisition_tv = findViewById(R.id.hint_acquisition);
        hint_add_item_tv = findViewById(R.id.hint_add_product);
        hint_delivery_tv = findViewById(R.id.hint_Delivery);

        //Set click listeners on fabs
        fab_main.setOnClickListener(this);
        fab_add_product.setOnClickListener(this);
        fab_delivery.setOnClickListener(this);
        fab_acquisition.setOnClickListener(this);

        //Add product list fragment as the default fragment
        if(savedInstanceState == null) {
            ProductListFragment productListFrag = new ProductListFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, productListFrag)
                    .addToBackStack(null)
                    .commit();
        } else {
            fabsInClickedState = savedInstanceState.getBoolean(Constants.IS_FAB_CLICKED);
            if(fabsInClickedState) showFourFABs();
        }

        preferences = getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = preferences.edit();
        //This is for QuickStart. It will be shown only once at the first launch.
        if((preferences.getInt(Constants.FIRST_TAPPROMPT_IS_SHOWN, 0) == 0)){
            new MaterialTapTargetPrompt.Builder(MainActivity.this)
                    .setTarget(findViewById(R.id.fab_main))
                    .setPrimaryText("Welcome to your mobile inventory. Let's get started!")
                    .setSecondaryText("Tap the see more options")
                    .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
                    {
                        @Override
                        public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state)
                        {
                            if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED)
                            {
                                //showFourFABs();
                            }
                        }
                    })
                    .show();
            editor.putInt(Constants.FIRST_TAPPROMPT_IS_SHOWN, 1);
            editor.apply();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Constants.IS_FAB_CLICKED, fabsInClickedState);
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
                if(!fabsInClickedState){
                    showFourFABs();
                } else {
                    showSingleFAB();
                }
                break;
            }
            case R.id.fab_add_item:{
                AddProductFragment addProductFrag = new AddProductFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, addProductFrag)
                        .addToBackStack(null)
                        .commit();
                showSingleFAB();
                break;
            }
            case R.id.fab_acquisition:{
                openAddTransactionFragment(Constants.ACQUISITION);
                showSingleFAB();
                break;
            }
            case R.id.fab_delivery:{
                openAddTransactionFragment(Constants.DELIVERY);
                showSingleFAB();
                break;
            }
        }
        fabsInClickedState ^= true;
    }

    private void showFourFABs(){
        TransitionManager.beginDelayedTransition(mConstraintLayout, new MainActivity.MyTransition());
        hint_delivery_tv.setVisibility(View.VISIBLE);
        hint_add_item_tv.setVisibility(View.VISIBLE);
        hint_acquisition_tv.setVisibility(View.VISIBLE);
        hint_main_tv.setVisibility(View.VISIBLE);
        mConstraintSet2.applyTo(mConstraintLayout);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fab_main.setImageResource(R.drawable.restart_white_32);

            }
        }, 500);

        //This is for QuickStart. It will be shown only once at the first launch.
        if(preferences.getInt(Constants.SECOND_TAPPROMPT_IS_SHOWN, 0) == 0){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new MaterialTapTargetPrompt.Builder(MainActivity.this)
                            .setTarget(findViewById(R.id.fab_add_item))
                            .setPrimaryText("Add your first product")
                            .setSecondaryText("Tap to enter your first product")
                            .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                                @Override
                                public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state) {
                                    if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {

                                    }
                                }
                            })
                            .show();
                    editor.putInt(Constants.SECOND_TAPPROMPT_IS_SHOWN, 1);
                    editor.apply();
                }
            }, 3000);
        }
    }



    private void showSingleFAB(){
        TransitionManager.beginDelayedTransition(mConstraintLayout, new MainActivity.MyTransition());
        hint_delivery_tv.setVisibility(View.GONE);
        hint_add_item_tv.setVisibility(View.GONE);
        hint_acquisition_tv.setVisibility(View.GONE);
        hint_main_tv.setVisibility(View.GONE);
        mConstraintSet1.applyTo(mConstraintLayout);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fab_main.setImageResource(R.drawable.baseline_add_white_24dp);

            }
        }, 500);
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
                openEnterpriseListFragment(Constants.SUPPLIER);
                break;
            }
            case R.id.clients:{
                openEnterpriseListFragment(Constants.CLIENT);
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
                openAddEnterPriseFragment(Constants.SUPPLIER);
                break;
            }
            case R.id.add_client:{
                openAddEnterPriseFragment(Constants.CLIENT);
                break;
            }
            case R.id.new_delivery:{
                openAddTransactionFragment(Constants.DELIVERY);
                break;
            }
            case R.id.new_acquisition:{
                openAddTransactionFragment(Constants.ACQUISITION);
                break;
            }
            case R.id.transaction_list:{
                TransactionListFragment transactionListFrag = new TransactionListFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, transactionListFrag)
                        .addToBackStack(null)
                        .commit();
                break;
            }
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openEnterpriseListFragment(String relationshipType){
        EnterpriseListFragment supplierListFrag = new EnterpriseListFragment();
        Bundle args = new Bundle();
        args.putString(Constants.RELATION_TYPE, relationshipType);
        supplierListFrag.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, supplierListFrag)
                .addToBackStack(null)
                .commit();
    }

    private void openAddEnterPriseFragment(String relationType) {
        AddEnterpriseFragment addSupplierFrag = new AddEnterpriseFragment();
        Bundle args = new Bundle();
        args.putString(Constants.RELATION_TYPE, relationType);
        addSupplierFrag.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, addSupplierFrag)
                .addToBackStack(null)
                .commit();
    }

    private void openAddTransactionFragment(String transactionType){
        AddTransactionFragment transactionFrag = new AddTransactionFragment();
        Bundle args = new Bundle();
        args.putString(Constants.TRANSACTION_TYPE, transactionType);
        transactionFrag.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, transactionFrag)
                .addToBackStack(null)
                .commit();
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
}
