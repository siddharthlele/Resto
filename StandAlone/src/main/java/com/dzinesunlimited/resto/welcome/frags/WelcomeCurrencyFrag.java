package com.dzinesunlimited.resto.welcome.frags;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.dzinesunlimited.resto.R;
import com.dzinesunlimited.resto.utils.AppPrefs;
import com.dzinesunlimited.resto.utils.db.DBResto;
import com.dzinesunlimited.resto.welcome.WelcomeContainer;

import java.util.ArrayList;

public class WelcomeCurrencyFrag extends Fragment {

    /** GLOBAL VIEW INSTANCE **/
    private View view;

    /** THE DATABASE HELPER INSTANCE **/
    private DBResto db;

    /** THE CURSOR INSTANCE **/
    private Cursor cursor;

    /** DECLARE THE LAYOUT ELEMENTS **/
    private LinearLayout linlaHeaderProgress;
    private ListView listCurrency;
    private LinearLayout linlaEmpty;

    /** THE ADAPTER TO SHOW THE LIST OF COUNTRIES **/
    private WelcomeCurrencyAdapter adapter;

    /** THE ARRAYLIST **/
    private ArrayList<WelcomeCurrencyData> arrCurrency = new ArrayList<>();

    private AppPrefs getApp()	{
        return (AppPrefs) getActivity().getApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /** CAST THE LAYOUT TO A NEW VIEW INSTANCE **/
        view = inflater.inflate(R.layout.welcome_currency_list, container, false);

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

        /** SHOW THE LIST OF COUNTRIES **/
        new showCountries().execute();

        /***** INSTANTIATE THE ADAPTER *****/
        adapter = new WelcomeCurrencyAdapter(getActivity(), arrCurrency);

        /** SELECT A CURRENCY AND SHIFT TO TAXES **/
        listCurrency.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                /** GET THE CURRENCY ID AND THE CURRENCY NAME **/
                String strCurrencyID = arrCurrency.get(position).getCurrencyID();
                String strCurrencyName = arrCurrency.get(position).getCurrencyName();
                String strCurrencyISOCode = arrCurrency.get(position).getCurrencyISOCode();
                String strCurrencySymbol = arrCurrency.get(position).getCurrencySymbol();

                /** SET THE COUNTRY ID AND THE COUNTRY NAME **/
                getApp().setCurrency(strCurrencyID, strCurrencyName, strCurrencyISOCode, strCurrencySymbol);

                /** SET CURRENCY SELECTED **/
                getApp().setCurrencySelection();

                /** CHANGE THE PAGER FRAGMENT **/
                ((WelcomeContainer) getActivity()).getPager().setCurrentItem(5);
            }
        });
    }


    private class showCountries extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /** SHOW THE PROGRESSBAR WHILE FETCHING THE LIST OF TABLES **/
            linlaHeaderProgress.setVisibility(View.VISIBLE);

			/* CONSTRUCT A QUERY TO FETCH TABLES ON RECORD */
            String strQueryData = "SELECT * FROM currency";
//			Log.e("TABLE QUERY", strQueryData);

			/* CAST THE QUERY IN THE CURSOR TO FETCH THE RESULTS */
            cursor = db.selectAllData(strQueryData);
//			Log.e("CURSOR", DatabaseUtils.dumpCursorToString(cursor));
        }

        @Override
        protected Void doInBackground(Void... params) {

            /** CHECK THAT THE DATABASE QUERY RETURNED SOME RESULTS **/
            if (cursor != null && cursor.getCount() != 0)	{

				/* AN INSTANCE OF THE CurrencyData HELPER CLASS */
                WelcomeCurrencyData currency;

                /** LOOP THROUGH THE RESULT SET AND PARSE NECESSARY INFORMATION **/
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                    /***** INSTANTIATE THE CurrencyData INSTANCE "currency" *****/
                    currency = new WelcomeCurrencyData();

                    /** GET THE CURRENCY ID **/
                    if (cursor.getString(cursor.getColumnIndex(db.CURRENCY_ID)) != null)	{
                        String CURRENCY_ID = cursor.getString(cursor.getColumnIndex(db.CURRENCY_ID));
                        currency.setCurrencyID(CURRENCY_ID);
                    } else {
                        currency.setCurrencyID(null);
                    }

                    /** GET THE CURRENCY NAME **/
                    if (cursor.getString(cursor.getColumnIndex(db.CURRENCY_NAME)) != null)	{
                        String CURRENCY_NAME = cursor.getString(cursor.getColumnIndex(db.CURRENCY_NAME));
                        currency.setCurrencyName(CURRENCY_NAME);
                    } else {
                        currency.setCurrencyName(null);
                    }

                    /** GET THE CURRENCY ISO CODE **/
                    if (cursor.getString(cursor.getColumnIndex(db.CURRENCY_ISO_CODE)) != null)	{
                        String CURRENCY_ISO_CODE = cursor.getString(cursor.getColumnIndex(db.CURRENCY_ISO_CODE));
                        currency.setCurrencyISOCode(CURRENCY_ISO_CODE);
//						Log.e("CODE", CURRENCY_ISO_CODE);
                    } else {
                        currency.setCurrencyISOCode(null);
                    }

                    /** GET THE CURRENCY UNICODE **/
                    if (cursor.getString(cursor.getColumnIndex(db.CURRENCY_SYMBOL)) != null)	{
                        String CURRENCY_SYMBOL = cursor.getString(cursor.getColumnIndex(db.CURRENCY_SYMBOL));
                        currency.setCurrencySymbol(CURRENCY_SYMBOL);
//						Log.e("SYMBOL", CURRENCY_SYMBOL);
                    } else {
                        currency.setCurrencySymbol(null);
                    }

                    /** ADD THE COLLECTED DATA TO THE ARRAYLIST **/
                    arrCurrency.add(currency);
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
            listCurrency.setAdapter(adapter);

            /** HIDE THE PROGRESSBAR AFTER FETCHING THE LIST OF TABLES **/
            linlaHeaderProgress.setVisibility(View.GONE);
        }
    }

    private void castLayoutElements() {
        linlaHeaderProgress = (LinearLayout) view.findViewById(R.id.linlaHeaderProgress);
        listCurrency = (ListView) view.findViewById(R.id.listCurrency);
        linlaEmpty = (LinearLayout) view.findViewById(R.id.linlaEmpty);
    }

    private class WelcomeCurrencyAdapter extends BaseAdapter {

        /** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER **/
        private final Activity activity;

        /** LAYOUT INFLATER TO USE A CUSTOM LAYOUT **/
        private LayoutInflater inflater = null;

        /** ARRAYLIST TO GET DATA FROM THE ACTIVITY **/
        private ArrayList<WelcomeCurrencyData> arrAdapCurrency;

        public WelcomeCurrencyAdapter(Activity activity, ArrayList<WelcomeCurrencyData> arrAdapCurrency) {

            // CAST THE ACTIVITY FROM THE METHOD TO THE LOCAL ACTIVITY INSTANCE
            this.activity = activity;

            // CAST THE CONTENTS OF THE ARRAYLIST IN THE METHOD TO THE LOCAL INSTANCE
            this.arrAdapCurrency = arrAdapCurrency;

            // INSTANTIATE THE LAYOUT INFLATER
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return arrAdapCurrency.size();
        }

        @Override
        public Object getItem(int position) {
            return arrAdapCurrency.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

		    /* A VIEW HOLDER INSTANCE */
            ViewHolder holder;

		    /* CAST THE CONVERT VIEW IN A VIEW INSTANCE */
            View vi = convertView;

            /** CHECK CONVERTVIEW STATUS **/
            if (convertView == null)	{
			    /* CAST THE CONVERTVIEW INTO THE VIEW INSTANCE "vi"	*/
                vi = inflater.inflate(R.layout.welcome_currency_item, null);

                // INSTANTIATE THE VIEW HOLDER INSTANCE
                holder = new ViewHolder();

                /*****	CAST THE LAYOUT ELEMENTS	*****/
                holder.txtCurrencyName = (AppCompatTextView) vi.findViewById(R.id.txtCurrencyName);
                holder.txtCurrencySymbol = (AppCompatTextView) vi.findViewById(R.id.txtCurrencySymbol);
                holder.txtCurrencyCode = (AppCompatTextView) vi.findViewById(R.id.txtCurrencyCode);

			    /* SET THE TAG TO "vi" */
                vi.setTag(holder);
            } else {
			    /* CAST THE VIEW HOLDER INSTANCE */
                holder = (ViewHolder) vi.getTag();
            }

            /** INSTANTIATE THE CUSTOM FONT AND SET **/
            Typeface fntCurrency = Typeface.createFromAsset(activity.getAssets(), "fonts/RobotoCondensed-Regular.ttf");
            holder.txtCurrencyName.setTypeface(fntCurrency);
            holder.txtCurrencySymbol.setTypeface(fntCurrency);
            holder.txtCurrencyCode.setTypeface(fntCurrency);

            /** GET AND SET THE CURRENCY NAME **/
            String strCurrencyName = arrAdapCurrency.get(position).getCurrencyName();
            if (strCurrencyName != null)	{
                holder.txtCurrencyName.setText(strCurrencyName);
            }

            /** GET AND SET THE CURRENCY ISO CODE **/
            String strCurrencyISOCode = arrAdapCurrency.get(position).getCurrencyISOCode();
            if (strCurrencyISOCode != null)	{
                holder.txtCurrencySymbol.setText(strCurrencyISOCode);
            }

            /** GET AND SET THE CURRENCY SYMBOL **/
            String strCurrencySymbol = arrAdapCurrency.get(position).getCurrencySymbol();
            if (strCurrencySymbol != null)	{
                holder.txtCurrencyCode.setText(strCurrencySymbol);
            }

            return vi;
        }

        private class ViewHolder	{
            AppCompatTextView txtCurrencyName;
            AppCompatTextView txtCurrencySymbol;
            AppCompatTextView txtCurrencyCode;
        }
    }

    private class WelcomeCurrencyData {

        private String currencyID;
        private String currencyName;
        private String currencyISOCode;
        private String currencySymbol;

        public String getCurrencyID() {
            return currencyID;
        }

        public void setCurrencyID(String currencyID) {
            this.currencyID = currencyID;
        }

        public String getCurrencyName() {
            return currencyName;
        }

        public void setCurrencyName(String currencyName) {
            this.currencyName = currencyName;
        }

        public String getCurrencyISOCode() {
            return currencyISOCode;
        }

        public void setCurrencyISOCode(String currencyISOCode) {
            this.currencyISOCode = currencyISOCode;
        }

        public String getCurrencySymbol() {
            return currencySymbol;
        }

        public void setCurrencySymbol(String currencySymbol) {
            this.currencySymbol = currencySymbol;
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }
}