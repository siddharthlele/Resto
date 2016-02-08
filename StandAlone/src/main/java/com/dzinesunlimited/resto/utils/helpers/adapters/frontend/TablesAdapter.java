package com.dzinesunlimited.resto.utils.helpers.adapters.frontend;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dzinesunlimited.resto.R;
import com.dzinesunlimited.resto.frontend.menu.CategorySelector;
import com.dzinesunlimited.resto.utils.db.DBResto;
import com.dzinesunlimited.resto.utils.helpers.pojos.backend.TablesData;

import java.util.ArrayList;

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
    public void onBindViewHolder(TablesVH holder, final int position) {
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

        /***** SHOW THE RESTO FRONT END *****/
        holder.linlaTableContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /** GET THE TABLE ID AND NUMBER **/
                String TABLE_NO = td.getTableNo();
                String TABLE_STATUS = td.getTableOccupationStatus();
                if (TABLE_STATUS != null) {
//                    Log.e("STATUS", TABLE_STATUS);
                }

                /** TOGGLE THE TABLE STATUS **/
                db = new DBResto(activity);
                db.updateTableStatus(td.getTableNo(), "true");
                db.close();

                Intent startTakingOrders = new Intent(activity, CategorySelector.class);
                startTakingOrders.putExtra("TABLE_NO", TABLE_NO);
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