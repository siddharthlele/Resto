package com.dzinesunlimited.resto.backend.modifiers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.dzinesunlimited.resto.R;
import com.dzinesunlimited.resto.backend.admin.frags.PrinterDiscovery;
import com.dzinesunlimited.resto.utils.TypefaceSpan;
import com.dzinesunlimited.resto.utils.db.DBResto;
import com.dzinesunlimited.resto.utils.helpers.adapters.backend.PrintersAdapter;
import com.dzinesunlimited.resto.utils.helpers.pojos.PrinterData;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CategoryModifier extends AppCompatActivity {

    /** THE INCOMING CATEGORY ID **/
    private String INCOMING_CATEGORY_ID = null;
    private String INCOMING_CATEGORY_NAME = null;

    /***** DATABASE INSTANCE *****/
    private DBResto db;

    /****** DATATYPES FOR CATEGORY DETAILS *****/
    private String CATEGORY_NAME = null;
    private byte[] CATEGORY_THUMB = null;
    private String CATEGORY_PRINTER = null;

    /***** DECLARE THE LAYOUT ELEMENTS *****/
    private AppCompatEditText edtCategoryName;
    private AppCompatSpinner spnPrinters;
    private AppCompatTextView txtAddPrinter;
    private AppCompatImageView imgvwCategoryThumb;

    /** THE ARRAYLIST FOR THE PRINTERS **/
    ArrayList<PrinterData> arrPrinters = new ArrayList<>();

    /** THE REQUEST FOR A NEW PRINTER **/
    private static final int ACTION_REQUEST_NEW_PRINTER = 101;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.be_modifier_category);

        /***** CAST THE LAYOUT ELEMENTS *****/
        castLayoutElements();

        /***** CONFIGURE THE ACTIONBAR *****/
        configAB();

        /** FETCH THE LIST OF PRINTERS **/
        new fetchPrintersList().execute();

        /** FETCH THE INCOMING DATA **/
        fetchIncomingData();

        /** SELECT THE PRINTER THE NEW CATEGORY PRINTS TO **/
        spnPrinters.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CATEGORY_PRINTER = arrPrinters.get(position).getPrinterID();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /** TASK TO FETCH A LIST OF ALL PRINTERS **/
    private class fetchPrintersList extends AsyncTask<Void, Void, Void> {

        /** A CURSOR INSTANCE **/
        Cursor cursor;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /** INSTANTIATE THE DATABASE HELPER CLASS **/
            db = new DBResto(CategoryModifier.this);

            /** CONSTRUCT THE QUERY TO FETCH ALL TWEETS FROM THE DATABASE **/
            String strQueryData = "SELECT * FROM " + db.PRINTERS;

            /** CAST THE QUERY IN THE CURSOR TO FETCH THE RESULTS **/
            cursor = db.selectAllData(strQueryData);
        }

        @Override
        protected Void doInBackground(Void... params) {

            /** CHECK THAT THE DATABASE QUERY RETURNED SOME RESULTS **/
            if (cursor != null && cursor.getCount() != 0)	{

            /* AN INSTANCE OF THE PrinterData POJO CLASS */
                PrinterData printerData;

                /** LOOP THROUGH THE RESULT SET AND PARSE NECESSARY INFORMATION **/
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                    /***** INSTANTIATE THE PrinterData INSTANCE "printerData" *****/
                    printerData = new PrinterData();

                    /** GET THE PRINTER ID **/
                    if (cursor.getString(cursor.getColumnIndex(db.PRINTER_ID)) != null)	{
                        String PRINTER_ID = cursor.getString(cursor.getColumnIndex(db.PRINTER_ID));
                        printerData.setPrinterID(PRINTER_ID);
                    } else {
                        printerData.setPrinterID(null);
                    }

                    /** GET THE PRINTER NAME **/
                    if (cursor.getString(cursor.getColumnIndex(db.PRINTER_NAME)) != null)	{
                        String PRINTER_NAME = cursor.getString(cursor.getColumnIndex(db.PRINTER_NAME));
                        printerData.setPrinterName(PRINTER_NAME);
                    } else {
                        printerData.setPrinterName(null);
                    }

//                    /** GET THE PRINTER_SELECTED_NAME **/
//                    if (cursor.getString(cursor.getColumnIndex(db.PRINTER_SELECTED_NAME)) != null)	{
//                        String PRINTER_SELECTED_NAME = cursor.getString(cursor.getColumnIndex(db.PRINTER_SELECTED_NAME));
//                        printerData.setPrinterSelectedName(PRINTER_SELECTED_NAME);
//                    } else {
//                        printerData.setPrinterSelectedName(null);
//                    }

                    /** ADD THE COLLECTED DATA TO THE ARRAYLIST **/
                    arrPrinters.add(printerData);
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

            /** SET THE ADAPTER TO THE SPINNER **/
            spnPrinters.setAdapter(new PrintersAdapter(
                    CategoryModifier.this,
                    R.layout.custom_spinner_row,
                    arrPrinters));
        }
    }

    /** FETCH THE CATEGORY DETAILS **/
    private class fetchCategoryDetails extends AsyncTask<Void, Void, Void>  {

        /** A CURSOR INSTANCE **/
        Cursor cursor;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /** INSTANTIATE THE DATABASE INSTANCE **/
            db = new DBResto(CategoryModifier.this);

            /** CONSTRUCT THE QUERY **/
            String s = "SELECT * FROM " + db.CATEGORY + " WHERE " + db.CATEGORY_ID + " = " + INCOMING_CATEGORY_ID;

            /** FETCH THE RESULT **/
            cursor = db.selectAllData(s);
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (cursor != null && cursor.getCount() != 0) {
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    /** GET THE CATEGORY NAME **/
                    if (cursor.getString(cursor.getColumnIndex(db.CATEGORY_NAME)) != null)	{
                        CATEGORY_NAME = cursor.getString(cursor.getColumnIndex(db.CATEGORY_NAME));
                    } else {
                        CATEGORY_NAME = null;
                    }

                    /** GET THE CATEGORY IMAGE **/
                    if (cursor.getBlob(cursor.getColumnIndex(db.CATEGORY_IMAGE)) != null)	{
                        CATEGORY_THUMB = cursor.getBlob(cursor.getColumnIndex(db.CATEGORY_IMAGE));
                    } else {
                        CATEGORY_THUMB = null;
                    }

                    /** GET THE CATEGORY PRINTER **/
                    if (cursor.getString(cursor.getColumnIndex(db.CATEGORY_PRINTER_ID)) != null)	{
                        CATEGORY_PRINTER = cursor.getString(cursor.getColumnIndex(db.CATEGORY_PRINTER_ID));
                    } else {
                        CATEGORY_PRINTER = null;
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

            /***** SET THE COLLECTED DATA *****/
            if (CATEGORY_NAME != null)  {
                edtCategoryName.setText(CATEGORY_NAME);
            }

            if (CATEGORY_THUMB != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(CATEGORY_THUMB, 0, CATEGORY_THUMB.length);
                imgvwCategoryThumb.setScaleType(AppCompatImageView.ScaleType.CENTER_CROP);
                imgvwCategoryThumb.setImageBitmap(bitmap);
            }
        }
    }

    private void fetchIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey("CATEGORY_ID") && bundle.containsKey("CATEGORY_NAME"))  {
            INCOMING_CATEGORY_NAME = bundle.getString("CATEGORY_NAME");
            INCOMING_CATEGORY_ID = bundle.getString("CATEGORY_ID");
            if (INCOMING_CATEGORY_ID != null)   {
                new fetchCategoryDetails().execute();
            } else {
                //TODO: SHOW AN ERROR
            }
        } else {
            //TODO: SHOW AN ERROR
        }
    }

    /***** CAST THE LAYOUT ELEMENTS *****/
    private void castLayoutElements() {

        edtCategoryName = (AppCompatEditText) findViewById(R.id.edtCategoryName);
        spnPrinters = (AppCompatSpinner) findViewById(R.id.spnPrinters);
        txtAddPrinter = (AppCompatTextView) findViewById(R.id.txtAddPrinter);
        imgvwCategoryThumb = (AppCompatImageView) findViewById(R.id.imgvwCategoryThumb);

        /** ADD A PRINTER TO THE DATABASE **/
        txtAddPrinter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newPrinter = new Intent(CategoryModifier.this, PrinterDiscovery.class);
                startActivityForResult(newPrinter, ACTION_REQUEST_NEW_PRINTER);
            }
        });

        /***** GET THE CATEGORY IMAGE *****/
        imgvwCategoryThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyImage.openChooser(CategoryModifier.this, "Pick Image Source", true);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == ACTION_REQUEST_NEW_PRINTER)    {

            /** CLEAR THE ARRAYLIST AND REFRESH THE LIST OF PRINTERS **/
            arrPrinters.clear();
            new fetchPrintersList().execute();

        } else if (resultCode == RESULT_OK) {
            EasyImage.handleActivityResult(requestCode, resultCode, data, CategoryModifier.this, new DefaultCallback() {

                @Override
                public void onImagePicked(File imageFile, EasyImage.ImageSource source) {
                    super.onImagePicked(imageFile, source);
                    onPhotoReturned(imageFile);
                }

                @Override
                public void onImagePickerError(Exception e, EasyImage.ImageSource source) {
                    super.onImagePickerError(e, source);
                }

                @Override
                public void onCanceled(EasyImage.ImageSource source) {
                    super.onCanceled(source);
                }
            });
        }
    }

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            imgvwCategoryThumb.setImageBitmap(bitmap);

            /** CONVERT THE BITMAP TO A BYTE ARRAY **/
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            CATEGORY_THUMB = bos.toByteArray();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    private void onPhotoReturned(File photoFile) {
        Picasso.with(CategoryModifier.this)
                .load(photoFile)
                .resize(800, 800)
                .centerInside()
                .into(target);
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {

        Toolbar myToolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);

        String strTitle = getResources().getString(R.string.category_modifier_title);
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(
                CategoryModifier.this, "RobotoCondensed-Regular.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(s);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(CategoryModifier.this);
        inflater.inflate(R.menu.category_creator, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i1 = new Intent();
                setResult(RESULT_CANCELED, i1);
                finish();
                break;

            case R.id.save:
                validateData();
                break;

            case R.id.cancel:
                Intent i2 = new Intent();
                setResult(RESULT_CANCELED, i2);
                finish();
                break;

            default:
                break;
        }

        return false;
    }

    /***** VALIDATE REQUIRED DATA *****/
    private void validateData() {

		/** HIDE THE KEYBOARD **/
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtCategoryName.getWindowToken(), 0);

        /** CHECK THAT THE CATEGORY NAME AND THE CATEGORY THUMB ARE NOT EMPTY **/
        if (edtCategoryName.getText().toString().length() == 0 && CATEGORY_THUMB == null)	{
            edtCategoryName.setError("You need to enter the Category Name");
            String strMessage = "An image representing the Category is necessary to save it!";
            Toast.makeText(getApplicationContext(), strMessage, Toast.LENGTH_SHORT).show();
        } else if (edtCategoryName.getText().toString().length() == 0 && CATEGORY_THUMB != null) {
            edtCategoryName.setError("You need to enter the Category Name");
        } else if (edtCategoryName.getText().toString().length() != 0 && CATEGORY_THUMB == null) {
            String strMessage = "An image representing the Category is necessary to save it!";
            Toast.makeText(getApplicationContext(), strMessage, Toast.LENGTH_SHORT).show();
        } else {
            /***** CHECK FOR UNIQUE MEAL CATEGORY *****/
            new checkUniqueCategory().execute();
        }
    }

    /** CHECK FOR A UNIQUE CATEGORY NAME (IGNORE IF NAME IS SAME AS BEFORE) AND UPDATE **/
    private class checkUniqueCategory extends AsyncTask<Void, Void, Void>   {

        /** A PROGRESS DIALOG INSTANCE **/
        ProgressDialog pdUpdateCategory;

        /** A CURSOR INSTANCE **/
        Cursor cursor;

        /** A BOOLEAN TO TRACK EXISTING CATEGORY NAME **/
        boolean blnCatExists = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /** INSTANTIATE AND CONFIGURE THE PROGRESS DIALOG **/
            pdUpdateCategory = new ProgressDialog(CategoryModifier.this);
            pdUpdateCategory.setMessage("Please wait while the Category is being updated....");
            pdUpdateCategory.setIndeterminate(false);
            pdUpdateCategory.setCancelable(false);
            pdUpdateCategory.show();

            /** INSTANTIATE THE DATABASE INSTANCE **/
            db = new DBResto(CategoryModifier.this);

            /** CONSTRUCT THE QUERY **/
            String s = "SELECT * FROM " + db.CATEGORY + " WHERE " + db.CATEGORY_ID + " = " + INCOMING_CATEGORY_ID;

            /** FETCH RESULTS FROM THE DATABASE **/
            cursor = db.selectAllData(s);
        }

        @Override
        protected Void doInBackground(Void... params) {

            /** CHECK THAT THE DATABASE QUERY RETURNED SOME RESULTS **/
            if (cursor != null && cursor.getCount() != 0)	{
                /** LOOP THROUGH THE RESULT SET AND PARSE NECESSARY INFORMATION **/
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    /** GET THE CATEGORY NAME **/
                    if (cursor.getString(cursor.getColumnIndex(db.CATEGORY_NAME)) != null)	{
                        String catName = cursor.getString(cursor.getColumnIndex(db.CATEGORY_NAME));
                        if (catName.equals(CATEGORY_NAME))	{
                            if (CATEGORY_NAME.equalsIgnoreCase(INCOMING_CATEGORY_NAME))   {
							    /* TOGGLE THE BOOLEAN TO INDICATE THE CATEGORY DOES NOT EXIST */
                                blnCatExists = false;
                            } else {
							    /* TOGGLE THE BOOLEAN TO INDICATE THE CATEGORY EXISTS */
                                blnCatExists = true;
                            }
                        } else {
							/* TOGGLE THE BOOLEAN TO INDICATE THE CATEGORY DOES NOT EXIST */
                            blnCatExists = false;
                        }
                    } else {
						/* TOGGLE THE BOOLEAN TO INDICATE THE CATEGORY DOES NOT EXIST */
                        blnCatExists = false;
                    }
                }
            } else {
                blnCatExists = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (!blnCatExists)  {

                /** UPDATE THE CATEGORY **/
                db.updateCategory(
                        INCOMING_CATEGORY_ID, edtCategoryName.getText().toString().trim(), CATEGORY_THUMB, Integer.valueOf(CATEGORY_PRINTER));

                /** CLOSE THE DATABASE **/
                db.close();

                /** CLOSE THE CURSOR **/
                if (cursor != null && !cursor.isClosed())	{
                    cursor.close();
                }

                /** DISMISS THE PROGRESS DIALOG **/
                pdUpdateCategory.dismiss();

                /***** SET THE RESULT TO "RESULT_OK" AND FINISH THE ACTIVITY *****/
                Intent updatedCategory = new Intent();
                setResult(RESULT_OK, updatedCategory);

                /** FINISH THE ACTIVITY **/
                finish();

            } else {

                /** DISMISS THE DIALOG **/
                pdUpdateCategory.dismiss();

                /** SHOW THE OVERWRITE PROMPT **/
                String strTitle = getResources().getString(R.string.cat_modifier_duplicate_error_title);
                String strMessage = getResources().getString(R.string.cat_modifier_duplicate_error_message);
                String strNeutral = getResources().getString(R.string.generic_mb_ok);

                /** CONFIGURE THE ALERTDIALOG **/
                new MaterialDialog.Builder(CategoryModifier.this)
                        .icon(ContextCompat.getDrawable(CategoryModifier.this, R.drawable.ic_info_outline_black_24dp))
                        .title(strTitle)
                        .content(strMessage)
                        .theme(Theme.LIGHT)
                        .typeface("HelveticaNeueLTW1G-MdCn.otf", "HelveticaNeueLTW1G-Cn.otf")
                        .neutralText(strNeutral)
                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}