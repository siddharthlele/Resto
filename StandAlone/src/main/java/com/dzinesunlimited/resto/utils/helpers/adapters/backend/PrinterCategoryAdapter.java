package com.dzinesunlimited.resto.utils.helpers.adapters.backend;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;

import com.dzinesunlimited.resto.R;
import com.dzinesunlimited.resto.utils.db.DBResto;
import com.dzinesunlimited.resto.utils.helpers.pojos.backend.PrinterCategoryData;

import java.util.ArrayList;

public class PrinterCategoryAdapter extends BaseAdapter {

    /** A DATABASE INSTANCE **/
    DBResto db;

    /** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER **/
    Activity activity;

    /** LAYOUT INFLATER TO USE A CUSTOM LAYOUT **/
    LayoutInflater inflater = null;

    /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
    ArrayList<PrinterCategoryData> arrCategories;

    boolean[] checkBoxState;

    public PrinterCategoryAdapter(Activity activity, ArrayList<PrinterCategoryData> arrCategories) {

        /** CAST THE ACTIVITY FROM THE METHOD TO THE LOCAL ACTIVITY INSTANCE **/
        this.activity = activity;

        /** CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE **/
        this.arrCategories = arrCategories;

        /** INSTANTIATE THE LAYOUT INFLATER **/
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        checkBoxState = new boolean[arrCategories.size()];
    }

    @Override
    public int getCount() {
        return arrCategories.size();
    }

    @Override
    public Object getItem(int position) {
        return arrCategories.get(position);
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view;

        if (convertView == null)    {
            inflater = activity.getLayoutInflater();
            view = inflater.inflate(R.layout.custom_checkbox_item, null);
            final ViewHolder holder = new ViewHolder();

            /*****	CAST THE LAYOUT ELEMENTS	*****/
            holder.txtValue = (AppCompatTextView) view.findViewById(R.id.txtValue);
            holder.chkbxValue = (AppCompatCheckBox) view.findViewById(R.id.chkbxValue);

            view.setTag(holder);
            holder.chkbxValue.setTag(arrCategories.get(position));
        } else {
            view = convertView;
            ((ViewHolder)view.getTag()).chkbxValue.setTag(arrCategories.get(position));
        }

        final ViewHolder holder = (ViewHolder) view.getTag();
        holder.txtValue.setTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/HelveticaNeueLTW1G-Cn.otf"));

        /** SET THE CATEGORY NAME **/
        String strCatName = arrCategories.get(position).getCatName();
        holder.txtValue.setText(strCatName);

        /** SET THE CHECKED STATUS **/
        boolean blnStatus = arrCategories.get(position).isStatus();
        if (blnStatus)  {
            holder.chkbxValue.setChecked(true);
        } else {
            holder.chkbxValue.setChecked(false);
        }

        holder.chkbxValue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                PrinterCategoryData data = (PrinterCategoryData) holder.chkbxValue.getTag();
                data.setStatus(buttonView.isChecked());

                /** UPDATE THE CATEGORY TAX APPLICABLE STATUS **/
                db = new DBResto(activity);
                db.updatePrinterCategory(data.getPrintCatID(), isChecked);
                db.close();
            }
        });

        return view;
    }

    private static class ViewHolder	{
        AppCompatTextView txtValue;
        AppCompatCheckBox chkbxValue;
    }
}