package com.dzinesunlimited.resto.backend.modifiers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.RadioGroup;
import android.widget.Toast;

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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MenuModifier extends AppCompatActivity {

    /** THE INCOMING MENU ID AND NAME **/
    private String INCOMING_MENU_ID = null;
    private String INCOMING_MENU_NAME = null;

    /** THE DATABASE INSTANCE **/
    DBResto db;

    /** DECLARE THE LAYOUT ELEMENTS **/
    @Bind(R.id.spnMenuCategory)AppCompatSpinner spnMenuCategory;
    @Bind(R.id.inputDishName)TextInputLayout inputDishName;
    @Bind(R.id.edtDishName)AppCompatEditText edtDishName;
    @Bind(R.id.inputDishDescription)TextInputLayout inputDishDescription;
    @Bind(R.id.edtDishDescription)AppCompatEditText edtDishDescription;
    @Bind(R.id.inputDishPrice)TextInputLayout inputDishPrice;
    @Bind(R.id.edtDishPrice)AppCompatEditText edtDishPrice;
    @Bind(R.id.rdgDishType)RadioGroup rdgDishType;
    @Bind(R.id.rdbtnVeg)AppCompatRadioButton rdbtnVeg;
    @Bind(R.id.rdbtnNonVeg)AppCompatRadioButton rdbtnNonVeg;
    @Bind(R.id.spnServes)AppCompatSpinner spnServes;
    @Bind(R.id.imgvwAddPhoto)AppCompatImageView imgvwAddPhoto;
    @OnClick(R.id.imgvwAddPhoto) protected void GetImage() {
        EasyImage.openChooser(MenuModifier.this, "Pick Image Source", true);
    }

    /***** DATA TYPES TO HOLD DISH DETAILS *****/
    private String MENU_CATEGORY_ID = null;
    private String MENU_NAME;
    private String MENU_DESCRIPTION;
    private String MENU_PRICE;
    private String MENU_TYPE = "Veg";
    private String MENU_SERVES;
    private byte[] MENU_IMAGE;

    /***** ADAPTER AND ARRAYLIST FOR MENU CATEGORIES *****/
    private ArrayList<MenuCategoryData> arrCategory = new ArrayList<>();
    private List<String> arrServes = new ArrayList<>();

    /***** DATA TYPES TO HOLD VARIOUS DISH DETAILS *****/
    private String SELECTED_CATEGORY_ID;

    /***** THE REQUEST ID FOR A NEW CATEGORY *****/
    private static final int ACTION_REQUEST_NEW_CATEGORY = 101;

    /** IMAGE SOURCE **/
    int imgSource = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.be_modifier_menu);
        ButterKnife.bind(this);

        /***** CONFIGURE THE ACTIONBAR *****/
        configAB();

        /***** FETCH THE MENU CATEGORIES *****/
        new fetchMenuCategories().execute();

        /** POPULATE THE SPINNER **/
        String[] strServes = getResources().getStringArray(R.array.menuServes);
        arrServes = Arrays.asList(strServes);
        spnServes.setAdapter(new MenuServesAdapter(
                MenuModifier.this,
                R.layout.custom_spinner_row,
                arrServes));

        /** CHANGE THE MENU SERVES NUMBER **/
        spnServes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MENU_SERVES = arrServes.get(position);
                Log.e("MENU SERVES", MENU_SERVES);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        /** CHANGE THE DISH TYPE **/
        rdgDishType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.rdbtnVeg:
                        /** SET THE DISH TYPE TO "VEG" **/
                        MENU_TYPE = "Veg";
                        break;
                    case R.id.rdbtnNonVeg:
                        /** SET THE DISH TYPE TO "NON-VEG" **/
                        MENU_TYPE = "Non-veg";
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /** FETCH EXISTING DISH DETAILS **/
    private class fetchDishDetails extends AsyncTask<Void, Void, Void>  {

        /** A CURSOR INSTANCE **/
        Cursor cursor;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /** INSTANTIATE THE DATABASE INSTANCE **/
            db = new DBResto(MenuModifier.this);

            /** CONSTRUCT THE QUERY **/
            String s = "SELECT * FROM " + db.MENU + " WHERE " + db.MENU_ID + " = " + INCOMING_MENU_ID;

            /** FETCH THE RESULT FROM THE DATABASE **/
            cursor = db.selectAllData(s);
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (cursor != null && cursor.getCount() != 0) {
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    /** GET THE MENU CATEGORY ID**/
                    if (cursor.getString(cursor.getColumnIndex(db.MENU_CATEGORY_ID)) != null)	{
                        MENU_CATEGORY_ID = cursor.getString(cursor.getColumnIndex(db.MENU_CATEGORY_ID));
                    } else {
                        MENU_CATEGORY_ID = null;
                    }
                    /** GET THE MENU NAME **/
                    if (cursor.getString(cursor.getColumnIndex(db.MENU_NAME)) != null)	{
                        MENU_NAME = cursor.getString(cursor.getColumnIndex(db.MENU_NAME));
                    } else {
                        MENU_NAME = null;
                    }

                    /** GET THE MENU DESCRIPTION **/
                    if (cursor.getString(cursor.getColumnIndex(db.MENU_DESCRIPTION)) != null)	{
                        MENU_DESCRIPTION = cursor.getString(cursor.getColumnIndex(db.MENU_DESCRIPTION));
                    } else {
                        MENU_DESCRIPTION = null;
                    }

                    /** GET THE MENU PRICE **/
                    if (cursor.getString(cursor.getColumnIndex(db.MENU_PRICE)) != null)	{
                        MENU_PRICE = cursor.getString(cursor.getColumnIndex(db.MENU_PRICE));
                    } else {
                        MENU_PRICE = null;
                    }

                    /** GET THE MENU TYPE **/
                    if (cursor.getString(cursor.getColumnIndex(db.MENU_TYPE)) != null)	{
                        MENU_TYPE = cursor.getString(cursor.getColumnIndex(db.MENU_TYPE));
                    } else {
                        MENU_TYPE = null;
                    }

                    /** GET THE MENU SERVES **/
                    if (cursor.getString(cursor.getColumnIndex(db.MENU_SERVES)) != null)	{
                        MENU_SERVES = cursor.getString(cursor.getColumnIndex(db.MENU_SERVES));
                    } else {
                        MENU_SERVES = null;
                    }

                    /** GET THE MENU IMAGE **/
                    if (cursor.getBlob(cursor.getColumnIndex(db.MENU_IMAGE)) != null)	{
                        MENU_IMAGE = cursor.getBlob(cursor.getColumnIndex(db.MENU_IMAGE));
                    } else {
                        MENU_IMAGE = null;
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
            if (MENU_CATEGORY_ID != null)   {
                int posCategory = getCategoryIndex(arrCategory, MENU_CATEGORY_ID);
                spnMenuCategory.setSelection(posCategory);
            }

            if (MENU_NAME != null)  {
                edtDishName.setText(MENU_NAME);
            }

            if (MENU_DESCRIPTION != null)   {
                edtDishDescription.setText(MENU_DESCRIPTION);
            }

            if (MENU_TYPE != null)  {
                if (MENU_TYPE.equalsIgnoreCase("Veg"))  {
                    rdbtnVeg.setChecked(true);
                    rdbtnNonVeg.setChecked(false);
                } else if (MENU_TYPE.equalsIgnoreCase("Non-veg"))   {
                    rdbtnNonVeg.setChecked(true);
                    rdbtnVeg.setChecked(false);
                }
            } else {
                rdbtnVeg.setChecked(true);
                rdbtnNonVeg.setChecked(false);
            }

            if (MENU_PRICE != null) {
                edtDishPrice.setText(MENU_PRICE);
            }

            if (MENU_SERVES != null)    {
                int posServes = getServesIndex(arrServes, MENU_SERVES);
                spnServes.setSelection(posServes);
            }

            if (MENU_IMAGE != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(MENU_IMAGE, 0, MENU_IMAGE.length);
                imgvwAddPhoto.setScaleType(AppCompatImageView.ScaleType.CENTER_CROP);
                imgvwAddPhoto.setImageBitmap(bitmap);
            }
        }
    }

    /***** GET THE CATEGORY POSITION *****/
    private int getCategoryIndex(ArrayList<MenuCategoryData> array, String catID) {
        int index = 0;
        for (int i =0; i < array.size(); i++) {
            if (array.get(i).getCatID().equalsIgnoreCase(catID))   {
                index = i;
                break;
            }
        }
        return index;
    }

    /***** GET THE SERVES POSITION *****/
    private int getServesIndex(List<String> array, String serves) {
        int index = 0;
        for (int i =0; i < array.size(); i++) {
            if (array.get(i).equalsIgnoreCase(serves))   {
                index = i;
                break;
            }
        }
        return index;
    }

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
            db = new DBResto(MenuModifier.this);

            /** CONSTRUCT THE QUERY TO FETCH ALL MENU CATEGORIES FROM THE DATABASE **/
            String strQueryData = "SELECT * FROM " + db.CATEGORY;

            /** CAST THE QUERY IN THE CURSOR TO FETCH THE RESULTS **/
            cursor = db.selectAllData(strQueryData);
//			Log.e("CURSOR", DatabaseUtils.dumpCursorToString(cursor));

            arrCategory.clear();
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
                    MenuModifier.this,
                    R.layout.custom_spinner_row,
                    arrCategory));

            /***** SELECT A MENU CATEGORY FOR THE DISH TO GO IN *****/
            spnMenuCategory.setOnItemSelectedListener(selectCategory);

            /** GET THE INCOMING DATA **/
            fetchIncomingData();
        }
    }

    /***** SELECT A MENU CATEGORY FOR THE DISH TO GO IN *****/
    private final AdapterView.OnItemSelectedListener selectCategory = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parentView, View v, int position, long id) {

			/** GET THE CURRENT SELECTED MENU CATEGORY **/
            SELECTED_CATEGORY_ID = arrCategory.get(position).getCatID();
            Log.e("SELECTED CATEGORY ID", SELECTED_CATEGORY_ID);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parentView) {

        }
    };

    /** GET THE INCOMING DATA **/
    private void fetchIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey("MENU_ID") && bundle.containsKey("MENU_NAME"))   {
            INCOMING_MENU_NAME = bundle.getString("MENU_NAME");
            INCOMING_MENU_ID = bundle.getString("MENU_ID");
            if (INCOMING_MENU_ID != null)   {
                new fetchDishDetails().execute();
            } else {
                //TODO: SHOW AN ERROR
            }
        } else {
            //TODO: SHOW AN ERROR
        }
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
                MENU_IMAGE = bos.toByteArray();

            } else if (imgSource == 2)  {
                imgvwAddPhoto.setImageBitmap(bitmap);
                imgvwAddPhoto.setScaleType(AppCompatImageView.ScaleType.CENTER_CROP);

                /** CONVERT THE BITMAP TO A BYTE ARRAY **/
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                MENU_IMAGE = bos.toByteArray();
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

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {

        Toolbar myToolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);

        String strTitle = getResources().getString(R.string.menu_modifier_title);
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(
                MenuModifier.this, "RobotoCondensed-Regular.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(s);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(MenuModifier.this);
        inflater.inflate(R.menu.menu_creator, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                /***** SET THE RESULT TO "RESULT_CANCELED" AND FINISH THE ACTIVITY *****/
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                /** FINISH THE ACTIVITY **/
                this.finish();
                break;
            case R.id.save:
                /** VALIDATE NECESSARY DATA BEFORE CREATING UPDATING THE DISH **/
                validateData();
                break;
            case R.id.clear:
                /***** SET THE RESULT TO "RESULT_CANCELED" AND FINISH THE ACTIVITY *****/
                Intent intent1 = new Intent();
                setResult(RESULT_CANCELED, intent1);

                /** FINISH THE ACTIVITY **/
                finish();
                break;
            default:
                break;
        }

        return false;
    }

    /** VALIDATE NECESSARY DATA BEFORE CREATING UPDATING THE DISH **/
    private void validateData() {

		/* HIDE THE KEYBOARD */
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtDishName.getWindowToken(), 0);

        /** GET THE REQUIRED DATA **/
        MENU_NAME = edtDishName.getText().toString().trim();
        MENU_DESCRIPTION = edtDishDescription.getText().toString().trim();
        MENU_PRICE = edtDishPrice.getText().toString().trim();

        if (MENU_NAME.length() == 0) {
            inputDishName.setError("Menu / Dish name cannot be empty!");
            edtDishName.requestFocus();
        } else if (MENU_DESCRIPTION.length() == 0) {
            inputDishDescription.setError("Please provide the Menu / Dish Description");
            edtDishDescription.requestFocus();
        } else if (MENU_PRICE.length() == 0) {
            inputDishPrice.setError("The Menu / Dish needs to have a Price");
            edtDishPrice.requestFocus();
        } else if (MENU_IMAGE == null)    {
            Toast.makeText(getApplicationContext(), "Please select an Image for your Dish", Toast.LENGTH_LONG).show();
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

        /** A PROGRESS DIALOG INSTANCE **/
        ProgressDialog pdUpdateMenu;

        /** A CURSOR INSTANCE **/
        Cursor cursor;

        /** A BOOLEAN TO TRACK EXISTING MENU NAME **/
        boolean blnMenuExists = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /** INSTANTIATE AND CONFIGURE THE PROGRESS DIALOG **/
            pdUpdateMenu = new ProgressDialog(MenuModifier.this);
            pdUpdateMenu.setMessage("Please wait while the new Dish / Menu is being updated....");
            pdUpdateMenu.setIndeterminate(false);
            pdUpdateMenu.setCancelable(true);
            pdUpdateMenu.show();

            /** INSTANTIATE THE DATABASE HELPER CLASS **/
            db = new DBResto(MenuModifier.this);

			/** CONSTRUCT A QUERY TO FETCH DISHES / MENUS ON RECORD **/
            String strQueryData = "SELECT * FROM " + db.MENU + " WHERE " + db.MENU_NAME + " = '" + MENU_NAME + "'";

			/** CAST THE QUERY IN THE CURSOR TO FETCH THE RESULTS **/
            cursor = db.selectAllData(strQueryData);
            Log.e("MENUS", DatabaseUtils.dumpCursorToString(cursor));
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
                        if (strMenuName.equalsIgnoreCase(INCOMING_MENU_NAME))   {
                            blnMenuExists = false;
                        } else if (strMenuName.equalsIgnoreCase(MENU_NAME))    {
                            blnMenuExists = true;
                        }
                    } else {
                        blnMenuExists = false;
                    }
                }
            } else {
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

                /***** UPDATE THE DISH IN THE DATABASE *****/
                db.updateMenu(
                        INCOMING_MENU_ID, SELECTED_CATEGORY_ID, MENU_NAME, MENU_DESCRIPTION,
                        MENU_PRICE, MENU_TYPE, MENU_SERVES, MENU_IMAGE);

				/* DISMISS THE DIALOG */
                pdUpdateMenu.dismiss();

                /***** SET THE RESULT TO "RESULT_OK" AND FINISH THE ACTIVITY *****/
                Intent updatedMenu = new Intent();
                setResult(RESULT_OK, updatedMenu);

                /** FINISH THE ACTIVITY **/
                finish();
            } else {
				/* SET THE ERROR MESSAGE */
                //TODO: ADD THE ERROR TO THE STRINGS.XML
                inputDishName.setError("A Dish with the same name exists. Choose a different name");
                inputDishName.requestFocus();

				/* DISMISS THE DIALOG */
                pdUpdateMenu.dismiss();
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}