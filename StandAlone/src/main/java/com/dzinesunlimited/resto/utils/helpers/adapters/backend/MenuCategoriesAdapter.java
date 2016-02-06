package com.dzinesunlimited.resto.utils.helpers.adapters.backend;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dzinesunlimited.resto.R;
import com.dzinesunlimited.resto.utils.helpers.pojos.MenuCategoryData;

import java.util.ArrayList;

public class MenuCategoriesAdapter extends ArrayAdapter<MenuCategoryData> {

    /** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER **/
    private Activity activity;

    /** LAYOUT INFLATER TO USE A CUSTOM LAYOUT **/
    private LayoutInflater inflater = null;

    /** ARRAYLIST TO GET DATA FROM THE ACTIVITY **/
    private ArrayList<MenuCategoryData> arrAdapCategory;

    public MenuCategoriesAdapter(
            Activity activity,
            int resource,
            ArrayList<MenuCategoryData> arrAdapCategory) {
        super(activity, resource);

        /** CAST THE ACTIVITY FROM THE METHOD TO THE LOCAL ACTIVITY INSTANCE **/
        this.activity = activity;

        /** CAST THE CONTENTS OF THE ARRAYLIST IN THE METHOD TO THE LOCAL INSTANCE **/
        this.arrAdapCategory = arrAdapCategory;

        /** INSTANTIATE THE LAYOUTINFLATER **/
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrAdapCategory.size();
    }

    @Override
    public MenuCategoryData getItem(int position) {
        return arrAdapCategory.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        /********** Inflate spinner_rows.xml file for each row ( Defined below ) ************/
        View row = inflater.inflate(R.layout.custom_spinner_row, parent, false);

        TextView txtCategoryCode = (TextView) row.findViewById(R.id.txtValue);

        /** SET THE MENU CATEGORY NAME **/
        String strCategoryName = arrAdapCategory.get(position).getCatName();
        if (strCategoryName != null)	{
            txtCategoryCode.setText(strCategoryName);
        }

        return row;
    }
}