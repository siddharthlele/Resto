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
import com.dzinesunlimited.resto.utils.db.DBResto;
import com.dzinesunlimited.resto.welcome.WelcomeContainer;

import java.util.ArrayList;

public class WelcomeCountriesFrag extends Fragment {

    /** GLOBAL VIEW INSTANCE **/
    private View view;

    /** THE DATABASE HELPER INSTANCE **/
    private DBResto db;

    /** THE CURSOR INSTANCE **/
    private Cursor cursor;

    /** DECLARE THE LAYOUT ELEMENTS **/
    private LinearLayout linlaHeaderProgress;
    private ListView listCountries;
    private LinearLayout linlaEmpty;

    /** THE ADAPTER TO SHOW THE LIST OF COUNTRIES **/
    private WelcomeCountriesAdapter adapter;

    /** THE ARRAYLIST **/
    private ArrayList<WelcomeCountriesData> arrCountry = new ArrayList<>();

//    private AppPrefs getApp()	{
//        return (AppPrefs) getActivity().getApplication();
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /** CAST THE LAYOUT TO A NEW VIEW INSTANCE **/
        view = inflater.inflate(R.layout.welcome_countries_list, container, false);

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

        /** PREVENT THE FRAGMENT TO PASS THE FOCUS TO THE EDITTEXT **/
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
        adapter = new WelcomeCountriesAdapter(getActivity(), arrCountry);

        /** SELECT A COUNTRY AND SHIFT TO CURRENCIES **/
        listCountries.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                /** GET THE COUNTRY ID AND THE COUNTRY NAME **/
                String strCountryID = arrCountry.get(position).getCountryID();
                String strCountryName = arrCountry.get(position).getCountryName();

                /** SET THE COUNTRY ID AND THE COUNTRY NAME **/
//                getApp().setCountry(strCountryID, strCountryName);

                /** SET COUNTRY SELECTED **/
//                getApp().setCountrySelection();

                /** CHANGE THE PAGER FRAGMENT **/
                ((WelcomeContainer) getActivity()).getPager().setCurrentItem(4);
            }
        });
    }

    /** TASK TO SHOW THE LIST OF COUNTRIES **/
    private class showCountries extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /** SHOW THE PROGRESSBAR WHILE FETCHING THE LIST OF TABLES **/
            linlaHeaderProgress.setVisibility(View.VISIBLE);

			/** CONSTRUCT A QUERY TO FETCH COUNTRIES ON RECORD **/
            String strQueryData = "SELECT * FROM " + db.COUNTRIES;

			/** CAST THE QUERY IN THE CURSOR TO FETCH THE RESULTS **/
            cursor = db.selectAllData(strQueryData);
        }

        @Override
        protected Void doInBackground(Void... params) {

            /** CHECK THAT THE DATABASE QUERY RETURNED SOME RESULTS **/
            if (cursor != null && cursor.getCount() != 0)	{

				/* AN INSTANCE OF THE CountriesData HELPER CLASS */
                WelcomeCountriesData country;

                /** LOOP THROUGH THE RESULT SET AND PARSE NECESSARY INFORMATION **/
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                    /***** INSTANTIATE THE CountriesData INSTANCE "country" *****/
                    country = new WelcomeCountriesData();

                    /** GET THE COUNTRY ID **/
                    if (cursor.getString(cursor.getColumnIndex(db.COUNTRY_ID)) != null)	{
                        String COUNTRY_ID = cursor.getString(cursor.getColumnIndex(db.COUNTRY_ID));
                        country.setCountryID(COUNTRY_ID);
                    } else {
                        country.setCountryID(null);
                    }

                    /** GET THE COUNTRY NAME **/
                    if (cursor.getString(cursor.getColumnIndex(db.COUNTRY_NAME)) != null)	{
                        String COUNTRY_NAME = cursor.getString(cursor.getColumnIndex(db.COUNTRY_NAME));
                        country.setCountryName(COUNTRY_NAME);
                    } else {
                        country.setCountryName(null);
                    }

                    /** ADD THE COLLECTED DATA TO THE ARRAYLIST **/
                    arrCountry.add(country);
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
            listCountries.setAdapter(adapter);

            /** HIDE THE PROGRESSBAR AFTER FETCHING THE LIST OF TABLES **/
            linlaHeaderProgress.setVisibility(View.GONE);
        }
    }

    private void castLayoutElements() {
        linlaHeaderProgress = (LinearLayout) view.findViewById(R.id.linlaHeaderProgress);
        listCountries = (ListView) view.findViewById(R.id.listCountries);
        linlaEmpty = (LinearLayout) view.findViewById(R.id.linlaEmpty);
    }

    private class WelcomeCountriesAdapter extends BaseAdapter {

        /** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER **/
        private final Activity activity;

        /** LAYOUT INFLATER TO USE A CUSTOM LAYOUT **/
        private LayoutInflater inflater = null;

        /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
        private ArrayList<WelcomeCountriesData> arrAdapCountry;

        /** CUSTOM FONTS **/
        private Typeface fntCountry;

        public WelcomeCountriesAdapter(Activity activity, ArrayList<WelcomeCountriesData> arrAdapCountry) {

            // CAST THE ACTIVITY FROM THE METHOD TO THE LOCAL ACTIVITY INSTANCE
            this.activity = activity;

            // CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE
            this.arrAdapCountry = arrAdapCountry;

            // INSTANTIATE THE LAYOUT INFLATER
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return arrAdapCountry.size();
        }

        @Override
        public Object getItem(int position) {
            return arrAdapCountry.get(position);
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

            /** CHECK CONVERT VIEW STATUS **/
            if (convertView == null)	{
			    /* CAST THE CONVERT VIEW INTO THE VIEW INSTANCE "vi"	*/
                vi = inflater.inflate(R.layout.welcome_countries_item, null);

                /* INSTANTIATE THE VIEW HOLDER INSTANCE */
                holder = new ViewHolder();

                /*****	CAST THE LAYOUT ELEMENTS	*****/
                holder.txtCountryName = (AppCompatTextView) vi.findViewById(R.id.txtCountryName);

			    /* SET THE TAG TO "vi" */
                vi.setTag(holder);
            } else {
			    /* CAST THE VIEW HOLDER INSTANCE */
                holder = (ViewHolder) vi.getTag();
            }

            /** INSTANTIATE THE CUSTOM FONT AND SET **/
            fntCountry = Typeface.createFromAsset(activity.getAssets(), "fonts/RobotoCondensed-Regular.ttf");
            holder.txtCountryName.setTypeface(fntCountry);

            /** GET AND SET THE COUNTRY NAME **/
            String strCountryName = arrAdapCountry.get(position).getCountryName();
            if (strCountryName != null)	{
                holder.txtCountryName.setText(strCountryName);
            }

            return vi;
        }


        private class ViewHolder	{
            AppCompatTextView txtCountryName;
        }
    }

    private class WelcomeCountriesData {

        private String countryID;
        private String countryName;

        public String getCountryID() {
            return countryID;
        }

        public void setCountryID(String countryID) {
            this.countryID = countryID;
        }

        public String getCountryName() {
            return countryName;
        }

        public void setCountryName(String countryName) {
            this.countryName = countryName;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }
}