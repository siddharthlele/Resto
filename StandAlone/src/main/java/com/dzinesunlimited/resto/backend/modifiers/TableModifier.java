package com.dzinesunlimited.resto.backend.modifiers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.dzinesunlimited.resto.R;
import com.dzinesunlimited.resto.utils.TypefaceSpan;
import com.dzinesunlimited.resto.utils.db.DBResto;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TableModifier extends AppCompatActivity {

    /** THE INCOMING TABLE NUMBER **/
    private String INCOMING_TABLE_NUMBER = null;

    /** THE DATABASE HELPER INSTANCE **/
    private DBResto db;

    /** DECLARE THE LAYOUT ELEMENTS **/
    private AppCompatEditText edtTableNumber;
    private AppCompatEditText edtTableSeats;

    /** STRINGS TO HOLD THE TABLE DETAILS **/
    private String TABLE_ID = null;
    private String TABLE_SEATS = null;

    /** A PROGRESS DIALOG INSTANCE TO SHOW THE PROGRESS **/
    private ProgressDialog pdNewTable;

    /** BOOLEAN INSTANCE TO CHECK IF THE TABLE NUMBER EXISTS **/
    private boolean blnTableExists = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.be_modifier_table);

        /** CAST THE LAYOUT ELEMENTS **/
        castLayoutElements();

        /***** CONFIGURE THE ACTIONBAR *****/
        configAB();

        /** FETCH THE INCOMING DATA **/
        fetchIncomingData();
    }

    /*** FETCH THE TABLE DETAILS ***/
    private class fetchTableDetails extends AsyncTask<Void, Void, Void> {

        /** A CURSOR INSTANCE **/
        Cursor cursor;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /** INITIALIZE THE DATABASE **/
            db = new DBResto(TableModifier.this);

            /** CONSTRUCT THE QUERY **/
            String s = "SELECT * FROM " + db.TABLES + " WHERE " + db.TABLE_ID + " = " + INCOMING_TABLE_NUMBER;

            /** FETCH THE RESULT **/
            cursor = db.selectAllData(s);
        }

        @Override
        protected Void doInBackground(Void... params) {
            /** CHECK THAT THE DATABASE QUERY RETURNED SOME RESULTS **/
            if (cursor != null && cursor.getCount() != 0) {
                /** LOOP THROUGH THE RESULT SET AND PARSE NECESSARY INFORMATION **/
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    /** GET THE TABLE NUMBER **/
                    if (cursor.getString(cursor.getColumnIndex(db.TABLE_ID)) != null)	{
                        TABLE_ID = cursor.getString(cursor.getColumnIndex(db.TABLE_ID));
                    } else {
                        TABLE_ID = null;
                    }

                    /** GET THE TABLE SEATS **/
                    if (cursor.getString(cursor.getColumnIndex(db.TABLE_SEATS)) != null)	{
                        TABLE_SEATS = cursor.getString(cursor.getColumnIndex(db.TABLE_SEATS));
                    } else {
                        TABLE_SEATS = null;
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

            /***** SEE THE COLLECTED DATA *****/
            if (TABLE_ID != null)   {
                edtTableNumber.setText(TABLE_ID);
            }

            if (TABLE_SEATS != null)    {
                edtTableSeats.setText(TABLE_SEATS);
            }
        }
    }

    /** FETCH THE INCOMING DATA **/
    private void fetchIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey("TABLE_NO")) {
            INCOMING_TABLE_NUMBER = bundle.getString("TABLE_NO");
            if (INCOMING_TABLE_NUMBER != null)  {
                new fetchTableDetails().execute();
            } else {
                //TODO: SHOW AN ERROR
            }
        } else {
          //TODO: SHOW AN ERROR
        }
    }

    /** CAST THE LAYOUT ELEMENTS **/
    private void castLayoutElements() {

        edtTableNumber = (AppCompatEditText) findViewById(R.id.edtTableNumber);
        edtTableSeats = (AppCompatEditText) findViewById(R.id.edtTableSeats);
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {

        Toolbar myToolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);

        String strTitle = getResources().getString(R.string.table_modifier_title);
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(
                TableModifier.this, "RobotoCondensed-Regular.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(s);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(TableModifier.this);
        inflater.inflate(R.menu.generic_creator, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.save:
                /** CHECK FOR EMPTY EDIT TEXTS **/
                checkEmpty();
                break;
            case R.id.clear:
                /** FINISH THE ACTIVITY **/
                finish();
                break;
            default:
                break;
        }
        return false;
    }

    private void checkEmpty() {

		/* HIDE THE KEYBOARD */
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtTableNumber.getWindowToken(), 0);

        /** CHECK THAT THE TABLE NUMBER AND TABLE SEATS ARE NOT EMPTY **/
        if (edtTableNumber.getText().toString().length() == 0 &&
                edtTableSeats.getText().toString().length() == 0)	{
			/* SET ERROR MESSAGES IN BOTH EDIT TEXTS */
            edtTableNumber.setError(getResources().getString(R.string.table_creator_table_number_empty));
            edtTableSeats.setError(getResources().getString(R.string.table_creator_table_seats_empty));
        } else if (edtTableNumber.getText().toString().length() == 0 &&
                edtTableSeats.getText().toString().length() != 0) {
			/* SET ERROR MESSAGES IN THE TABLE NUMBER FIELD */
            edtTableNumber.setError(getResources().getString(R.string.table_creator_table_number_empty));
        } else if (edtTableNumber.getText().toString().length() != 0 &&
                edtTableSeats.getText().toString().length() == 0) {
			/* SET ERROR MESSAGES IN THE TABLE SEATS FIELD */
            edtTableSeats.setError(getResources().getString(R.string.table_creator_table_seats_empty));
        } else {
            /** CHECK THAT THE TABLE NUMBER IS NOT "0" **/
            if (edtTableNumber.getText().toString().equals("0"))	{
				/* SET ERROR MESSAGES IN THE TABLE NUMBER FIELD */
                edtTableNumber.setError(getResources().getString(R.string.table_creator_table_number_zero));
            } else {
				/* RUN TASK TO ADD THE NEW TABLE TO THE DATABASE */
                new checkUniqueTable().execute();
            }
        }
    }

    /***** CHECK FOR UNIQUE TABLES BEFORE SAVING / CREATING A NEW TABLE ENTRY *****/
    private class checkUniqueTable extends AsyncTask<Void, Void, Void> {

        /** A CURSOR INSTANCE **/
        Cursor cursor;

        /** INTEGER INSTANCE TO HOLD THE TABLE NUMBER AND SEATS **/
        String TABLE_NUMBER;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /** INSTANTIATE THE DATABASE HELPER CLASS **/
            db = new DBResto(TableModifier.this);

            /** INSTANTIATE AND CONFIGURE THE PROGRESS DIALOG **/
            pdNewTable = new ProgressDialog (TableModifier.this);

            pdNewTable.setMessage("Please wait while the new Table is being created.");
            pdNewTable.setIndeterminate(false);
            pdNewTable.setCancelable(true);
            pdNewTable.show();

            TABLE_NUMBER = edtTableNumber.getText().toString().trim();

            /** CONSTRUCT A QUERY TO FETCH TABLES ON RECORD **/
            String strQueryData = "SELECT * FROM " + db.TABLES + " WHERE " + db.TABLE_ID + " = " + TABLE_NUMBER;

            /** CAST THE QUERY IN THE CURSOR TO FETCH THE RESULTS **/
            cursor = db.selectAllData(strQueryData);
        }

        @Override
        protected Void doInBackground(Void... params) {

			/* CHECK THAT THE DATABASE QUERY RETURNED SOME RESULTS */
            if (cursor != null && cursor.getCount() != 0)	{
				/* LOOP THROUGH THE RESULT SET AND PARSE NECESSARY INFORMATION */
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
					/* GET THE TABLE NUMBERS */
                    if (cursor.getString(cursor.getColumnIndex(db.TABLE_ID)) != null)	{
                        String strTableNumber = cursor.getString(cursor.getColumnIndex(db.TABLE_ID));
                        if (strTableNumber.equals(INCOMING_TABLE_NUMBER))   {
                            blnTableExists = false;
                        } else if (strTableNumber.equals(TABLE_NUMBER))    {
                            blnTableExists = true;
                        }
                    } else {
                        blnTableExists = false;
                    }
                }
            } else {
                blnTableExists = false;
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

            if (!blnTableExists)	{

                /** ADD THE NEW TABLE TO THE DATABASE **/
                addNewTable();

            } else {
				/* SET THE ERROR MESSAGE */
                edtTableNumber.setError(getResources().getString(R.string.table_creator_table_number_duplicate));

				/* DISMISS THE DIALOG */
                pdNewTable.dismiss();
            }
        }
    }

    /** ADD THE NEW TABLE TO THE DATABASE **/
    private void addNewTable() {

        /** INTEGER INSTANCE TO HOLD THE TABLE NUMBER AND SEATS **/
        String TABLE_NUMBER = null;
        String TABLE_SEATS = null;

        /** INSTANTIATE THE DATABASE HELPER CLASS **/
        db = new DBResto(TableModifier.this);

        /* GET THE TABLE NUMBER */
        if (edtTableNumber.getText().toString().length() != 0)	{
            TABLE_NUMBER = edtTableNumber.getText().toString().trim();
        }

        /* GET THE TABLE SEATS */
        if (edtTableSeats.getText().toString().length() != 0)	{
            TABLE_SEATS = edtTableSeats.getText().toString().trim();
        }

        /* ADD THE TABLE TO THE DATABASE */
        db.updateTable(TABLE_NUMBER, TABLE_SEATS, "false");

        /** CLOSE THE DATABASE **/
        db.close();

        Intent tableCreated = new Intent();
        setResult(RESULT_OK, tableCreated);

        /* DISMISS THE DIALOG */
        pdNewTable.dismiss();

        /** FINISH THE ACTIVITY **/
        finish();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}