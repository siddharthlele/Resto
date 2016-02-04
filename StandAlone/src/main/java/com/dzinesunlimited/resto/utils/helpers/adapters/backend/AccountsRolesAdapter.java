package com.dzinesunlimited.resto.utils.helpers.adapters.backend;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.dzinesunlimited.resto.R;
import com.dzinesunlimited.resto.utils.helpers.pojos.backend.AccountsRolesData;

import java.util.ArrayList;
public class AccountsRolesAdapter extends ArrayAdapter<AccountsRolesData> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    Activity activity;

    /***** LAYOUTINFLATER TO USE A CUSTOM LAYOUT *****/
    LayoutInflater inflater = null;

    /***** ARRAYLIST TO GET DATA FROM THE ACTIVITY *****/
    ArrayList<AccountsRolesData> arrAdapRoles;

    public AccountsRolesAdapter(
            Activity activity,
            int resource,
            ArrayList<AccountsRolesData> arrAdapRoles) {
        super(activity, resource);

		    /* CAST THE ACTIVITY FROM THE METHOD TO THE LOCAL ACTIVITY INSTANCE */
        this.activity = activity;

		    /* CAST THE CONTENTS OF THE ARRAYLIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrAdapRoles = arrAdapRoles;

		    /* INSTANTIATE THE LAYOUTINFLATER */
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrAdapRoles.size();
    }

    @Override
    public AccountsRolesData getItem(int position) {
        return arrAdapRoles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @SuppressWarnings("UnusedParameters")
    private View getCustomView(int position, View convertView, ViewGroup parent) {

        /********** Inflate spinner_rows.xml file for each row ( Defined below ) ************/
        View row = inflater.inflate(R.layout.custom_spinner_row, parent, false);

        AppCompatTextView txtCategoryCode = (AppCompatTextView) row.findViewById(R.id.txtValue);

        /** SET THE USER ROLE TEXT **/
        String strInvTypeName = arrAdapRoles.get(position).getRoleText();
        if (strInvTypeName != null)	{
            txtCategoryCode.setText(strInvTypeName);
        }

        return row;
    }
}