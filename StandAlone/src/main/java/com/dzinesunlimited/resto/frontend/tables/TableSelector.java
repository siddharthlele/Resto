package com.dzinesunlimited.resto.frontend.tables;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.dzinesunlimited.resto.R;
import com.dzinesunlimited.resto.utils.TypefaceSpan;
import com.dzinesunlimited.resto.utils.db.DBResto;
import com.dzinesunlimited.resto.utils.helpers.adapters.frontend.TablesAdapter;
import com.dzinesunlimited.resto.utils.helpers.pojos.backend.TablesData;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TableSelector extends AppCompatActivity {

    /** THE DATABASE HELPER INSTANCE **/
    DBResto db;

    /** DECLARE THE LAYOUT ELEMENTS **/
    LinearLayout linlaHeaderProgress;
    RecyclerView listTables;
    LinearLayout linlaEmpty;

    /** THE ADAPTER TO DISPLAY CUSTOM TABLE ITEMS **/
    TablesAdapter adapter;

    /** ARRAYLIST TO STORE AND PASS TABLES TO THE ADAPTER **/
    ArrayList<TablesData> arrTables = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fe_table_selector);

        /** CAST THE LAYOUT ELEMENTS **/
        castLayoutElements();

        /***** CONFIGURE THE ACTIONBAR *****/
        configAB();

        /** CLEAR THE ARRAY **/
        arrTables.clear();

        /** SHOW THE LIST OF TABLES **/
        new showTables().execute();

        /***** INSTANTIATE THE ADAPTER *****/
        adapter = new TablesAdapter(TableSelector.this, arrTables);
    }

    /** FETCH THE LIST OF TABLES **/
    private class showTables extends AsyncTask<Void, Void, Void> {

        /** A CURSOR INSTANCE **/
        Cursor cursor;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /** INSTANTIATE THE DATABASE HELPER CLASS **/
            db = new DBResto(TableSelector.this);

            /** SHOW THE PROGRESSBAR WHILE FETCHING THE LIST OF TABLES **/
            linlaHeaderProgress.setVisibility(View.VISIBLE);

			/* CONSTRUCT A QUERY TO FETCH TABLES ON RECORD */
            String strQueryData = "SELECT * FROM tables";

			/* CAST THE QUERY IN THE CURSOR TO FETCH THE RESULTS */
            cursor = db.selectAllData(strQueryData);
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            /** CHECK THAT THE DATABASE QUERY RETURNED SOME RESULTS **/
            if (cursor != null && cursor.getCount() != 0)	{

				/* AN INSTANCE OF THE TablesData HELPER CLASS */
                TablesData table;

                /** LOOP THROUGH THE RESULT SET AND PARSE NECESSARY INFORMATION **/
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                    /***** INSTANTIATE THE TablesData INSTANCE "table" *****/
                    table = new TablesData();

                    /** GET THE TABLE NUMBER **/
                    if (cursor.getString(cursor.getColumnIndex(db.TABLE_ID)) != null)	{
                        String TABLE_NO = cursor.getString(cursor.getColumnIndex(db.TABLE_ID));
                        table.setTableNo(TABLE_NO);
                    } else {
                        table.setTableNo(null);
                    }

                    /** GET THE TABLE SEATS **/
                    if (cursor.getString(cursor.getColumnIndex(db.TABLE_SEATS)) != null)	{
                        String TABLE_SEATS = cursor.getString(cursor.getColumnIndex(db.TABLE_SEATS));
                        table.setTableSeats(TABLE_SEATS);
                    } else {
                        table.setTableNo(null);
                    }

                    /** GET THE TABLE OCCUPATION STATUS **/
                    if (cursor.getString(cursor.getColumnIndex(db.TABLE_STATUS)) != null)	{
                        String TABLE_STATUS = cursor.getString(cursor.getColumnIndex(db.TABLE_STATUS));
//                        Log.e("STATUS", TABLE_OCCUPANCY);
                        table.setTableStatus(TABLE_STATUS);
                    } else {
                        table.setTableStatus(null);
                    }

                    /** ADD THE COLLECTED DATA TO THE ARRAYLIST **/
                    arrTables.add(table);
                }
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
//            db.close();

            /** SET THE ADAPTER TO THE LISTVIEW **/
            listTables.setAdapter(adapter);

            /** HIDE THE PROGRESSBAR AFTER FETCHING THE LIST OF TABLES **/
            linlaHeaderProgress.setVisibility(View.GONE);
        }
    }

    /** CAST THE LAYOUT ELEMENTS **/
    private void castLayoutElements() {
        linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
        listTables = (RecyclerView) findViewById(R.id.listTables);
        linlaEmpty = (LinearLayout) findViewById(R.id.linlaEmpty);

        /** CONFIGURE THE RECYCLERVIEW **/
        listTables.setHasFixedSize(false);
        LinearLayoutManager llm = new LinearLayoutManager(TableSelector.this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        listTables.setLayoutManager(llm);
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {

        Toolbar myToolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);

        SpannableString s = new SpannableString(getResources().getString(R.string.table_selector_title));
        s.setSpan(new TypefaceSpan(
                this, "RobotoCondensed-Regular.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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

                this.finish();

                break;

            default:
                break;
        }

        return false;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}