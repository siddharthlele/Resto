package com.dzinesunlimited.resto.backend.creators;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.dzinesunlimited.resto.R;
import com.dzinesunlimited.resto.utils.TypefaceSpan;
import com.dzinesunlimited.resto.utils.db.DBResto;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TableCreator extends AppCompatActivity {

    /** THE DATABASE HELPER INSTANCE **/
    private DBResto db;

    /** DECLARE THE LAYOUT ELEMENTS **/
    private EditText edtTableNumber;
    private EditText edtTableSeats;

    /** A PROGRESS DIALOG INSTANCE TO SHOW THE PROGRESS **/
    private ProgressDialog pdNewTable;

    /** BOOLEAN INSTANCE TO CHECK IF THE TABLE NUMBER EXISTS **/
    private boolean blnTableExists = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.be_creator_table);

        /** CAST THE LAYOUT ELEMENTS **/
        castLayoutElements();

        /***** CONFIGURE THE ACTIONBAR *****/
        configAB();
    }

    /** CAST THE LAYOUT ELEMENTS **/
    private void castLayoutElements() {

        edtTableNumber = (EditText) findViewById(R.id.edtTableNumber);
        edtTableSeats = (EditText) findViewById(R.id.edtTableSeats);
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {

        Toolbar myToolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);

        String strTitle = getResources().getString(R.string.table_creator_title);
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(
                TableCreator.this, "RobotoCondensed-Regular.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(s);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(TableCreator.this);
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

                /** HIDE THE KEYBOARD **/
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);

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
        int TABLE_NUMBER;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /** INSTANTIATE THE DATABASE HELPER CLASS **/
            db = new DBResto(TableCreator.this);

            /** INSTANTIATE AND CONFIGURE THE PROGRESS DIALOG **/
            pdNewTable = new ProgressDialog (TableCreator.this);

            pdNewTable.setMessage("Please wait while the new Table is being created.");
            pdNewTable.setIndeterminate(false);
            pdNewTable.setCancelable(true);
            pdNewTable.show();

            TABLE_NUMBER = Integer.valueOf(edtTableNumber.getText().toString());

            /** CONSTRUCT A QUERY TO FETCH TABLES ON RECORD **/
            String strQueryData =
                    "SELECT * FROM tables WHERE table_id = '" + String.valueOf(TABLE_NUMBER) + "'";

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
                        if (Integer.valueOf(strTableNumber) == TABLE_NUMBER)	{
							/* TOGGLE THE BOOLEAN TO INDICATE THE TABLE NUMBER EXISTS */
                            blnTableExists = true;
                        } else {
							/* TOGGLE THE BOOLEAN TO INDICATE THE TABLE NUMBER DOES NOT EXIST */
                            blnTableExists = false;
                        }
                    } else {
						/* TOGGLE THE BOOLEAN TO INDICATE THE TABLE NUMBER DOES NOT EXIST */
                        blnTableExists = false;
                    }
                }
            } else {
				/* TOGGLE THE BOOLEAN TO INDICATE THE TABLE NUMBER DOES NOT EXIST */
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
//                new addNewTable().execute();

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
        int TABLE_NUMBER = 0;
        int TABLE_SEATS = 0;

        /** INSTANTIATE THE DATABASE HELPER CLASS **/
        db = new DBResto(TableCreator.this);

        /* GET THE TABLE NUMBER */
        if (edtTableNumber.getText().toString().length() != 0)	{
            TABLE_NUMBER = Integer.valueOf(edtTableNumber.getText().toString());
        }

        /* GET THE TABLE SEATS */
        if (edtTableSeats.getText().toString().length() != 0)	{
            TABLE_SEATS = Integer.valueOf(edtTableSeats.getText().toString());
        }

        /* ADD THE TABLE TO THE DATABASE */
        db.createNewTable(String.valueOf(TABLE_NUMBER), String.valueOf(TABLE_SEATS), "false");

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