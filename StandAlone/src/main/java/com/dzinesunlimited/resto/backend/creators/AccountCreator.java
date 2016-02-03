package com.dzinesunlimited.resto.backend.creators;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RadioGroup;

import com.dzinesunlimited.resto.R;
import com.dzinesunlimited.resto.utils.TypefaceSpan;
import com.dzinesunlimited.resto.utils.db.DBResto;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AccountCreator extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    /** THE DATABASE INSTANCE **/
    DBResto db;

    /** DECLARE THE LAYOUT ELEMENTS **/
    @Bind(R.id.spnStaffRole)AppCompatSpinner spnStaffRole;
    @Bind(R.id.inputFullName)TextInputLayout inputFullName;
    @Bind(R.id.inputAddress)TextInputLayout inputAddress;
    @Bind(R.id.inputCity)TextInputLayout inputCity;
    @Bind(R.id.inputState)TextInputLayout inputState;
    @Bind(R.id.inputCountry)TextInputLayout inputCountry;
    @Bind(R.id.inputZIP)TextInputLayout inputZIP;
    @Bind(R.id.inputPhone)TextInputLayout inputPhone;
    @Bind(R.id.inputUserName)TextInputLayout inputUserName;
    @Bind(R.id.inputPassword)TextInputLayout inputPassword;
    @Bind(R.id.inputConfirmPassword)TextInputLayout inputConfirmPassword;
    @Bind(R.id.edtFullName)AppCompatEditText edtFullName;
    @Bind(R.id.edtAddress)AppCompatEditText edtAddress;
    @Bind(R.id.edtCity)AppCompatEditText edtCity;
    @Bind(R.id.edtState)AppCompatEditText edtState;
    @Bind(R.id.edtCountry)AppCompatEditText edtCountry;
    @Bind(R.id.edtZIP)AppCompatEditText edtZIP;
    @Bind(R.id.edtPhone)AppCompatEditText edtPhone;
    @Bind(R.id.edtDOB)AppCompatEditText edtDOB;
    @OnClick(R.id.imgbtnDOB) protected void PickDOB()   {
        Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(AccountCreator.this,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show(getFragmentManager(), "DatePickerDialog");
    }
    @Bind(R.id.rdgGender)RadioGroup rdgGender;
    @Bind(R.id.edtUserName)AppCompatEditText edtUserName;
    @Bind(R.id.edtPassword)AppCompatEditText edtPassword;
    @Bind(R.id.edtConfirmPassword)AppCompatEditText edtConfirmPassword;
    @Bind(R.id.imgvwProfile)AppCompatImageView imgvwProfile;
    @OnClick(R.id.imgvwProfile) protected void GetImage() {
        EasyImage.openChooser(AccountCreator.this, "Pick Image Source", true);
    }

    /** ARRAYLIST FOR THE SPINNER (USER ROLES) **/
//    ArrayList<AccountsRolesData> arrRoles = new ArrayList<>();

    /** VARIABLES TO HOLD THE COLLECTED DATA **/
    private String SELECTED_ROLE_ID = null;
    private String FULL_NAME = null;
    private String ADDRESS = null;
    private String CITY = null;
    private String STATE = null;
    private String COUNTRY = null;
    private String ZIP = null;
    private String PHONE = null;
    private String GENDER = "Male";
    private String DOB = null;
    private String USER_NAME = null;
    private String PASSWORD = null;
    private String CONFIRM_PASSWORD = null;
    private byte[] PROFILE_PICTURE;

    /** IMAGE SOURCE **/
    int imgSource = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.be_creator_account);
        ButterKnife.bind(this);

        /***** CONFIGURE THE ACTIONBAR *****/
        configAB();

        /** FETCH THE LIST OF ALL ACCOUNT CATEGORIES **/
//        new fetchAllUserRoles().execute();

        /** CHANGE THE USER ROLES AND SHOW IT'S USERS **/
        spnStaffRole.setOnItemSelectedListener(selectRole);

        /** SELECT THE USER'S GENDER **/
        rdgGender.setOnCheckedChangeListener(selectGender);
    }

    /** FETCH THE LIST OF ALL ACCOUNT CATEGORIES **/
//    private class fetchAllUserRoles extends AsyncTask<Void, Void, Void> {
//
//        /** A CURSOR INSTANCE **/
//        Cursor cursor;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//            /** INSTANTIATE THE DATABASE HELPER CLASS **/
//            db = new DBResto(AccountCreator.this);
//
//            /** CONSTRUCT THE QUERY TO FETCH ALL TWEETS FROM THE DATABASE **/
//            String strQueryData = "SELECT * FROM " + db.STAFF_ROLES;
//            Log.e("CREATOR ROLES QUERY", strQueryData);
//
//            /** CAST THE QUERY IN THE CURSOR TO FETCH THE RESULTS **/
//            cursor = db.selectAllData(strQueryData);
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//
//            /** CHECK THAT THE DATABASE QUERY RETURNED SOME RESULTS **/
//            if (cursor != null && cursor.getCount() != 0)	{
//
//				/* AN INSTANCE OF THE AccountsUserRolesData POJO CLASS */
//                AccountsRolesData accRoles;
//
//                /** LOOP THROUGH THE RESULT SET AND PARSE NECESSARY INFORMATION **/
//                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
//
//                    /***** INSTANTIATE THE AccountsUserRolesData INSTANCE "accRoles" *****/
//                    accRoles = new AccountsRolesData();
//
//                    /** GET THE STAFF_ROLE_ID **/
//                    if (cursor.getString(cursor.getColumnIndex(db.ROLE_ID)) != null)	{
//                        String ROLE_ID = cursor.getString(cursor.getColumnIndex(db.ROLE_ID));
//                        accRoles.setStffRoleID(ROLE_ID);
//                    } else {
//                        accRoles.setStffRoleID(null);
//                    }
//
//                    /** GET THE STAFF_ROLE_CODE **/
//                    if (cursor.getString(cursor.getColumnIndex(db.ROLE_CODE)) != null)	{
//                        String ROLE_CODE = cursor.getString(cursor.getColumnIndex(db.ROLE_CODE));
//                        accRoles.setStffRoleCode(ROLE_CODE);
//                    } else {
//                        accRoles.setStffRoleCode(null);
//                    }
//
//                    /** GET THE STAFF_ROLE_TEXT **/
//                    if (cursor.getString(cursor.getColumnIndex(db.ROLE_TEXT)) != null)	{
//                        String ROLE_TEXT = cursor.getString(cursor.getColumnIndex(db.ROLE_TEXT));
//                        accRoles.setStffRoleText(ROLE_TEXT);
//                    } else {
//                        accRoles.setStffRoleText(null);
//                    }
//
//                    /** GET THE STAFF_ROLE_DESCRIPTION **/
//                    if (cursor.getString(cursor.getColumnIndex(db.ROLE_DESCRIPTION)) != null)	{
//                        String ROLE_DESCRIPTION = cursor.getString(cursor.getColumnIndex(db.ROLE_DESCRIPTION));
//                        accRoles.setStffRoleDescription(ROLE_DESCRIPTION);
//                    } else {
//                        accRoles.setStffRoleDescription(null);
//                    }
//
//                    /** ADD THE COLLECTED DATA TO THE ARRAYLIST **/
//                    arrRoles.add(accRoles);
//                }
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//
//            /** CLOSE THE CURSOR **/
//            if (cursor != null && !cursor.isClosed())	{
//                cursor.close();
//            }
//
//            /** CLOSE THE DATABASE **/
//            db.close();
//
//            /** SET THE ADAPTER TO THE USER ROLES SPINNER **/
//            spnStaffRole.setAdapter(new AccountsRolesAdapter(
//                    AccountCreator.this,
//                    R.layout.custom_spinner_row,
//                    arrRoles));
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source) {
                super.onImagePicked(imageFile, source);
                onPhotoReturned(imageFile);
                Log.e("DATA", String.valueOf(data));

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

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

//            Bitmap BMP_PROFILE_IMAGE;

            if (imgSource == 1) {
                Log.e("HEIGHT / WIDTH", String.valueOf(bitmap.getHeight()) + " " + String.valueOf(bitmap.getWidth()));

                imgvwProfile.setImageBitmap(bitmap);
                imgvwProfile.setScaleType(AppCompatImageView.ScaleType.CENTER_CROP);

                /** CONVERT THE BITMAP TO A BYTE ARRAY **/
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                PROFILE_PICTURE = bos.toByteArray();

            } else if (imgSource == 2)  {
                Log.e("HEIGHT / WIDTH", String.valueOf(bitmap.getHeight()) + " " + String.valueOf(bitmap.getWidth()));

                imgvwProfile.setImageBitmap(bitmap);
                imgvwProfile.setScaleType(AppCompatImageView.ScaleType.CENTER_CROP);

                /** CONVERT THE BITMAP TO A BYTE ARRAY **/
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                PROFILE_PICTURE = bos.toByteArray();
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    private void onPhotoReturned(File photoFile) {
        Picasso.with(this)
                .load(photoFile)
                .centerCrop()
                .resize(200, 200)
                .into(target);
    }

    /***** SELECT THE USER ROLE FOR THE NEW ACCOUNT *****/
    private final AdapterView.OnItemSelectedListener selectRole = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parentView, View v, int position, long id) {
//            SELECTED_ROLE_ID = arrRoles.get(position).getStffRoleID();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parentView) {

        }
    };

    /***** SELECT THE USER'S GENDER *****/
    private final RadioGroup.OnCheckedChangeListener selectGender = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            switch (checkedId) {
                case R.id.rdbtnMale:

                    /** SET THE GENDER TO MALE **/
                    GENDER = "Male";

                    break;

                case R.id.rdbtnFemale:

                    /** SET THE GENDER TO FEMALE **/
                    GENDER = "Female";

                    break;

                default:
                    break;
            }
        }
    };

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {

        Toolbar myToolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);

        String strTitle = getResources().getString(R.string.account_creator_title);
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(
                AccountCreator.this, "RobotoCondensed-Regular.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(s);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(AccountCreator.this);
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

                /** FINISH THE ACTIVITY **/
                this.finish();
                break;
            case R.id.save:
                /** VALIDATE NECESSARY DATA BEFORE CREATING THE NEW ACCOUNT **/
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

    /** VALIDATE NECESSARY DATA BEFORE CREATING THE NEW ACCOUNT **/
    private void validateData() {

        /** GET THE DATA **/
        FULL_NAME = edtFullName.getText().toString();
        ADDRESS = edtAddress.getText().toString();
        CITY = edtCity.getText().toString();
        STATE = edtState.getText().toString();
        COUNTRY = edtCountry.getText().toString();
        ZIP = edtZIP.getText().toString();
        PHONE = edtPhone.getText().toString();
        DOB = edtDOB.getText().toString();
        USER_NAME = edtUserName.getText().toString();
        PASSWORD = edtPassword.getText().toString();
        CONFIRM_PASSWORD = edtConfirmPassword.getText().toString();

        /** VALIDATE REQUIRED INFO OR SHOW APPROPRIATE ERRORS **/
        if (FULL_NAME.length() == 0)   {
            inputFullName.setError("Please provide the Full Name");
            edtFullName.requestFocus();
        } else if (ADDRESS.length() == 0) {
            inputAddress.setError("Please provide the User's Address");
            edtAddress.requestFocus();
        } else if (CITY.length() == 0)  {
            inputCity.setError("Please provide the User's City");
            edtCity.requestFocus();
        } else if (STATE.length() == 0) {
            inputState.setError("Please provide the User's State");
            edtState.requestFocus();
        } else if (COUNTRY.length() == 0)   {
            inputCountry.setError("Please provide the User's Country");
            edtCountry.requestFocus();
        } else if (ZIP.length() == 0)    {
            inputZIP.setError("Please provide a valid ZIP / Pincode");
            edtZIP.requestFocus();
        } else if (PHONE.length() == 0) {
          inputPhone.setError("Please provide the User's Phone Number");
            edtPhone.requestFocus();
        } else if (USER_NAME.length() == 0) {
            inputUserName.setError("Please choose a User Name");
            edtUserName.requestFocus();
        } else if (PASSWORD.length() == 0)  {
            inputPassword.setError("Please enter a Password");
            edtPassword.requestFocus();
        } else if (CONFIRM_PASSWORD.length() == 0)  {
            inputConfirmPassword.setError("Please confirm the Password");
            edtConfirmPassword.requestFocus();
        } else if (!CONFIRM_PASSWORD.equals(PASSWORD))  {
            inputConfirmPassword.setError("The Passwords do not match");
            edtConfirmPassword.requestFocus();
        } else {
            /** CHECK IF THE USERNAME ALREADY EXISTS **/
//            new CheckExistingUser().execute();
        }
    }

//    private class CheckExistingUser extends AsyncTask<Void, Void, Void> {
//
//        /** A PROGRESS DIALOG INSTANCE **/
//        ProgressDialog dialog;
//
//        /** A CURSOR INSTANCE **/
//        Cursor cursor;
//
//        /** A BOOLEAN INSTANCE **/
//        Boolean blnUserExists = false;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//            /** INSTANTIATE AND CONFIGURE THE PROGRESSDIALOG **/
//            dialog = new ProgressDialog (AccountCreator.this);
//            dialog.setMessage("Please wait while the new User Account is being created.");
//            dialog.setIndeterminate(false);
//            dialog.setCancelable(false);
//            dialog.show();
//
//            /** INSTANTIATE THE DATABASE HELPER CLASS **/
//            db = new DBResto(AccountCreator.this);
//
//			/* GET THE TYPED USERNAME */
//            String strUserName = edtUserName.getText().toString();
//
//            /** CONSTRUCT A QUERY TO FETCH ACCOUNTS ON RECORD **/
//            String strQueryData = "SELECT * FROM " + db.STAFF + " WHERE " + db.STAFF_USER_NAME + " = '" + strUserName + "'";
//
//            /** CAST THE QUERY IN THE CURSOR TO FETCH THE RESULTS **/
//            cursor = db.selectAllData(strQueryData);
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//
//            /** CHECK THAT THE DATABASE QUERY RETURNED SOME RESULTS **/
//            if (cursor != null && cursor.getCount() != 0)	{
//                /** LOOP THROUGH THE RESULT SET AND PARSE NECESSARY INFORMATION **/
//                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
//                    /** GET THE USERNAME **/
//                    if (cursor.getString(cursor.getColumnIndex(db.STAFF_USER_NAME)) != null)	{
//                        String strUserName = cursor.getString(cursor.getColumnIndex(db.STAFF_USER_NAME));
//                        if (strUserName.equals(USER_NAME))	{
//							/* TOGGLE THE BOOLEAN TO INDICATE THE USERNAME EXISTS */
//                            blnUserExists = true;
//                        } else {
//							/* TOGGLE THE BOOLEAN TO INDICATE THE USERNAME DOES NOT EXIST */
//                            blnUserExists = false;
//                        }
//                    } else {
//						/* TOGGLE THE BOOLEAN TO INDICATE THE USERNAME DOES NOT EXIST */
//                        blnUserExists = false;
//                    }
//                }
//            } else {
//                blnUserExists = false;
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//
//            /***** CHECK USERNAME STATUS (EXISTS | DOES NOT EXIST) *****/
//            if (!blnUserExists)	{
//
//                /** ADD THE NEW ACCOUNT TO THE DATABASE **/
//                db.addStaff(
//                        SELECTED_ROLE_ID, FULL_NAME, ADDRESS, CITY, STATE, COUNTRY, ZIP,
//                        PHONE, GENDER, DOB, USER_NAME, PASSWORD, PROFILE_PICTURE);
//
//                /** CLOSE THE CURSOR **/
//                if (cursor != null && !cursor.isClosed())	{
//                    cursor.close();
//                }
//
//                /** CLOSE THE DATABASE **/
//                db.close();
//
//                /** DISMISS THE DIALOG **/
//                dialog.dismiss();
//
//                /***** SET THE RESULT TO "RESULT_OK" AND FINISH THE ACTIVITY *****/
//                Intent createdAccount = new Intent();
//                setResult(RESULT_OK, createdAccount);
//
//                /** FINISH THE ACTIVITY **/
//                finish();
//
//            } else {
//
//                /** DISMISS THE DIALOG **/
//                dialog.dismiss();
//
//                /** SET THE ERROR MESSAGE **/
//                edtUserName.setError(getResources().getString(R.string.account_empty_duplicate_username));
//            }
//        }
//    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int date) {
        String strMonth = null;
        switch (month)  {
            case 0:
                strMonth = "January";
                break;
            case 1:
                strMonth = "February";
                break;
            case 2:
                strMonth = "March";
                break;
            case 3:
                strMonth = "April";
                break;
            case 4:
                strMonth = "May";
                break;
            case 5:
                strMonth = "June";
                break;
            case 6:
                strMonth = "July";
                break;
            case 7:
                strMonth = "August";
                break;
            case 8:
                strMonth = "September";
                break;
            case 9:
                strMonth = "October";
                break;
            case 10:
                strMonth = "November";
                break;
            case 11:
                strMonth = "December";
                break;
        }
        String strDate = date + " " + strMonth + " " + year;
        edtDOB.setText(strDate);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}