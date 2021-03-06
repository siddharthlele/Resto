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
import com.dzinesunlimited.resto.utils.helpers.pojos.PrinterData;

import java.util.ArrayList;

public class PrintersAdapter extends ArrayAdapter<PrinterData> {

    /** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER **/
    private Activity activity;

    /** LAYOUT INFLATER TO USE A CUSTOM LAYOUT **/
    private LayoutInflater inflater = null;

    /** ARRAYLIST TO GET DATA FROM THE ACTIVITY **/
    private ArrayList<PrinterData> arrPrinters;

    public PrintersAdapter(
            Activity activity,
            int resource,
            ArrayList<PrinterData> arrPrinters) {
        super(activity, resource);

        /** CAST THE ACTIVITY FROM THE METHOD TO THE LOCAL ACTIVITY INSTANCE **/
        this.activity = activity;

        /** CAST THE CONTENTS OF THE ARRAYLIST IN THE METHOD TO THE LOCAL INSTANCE **/
        this.arrPrinters = arrPrinters;

        /** INSTANTIATE THE LAYOUTINFLATER **/
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrPrinters.size();
    }

    @Override
    public PrinterData getItem(int position) {
        return arrPrinters.get(position);
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

        /* A VIEW HOLDER INSTANCE */
        ViewHolder holder;

        /* CAST THE CONVERT VIEW IN A VIEW INSTANCE */
        View vi = convertView;

        /** CHECK CONVERTVIEW STATUS **/
        if (convertView == null)	{
            /* CAST THE CONVERTVIEW INTO THE VIEW INSTANCE "vi"	*/
            vi = inflater.inflate(R.layout.custom_spinner_row, null);

            /* INSTANTIATE THE VIEW HOLDER INSTANCE */
            holder = new ViewHolder();

            /* CAST THE LAYOUT ELEMENTS */
            holder.txtValue = (AppCompatTextView) vi.findViewById(R.id.txtValue);

            /* SET THE TAG TO "vi" */
            vi.setTag(holder);
        } else {
            /* CAST THE VIEW HOLDER INSTANCE */
            holder = (ViewHolder) vi.getTag();
        }

        /** INSTANTIATE THE CUSTOM FONT AND SET **/
        Typeface typeface = Typeface.createFromAsset(activity.getAssets(), "fonts/HelveticaNeueLTW1G-Cn.otf");
        holder.txtValue.setTypeface(typeface);

        /** GET AND SET THE CATEGORY NAME **/
        String strPrinterName = arrPrinters.get(position).getPrinterSelectedName();
        if (strPrinterName != null)	{
            holder.txtValue.setText(strPrinterName);
        }

        return vi;
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
            vi = inflater.inflate(R.layout.custom_spinner_row, null);

            /* INSTANTIATE THE VIEW HOLDER INSTANCE */
            holder = new ViewHolder();

            /* CAST THE LAYOUT ELEMENTS */
            holder.txtValue = (AppCompatTextView) vi.findViewById(R.id.txtValue);

            /* SET THE TAG TO "vi" */
            vi.setTag(holder);
        } else {
            /* CAST THE VIEW HOLDER INSTANCE */
            holder = (ViewHolder) vi.getTag();
        }

        /** INSTANTIATE THE CUSTOM FONT AND SET **/
        Typeface typeface = Typeface.createFromAsset(activity.getAssets(), "fonts/HelveticaNeueLTW1G-Cn.otf");
        holder.txtValue.setTypeface(typeface);

        /** GET AND SET THE PRINTER NAME **/
            holder.txtValue.setText(arrPrinters.get(position).getPrinterSelectedName());

        return vi;
    }

    private class ViewHolder	{
        AppCompatTextView txtValue;
    }
}