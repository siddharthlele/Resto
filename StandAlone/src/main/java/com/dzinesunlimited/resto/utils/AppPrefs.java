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

    /* RESTAURANT DETAILS */
    private final String RESTAURANT_NAME = "restaurant_name";
    private final String RESTAURANT_ADDRESS_1 = "restaurant_add1";
    private final String RESTAURANT_ADDRESS_2 = "restaurant_add2";
    private final String RESTAURANT_CITY = "restaurant_city";
    private final String RESTAURANT_STATE = "restaurant_state";
    private final String RESTAURANT_COUNTRY = "restaurant_country";
    private final String RESTAURANT_ZIP = "restaurant_zip";
    private final String RESTAURANT_CONTACT_NUMBER = "restaurant_contact_number";
    private final String RESTAURANT_EMAIL = "restaurant_email";
    private final String RESTAURANT_WEBSITE = "restaurant_website";

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

    /** SET THE RESTAURANT DETAILS **/
    public void setRestaurantDetails(
            String name, String address1, String address2,
            String city, String state, String country, String zip,
            String contactNo, String emailAddress, String website) {

        final Editor edit = mPreferences.edit();
        edit.putString(RESTAURANT_NAME, name);
        edit.putString(RESTAURANT_ADDRESS_1, address1);
        edit.putString(RESTAURANT_ADDRESS_2, address2);
        edit.putString(RESTAURANT_CITY, city);
        edit.putString(RESTAURANT_STATE, state);
        edit.putString(RESTAURANT_COUNTRY, country);
        edit.putString(RESTAURANT_ZIP, zip);
        edit.putString(RESTAURANT_CONTACT_NUMBER, contactNo);
        edit.putString(RESTAURANT_EMAIL, emailAddress);
        edit.putString(RESTAURANT_WEBSITE, website);
        edit.apply();
    }

    /** GET THE RESTAURANT DETAILS **/
    public String[] getRestaurantDetails()	{
        String name = mPreferences.getString(RESTAURANT_NAME, null);
        String address1 = mPreferences.getString(RESTAURANT_ADDRESS_1, null);
        String address2 = mPreferences.getString(RESTAURANT_ADDRESS_2, null);
        String city = mPreferences.getString(RESTAURANT_CITY, null);
        String state = mPreferences.getString(RESTAURANT_STATE, null);
        String country = mPreferences.getString(RESTAURANT_COUNTRY, null);
        String zip = mPreferences.getString(RESTAURANT_ZIP, null);
        String contact = mPreferences.getString(RESTAURANT_CONTACT_NUMBER, null);
        String emailAddress = mPreferences.getString(RESTAURANT_EMAIL, null);
        String website = mPreferences.getString(RESTAURANT_WEBSITE, null);

        return new String[]	{
                name, address1, address2,
                city, state, country, zip,
                contact, emailAddress, website};
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