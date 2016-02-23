package com.dzinesunlimited.resto.backend.admin.frags;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.dzinesunlimited.resto.R;
import com.dzinesunlimited.resto.utils.TypefaceSpan;
import com.dzinesunlimited.resto.utils.db.DBResto;
import com.dzinesunlimited.resto.utils.helpers.adapters.backend.PrinterCategoryAdapter;
import com.dzinesunlimited.resto.utils.helpers.pojos.backend.PrinterCategoryData;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PrintCategorySelector extends AppCompatActivity {

//    /***** DATABASE INSTANCE *****/
//    private DBResto db;
//
//    /** THE INCOMING PRINTER ID AND IP **/
//    private String INCOMING_PRINTER_ID = null;
//    private String INCOMING_PRINTER_IP = null;
//
//    /** DECLARE THE LAYOUT ELEMENTS **/
//    LinearLayout linlaHeaderProgress;
//    AppCompatTextView txtPrinterIP;
//    ListView listCategories;
//    LinearLayout linlaEmpty;
//
//    /** THE ADAPTER AND THE ARRAYLIST **/
//    PrinterCategoryAdapter adapter;
//    ArrayList<PrinterCategoryData> arrCategories = new ArrayList<>();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.be_print_categories);
//
//        /** CAST THE LAYOUT ELEMENTS **/
//        castLayoutElements();
//
//        /***** CONFIGURE THE ACTIONBAR *****/
//        configAB();
//
//        /** GET THE INCOMING CATEGORY ID **/
//        getIncomingData();
//
//        /** INSTANTIATE THE ADAPTER **/
//        adapter = new PrinterCategoryAdapter(PrintCategorySelector.this, arrCategories);
//    }
//
//    /** FETCH THE LIST OF ALL CATEGORIES **/
//    private class fetchMenuCategories extends AsyncTask<Void, Void, Void>   {
//
//        /** A CURSOR INSTANCE **/
//        Cursor cursor;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//            /** SHOW THE PROGRESS WHILE FETCHING THE LIST OF CATEGORIES **/
//            linlaHeaderProgress.setVisibility(View.VISIBLE);
//
//            /** INSTANTIATE THE DATABASE INSTANCE **/
//            db = new DBResto(PrintCategorySelector.this);
//
//            /** CONSTRUCT THE QUERY **/
//            String s = "SELECT * FROM " + db.PRINT_CATEGORIES + " WHERE " + db.PRINT_PRINTER_ID + " = " + INCOMING_PRINTER_ID;
//
//            /** CAST THE RESULTS IN THE CURSOR **/
//            cursor = db.selectAllData(s);
////            Log.e("DUMP", DatabaseUtils.dumpCursorToString(cursor));
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//
//            /** CHECK THAT THE DATABASE QUERY RETURNED SOME RESULTS **/
//            if (cursor != null && cursor.getCount() != 0)	{
//
//				/* AN INSTANCE OF THE PrinterCategoryData POJO CLASS */
//                PrinterCategoryData data;
//
//                /** LOOP THROUGH THE RESULT SET AND PARSE NECESSARY INFORMATION **/
//                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
//
//                    /***** INSTANTIATE THE PrinterCategoryData INSTANCE "data" *****/
//                    data = new PrinterCategoryData();
//
//                    /** GET THE PRINT CATEGORY ID **/
//                    if (cursor.getString(cursor.getColumnIndex(db.PRINT_CATEGORY_ID)) != null)	{
//                        String PRINT_CATEGORY_ID = cursor.getString(cursor.getColumnIndex(db.PRINT_CATEGORY_ID));
//                        data.setPrintCatID(PRINT_CATEGORY_ID);
//                    } else {
//                        data.setPrintCatID(null);
//                    }
//
//                    /** GET THE PRINTER ID **/
//                    if (cursor.getString(cursor.getColumnIndex(db.PRINT_PRINTER_ID)) != null)	{
//                        String PRINT_PRINTER_ID = cursor.getString(cursor.getColumnIndex(db.PRINT_PRINTER_ID));
//                        data.setPrinterID(PRINT_PRINTER_ID);
//                    } else {
//                        data.setPrinterID(null);
//                    }
//
//                    /** GET THE CATEGORY_ID **/
//                    if (cursor.getString(cursor.getColumnIndex(db.PRINT_CAT_ID)) != null)	{
//                        String CATEGORY_ID = cursor.getString(cursor.getColumnIndex(db.PRINT_CAT_ID));
//                        data.setCatID(CATEGORY_ID);
//
//                        /** GET THE CATEGORY NAME **/
//                        String strQueryTax = "SELECT * FROM " + db.CATEGORY + " WHERE " + db.CATEGORY_ID + " = " + CATEGORY_ID;
//                        Cursor curCat = db.selectAllData(strQueryTax);
//
//                        /** LOOP THROUGH THE RESULT SET AND PARSE NECESSARY INFORMATION **/
//                        for (curCat.moveToFirst(); !curCat.isAfterLast(); curCat.moveToNext()) {
//                            String CATEGORY_NAME = curCat.getString(curCat.getColumnIndex(db.CATEGORY_NAME));
//                            data.setCatName(CATEGORY_NAME);
//                        }
//                    } else {
//                        data.setCatID(null);
//                        data.setCatName(null);
//                    }
//
//                    /** GET THE PRINT CATEGORY STATUS **/
//                    if (cursor.getString(cursor.getColumnIndex(db.PRINT_CAT_STATUS)) != null)	{
//                        String PRINT_CAT_STATUS = cursor.getString(cursor.getColumnIndex(db.PRINT_CAT_STATUS));
//                        Log.e("PRINT CAT STATUS", PRINT_CAT_STATUS);
//                        if (PRINT_CAT_STATUS.equals("1"))   {
//                            data.setStatus(true);
//                        } else if (PRINT_CAT_STATUS.equals("0"))    {
//                            data.setStatus(false);
//                        }
//                    } else {
//                        data.setStatus(false);
//                    }
//
//                    /** ADD THE COLLECTED DATA TO THE ARRAYLIST **/
//                    arrCategories.add(data);
//                }
//
//                /** SHOW THE GRIDVIEW  AND HIDE THE EMPTY CONTAINER **/
//                Runnable run = new Runnable() {
//
//                    @Override
//                    public void run() {
//
//                        /** SHOW THE RECYCLERVIEW **/
//                        listCategories.setVisibility(View.VISIBLE);
//
//                        /** HIDE THE EMPTY DATA SET IMAGE AND TEXT **/
//                        linlaEmpty.setVisibility(View.GONE);
//                    }
//                }; runOnUiThread(run);
//
//            } else {
//                /** SHOW THE EMPTY CONTAINER AND HIDE THE GRIDVIEW **/
//                Runnable run = new Runnable() {
//
//                    @Override
//                    public void run() {
//
//                        /** HIDE THE RECYCLERVIEW **/
//                        listCategories.setVisibility(View.GONE);
//
//                        /** SHOW THE EMPTY DATA SET IMAGE AND TEXT **/
//                        linlaEmpty.setVisibility(View.VISIBLE);
//                    }
//                }; runOnUiThread(run);
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//
//            /** CLOSE THE CURSOR **/
//            if (cursor != null && !cursor.isClosed())	{
//                cursor.close();
//            }
//
//            /** CLOSE THE DATABASE **/
//            db.close();
//
//            /** SET THE ADAPTER TO THE RECYCLERVIEW **/
//            listCategories.setAdapter(adapter);
//
//            /** HIDE THE PROGRESS AFTER FETCHING THE LIST OF CATEGORIES **/
//            linlaHeaderProgress.setVisibility(View.GONE);
//        }
//    }
//
//    /** GET THE INCOMING CATEGORY ID **/
//    private void getIncomingData() {
//        Bundle bundle = getIntent().getExtras();
//        if (bundle.containsKey("PRINTER_ID") && bundle.containsKey("PRINTER_IP"))   {
//            INCOMING_PRINTER_ID = bundle.getString("PRINTER_ID");
//            INCOMING_PRINTER_IP = bundle.getString("PRINTER_IP");
//            if (INCOMING_PRINTER_ID != null && INCOMING_PRINTER_IP != null)    {
//                String part1 = getResources().getString(R.string.print_sel_top_message_1);
//                String part2 = getResources().getString(R.string.print_sel_top_message_2);
//                txtPrinterIP.setText(part1 + INCOMING_PRINTER_IP + part2);
//
//                /** CHECK IF THE CATEGORIES EXIST IN THE PRINT CATEGORIES TABLE **/
//                db = new DBResto(getBaseContext());
//                String s = "SELECT * FROM " + db.PRINT_CATEGORIES + " WHERE " + db.PRINT_PRINTER_ID + " = " + INCOMING_PRINTER_ID;
//                Cursor cursor = db.selectAllData(s);
//                if (cursor.getCount() == 0) {
//                    /** ADD ALL CATEGORIES TO THE PRINT CATEGORIES TABLE **/
//                    String strCategories = "SELECT * FROM " + db.CATEGORY;
//                    Cursor curCategories = db.selectAllData(strCategories);
////                    Log.e("CATEGORIES", String.valueOf(curCategories.getCount()));
//                    if (curCategories != null && curCategories.getCount() != 0) {
//                        /** LOOP THROUGH THE RESULT SET AND PARSE NECESSARY INFORMATION **/
//                        for (curCategories.moveToFirst(); !curCategories.isAfterLast(); curCategories.moveToNext()) {
//                            if (curCategories.getString(curCategories.getColumnIndex(db.CATEGORY_ID)) != null)	{
//                                String CATEGORY_ID = curCategories.getString(curCategories.getColumnIndex(db.CATEGORY_ID));
////                                Log.e("CATEGORY ID", CATEGORY_ID);
//                                db.addPrintCategories(INCOMING_PRINTER_ID, CATEGORY_ID, false);
//                            }
//                        }
//                    }
//
//                    /** FETCH THE LIST OF ALL CATEGORIES **/
//                    new fetchMenuCategories().execute();
//
//                } else {
//
//                    /** FETCH THE LIST OF ALL CATEGORIES **/
//                    new fetchMenuCategories().execute();
//                }
//            } else {
//                //TODO: SHOW AN ERROR
//            }
//        } else {
//            //TODO: SHOW AN ERROR
//        }
//    }
//
//    /** CAST THE LAYOUT ELEMENTS **/
//    private void castLayoutElements() {
//        linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
//        txtPrinterIP = (AppCompatTextView) findViewById(R.id.txtPrinterIP);
//        listCategories = (ListView) findViewById(R.id.listCategories);
//        linlaEmpty = (LinearLayout) findViewById(R.id.linlaEmpty);
//    }
//
//    /***** CONFIGURE THE ACTIONBAR *****/
//    private void configAB() {
//
//        Toolbar myToolbar = (Toolbar) findViewById(R.id.myToolbar);
//        setSupportActionBar(myToolbar);
//
//        SpannableString s = new SpannableString("Select applicable taxes");
//        s.setSpan(new TypefaceSpan(
//                this, "RobotoCondensed-Regular.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowTitleEnabled(true);
//        getSupportActionBar().setTitle(s);
//        getSupportActionBar().setSubtitle(null);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = new MenuInflater(PrintCategorySelector.this);
//        inflater.inflate(R.menu.print_selector, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                this.finish();
//                break;
//            case R.id.save:
//
//                /***** SET THE RESULT TO "RESULT_OK" AND FINISH THE ACTIVITY *****/
//                Intent categoriesSelected = new Intent();
//                setResult(RESULT_OK, categoriesSelected);
//
//                /** FINISH THE ACTIVITY **/
//                finish();
//            default:
//                break;
//        }
//        return false;
//    }
//
//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
//    }
}