package com.dzinesunlimited.resto;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.dzinesunlimited.resto.backend.admin.AdminLanding;
import com.dzinesunlimited.resto.utils.AppPrefs;
import com.dzinesunlimited.resto.utils.db.DBResto;
import com.dzinesunlimited.resto.welcome.WelcomeContainer;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SplashScreen extends AppCompatActivity {

    /** INSTANCE OF THE DATABASE HELPER CLASS **/
    private DBResto db;

    /** THE TEXT VIEW FOR ANIMATING (FADE IN / OUT) **/
    AppCompatTextView txtAppName;

    /** THE ANIMATIONS (FADE IN / OUT) **/
    Animation animIn;

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        /** CONFIGURE THE ANIMATIONS **/
        animIn = new AlphaAnimation(0.0f, 1.0f);
        animIn.setDuration(4000);

        /** CAST THE TEXT VIEW AND ANIMATE **/
        txtAppName = (AppCompatTextView) findViewById(R.id.txtAppName);
        txtAppName.startAnimation(animIn);

        db = new DBResto(this);
        String s = "SELECT * FROM " + db.COUNTRIES;
        Cursor cursor = db.selectAllData(s);
        if (cursor.getCount() == 0) {

            /** POPULATE THE "COUNTRIES" TABLE IN THE DATABASE ***/
            new addCountriesToDB().execute();
        } else {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    /** CHECK TUTORIAL STATUS **/
                    boolean blnTutorial = getApp().getTutorialCompleted();
                    if (blnTutorial) {
                        /* SHOW THE LOGIN PAGE */
                        Intent startMainPage = new Intent(SplashScreen.this, LoginActivity.class);
                        startMainPage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(startMainPage);

				        /* FINISH THE CURRENT ACTIVITY */
                        finish();
                    } else {
				        /* SHOW THE WELCOME SCREEN **/
                        Intent showWelcome = new Intent(SplashScreen.this, WelcomeContainer.class);
                        showWelcome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(showWelcome);

				        /* FINISH THE CURRENT ACTIVITY */
                        finish();
                    }
                }
            }, 4000);
        }
    }

    /***** ADD COUNTRIES FROM THE ARRAY RESOURCE FILE TO THE DATABASE *****/
    private class addCountriesToDB extends AsyncTask<Void, Void, Void> {

        /* STRING ARRAY TO HOLD THE COUNTRIES ARRAY FROM RESOURCES */
        String[] arrCountries;

        /* A CURSOR INSTANCE */
        Cursor cursor;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /** INSTANTIATE THE DATABASE **/
            db = new DBResto(SplashScreen.this);

            /** GET COUNTRIES ARRAY FROM STRINGS.XML **/
            arrCountries = getResources().getStringArray(R.array.countries_array);

            /** CONSTRUCT A QUERY TO FETCH THE COUNTRIES ON RECORD **/
            String strQueryData = "SELECT * FROM " + db.COUNTRIES;

            /** CAST THE QUERY IN THE CURSOR TO FETCH THE RESULTS **/
            cursor = db.selectAllData(strQueryData);
        }

        @Override
        protected Void doInBackground(Void... params) {

            for (String strCountry : arrCountries) {
                /** ADD THE COUNTRY TO THE DATABASE **/
                if (cursor.getCount() == 0) {
                    db.addCountry(strCountry);
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

            /** CHECK TUTORIAL STATUS **/
            boolean blnTutorial = getApp().getTutorialCompleted();
            if (blnTutorial) {
                Intent startMainPage = new Intent(SplashScreen.this, LoginActivity.class);
                startMainPage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(startMainPage);

				/* FINISH THE CURRENT ACTIVITY */
                finish();
            } else {
				/* START THE WELCOME SCREEN **/
                Intent showWelcome = new Intent(SplashScreen.this, WelcomeContainer.class);
                showWelcome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(showWelcome);

				/* FINISH THE CURRENT ACTIVITY */
                finish();
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}