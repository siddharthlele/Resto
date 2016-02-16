package com.dzinesunlimited.resto.utils.helpers.adapters.frontend;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dzinesunlimited.resto.R;
import com.dzinesunlimited.resto.frontend.menu.CategorySelector;
import com.dzinesunlimited.resto.utils.db.DBResto;
import com.dzinesunlimited.resto.utils.helpers.pojos.backend.TablesData;

import java.util.ArrayList;
import java.util.UUID;

public class TablesAdapter extends RecyclerView.Adapter<TablesAdapter.TablesVH> {

    /** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER **/
    Activity activity;

    /** ARRAYLIST TO GET DATA FROM THE ACTIVITY **/
    ArrayList<TablesData> arrTables;

    /** THE DATABASE HELPER INSTANCE **/
    DBResto db;

    public TablesAdapter(Activity activity, ArrayList<TablesData> arrTables) {

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
    public void onBindViewHolder(final TablesVH holder, final int position) {
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
        String strOccupationStatus = td.getTableStatus();
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

        /***** SHOW THE RESTO FRONT END *****/
        holder.linlaTableContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /** A CURSOR INSTANCE **/
                Cursor curSession = null;

                /** GET THE TABLE ID AND NUMBER **/
                String TABLE_NO = td.getTableNo();
                String SESSION_ID = null;

                /** CHECK IF A SESSION EXISTS AND IS OPEN **/
                db = new DBResto(activity);
                String qryOpenSession =
                        "SELECT * FROM " + db.SESSIONS +
                                " WHERE " + db.SESSION_TABLE_ID + " = " + TABLE_NO +
                                " AND " + db.SESSION_STATUS + " = 'open'";
                Log.e("OPEN SESSION QUERY", qryOpenSession);
                curSession = db.selectAllData(qryOpenSession);
//                Log.e("CURSOR DUMP", DatabaseUtils.dumpCursorToString(curSession));
//                db.close();
                if (curSession != null && curSession.getCount() != 0) {
                    /* SESSION ALREADY EXISTS. GET THE CURRENT SESSION ID */
                    for (curSession.moveToFirst(); !curSession.isAfterLast(); curSession.moveToNext()) {
                        /** GET THE SESSION TOKEN **/
                        if (curSession.getString(curSession.getColumnIndex(db.SESSION_TOKEN)) != null)	{
                            SESSION_ID = curSession.getString(curSession.getColumnIndex(db.SESSION_TOKEN));
                            Log.e("OPEN SESSION ID", SESSION_ID);
                        } else {
                            SESSION_ID = null;
                        }
                    }
                } else {
                    /** GENERATE A SESSION TOKEN **/
                    UUID uuid = UUID.randomUUID();
                    String sessionToken = String.valueOf(uuid);
//                    Log.e("SESSION ID", sessionToken);

                    /** TOGGLE THE TABLE STATUS **/
//                    db = new DBResto(activity);
                    db.updateTableStatus(td.getTableNo(), "true");

                    /** CREATE A NEW SESSION FOR THE CURRENT SELECTED TABLE **/
                    db.generateSession(sessionToken, td.getTableNo(), "open");
//                    db.close();
                }

                Intent startTakingOrders = new Intent(activity, CategorySelector.class);
                startTakingOrders.putExtra("TABLE_NO", TABLE_NO);
                startTakingOrders.putExtra("SESSION", SESSION_ID);
                activity.startActivity(startTakingOrders);
            }
        });
    }

    @Override
    public TablesVH onCreateViewHolder(ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.fe_table_selector_item, parent, false);

        return new TablesVH(itemView);
    }

    public class TablesVH extends RecyclerView.ViewHolder   {

        /* TABLE CONTAINER */
        LinearLayout linlaTableContainer;

        /* TABLE NUMBER */
        AppCompatTextView txtTableNumberLabel;
        AppCompatTextView txtTableNumber;

        /* TABLE SEATS */
        AppCompatTextView txtTableSeatsLabel;
        AppCompatTextView txtTableSeats;

        /* OCCUPATION STATUS */
        AppCompatTextView txtOccupationStatusLabel;
        AppCompatImageView imgvwOccupationStatus;

        public TablesVH(View v) {
            super(v);

                /* TABLE CONTAINER */
            linlaTableContainer = (LinearLayout) v.findViewById(R.id.linlaTableContainer);

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