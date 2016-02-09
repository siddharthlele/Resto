package com.dzinesunlimited.resto.frontend.menu;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dzinesunlimited.resto.R;
import com.dzinesunlimited.resto.utils.TypefaceSpan;
import com.dzinesunlimited.resto.utils.db.DBResto;
import com.dzinesunlimited.resto.utils.helpers.pojos.frontend.OrderData;

import java.util.ArrayList;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OrderCart extends AppCompatActivity {

    /** GET THE INCOMING TABLE ID **/
    private String INCOMING_TABLE_ID = null;

    /** THE DATABASE HELPER INSTANCE **/
    DBResto db;

    /** DECLARE THE LAYOUT ELEMENTS **/
    @Bind(R.id.linlaHeaderProgress)LinearLayout linlaHeaderProgress;
    @Bind(R.id.orderCart)RecyclerView orderCart;
    @Bind(R.id.linlaEmpty)LinearLayout linlaEmpty;
    @Bind(R.id.txtOrderTotal)AppCompatTextView txtOrderTotal;
    
    /** THE ADAPTER AND THE ARRAYLIST **/
    OrderAdapter adapter;
    ArrayList<OrderData> arrOrders = new ArrayList<>();

    /** THE ORDER TOTAL **/
    Double ORDER_TOTAL;

    /** TOTAL DISHES ORDERED (NOT QUANTITY OF DISHES) **/
    int orderTotal = 0;

    /** (ADAPTER) ORDER QUANTITY **/
    int dishQuantity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fe_order_cart);
        ButterKnife.bind(this);

        //TODO: REMOVE AFTER TESTING (FIND A FUCKING SOLUTION!!!!) !!!!
        /** TESTING A SESSION GENERATOR **/
        UUID uuid = UUID.randomUUID();
        String sessionID = String.valueOf(uuid);
        Log.e("SESSION ID", sessionID);

        /***** CONFIGURE THE ACTIONBAR *****/
        configAB();

        /** CONFIGURE THE RECYCLER VIEW **/
        configRecyclerView();
        
        /** INSTANTIATE THE ADAPTER **/
        adapter = new OrderAdapter(OrderCart.this, arrOrders);

        /** FETCH THE INCOMING DATA **/
        fetchIncomingData();

        /** REFRESH THE CURRENT ORDER TOTAL **/
        refreshOrderTotal();
    }

    /** REFRESH THE CURRENT ORDER TOTAL **/
    private void refreshOrderTotal() {
        DBResto resto = new DBResto(OrderCart.this);
        String s = "SELECT SUM(" + db.ORDER_TOTAL + ") FROM " + db.ORDER_CART;
        Cursor cursor = resto.selectAllData(s);
        if (cursor != null && cursor.getCount() != 0)	{
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                ORDER_TOTAL = cursor.getDouble(0);
                txtOrderTotal.setText(String.valueOf(ORDER_TOTAL));
            }
        }
    }

    private class fetchCurrentOrders extends AsyncTask<Void, Void, Void>    {

        /** A CURSOR INSTANCE **/
        Cursor cursor;
        Cursor curMenu;

        /** A DATABASE INSTANCE FOR QUERYING THE MENU TABLE **/
        DBResto dbMenus;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /** SHOW PROGRESS WHILE FETCHING CURRENT ORDERS **/
            linlaHeaderProgress.setVisibility(View.VISIBLE);

            /** INSTANTIATE THE DATABASE **/
            db = new DBResto(OrderCart.this);

            /** CONSTRUCT THE QUERY **/
            String s = "SELECT * FROM " + db.ORDER_CART + " WHERE " + db.ORDER_TABLE_ID + " = " + INCOMING_TABLE_ID;

            /** FETCH THE RESULT FROM THE DATABASE **/
            cursor = db.selectAllData(s);
//            Log.e("DUMP", DatabaseUtils.dumpCursorToString(cursor));
        }

        @Override
        protected Void doInBackground(Void... params) {

            /** CHECK THAT THE DATABASE QUERY RETURNED SOME RESULTS **/
            if (cursor != null && cursor.getCount() != 0)	{

                /** AN INSTANCE OF THE OrderData HELPER CLASS **/
                OrderData data;

                /** LOOP THROUGH THE RESULT SET AND PARSE NECESSARY INFORMATION **/
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                    /***** INSTANTIATE THE OrderData INSTANCE "data" *****/
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
                        dbMenus = new DBResto(OrderCart.this);
                        String strQuery = "SELECT * FROM " + dbMenus.MENU + " WHERE " + dbMenus.MENU_ID + " = " + ORDER_MENU_ID;
                        curMenu = dbMenus.selectAllData(strQuery);
                        if (curMenu != null && curMenu.getCount() != 0)	{
                            for (curMenu.moveToFirst(); !curMenu.isAfterLast(); curMenu.moveToNext()) {

                                /** GET THE MEAL NAME **/
                                if (curMenu.getString(curMenu.getColumnIndex(dbMenus.MENU_NAME)) != null)	{
                                    String strMealName = curMenu.getString(curMenu.getColumnIndex(dbMenus.MENU_NAME));
                                    data.setMenuName(strMealName);
                                } else {
                                    data.setMenuName(null);
                                }

                                /** GET THE MEAL PRICE **/
                                if (curMenu.getString(curMenu.getColumnIndex(dbMenus.MENU_PRICE)) != null)	{
                                    String strMealPrice = curMenu.getString(curMenu.getColumnIndex(dbMenus.MENU_PRICE));
                                    data.setMenuPrice(strMealPrice);
                                } else {
                                    data.setMenuPrice(null);
                                }

                                /** GET THE MEAL IMAGE **/
                                if (curMenu.getBlob(curMenu.getColumnIndex(dbMenus.MENU_IMAGE)) != null)	{
                                    byte[] bytImage = curMenu.getBlob(curMenu.getColumnIndex(dbMenus.MENU_IMAGE));
                                    data.setMenuImage(bytImage);
                                } else {
                                    data.setMenuImage(null);
                                }
                            }
                        }
                        data.setMenuID(ORDER_MENU_ID);
                    } else {
                        data.setMenuID(null);
                        data.setMenuName(null);
                        data.setMenuImage(null);
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
                        data.setOrderStatus(Boolean.parseBoolean(ORDER_STATUS));
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

                    /** ADD THE COLLECTED DATA TO THE ARRAYLIST **/
                    arrOrders.add(data);

                    /** SET THE TOTAL ORDERS **/
                    orderTotal = arrOrders.size();
                }

                /** SHOW THE LISTVIEW AND HIDE THE EMPTY CONTAINER **/
                Runnable run = new Runnable() {

                    @Override
                    public void run() {

                        /** SHOW THE RECYCLERVIEW **/
                        orderCart.setVisibility(View.VISIBLE);

                        /** HIDE THE EMPTY DATA SET IMAGE AND TEXT **/
                        linlaEmpty.setVisibility(View.GONE);
                    }
                }; runOnUiThread(run);

            } else {

                /** SHOW THE EMPTY CONTAINER AND HIDE THE LISTVIEW **/
                Runnable run = new Runnable() {

                    @Override
                    public void run() {

                        /** SHOW THE EMPTY DATA SET IMAGE AND TEXT **/
                        linlaEmpty.setVisibility(View.VISIBLE);

                        /** HIDE THE RECYCLERVIEW **/
                        orderCart.setVisibility(View.GONE);
                    }
                }; runOnUiThread(run);
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

            /** SET THE ADAPTER TO THE RECYCLERVIEW **/
            orderCart.setAdapter(adapter);

            /** HIDE THE PROGRESS AFTER FETCHING CURRENT ORDERS **/
            linlaHeaderProgress.setVisibility(View.GONE);
        }
    }

    private void fetchIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey("TABLE_NO")) {
            INCOMING_TABLE_ID = bundle.getString("TABLE_NO");
            if (INCOMING_TABLE_ID != null)  {
                new fetchCurrentOrders().execute();
            } else {
                //TODO: SHOW AN ERROR
            }
        } else {
            //TODO: SHOW AN ERROR
        }
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {

        Toolbar myToolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);

        String strTitle = getResources().getString(R.string.order_cart_title);
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(
                OrderCart.this, "RobotoCondensed-Regular.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(s);
        getSupportActionBar().setSubtitle(null);
    }

    /** CONFIGURE THE RECYCLER VIEW **/
    private void configRecyclerView() {
        orderCart.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(OrderCart.this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        orderCart.setLayoutManager(llm);
    }

    /** THE CATEGORIES CUSTOM ADAPTER **/
    private class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrdersVH> {

        /** AN ACTIVITY INSTANCE **/
        Activity activity;

        /** ARRAYLIST TO GET DATA FROM THE ACTIVITY **/
        ArrayList<OrderData> arrAdapOrders;

        public OrderAdapter(Activity activity, ArrayList<OrderData> arrAdapOrders) {

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
        public void onBindViewHolder(final OrdersVH holder, final int position) {
            final OrderData data = arrAdapOrders.get(position);

            /** GET THE MENU NAME **/
            final String SUB_MENU_NAME = data.getMenuName();
            holder.txtMenuName.setText(SUB_MENU_NAME);

            /** GET THE SUB MENU THUMBNAIL **/
            byte[] CATEGORY_THUMB = data.getMenuImage();
            if (CATEGORY_THUMB != null)	{
                Bitmap bmpThumb = BitmapFactory.decodeByteArray(CATEGORY_THUMB, 0, CATEGORY_THUMB.length);
                holder.imgvwMenu.setImageBitmap(bmpThumb);
            }

            /** SET THE DEFAULT QUANTITY **/
            holder.txtQuantity.setText(String.valueOf(dishQuantity));

            /** SET THE DISH PRICE **/
            String strDishPrice = data.getMenuPrice();
            double dishBasePrice = Double.parseDouble(strDishPrice);
            double dishTotal = dishBasePrice * dishQuantity;
            holder.txtDishTotal.setText(String.valueOf(dishTotal));

            /** INCREASE THE DISH QUANTITY **/
            holder.txtQtyPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /** INCREMENT THE QUANTITY BY 1 **/
                    dishQuantity++;

                    /** SET THE NEW ORDER QUANTITY **/
                    holder.txtQuantity.setText(String.valueOf(dishQuantity));

                    /** UPDATE THE DISH PRICE **/
                    String strDishPrice = data.getMenuPrice();
                    double dishBasePrice = Double.parseDouble(strDishPrice);
                    double dishTotal = dishBasePrice * dishQuantity;
                    holder.txtDishTotal.setText(String.valueOf(dishTotal));

                    /** UPDATE THE ORDER IN THE ORDER CART TABLE **/
                    db = new DBResto(activity);
                    db.updateOrderQuantity(data.getOrderID(), dishQuantity);
                    db.close();

                    /** REFRESH THE ORDER TOTAL **/
                    refreshOrderTotal();
                }
            });

            /** REDUCE THE DISH QUANTITY **/
            holder.txtQtyMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /** DECREASE THE QUANTITY BY 1 **/
                    if (dishQuantity > 1) {
                        dishQuantity--;

                        /** SET THE NEW ORDER QUANTITY **/
                        holder.txtQuantity.setText(String.valueOf(dishQuantity));

                        /** UPDATE THE DISH PRICE **/
                        String strDishPrice = data.getMenuPrice();
                        double dishBasePrice = Double.parseDouble(strDishPrice);
                        double dishTotal = dishBasePrice * dishQuantity;
                        holder.txtDishTotal.setText(String.valueOf(dishTotal));

                        /** UPDATE THE ORDER IN THE ORDER CART TABLE **/
                        db = new DBResto(activity);
                        db.updateOrderQuantity(data.getOrderID(), dishQuantity);
                        db.close();

                        /** REFRESH THE ORDER TOTAL **/
                        refreshOrderTotal();
                    }
                }
            });

            /** REMOVE THE DISH FROM THE ORDER CART **/
//            holder.txtRemoveDish.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                    Log.e("STATUS", String.valueOf(data.isOrderStatus()));
//                    removeAt(position);
//
//                    /** DELETE THE ORDER FROM THE DATABASE **/
//                    if (!data.isOrderStatus())   {
//                        /* OPEN THE DATABASE CONNECTION */
//                        db = new DBResto(activity);
//
//                        /* DELETE THE ORDER CART RECORD */
//                        db.deleteOrder(data.getOrderID());
//
//                        /* CLOSE THE DATABASE CONNECTION */
//                        db.close();
//
//                        /** REFRESH THE ORDER TOTAL **/
//                        refreshOrderTotal();
//                    }
//                }
//            });
        }

        private void removeAt(int position) {
            arrAdapOrders.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, arrAdapOrders.size());
        }

        @Override
        public OrdersVH onCreateViewHolder(ViewGroup parent, int i) {

            View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.fe_order_cart_item_test, parent, false);

            return new OrdersVH(itemView);
        }

        public class OrdersVH extends RecyclerView.ViewHolder	{

//            AppCompatTextView txtRemoveDish;
            AppCompatImageView imgvwMenu;
            AppCompatTextView txtMenuName;
            AppCompatTextView txtQtyMinus;
            AppCompatTextView txtQuantity;
            AppCompatTextView txtQtyPlus;
            AppCompatTextView txtDishTotal;

            public OrdersVH(View v) {
                super(v);
//                txtRemoveDish = (AppCompatTextView) v.findViewById(R.id.txtRemoveDish);
                imgvwMenu = (AppCompatImageView) v.findViewById(R.id.imgvwMenu);
                txtMenuName = (AppCompatTextView) v.findViewById(R.id.txtMenuName);
                txtQtyMinus = (AppCompatTextView) v.findViewById(R.id.txtQtyMinus);
                txtQuantity = (AppCompatTextView) v.findViewById(R.id.txtQuantity);
                txtQtyPlus = (AppCompatTextView) v.findViewById(R.id.txtQtyPlus);
                txtDishTotal = (AppCompatTextView) v.findViewById(R.id.txtDishTotal);
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}