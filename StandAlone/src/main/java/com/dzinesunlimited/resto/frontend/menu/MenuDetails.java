package com.dzinesunlimited.resto.frontend.menu;

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
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dzinesunlimited.resto.R;
import com.dzinesunlimited.resto.utils.AppPrefs;
import com.dzinesunlimited.resto.utils.db.DBResto;
import com.dzinesunlimited.resto.utils.helpers.OrderValueMinMax;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MenuDetails extends AppCompatActivity {

    /** THE DATABASE HELPER INSTANCE **/
    DBResto db;

    /** THE INCOMING DISH ID AND THE TABLE NUMBER**/
    private String INCOMING_DISH_ID = null;
    private String INCOMING_TABLE_NO = null;

    /** THE STAFF ID **/
    private String STAFF_ID = null;

    /** DECLARE THE LAYOUT ELEMENTS **/
    @Bind(R.id.linlaHeaderProgress)LinearLayout linlaHeaderProgress;
    @Bind(R.id.imgvwDish)AppCompatImageView imgvwDish;
    @Bind(R.id.imgvwMealType)AppCompatImageView imgvwMealType;
    @Bind(R.id.txtRating)AppCompatTextView txtRating;
    @Bind(R.id.txtDishPrice)AppCompatTextView txtDishPrice;
    @Bind(R.id.txtDishName)AppCompatTextView txtDishName;
    @Bind(R.id.txtDishDescription)AppCompatTextView txtDishDescription;
    @Bind(R.id.txtQuantity)AppCompatTextView txtQuantity;
    @Bind(R.id.txtDishTotal)AppCompatTextView txtDishTotal;

    /** OBJECTS FOR HOLDING THE DISH (MENU) DETAILS **/
    String MEAL_NAME;
    String MEAL_DESCRIPTION;
    byte[] MEAL_IMAGE;
    String CURRENCY_CODE;
    String MEAL_PRICE;
    String ORDER_TOTAL;
    String MEAL_TYPE;
    private int MEAL_QUANTITY;

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fe_menu_details);
        ButterKnife.bind(this);

//        txtQuantity.setFilters(new InputFilter[]{new OrderValueMinMax("1", "20")});
        MEAL_QUANTITY = Integer.parseInt(txtQuantity.getText().toString());

        /** GET THE STAFF ID **/
        String[] arrStaff = getApp().getLogin();
        STAFF_ID = arrStaff[0];
        if (STAFF_ID != null)   {
//            Log.e("STAFF ID", STAFF_ID);
        }

        /** GET THE CURRENCY CODE **/
        String[] arrCurrency = getApp().getCurrency();
        CURRENCY_CODE = arrCurrency[3];

        /** GET THE INCOMING DISH ID **/
        getIncomingDishDetails();
    }

    /** GET THE INCOMING DISH ID **/
    private void getIncomingDishDetails() {
        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey("DISH_ID") && bundle.containsKey("TABLE_NO"))  {
            INCOMING_DISH_ID = bundle.getString("DISH_ID");
            INCOMING_TABLE_NO = bundle.getString("TABLE_NO");
            if (INCOMING_DISH_ID != null)   {

                /** FETCH THE DISH DETAILS **/
                new fetchDishDetails().execute();

            } else {
                // TODO: SHOW AN ERROR
            }
        } else {
            // TODO: SHOW AN ERROR
        }
    }

    /** FETCH THE DISH DETAILS **/
    private class fetchDishDetails extends AsyncTask<Void, Void, Void> {

        /** A CURSOR INSTANCE **/
        Cursor cursor;

        @Override
        protected void onPreExecute() {

            /** INSTANTIATE THE DATABASE HELPER CLASS **/
            db = new DBResto(MenuDetails.this);

            /** SHOW THE PROGRESS BAR WHILE FETCHING DISH DETAILS **/
            linlaHeaderProgress.setVisibility(View.VISIBLE);

            /** CONSTRUCT THE QUERY TO FETCH THE DISH DETAILS FROM THE DATABASE **/
            String strQueryData = "SELECT * FROM " + db.MENU + " WHERE " + db.MENU_ID + " = " + INCOMING_DISH_ID;

            /** CAST THE QUERY IN THE CURSOR TO FETCH THE RESULTS **/
            cursor = db.selectAllData(strQueryData);
        }

        @Override
        protected Void doInBackground(Void... params) {

            /** CHECK THAT THE DATABASE QUERY RETURNED SOME RESULTS **/
            if (cursor != null && cursor.getCount() != 0)	{

                /** LOOP THROUGH THE RESULT SET AND PARSE NECESSARY INFORMATION **/
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                    /** GET THE MEAL NAME **/
                    if (cursor.getString(cursor.getColumnIndex(db.MENU_NAME)) != null)	{
                        MEAL_NAME = cursor.getString(cursor.getColumnIndex(db.MENU_NAME));
//						Log.e("MEAL NAME", MEAL_NAME);
                    } else {
                        MEAL_NAME = null;
                    }

                    /** GET THE MEAL DESCRIPTION **/
                    if (cursor.getString(cursor.getColumnIndex(db.MENU_DESCRIPTION)) != null)	{
                        MEAL_DESCRIPTION = cursor.getString(cursor.getColumnIndex(db.MENU_DESCRIPTION));
//						Log.e("MEAL DESCRIPTION", MEAL_DESCRIPTION);
                    } else {
                        MEAL_DESCRIPTION = null;
                    }

                    /** GET THE MEAL IMAGE **/
                    if (cursor.getBlob(cursor.getColumnIndex(db.MENU_IMAGE)) != null)	{
                        MEAL_IMAGE = cursor.getBlob(cursor.getColumnIndex(db.MENU_IMAGE));
                    } else {
                        MEAL_IMAGE = null;
                    }

                    /** GET THE MEAL PRICE **/
                    if (cursor.getString(cursor.getColumnIndex(db.MENU_PRICE)) != null)	{
                        MEAL_PRICE = cursor.getString(cursor.getColumnIndex(db.MENU_PRICE));
//						Log.e("MEAL PRICE", MEAL_PRICE);
                    } else {
                        MEAL_PRICE = null;
                    }

                    /** THE MENU TYPE **/
                    if (cursor.getString(cursor.getColumnIndex(db.MENU_TYPE)) != null)	{
                        MEAL_TYPE = cursor.getString(cursor.getColumnIndex(db.MENU_TYPE));
                        Log.e("MEAL TYPE", MEAL_TYPE);
                    } else {
                        MEAL_TYPE = "NA";
                    }
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            /** CLOSE THE CURSOR **/
            if (cursor != null && !cursor.isClosed())	{
                cursor.close();
            }

            /** CLOSE THE DATABASE **/
            db.close();

            /** SET THE MEAL NAME **/
            if (MEAL_NAME != null)	{
                txtDishName.setText(MEAL_NAME.toUpperCase());
            } else {
                String strMessage = "There was a problem fetching the Dish Details. Sorry. :-(";
                Toast.makeText(getApplicationContext(), strMessage, Toast.LENGTH_SHORT).show();

				/* FINISH THE ACTIVITY*/
                finish();
            }

            /** SET THE MEAL PRICE **/
            if (MEAL_PRICE != null) {
                txtDishPrice.setText(CURRENCY_CODE + " " + MEAL_PRICE);
            }

            /** CALCULATE THE ORDER TOTAL **/
            Double dblMealPrice = Double.valueOf(MEAL_PRICE);
            Double dblOrderTotal = dblMealPrice * MEAL_QUANTITY;
            ORDER_TOTAL = String.valueOf(dblOrderTotal);

            /** SET THE MEAL DESCRIPTION **/
            if (MEAL_DESCRIPTION != null)	{
                txtDishDescription.setText(MEAL_DESCRIPTION);
                txtDishDescription.setVisibility(View.VISIBLE);
            } else {
				/* SET EMPTY TEXT ("") AND HIDE THE DESCRIPTION TEXT VIEW */
                txtDishDescription.setVisibility(View.GONE);
            }

            /** SET THE MEAL IMAGE **/
            Bitmap bmpThumb = BitmapFactory.decodeByteArray(MEAL_IMAGE, 0, MEAL_IMAGE.length);
            imgvwDish.setImageBitmap(bmpThumb);

            /** SET THE MEAL TYPE **/
            if (!MEAL_TYPE.equals("NA")) {
                if (MEAL_TYPE.equals("Veg"))   {
                    imgvwMealType.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.veg_mark));
                } else if (MEAL_TYPE.equals("Non-veg"))    {
                    imgvwMealType.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_veg_mark));
                }
            } else {
                imgvwMealType.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_veg_mark));
            }

            /** SET THE RATING **/
            txtRating.setText("4.7" + getResources().getString(R.string.star_symbol));

            /** HIDE THE PROGRESS BAR AFTER FETCHING DISH DETAILS **/
            linlaHeaderProgress.setVisibility(View.GONE);
        }
    }

    /** INCREASE THE QUANTITY **/
    @OnClick(R.id.txtQtyPlus) protected void increaseQuantity() {
        /** INCREMENT THE QUANTITY BY 1 **/
        if (MEAL_QUANTITY == 20)  {
            Toast.makeText(getApplicationContext(), "You cannot add more than 20 dishes", Toast.LENGTH_LONG).show();
        } else {
            MEAL_QUANTITY++;

            /** SET THE NEW ORDER QUANTITY **/
            txtQuantity.setText(String.valueOf(MEAL_QUANTITY));

            /** UPDATE THE DISH PRICE **/
            String strDishPrice = MEAL_PRICE;
            double dishBasePrice = Double.parseDouble(strDishPrice);
            double dishTotal = dishBasePrice * MEAL_QUANTITY;
            txtDishTotal.setText(String.valueOf(dishTotal));
            ORDER_TOTAL = txtDishTotal.getText().toString();
        }
    }

    /** REDUCE THE QUANTITY **/
    @OnClick(R.id.txtQtyMinus) protected void reduceQuantity()  {
        /** DECREASE THE QUANTITY BY 1 **/
        if (MEAL_QUANTITY > 1) {
            MEAL_QUANTITY--;

            /** SET THE NEW ORDER QUANTITY **/
            txtQuantity.setText(String.valueOf(MEAL_QUANTITY));

            /** UPDATE THE DISH PRICE **/
            String strDishPrice = MEAL_PRICE;
            double dishBasePrice = Double.parseDouble(strDishPrice);
            double dishTotal = dishBasePrice * MEAL_QUANTITY;
            txtDishTotal.setText(String.valueOf(dishTotal));
            ORDER_TOTAL = txtDishTotal.getText().toString();
        }
    }

    /** ADD THE DISH TO THE CART **/
    @OnClick(R.id.txtAddToCart) protected void addDishToCart ()   {
        /** INSTANTIATE THE DATABASE INSTANCE **/
        db = new DBResto(MenuDetails.this);

        /** SHOW A PROGRESS DIALOG WHILE ADDING DISH TO CART **/
        ProgressDialog dialog = new ProgressDialog(MenuDetails.this);
        dialog.setCancelable(false);
        dialog.setMessage("Adding Dish to the Order Cart....");
        dialog.setIndeterminate(true);
        dialog.show();

        /** CHECK IF THE ORDER EXISTS IN THE ORDER CART **/
        db = new DBResto(MenuDetails.this);
        String strQueryData =
                "SELECT * FROM " + db.ORDER_CART + " WHERE " + db.ORDER_MENU_ID + " = " + INCOMING_DISH_ID +
                        " AND " + db.ORDER_STATUS + " = 0 AND " + db.ORDER_TABLE_ID + " = " + INCOMING_TABLE_NO;
//                Log.e("QUERY", strQueryData);
        Cursor cursor = db.selectAllData(strQueryData);
        if (cursor.getCount() != 0) {
            Toast.makeText(
                    getApplicationContext(),
                    "Dish already present in your Order Cart. Change the quantity from the Order Cart on the previous page",
                    Toast.LENGTH_LONG).show();
        } else {
            /** ADD THE DATA TO THE ORDER CART **/
            db.addOrder(
                    Integer.valueOf(INCOMING_TABLE_NO),
                    Integer.valueOf(INCOMING_DISH_ID),
                    MEAL_PRICE,
                    MEAL_QUANTITY,
                    ORDER_TOTAL,
                    false);

            /** SHOW ORDER ADDED NOTE **/
            Toast.makeText(getApplicationContext(), "Dish added successfully", Toast.LENGTH_LONG).show();

            /** FINISH THE ACTIVITY WITH "RESULT OK" **/
            Intent orderAdded = new Intent();
            setResult(RESULT_OK, orderAdded);
            finish();
        }

        /** DISMISS THE DIALOG **/
        dialog.dismiss();

        /** CLOSE THE DATABASE CONNECTION **/
        db.close();
    }

    /** CLOSE THE DISH WITHOUT ADDING TO CART **/
    @OnClick(R.id.txtCancel) protected void cancelDish()    {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}