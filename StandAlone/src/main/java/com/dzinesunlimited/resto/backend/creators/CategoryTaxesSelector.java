package com.dzinesunlimited.resto.backend.creators;

import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.dzinesunlimited.resto.utils.helpers.adapters.backend.CategoryTaxesAdapter;
import com.dzinesunlimited.resto.utils.helpers.pojos.backend.CategoryTaxesData;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CategoryTaxesSelector extends AppCompatActivity {

    /** A DATABASE INSTANCE **/
    DBResto db;

    /** THE INCOMING CATEGORY ID **/
    private String INCOMING_CATEGORY_ID = null;

    /** DECLARE THE LAYOUT ELEMENTS **/
    @Bind(R.id.linlaHeaderProgress)LinearLayout linlaHeaderProgress;
    @Bind(R.id.listTaxes)ListView listTaxes;
    @Bind(R.id.linlaEmpty)LinearLayout linlaEmpty;

    /** THE ADAPTER AND THE ARRAYLIST **/
    CategoryTaxesAdapter adapter;
    ArrayList<CategoryTaxesData> arrTaxes = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.be_category_tax_selector);
        ButterKnife.bind(this);

        /***** CONFIGURE THE ACTIONBAR *****/
        configAB();

        /** INSTANTIATE THE ADAPTER **/
        adapter = new CategoryTaxesAdapter(CategoryTaxesSelector.this, arrTaxes);

        /** GET THE INCOMING DATA **/
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("CATEGORY_ID"))    {
            INCOMING_CATEGORY_ID = bundle.getString("CATEGORY_ID");
            if (INCOMING_CATEGORY_ID != null)   {
                /** CHECK IF CATEGORY HAS TAXES CONFIGURED **/
                db = new DBResto(CategoryTaxesSelector.this);
                String s = " SELECT * FROM " + db.CAT_TAXES + " WHERE " + db.TRANS_CAT_ID + " = " + INCOMING_CATEGORY_ID;
                Log.e("QUERY 1", s);
                Cursor cursor = db.selectAllData(s);
                if (cursor.getCount() != 0) {
                    /** FETCH THE CATEGORY TAXES **/
                    new fetchCategoryTaxes().execute();
                } else {
                    /** ADD THE TAXES IN "FALSE / INACTIVE" STATE TO THE CATEGORY TAXES TABLE **/
                    String s1 = "SELECT * FROM " + db.TAXES;
                    Cursor curTaxes = db.selectAllData(s1);
                    Log.e("QUERY 2", s1);
//                    Log.e("TAXES", DatabaseUtils.dumpCursorToString(cursor));
                    if (curTaxes != null && curTaxes.getCount() != 0)	{
                        for (curTaxes.moveToFirst(); !curTaxes.isAfterLast(); curTaxes.moveToNext()) {

                            /** GET THE TAX ID **/
                            if (curTaxes.getString(curTaxes.getColumnIndex(db.TAX_ID)) != null)	{
                                String TAX_ID = curTaxes.getString(curTaxes.getColumnIndex(db.TAX_ID));
                                Log.e("TAX ID", TAX_ID);
                                db.addCategoryTaxes(TAX_ID, INCOMING_CATEGORY_ID, false);
                            }
                        }
                        /** FETCH THE CATEGORY TAXES **/
                        new fetchCategoryTaxes().execute();
                    }
                }
            } else {
                //// TODO: 2/29/2016
            }
        } else {
            //// TODO: 2/29/2016
        }
    }

    /** TASK TO FETCH CATEGORY TAXES **/
    private class fetchCategoryTaxes extends AsyncTask<Void, Void, Void> {

        /** A CURSOR INSTANCE **/
        Cursor cursor;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /** INSTANTIATE THE DATABASE HELPER CLASS **/
            db = new DBResto(CategoryTaxesSelector.this);

            /** CONSTRUCT THE QUERY TO FETCH ALL USER FROM THE CATEGORY TAXES TABLE **/
            String strQueryData = "SELECT * FROM " + db.CAT_TAXES + " WHERE " + db.TRANS_CAT_ID + " = " + INCOMING_CATEGORY_ID;

            /** CAST THE QUERY IN THE CURSOR TO FETCH THE RESULTS **/
            cursor = db.selectAllData(strQueryData);
            Log.e("CATEGORY TAXES", DatabaseUtils.dumpCursorToString(cursor));
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

            /** SET THE ADAPTER TO THE LIST VIEW **/
            listTaxes.setAdapter(adapter);

            /** HIDE THE PROGRESS AFTER FETCHING THE DATA **/
            linlaHeaderProgress.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... params) {

            /** CHECK THAT THE DATABASE QUERY RETURNED SOME RESULTS **/
            if (cursor != null && cursor.getCount() != 0)	{

                /** SHOW THE LISTVIEW AND HIDE THE EMPTY CONTAINER **/
                Runnable run = new Runnable() {

                    @Override
                    public void run() {

                        /** SHOW THE LISTVIEW **/
                        listTaxes.setVisibility(View.VISIBLE);

                        /** HIDE THE EMPTY DATA SET IMAGE AND TEXT **/
                        linlaEmpty.setVisibility(View.GONE);
                    }
                }; runOnUiThread(run);

				/* AN INSTANCE OF THE CategoryTaxesData POJO CLASS */
                CategoryTaxesData taxesData;

                /** LOOP THROUGH THE RESULT SET AND PARSE NECESSARY INFORMATION **/
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                    /***** INSTANTIATE THE CategoryTaxesData INSTANCE "taxesData" *****/
                    taxesData = new CategoryTaxesData();

                    /** GET THE CATEGORY_TAX_ID **/
                    if (cursor.getString(cursor.getColumnIndex(db.CATEGORY_TAX_ID)) != null)	{
                        String CAT_TAXES_ID = cursor.getString(cursor.getColumnIndex(db.CATEGORY_TAX_ID));
                        taxesData.setCatTaxesID(CAT_TAXES_ID);
                    }

                    /** GET THE TRANS_TAX_ID **/
                    if (cursor.getString(cursor.getColumnIndex(db.TRANS_TAX_ID)) != null)	{
                        String TRANS_TAX_ID = cursor.getString(cursor.getColumnIndex(db.TRANS_TAX_ID));
                        taxesData.setCatID(TRANS_TAX_ID);

                        /** GET THE TAX NAME **/
                        String strQueryTax = "SELECT * FROM " + db.TAXES + " WHERE " + db.TAX_ID + " = " + TRANS_TAX_ID;
                        Cursor curCat = db.selectAllData(strQueryTax);

                        /** LOOP THROUGH THE RESULT SET AND PARSE NECESSARY INFORMATION **/
                        for (curCat.moveToFirst(); !curCat.isAfterLast(); curCat.moveToNext()) {
                            String strTaxName = curCat.getString(curCat.getColumnIndex(db.TAX_NAME));
                            taxesData.setTaxName(strTaxName);
                        }
                    }

                    /** GET THE TRANS_CAT_ID **/
                    if (cursor.getString(cursor.getColumnIndex(db.TRANS_CAT_ID)) != null)	{
                        String CAT_TAX_ID = cursor.getString(cursor.getColumnIndex(db.TRANS_CAT_ID));
                        taxesData.setTaxID(CAT_TAX_ID);
                    }

                    /** GET THE TRANS_TAX_STATUS **/
                    if (cursor.getString(cursor.getColumnIndex(db.TRANS_TAX_STATUS)) != null)	{
                        String TRANS_TAX_STATUS = cursor.getString(cursor.getColumnIndex(db.TRANS_TAX_STATUS));
                        Log.e("TRANS TAX STATUS", TRANS_TAX_STATUS);
                        if (TRANS_TAX_STATUS.equals("1"))   {
                            taxesData.setTaxStatus(true);
                        } else {
                            taxesData.setTaxStatus(false);
                        }
                    }

                    /** ADD THE COLLECTED DATA TO THE ARRAYLIST **/
                    arrTaxes.add(taxesData);
                }

            } else {

                /** SHOW THE EMPTY CONTAINER AND HIDE THE LISTVIEW **/
                Runnable run = new Runnable() {

                    @Override
                    public void run() {

                        /** SHOW THE EMPTY DATA SET IMAGE AND TEXT **/
                        linlaEmpty.setVisibility(View.VISIBLE);

                        /** HIDE THE LISTVIEW **/
                        listTaxes.setVisibility(View.GONE);
                    }
                }; runOnUiThread(run);
            }

            return null;
        }
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {

        Toolbar myToolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);

        SpannableString s = new SpannableString("Select applicable taxes");
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
        MenuInflater inflater = new MenuInflater(CategoryTaxesSelector.this);
        inflater.inflate(R.menu.generic_creator, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                /***** SET THE RESULT TO "RESULT_CANCELED" AND FINISH THE ACTIVITY *****/
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
                break;
            case R.id.save:
                Intent taxesSaved = new Intent();
                setResult(RESULT_OK, taxesSaved);
                finish();
                break;
            case R.id.clear:
                /***** SET THE RESULT TO "RESULT_CANCELED" AND FINISH THE ACTIVITY *****/
                Intent intent1 = new Intent();
                setResult(RESULT_CANCELED, intent1);
                finish();
                break;
            default:
                break;
        }

        return false;
    }
}