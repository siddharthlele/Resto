package com.dzinesunlimited.resto.backend.creators;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.dzinesunlimited.resto.R;
import com.dzinesunlimited.resto.utils.TypefaceSpan;
import com.dzinesunlimited.resto.utils.db.DBResto;
import com.dzinesunlimited.resto.utils.helpers.adapters.backend.MenuCategoriesAdapter;
import com.dzinesunlimited.resto.utils.helpers.adapters.backend.MenuServesAdapter;
import com.dzinesunlimited.resto.utils.helpers.pojos.MenuCategoryData;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MenuCreator extends AppCompatActivity {

    /** THE DATABASE HELPER INSTANCE **/
    private DBResto db;

    /***** DECLARE THE LAYOUT ELEMENTS *****/
    private AppCompatSpinner spnMenuCategory;
    private TextInputLayout inputDishName;
    private AppCompatEditText edtDishName;
    private TextInputLayout inputDishDescription;
    private AppCompatEditText edtDishDescription;
    private TextInputLayout inputDishPrice;
    private AppCompatEditText edtDishPrice;
    private AppCompatSpinner spnServes;
    private AppCompatImageView imgvwAddPhoto;

    /***** DATA TYPES TO HOLD DISH DETAILS *****/
    private String MEAL_NAME;
    private String MEAL_DESCRIPTION;
    private String MEAL_PRICE;
    private String MEAL_TYPE = "Veg";
    private String MEAL_SERVES;
    private byte[] MEAL_IMAGE;

    /***** ADAPTER AND ARRAYLIST FOR MENU CATEGORIES *****/
    private ArrayList<MenuCategoryData> arrCategory = new ArrayList<>();

    /***** DATA TYPES TO HOLD VARIOUS DISH DETAILS *****/
    private String SELECTED_CATEGORY_ID;

    /***** THE REQUEST ID FOR A NEW CATEGORY *****/
    private static final int ACTION_REQUEST_NEW_CATEGORY = 101;

    /** A PROGRESS DIALOG INSTANCE TO SHOW THE PROGRESS **/
    private ProgressDialog pdNewMenu;

    /** BOOLEAN INSTANCE TO CHECK IF THE DISH / MENU NAME EXISTS **/
    private boolean blnMenuExists = false;

    /** IMAGE SOURCE **/
    int imgSource = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.be_creator_menu);

        /***** CONFIGURE THE ACTIONBAR *****/
        configAB();

        /***** CAST THE LAYOUT ELEMENTS *****/
        castLayoutElements();

        /***** FETCH THE MENU CATEGORIES *****/
        new fetchMenuCategories().execute();

        /***** SELECT A MENU CATEGORY FOR THE DISH TO GO IN *****/
        spnMenuCategory.setOnItemSelectedListener(selectCategory);
    }

    /***** SELECT A MENU CATEGORY FOR THE DISH TO GO IN *****/
    private final AdapterView.OnItemSelectedListener selectCategory = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parentView, View v, int position, long id) {

			/* GET THE CURRENT SELECTED MENU CATEGORY */
            SELECTED_CATEGORY_ID = arrCategory.get(position).getCatID();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parentView) {

        }
    };

    /***** FETCH THE MENU CATEGORIES *****/
    private class fetchMenuCategories extends AsyncTask<Void, Void, Void> {

        /** A CURSOR INSTANCE **/
        Cursor cursor;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

//			/***** SHOW THE PROGRESS WHILE LOADING THE CATEGORIES AND THE MENU IN THEM *****/
//			linlaHeaderProgress.setVisibility(View.VISIBLE);

            /** INSTANTIATE THE DATABASE HELPER CLASS **/
            db = new DBResto(MenuCreator.this);

            /** CONSTRUCT THE QUERY TO FETCH ALL MENU CATEGORIES FROM THE DATABASE **/
            String strQueryData = "SELECT * FROM " + db.CATEGORY;

            /** CAST THE QUERY IN THE CURSOR TO FETCH THE RESULTS **/
            cursor = db.selectAllData(strQueryData);
//			Log.e("CURSOR", DatabaseUtils.dumpCursorToString(cursor));
        }

        @Override
        protected Void doInBackground(Void... params) {

            /** CHECK THAT THE DATABASE QUERY RETURNED SOME RESULTS **/
            if (cursor != null && cursor.getCount() != 0)	{

				/* AN INSTANCE OF THE MenuCategoryData POJO CLASS */
                MenuCategoryData menuCategory;

                /** LOOP THROUGH THE RESULT SET AND PARSE NECESSARY INFORMATION **/
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                    /***** INSTANTIATE THE MenuCreatorCategoryData INSTANCE "menuCategory" *****/
                    menuCategory = new MenuCategoryData();

                    /** GET THE CATEGORY ID **/
                    if (cursor.getString(cursor.getColumnIndex(db.CATEGORY_ID)) != null)	{
                        String strMenuCatID = cursor.getString(cursor.getColumnIndex(db.CATEGORY_ID));
                        menuCategory.setCatID(strMenuCatID);
                    } else {
                        menuCategory.setCatID(null);
                    }

                    /** GET THE CATEGORY NAME **/
                    if (cursor.getString(cursor.getColumnIndex(db.CATEGORY_NAME)) != null)	{
                        String strCatName = cursor.getString(cursor.getColumnIndex(db.CATEGORY_NAME));
                        menuCategory.setCatName(strCatName);
                    } else {
                        menuCategory.setCatName(null);
                    }

                    /** ADD THE COLLECTED DATA TO THE ARRAYLIST **/
                    arrCategory.add(menuCategory);

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
            db.close();

            /** SET THE ADAPTER TO THE SPINNER **/
            spnMenuCategory.setAdapter(new MenuCategoriesAdapter(
                    MenuCreator.this,
                    R.layout.custom_spinner_row,
                    arrCategory));
        }
    }

    /***** CAST THE LAYOUT ELEMENTS *****/
    private void castLayoutElements() {

		/* CATEGORY SELECTOR */
        spnMenuCategory = (AppCompatSpinner) findViewById(R.id.spnMenuCategory);
        AppCompatTextView txtAddNewCategory = (AppCompatTextView) findViewById(R.id.txtAddNewCategory);

		/* MENU / DISH DETAILS */
        inputDishName = (TextInputLayout) findViewById(R.id.inputDishName);
        edtDishName = (AppCompatEditText) findViewById(R.id.edtDishName);
        inputDishDescription = (TextInputLayout) findViewById(R.id.inputDishDescription);
        edtDishDescription = (AppCompatEditText) findViewById(R.id.edtDishDescription);
        inputDishPrice = (TextInputLayout) findViewById(R.id.inputDishPrice);
        edtDishPrice = (AppCompatEditText) findViewById(R.id.edtDishPrice);
        RadioGroup rdgDishType = (RadioGroup) findViewById(R.id.rdgDishType);
        spnServes = (AppCompatSpinner) findViewById(R.id.spnServes);
        imgvwAddPhoto = (AppCompatImageView) findViewById(R.id.imgvwAddPhoto);

        /** CHANGE THE MEAL TYPE **/
        rdgDishType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

                switch (checkedId) {
                    case R.id.rdbtnVeg:
                        /** SET THE DISH TYPE TO "VEG" **/
                        MEAL_TYPE = "Veg";
                        break;
                    case R.id.rdbtnNonVeg:
                        /** SET THE DISH TYPE TO "NON-VEG" **/
                        MEAL_TYPE = "Non-veg";
                        break;
                    default:
                        break;
                }
            }
        });

        /***** CREATE A NEW CATEGORY *****/
        txtAddNewCategory.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent newCategory = new Intent(MenuCreator.this, CategoryCreator.class);
                startActivityForResult(newCategory, ACTION_REQUEST_NEW_CATEGORY);
            }
        });

        /***** SELECT A PHOTO FOR THE DISH *****/
        imgvwAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyImage.openChooser(MenuCreator.this, "Pick Image Source", true);
            }
        });

        /** POPULATE THE SPINNER **/
        String[] strServes = getResources().getStringArray(R.array.menuServes);
        final List<String> arrServes;
        arrServes = Arrays.asList(strServes);
        spnServes.setAdapter(new MenuServesAdapter(
                MenuCreator.this,
                R.layout.custom_spinner_row,
                arrServes));

        /** CHANGE THE MENU SERVES NUMBER **/
        spnServes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getApplication(), arrServes.get(position), Toast.LENGTH_SHORT).show();
                MEAL_SERVES = arrServes.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == ACTION_REQUEST_NEW_CATEGORY)    {

            /** CLEAR THE ARRAYLIST AND REFRESH THE SPINNER CATEGORIES DATA **/
            arrCategory.clear();
            new fetchMenuCategories().execute();

        } else if (resultCode == RESULT_OK) {
            EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {

                @Override
                public void onImagePicked(File imageFile, EasyImage.ImageSource source) {
                    super.onImagePicked(imageFile, source);
                    onPhotoReturned(imageFile);
//                Log.e("DATA", String.valueOf(data));

                    if (source == EasyImage.ImageSource.CAMERA) {
                        imgSource = 1;
                    } else {
                        imgSource = 2;
                    }
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

            Bitmap BMP_MEAL_IMAGE;

            if (imgSource == 1) {
                BMP_MEAL_IMAGE = resizeCameraBitmap(bitmap);

                imgvwAddPhoto.setImageBitmap(BMP_MEAL_IMAGE);
                imgvwAddPhoto.setScaleType(AppCompatImageView.ScaleType.CENTER_CROP);

                /** CONVERT THE BITMAP TO A BYTE ARRAY **/
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                BMP_MEAL_IMAGE.compress(Bitmap.CompressFormat.PNG, 100, bos);
                MEAL_IMAGE = bos.toByteArray();

            } else if (imgSource == 2)  {
                imgvwAddPhoto.setImageBitmap(bitmap);
                imgvwAddPhoto.setScaleType(AppCompatImageView.ScaleType.CENTER_CROP);

                /** CONVERT THE BITMAP TO A BYTE ARRAY **/
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                MEAL_IMAGE = bos.toByteArray();
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    /** RESIZE THE CAMERA IMAGE **/
    private Bitmap resizeCameraBitmap(Bitmap bitmap) {

        return Bitmap.createScaledBitmap(
                bitmap,
                (int)(bitmap.getWidth() * 0.4),
                (int)(bitmap.getHeight() * 0.4),
                true);
    }

    private void onPhotoReturned(File photoFile) {
        Picasso.with(this)
                .load(photoFile)
                .into(target);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(MenuCreator.this);
        inflater.inflate(R.menu.menu_creator, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:

                this.finish();

                break;

            case R.id.save:

                /** CHECK FOR EMPTY FIELDS **/
                checkEmpty();

                break;

            case R.id.cancel:

                /** CLEAR ALL FIELDS **/

                break;

            default:
                break;
        }

        return false;
    }

    /***** CHECK FOR EMPTY FIELDS *****/
    private void checkEmpty() {

		/* HIDE THE KEYBOARD */
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtDishName.getWindowToken(), 0);

        /** GET THE REQUIRED DATA **/
        MEAL_NAME = edtDishName.getText().toString();
        MEAL_DESCRIPTION = edtDishDescription.getText().toString();
        MEAL_PRICE = edtDishPrice.getText().toString();

        if (MEAL_NAME.length() == 0) {
            inputDishName.setError("Menu / Dish name cannot be empty!");
            edtDishName.requestFocus();
        } else if (MEAL_DESCRIPTION.length() == 0) {
            inputDishDescription.setError("Please provide the Menu / Dish Description");
            edtDishDescription.requestFocus();
        } else if (MEAL_PRICE.length() == 0) {
            inputDishPrice.setError("The Menu / Dish needs to have a Price");
            edtDishPrice.requestFocus();
        } else {
            /* REMOVE THE ERRORS (IF SHOWN) */
            inputDishName.setErrorEnabled(false);
            inputDishDescription.setErrorEnabled(false);
            inputDishPrice.setErrorEnabled(false);

            /* CHECK FOR UNIQUE MENU / DISH NAME */
            new checkUniqueDishName().execute();
        }
    }

    private class checkUniqueDishName extends AsyncTask<Void, Void, Void>   {

        /** A CURSOR INSTANCE **/
        Cursor cursor;

        /* A STRING INSTANCE TO GET THE DISH NAME FROM THE EDITTEXT */
        String DISH_NAME;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /** INSTANTIATE THE DATABASE HELPER CLASS **/
            db = new DBResto(MenuCreator.this);

            /** INSTANTIATE AND CONFIGURE THE PROGRESS DIALOG **/
            pdNewMenu = new ProgressDialog(MenuCreator.this);
            pdNewMenu.setMessage("Please wait while the new Dish / Menu is being created.");
            pdNewMenu.setIndeterminate(false);
            pdNewMenu.setCancelable(true);
            pdNewMenu.show();

            /* GET THE DISH NAME FROM THE EDITTEXT */
            DISH_NAME = edtDishName.getText().toString();

			/* CONSTRUCT A QUERY TO FETCH DISHES / MENUS ON RECORD */
            String strQueryData = "SELECT * FROM " + db.MENU + " WHERE " + db.MENU_NAME + " = '" + DISH_NAME + "'";
//			Log.e("VERIFY TABLE QUERY", strQueryData);

			/* CAST THE QUERY IN THE CURSOR TO FETCH THE RESULTS */
            cursor = db.selectAllData(strQueryData);
        }

        @Override
        protected Void doInBackground(Void... voids) {

			/* CHECK THAT THE DATABASE QUERY RETURNED SOME RESULTS */
            if (cursor != null && cursor.getCount() != 0)	{
				/* LOOP THROUGH THE RESULT SET AND PARSE NECESSARY INFORMATION */
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
					/* GET THE MENU NAMES */
                    if (cursor.getString(cursor.getColumnIndex(db.MENU_NAME)) != null)	{
                        String strMenuName = cursor.getString(cursor.getColumnIndex(db.MENU_NAME));
                        if (strMenuName.equals(DISH_NAME))	{
							/* TOGGLE THE BOOLEAN TO INDICATE THE DISH / MENU EXISTS */
                            blnMenuExists = true;
                        } else {
							/* TOGGLE THE BOOLEAN TO INDICATE THE DISH / MENU DOES NOT EXIST */
                            blnMenuExists = false;
                        }
                    } else {
						/* TOGGLE THE BOOLEAN TO INDICATE THE DISH / MENU DOES NOT EXIST */
                        blnMenuExists = false;
                    }
                }
            } else {
				/* TOGGLE THE BOOLEAN TO INDICATE THE DISH / MENU DOES NOT EXIST */
                blnMenuExists = false;
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

            /***** CHECK IF THE DISH / MENU EXISTS *****/
            if (!blnMenuExists)	{

                /***** PROCEED TO ADD THE NEW DISH / MENU TO THE DATABASE *****/
                new addNewMenu().execute();

            } else {
				/* SET THE ERROR MESSAGE */
                //TODO: ADD THE ERROR TO THE STRINGS.XML
                edtDishName.setError("The Dish / Menu already exists. Please choose a different name");

				/* DISMISS THE DIALOG */
                pdNewMenu.dismiss();
            }
        }
    }

    /***** SAVE THE NEW DISH / MENU TO THE DATABASE *****/
    private class addNewMenu extends AsyncTask<Void, Void, Void>    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /** INSTANTIATE THE DATABASE HELPER CLASS **/
            db = new DBResto(MenuCreator.this);
        }

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            /***** ADD THE COLLECTED DATA AND SAVE THE NEW DISH IN THE MEALS TABLE *****/
            db.newDishItem(
                    SELECTED_CATEGORY_ID, MEAL_NAME, MEAL_DESCRIPTION,
                    MEAL_PRICE, MEAL_TYPE, MEAL_SERVES, MEAL_IMAGE);

            Intent createdIngCategory = new Intent();
            setResult(RESULT_OK, createdIngCategory);

            /** CLOSE THE DATABASE **/
            db.close();

            /** DISMISS THE DIALOG **/
            pdNewMenu.dismiss();

            /* FINISH THE ACTIVITY */
            finish();
        }
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {

        Toolbar myToolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);

        String strTitle = getResources().getString(R.string.menu_creator_title);
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(
                MenuCreator.this, "RobotoCondensed-Regular.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(s);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}