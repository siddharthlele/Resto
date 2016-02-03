package com.dzinesunlimited.resto.welcome.frags;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.dzinesunlimited.resto.R;
import com.dzinesunlimited.resto.backend.creators.TaxCreator;
import com.dzinesunlimited.resto.utils.AppPrefs;
import com.dzinesunlimited.resto.utils.db.DBResto;
import com.dzinesunlimited.resto.utils.helpers.pojos.backend.TaxesData;

import java.util.ArrayList;

public class WelcomeTaxesFrag extends Fragment {

    /** GLOBAL VIEW INSTANCE **/
    private View view;

    /** THE DATABASE HELPER INSTANCE **/
    private DBResto db;

    /** THE CURSOR INSTANCE **/
    private Cursor cursor;

    /** DECLARE THE LAYOUT ELEMENTS **/
    private AppCompatTextView txtCountrySelected;
    private AppCompatTextView txtCurrencySelected;

    /* THE LIST VIEW */
    private ListView listTaxes;

    /** THE ADAPTER  **/
    private WelcomeTaxesAdapter adapter;

    /** THE ARRAYLIST **/
    private ArrayList<TaxesData> arrTaxes;

    /** NEW TAXES CREATOR REQUEST CODE **/
    private static final int ACTION_REQUEST_NEW_TAX = 0;

    private AppPrefs getApp()	{
        return (AppPrefs) getActivity().getApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /** CAST THE LAYOUT TO A NEW VIEW INSTANCE **/
        view = inflater.inflate(R.layout.welcome_taxes_list, container, false);

        /** RETURN THE VIEW INSTANCE TO SETUP THE LAYOUT **/
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /** INDICATE THAT THE FRAGMENT SHOULD RETAIN IT'S STATE **/
        setRetainInstance(true);

        /** INDICATE THAT THE FRAGMENT HAS AN OPTIONS MENU **/
        setHasOptionsMenu(true);

        /** INVALIDATE THE EARLIER OPTIONS MENU SET IN OTHER FRAGMENTS / ACTIVITIES **/
        getActivity().invalidateOptionsMenu();

        /** PREVENT THE FRAGMENT TO PASS THE FOCUS TO THE EDIT TEXT **/
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /** INSTANTIATE THE DATABASE HELPER CLASS **/
        db = new DBResto(getActivity());

        /** CAST THE LAYOUT ELEMENTS **/
        castLayoutElements();

        /** RUN THE TASK TO FETCH ALL THE TAXES FROM THE DATABASE **/
        new fetchTaxes().execute();

        /***** INSTANTIATE THE ADAPTER *****/
        adapter = new WelcomeTaxesAdapter(getActivity(), arrTaxes);
    }

    private void castLayoutElements() {

        txtCountrySelected = (AppCompatTextView) view.findViewById(R.id.txtCountrySelected);
        txtCurrencySelected = (AppCompatTextView) view.findViewById(R.id.txtCurrencySelected);

        /** THE LIST VIEW **/
        listTaxes = (ListView) view.findViewById(R.id.listTaxes);

        /** INSTANTIATE THE ARRAY LIST **/
        arrTaxes = new ArrayList<>();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser)	{

//			Log.e("setUserVisibleHint", "setUserVisibleHint CALLED");

            /** FETCH THE COUNTRY AND CURRENCY DEFAULT **/
//            fetchDefaults();
        }
    }


    private class fetchTaxes extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /** SHOW THE PROGRESSBAR WHILE FETCHING THE LIST OF TABLES **/
//			linlaHeaderProgress.setVisibility(View.VISIBLE);

			/* CONSTRUCT A QUERY TO FETCH TABLES ON RECORD */
            String strQueryData = "SELECT * FROM " + db.TAXES;
//			Log.e("TABLE QUERY", strQueryData);

			/* CAST THE QUERY IN THE CURSOR TO FETCH THE RESULTS */
            cursor = db.selectAllData(strQueryData);
//			Log.e("CURSOR", DatabaseUtils.dumpCursorToString(cursor));
        }

        @Override
        protected Void doInBackground(Void... params) {

            /** CHECK THAT THE DATABASE QUERY RETURNED SOME RESULTS **/
            if (cursor != null && cursor.getCount() != 0)	{

				/* AN INSTANCE OF THE TaxesData HELPER CLASS */
                TaxesData taxes;

                /** LOOP THROUGH THE RESULT SET AND PARSE NECESSARY INFORMATION **/
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                    /***** INSTANTIATE THE TaxesData INSTANCE "taxes" *****/
                    taxes = new TaxesData();

                    /** GET THE TAX ID **/
                    if (cursor.getString(cursor.getColumnIndex(db.TAX_ID)) != null)	{
                        String TAX_ID = cursor.getString(cursor.getColumnIndex(db.TAX_ID));
                        taxes.setTaxID(TAX_ID);
                    } else {
                        taxes.setTaxID(null);
                    }

                    /** GET THE TAX NAME **/
                    if (cursor.getString(cursor.getColumnIndex(db.TAX_NAME)) != null)	{
                        String TAX_NAME = cursor.getString(cursor.getColumnIndex(db.TAX_NAME));
                        taxes.setTaxName(TAX_NAME);
                    } else {
                        taxes.setTaxName(null);
                    }

                    /** GET THE TAX PERCENTAGE **/
                    if (cursor.getString(cursor.getColumnIndex(db.TAX_PERCENTAGE)) != null)	{
                        String TAX_PERCENTAGE = cursor.getString(cursor.getColumnIndex(db.TAX_PERCENTAGE));
                        taxes.setTaxPercentage(TAX_PERCENTAGE);
                    } else {
                        taxes.setTaxPercentage(null);
                    }

                    /** GET THE TAX REGISTRATION NUMBER **/
                    if (cursor.getString(cursor.getColumnIndex(db.TAX_REGISTRATION)) != null)	{
                        String TAX_REGISTRATION = cursor.getString(cursor.getColumnIndex(db.TAX_REGISTRATION));
                        taxes.setTaxRegistration(TAX_REGISTRATION);
                    } else {
                        taxes.setTaxRegistration(null);
                    }

                    /** GET THE TAX APPLICABLE ENTIRE AMOUNT STATUS **/
                    if (cursor.getString(cursor.getColumnIndex(db.TAX_ENTIRE_AMOUNT)) != null)	{
                        String TAX_ENTIRE_AMOUNT = cursor.getString(cursor.getColumnIndex(db.TAX_ENTIRE_AMOUNT));
                        taxes.setTaxCompleteAmount(TAX_ENTIRE_AMOUNT);
                    } else {
                        taxes.setTaxCompleteAmount(null);
                    }

                    /** GET THE TAX_AMOUNT_PERCENTAGE **/
                    if (cursor.getString(cursor.getColumnIndex(db.TAX_TAXABLE_PERCENTAGE)) != null)	{
                        String TAX_TAXABLE_PERCENTAGE = cursor.getString(cursor.getColumnIndex(db.TAX_TAXABLE_PERCENTAGE));
                        taxes.setTaxPercentageAmount(TAX_TAXABLE_PERCENTAGE);
                    } else {
                        taxes.setTaxPercentageAmount(null);
                    }

                    /** ADD THE COLLECTED DATA TO THE ARRAYLIST **/
                    arrTaxes.add(taxes);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

			/* CLOSE THE CURSOR */
            if (cursor != null && !cursor.isClosed())	{
                cursor.close();
            }

			/* CLOSE THE DATABASE */
            db.close();

            /** SET THE ADAPTER TO THE LISTVIEW **/
            listTaxes.setAdapter(adapter);

            /** HIDE THE PROGRESSBAR AFTER FETCHING THE LIST OF TABLES **/
//			linlaHeaderProgress.setVisibility(View.GONE);
        }
    }

    /** FETCH THE SELECTED COUNTRY AND CURRENCY **/
//    private void fetchDefaults() {
//
//        /***** GET THE SELECTED COUNTRY *****/
//        if (getApp().mPreferences.contains(getApp().COUNTRY_ID))	{
//
//            /***** GET THE SELECTED COUNTRY AND THE DEFAULT CURRENCY*****/
//            String[] arrCountry = getApp().getCountry();
//			/* THE COUNTRY AND CURRENCY STRINGS */
//            String COUNTRY_ID = arrCountry[0];
//            String COUNTRY_NAME = arrCountry[1];
//
//            if (COUNTRY_ID != null && COUNTRY_NAME != null)	{
//                txtCountrySelected.setText(COUNTRY_NAME);
//            } else {
//                String strCountry = getResources().getString(R.string.taxes_please_select_country);
//                txtCountrySelected.setText(strCountry);
//            }
//
//        } else {
//            String strCountry = getResources().getString(R.string.taxes_please_select_country);
//            txtCountrySelected.setText(strCountry);
//        }
//
//        /***** GET THE SELECTED CURRENCY *****/
//        if (getApp().mPreferences.contains(getApp().CURRENCY_ID))	{
//
//            String[] arrCurrency = getApp().getCurrency();
//            String CURRENCY_ID = arrCurrency[0];
//            String CURRENCY_NAME = arrCurrency[1];
//            String CURRENCY_CODE = arrCurrency[2];
//            String CURRENCY_SYMBOL = arrCurrency[3];
//
//            if ( CURRENCY_ID != null && CURRENCY_NAME != null && CURRENCY_CODE != null && CURRENCY_SYMBOL != null)	{
//                txtCurrencySelected.setText(CURRENCY_NAME + " (" + CURRENCY_CODE + ") ");
//            } else {
//                String strCurrency = getResources().getString(R.string.taxes_please_select_currency);
//                txtCurrencySelected.setText(strCurrency);
//            }
//
//        } else {
//            String strCurrency = getResources().getString(R.string.taxes_please_select_currency);
//            txtCurrencySelected.setText(strCurrency);
//        }
//
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_CANCELED)	{

			/* INVALIDATE THE LISTVIEW */
            listTaxes.invalidate();

			/* NOTIFY ADAPTER */
            adapter.notifyDataSetChanged();

			/* CLEAR THE ARRAYLIST */
            arrTaxes.clear();

            /** REFRESH THE LIST OF TABLES **/
            new fetchTaxes().execute();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.intro_tables_taxes, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.addNew:

				/* SHOW A DIALOG TO ADD A NEW TAX **/
                Intent addNewTax = new Intent(getActivity(), TaxCreator.class);
                startActivityForResult(addNewTax, ACTION_REQUEST_NEW_TAX);

                break;

            default:
                break;
        }

        return false;
    }

    private class WelcomeTaxesAdapter extends BaseAdapter {

        /* THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER */
        private final Activity activity;

        /* LAYOUTINFLATER TO USE A CUSTOM LAYOUT */
        private LayoutInflater inflater = null;

        /* ARRAYLIST TO GET DATA FROM THE ACTIVITY */
        private final ArrayList<TaxesData> arrTaxes;

        public WelcomeTaxesAdapter(Activity activity, ArrayList<TaxesData> arrTaxes) {

            // CAST THE ACTIVITY FROM THE METHOD TO THE LOCAL ACTIVITY INSTANCE
            this.activity = activity;

            // CAST THE CONTENTS OF THE ARRAYLIST IN THE METHOD TO THE LOCAL INSTANCE
            this.arrTaxes = arrTaxes;

            // INSTANTIATE THE LAYOUTINFLATER
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return arrTaxes.size();
        }

        @Override
        public Object getItem(int position) {
            return arrTaxes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

		    /* A VIEWHOLDER INSTANCE */
            ViewHolder holder;

		    /* CAST THE CONVERTVIEW IN A VIEW INSTANCE */
            View vi = convertView;

            /** CHECK CONVERTVIEW STATUS **/
            if (convertView == null)	{
			    /* CAST THE CONVERTVIEW INTO THE VIEW INSTANCE "vi"	*/
                vi = inflater.inflate(R.layout.welcome_taxes_item, null);

                // INSTANTIATE THE VIEWHOLDER INSTANCE
                holder = new ViewHolder();

                /*****	CAST THE LAYOUT ELEMENTS	*****/
                holder.txtNameLabel = (AppCompatTextView) vi.findViewById(R.id.txtNameLabel);
                holder.txtName = (AppCompatTextView) vi.findViewById(R.id.txtName);

                holder.txtPercentageLabel = (AppCompatTextView) vi.findViewById(R.id.txtPercentageLabel);
                holder.txtPercentage = (AppCompatTextView) vi.findViewById(R.id.txtPercentage);

                holder.txtNumberLabel = (AppCompatTextView) vi.findViewById(R.id.txtNumberLabel);
                holder.txtNumber = (AppCompatTextView) vi.findViewById(R.id.txtNumber);

                holder.txtAppliesOn = (AppCompatTextView) vi.findViewById(R.id.txtAppliesOn);

    			/* SET THE TAG TO "vi" */
                vi.setTag(holder);
            } else {
			    /* CAST THE VIEWHOLDER INSTANCE */
                holder = (ViewHolder) vi.getTag();
            }

            /** INSTANTIATE THE CUSTOM FONT **/
            Typeface fntTaxes = Typeface.createFromAsset(activity.getAssets(), "fonts/RobotoCondensed-Regular.ttf");
		    /* SET THE FONTS */
            holder.txtNameLabel.setTypeface(fntTaxes);
            holder.txtName.setTypeface(fntTaxes);

            holder.txtPercentageLabel.setTypeface(fntTaxes);
            holder.txtPercentage.setTypeface(fntTaxes);

            holder.txtNumberLabel.setTypeface(fntTaxes);
            holder.txtNumber.setTypeface(fntTaxes);

            holder.txtAppliesOn.setTypeface(fntTaxes);

            /** GET AND SET THE TAX NAME **/
            String strTaxName = arrTaxes.get(position).getTaxName();
            if (strTaxName != null)	{
                holder.txtName.setText(strTaxName);
            }

            /** GET AND SET THE TAX PERCENTAGE **/
            String strTaxPercentage = arrTaxes.get(position).getTaxPercentage();
            if (strTaxPercentage != null)	{
                holder.txtPercentage.setText(strTaxPercentage);
            }

            /** GET AND SET THE TAX REGISTRATION NUMBER **/
            String strTaxNumber = arrTaxes.get(position).getTaxRegistration();
            if (strTaxNumber != null)	{
                holder.txtNumber.setText(strTaxNumber);
            }

            /** GET THE STATUS OF TAX APPLICABLE ON AMOUNT **/
            boolean blnAppliesOn = Boolean.valueOf(arrTaxes.get(position).getTaxCompleteAmount());
            if (blnAppliesOn)	{
			    /* SET THE TAX APPLIES ON ENTIRE BILL AMOUNT */
                holder.txtAppliesOn.setText("This Tax " + "(\"" + strTaxName + "\")" + " applies on the entire Bill Amount");
            } else {

			    /* GET THE PERCENTAGE OF AMOUNT */
                String strPercentageAmount = arrTaxes.get(position).getTaxPercentageAmount();
                if (strPercentageAmount != null)	{

				    /* SET THE TAX APPLIES ON PERCENTAGE OF THE AMOUNT */
                    holder.txtAppliesOn.setText("This Tax " + "(\"" + strTaxName + "\")" +" applies on " + strPercentageAmount + "%" + " of the Bill Amount");
                }
            }

            /** GET THE **/

            return vi;
        }

        private class ViewHolder	{
            AppCompatTextView txtNameLabel;
            AppCompatTextView txtName;

            AppCompatTextView txtPercentageLabel;
            AppCompatTextView txtPercentage;

            AppCompatTextView txtNumberLabel;
            AppCompatTextView txtNumber;

            AppCompatTextView txtAppliesOn;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putString("WORKAROUND_FOR_BUG_19917_KEY",  "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }
}