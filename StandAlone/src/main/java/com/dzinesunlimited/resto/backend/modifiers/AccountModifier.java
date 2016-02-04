package com.dzinesunlimited.resto.backend.modifiers;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.LinearLayout;

import com.dzinesunlimited.resto.R;
import com.dzinesunlimited.resto.utils.db.DBResto;
import com.dzinesunlimited.resto.utils.helpers.pojos.backend.AccountsRolesData;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            /** HIDE THE PROGRESS AFTER FETCHING THE ACCOUNT DETAILS **/
            linlaHeaderProgress.setVisibility(View.GONE);
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

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}