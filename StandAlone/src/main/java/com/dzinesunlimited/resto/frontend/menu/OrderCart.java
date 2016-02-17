package com.dzinesunlimited.resto.frontend.menu;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.dzinesunlimited.resto.R;
import com.dzinesunlimited.resto.ShowMsg;
import com.dzinesunlimited.resto.SpnModelsItem;
import com.dzinesunlimited.resto.utils.TypefaceSpan;
import com.dzinesunlimited.resto.utils.db.DBResto;
import com.dzinesunlimited.resto.utils.helpers.pojos.frontend.OrderData;
import com.epson.epos2.Epos2Exception;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OrderCart extends AppCompatActivity implements ReceiveListener {

    /** GET THE INCOMING TABLE ID **/
    private String INCOMING_TABLE_ID = null;

    /** THE DATABASE HELPER INSTANCE **/
    DBResto db;

    /** THE PRINTER INSTANCE **/
    private Printer mPrinter = null;

    /** DECLARE THE LAYOUT ELEMENTS **/
    @Bind(R.id.linlaHeaderProgress)LinearLayout linlaHeaderProgress;
    @Bind(R.id.orderCart)RecyclerView orderCart;
    @Bind(R.id.linlaEmpty)LinearLayout linlaEmpty;
    @Bind(R.id.txtOrderTotal)AppCompatTextView txtOrderTotal;
    @Bind(R.id.btnConfirmOrder)AppCompatButton btnConfirmOrder;
    @OnClick(R.id.btnConfirmOrder) protected void PrintKOT() {
        /** PRINT THE KOT **/
        printKOT();
    }

    /** PRINT THE KOT AND CONFIRM THE ORDER (UPDATE ORDER STATUS) **/
    private void printKOT() {
        btnConfirmOrder.setEnabled(false);
        if (!runPrintReceiptSequence()) {
            btnConfirmOrder.setEnabled(true);
        }
    }

    private boolean runPrintReceiptSequence() {
        if (!initializeObject()) {
            return false;
        }

        if (!createKOTData()) {
            finalizeObject();
            return false;
        }

        if (!printData()) {
            finalizeObject();
            return false;
        }

        return true;
    }

    private boolean printData() {
        if (mPrinter == null) {
            return false;
        }

        if (!connectPrinter()) {
            return false;
        }

        PrinterStatusInfo status = mPrinter.getStatus();

        dispPrinterWarnings(status);

        if (!isPrintable(status)) {
            ShowMsg.showMsg(makeErrorMessage(status), this);
            try {
                mPrinter.disconnect();
            }
            catch (Exception ex) {
                // Do nothing
            }
            return false;
        }

        try {
            mPrinter.sendData(Printer.PARAM_DEFAULT);
        }
        catch (Exception e) {
            ShowMsg.showException(e, "sendData", this);
            try {
                mPrinter.disconnect();
            }
            catch (Exception ex) {
                // Do nothing
            }
            return false;
        }

        return true;
    }

    private boolean isPrintable(PrinterStatusInfo status) {
        if (status == null) {
            return false;
        }

        if (status.getConnection() == Printer.FALSE) {
            return false;
        }
        else if (status.getOnline() == Printer.FALSE) {
            return false;
        }
        else {
            //print available
        }

        return true;
    }

    private boolean connectPrinter() {
        boolean isBeginTransaction = false;

        if (mPrinter == null) {
            return false;
        }

        try {
            mPrinter.connect("TCP:192.168.11.200", Printer.PARAM_DEFAULT);
        }
        catch (Exception e) {
            ShowMsg.showException(e, "connect", this);
            return false;
//            Log.e("CONNECTION ERROR", e.getMessage().toString());
        }

        try {
            mPrinter.beginTransaction();
            isBeginTransaction = true;
        }
        catch (Exception e) {
            ShowMsg.showException(e, "beginTransaction", this);
        }

        if (isBeginTransaction == false) {
            try {
                mPrinter.disconnect();
            }
            catch (Epos2Exception e) {
                // Do nothing
                return false;
            }
        }

        return true;
    }

    private boolean initializeObject() {
        try {
            mPrinter = new Printer(0, 0, this);
        }
        catch (Exception e) {
            ShowMsg.showException(e, "Printer", this);
            return false;
        }

        mPrinter.setReceiveEventListener(OrderCart.this);

        return true;
    }

    private void finalizeObject() {
        if (mPrinter == null) {
            return;
        }

        mPrinter.clearCommandBuffer();

        mPrinter.setReceiveEventListener(null);

        mPrinter = null;
    }

    private boolean createKOTData() {
//        try {
//            mPrinter.connect("TCP:192.168.11.200", Printer.PARAM_DEFAULT);
//        } catch (Epos2Exception e) {
//            e.printStackTrace();
//            Log.e("CONNECTION ERROR", e.getMessage().toString());
//        }

        String method = "";
        Bitmap logoData = BitmapFactory.decodeResource(getResources(), R.drawable.store);
        StringBuilder textData = new StringBuilder();
        final int barcodeWidth = 2;
        final int barcodeHeight = 100;

        if (mPrinter == null) {
            return false;
        }

        try {
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);

            method = "addImage";
            mPrinter.addImage(logoData, 0, 0,
                    logoData.getWidth(),
                    logoData.getHeight(),
                    Printer.COLOR_1,
                    Printer.MODE_MONO,
                    Printer.HALFTONE_DITHER,
                    Printer.PARAM_DEFAULT,
                    Printer.COMPRESS_AUTO);

            method = "addFeedLine";
            mPrinter.addFeedLine(1);
            textData.append("THE STORE 123 (555) 555 – 5555\n");
            textData.append("STORE DIRECTOR – John Smith\n");
            textData.append("\n");
            textData.append("7/01/07 16:58 6153 05 0191 134\n");
            textData.append("ST# 21 OP# 001 TE# 01 TR# 747\n");
            textData.append("------------------------------\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            textData.append("400 OHEIDA 3PK SPRINGF  9.99 R\n");
            textData.append("410 3 CUP BLK TEAPOT    9.99 R\n");
            textData.append("445 EMERIL GRIDDLE/PAN 17.99 R\n");
            textData.append("438 CANDYMAKER ASSORT   4.99 R\n");
            textData.append("474 TRIPOD              8.99 R\n");
            textData.append("433 BLK LOGO PRNTED ZO  7.99 R\n");
            textData.append("458 AQUA MICROTERRY SC  6.99 R\n");
            textData.append("493 30L BLK FF DRESS   16.99 R\n");
            textData.append("407 LEVITATING DESKTOP  7.99 R\n");
            textData.append("441 **Blue Overprint P  2.99 R\n");
            textData.append("476 REPOSE 4PCPM CHOC   5.49 R\n");
            textData.append("461 WESTGATE BLACK 25  59.99 R\n");
            textData.append("------------------------------\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            textData.append("SUBTOTAL                160.38\n");
            textData.append("TAX                      14.43\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            method = "addTextSize";
            mPrinter.addTextSize(2, 2);
            method = "addText";
            mPrinter.addText("TOTAL    174.81\n");
            method = "addTextSize";
            mPrinter.addTextSize(1, 1);
            method = "addFeedLine";
            mPrinter.addFeedLine(1);

            textData.append("CASH                    200.00\n");
            textData.append("CHANGE                   25.19\n");
            textData.append("------------------------------\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            textData.append("Purchased item total number\n");
            textData.append("Sign Up and Save !\n");
            textData.append("With Preferred Saving Card\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());
            method = "addFeedLine";
            mPrinter.addFeedLine(2);

            method = "addBarcode";
            mPrinter.addBarcode("01209457",
                    Printer.BARCODE_CODE39,
                    Printer.HRI_BELOW,
                    Printer.FONT_A,
                    barcodeWidth,
                    barcodeHeight);

            method = "addCut";
            mPrinter.addCut(Printer.CUT_FEED);
        }
        catch (Exception e) {
            ShowMsg.showException(e, method, this);
            return false;
        }

        textData = null;

        return true;
    }

    @Override
    public void onPtrReceive(final Printer printerObj, final int code, final PrinterStatusInfo status, final String printJobId) {
        runOnUiThread(new Runnable() {
            @Override
            public synchronized void run() {
                ShowMsg.showResult(code, makeErrorMessage(status), OrderCart.this);

                dispPrinterWarnings(status);

                btnConfirmOrder.setEnabled(true);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        disconnectPrinter();
                    }
                }).start();
            }
        });
    }

    private void disconnectPrinter() {
        if (mPrinter == null) {
            return;
        }

        try {
            mPrinter.endTransaction();
        }
        catch (final Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
                    ShowMsg.showException(e, "endTransaction", OrderCart.this);
                }
            });
        }

        try {
            mPrinter.disconnect();
        }
        catch (final Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
                    ShowMsg.showException(e, "disconnect", OrderCart.this);
                }
            });
        }

        finalizeObject();
    }

    /** THE ADAPTER AND THE ARRAYLIST **/
    OrderAdapter adapter;
    ArrayList<OrderData> arrOrders = new ArrayList<>();

    /** THE ORDER TOTAL **/
    Double ORDER_TOTAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fe_order_cart);
        ButterKnife.bind(this);

        /***** CONFIGURE THE ACTIONBAR *****/
        configAB();

        /** CONFIGURE THE RECYCLER VIEW **/
        configRecyclerView();
        
        /** INSTANTIATE THE ADAPTER **/
        adapter = new OrderAdapter(OrderCart.this, arrOrders);

        /** FETCH THE INCOMING DATA **/
        fetchIncomingData();
    }

    /** REFRESH THE CURRENT ORDER TOTAL **/
    private void refreshOrderTotal() {
        DBResto resto = new DBResto(OrderCart.this);
        String s =
                "SELECT SUM(" + db.ORDER_TOTAL + ") FROM " + db.ORDER_CART +
                        " WHERE " + db.ORDER_TABLE_ID + " = " + INCOMING_TABLE_ID;
        Log.e("CART QUERY", s);
        Cursor cursor = resto.selectAllData(s);
        Log.e("ORDER TOTAL DUMP", DatabaseUtils.dumpCursorToString(cursor));
        if (cursor != null && cursor.getCount() != 0)	{
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                ORDER_TOTAL = cursor.getDouble(0);
                txtOrderTotal.setText(String.valueOf(ORDER_TOTAL));
            }
        }
    }

    /** FETCH THE CURRENT LIST OF ORDERS **/
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
            Log.e("DUMP", DatabaseUtils.dumpCursorToString(cursor));
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

            /** REFRESH THE CURRENT ORDER TOTAL **/
            refreshOrderTotal();
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

            /** SET THE DISH QUANTITY **/
            holder.txtQuantity.setText(String.valueOf(data.getOrderQuantity()));

            /** SET THE DISH PRICE **/
            String strDishPrice = data.getMenuPrice();
            double dishBasePrice = Double.parseDouble(strDishPrice);
            double dishTotal = dishBasePrice * Integer.parseInt(holder.txtQuantity.getText().toString());
            holder.txtDishTotal.setText(String.valueOf(dishTotal));

            /** INCREASE THE DISH QUANTITY **/
            holder.txtQtyPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int qty = Integer.parseInt(holder.txtQuantity.getText().toString());

                    /** INCREMENT THE QUANTITY BY 1 **/
                    qty++;

                    /** SET THE NEW ORDER QUANTITY **/
                    holder.txtQuantity.setText(String.valueOf(qty));

                    /** UPDATE THE DISH PRICE **/
                    String strDishPrice = data.getMenuPrice();
                    double dishBasePrice = Double.parseDouble(strDishPrice);
                    double dishTotal = dishBasePrice * qty;
                    holder.txtDishTotal.setText(String.valueOf(dishTotal));

                    /** UPDATE THE ORDER IN THE ORDER CART TABLE **/
                    db = new DBResto(activity);
                    db.updateOrderQuantity(data.getOrderID(), qty, String.valueOf(dishTotal));
                    db.close();

                    /** REFRESH THE ORDER TOTAL **/
                    refreshOrderTotal();
                }
            });

            /** REDUCE THE DISH QUANTITY **/
            holder.txtQtyMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int qty = Integer.parseInt(holder.txtQuantity.getText().toString());

                    /** DECREASE THE QUANTITY BY 1 **/
                    if (qty > 1) {
                        qty--;

                        /** SET THE NEW ORDER QUANTITY **/
                        holder.txtQuantity.setText(String.valueOf(qty));

                        /** UPDATE THE DISH PRICE **/
                        String strDishPrice = data.getMenuPrice();
                        double dishBasePrice = Double.parseDouble(strDishPrice);
                        double dishTotal = dishBasePrice * qty;
                        holder.txtDishTotal.setText(String.valueOf(dishTotal));

                        /** UPDATE THE ORDER IN THE ORDER CART TABLE **/
                        db = new DBResto(activity);
                        db.updateOrderQuantity(data.getOrderID(), qty, String.valueOf(dishTotal));
                        db.close();

                        /** REFRESH THE ORDER TOTAL **/
                        refreshOrderTotal();
                    }
                }
            });

            /** REMOVE THE DISH FROM THE ORDER CART **/
            holder.txtRemoveDish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Log.e("STATUS", String.valueOf(data.isOrderStatus()));
                    removeAt(position);

                    /** DELETE THE ORDER FROM THE DATABASE **/
                    if (!data.isOrderStatus())   {
                        /* OPEN THE DATABASE CONNECTION */
                        db = new DBResto(activity);

                        /* DELETE THE ORDER CART RECORD */
                        db.deleteOrder(data.getOrderID());

                        /* CLOSE THE DATABASE CONNECTION */
                        db.close();

                        /** REFRESH THE ORDER TOTAL **/
                        refreshOrderTotal();
                    }
                }
            });
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
                    inflate(R.layout.fe_order_cart_item, parent, false);

            return new OrdersVH(itemView);
        }

        public class OrdersVH extends RecyclerView.ViewHolder	{

            AppCompatTextView txtRemoveDish;
            CircleImageView imgvwMenu;
            AppCompatTextView txtMenuName;
            AppCompatTextView txtQtyMinus;
            AppCompatTextView txtQuantity;
            AppCompatTextView txtQtyPlus;
            AppCompatTextView txtDishTotal;

            public OrdersVH(View v) {
                super(v);
                txtRemoveDish = (AppCompatTextView) v.findViewById(R.id.txtRemoveDish);
                imgvwMenu = (CircleImageView) v.findViewById(R.id.imgvwMenu);
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

    private String makeErrorMessage(PrinterStatusInfo status) {
        String msg = "";

        if (status.getOnline() == Printer.FALSE) {
            msg += getString(R.string.handlingmsg_err_offline);
        }
        if (status.getConnection() == Printer.FALSE) {
            msg += getString(R.string.handlingmsg_err_no_response);
        }
        if (status.getCoverOpen() == Printer.TRUE) {
            msg += getString(R.string.handlingmsg_err_cover_open);
        }
        if (status.getPaper() == Printer.PAPER_EMPTY) {
            msg += getString(R.string.handlingmsg_err_receipt_end);
        }
        if (status.getPaperFeed() == Printer.TRUE || status.getPanelSwitch() == Printer.SWITCH_ON) {
            msg += getString(R.string.handlingmsg_err_paper_feed);
        }
        if (status.getErrorStatus() == Printer.MECHANICAL_ERR || status.getErrorStatus() == Printer.AUTOCUTTER_ERR) {
            msg += getString(R.string.handlingmsg_err_autocutter);
            msg += getString(R.string.handlingmsg_err_need_recover);
        }
        if (status.getErrorStatus() == Printer.UNRECOVER_ERR) {
            msg += getString(R.string.handlingmsg_err_unrecover);
        }
        if (status.getErrorStatus() == Printer.AUTORECOVER_ERR) {
            if (status.getAutoRecoverError() == Printer.HEAD_OVERHEAT) {
                msg += getString(R.string.handlingmsg_err_overheat);
                msg += getString(R.string.handlingmsg_err_head);
            }
            if (status.getAutoRecoverError() == Printer.MOTOR_OVERHEAT) {
                msg += getString(R.string.handlingmsg_err_overheat);
                msg += getString(R.string.handlingmsg_err_motor);
            }
            if (status.getAutoRecoverError() == Printer.BATTERY_OVERHEAT) {
                msg += getString(R.string.handlingmsg_err_overheat);
                msg += getString(R.string.handlingmsg_err_battery);
            }
            if (status.getAutoRecoverError() == Printer.WRONG_PAPER) {
                msg += getString(R.string.handlingmsg_err_wrong_paper);
            }
        }
        if (status.getBatteryLevel() == Printer.BATTERY_LEVEL_0) {
            msg += getString(R.string.handlingmsg_err_battery_real_end);
        }

        return msg;
    }

    private void dispPrinterWarnings(PrinterStatusInfo status) {
//        EditText edtWarnings = (EditText)findViewById(R.id.edtWarnings);
        String warningsMsg = "";

        if (status == null) {
            return;
        }

        if (status.getPaper() == Printer.PAPER_NEAR_END) {
            warningsMsg += getString(R.string.handlingmsg_warn_receipt_near_end);
        }

        if (status.getBatteryLevel() == Printer.BATTERY_LEVEL_1) {
            warningsMsg += getString(R.string.handlingmsg_warn_battery_near_end);
        }

//        edtWarnings.setText(warningsMsg);
    }
}