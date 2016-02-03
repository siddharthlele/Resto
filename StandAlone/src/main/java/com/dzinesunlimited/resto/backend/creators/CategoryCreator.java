package com.dzinesunlimited.resto.backend.creators;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.dzinesunlimited.resto.R;
import com.dzinesunlimited.resto.utils.TypefaceSpan;
import com.dzinesunlimited.resto.utils.db.DBResto;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CategoryCreator extends AppCompatActivity {

    /***** DATABASE INSTANCE *****/
    private DBResto db;

    /****** DATATYPES FOR CATEGORY DETAILS *****/
    private String CATEGORY_NAME;
    private byte[] CATEGORY_THUMB;

    /***** DECLARE THE LAYOUT ELEMENTS *****/
    private AppCompatEditText edtCategoryName;
    private AppCompatImageView imgvwCategoryThumb;

    /***** A PROGRESS DIALOG INSTANCE *****/
    ProgressDialog pdNewCategory;

    /** BOOLEAN INSTANCE TO CHECK IF THE TAX EXISTS **/
    boolean blnCategoryExists = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.be_creator_category);

        /***** CONFIGURE THE ACTIONBAR *****/
        configAB();

        /***** CAST THE LAYOUT ELEMENTS *****/
        castLayoutElements();
    }

    /***** CAST THE LAYOUT ELEMENTS *****/
    private void castLayoutElements() {

        edtCategoryName = (AppCompatEditText) findViewById(R.id.edtCategoryName);
        imgvwCategoryThumb = (AppCompatImageView) findViewById(R.id.imgvwCategoryThumb);

        /***** GET THE CATEGORY IMAGE *****/
        imgvwCategoryThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyImage.openChooser(CategoryCreator.this, "Pick Image Source");
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, CategoryCreator.this, new DefaultCallback() {

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
        Picasso.with(CategoryCreator.this)
                .load(photoFile)
                .resize(800, 800)
                .centerInside()
                .into(target);
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {

        Toolbar myToolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);

        String strTitle = getResources().getString(R.string.cat_creator_title);
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(
                CategoryCreator.this, "RobotoCondensed-Regular.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(s);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(CategoryCreator.this);
        inflater.inflate(R.menu.category_creator, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:

                this.finish();

                break;

            case R.id.save:

                /***** CHECK FOR ALL DETAILS  *****/
                checkCatDetails();

                break;

            case R.id.cancel:

                /** CANCEL CATEGORY CREATION **/
                finish();

                break;

            default:
                break;
        }

        return false;
    }

    /***** CHECK FOR NECESSARY DETAILS *****/
    private void checkCatDetails() {

		/* HIDE THE KEYBOARD */
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtCategoryName.getWindowToken(), 0);

        /** CHECK THAT THE CATEGORY NAME AND THE CATEGORY THUMB ARE NOT EMPTY **/
        if (edtCategoryName.getText().toString().length() == 0 && CATEGORY_THUMB == null)	{
            edtCategoryName.setError("You need to enter the Category Name");
            String strMessage = "An image representing the Category is necssary to save it!";
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

    /***** CLASS TO CHECK FOR A UNIQUE CATEGORY NAME AND SAVE IF NAME IS UNIQUE *****/
    private class checkUniqueCategory extends AsyncTask<Void, Void, Void> {

        /** A CURSOR INSTANCE **/
        Cursor cursor;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /** INSTANTIATE AND CONFIGURE THE PROGRESSDIALOG **/
            pdNewCategory = new ProgressDialog(CategoryCreator.this);

            String strRestDtlProgress = "Please wait while we save the new Category";
            pdNewCategory.setMessage(strRestDtlProgress);
            pdNewCategory.setIndeterminate(false);
            pdNewCategory.setCancelable(true);
            pdNewCategory.show();

            /** INSTANTIATE THE DATABASE HELPER CLASS **/
            db = new DBResto(CategoryCreator.this);

            String strCategoryName = edtCategoryName.getText().toString().trim();

            /** CONSTRUCT A QUERY TO FETCH TABLES ON RECORD **/
            String strQueryData = "SELECT * FROM " + db.CATEGORY + " WHERE " + db.CATEGORY_NAME + " = '" + strCategoryName + "'";
//            Log.e("MEAL TYPE", strQueryData);

            /** CAST THE QUERY IN THE CURSOR TO FETCH THE RESULTS **/
            cursor = db.selectAllData(strQueryData);
        }

        @Override
        protected Void doInBackground(Void... params) {

            /** CHECK THAT THE DATABASE QUERY RETURNED SOME RESULTS **/
            if (cursor != null && cursor.getCount() != 0)	{
                /** LOOP THROUGH THE RESULT SET AND PARSE NECESSARY INFORMATION **/
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
					/* GET THE MEAL CATEGORY NAME */
                    if (cursor.getString(cursor.getColumnIndex(db.CATEGORY_NAME)) != null)	{
                        String strTypeName = cursor.getString(cursor.getColumnIndex(db.CATEGORY_NAME));
                        if (strTypeName.equals(CATEGORY_NAME))	{
							/* TOGGLE THE BOOLEAN TO INDICATE THE CATEGORY EXISTS */
                            blnCategoryExists = true;
                        } else {
							/* TOGGLE THE BOOLEAN TO INDICATE THE CATEGORY DOES NOT EXIST */
                            blnCategoryExists = false;
                        }
                    } else {
						/* TOGGLE THE BOOLEAN TO INDICATE THE CATEGORY DOES NOT EXIST */
                        blnCategoryExists = false;
                    }
                }
            } else {
				/* TOGGLE THE BOOLEAN TO INDICATE THE CATEGORY DOES NOT EXIST */
                blnCategoryExists = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            /** CHECK FOR EXISTING MEAL CATEGORY **/
            if (!blnCategoryExists)  {
                /***** GET THE CATEGORY NAME *****/
                CATEGORY_NAME = edtCategoryName.getText().toString().trim();

                /***** SAVE THE CATEGORY IN THE DATABASE *****/
                db.newMenuCategory(CATEGORY_NAME, CATEGORY_THUMB);

                /** CLOSE THE CURSOR **/
                if (cursor != null && !cursor.isClosed())	{
                    cursor.close();
                }

                /** GET THE RECENTLY CREATED CATEGORY ID **/
                String strQueryCat = "SELECT * FROM " + db.CATEGORY + " ORDER BY " + db.CATEGORY_ID + " DESC LIMIT 1";
                Log.e("QUERY", strQueryCat);
                Cursor cur = db.selectAllData(strQueryCat);
                String strCatID = null;
                String strCatName = null;

                /** CHECK THAT THE DATABASE QUERY RETURNED SOME RESULTS **/
                if (cur != null && cur.getCount() != 0)	{

                    /** LOOP THROUGH THE RESULT SET AND PARSE NECESSARY INFORMATION **/
                    for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {

                        /* GET THE LAST CATEGORY ID */
                        if (cur.getString(cur.getColumnIndex(db.CATEGORY_ID)) != null)	{
                            strCatID = cur.getString(cur.getColumnIndex(db.CATEGORY_ID));
                        } else {
                            strCatID = "1";
                        }

                        /* GET THE LAST CATEGORY NAME */
                        if (cur.getString(cur.getColumnIndex(db.CATEGORY_NAME)) != null)	{
                            strCatName = cur.getString(cur.getColumnIndex(db.CATEGORY_NAME));
                        } else {
                            strCatName = null;
                        }
                    }
                } else {
                    strCatID = "1";
                    strCatName = null;
                }

                /** CLOSE THE DATABASE **/
                db.close();

                /** DISMISS THE DIALOG **/
                pdNewCategory.dismiss();

                Intent success = new Intent();
                setResult(RESULT_OK, success);

                /** FINISH THE TASK **/
                finish();
            } else {
                /** SET THE ERROR MESSAGE **/
                edtCategoryName.setError("This Meal Category already exists on record. Please choose a different name");

                /** DISMISS THE DIALOG **/
                pdNewCategory.dismiss();

            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}