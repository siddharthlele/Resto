package com.dzinesunlimited.resto.frontend.menu;

import android.support.v7.app.AppCompatActivity;

public class OrderCart extends AppCompatActivity /*implements ReceiveListener*/ {

//    /** GET THE INCOMING TABLE ID **/
//    private String INCOMING_TABLE_ID = null;
//
//    /** THE DATABASE HELPER INSTANCE **/
//    DBResto db;
//
//    /** THE PRINTER INSTANCE **/
//    private Printer mPrinter = null;
//
//    /** DECLARE THE LAYOUT ELEMENTS **/
//    @Bind(R.id.linlaHeaderProgress)LinearLayout linlaHeaderProgress;
//    @Bind(R.id.orderCart)RecyclerView orderCart;
//    @Bind(R.id.linlaEmpty)LinearLayout linlaEmpty;
//    @Bind(R.id.txtOrderTotal)AppCompatTextView txtOrderTotal;
//    @Bind(R.id.btnConfirmOrder)AppCompatButton btnConfirmOrder;
//    @OnClick(R.id.btnConfirmOrder) protected void PrintKOT() {
//        btnConfirmOrder.setEnabled(false);
//        if (!runPrintReceiptSequence()) {
//            btnConfirmOrder.setEnabled(true);
//        }
//    }
//
//    /** THE ADAPTER AND THE ARRAYLIST **/
//    OrderAdapter adapter;
//    ArrayList<OrderData> arrOrders = new ArrayList<>();
//
//    /** THE ORDER TOTAL **/
//    Double ORDER_TOTAL;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fe_order_cart);
//        ButterKnife.bind(this);
//
//        /***** CONFIGURE THE ACTIONBAR *****/
//        configAB();
//
//        /** CONFIGURE THE RECYCLER VIEW **/
//        configRecyclerView();
//
//        /** INSTANTIATE THE ADAPTER **/
//        adapter = new OrderAdapter(OrderCart.this, arrOrders);
//
//        /** FETCH THE INCOMING DATA **/
//        Bundle bundle = getIntent().getExtras();
//        if (bundle.containsKey("TABLE_NO")) {
//            INCOMING_TABLE_ID = bundle.getString("TABLE_NO");
//            if (INCOMING_TABLE_ID != null)  {
//
//                /** GET THE ORDER CART DETAILS AND THE PRINTERS REQUIRED TO PRINT THE KOT **/
//                linlaHeaderProgress.setVisibility(View.VISIBLE);
//                db = new DBResto(OrderCart.this);
//                String qryOrderCategories =
//                        "SELECT * FROM orderCart INNER JOIN menu ON orderCart.menuID = menu.menuID " +
//                                "INNER JOIN category ON menu.categoryID = category.categoryID " +
//                                "INNER JOIN printers ON category.categoryPrinterID = printers.printerID WHERE "
//                                + db.ORDER_TABLE_ID + " = " + INCOMING_TABLE_ID;
//                Cursor cursor = db.selectAllData(qryOrderCategories);
//                Log.e("INNER JOIN DUMP", DatabaseUtils.dumpCursorToString(cursor));
//                if (cursor != null && cursor.getCount() != 0)	{
//                    /** AN INSTANCE OF THE OrderData HELPER CLASS **/
//                    OrderData data;
//                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
//
//                        /***** INSTANTIATE THE KOTPrint INSTANCE "print" *****/
//                        data = new OrderData();
//
//                        /** GET THE ORDER ID **/
//                        if (cursor.getString(cursor.getColumnIndex(db.ORDER_CART_ID)) != null)	{
//                            String ORDER_CART_ID = cursor.getString(cursor.getColumnIndex(db.ORDER_CART_ID));
//                            data.setOrderID(ORDER_CART_ID);
//                        } else {
//                            data.setOrderID(null);
//                        }
//
//                        /** GET THE TABLE ID **/
//                        if (cursor.getString(cursor.getColumnIndex(db.ORDER_TABLE_ID)) != null)	{
//                            String ORDER_TABLE_ID = cursor.getString(cursor.getColumnIndex(db.ORDER_TABLE_ID));
//                            data.setTableID(ORDER_TABLE_ID);
//                        } else {
//                            data.setTableID(null);
//                        }
//
//                        /** GET THE MENU ID **/
//                        if (cursor.getString(cursor.getColumnIndex(db.MENU_ID)) != null)	{
//                            String MENU_ID = cursor.getString(cursor.getColumnIndex(db.MENU_ID));
//                            data.setMenuID(MENU_ID);
//                        } else {
//                            data.setMenuID(null);
//                        }
//
//                        /** GET THE MENU NAME **/
//                        if (cursor.getString(cursor.getColumnIndex(db.MENU_NAME)) != null)	{
//                            String MENU_NAME = cursor.getString(cursor.getColumnIndex(db.MENU_NAME));
//                            data.setMenuName(MENU_NAME);
//                        } else {
//                            data.setMenuName(null);
//                        }
//
//                        /** GET THE MEAL PRICE **/
//                        if (cursor.getString(cursor.getColumnIndex(db.MENU_PRICE)) != null)	{
//                            String strMealPrice = cursor.getString(cursor.getColumnIndex(db.MENU_PRICE));
//                            data.setMenuPrice(strMealPrice);
//                        } else {
//                            data.setMenuPrice(null);
//                        }
//
//                        /** GET THE MEAL IMAGE **/
//                        if (cursor.getBlob(cursor.getColumnIndex(db.MENU_IMAGE)) != null)	{
//                            byte[] bytImage = cursor.getBlob(cursor.getColumnIndex(db.MENU_IMAGE));
//                            data.setMenuImage(bytImage);
//                        } else {
//                            data.setMenuImage(null);
//                        }
//
//                        /** GET THE QUANTITY **/
//                        if (cursor.getString(cursor.getColumnIndex(db.ORDER_QUANTITY)) != null)	{
//                            String ORDER_QUANTITY = cursor.getString(cursor.getColumnIndex(db.ORDER_QUANTITY));
//                            data.setOrderQuantity(ORDER_QUANTITY);
//                        } else {
//                            data.setOrderQuantity(null);
//                        }
//
//                        /** GET THE STATUS **/
//                        if (cursor.getString(cursor.getColumnIndex(db.ORDER_STATUS)) != null)	{
//                            String ORDER_STATUS = cursor.getString(cursor.getColumnIndex(db.ORDER_STATUS));
//                            data.setOrderStatus(Boolean.parseBoolean(ORDER_STATUS));
//                        } else {
//                            data.setOrderStatus(false);
//                        }
//
//                        /** GET THE TIMESTAMP **/
//                        if (cursor.getString(cursor.getColumnIndex(db.ORDER_TIMESTAMP)) != null)	{
//                            String ORDER_TIMESTAMP = cursor.getString(cursor.getColumnIndex(db.ORDER_TIMESTAMP));
//                            data.setOrderTimestamp(ORDER_TIMESTAMP);
//                        } else {
//                            data.setOrderTimestamp(null);
//                        }
//
//                        /** GET THE PRINTER IP **/
//                        if (cursor.getString(cursor.getColumnIndex(db.PRINTER_IP)) != null)	{
//                            String PRINTER_IP = cursor.getString(cursor.getColumnIndex(db.PRINTER_IP));
//                            data.setPrinterIP(PRINTER_IP);
//                        } else {
//                            data.setPrinterIP(null);
//                        }
//
//                        arrOrders.add(data);
//                    }
//
//                    /** CLOSE THE CURSOR **/
//                    if (cursor != null && !cursor.isClosed())	{
//                        cursor.close();
//                    }
//
//                    /** CLOSE THE DATABASE **/
//                    db.close();
//
//                    /** SET THE ADAPTER TO THE RECYCLERVIEW **/
//                    orderCart.setAdapter(adapter);
//
//                    /** HIDE THE PROGRESS AFTER FETCHING CURRENT ORDERS **/
//                    linlaHeaderProgress.setVisibility(View.GONE);
//
//                    /** REFRESH THE CURRENT ORDER TOTAL **/
//                    refreshOrderTotal();
//
//                    /** SHOW THE SIZE OF THE KOT ARRAY **/
//                    Toast.makeText(getApplicationContext(), "ARRAY SIZE: " + String.valueOf(arrOrders.size()), Toast.LENGTH_SHORT).show();
//                    Collections.sort(arrOrders, new comparePrinterIP());
//                    for (int i = 0; i < arrOrders.size(); i++) {
//                        Log.e("PRINTER IP", arrOrders.get(i).getPrinterIP());
//                    }
//
//                    /** SHOW THE RECYCLERVIEW **/
//                    orderCart.setVisibility(View.VISIBLE);
//
//                    /** HIDE THE EMPTY DATA SET IMAGE AND TEXT **/
//                    linlaEmpty.setVisibility(View.GONE);
//                } else {
//                    /** SHOW THE EMPTY DATA SET IMAGE AND TEXT **/
//                    linlaEmpty.setVisibility(View.VISIBLE);
//
//                    /** HIDE THE RECYCLERVIEW **/
//                    orderCart.setVisibility(View.GONE);
//                }
//
//            } else {
//                //TODO: SHOW AN ERROR
//            }
//        } else {
//            //TODO: SHOW AN ERROR
//        }
//    }
//
//    private boolean runPrintReceiptSequence() {
//        if (!initializeObject()) {
//            return false;
//        }
//
//        if (!createKOTData()) {
//            finalizeObject();
//            return false;
//        }
//
//        if (!printData()) {
//            finalizeObject();
//            return false;
//        }
//
//        return true;
//    }
//
//    /** CREATE THE KOT AND PRINT TO THE DESIGNATED PRINTER **/
//    private boolean createKOTData() {
//        String method = null;
//        StringBuilder textData = new StringBuilder();
//
//        if (mPrinter == null) {
//            return false;
//        }
//
//        try {
//            /** GET THE TIMESTAMP FOR THE ORDER CONFIRMATION **/
//            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
//            String currentTS = sdf.format(new Date());
//
//            /** CONSTRUCT THE TIME LINE / ROW **/
//            String strTimeLabel = "TIME:";
//            String strActualTime = currentTS;
//            String strFinalTimeLabel = StringUtils.rightPad(strTimeLabel, 32, " ") + strActualTime;
//
//            /** CONSTRUCT THE TABLE NUMBER LINE / ROW **/
//            String strTable = "TABLE:";
//            String strFinalTable = null;
//            if (INCOMING_TABLE_ID.length() == 1)    {
//                strFinalTable = StringUtils.rightPad(strTable, 39, " ");
//            } else if (INCOMING_TABLE_ID.length() > 1)  {
//                strFinalTable = StringUtils.rightPad(strTable, 38, " ");
//            } else if ((INCOMING_TABLE_ID.length() >= 2))   {
//                strFinalTable = StringUtils.rightPad(strTable, 37, " ");
//            }
//
//            method = "addTextAlign";
//            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
//            method = "addFeedLine";
//            mPrinter.addFeedLine(1);
//            textData.append("KOT (Kitchen Order Token)\n\n");
//            textData.append(strFinalTimeLabel + "\n\n");
//            mPrinter.addText(textData.toString());
//            textData.delete(0, textData.length());
//
//            method = "addTextSize";
//            mPrinter.addTextSize(1, 1);
//            textData.append(strFinalTable + INCOMING_TABLE_ID + "\n\n");
//            textData.append("------------------------------\n\n");
//            method = "addText";
//            mPrinter.addText(textData.toString());
//            textData.delete(0, textData.length());
//
//            /** GET THE LIST OF ORDERS **/
//            if (arrOrders.size() != 0)  {
//                /** A STRING BUILDER FOR COLLATING KOT PRINT DATA **/
//                StringBuilder stbPrintKOT;
//
//                for (int i = 0; i < arrOrders.size(); i++) {
//                    stbPrintKOT = new StringBuilder();
//
//                    /** GET THE MEAL NAME **/
//                    String MEAL_NAME = arrOrders.get(i).getMenuName();
//                    if (MEAL_NAME.length() > 40)    {
//                        String strFinalMealName = MEAL_NAME.substring(0, 37);
//                        stbPrintKOT.append(strFinalMealName + "  ");
//                    } else {
//                        String strMealNameWithPad = StringUtils.rightPad(MEAL_NAME, 37, "*");
//                        stbPrintKOT.append(strMealNameWithPad + "  ");
//                    }
//
//                    /** GET THE QUANTITY **/
//                    String MEAL_QUANTITY = arrOrders.get(i).getOrderQuantity();
//                    if (MEAL_QUANTITY.length() < 4) {
//                        String strQuantityWithPad = StringUtils.leftPad(MEAL_QUANTITY, 4, "*");
//                        stbPrintKOT.append(strQuantityWithPad + "  \n\n");
//                    } else {
//                        stbPrintKOT.append(MEAL_QUANTITY + "  \n\n");
//                    }
//
//                    String strFinalOrderDetails = String.valueOf(stbPrintKOT);
//                    strFinalOrderDetails = strFinalOrderDetails.replaceAll("\\*", " ");
//                    textData.append(strFinalOrderDetails);
//                }
//            }
//
//            method = "addText";
//            mPrinter.addText(textData.toString());
//            textData.delete(0, textData.length());
//
//            String strDashes = "\n------------------------------\n\nEND OF ORDER\n\n";
//
//            textData.append(strDashes);
//            method = "addText";
//            mPrinter.addText(textData.toString());
//            textData.delete(0, textData.length());
//
//            method = "addCut";
//            mPrinter.addCut(Printer.CUT_FEED);
//        }
//        catch (Exception e) {
//            ShowMsg.showException(e, method, this);
//            return false;
//        }
//
//        return true;
//    }
//
//    private boolean printData() {
//        if (mPrinter == null) {
//            return false;
//        }
//
//        if (!connectPrinter()) {
//            return false;
//        }
//
//        PrinterStatusInfo status = mPrinter.getStatus();
//
//        dispPrinterWarnings(status);
//
//        if (!isPrintable(status)) {
//            ShowMsg.showMsg(makeErrorMessage(status), this);
//            try {
//                mPrinter.disconnect();
//            }
//            catch (Exception ex) {
//                // Do nothing
//            }
//            return false;
//        }
//
//        try {
//            mPrinter.sendData(Printer.PARAM_DEFAULT);
//        }
//        catch (Exception e) {
//            ShowMsg.showException(e, "sendData", this);
//            try {
//                mPrinter.disconnect();
//            }
//            catch (Exception ex) {
//                // Do nothing
//            }
//            return false;
//        }
//
//        return true;
//    }
//
//    private boolean isPrintable(PrinterStatusInfo status) {
//        if (status == null) {
//            return false;
//        }
//
//        if (status.getConnection() == Printer.FALSE) {
//            return false;
//        }
//        else if (status.getOnline() == Printer.FALSE) {
//            return false;
//        }
//        else {
//            //print available
//        }
//
//        return true;
//    }
//
//    private boolean connectPrinter() {
//        boolean isBeginTransaction = false;
//
//        if (mPrinter == null) {
//            return false;
//        }
//
//        try {
//            mPrinter.connect("TCP:192.168.11.202", Printer.PARAM_DEFAULT);
//        }
//        catch (Exception e) {
//            ShowMsg.showException(e, "connect", this);
//            return false;
////            Log.e("CONNECTION ERROR", e.getMessage().toString());
//        }
//
//        try {
//            mPrinter.beginTransaction();
//            isBeginTransaction = true;
//        }
//        catch (Exception e) {
//            ShowMsg.showException(e, "beginTransaction", this);
//        }
//
//        if (isBeginTransaction == false) {
//            try {
//                mPrinter.disconnect();
//            }
//            catch (Epos2Exception e) {
//                // Do nothing
//                return false;
//            }
//        }
//
//        return true;
//    }
//
//    private boolean initializeObject() {
//        try {
//            mPrinter = new Printer(0, 0, this);
//        }
//        catch (Exception e) {
//            ShowMsg.showException(e, "Printer", this);
//            return false;
//        }
//
//        mPrinter.setReceiveEventListener(OrderCart.this);
//
//        return true;
//    }
//
//    private void finalizeObject() {
//        if (mPrinter == null) {
//            return;
//        }
//
//        mPrinter.clearCommandBuffer();
//
//        mPrinter.setReceiveEventListener(null);
//
//        mPrinter = null;
//    }
//
//    @Override
//    public void onPtrReceive(final Printer printerObj, final int code, final PrinterStatusInfo status, final String printJobId) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public synchronized void run() {
//                ShowMsg.showResult(code, makeErrorMessage(status), OrderCart.this);
//
//                dispPrinterWarnings(status);
//
//                btnConfirmOrder.setEnabled(true);
//
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        disconnectPrinter();
//                    }
//                }).start();
//            }
//        });
//    }
//
//    private void disconnectPrinter() {
//        if (mPrinter == null) {
//            return;
//        }
//
//        try {
//            mPrinter.endTransaction();
//        }
//        catch (final Exception e) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public synchronized void run() {
//                    ShowMsg.showException(e, "endTransaction", OrderCart.this);
//                }
//            });
//        }
//
//        try {
//            mPrinter.disconnect();
//        }
//        catch (final Exception e) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public synchronized void run() {
//                    ShowMsg.showException(e, "disconnect", OrderCart.this);
//                }
//            });
//        }
//
//        finalizeObject();
//    }
//
//    /***** CONFIGURE THE ACTIONBAR *****/
//    private void configAB() {
//
//        Toolbar myToolbar = (Toolbar) findViewById(R.id.myToolbar);
//        setSupportActionBar(myToolbar);
//
//        String strTitle = getResources().getString(R.string.order_cart_title);
//        SpannableString s = new SpannableString(strTitle);
//        s.setSpan(new TypefaceSpan(
//                OrderCart.this, "RobotoCondensed-Regular.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowTitleEnabled(true);
//        getSupportActionBar().setTitle(s);
//        getSupportActionBar().setSubtitle(null);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                finish();
//                break;
//            default:
//                break;
//        }
//        return false;
//    }
//
//    /** THE CATEGORIES CUSTOM ADAPTER **/
//    private class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrdersVH> {
//
//        /** AN ACTIVITY INSTANCE **/
//        Activity activity;
//
//        /** ARRAYLIST TO GET DATA FROM THE ACTIVITY **/
//        ArrayList<OrderData> arrAdapOrders;
//
//        public OrderAdapter(Activity activity, ArrayList<OrderData> arrAdapOrders) {
//
//            /** CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE **/
//            this.activity = activity;
//
//            /** CAST THE CONTENTS OF THE ARRAYLIST IN THE METHOD TO THE LOCAL INSTANCE **/
//            this.arrAdapOrders = arrAdapOrders;
//        }
//
//        @Override
//        public int getItemCount() {
//            return arrAdapOrders.size();
//        }
//
//        @Override
//        public void onBindViewHolder(final OrdersVH holder, final int position) {
//            final OrderData data = arrAdapOrders.get(position);
//
//            /** GET THE MENU NAME **/
//            final String SUB_MENU_NAME = data.getMenuName();
//            holder.txtMenuName.setText(SUB_MENU_NAME);
//
//            /** GET THE SUB MENU THUMBNAIL **/
//            byte[] CATEGORY_THUMB = data.getMenuImage();
//            if (CATEGORY_THUMB != null)	{
//                Bitmap bmpThumb = BitmapFactory.decodeByteArray(CATEGORY_THUMB, 0, CATEGORY_THUMB.length);
//                holder.imgvwMenu.setImageBitmap(bmpThumb);
//            }
//
//            /** SET THE DISH QUANTITY **/
//            holder.txtQuantity.setText(String.valueOf(data.getOrderQuantity()));
//
//            /** SET THE DISH PRICE **/
//            String strDishPrice = data.getMenuPrice();
//            double dishBasePrice = Double.parseDouble(strDishPrice);
//            double dishTotal = dishBasePrice * Integer.parseInt(holder.txtQuantity.getText().toString());
//            holder.txtDishTotal.setText(String.valueOf(dishTotal));
//
//            /** INCREASE THE DISH QUANTITY **/
//            holder.txtQtyPlus.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int qty = Integer.parseInt(holder.txtQuantity.getText().toString());
//
//                    /** INCREMENT THE QUANTITY BY 1 **/
//                    qty++;
//
//                    /** SET THE NEW ORDER QUANTITY **/
//                    holder.txtQuantity.setText(String.valueOf(qty));
//
//                    /** UPDATE THE DISH PRICE **/
//                    String strDishPrice = data.getMenuPrice();
//                    double dishBasePrice = Double.parseDouble(strDishPrice);
//                    double dishTotal = dishBasePrice * qty;
//                    holder.txtDishTotal.setText(String.valueOf(dishTotal));
//
//                    /** UPDATE THE ORDER IN THE ORDER CART TABLE **/
//                    db = new DBResto(activity);
//                    db.updateOrderQuantity(data.getOrderID(), qty, String.valueOf(dishTotal));
//                    db.close();
//
//                    /** REFRESH THE ORDER TOTAL **/
//                    refreshOrderTotal();
//                }
//            });
//
//            /** REDUCE THE DISH QUANTITY **/
//            holder.txtQtyMinus.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int qty = Integer.parseInt(holder.txtQuantity.getText().toString());
//
//                    /** DECREASE THE QUANTITY BY 1 **/
//                    if (qty > 1) {
//                        qty--;
//
//                        /** SET THE NEW ORDER QUANTITY **/
//                        holder.txtQuantity.setText(String.valueOf(qty));
//
//                        /** UPDATE THE DISH PRICE **/
//                        String strDishPrice = data.getMenuPrice();
//                        double dishBasePrice = Double.parseDouble(strDishPrice);
//                        double dishTotal = dishBasePrice * qty;
//                        holder.txtDishTotal.setText(String.valueOf(dishTotal));
//
//                        /** UPDATE THE ORDER IN THE ORDER CART TABLE **/
//                        db = new DBResto(activity);
//                        db.updateOrderQuantity(data.getOrderID(), qty, String.valueOf(dishTotal));
//                        db.close();
//
//                        /** REFRESH THE ORDER TOTAL **/
//                        refreshOrderTotal();
//                    }
//                }
//            });
//
//            /** REMOVE THE DISH FROM THE ORDER CART **/
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
//        }
//
//        private void removeAt(int position) {
//            arrAdapOrders.remove(position);
//            notifyItemRemoved(position);
//            notifyItemRangeChanged(position, arrAdapOrders.size());
//        }
//
//        @Override
//        public OrdersVH onCreateViewHolder(ViewGroup parent, int i) {
//
//            View itemView = LayoutInflater.
//                    from(parent.getContext()).
//                    inflate(R.layout.fe_order_cart_item, parent, false);
//
//            return new OrdersVH(itemView);
//        }
//
//        public class OrdersVH extends RecyclerView.ViewHolder	{
//
//            AppCompatTextView txtRemoveDish;
//            CircleImageView imgvwMenu;
//            AppCompatTextView txtMenuName;
//            AppCompatTextView txtQtyMinus;
//            AppCompatTextView txtQuantity;
//            AppCompatTextView txtQtyPlus;
//            AppCompatTextView txtDishTotal;
//
//            public OrdersVH(View v) {
//                super(v);
//                txtRemoveDish = (AppCompatTextView) v.findViewById(R.id.txtRemoveDish);
//                imgvwMenu = (CircleImageView) v.findViewById(R.id.imgvwMenu);
//                txtMenuName = (AppCompatTextView) v.findViewById(R.id.txtMenuName);
//                txtQtyMinus = (AppCompatTextView) v.findViewById(R.id.txtQtyMinus);
//                txtQuantity = (AppCompatTextView) v.findViewById(R.id.txtQuantity);
//                txtQtyPlus = (AppCompatTextView) v.findViewById(R.id.txtQtyPlus);
//                txtDishTotal = (AppCompatTextView) v.findViewById(R.id.txtDishTotal);
//            }
//        }
//    }
//
//    private String makeErrorMessage(PrinterStatusInfo status) {
//        String msg = "";
//
//        if (status.getOnline() == Printer.FALSE) {
//            msg += getString(R.string.handlingmsg_err_offline);
//        }
//        if (status.getConnection() == Printer.FALSE) {
//            msg += getString(R.string.handlingmsg_err_no_response);
//        }
//        if (status.getCoverOpen() == Printer.TRUE) {
//            msg += getString(R.string.handlingmsg_err_cover_open);
//        }
//        if (status.getPaper() == Printer.PAPER_EMPTY) {
//            msg += getString(R.string.handlingmsg_err_receipt_end);
//        }
//        if (status.getPaperFeed() == Printer.TRUE || status.getPanelSwitch() == Printer.SWITCH_ON) {
//            msg += getString(R.string.handlingmsg_err_paper_feed);
//        }
//        if (status.getErrorStatus() == Printer.MECHANICAL_ERR || status.getErrorStatus() == Printer.AUTOCUTTER_ERR) {
//            msg += getString(R.string.handlingmsg_err_autocutter);
//            msg += getString(R.string.handlingmsg_err_need_recover);
//        }
//        if (status.getErrorStatus() == Printer.UNRECOVER_ERR) {
//            msg += getString(R.string.handlingmsg_err_unrecover);
//        }
//        if (status.getErrorStatus() == Printer.AUTORECOVER_ERR) {
//            if (status.getAutoRecoverError() == Printer.HEAD_OVERHEAT) {
//                msg += getString(R.string.handlingmsg_err_overheat);
//                msg += getString(R.string.handlingmsg_err_head);
//            }
//            if (status.getAutoRecoverError() == Printer.MOTOR_OVERHEAT) {
//                msg += getString(R.string.handlingmsg_err_overheat);
//                msg += getString(R.string.handlingmsg_err_motor);
//            }
//            if (status.getAutoRecoverError() == Printer.BATTERY_OVERHEAT) {
//                msg += getString(R.string.handlingmsg_err_overheat);
//                msg += getString(R.string.handlingmsg_err_battery);
//            }
//            if (status.getAutoRecoverError() == Printer.WRONG_PAPER) {
//                msg += getString(R.string.handlingmsg_err_wrong_paper);
//            }
//        }
//        if (status.getBatteryLevel() == Printer.BATTERY_LEVEL_0) {
//            msg += getString(R.string.handlingmsg_err_battery_real_end);
//        }
//
//        return msg;
//    }
//
//    private void dispPrinterWarnings(PrinterStatusInfo status) {
//        String warningsMsg = "";
//
//        if (status == null) {
//            return;
//        }
//
//        if (status.getPaper() == Printer.PAPER_NEAR_END) {
//            warningsMsg += getString(R.string.handlingmsg_warn_receipt_near_end);
//        }
//
//        if (status.getBatteryLevel() == Printer.BATTERY_LEVEL_1) {
//            warningsMsg += getString(R.string.handlingmsg_warn_battery_near_end);
//        }
//    }
//
//    /** CONFIGURE THE RECYCLER VIEW **/
//    private void configRecyclerView() {
//        orderCart.setHasFixedSize(true);
//        LinearLayoutManager llm = new LinearLayoutManager(OrderCart.this);
//        llm.setOrientation(LinearLayoutManager.VERTICAL);
//        orderCart.setLayoutManager(llm);
//    }
//
//    /** REFRESH THE CURRENT ORDER TOTAL **/
//    private void refreshOrderTotal() {
//        DBResto resto = new DBResto(OrderCart.this);
//        String s =
//                "SELECT SUM(" + db.ORDER_TOTAL + ") FROM " + db.ORDER_CART +
//                        " WHERE " + db.ORDER_TABLE_ID + " = " + INCOMING_TABLE_ID;
////        Log.e("CART QUERY", s);
//        Cursor cursor = resto.selectAllData(s);
////        Log.e("ORDER TOTAL DUMP", DatabaseUtils.dumpCursorToString(cursor));
//        if (cursor != null && cursor.getCount() != 0)	{
//            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
//                ORDER_TOTAL = cursor.getDouble(0);
//                txtOrderTotal.setText(String.valueOf(ORDER_TOTAL));
//            }
//        }
//    }
//
//    /** COMPARE PRINTER IP ADDRESSES TO SORT THEM **/
//    private class comparePrinterIP implements Comparator<OrderData>  {
//
//        @Override
//        public int compare(OrderData lhs, OrderData rhs) {
//            return lhs.getPrinterIP().compareTo(rhs.getPrinterIP());
//        }
//    }
//
//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
//    }
}