package com.dzinesunlimited.resto.utils;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.dzinesunlimited.resto.BuildConfig;
import com.dzinesunlimited.resto.R;

import butterknife.ButterKnife;
import pl.aprilapps.easyphotopicker.EasyImage;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class AppPrefs extends Application {
	
	/***** SHARED PREFERENCES INSTANCE *****/
	public SharedPreferences mPreferences;

	/** THE INITIAL SETUP STATUS **/
	private final String SETUP_STATUS = "setupStatus";

    /* RESTAURANT SET */
    private final String RESTAURANT_SET = "restaurant_set";

    /** LOGIN USER ID AND NAME **/
    private final String LOGIN_USER_ID = "userID";
    private final String LOGIN_USERNAME = "userName";

    /* CURRENCY SELECTED */
    private final String CURRENCY_SET = "currency_set";
    public final String CURRENCY_ID = "currency_id";
    private final String CURRENCY_NAME = "currency_name";
    private final String CURRENCY_ISO_CODE = "currency_iso_code";
    private final String CURRENCY_SYMBOL = "currency_symbol";
	
    @Override
    public void onCreate() {
    	super.onCreate();

        /** ENABLE BUTTERKNIFE DEBUGGER **/
        ButterKnife.setDebug(BuildConfig.DEBUG);

//        /** INITIALIZE THE FACEBOOK STETHO SDK **/
//        Stetho.initializeWithDefaults(this);
//        OkHttpClient client = new OkHttpClient();
//        client.networkInterceptors().add(new StethoInterceptor());

        /** CONFIGURE THE EASY IMAGE LIBRARY **/
        EasyImage.configuration(this)
                .setImagesFolderName("Resto")
                .saveInAppExternalFilesDir();

		/** INSTANTIATE THE PREFERENCE MANAGER **/
    	mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		/** INSTANTIATE THE CALLIGRAPHY LIBRARY **/
		CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
				.setDefaultFontPath("fonts/Roboto-Regular.ttf")
				.setFontAttrId(R.attr.fontPath)
				.build());
    }

	/** SET THE INITIAL SETUP STATUS **/
	public void setTutorialCompleted() {
		final Editor edit = mPreferences.edit();
		edit.putBoolean(SETUP_STATUS, true);
		edit.apply();
	}

	/** GET THE INITIAL SETUP COMPLETED STATUS **/
	public boolean getTutorialCompleted()	{
		return mPreferences.getBoolean(SETUP_STATUS, false);
	}

    /** SET RESTAURANT **/
    public void setRestaurantSet() {
        final Editor edit = mPreferences.edit();
        edit.putBoolean(RESTAURANT_SET, true);
        edit.apply();
    }

    /** GET RESTAURANT **/
    public boolean getRestaurantSet() {
        return mPreferences.getBoolean(RESTAURANT_SET, false);
    }

    /** SET THE LOGIN - USERNAME **/
    public void setLogin(String strUserID, String strUserName) {
        final Editor edit = mPreferences.edit();
        edit.putString(LOGIN_USER_ID, strUserID);
        edit.putString(LOGIN_USERNAME, strUserName);
        edit.apply();
    }

    /** GET THE LOGIN - USERNAME **/
    public String[] getLogin()	{
        String strUserID = mPreferences.getString(LOGIN_USER_ID, null);
        String strUserName = mPreferences.getString(LOGIN_USERNAME, null);

        return new String[]	{ strUserID, strUserName };
    }

    /** A CURRENCY IS SET **/
    public void setCurrencySelection()	{
        final Editor edit = mPreferences.edit();
        edit.putBoolean(CURRENCY_SET, true);
        edit.apply();
    }

    /** GET THE CURRENCY STATUS **/
    public boolean getCurrencySelection()	{
        return mPreferences.getBoolean(CURRENCY_SET, false);
    }

    public void setCurrency(
            String strCurrencyID, String strCurrencyName,
            String strCurrencyISOCode, String strCurrencySymbol) {
        final Editor edit = mPreferences.edit();
        edit.putString(CURRENCY_ID, strCurrencyID);
        edit.putString(CURRENCY_NAME, strCurrencyName);
        edit.putString(CURRENCY_ISO_CODE, strCurrencyISOCode);
        edit.putString(CURRENCY_SYMBOL, strCurrencySymbol);
        edit.apply();

    }

    public String[] getCurrency() {
        String strCurrencyID = mPreferences.getString(CURRENCY_ID, null);
        String strCurrencyName = mPreferences.getString(CURRENCY_NAME, null);
        String strCurrencyCode = mPreferences.getString(CURRENCY_ISO_CODE, null);
        String strCurrencySymbol = mPreferences.getString(CURRENCY_SYMBOL, null);

        return new String[] { strCurrencyID, strCurrencyName, strCurrencyCode, strCurrencySymbol };
    }
}