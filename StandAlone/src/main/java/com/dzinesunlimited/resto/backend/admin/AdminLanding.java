package com.dzinesunlimited.resto.backend.admin;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.dzinesunlimited.resto.R;
import com.dzinesunlimited.resto.backend.admin.frags.Accounts;
import com.dzinesunlimited.resto.backend.admin.frags.Categories;
import com.dzinesunlimited.resto.backend.admin.frags.Dashboard;
import com.dzinesunlimited.resto.backend.admin.frags.PaymentsManager;
import com.dzinesunlimited.resto.backend.admin.frags.Reports;
import com.dzinesunlimited.resto.backend.admin.frags.Tables;
import com.dzinesunlimited.resto.backend.admin.frags.Taxes;
import com.dzinesunlimited.resto.utils.db.DBResto;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AdminLanding extends AppCompatActivity {

    /** DECLARE THE LAYOUT ELEMENTS **/
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    /** A FRAGMENT INSTANCE **/
    private Fragment mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.be_admin_landing);

        /** CONFIGURE THE TOOLBAR **/
        configToolbar();

        /** CONFIGURE THE NAVIGATION BAR **/
        configureNavBar();

        /** SHOW THE FIRST FRAGMENT (DASHBOARD) **/
        if (savedInstanceState == null) {
            Fragment mContent = new Dashboard();
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame, mContent, "KEY_FRAG")
                    .commit();
        }
    }

    /** CONFIGURE THE NAVIGATION BAR **/
    private void configureNavBar() {

        /** INITIALIZE THE NAVIGATION VIEW **/
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View view = navigationView.getHeaderView(0);
        AppCompatImageView imgvwRestaurantLogo = (AppCompatImageView) view.findViewById(R.id.imgvwRestaurantLogo);
        AppCompatTextView txtRestaurantName = (AppCompatTextView) view.findViewById(R.id.txtRestaurantName);
        String RESTAURANT_NAME = null;
        byte[] RESTAURANT_LOGO = new byte[0];

        /** GET AND SET THE RESTAURANT LOGO AND NAME **/
        DBResto db = new DBResto(AdminLanding.this);
        Cursor cursor = db.selectAllData("SELECT * FROM " + db.RESTAURANT);

        if (cursor != null && cursor.getCount() != 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                /** GET THE RESTAURANT NAME **/
                if (cursor.getString(cursor.getColumnIndex(db.RESTAURANT_NAME)) != null)	{
                    RESTAURANT_NAME = cursor.getString(cursor.getColumnIndex(db.RESTAURANT_NAME));
                } else {
                    RESTAURANT_NAME = null;
                }

                /** GET THE RESTAURANT LOGO **/
                if (cursor.getBlob(cursor.getColumnIndex(db.RESTAURANT_LOGO)) != null)	{
                    RESTAURANT_LOGO = cursor.getBlob(cursor.getColumnIndex(db.RESTAURANT_LOGO));
                } else {
                    RESTAURANT_LOGO = null;
                }
            }

            /** SET THE COLLECTED DATA **/
            if (RESTAURANT_NAME != null)    {
                txtRestaurantName.setText(RESTAURANT_NAME);
            }

            if (RESTAURANT_LOGO != null)    {
                Bitmap BMP_RESTAURANT_LOGO = BitmapFactory.decodeByteArray(RESTAURANT_LOGO, 0, RESTAURANT_LOGO.length);
                imgvwRestaurantLogo.setScaleType(AppCompatImageView.ScaleType.FIT_CENTER);
                imgvwRestaurantLogo.setImageBitmap(BMP_RESTAURANT_LOGO);
            }
        }


        /** CHANGE THE FRAGMENTS ON NAVIGATION ITEM SELECTION **/
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                /** CHECK IF AN ITEM IS CHECKED / NOT CHECKED **/
                if (menuItem.isChecked())   {
                    menuItem.setChecked(false);
                }  else {
                    menuItem.setChecked(true);
                }

                /** Closing drawer on item click **/
                drawerLayout.closeDrawers();

                /** CHECK SELECTED ITEM AND SHOW APPROPRIATE FRAGMENT **/
                switch (menuItem.getItemId()){
                    case R.id.dashHome:
                        mContent = new Dashboard();
                        switchFragment(mContent);
                        return true;
                    case R.id.dashAccounts:
                        mContent = new Accounts();
                        switchFragment(mContent);
                        return true;
                    case R.id.dashMenu:
                        mContent = new Categories();
                        switchFragment(mContent);
                        return true;
                    case R.id.dashReports:
                        mContent = new Reports();
                        switchFragment(mContent);
                        return true;
                    case R.id.dashTables:
                        mContent = new Tables();
                        switchFragment(mContent);
                        return true;
                    case R.id.dashTaxes:
                        mContent = new Taxes();
                        switchFragment(mContent);
                        return true;
                    case R.id.dashPayments:
                        mContent = new PaymentsManager();
                        switchFragment(mContent);
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                        return true;
                }
            }
        });
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.myToolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                syncState();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
                syncState();
            }
        };
        drawerLayout.setDrawerListener(mDrawerToggle);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())   {
            case android.R.id.home:
                if (drawerLayout.isDrawerOpen(navigationView))    {
                    drawerLayout.closeDrawer(navigationView);
                } else {
                    drawerLayout.openDrawer(navigationView);
                }
                return  true;

            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /** METHOD TO CHANGE THE FRAGMENT **/
    private void switchFragment(Fragment fragment) {

        /** HIDE THE NAV DRAWER **/
        drawerLayout.closeDrawer(GravityCompat.START);

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, fragment)
                .commit();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}