package com.dzinesunlimited.resto.frontend.menu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dzinesunlimited.resto.R;
import com.dzinesunlimited.resto.utils.TypefaceSpan;
import com.dzinesunlimited.resto.utils.db.DBResto;
import com.dzinesunlimited.resto.utils.helpers.pojos.CategoriesData;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CategorySelector extends AppCompatActivity {

    /** THE DATABASE HELPER INSTANCE **/
    DBResto db;

    /** THE INCOMING TABLE NUMBER**/
    String INCOMING_TABLE_NO = null;

    /** THE CURRENT ORDER CART STATUS **/
    int ORDER_CART_COUNT = 0;

    /** DECLARE THE LAYOUT ELEMENTS **/
    LinearLayout linlaHeaderProgress;
    RecyclerView gridCategories;
    LinearLayout linlaEmpty;

    /***** THE ADAPTER AND THE ARRAY LIST *****/
    CategoriesAdapter adapCategories;
    ArrayList<CategoriesData> arrCategory = new ArrayList<>();

    /** STATIC REQUEST CODE FOR UPDATING ORDERS **/
    private static final int ACTION_EDIT_ORDERS = 101;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fe_category_selector);

        /** GET THE INCOMING DATA **/
        getIncomingData();

        /** CAST THE LAYOUT ELEMENTS **/
        castLayoutElements();

        /***** CONFIGURE THE ACTIONBAR *****/
        configAB();

        /** FETCH CATEGORIES **/
        new fetchCategories().execute();

        /** INSTANTIATE THE CATEGORIES ADAPTER **/
        adapCategories = new CategoriesAdapter(CategorySelector.this, arrCategory);
    }

    /** FETCH THE LIST OF CATEGORIES **/
    private class fetchCategories extends AsyncTask<String, String, String> {

        /** A CURSOR INSTANCE **/
        Cursor cursor;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /** SHOW THE PROGRESSBAR WHILE LOADING THE DATA **/
            linlaHeaderProgress.setVisibility(View.VISIBLE);

            /** INSTANTIATE THE DATABASE HELPER CLASS **/
            db = new DBResto(CategorySelector.this);

            /** CONSTRUCT THE QUERY TO FETCH THE MENU CATEGORIES FROM THE DATABASE **/
            String strQueryData = "SELECT * FROM " + db.CATEGORY;

            /** CAST THE QUERY IN THE CURSOR TO FETCH THE RESULTS **/
            cursor = db.selectAllData(strQueryData);
        }

        @Override
        protected String doInBackground(String... args) {

            /** CHECK THAT THE DATABASE QUERY RETURNED SOME RESULTS **/
            if (cursor != null && cursor.getCount() != 0)	{

                /** AN INSTANCE OF THE CategoriesData HELPER CLASS **/
                CategoriesData category;

                /** LOOP THROUGH THE RESULT SET AND PARSE NECESSARY INFORMATION **/
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                    /***** INSTANTIATE THE CategoriesData INSTANCE "category" *****/
                    category = new CategoriesData();

                    /** GET THE CATEGORY ID **/
                    if (cursor.getString(cursor.getColumnIndex(db.CATEGORY_ID)) != null)	{
                        String CATEGORY_ID = cursor.getString(cursor.getColumnIndex(db.CATEGORY_ID));
                        category.setCatID(CATEGORY_ID);
//						Log.e("CATEGORY_ID", CATEGORY_ID);
                    } else {
                        category.setCatID(null);
                    }

                    /** GET THE CATEGORY NAME **/
                    if (cursor.getString(cursor.getColumnIndex(db.CATEGORY_NAME)) != null)	{
                        String CATEGORY_NAME = cursor.getString(cursor.getColumnIndex(db.CATEGORY_NAME)).toUpperCase();
                        category.setCatName(CATEGORY_NAME);
//						Log.e("CATEGORY_NAME", CATEGORY_NAME);
                    } else {
                        category.setCatName(null);
                    }

                    /** GET THE CATEGORY ID **/
                    if (cursor.getBlob(cursor.getColumnIndex(db.CATEGORY_IMAGE)) != null)	{
                        byte[] CATEGORY_THUMB = cursor.getBlob(cursor.getColumnIndex(db.CATEGORY_IMAGE));
                        category.setCatPicture(CATEGORY_THUMB);
                    } else {
                        category.setCatPicture(null);
                    }

                    /** ADD THE COLLECTED DATA TO THE ARRAYLIST **/
                    arrCategory.add(category);
                }

            } else {
                // TODO: SHOW EMPTY MESSAGE
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            /** CLOSE THE CURSOR **/
            if (cursor != null && !cursor.isClosed())	{
                cursor.close();
            }

            /** CLOSE THE DATABASE **/
            db.close();

            /** SET THE ADAPTER TO THE CATEGORIES RECYCLER VIEW **/
            gridCategories.setAdapter(adapCategories);

            /** HIDE THE PROGRESSBAR AFTER LOADING THE DATA **/
            linlaHeaderProgress.setVisibility(View.GONE);
        }
    }

    /** CAST THE LAYOUT ELEMENTS **/
    private void castLayoutElements() {
        linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
        gridCategories = (RecyclerView) findViewById(R.id.gridCategories);
        linlaEmpty = (LinearLayout) findViewById(R.id.linlaEmpty);

        /* CONFIGURE THE RECYCLER VIEW */
        int intOrientation = getResources().getConfiguration().orientation;
        gridCategories = (RecyclerView) findViewById(R.id.gridCategories);
        gridCategories.setHasFixedSize(true);
        GridLayoutManager glm = null;
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isTablet)   {
            if (intOrientation == 1)	{
                glm = new GridLayoutManager(CategorySelector.this, 2);
            } else if (intOrientation == 2) {
                glm = new GridLayoutManager(CategorySelector.this, 3);
            }
        } else {
            if (intOrientation == 1)    {
                glm = new GridLayoutManager(CategorySelector.this, 1);
            } else if (intOrientation == 2) {
                glm = new GridLayoutManager(CategorySelector.this, 2);
            }
        }
        gridCategories.setLayoutManager(glm);
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {

        Toolbar myToolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);

        SpannableString s = new SpannableString("Menu Categories");
        s.setSpan(new TypefaceSpan(
                this, "RobotoCondensed-Regular.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(s);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(CategorySelector.this);
        inflater.inflate(R.menu.menu_landing, menu);

        /** INFLATE THE CUSTOM ORDER CART LAYOUT **/
        View view = menu.findItem(R.id.menuOrderCart).getActionView();
        AppCompatImageView imgvwOrderCart = (AppCompatImageView) view.findViewById(R.id.imgvwOrderCart);
        AppCompatTextView txtOrderCart = (AppCompatTextView) view.findViewById(R.id.txtOrderCart);

        /** SHOW THE ORDER ITEMS QUANTITY **/
        txtOrderCart.setText(String.valueOf(ORDER_CART_COUNT));

        /** SHOW THE ORDER CART **/
        imgvwOrderCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showOrders = new Intent(CategorySelector.this, OrderCart.class);
                showOrders.putExtra("TABLE_NO", INCOMING_TABLE_NO);
                startActivityForResult(showOrders, ACTION_EDIT_ORDERS);
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:

                this.finish();

                break;

            default:
                break;
        }

        return false;
    }

    /** GET THE INCOMING DATA **/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey("TABLE_NO")) {
            INCOMING_TABLE_NO = bundle.getString("TABLE_NO");
            if (INCOMING_TABLE_NO != null)  {
            } else {
                // TODO: SHOW AN ERROR
            }
        } else {
            // TODO: SHOW AN ERROR
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        /** GET THE ORDER CART COUNT **/
        db = new DBResto(CategorySelector.this);
        String strQueryData = "SELECT * FROM " + db.ORDER_CART + " WHERE " + db.ORDER_TABLE_ID + " = " + INCOMING_TABLE_NO;
        Cursor cursor = db.selectAllData(strQueryData);
        ORDER_CART_COUNT = cursor.getCount();
        db.close();

        /** INVALIDATE THE OPTIONS MENU **/
        invalidateOptionsMenu();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    /** THE CATEGORIES CUSTOM ADAPTER **/
    private class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoriesVH> {

        /** AN ACTIVITY INSTANCE **/
        Activity activity;

        /** ARRAYLIST TO GET DATA FROM THE ACTIVITY **/
        ArrayList<CategoriesData> arrCategories;

        public CategoriesAdapter(Activity activity, ArrayList<CategoriesData> arrCategories) {

            /** CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE **/
            this.activity = activity;

            /** CAST THE CONTENTS OF THE ARRAYLIST IN THE METHOD TO THE LOCAL INSTANCE **/
            this.arrCategories = arrCategories;
        }

        @Override
        public int getItemCount() {
            return arrCategories.size();
        }

        @Override
        public void onBindViewHolder(CategoriesVH holder, final int position) {
            final CategoriesData md = arrCategories.get(position);

            /** GET THE SUB MENU NAME **/
            final String SUB_MENU_NAME = md.getCatName();
            holder.txtCategoryName.setText(SUB_MENU_NAME);

            /** GET THE SUB MENU THUMBNAIL **/
            byte[] CATEGORY_THUMB = md.getCatPicture();
            if (CATEGORY_THUMB != null)	{
                Bitmap bmpThumb = BitmapFactory.decodeByteArray(CATEGORY_THUMB, 0, CATEGORY_THUMB.length);
                holder.imgvwCategory.setImageBitmap(bmpThumb);
            }

            /** CHANGE THE CATEGORY SELECTION **/
            holder.imgvwCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent showMenu = new Intent(activity, MenuActivity.class);
                    showMenu.putExtra("CATEGORY_ID", md.getCatID());
                    showMenu.putExtra("TABLE_NO", INCOMING_TABLE_NO);
                    activity.startActivity(showMenu);
                }
            });
        }

        @Override
        public CategoriesVH onCreateViewHolder(ViewGroup parent, int i) {

            View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.fe_category_selector_item, parent, false);

            return new CategoriesVH(itemView);
        }

        public class CategoriesVH extends RecyclerView.ViewHolder	{

            AppCompatImageView imgvwCategory;
            AppCompatTextView txtCategoryName;

            public CategoriesVH(View v) {
                super(v);

                /*****	CAST THE LAYOUT ELEMENTS	*****/
                imgvwCategory = (AppCompatImageView) v.findViewById(R.id.imgvwCategory);
                txtCategoryName = (AppCompatTextView) v.findViewById(R.id.txtCategoryName);
            }

        }
    }
}