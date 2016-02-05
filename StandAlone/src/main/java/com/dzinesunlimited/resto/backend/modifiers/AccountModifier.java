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
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.dzinesunlimited.resto.R;
import com.dzinesunlimited.resto.utils.TypefaceSpan;
import com.dzinesunlimited.resto.utils.db.DBResto;
import com.dzinesunlimited.resto.utils.helpers.adapters.backend.AccountsRolesAdapter;
import com.dzinesunlimited.resto.utils.helpers.pojos.backend.AccountsRolesData;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AccountModifier extends AppCompatActivity {

    /** INCOMING STAFF ID **/
    private String INCOMING_STAFF_ID = null;

    /** THE DATABASE INSTANCE **/
    DBResto db;

    /** DECLARE THE LAYOUT ELEMENTS **/
    @Bind(R.id.linlaHeaderProgress)LinearLayout linlaHeaderProgress;
    @Bind(R.id.spnStaffRole)AppCompatSpinner spnStaffRole;
    @Bind(R.id.inputFullName)TextInputLayout inputFullName;
    @Bind(R.id.edtFullName)AppCompatEditText edtFullName;
    @Bind(R.id.inputPhone)TextInputLayout inputPhone;
    @Bind(R.id.edtPhone)AppCompatEditText edtPhone;
    @Bind(R.id.inputUserName)TextInputLayout inputUserName;
    @Bind(R.id.edtUserName)AppCompatEditText edtUserName;
    @Bind(R.id.inputPassword)TextInputLayout inputPassword;
    @Bind(R.id.edtPassword)AppCompatEditText edtPassword;
    @Bind(R.id.inputConfirmPassword)TextInputLayout inputConfirmPassword;
    @Bind(R.id.edtConfirmPassword)AppCompatEditText edtConfirmPassword;
    @Bind(R.id.imgvwProfile)AppCompatImageView imgvwProfile;
    @OnClick(R.id.imgvwProfile) protected void GetImage() {
        EasyImage.openChooser(AccountModifier.this, "Pick Image Source", true);
    }

    /** ARRAYLIST FOR THE SPINNER (USER ROLES) **/
    ArrayList<AccountsRolesData> arrRoles = new ArrayList<>();

    /** VARIABLES TO HOLD THE COLLECTED DATA **/
    private String SELECTED_ROLE_ID = null;
    private String FULL_NAME = null;
    private String PHONE = null;
    private String USER_NAME = null;
    private String PASSWORD = null;
    private String CONFIRM_PASSWORD = null;
    private byte[] PROFILE_PICTURE;

    /** IMAGE SOURCE **/
    int imgSource = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.be_modifier_account);
        ButterKnife.bind(this);

        /** FETCH THE INCOMING DATA **/
        fetchIncomingData();

        /***** CONFIGURE THE ACTIONBAR *****/
        configAB();

        /** FETCH THE LIST OF ALL ACCOUNT CATEGORIES **/
        new fetchAllUserRoles().execute();

        /** CHANGE THE USER ROLES AND SHOW IT'S USERS **/
        spnStaffRole.setOnItemSelectedListener(selectRole);
    }

    /** FETCH ACCOUNT DETAILS **/
    private class fetchAccountDetails extends AsyncTask<Void, Void, Void> {

        /** A CURSOR INSTANCE **/
        Cursor cursor;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /** SHOW THE PROGRESS WHILE FETCHING THE ACCOUNT DETAILS **/
            linlaHeaderProgress.setVisibility(View.VISIBLE);

            /** INITIALISE THE DATABASE INSTANCE **/
            db = new DBResto(AccountModifier.this);

            /** CONSTRUCT THE QUERY **/
            String s = "SELECT * FROM " + db.STAFF + " WHERE " + db.STAFF_ID + " = " + INCOMING_STAFF_ID;

            /** FETCH THE RESULT FROM THE DATABASE **/
            cursor = db.selectAllData(s);
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (cursor != null && cursor.getCount() != 0) {
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    /** GET THE FULL NAME **/
                    if (cursor.getString(cursor.getColumnIndex(db.STAFF_FULL_NAME)) != null)	{
                        FULL_NAME = cursor.getString(cursor.getColumnIndex(db.STAFF_FULL_NAME));
                    } else {
                        FULL_NAME = null;
                    }

                    /** GET THE PHONE **/
                    if (cursor.getString(cursor.getColumnIndex(db.STAFF_PHONE)) != null)	{
                        PHONE = cursor.getString(cursor.getColumnIndex(db.STAFF_PHONE));
                    } else {
                        PHONE = null;
                    }

                    /** GET THE USER NAME **/
                    if (cursor.getString(cursor.getColumnIndex(db.STAFF_USER_NAME)) != null)	{
                        USER_NAME = cursor.getString(cursor.getColumnIndex(db.STAFF_USER_NAME));
                    } else {
                        USER_NAME = null;
                    }

                    /** GET THE PASSWORD **/
                    if (cursor.getString(cursor.getColumnIndex(db.STAFF_PASSWORD)) != null)	{
                        PASSWORD = cursor.getString(cursor.getColumnIndex(db.STAFF_PASSWORD));
                    } else {
                        PASSWORD = null;
                    }

                    /** GET THE PROFILE PICTURE **/
                    if (cursor.getBlob(cursor.getColumnIndex(db.STAFF_PROFILE_PICTURE)) != null)	{
                        PROFILE_PICTURE = cursor.getBlob(cursor.getColumnIndex(db.STAFF_PROFILE_PICTURE));
                    } else {
                        PROFILE_PICTURE = null;
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
            if (FULL_NAME != null)  {
                edtFullName.setText(FULL_NAME);
            }

            if (PHONE != null)  {
                edtPhone.setText(PHONE);
            }

            if (USER_NAME != null)  {
                edtUserName.setText(USER_NAME);
            }

            if (PASSWORD != null)   {
                edtPassword.setText(PASSWORD);
            }

            if (PROFILE_PICTURE != null)    {
                Bitmap bitmap = BitmapFactory.decodeByteArray(PROFILE_PICTURE, 0, PROFILE_PICTURE.length);
                imgvwProfile.setScaleType(AppCompatImageView.ScaleType.CENTER_CROP);
                imgvwProfile.setImageBitmap(bitmap);
            }

            /** HIDE THE PROGRESS AFTER FETCHING THE ACCOUNT DETAILS **/
            linlaHeaderProgress.setVisibility(View.GONE);
        }
    }

    /** FETCH THE LIST OF ALL ACCOUNT CATEGORIES **/
    private class fetchAllUserRoles extends AsyncTask<Void, Void, Void> {

        /** A CURSOR INSTANCE **/
        Cursor cursor;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /** INSTANTIATE THE DATABASE HELPER CLASS **/
            db = new DBResto(AccountModifier.this);

            /** CONSTRUCT THE QUERY TO FETCH ALL TWEETS FROM THE DATABASE **/
            String strQueryData = "SELECT * FROM " + db.STAFF_ROLES;

            /** CAST THE QUERY IN THE CURSOR TO FETCH THE RESULTS **/
            cursor = db.selectAllData(strQueryData);
        }

        @Override
        protected Void doInBackground(Void... params) {

            /** CHECK THAT THE DATABASE QUERY RETURNED SOME RESULTS **/
            if (cursor != null && cursor.getCount() != 0)	{

				/* AN INSTANCE OF THE AccountsUserRolesData POJO CLASS */
                AccountsRolesData accRoles;

                /** LOOP THROUGH THE RESULT SET AND PARSE NECESSARY INFORMATION **/
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                    /***** INSTANTIATE THE AccountsUserRolesData INSTANCE "accRoles" *****/
                    accRoles = new AccountsRolesData();

                    /** GET THE ROLE_ID **/
                    if (cursor.getString(cursor.getColumnIndex(db.ROLE_ID)) != null)	{
                        String ROLE_ID = cursor.getString(cursor.getColumnIndex(db.ROLE_ID));
                        accRoles.setRoleID(ROLE_ID);
                    } else {
                        accRoles.setRoleID(null);
                    }

                    /** GET THE ROLE_TEXT **/
                    if (cursor.getString(cursor.getColumnIndex(db.ROLE_TEXT)) != null)	{
                        String ROLE_TEXT = cursor.getString(cursor.getColumnIndex(db.ROLE_TEXT));
                        accRoles.setRoleText(ROLE_TEXT);
                    } else {
                        accRoles.setRoleText(null);
                    }

                    /** ADD THE COLLECTED DATA TO THE ARRAYLIST **/
                    arrRoles.add(accRoles);
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

            /** SET THE ADAPTER TO THE USER ROLES SPINNER **/
            spnStaffRole.setAdapter(new AccountsRolesAdapter(
                    AccountModifier.this,
                    R.layout.custom_spinner_row,
                    arrRoles));
        }
    }

    /** FETCH THE INCOMING DATA **/
    private void fetchIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey("STAFF_ID")) {
            INCOMING_STAFF_ID = bundle.getString("STAFF_ID");
            if (INCOMING_STAFF_ID != null)  {
                new fetchAccountDetails().execute();
            } else {
                //TODO: SHOW AN ERROR
            }
        } else {
            //TODO: SHOW AN ERROR
        }
    }

    /***** SELECT THE USER ROLE FOR THE NEW ACCOUNT *****/
    private final AdapterView.OnItemSelectedListener selectRole = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parentView, View v, int position, long id) {
            SELECTED_ROLE_ID = arrRoles.get(position).getRoleID();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parentView) {

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source) {
                super.onImagePicked(imageFile, source);
                onPhotoReturned(imageFile);

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

            if (imgSource == 1) {
//                Log.e("HEIGHT / WIDTH", String.valueOf(bitmap.getHeight()) + " " + String.valueOf(bitmap.getWidth()));
                imgvwProfile.setImageBitmap(bitmap);
                imgvwProfile.setScaleType(AppCompatImageView.ScaleType.CENTER_CROP);

                /** CONVERT THE BITMAP TO A BYTE ARRAY **/
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                PROFILE_PICTURE = bos.toByteArray();

            } else if (imgSource == 2)  {
//                Log.e("HEIGHT / WIDTH", String.valueOf(bitmap.getHeight()) + " " + String.valueOf(bitmap.getWidth()));
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

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {

        Toolbar myToolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);

        String strTitle = getResources().getString(R.string.account_modifier_title);
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(
                AccountModifier.this, "RobotoCondensed-Regular.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(s);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(AccountModifier.this);
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
        PHONE = edtPhone.getText().toString();
        USER_NAME = edtUserName.getText().toString();
        PASSWORD = edtPassword.getText().toString();
        CONFIRM_PASSWORD = edtConfirmPassword.getText().toString();

        /** VALIDATE REQUIRED INFO OR SHOW APPROPRIATE ERRORS **/
        if (FULL_NAME.length() == 0)   {
            inputFullName.setError("Please provide the Full Name");
            edtFullName.requestFocus();
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
            /** UPDATE THE STAFF ACCOUNT **/

            /** INSTANTIATE AND CONFIGURE THE PROGRESSDIALOG **/
            ProgressDialog dialog = new ProgressDialog(AccountModifier.this);
            dialog.setMessage("Please wait while the new Account is being updated.");
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.show();

            db = new DBResto(AccountModifier.this);
            db.updateStaff(INCOMING_STAFF_ID, SELECTED_ROLE_ID, FULL_NAME, PHONE, USER_NAME, PASSWORD, PROFILE_PICTURE);
            db.close();
            dialog.dismiss();

            /***** SET THE RESULT TO "RESULT_OK" AND FINISH THE ACTIVITY *****/
            Intent updatedAccount = new Intent();
            setResult(RESULT_OK, updatedAccount);

            /** FINISH THE ACTIVITY **/
            finish();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}