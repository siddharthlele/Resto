package com.dzinesunlimited.resto.backend.modifiers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.dzinesunlimited.resto.R;
import com.dzinesunlimited.resto.utils.TypefaceSpan;
import com.dzinesunlimited.resto.utils.db.DBResto;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TaxModifier extends AppCompatActivity {

    /** THE INCOMING TAX ID AND NAME **/
    private String INCOMING_TAX_ID = null;
    private String INCOMING_TAX_NAME = null;

    /** THE DATABASE HELPER INSTANCE **/
    DBResto db;

    /***** DECLARE THE LAYOUT ELEMENTS *****/
    AppCompatEditText edtTaxName;
    AppCompatEditText edtTaxPercentage;
    AppCompatEditText edtTaxRegistration;
    RadioGroup rdgrpPercentOfAmount;
    RadioButton rdbtnYes, rdbtnNo;
    LinearLayout linlaPercentageOfAmount;
    AppCompatEditText edtTaxPercentageOfAmount;
    ImageView imgvwWhatIsThis;

    /** STRINGS TO HOLD THE COLLECTED DATA **/
    String TAX_NAME;
    String TAX_PERCENTAGE;
    String TAX_REGISTRATION;
    boolean TAX_COMPLETE_AMOUNT = true;
    String TAX_PERCENT_OF_AMOUNT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.be_modifier_tax);

        /** CAST THE LAYOUT ELEMENTS **/
        castLayoutElements();

        /***** CONFIGURE THE ACTIONBAR *****/
        configAB();

        /** FETCH THE INCOMING DATA **/
        fetchIncomingData();
    }

    /** FETCH THE INCOMING DATA **/
    private void fetchIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey("TAX_ID") && bundle.containsKey("TAX_NAME"))   {
            INCOMING_TAX_NAME = bundle.getString("TAX_NAME");
            INCOMING_TAX_ID = bundle.getString("TAX_ID");
            if (INCOMING_TAX_ID != null)    {
                new fetchTaxDetails().execute();
            } else {
                //TODO: SHOW AN ERROR
            }
        } else {
            //TODO: SHOW AN ERROR
        }
    }

    /** TASK TO FETCH TAX DETAILS **/
    private class fetchTaxDetails extends AsyncTask<Void, Void, Void>   {

        /** A CURSOR INSTANCE **/
        Cursor cursor;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /** INSTANTIATE THE DATABASE INSTANCE **/
            db = new DBResto(TaxModifier.this);

            /** CONSTRUCT THE QUERY **/
            String s = "SELECT * FROM " + db.TAXES + " WHERE " + db.TAX_ID + " = " + INCOMING_TAX_ID;

            /** FETCH THE RESULT IN THE CURSOR **/
            cursor = db.selectAllData(s);
        }

        @Override
        protected Void doInBackground(Void... params) {
            /** CHECK THAT THE DATABASE QUERY RETURNED SOME RESULTS **/
            if (cursor != null && cursor.getCount() != 0) {
                /** LOOP THROUGH THE RESULT SET AND PARSE NECESSARY INFORMATION **/
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    /** GET THE TAX NAME **/
                    if (cursor.getString(cursor.getColumnIndex(db.TAX_NAME)) != null) {
                        TAX_NAME = cursor.getString(cursor.getColumnIndex(db.TAX_NAME));
                    } else {
                        TAX_NAME = null;
                    }

                    /** GET THE TAX REGISTRATION **/
                    if (cursor.getString(cursor.getColumnIndex(db.TAX_REGISTRATION)) != null) {
                        TAX_REGISTRATION = cursor.getString(cursor.getColumnIndex(db.TAX_REGISTRATION));
                    } else {
                        TAX_REGISTRATION = null;
                    }

                    /** GET THE TAX PERCENTAGE **/
                    if (cursor.getString(cursor.getColumnIndex(db.TAX_PERCENTAGE)) != null) {
                        TAX_PERCENTAGE = cursor.getString(cursor.getColumnIndex(db.TAX_PERCENTAGE));
                    } else {
                        TAX_PERCENTAGE = null;
                    }

                    /** GET THE TAX COMPLETE AMOUNT **/
                    if (cursor.getString(cursor.getColumnIndex(db.TAX_ENTIRE_AMOUNT)) != null) {
                        TAX_COMPLETE_AMOUNT = Boolean.valueOf(cursor.getString(cursor.getColumnIndex(db.TAX_ENTIRE_AMOUNT)));
                    } else {
                        TAX_COMPLETE_AMOUNT = false;
                    }

                    /** GET THE TAX TAXABLE PERCENTAGE **/
                    if (cursor.getString(cursor.getColumnIndex(db.TAX_TAXABLE_PERCENTAGE)) != null) {
                        TAX_PERCENT_OF_AMOUNT = cursor.getString(cursor.getColumnIndex(db.TAX_TAXABLE_PERCENTAGE));
                    } else {
                        TAX_PERCENT_OF_AMOUNT = null;
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
            if (TAX_NAME != null)   {
                edtTaxName.setText(TAX_NAME);
            }

            if (TAX_REGISTRATION != null)    {
                edtTaxRegistration.setText(TAX_REGISTRATION);
            }

            if (TAX_PERCENTAGE != null)    {
                edtTaxPercentage.setText(TAX_PERCENTAGE);
            }

            if (TAX_COMPLETE_AMOUNT)    {
                rdbtnYes.setChecked(true);
                rdbtnNo.setChecked(false);
                linlaPercentageOfAmount.setVisibility(View.GONE);
            } else {
                rdbtnNo.setChecked(true);
                rdbtnYes.setChecked(false);
                linlaPercentageOfAmount.setVisibility(View.VISIBLE);
                if (TAX_PERCENT_OF_AMOUNT != null)  {
                    edtTaxPercentageOfAmount.setText(TAX_PERCENT_OF_AMOUNT);
                }
            }
        }
    }

    /** CAST THE LAYOUT ELEMENTS **/
    private void castLayoutElements() {
        edtTaxName = (AppCompatEditText) findViewById(R.id.edtTaxName);
        edtTaxPercentage = (AppCompatEditText) findViewById(R.id.edtTaxPercentage);
        edtTaxRegistration = (AppCompatEditText) findViewById(R.id.edtTaxRegistration);
        rdgrpPercentOfAmount = (RadioGroup) findViewById(R.id.rdgrpPercentOfAmount);
        rdbtnYes = (RadioButton) findViewById(R.id.rdbtnYes);
        rdbtnNo = (RadioButton) findViewById(R.id.rdbtnNo);
        linlaPercentageOfAmount = (LinearLayout) findViewById(R.id.linlaPercentageOfAmount);
        edtTaxPercentageOfAmount = (AppCompatEditText) findViewById(R.id.edtTaxPercentageOfAmount);
        imgvwWhatIsThis = (ImageView) findViewById(R.id.imgvwWhatIsThis);

        /** CHECK PERCENTAGE OF AMOUNT RADIOBUTTON CLICK **/
        rdgrpPercentOfAmount.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int position = rdgrpPercentOfAmount.indexOfChild(findViewById(checkedId));
                switch (position) {
                    case 0:
                        /** SET ENTIRE AMOUNT TAXABLE **/
                        TAX_COMPLETE_AMOUNT = true;

                        /** HIDE THE PERCENTAGE OF AMOUNT AppCompatEditText **/
                        linlaPercentageOfAmount.setVisibility(View.GONE);
                        break;
                    case 1:
                        /** SET PART OF AMOUNT TAXABLE **/
                        TAX_COMPLETE_AMOUNT = false;

                        /** SHOW THE PERCENTAGE OF AMOUNT AppCompatEditText **/
                        linlaPercentageOfAmount.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
            }
        });

        /** SHOW THE NOTICE (WHAT IS THIS) **/
        imgvwWhatIsThis.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                MaterialDialog dialog = new MaterialDialog.Builder(TaxModifier.this)
                        .title(R.string.tax_creator_popup_title)
                        .content(R.string.tax_creator_what_is_this)
                        .positiveText(R.string.tax_creator_popup_title_dismiss)
                        .theme(Theme.LIGHT)
                        .icon(ContextCompat.getDrawable(TaxModifier.this, R.drawable.ic_info_outline_white_24dp))
                        .typeface("HelveticaNeueLTW1G-MdCn.otf", "HelveticaNeueLTW1G-Cn.otf")
                        .show();
            }
        });
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {

        Toolbar myToolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);

        String strTitle = getResources().getString(R.string.tax_creator_title);
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(
                TaxModifier.this, "RobotoCondensed-Regular.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(s);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(TaxModifier.this);
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
                validateData();
                break;
            case R.id.clear:
                finish();
                break;
            default:
                break;
        }

        return false;
    }

    private void validateData() {

        /** HIDE THE KEYBOARD **/
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);

        /*** GRAB THE DATA ***/
        TAX_NAME = edtTaxName.getText().toString();
        TAX_PERCENTAGE = edtTaxPercentage.getText().toString();
        TAX_REGISTRATION = edtTaxRegistration.getText().toString();
        if (TAX_COMPLETE_AMOUNT)    {
            TAX_PERCENT_OF_AMOUNT = "100";
        } else {
            TAX_PERCENT_OF_AMOUNT = edtTaxPercentageOfAmount.getText().toString();
        }

        /***** CHECK FOR THE REQUIRED DATA *****/
        if (edtTaxName.getText().toString().length() == 0)  {
            edtTaxName.setError(getResources().getString(R.string.tax_creator_tax_name_empty));
            edtTaxName.requestFocus();
        } else if (edtTaxPercentage.getText().toString().length() == 0)    {
            edtTaxPercentage.setError(getResources().getString(R.string.tax_creator_tax_percentage_empty));
            edtTaxPercentage.requestFocus();
        } else if (edtTaxPercentage.getText().toString().equals("0") ||
                edtTaxPercentage.getText().toString().equals("0.0") ||
                edtTaxPercentage.getText().toString().equals("0.00")) {
            edtTaxPercentage.setError(getResources().getString(R.string.tax_creator_tax_percentage_zero));
            edtTaxPercentage.requestFocus();
        } else if (edtTaxRegistration.getText().toString().length() == 0)    {
            edtTaxRegistration.setError(getResources().getString(R.string.tax_creator_tax_registration_empty));
            edtTaxRegistration.requestFocus();
        } else if (!TAX_COMPLETE_AMOUNT && edtTaxPercentageOfAmount.getText().toString().length() == 0) {
            edtTaxPercentageOfAmount.setError(getResources().getString(R.string.tax_creator_tax_percentage_of_amount_empty));
            edtTaxPercentageOfAmount.requestFocus();
        } else if (edtTaxPercentageOfAmount.getText().toString().equals("0") ||
                edtTaxPercentageOfAmount.getText().toString().equals("0.0") ||
                edtTaxPercentageOfAmount.getText().toString().equals("0.00"))    {
            edtTaxPercentageOfAmount.setError(getResources().getString(R.string.tax_creator_tax_percentage_of_amount_zero));
            edtTaxPercentageOfAmount.requestFocus();
        } else {
            /** CHECK FOR UNIQUE TAX NAME **/
            new checkUniqueTaxName().execute();
        }
    }

    /***** TASK TO CHECK FOR UNIQUE TAX NAME *****/
    private class checkUniqueTaxName extends AsyncTask<Void, Void, Void> {

        /** A CURSOR INSTANCE **/
        Cursor cursor;

        /** A PROGRESSDIALOG INSTANCE TO SHOW THE PROGRESS **/
        ProgressDialog pdUpdateTax;

        /** BOOLEAN INSTANCE TO CHECK IF THE TAX EXISTS **/
        boolean blnTaxExists = false;

        /** STRING TO GET THE TAX NAME FROM THE EDITTEXT **/
        String tax_name;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /** INSTANTIATE AND CONFIGURE THE PROGRESS DIALOG **/
            pdUpdateTax = new ProgressDialog(TaxModifier.this);

            pdUpdateTax.setMessage("Please wait while the new Tax is being added.");
            pdUpdateTax.setIndeterminate(false);
            pdUpdateTax.setCancelable(true);
            pdUpdateTax.show();

            /** INSTANTIATE THE DATABASE HELPER CLASS **/
            db = new DBResto(TaxModifier.this);

            /** GET THE TAX NAME FROM THE EDITTEXT **/
            tax_name = edtTaxName.getText().toString().trim();

            /** CONSTRUCT A QUERY TO FETCH THE TAXES ON RECORD **/
            String strQueryData = "SELECT * FROM " + db.TAXES + " WHERE " + db.TAX_NAME + " = '" + tax_name + "'";

            /** CAST THE QUERY IN THE CURSOR TO FETCH THE RESULTS **/
            cursor = db.selectAllData(strQueryData);
        }

        @Override
        protected Void doInBackground(Void... params) {

            /** CHECK THAT THE DATABASE QUERY RETURNED SOME RESULTS **/
            if (cursor != null && cursor.getCount() != 0)	{
                /** LOOP THROUGH THE RESULT SET AND PARSE NECESSARY INFORMATION **/
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
					/* GET THE TAX NAME */
                    if (cursor.getString(cursor.getColumnIndex(db.TAX_NAME)) != null)	{
                        String strTaxName = cursor.getString(cursor.getColumnIndex(db.TAX_NAME));
                        if (strTaxName.equals(INCOMING_TAX_NAME))	{
                            blnTaxExists = false;
                        } else if (strTaxName.equalsIgnoreCase(tax_name))    {
                            blnTaxExists = true;
                        } else {
                            blnTaxExists = false;
                        }
                    } else {
						/* TOGGLE THE BOOLEAN TO INDICATE THE TABLE NUMBER DOES NOT EXIST */
                        blnTaxExists = false;
                    }
                }
            } else {
				/* TOGGLE THE BOOLEAN TO INDICATE THE TABLE NUMBER DOES NOT EXIST */
                blnTaxExists = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            /** CHECK EXISTING TABLE STATUS **/
            if (!blnTaxExists)  {

                /** ADD THE TABLE TO THE DATABASE **/
                db.updateTax(
                        INCOMING_TAX_ID,
                        TAX_NAME,
                        TAX_PERCENTAGE,
                        TAX_REGISTRATION,
                        TAX_COMPLETE_AMOUNT,
                        TAX_PERCENT_OF_AMOUNT);

                /** CLOSE THE CURSOR **/
                if (cursor != null && !cursor.isClosed())	{
                    cursor.close();
                }

                /** CLOSE THE DATABASE **/
                db.close();

                /** SET THE INTENT RESULT **/
                Intent taxUpdated = new Intent();
                setResult(RESULT_OK, taxUpdated);

                /** DISMISS THE DIALOG **/
                pdUpdateTax.dismiss();

                /** FINISH THE ACTIVITY **/
                finish();

            } else {
                /** SET THE ERROR MESSAGE **/
                edtTaxName.setError(getResources().getString(R.string.tax_creator_duplicate_tax));

                /** DISMISS THE DIALOG **/
                pdUpdateTax.dismiss();

            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}