package com.dzinesunlimited.resto.frontend.menu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dzinesunlimited.resto.R;
import com.dzinesunlimited.resto.utils.AppPrefs;
import com.dzinesunlimited.resto.utils.TypefaceSpan;
import com.dzinesunlimited.resto.utils.db.DBResto;
import com.dzinesunlimited.resto.utils.helpers.pojos.frontend.DishesData;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MenuActivity extends AppCompatActivity {

    /** THE DATABASE HELPER INSTANCE **/
    DBResto db;

    /** THE INCOMING CATEGORY ID AND TABLE NUMBER **/
    String INCOMING_CATEGORY_ID = null;
    String INCOMING_TABLE_NO = null;

    /** DECLARE THE LAYOUT ELEMENTS **/
    LinearLayout linlaHeaderProgress;
    RecyclerView listMenus;
    LinearLayout linlaEmpty;

    /***** THE ADAPTER AND THE ARRAY LIST *****/
    DishesAdapter adapDishes;
    ArrayList<DishesData> arrDishes = new ArrayList<>();

    /** THE CURRENT ORDER CART STATUS **/
    int ORDER_CART_COUNT = 0;

    /** STATIC INTENT DECLARATION **/
    private static final int ACTION_ADD_ORDER = 100;

    /** STATIC REQUEST CODE FOR UPDATING ORDERS **/
    private static final int ACTION_EDIT_ORDERS = 101;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fe_menu);

        /** CAST THE LAYOUT ELEMENTS **/
        castLayoutElements();

        /***** CONFIGURE THE ACTIONBAR *****/
        configAB();

        /** GET THE INCOMING CATEGORY ID **/
        getIncomingData();

        /** INSTANTIATE THE ADAPTER **/
        adapDishes = new DishesAdapter(MenuActivity.this, arrDishes);
    }

    /** GET THE INCOMING CATEGORY ID **/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey("CATEGORY_ID") && bundle.containsKey("TABLE_NO")) {
            INCOMING_CATEGORY_ID = bundle.getString("CATEGORY_ID");
            INCOMING_TABLE_NO = bundle.getString("TABLE_NO");
            if (INCOMING_CATEGORY_ID != null)   {
                /** RUN THE TASK TO FETCH MEALS / MENUS IN THE SELECTED CATEGORY **/
                new fetchDishes().execute();
            } else {
                // SHOW AN ERROR
            }
        } else {
            // SHOW AN ERROR
        }
    }

    private class fetchDishes extends AsyncTask<Void, Void, Void> {

        /** A CURSOR INSTANCE **/
        Cursor cursor;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /** SHOW THE PROGRESSBAR WHILE LOADING THE INGREDIENTS **/
            linlaHeaderProgress.setVisibility(View.VISIBLE);

            /** INSTANTIATE THE DATABASE HELPER CLASS **/
            db = new DBResto(MenuActivity.this);

            /** CONSTRUCT THE QUERY TO FETCH ALL MEALS / MENUS FROM THE DATABASE **/
            String strQueryData = "SELECT * FROM " + db.MENU + " WHERE " + db.MENU_CATEGORY_ID + " = " + INCOMING_CATEGORY_ID;

            /** CAST THE QUERY IN THE CURSOR TO FETCH THE RESULTS **/
            cursor = db.selectAllData(strQueryData);
        }

        @Override
        protected Void doInBackground(Void... params) {

            /** CHECK THAT THE DATABASE QUERY RETURNED SOME RESULTS **/
            if (cursor != null && cursor.getCount() != 0)	{

                /** AN INSTANCE OF THE DishesData HELPER CLASS **/
                DishesData dish;

                /** LOOP THROUGH THE RESULT SET AND PARSE NECESSARY INFORMATION **/
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                    /***** INSTANTIATE THE DishesData INSTANCE "dish" *****/
                    dish = new DishesData();

                    /** GET THE MEAL ID **/
                    if (cursor.getString(cursor.getColumnIndex(db.MENU_ID)) != null)	{
                        String strMealNo = cursor.getString(cursor.getColumnIndex(db.MENU_ID));
                        dish.setMenuID(strMealNo);
//                        Log.e("MEAL NO.", strMealNo);
                    } else {
                        dish.setMenuID(null);
                    }

                    /** GET THE MEAL NAME **/
                    if (cursor.getString(cursor.getColumnIndex(db.MENU_NAME)) != null)	{
                        String strMealName = cursor.getString(cursor.getColumnIndex(db.MENU_NAME));
                        dish.setMenuName(strMealName);
//						Log.e("MEAL NAME", strMealName);
                    } else {
                        dish.setMenuName(null);
                    }

                    /** GET THE MEAL DESCRIPTION **/
                    if (cursor.getString(cursor.getColumnIndex(db.MENU_DESCRIPTION)) != null)	{
                        String strMealDesc = cursor.getString(cursor.getColumnIndex(db.MENU_DESCRIPTION));
                        dish.setMenuDescription(strMealDesc);
//						Log.e("MEAL DESCRIPTION", strMealDesc);
                    } else {
                        dish.setMenuDescription(null);
                    }

                    /** GET THE MEAL IMAGE **/
                    if (cursor.getBlob(cursor.getColumnIndex(db.MENU_IMAGE)) != null)	{
                        byte[] bytImage = cursor.getBlob(cursor.getColumnIndex(db.MENU_IMAGE));
                        dish.setMenuImage(bytImage);
                    } else {
                        dish.setMenuImage(null);
                    }

                    /** GET THE MEAL PRICE **/
                    if (cursor.getString(cursor.getColumnIndex(db.MENU_PRICE)) != null)	{
                        String strMealPrice = cursor.getString(cursor.getColumnIndex(db.MENU_PRICE));
                        dish.setMenuPrice(strMealPrice);
//						Log.e("MEAL PRICE", strMealPrice);
                    } else {
                        dish.setMenuPrice(null);
                    }

                    /** GET THE MEAL TYPE **/
                    if (cursor.getString(cursor.getColumnIndex(db.MENU_TYPE)) != null)	{
                        String strMealType = cursor.getString(cursor.getColumnIndex(db.MENU_TYPE));
                        dish.setMenuType(strMealType);
                    } else {
                        dish.setMenuType(null);
                    }

                    /** GET THE MEAL CATEGORY ID **/
                    if (cursor.getString(cursor.getColumnIndex(db.MENU_CATEGORY_ID)) != null)	{
                        String strMealCatID = cursor.getString(cursor.getColumnIndex(db.MENU_CATEGORY_ID));
                        dish.setMenuCategoryID(strMealCatID);
//						Log.e("MEAL CATEGORY", strMealCatID);
                    } else {
                        dish.setMenuCategoryID(null);
                    }

                    /** ADD THE COLLECTED DATA TO THE ARRAYLIST **/
                    arrDishes.add(dish);
                }

                /** SHOW THE MENU RECYCLER VIEW  AND HIDE THE EMPTY CONTAINER **/
                Runnable run = new Runnable() {

                    @Override
                    public void run() {

                        /** SHOW THE MENU RECYCLER VIEW **/
                        listMenus.setVisibility(View.VISIBLE);

                        /** HIDE THE EMPTY DATA SET IMAGE AND TEXT **/
                        linlaEmpty.setVisibility(View.GONE);
                    }
                }; runOnUiThread(run);

            } else {

                /** SHOW THE EMPTY CONTAINER AND HIDE THE MENU RECYCLER VIEW **/
                Runnable run = new Runnable() {

                    @Override
                    public void run() {

                        /** HIDE THE MENU RECYCLER VIEW **/
                        listMenus.setVisibility(View.GONE);

                        /** SHOW THE EMPTY DATA SET IMAGE AND TEXT **/
                        linlaEmpty.setVisibility(View.VISIBLE);
                    }
                }; runOnUiThread(run);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            /** CLOSE THE CURSOR **/
            if (cursor != null && !cursor.isClosed())	{
                cursor.close();
            }

            /** CLOSE THE DATABASE **/
            db.close();

            /** SET THE ADAPTER TO THE RECYCLER VIEW **/
            listMenus.setAdapter(adapDishes);

            /** HIDE THE PROGRESSBAR AFTER LOADING THE DATA **/
            linlaHeaderProgress.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == ACTION_ADD_ORDER) {
            /** GET THE ORDER CART COUNT **/
            db = new DBResto(MenuActivity.this);
            String strQueryData = "SELECT * FROM " + db.ORDER_CART + " WHERE " + db.ORDER_TABLE_ID + " = " + INCOMING_TABLE_NO;
            Cursor cursor = db.selectAllData(strQueryData);
            ORDER_CART_COUNT = cursor.getCount();
            Log.e("CART COUNT", String.valueOf(ORDER_CART_COUNT));
            db.close();

            /** INVALIDATE THE OPTIONS MENU **/
            invalidateOptionsMenu();
        }
    }

    /** CAST THE LAYOUT ELEMENTS **/
    private void castLayoutElements() {
        linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
        listMenus = (RecyclerView) findViewById(R.id.listMenus);
        linlaEmpty = (LinearLayout) findViewById(R.id.linlaEmpty);

        /* CONFIGURE THE RECYCLER VIEW */
        int intOrientation = getResources().getConfiguration().orientation;
        listMenus.setHasFixedSize(true);
        GridLayoutManager glm = null;
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isTablet)   {
            if (intOrientation == 1)	{
                glm = new GridLayoutManager(MenuActivity.this, 2);
            } else if (intOrientation == 2) {
                glm = new GridLayoutManager(MenuActivity.this, 3);
            }
        } else {
            if (intOrientation == 1)    {
                glm = new GridLayoutManager(MenuActivity.this, 1);
            } else if (intOrientation == 2) {
                glm = new GridLayoutManager(MenuActivity.this, 2);
            }
        }
        listMenus.setLayoutManager(glm);
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {

        Toolbar myToolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);

        SpannableString s = new SpannableString("Dishes");
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
        MenuInflater inflater = new MenuInflater(MenuActivity.this);
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
                Intent showOrders = new Intent(MenuActivity.this, TestOrderCart.class);
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

    /** THE CUSTOM MENU ADAPTER **/
    private class DishesAdapter extends RecyclerView.Adapter<DishesAdapter.DishesVH> {

        /** AN ACTIVITY INSTANCE **/
        Activity activity;

        /** ARRAYLIST TO GET DATA FROM THE ACTIVITY **/
        ArrayList<DishesData> arrSubDishes;

        public DishesAdapter(Activity activity, ArrayList<DishesData> arrSubDishes) {

            /** CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE **/
            this.activity = activity;

            /** CAST THE CONTENTS OF THE ARRAYLIST IN THE METHOD TO THE LOCAL INSTANCE **/
            this.arrSubDishes = arrSubDishes;
        }

        public AppPrefs getApp()	{
            return (AppPrefs) activity.getApplication();
        }

        @Override
        public int getItemCount() {
            return arrSubDishes.size();
        }

        @Override
        public void onBindViewHolder(DishesVH holder, final int position) {

            final DishesData md = arrSubDishes.get(position);

            /** GET AND SET THE DEFAULT CURRENCY CODE **/
            String[] arrCurrency = getApp().getCurrency();
            String CURRENCY_CODE = arrCurrency[3];
            holder.txtCurrency.setText(CURRENCY_CODE);

			/* GET THE SUB MENU NAME */
            final String SUB_MENU_NAME = md.getMenuName();
            holder.txtDishName.setText(SUB_MENU_NAME);

			/* GET THE SUB MENU THUMBNAIL */
            byte[] MEAL_THUMB = md.getMenuImage();
            if (MEAL_THUMB != null)	{
                Bitmap bmpThumb = BitmapFactory.decodeByteArray(MEAL_THUMB, 0, MEAL_THUMB.length);
                holder.imgvwDish.setImageBitmap(bmpThumb);
            }

		    /* GET THE DISH PRICE */
            final String SUB_MENU_PRICE = md.getMenuPrice();
            holder.txtPrice.setText(/*CURRENCY_CODE + " " +*/SUB_MENU_PRICE);

            /* SET THE RATING */
            String strStar = activity.getResources().getString(R.string.star_symbol);
            String strRating = "4.7";
            String DISH_RATING = strRating + " " + strStar;
            holder.txtRating.setText(DISH_RATING);

            /* SET THE MENU TYPE */
            String strMenuType = md.getMenuType();
            if (strMenuType.equalsIgnoreCase("veg"))    {
                holder.imgvwMealType.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.veg_mark));
            } else if (strMenuType.equalsIgnoreCase("non-veg")) {
                holder.imgvwMealType.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_veg_mark));
            }

            /** SHOW THE DISH DETAILS **/
            holder.crdDishContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent showDishDetails = new Intent(activity, MenuDetails.class);
                    showDishDetails.putExtra("DISH_ID", md.getMenuID());
                    showDishDetails.putExtra("TABLE_NO", INCOMING_TABLE_NO);
                    activity.startActivityForResult(showDishDetails, ACTION_ADD_ORDER);
                }
            });

            /** ADD THE DISH TO THE ORDER CART **/
            holder.txtAddToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    /** GET THE SELECTED DISH ID **/
                    String MENU_ID = md.getMenuID();
                    String MENU_PRICE = md.getMenuPrice();

                    /** CALCULATE THE ORDER TOTAL **/
                    Double dblMealPrice = Double.valueOf(MENU_PRICE);
                    Double dblQuantity = Double.valueOf(1);
                    Double dblOrderTotal = dblMealPrice * dblQuantity;
                    String ORDER_TOTAL = String.valueOf(dblOrderTotal);

                    /** INSTANTIATE THE DATABASE INSTANCE **/
                    db = new DBResto(MenuActivity.this);

                    /** SHOW A PROGRESS DIALOG WHILE ADDING DISH TO CART **/
                    ProgressDialog dialog = new ProgressDialog(MenuActivity.this);
                    dialog.setCancelable(false);
                    dialog.setMessage("Adding Dish to the Order Cart....");
                    dialog.setIndeterminate(true);
                    dialog.show();

                    /** CHECK IF THE ORDER EXISTS IN THE ORDER CART **/
                    db = new DBResto(MenuActivity.this);
                    String strQueryData =
                            "SELECT * FROM " + db.ORDER_CART + " WHERE " + db.ORDER_MENU_ID + " = " + MENU_ID +
                                    " AND " + db.ORDER_STATUS + " = 0 AND " + db.ORDER_TABLE_ID + " = " + INCOMING_TABLE_NO;
//                Log.e("QUERY", strQueryData);
                    Cursor cursor = db.selectAllData(strQueryData);
                    if (cursor.getCount() != 0) {
                        Toast.makeText(
                                getApplicationContext(),
                                "Dish already present in your Order Cart. Change the quantity from the Order Cart on the previous page",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        /** ADD THE DATA TO THE ORDER CART **/
                        db.addOrder(
                                Integer.valueOf(INCOMING_TABLE_NO),
                                Integer.valueOf(MENU_ID),
                                MENU_PRICE,
                                1,
                                ORDER_TOTAL,
                                false);

                        /** REFRESH THE ORDER CART COUNT **/
                        db = new DBResto(MenuActivity.this);
                        String s = "SELECT * FROM " + db.ORDER_CART + " WHERE " + db.ORDER_TABLE_ID + " = " + INCOMING_TABLE_NO;
                        Cursor curOrders  = db.selectAllData(s);
                        ORDER_CART_COUNT = curOrders.getCount();
                        db.close();

                        /** INVALIDATE THE OPTIONS MENU **/
                        invalidateOptionsMenu();

                        /** SHOW ORDER ADDED NOTE **/
                        Toast.makeText(getApplicationContext(), "Dish added successfully", Toast.LENGTH_SHORT).show();
                    }

                    /** DISMISS THE DIALOG **/
                    dialog.dismiss();

                    /** CLOSE THE DATABASE CONNECTION **/
                    db.close();
                }
            });
        }

        @Override
        public DishesVH onCreateViewHolder(ViewGroup parent, int i) {

            View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.fe_menu_item_test, parent, false);

            return new DishesVH(itemView);
        }

        public class DishesVH extends RecyclerView.ViewHolder	{

            CardView crdDishContainer;
            AppCompatImageView imgvwDish;
            AppCompatImageView imgvwMealType;
            AppCompatTextView txtCurrency;
            AppCompatTextView txtPrice;
            AppCompatTextView txtRating;
            AppCompatTextView txtDishName;
            AppCompatTextView txtAddToCart;

            public DishesVH(View v) {
                super(v);

                /*****	CAST THE LAYOUT ELEMENTS	*****/
                crdDishContainer = (CardView) v.findViewById(R.id.crdDishContainer);
                imgvwDish = (AppCompatImageView) v.findViewById(R.id.imgvwDish);
                imgvwMealType = (AppCompatImageView) v.findViewById(R.id.imgvwMealType);
                txtCurrency = (AppCompatTextView) v.findViewById(R.id.txtCurrency);
                txtPrice = (AppCompatTextView) v.findViewById(R.id.txtPrice);
                txtRating = (AppCompatTextView) v.findViewById(R.id.txtRating);
                txtDishName = (AppCompatTextView) v.findViewById(R.id.txtDishName);
                txtAddToCart = (AppCompatTextView) v.findViewById(R.id.txtAddToCart);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        /** GET THE ORDER CART COUNT **/
        db = new DBResto(MenuActivity.this);
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
}