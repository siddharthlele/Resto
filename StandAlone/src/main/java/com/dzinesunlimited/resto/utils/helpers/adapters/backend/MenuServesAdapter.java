package com.dzinesunlimited.resto.utils.helpers.adapters.backend;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.dzinesunlimited.resto.R;

import java.util.List;

public class MenuServesAdapter extends ArrayAdapter<String> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    final Activity activity;

    /***** LAYOUT INFLATER TO USE A CUSTOM LAYOUT *****/
    LayoutInflater inflater = null;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    final List<String> arrCheckIn;

    public MenuServesAdapter(
            Activity activity,
            int resource,
            List<String> arrCheckIn) {
        super(activity, resource);

        /* CAST THE ACTIVITY FROM THE METHOD TO THE LOCAL ACTIVITY INSTANCE */
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAYLIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrCheckIn = arrCheckIn;

        /* INSTANTIATE THE LAYOUTINFLATER */
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrCheckIn.size();
    }

    @Override
    public String getItem(int position) {
        return arrCheckIn.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        /********** Inflate spinner_rows.xml file for each row ( Defined below ) ************/
        View row = inflater.inflate(R.layout.custom_spinner_row, parent, false);

        AppCompatTextView txtValue = (AppCompatTextView) row.findViewById(R.id.txtValue);
        txtValue.setTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/HelveticaNeueLTW1G-Cn.otf"));

        /** SET THE QUESTION NAME **/
        String strPropertyName = arrCheckIn.get(position);
        if (strPropertyName != null)	{
            txtValue.setText(strPropertyName);
        }

        return row;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        /********** Inflate spinner_rows.xml file for each row ( Defined below ) ************/
        View row = inflater.inflate(R.layout.custom_spinner_row, parent, false);

        AppCompatTextView txtValue = (AppCompatTextView) row.findViewById(R.id.txtValue);
        txtValue.setTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/HelveticaNeueLTW1G-Cn.otf"));

        /** SET THE QUESTION NAME **/
        String strPropertyName = arrCheckIn.get(position);
        if (strPropertyName != null)	{
            txtValue.setText(strPropertyName);
        }

        return row;
    }
}