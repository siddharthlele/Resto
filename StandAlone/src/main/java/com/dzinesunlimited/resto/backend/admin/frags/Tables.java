package com.dzinesunlimited.resto.backend.admin.frags;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dzinesunlimited.resto.R;
import com.dzinesunlimited.resto.backend.creators.TableCreator;
import com.dzinesunlimited.resto.backend.modifiers.TableModifier;
import com.dzinesunlimited.resto.utils.TypefaceSpan;
import com.dzinesunlimited.resto.utils.db.DBResto;
import com.dzinesunlimited.resto.utils.helpers.pojos.backend.TablesData;

import java.util.ArrayList;

public class Tables extends Fragment {

    /** GLOBAL VIEW INSTANCE **/
    private View view;

    /** THE DATABASE HELPER INSTANCE **/
    private DBResto db;

    /***** DECLARE THE LAYOUT ELEMENTS *****/
	/* PROGRESS BAR */
    private LinearLayout linlaHeaderProgress;
    private RecyclerView listTables;
    private LinearLayout linlaEmpty;

    /** THE ADAPTER AND ARRAYLIST TO DISPLAY TABLES **/
    private TablesAdminAdapter adapter;
    private ArrayList<TablesData> arrTables = new ArrayList<>();

    /** NEW TABLE CREATOR REQUEST CODE **/
    private static final int ACTION_REQUEST_NEW_TABLE = 100;
    private static final int ACTION_REQUEST_EDIT_TABLE = 101;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /***** CAST THE LAYOUT TO A NEW VIEW INSTANCE *****/
        view = inflater.inflate(R.layout.be_admin_tables, container, false);

        /***** RETURN THE VIEW INSTANCE TO SETUP THE LAYOUT *****/
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
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /***** CAST THE LAYOUT ELEMENTS *****/
        castLayoutElements();

        /***** CONFIGURE THE ACTIONBAR *****/
        configAB();

        /** CLEAR THE ARRAYLIST **/
        arrTables.clear();

        /** SHOW THE LIST OF TABLES **/
        new showTables().execute();

        /***** INSTANTIATE THE ADAPTER *****/
        adapter = new TablesAdminAdapter(getActivity(), arrTables);
    }

    /** FETCH THE LIST OF TABLES **/
    private class showTables extends AsyncTask<Void, Void, Void> {

        /** A CURSOR INSTANCE **/
        Cursor cursor;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /** INSTANTIATE THE DATABASE HELPER CLASS **/
            db = new DBResto(getActivity());

            /** SHOW THE PROGRESSBAR WHILE FETCHING THE LIST OF TABLES **/
            linlaHeaderProgress.setVisibility(View.VISIBLE);

			/* CONSTRUCT A QUERY TO FETCH TABLES ON RECORD */
            String strQueryData = "SELECT * FROM " + db.TABLES;

			/* CAST THE QUERY IN THE CURSOR TO FETCH THE RESULTS */
            cursor = db.selectAllData(strQueryData);
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            /** CHECK THAT THE DATABASE QUERY RETURNED SOME RESULTS **/
            if (cursor != null && cursor.getCount() != 0)	{

                /** SHOW THE LIST OF TABLES AND HIDE THE EMPTY CONTAINER **/
                Runnable run = new Runnable() {

                    @Override
                    public void run() {

                        /** SHOW THE LIST OF TABLES **/
                        listTables.setVisibility(View.VISIBLE);

                        /** HIDE THE EMPTY DATA SET IMAGE AND TEXT **/
                        linlaEmpty.setVisibility(View.GONE);
                    }
                }; getActivity().runOnUiThread(run);

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

                    /** GET THE TABLE OCCUPANCY **/
                    if (cursor.getString(cursor.getColumnIndex(db.TABLE_OCCUPANCY)) != null)	{
                        String TABLE_OCCUPANCY = cursor.getString(cursor.getColumnIndex(db.TABLE_OCCUPANCY));
                        table.setTableOccupationStatus(TABLE_OCCUPANCY);
                    } else {
                        table.setTableOccupationStatus(null);
                    }

                    /** ADD THE COLLECTED DATA TO THE ARRAYLIST **/
                    arrTables.add(table);
                }

            } else {
                /** SHOW THE EMPTY CONTAINER AND HIDE THE LIST OF TABLES **/
                Runnable run = new Runnable() {

                    @Override
                    public void run() {

                        /** SHOW THE EMPTY DATA SET IMAGE AND TEXT **/
                        linlaEmpty.setVisibility(View.VISIBLE);

                        /** HIDE THE LIST OF TABLES **/
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.intro_tables_taxes, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.addNew:

				/* SHOW A DIALOG TO ADD A NEW TABLE **/
                Intent addNewTable = new Intent(getActivity(), TableCreator.class);
                startActivityForResult(addNewTable, ACTION_REQUEST_NEW_TABLE);

                break;

            default:
                break;
        }

        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != android.app.Activity.RESULT_CANCELED)	{

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

    /***** CAST THE LAYOUT ELEMENTS *****/
    private void castLayoutElements() {

		/* PROGRESS BAR */
        linlaHeaderProgress = (LinearLayout) view.findViewById(R.id.linlaHeaderProgress);
        listTables = (RecyclerView) view.findViewById(R.id.listTables);

        /* CONFIGURE THE RECYCLER VIEW */
        listTables.setHasFixedSize(false);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        listTables.setLayoutManager(llm);

        linlaEmpty = (LinearLayout) view.findViewById(R.id.linlaEmpty);
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {

        String strTitle = getResources().getString(R.string.tables_manager_title);
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(
                getActivity(), "RobotoCondensed-Regular.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(s);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putString("WORKAROUND_FOR_BUG_19917_KEY",  "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

    /********** THE TABLES ADAPTER **********/
    public class TablesAdminAdapter extends RecyclerView.Adapter<TablesAdminAdapter.TablesVH> {

        /* THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER */
        private final Activity activity;

        /* ARRAYLIST TO GET DATA FROM THE ACTIVITY */
        private final ArrayList<TablesData> arrTables;

        public TablesAdminAdapter(Activity activity, ArrayList<TablesData> arrTables) {

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

            /***** SHOW THE POPUP MENU *****/
            holder.imgvwMenuOptions.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    PopupMenu pm = new PopupMenu(activity, v);
                    pm.getMenuInflater().inflate(R.menu.pm_tables_taxes_item, pm.getMenu());
                    pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
//                            Toast.makeText(getActivity(), String.valueOf(item.getTitle()), Toast.LENGTH_SHORT).show();

                            switch (item.getItemId())   {

                                case R.id.menuEdit:

                                    /** EDIT THE TABLE **/
                                    Intent editTable = new Intent(getActivity(), TableModifier.class);
                                    editTable.putExtra("TABLE_NO", td.getTableNo());
                                    startActivityForResult(editTable, ACTION_REQUEST_EDIT_TABLE);

                                    break;

                                case R.id.menuDelete:

                                    /** DELETE THE TABLE **/
                                    final String TABLE_NO = td.getTableNo();
                                    String strTitle = "Delete Table \"No. " + td.getTableNo() + "\"?";
                                    String strMessage = "Are you sure you want to delete this Table? Pressing \"Yes\" will confirm the deletion and will be <b>permanent</b>.";
                                    String strYes = getResources().getString(R.string.generic_mb_yes);
                                    String strNo = getResources().getString(R.string.generic_mb_no);

                                    /** CONFIGURE THE ALERTDIALOG **/
                                    AlertDialog.Builder alertDelete = new AlertDialog.Builder(activity);
                                    alertDelete.setIcon(R.drawable.ic_info_outline_black_24dp);
                                    alertDelete.setTitle(strTitle);
                                    alertDelete.setMessage(strMessage);

                                    alertDelete.setNegativeButton(strNo, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            /** DISMISS THE DIALOG **/
                                            dialog.dismiss();
                                        }
                                    });

                                    alertDelete.setPositiveButton(strYes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            db = new DBResto(activity);
                                            db.deleteTable(TABLE_NO);

                                            /* INVALIDATE THE TABLES LISTVIEW (RECYCLERVIEW) */
                                            listTables.invalidate();

                                            /* NOTIFY THE ADAPTER */
                                            adapter.notifyDataSetChanged();

                                            /* CLEAR THE ARRAYLIST */
                                            arrTables.clear();

                                            /** REFRESH THE SUPPLIERS LIST  **/
                                            new showTables().execute();
                                        }
                                    });
                                    alertDelete.show();

                                    break;

                                default:
                                    break;
                            }

                            return true;
                        }
                    });
                    pm.show();
                }
            });
        }

        @Override
        public TablesVH onCreateViewHolder(ViewGroup parent, int i) {

            View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.be_admin_tables_item, parent, false);

            return new TablesVH(itemView);
        }

        public class TablesVH extends RecyclerView.ViewHolder   {

            /** POPUP OPTIONS **/
            AppCompatImageView imgvwMenuOptions;

            /* TABLE NUMBER */
            final AppCompatTextView txtTableNumberLabel;
            final AppCompatTextView txtTableNumber;

            /* TABLE SEATS */
            final AppCompatTextView txtTableSeatsLabel;
            final AppCompatTextView txtTableSeats;

            /* OCCUPATION STATUS */
            final AppCompatTextView txtOccupationStatusLabel;
            final AppCompatImageView imgvwOccupationStatus;

            public TablesVH(View v) {
                super(v);

                /* POPUP OPTIONS */
                imgvwMenuOptions = (AppCompatImageView) v.findViewById(R.id.imgvwMenuOptions);

                /* TABLE NUMBER */
                txtTableNumberLabel = (AppCompatTextView) v.findViewById(R.id.txtTableNumberLabel);
                txtTableNumber = (AppCompatTextView) v.findViewById(R.id.txtTableNumber);

                /* TABLE SEATS */
                txtTableSeatsLabel = (AppCompatTextView) v.findViewById(R.id.txtTableSeatsLabel);
                txtTableSeats = (AppCompatTextView) v.findViewById(R.id.txtTableSeats);

                /* OCCUPATION STATUS */
                txtOccupationStatusLabel = (AppCompatTextView) v.findViewById(R.id.txtOccupationStatusLabel);
                imgvwOccupationStatus = (AppCompatImageView) v.findViewById(R.id.imgvwOccupationStatus);
            }
        }
    }
}