package com.dzinesunlimited.resto.frontend.billing;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dzinesunlimited.resto.R;
import com.dzinesunlimited.resto.utils.TypefaceSpan;
import com.dzinesunlimited.resto.utils.db.DBResto;
import com.dzinesunlimited.resto.utils.helpers.pojos.frontend.OrderData;
import com.epson.epos2.printer.Printer;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class BillingActivity extends AppCompatActivity {

    /** GET THE INCOMING TABLE ID **/
    private String INCOMING_TABLE_ID = null;

    /** THE DATABASE HELPER INSTANCE **/
    DBResto db;

    /** THE PRINTER INSTANCE **/
    private Printer mPrinter = null;
    private String PRINTER_IP = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @Bind(R.id.linlaHeaderProgress)LinearLayout linlaHeaderProgress;
    @Bind(R.id.orderCart)RecyclerView orderCart;
    @Bind(R.id.linlaEmpty)LinearLayout linlaEmpty;
    @Bind(R.id.txtGrandTotal)AppCompatTextView txtGrandTotal;

    /** THE ADAPTER AND THE ARRAYLIST **/
    BillingAdapter adapter;
    ArrayList<OrderData> arrOrders = new ArrayList<>();

    /** THE GRAND TOTAL **/
    Double GRAND_TOTAL;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fe_billing);
        ButterKnife.bind(this);

        /***** CONFIGURE THE ACTIONBAR *****/
        configAB();

        /** CONFIGURE THE RECYCLER VIEW **/
        configRecyclerView();

        /** INSTANTIATE THE ADAPTER **/
        adapter = new BillingAdapter(BillingActivity.this, arrOrders);

        /** GET THE INCOMING TABLE NUMBER **/
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("TABLE_NO"))    {
            INCOMING_TABLE_ID = bundle.getString("TABLE_NO");
            Toast.makeText(getApplicationContext(), INCOMING_TABLE_ID, Toast.LENGTH_SHORT).show();
            if (INCOMING_TABLE_ID != null)  {

                /** CURSOR INSTANCE FOR FETCHING THE MENU DETAILS **/
                Cursor curMenu;

                /** GET THE CONFIRMED ORDERS **/
                db = new DBResto(BillingActivity.this);
                String s = "SELECT * FROM " + db.ORDER_CART + " WHERE " + db.ORDER_TABLE_ID + " = " + INCOMING_TABLE_ID + " AND " + db.ORDER_STATUS + " = 1";
                Cursor cursor = db.selectAllData(s);
                Log.e("BILLING", DatabaseUtils.dumpCursorToString(cursor));
                if (cursor != null && cursor.getCount() != 0)	{
                    /** AN INSTANCE OF THE OrderData HELPER CLASS **/
                    OrderData data;
                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                        /***** INSTANTIATE THE KOTPrint INSTANCE "print" *****/
                        data = new OrderData();

                        /** GET THE ORDER ID **/
                        if (cursor.getString(cursor.getColumnIndex(db.ORDER_CART_ID)) != null)	{
                            String ORDER_CART_ID = cursor.getString(cursor.getColumnIndex(db.ORDER_CART_ID));
                            data.setOrderID(ORDER_CART_ID);
                        } else {
                            data.setOrderID(null);
                        }

                        /** GET THE TABLE ID **/
                        if (cursor.getString(cursor.getColumnIndex(db.ORDER_TABLE_ID)) != null)	{
                            String ORDER_TABLE_ID = cursor.getString(cursor.getColumnIndex(db.ORDER_TABLE_ID));
                            data.setTableID(ORDER_TABLE_ID);
                        } else {
                            data.setTableID(null);
                        }

                        /** GET THE MENU ID **/
                        if (cursor.getString(cursor.getColumnIndex(db.ORDER_MENU_ID)) != null)	{
                            String ORDER_MENU_ID = cursor.getString(cursor.getColumnIndex(db.ORDER_MENU_ID));

                            /** QUERY THE MENU TABLE **/
                            db = new DBResto(BillingActivity.this);
                            String strQuery = "SELECT * FROM " + db.MENU + " WHERE " + db.MENU_ID + " = " + ORDER_MENU_ID;
                            curMenu = db.selectAllData(strQuery);
                            if (curMenu != null && curMenu.getCount() != 0)	{
                                for (curMenu.moveToFirst(); !curMenu.isAfterLast(); curMenu.moveToNext()) {

                                    /** GET THE MEAL CATEGORY ID **/
                                    if (curMenu.getString(curMenu.getColumnIndex(db.MENU_CATEGORY_ID)) != null)	{
                                        String strMenuCatID = curMenu.getString(curMenu.getColumnIndex(db.MENU_CATEGORY_ID));
                                        String qryCategoryTaxes = "SELECT * FROM " + db.CAT_TAXES + " WHERE " + db.TRANS_CAT_ID + " = " + strMenuCatID;
                                        Cursor cursor1 = db.selectAllData(qryCategoryTaxes);
                                        if (cursor1 != null && cursor1.getCount() != 0) {
                                            for (cursor1.moveToFirst(); !cursor1.isAfterLast(); cursor1.moveToNext()) {

                                                /** GET THE TAX ID**/
                                                if (cursor1.getString(cursor1.getColumnIndex(db.TRANS_TAX_ID)) != null)	{
                                                    String strTaxID = cursor1.getString(cursor1.getColumnIndex(db.TRANS_TAX_ID));
                                                }
                                            }
                                        }
                                    }

                                    /** GET THE MEAL NAME **/
                                    if (curMenu.getString(curMenu.getColumnIndex(db.MENU_NAME)) != null)	{
                                        String strMealName = curMenu.getString(curMenu.getColumnIndex(db.MENU_NAME));
                                        data.setMenuName(strMealName);
                                    } else {
                                        data.setMenuName(null);
                                    }

                                    /** GET THE MEAL PRICE **/
                                    if (curMenu.getString(curMenu.getColumnIndex(db.MENU_PRICE)) != null)	{
                                        String strMealPrice = curMenu.getString(curMenu.getColumnIndex(db.MENU_PRICE));
                                        data.setMenuPrice(strMealPrice);
                                    } else {
                                        data.setMenuPrice(null);
                                    }
                                }
                            }
                            data.setMenuID(ORDER_MENU_ID);
                        } else {
                            data.setMenuID(null);
                            data.setMenuName(null);
                        }

                        /** GET THE QUANTITY **/
                        if (cursor.getString(cursor.getColumnIndex(db.ORDER_QUANTITY)) != null)	{
                            String ORDER_QUANTITY = cursor.getString(cursor.getColumnIndex(db.ORDER_QUANTITY));
                            data.setOrderQuantity(ORDER_QUANTITY);
                        } else {
                            data.setOrderQuantity(null);
                        }

                        /** GET THE STATUS **/
                        if (cursor.getString(cursor.getColumnIndex(db.ORDER_STATUS)) != null)	{
                            String ORDER_STATUS = cursor.getString(cursor.getColumnIndex(db.ORDER_STATUS));
                            if (ORDER_STATUS.equals("0"))   {
                                data.setOrderStatus(false);
                            } else if (ORDER_STATUS.equals("1"))    {
                                data.setOrderStatus(true);
                            }
//                            Log.e("METHOD STATUS", String.valueOf(Boolean.parseBoolean(ORDER_STATUS)));
                        } else {
                            data.setOrderStatus(false);
                        }

                        /** GET THE TIMESTAMP **/
                        if (cursor.getString(cursor.getColumnIndex(db.ORDER_TIMESTAMP)) != null)	{
                            String ORDER_TIMESTAMP = cursor.getString(cursor.getColumnIndex(db.ORDER_TIMESTAMP));
                            data.setOrderTimestamp(ORDER_TIMESTAMP);
                        } else {
                            data.setOrderTimestamp(null);
                        }

                        arrOrders.add(data);
                    }

                    /** CLOSE THE CURSOR **/
                    if (cursor != null && !cursor.isClosed())	{
                        cursor.close();
                    }

                    /** CLOSE THE DATABASE **/
                    db.close();

                    /** SET THE ADAPTER TO THE RECYCLERVIEW **/
                    orderCart.setAdapter(adapter);

                    /** HIDE THE PROGRESS AFTER FETCHING CURRENT ORDERS **/
                    linlaHeaderProgress.setVisibility(View.GONE);

                    /** REFRESH THE CURRENT GRAND TOTAL **/
                    refreshGrandTotal();

                    /** SHOW THE RECYCLERVIEW **/
                    orderCart.setVisibility(View.VISIBLE);

                    /** HIDE THE EMPTY DATA SET IMAGE AND TEXT **/
                    linlaEmpty.setVisibility(View.GONE);
                } else {
                    /** SHOW THE EMPTY DATA SET IMAGE AND TEXT **/
                    linlaEmpty.setVisibility(View.VISIBLE);

                    /** HIDE THE RECYCLERVIEW **/
                    orderCart.setVisibility(View.GONE);
                }
            } else {
                //// TODO: 2/28/2016
            }
        } else {
            //// TODO: 2/28/2016
        }

    }

    /** REFRESH THE CURRENT GRAND TOTAL **/
    private void refreshGrandTotal() {
        DBResto resto = new DBResto(BillingActivity.this);
        String s =
                "SELECT SUM(" + db.ORDER_TOTAL + ") FROM " + db.ORDER_CART +
                        " WHERE " + db.ORDER_TABLE_ID + " = " + INCOMING_TABLE_ID + " AND " + db.ORDER_STATUS + " = 1";
//        Log.e("CART QUERY", s);
        Cursor cursor = resto.selectAllData(s);
//        Log.e("ORDER TOTAL DUMP", DatabaseUtils.dumpCursorToString(cursor));
        if (cursor != null && cursor.getCount() != 0)	{
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                GRAND_TOTAL = cursor.getDouble(0);
                txtGrandTotal.setText(String.valueOf(GRAND_TOTAL));
            }
        }
    }

    /** CONFIGURE THE RECYCLER VIEW **/
    private void configRecyclerView() {
        orderCart.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(BillingActivity.this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        orderCart.setLayoutManager(llm);
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {

        Toolbar myToolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);

        String strTitle = getResources().getString(R.string.checkout_title);
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(
                BillingActivity.this, "RobotoCondensed-Regular.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(s);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return false;
    }

    private class BillingAdapter extends RecyclerView.Adapter<BillingAdapter.BillsVH> {

        /** AN ACTIVITY INSTANCE **/
        Activity activity;

        /** ARRAYLIST TO GET DATA FROM THE ACTIVITY **/
        ArrayList<OrderData> arrAdapOrders;

        public BillingAdapter(Activity activity, ArrayList<OrderData> arrAdapOrders) {

            /** CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE **/
            this.activity = activity;

            /** CAST THE CONTENTS OF THE ARRAYLIST IN THE METHOD TO THE LOCAL INSTANCE **/
            this.arrAdapOrders = arrAdapOrders;
        }

        @Override
        public int getItemCount() {
            return arrAdapOrders.size();
        }

        @Override
        public void onBindViewHolder(final BillsVH holder, final int position) {
            final OrderData data = arrAdapOrders.get(position);

            /** GET THE MENU NAME **/
            final String SUB_MENU_NAME = data.getMenuName();
            holder.txtMenuName.setText(SUB_MENU_NAME);

            /** SET THE DISH QUANTITY **/
            holder.txtQuantity.setText(String.valueOf(data.getOrderQuantity()));

            /** SET THE DISH PRICE **/
            String strDishPrice = data.getMenuPrice();
            double dishBasePrice = Double.parseDouble(strDishPrice);
            double dishTotal = dishBasePrice * Integer.parseInt(holder.txtQuantity.getText().toString());
            holder.txtDishTotal.setText(String.valueOf(dishTotal));
        }

        @Override
        public BillsVH onCreateViewHolder(ViewGroup parent, int i) {

            View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.fe_billing_item, parent, false);

            return new BillsVH(itemView);
        }

        public class BillsVH extends RecyclerView.ViewHolder	{

            AppCompatTextView txtMenuName;
            AppCompatTextView txtQuantity;
            AppCompatTextView txtDishTotal;

            public BillsVH(View v) {
                super(v);
                txtMenuName = (AppCompatTextView) v.findViewById(R.id.txtMenuName);
                txtQuantity = (AppCompatTextView) v.findViewById(R.id.txtQuantity);
                txtDishTotal = (AppCompatTextView) v.findViewById(R.id.txtDishTotal);
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}