package com.dzinesunlimited.resto;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dzinesunlimited.resto.backend.admin.AdminLanding;
import com.dzinesunlimited.resto.backend.creators.AccountCreator;
import com.dzinesunlimited.resto.frontend.tables.TableSelector;
import com.dzinesunlimited.resto.recovery.PasswordRecovery;
import com.dzinesunlimited.resto.utils.AppPrefs;
import com.dzinesunlimited.resto.utils.TypefaceSpan;
import com.dzinesunlimited.resto.utils.db.DBResto;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

//public class LoginActivity extends AppCompatActivity {
//
//    /***** DECLARE THE LAYOUT ELEMENTS *****/
//    private AppCompatEditText edtUserName;
//    private AppCompatEditText edtPassword;
//    private AppCompatTextView txtCreateAccount;
//    private AppCompatTextView txtForgotPassword;
//    private AppCompatButton btnSignIn;
//
//    /** STRING TO HOLD THE USERNAME AND PASSWORD ENTERED BY THE USER **/
//    private String strTypedUserName;
//    private String strTypedPassword;
//
//    /***** THE DATABASE OBJECTS AND INSTANCES *****/
//    // THE DATABASE HELPER INSTANCE
//    private DBResto db;
//
//    private AppPrefs getApp()	{
//        return (AppPrefs) getApplication();
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.login);
//
//        /***** CAST THE LAYOUT ELEMENTS *****/
//        castLayoutElements();
//
//        /***** CONFIGURE THE ACTIONBAR *****/
//        configAB();
//    }
//
//    /***** CAST THE LAYOUT ELEMENTS *****/
//    private void castLayoutElements() {
//
//        edtUserName = (AppCompatEditText) findViewById(R.id.edtUserName);
//        edtPassword = (AppCompatEditText) findViewById(R.id.edtPassword);
//        txtCreateAccount = (AppCompatTextView) findViewById(R.id.txtCreateAccount);
//        txtForgotPassword = (AppCompatTextView) findViewById(R.id.txtForgotPassword);
//        btnSignIn = (AppCompatButton) findViewById(R.id.btnSignIn);
//
//        /** FOR TESTING ONLY!!!! **/
//        edtUserName.setText("superadmin");
//        edtPassword.setText("admin1234");
//
//        /** UNDERLINE THE TEXTVIEWS **/
//		/* GET THE STRINGS TO UNDERLINE */
//        String strCreateAccount = "CREATE ACCOUNT";
//        String strForgotPassword = "TROUBLE SIGNING IN?";
//
//		/* CREATE THE UNDERLINED TEXT */
//        SpannableString spanCreateAccount = new SpannableString(strCreateAccount);
//        spanCreateAccount.setSpan(new UnderlineSpan(), 0, strCreateAccount.length(), 0);
//        SpannableString spanForgotPassword = new SpannableString(strForgotPassword);
//        spanForgotPassword.setSpan(new UnderlineSpan(), 0, strForgotPassword.length(), 0);
//
//		/* SET THE "UNDERLINED" TEXT */
//        txtCreateAccount.setText(spanCreateAccount);
//        txtForgotPassword.setText(spanForgotPassword);
//
//        /***** CREATE A NEW ACCOUNT *****/
//        txtCreateAccount.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Intent createAccount = new Intent(LoginActivity.this, AccountCreator.class);
//                startActivity(createAccount);
//            }
//        });
//
//        /***** FORGOT PASSWORD *****/
//        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Intent forgotPassword = new Intent(LoginActivity.this, PasswordRecovery.class);
//                startActivity(forgotPassword);
//            }
//        });
//
//        /***** PERFORM LOGIN *****/
//        btnSignIn.setOnClickListener(doLogin);
//    }
//
//    /***** ONCLICK TO INITIATE THE LOGIN FUNCTION *****/
//    private final View.OnClickListener doLogin = new View.OnClickListener() {
//
//        @Override
//        public void onClick(View v) {
//
////            Intent scanIntent = new Intent(LoginActivity.this, CardIOActivity.class);
////
////            // customize these values to suit your needs.
////            scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
////            scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false); // default: false
////            scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false
////
////            // hides the manual entry button
////            // if set, developers should provide their own manual entry mechanism in the app
////            scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, false); // default: false
////
////            // matches the theme of your application
////            scanIntent.putExtra(CardIOActivity.EXTRA_KEEP_APPLICATION_THEME, false); // default: false
////
////            // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
////            startActivityForResult(scanIntent, 101);
//
//            /** GET THE TYPED USERNAME **/
//            strTypedUserName = edtUserName.getText().toString();
//
//            /** GET THE TYPED PASSWORD **/
//            strTypedPassword = edtPassword.getText().toString();
//
//            if (!strTypedUserName.equals("") && !strTypedPassword.equals(""))	{
//
//                /** DO THE LOGIN PROCESS **/
//                new doLoginTask().execute();
//            } else {
//
//                MaterialDialog dialog = new MaterialDialog.Builder(LoginActivity.this)
//                        .title("AUTHENTICATION FAILED")
//                        .content(R.string.empty_creds)
//                        .positiveText("OKAY")
//                        .icon(ContextCompat.getDrawable(LoginActivity.this, R.drawable.ic_info_outline_white_24dp))
//                        .typeface("HelveticaNeueLTW1G-MdCn.otf", "HelveticaNeueLTW1G-Cn.otf")
//                        .show();
//            }
//        }
//    };
//
//    /***** THE TASK TO PERFORM THE LOGIN *****/
//    private class doLoginTask extends AsyncTask<Void, Void, Void> {
//
//        String strUserID;
//        String strUserName;
//        String strPassword;
//        String strUserRole;
//
//        /** A CURSOR INSTANCE **/
//        Cursor cursor;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//            /** INSTANTIATE THE DATABASE HELPER CLASS **/
//            db = new DBResto(LoginActivity.this);
//
//            // CONSTRUCT THE QUERY TO FETCH ALL TWEETS FROM THE DATABASE
//            String strQueryData = "SELECT * FROM " + db.STAFF;
//
//            // CAST THE QUERY IN THE CURSOR TO FETCH THE RESULTS
//            cursor = db.selectAllData(strQueryData);
//        }
//
//        @Override
//        protected Void doInBackground(Void... arg0) {
//
//            if (cursor != null && cursor.getCount() != 0) {
//
//                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
//
//					/* GET THE USER ID */
//                    if (cursor.getString(cursor.getColumnIndex(db.STAFF_ID)) != null)	{
//                        strUserID = cursor.getString(cursor.getColumnIndex(db.STAFF_ID));
//                    } else {
//                        strUserID = null;
//                    }
//
//					/* GET THE USERNAME */
//                    if (cursor.getString(cursor.getColumnIndex(db.STAFF_USER_NAME)) != null)	{
//                        strUserName = cursor.getString(cursor.getColumnIndex(db.STAFF_USER_NAME));
//                    } else {
//                        strUserName = null;
//                    }
//
//					/* GET THE PASSWORD IF THE TYPED USERNAME MATCHES THE ONES ON RECORD */
//                    if (strUserName.equals(strTypedUserName))	{
//                        if (cursor.getString(cursor.getColumnIndex(db.STAFF_USER_PASSWORD)) != null)	{
//                            strPassword = cursor.getString(cursor.getColumnIndex(db.STAFF_USER_PASSWORD));
//
//							/* GET THE USER ROLE */
//                            if (cursor.getString(cursor.getColumnIndex(db.STAFF_ROLE_ID)) != null)	{
//                                strUserRole = cursor.getString(cursor.getColumnIndex(db.STAFF_ROLE_ID));
//                            } else {
//                                strUserRole = null;
//                            }
//
//                            /** GET THE PASSWORD AND BREAK THE FOR LOOP **/
//                            break;
//                        } else {
//                            strPassword = null;
//                        }
//                    } else {
//                        strUserName = "UserMismatch";
//                    }
//                }
//
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            super.onPostExecute(result);
//
//            /** CLOSE THE CURSOR **/
//            if (cursor != null && !cursor.isClosed())	{
//                cursor.close();
//            }
//
//            /** CLOSE THE DATABASE **/
//            db.close();
//
//            if (strUserName.equals(strTypedUserName) && strPassword.equals(strTypedPassword)) {
//
//                if (strUserRole.equals("1") || strUserRole.equals("2")) {
//                    Intent startMainPage = new Intent(LoginActivity.this, AdminLanding.class);
//                    startMainPage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(startMainPage);
//
//                    /** SET LOGIN ID **/
//                    getApp().setLogin(strUserID, strUserName);
//
//						/* FINISH THE CURRENT ACTIVITY */
//                    finish();
//                } else {
//                    Intent showTableSelector = new Intent(LoginActivity.this, TableSelector.class);
//                    showTableSelector.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(showTableSelector);
//
//                    /** SET LOGIN ID **/
//                    getApp().setLogin(strUserID, strUserName);
//
//						/* FINISH THE CURRENT ACTIVITY */
//                    finish();
//                }
//            } else {
//
//                MaterialDialog dialog = new MaterialDialog.Builder(LoginActivity.this)
//                        .title("AUTHENTICATION FAILED")
//                        .content(R.string.wrong_creds)
//                        .positiveText("Re-enter Credentials")
//                        .icon(getResources().getDrawable(R.drawable.ic_info_outline_white_24dp))
//                        .typeface("HelveticaNeueLTW1G-MdCn.otf", "HelveticaNeueLTW1G-Cn.otf")
//                        .show();
//            }
//        }
//    }
//
//    /***** CONFIGURE THE ACTIONBAR *****/
//    private void configAB() {
//
//        Toolbar myToolbar = (Toolbar) findViewById(R.id.myToolbar);
//        setSupportActionBar(myToolbar);
//
//        SpannableString s = new SpannableString(getResources().getString(R.string.login_tb));
//        s.setSpan(new TypefaceSpan(
//                this, "RobotoCondensed-Regular.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowTitleEnabled(true);
//        getSupportActionBar().setTitle(s);
//        getSupportActionBar().setSubtitle(null);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = new MenuInflater(LoginActivity.this);
//        inflater.inflate(R.menu.login_info, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        switch (item.getItemId()) {
//            case android.R.id.home:
//
//                this.finish();
//
//                break;
//
//            case R.id.whatIsThis:
//
//                /** SHOW THE LOGIN INFO **/
//                showLoginInfo();
//
//                break;
//
//            default:
//                break;
//        }
//
//        return false;
//    }
//
//    /***** METHOD TO SHOW THE LOGIN INFO *****/
//    private void showLoginInfo() {
//
//        MaterialDialog dialog = new MaterialDialog.Builder(this)
//                .title(R.string.log_info_title)
//                .content(R.string.log_info)
//                .positiveText("OKAY")
//                .icon(ContextCompat.getDrawable(LoginActivity.this, R.drawable.ic_info_outline_white_24dp))
//                .typeface("HelveticaNeueLTW1G-MdCn.otf", "HelveticaNeueLTW1G-Cn.otf")
//                .show();
//    }
//
//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
//    }
//}