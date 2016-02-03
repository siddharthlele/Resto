package com.dzinesunlimited.resto.welcome.frags;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dzinesunlimited.resto.R;
import com.dzinesunlimited.resto.backend.creators.TableCreator;
import com.dzinesunlimited.resto.utils.db.DBResto;
import com.dzinesunlimited.resto.utils.helpers.pojos.backend.TablesData;

import java.util.ArrayList;

public class WelcomeTablesFrag extends Fragment {

    /** GLOBAL VIEW INSTANCE **/
    private View view;

    /** THE DATABASE HELPER INSTANCE **/
    private DBResto db;

    /** THE CURSOR INSTANCE **/
    private Cursor cursor;

    /** NEW TABLE CREATOR REQUEST CODE **/
    private static final int ACTION_REQUEST_NEW_TABLE = 0;

    /** DECLARE THE LAYOUT ELEMENTS **/
    private LinearLayout linlaHeaderProgress;
    private RecyclerView listTables;
    private LinearLayout linlaEmpty;

    /** THE ADAPTER TO DISPLAY CUSTOM TABLE ITEMS **/
    private WelcomeTablesAdapter adapter;

    /** ARRAYLIST TO STORE AND PASS TABLES TO THE ADAPTER **/
    private ArrayList<TablesData> arrTables = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /** CAST THE LAYOUT TO A NEW VIEW INSTANCE **/
        view = inflater.inflate(R.layout.welcome_tables_list, container, false);

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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /** INSTANTIATE THE DATABASE HELPER CLASS **/
        db = new DBResto(getActivity());

        /** CAST THE LAYOUT ELEMENTS **/
        castLayoutElements();

        /***** CONFIGURE THE ACTIONBAR *****/
//        configAB();

        /** SHOW THE LIST OF TABLES **/
        new showTables().execute();

        /***** INSTANTIATE THE ADAPTER *****/
        adapter = new WelcomeTablesAdapter(getActivity(), arrTables);
    }

    /** FETCH THE LIST OF TABLES **/
    private class showTables extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /** SHOW THE PROGRESSBAR WHILE FETCHING THE LIST OF TABLES **/
            linlaHeaderProgress.setVisibility(View.VISIBLE);

			/* CONSTRUCT A QUERY TO FETCH TABLES ON RECORD */
            String strQueryData = "SELECT * FROM " + db.TABLES;
//			Log.e("TABLE QUERY", strQueryData);

			/* CAST THE QUERY IN THE CURSOR TO FETCH THE RESULTS */
            cursor = db.selectAllData(strQueryData);
//			Log.e("CURSOR", DatabaseUtils.dumpCursorToString(cursor));
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            /** CHECK THAT THE DATABASE QUERY RETURNED SOME RESULTS **/
            if (cursor != null && cursor.getCount() != 0)	{

				/* AN INSTANCE OF THE TablesData HELPER CLASS */
                TablesData table;

                /** LOOP THROUGH THE RESULT SET AND PARSE NECESSARY INFORMATION **/
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                    /***** INSTANTIATE THE TablesData INSTANCE "table" *****/
                    table = new TablesData();

                    /** GET THE TABLE NUMBER **/
                    if (cursor.getString(cursor.getColumnIndex(db.TABLE_ID)) != null)	{
                        String TABLE_NO = cursor.getString(cursor.getColumnIndex(db.TABLE_ID));
                        table.setTableNo(TABLE_NO);
                    } else {
                        table.setTableNo(null);
                    }

                    /** GET THE TABLE SEATS **/
                    if (cursor.getString(cursor.getColumnIndex(db.TABLE_SEATS)) != null)	{
                        String TABLE_SEATS = cursor.getString(cursor.getColumnIndex(db.TABLE_SEATS));
                        table.setTableSeats(TABLE_SEATS);
                    } else {
                        table.setTableNo(null);
                    }

                    /** GET THE TABLE OCCUPANCY STATUS **/
                    if (cursor.getString(cursor.getColumnIndex(db.TABLE_OCCUPANCY)) != null)	{
                        String TABLE_OCCUPANCY = cursor.getString(cursor.getColumnIndex(db.TABLE_OCCUPANCY));
                        table.setTableOccupationStatus(TABLE_OCCUPANCY);
                    } else {
                        table.setTableOccupationStatus(null);
                    }

                    /** ADD THE COLLECTED DATA TO THE ARRAYLIST **/
                    arrTables.add(table);
                }

                /** SHOW THE LISTVIEW AND HIDE THE EMPTY CONTAINER **/
                Runnable run = new Runnable() {

                    @Override
                    public void run() {

                        /** SHOW THE RECYCLERVIEW **/
                        listTables.setVisibility(View.VISIBLE);

                        /** HIDE THE EMPTY DATA SET IMAGE AND TEXT **/
                        linlaEmpty.setVisibility(View.GONE);
                    }
                }; getActivity().runOnUiThread(run);

            } else {

                /** SHOW THE EMPTY CONTAINER AND HIDE THE LISTVIEW **/
                Runnable run = new Runnable() {

                    @Override
                    public void run() {

                        /** SHOW THE EMPTY DATA SET IMAGE AND TEXT **/
                        linlaEmpty.setVisibility(View.VISIBLE);

                        /** HIDE THE RECYCLERVIEW **/
                        listTables.setVisibility(View.GONE);
                    }
                }; getActivity().runOnUiThread(run);
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
            listTables.setAdapter(adapter);

            /** HIDE THE PROGRESSBAR AFTER FETCHING THE LIST OF TABLES **/
            linlaHeaderProgress.setVisibility(View.GONE);
        }
    }

    /** CAST THE LAYOUT ELEMENTS **/
    private void castLayoutElements() {

        linlaHeaderProgress = (LinearLayout) view.findViewById(R.id.linlaHeaderProgress);
        listTables = (RecyclerView) view.findViewById(R.id.listTables);
        linlaEmpty = (LinearLayout) view.findViewById(R.id.linlaEmpty);

        /* CONFIGURE THE RECYCLERVIEW */
        listTables.setHasFixedSize(false);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        listTables.setLayoutManager(llm);
    }

    /***** CONFIGURE THE ACTIONBAR *****/
//    private void configAB() {
//        Toolbar myToolbar = (Toolbar) view.findViewById(R.id.myToolbar);
//        ((AppCompatActivity)getActivity()).setSupportActionBar(myToolbar);
//
//        SpannableString s = new SpannableString("Add / Configure Your Tables");
//        s.setSpan(new TypefaceSpan(
//                getActivity(), "RobotoCondensed-Regular.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(s);
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle(null);
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_CANCELED)	{

			/* INVALIDATE THE LISTVIEW */
            listTables.invalidate();

			/* NOTIFY ADAPTER */
            adapter.notifyDataSetChanged();

			/* CLEAR THE ARRAYLIST */
            arrTables.clear();

            /** REFRESH THE LIST OF TABLES **/
            new showTables().execute();

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

                /** ADD A NEW TABLE **/
                Intent addNewTable = new Intent(getActivity(), TableCreator.class);
                startActivityForResult(addNewTable, ACTION_REQUEST_NEW_TABLE);

                break;

            default:
                break;
        }

        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putString("WORKAROUND_FOR_BUG_19917_KEY",  "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

    /********** THE TABLES ADAPTER **********/
    private class WelcomeTablesAdapter extends RecyclerView.Adapter<WelcomeTablesAdapter.TablesVH> {

        /* THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER */
        private final Activity activity;

        /* ARRAYLIST TO GET DATA FROM THE ACTIVITY */
        private final ArrayList<TablesData> arrTables;

        public WelcomeTablesAdapter(Activity activity, ArrayList<TablesData> arrTables) {

            /** CAST THE ACTIVITY FROM THE METHOD TO THE LOCAL ACTIVITY INSTANCE **/
            this.activity = activity;

            /** CAST THE CONTENTS OF THE ARRAYLIST IN THE METHOD TO THE LOCAL INSTANCE **/
            this.arrTables = arrTables;
        }

        @Override
        public int getItemCount() {
            return arrTables.size();
        }

        @Override
        public void onBindViewHolder(TablesVH holder, int position) {
            final TablesData td = arrTables.get(position);

            /** SET THE TABLE NUMBER **/
            String strTableNumber = td.getTableNo();
            if (strTableNumber != null)	{
                holder.txtTableNumber.setText(strTableNumber);
            }

            /** SET THE TABLE SEATS **/
            String strTableSeats = td.getTableSeats();
            if (strTableSeats != null)	{
                holder.txtTableSeats.setText(strTableSeats);
            }

            /** SET THE OCCUPATION STATUS */
            String strOccupationStatus = td.getTableOccupationStatus();
            if (strOccupationStatus != null)	{
                /* TRUE -> OCCUPIED	||	FALSE -> VACANT */
                if (strOccupationStatus.equals("true"))	{
                    holder.imgvwOccupationStatus.setImageResource(R.drawable.ic_signal_red);
                } else {
                    holder.imgvwOccupationStatus.setImageResource(R.drawable.ic_signal_green);
                }
            } else {
                holder.imgvwOccupationStatus.setImageResource(R.drawable.ic_signal_green);
            }
        }

        @Override
        public TablesVH onCreateViewHolder(ViewGroup parent, int i) {

            View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.welcome_tables_item, parent, false);

            return new TablesVH(itemView);
        }

        public class TablesVH extends RecyclerView.ViewHolder   {

            /* TABLE NUMBER */
            final AppCompatTextView txtTableNumberLabel;
            final AppCompatTextView txtTableNumber;

            /* TABLE SEATS */
            final AppCompatTextView txtTableSeatsLabel;
            final AppCompatTextView txtTableSeats;

            /* OCCUPATION STATUS */
            final AppCompatTextView txtOccupationStatusLabel;
            final ImageView imgvwOccupationStatus;

            public TablesVH(View v) {
                super(v);

                /* TABLE NUMBER */
                txtTableNumberLabel = (AppCompatTextView) v.findViewById(R.id.txtTableNumberLabel);
                txtTableNumber = (AppCompatTextView) v.findViewById(R.id.txtTableNumber);

                /* TABLE SEATS */
                txtTableSeatsLabel = (AppCompatTextView) v.findViewById(R.id.txtTableSeatsLabel);
                txtTableSeats = (AppCompatTextView) v.findViewById(R.id.txtTableSeats);

                /* OCCUPATION STATUS */
                txtOccupationStatusLabel = (AppCompatTextView) v.findViewById(R.id.txtOccupationStatusLabel);
                imgvwOccupationStatus = (ImageView) v.findViewById(R.id.imgvwOccupationStatus);

                /** INSTANTIATE THE CUSTOM FONT **/
                Typeface fntTablesActivity = Typeface.createFromAsset(activity.getAssets(), "fonts/RobotoCondensed-Regular.ttf");

                /* SET THE FONTS */
                txtTableNumber.setTypeface(fntTablesActivity);
                txtTableNumberLabel.setTypeface(fntTablesActivity);
                txtTableSeats.setTypeface(fntTablesActivity);
                txtTableSeatsLabel.setTypeface(fntTablesActivity);
                txtOccupationStatusLabel.setTypeface(fntTablesActivity);
            }
        }
    }
}
