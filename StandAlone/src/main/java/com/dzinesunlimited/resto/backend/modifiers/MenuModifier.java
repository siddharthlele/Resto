package com.dzinesunlimited.resto.backend.modifiers;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RadioGroup;

import com.dzinesunlimited.resto.R;
import com.dzinesunlimited.resto.utils.TypefaceSpan;
import com.dzinesunlimited.resto.utils.db.DBResto;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
    private String MENU_NAME;
    private String MENU_DESCRIPTION;
    private String MENU_PRICE;
    private String MENU_TYPE = "Veg";
    private String MENU_SERVES;
    private byte[] MENU_IMAGE;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.be_modifier_menu);
        ButterKnife.bind(this);

        /***** CONFIGURE THE ACTIONBAR *****/
        configAB();

        /** GET THE INCOMING DATA **/
        fetchIncomingData();

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

            if (MENU_IMAGE != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(MENU_IMAGE, 0, MENU_IMAGE.length);
                imgvwAddPhoto.setScaleType(AppCompatImageView.ScaleType.CENTER_CROP);
                imgvwAddPhoto.setImageBitmap(bitmap);
            }
        }
    }

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
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}