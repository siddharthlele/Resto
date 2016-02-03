package com.dzinesunlimited.resto.backend.creators;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
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
import com.dzinesunlimited.resto.R;
import com.dzinesunlimited.resto.utils.TypefaceSpan;
import com.dzinesunlimited.resto.utils.db.DBResto;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TaxCreator extends AppCompatActivity {

    /** THE DATABASE HELPER INSTANCE **/
    DBResto db;

    /***** DECLARE THE LAYOUT ELEMENTS *****/
    AppCompatTextView txtTaxNameLabel;
    AppCompatEditText edtTaxName;
    AppCompatTextView txtTaxPercentageLabel;
    AppCompatEditText edtTaxPercentage;
    AppCompatTextView txtTaxRegistrationLabel;
    AppCompatEditText edtTaxRegistration;
    AppCompatTextView txtTaxPercentageOfAmountLabel;
    RadioGroup rdgrpPercentOfAmount;
    RadioButton rdbtnYes, rdbtnNo;
    LinearLayout linlaPercentageOfAmount;
    AppCompatEditText edtTaxPercentageOfAmount;
    ImageView imgvwWhatIsThis;
    AppCompatTextView txtRequiredFields;

    /** BOOLEAN TO CHECK IF TAX APPLIES TO THE ENTIRE BILL OR PART OF THE BILL **/
    boolean COMPLETE_AMOUNT = true;

    /** STRINGS TO HOLD THE COLLECTED DATA **/
    String TAX_NAME;
    String TAX_PERCENTAGE;
    String TAX_REGISTRATION;
    String TAX_PERCENT_OF_AMOUNT;

    /** A PROGRESSDIALOG INSTANCE TO SHOW THE PROGRESS **/
    ProgressDialog pdNewTax;

    /** BOOLEAN INSTANCE TO CHECK IF THE TAX EXISTS **/
    boolean blnTaxExists = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.be_creator_tax);

        /** CAST THE LAYOUT ELEMENTS **/
        castLayoutElements();

        /***** CONFIGURE THE ACTIONBAR *****/
        configAB();
    }

    /** CAST THE LAYOUT ELEMENTS **/
    private void castLayoutElements() {
        txtTaxNameLabel = (AppCompatTextView) findViewById(R.id.txtTaxNameLabel);
        edtTaxName = (AppCompatEditText) findViewById(R.id.edtTaxName);
        txtTaxPercentageLabel = (AppCompatTextView) findViewById(R.id.txtTaxPercentageLabel);
        edtTaxPercentage = (AppCompatEditText) findViewById(R.id.edtTaxPercentage);
        txtTaxRegistrationLabel = (AppCompatTextView) findViewById(R.id.txtTaxRegistrationLabel);
        edtTaxRegistration = (AppCompatEditText) findViewById(R.id.edtTaxRegistration);
        txtTaxPercentageOfAmountLabel = (AppCompatTextView) findViewById(R.id.txtTaxPercentageOfAmountLabel);
        rdgrpPercentOfAmount = (RadioGroup) findViewById(R.id.rdgrpPercentOfAmount);
        rdbtnYes = (RadioButton) findViewById(R.id.rdbtnYes);
        rdbtnNo = (RadioButton) findViewById(R.id.rdbtnNo);
        linlaPercentageOfAmount = (LinearLayout) findViewById(R.id.linlaPercentageOfAmount);
        edtTaxPercentageOfAmount = (AppCompatEditText) findViewById(R.id.edtTaxPercentageOfAmount);
        imgvwWhatIsThis = (ImageView) findViewById(R.id.imgvwWhatIsThis);
        txtRequiredFields = (AppCompatTextView) findViewById(R.id.txtRequiredFields);

        /** CHECK PERCENTAGE OF AMOUNT RADIOBUTTON CLICK **/
        rdgrpPercentOfAmount.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                int position = rdgrpPercentOfAmount.indexOfChild(findViewById(checkedId));
                switch (position) {
                    case 0:

                        /** SET ENTIRE AMOUNT TAXABLE **/
                        COMPLETE_AMOUNT = true;
//                        Log.e("BOOLEAN", String.valueOf(COMPLETE_AMOUNT));

                        /** HIDE THE PERCENTAGE OF AMOUNT AppCompatEditText **/
                        linlaPercentageOfAmount.setVisibility(View.GONE);

                        break;

                    case 1:

                        /** SET PART OF AMOUNT TAXABLE **/
                        COMPLETE_AMOUNT = false;
//                        Log.e("BOOLEAN", String.valueOf(COMPLETE_AMOUNT));

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

                MaterialDialog dialog = new MaterialDialog.Builder(TaxCreator.this)
                        .title(R.string.tax_creator_popup_title)
                        .content(R.string.tax_creator_what_is_this)
                        .positiveText(R.string.tax_creator_popup_title_dismiss)
                        .icon(ContextCompat.getDrawable(TaxCreator.this, R.drawable.ic_info_outline_white_24dp))
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
                TaxCreator.this, "RobotoCondensed-Regular.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(s);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(TaxCreator.this);
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

                /** CHECK FOR EMPTY EDITTEXTS **/
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

    /***** CHECK FOR EMPTY DATA *****/
    private void checkEmpty() {

        /** HIDE THE KEYBOARD **/
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);

        /*** GRAB THE DATA ***/
        TAX_NAME = edtTaxName.getText().toString();
        TAX_PERCENTAGE = edtTaxPercentage.getText().toString();
        TAX_REGISTRATION = edtTaxRegistration.getText().toString();
        if (COMPLETE_AMOUNT)    {
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
        } else if (!COMPLETE_AMOUNT && edtTaxPercentageOfAmount.getText().toString().length() == 0) {
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

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /** INSTANTIATE AND CONFIGURE THE PROGRESS DIALOG **/
            pdNewTax = new ProgressDialog(TaxCreator.this);

            pdNewTax.setMessage("Please wait while the new Tax is being added.");
            pdNewTax.setIndeterminate(false);
            pdNewTax.setCancelable(true);
            pdNewTax.show();

            /** INSTANTIATE THE DATABASE HELPER CLASS **/
            db = new DBResto(TaxCreator.this);

            String strTaxName = edtTaxName.getText().toString();

            /** CONSTRUCT A QUERY TO FETCH TABLES ON RECORD **/
            String strQueryData = "SELECT * FROM taxes WHERE tax_name = '" + strTaxName + "'";

            /** CAST THE QUERY IN THE CURSOR TO FETCH THE RESULTS **/
            cursor = db.selectAllData(strQueryData);
        }

        @Override
        protected Void doInBackground(Void... params) {

            /** CHECK THAT THE DATABASE QUERY RETURNED SOME RESULTS **/
            if (cursor != null && cursor.getCount() != 0)	{
                /** LOOP THROUGH THE RESULT SET AND PARSE NECESSARY INFORMATION **/
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
					/* GET THE TABLE NUMBERS */
                    if (cursor.getString(cursor.getColumnIndex(db.TAX_NAME)) != null)	{
                        String strTaxName = cursor.getString(cursor.getColumnIndex(db.TAX_NAME));
                        if (strTaxName.equals(TAX_NAME))	{
							/* TOGGLE THE BOOLEAN TO INDICATE THE TABLE NUMBER EXISTS */
                            blnTaxExists = true;
                        } else {
							/* TOGGLE THE BOOLEAN TO INDICATE THE TABLE NUMBER DOES NOT EXIST */
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
                db.addTax(
                        TAX_NAME,
                        TAX_PERCENTAGE,
                        TAX_REGISTRATION,
                        COMPLETE_AMOUNT,
                        TAX_PERCENT_OF_AMOUNT);

                /** CLOSE THE CURSOR **/
                if (cursor != null && !cursor.isClosed())	{
                    cursor.close();
                }

                /** CLOSE THE DATABASE **/
                db.close();

                /** SET THE INTENT RESULT **/
                Intent tableCreated = new Intent();
                setResult(RESULT_OK, tableCreated);

                /** DISMISS THE DIALOG **/
                pdNewTax.dismiss();

                /** FINISH THE ACTIVITY **/
                finish();

            } else {
                /** SET THE ERROR MESSAGE **/
                edtTaxName.setError(getResources().getString(R.string.tax_creator_duplicate_tax));

                /** DISMISS THE DIALOG **/
                pdNewTax.dismiss();

            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}