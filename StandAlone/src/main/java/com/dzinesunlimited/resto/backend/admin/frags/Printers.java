package com.dzinesunlimited.resto.backend.admin.frags;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.dzinesunlimited.resto.R;
import com.dzinesunlimited.resto.utils.TypefaceSpan;
import com.dzinesunlimited.resto.utils.db.DBResto;
import com.dzinesunlimited.resto.utils.helpers.pojos.PrinterData;

import java.util.ArrayList;

public class Printers extends Fragment {

    /** GLOBAL VIEW INSTANCE **/
    private View view;

    /** THE DATABASE HELPER INSTANCE **/
    private DBResto db;
    
    /** DECLARE THE LAYOUT ELEMENTS **/
    LinearLayout linlaHeaderProgress;
    RecyclerView listPrinters;
    LinearLayout linlaEmpty;
    
    /** THE ADAPTER AND THE ARRAYLIST FOR THE LIST OF PRINTERS **/
    PrintersAdapter adapter;
    ArrayList<PrinterData> arrPrinters = new ArrayList<>();
    
    /** STATIC DECLARATION FOR ADDING A NEW PRINTER **/
    private static final int ACTION_REQUEST_NEW_PRINTER = 101;
    private static final int ACTION_REQUEST_MODIFY_PRINT_CATEGORIES = 102;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /** CAST THE LAYOUT TO A NEW VIEW INSTANCE **/
        view = inflater.inflate(R.layout.be_admin_printers, container, false);

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
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /***** CAST THE LAYOUT ELEMENTS *****/
        castLayoutElements();

        /***** CONFIGURE THE ACTIONBAR *****/
        configAB();

        /** INSTANTIATE THE ADAPTER **/
        adapter = new PrintersAdapter(getActivity(), arrPrinters);

        /** FETCH THE PRINTERS FROM THE DATABASE **/
        new fetchPrinters().execute();
    }

    /** TASK TO FETCH THE LIST OF PRINTERS FROM THE DATABASE **/
    private class fetchPrinters extends AsyncTask<Void, Void, Void> {

        /** A CURSOR INSTANCE **/
        Cursor cursor;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /** SHOW THE PROGRESS WHILE LOADING THE LIST OF PRINTERS **/
            linlaHeaderProgress.setVisibility(View.VISIBLE);

            /** CLEAR THE ARRAYLIST **/
            arrPrinters.clear();

            /** INSTANTIATE THE DATABASE INSTANCE **/
            db = new DBResto(getActivity());

            /** BUILD THE QUERY **/
            String s = "SELECT * FROM " + db.PRINTERS;

            /** CAST THE RESULT IN THE CURSOR INSTANCE **/
            cursor = db.selectAllData(s);
        }

        @Override
        protected Void doInBackground(Void... params) {

            /** CHECK THAT THE DATABASE QUERY RETURNED SOME RESULTS **/
            if (cursor != null && cursor.getCount() != 0)	{

				/* AN INSTANCE OF THE PrinterData POJO CLASS */
                PrinterData data;

                /** LOOP THROUGH THE RESULT SET AND PARSE NECESSARY INFORMATION **/
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                    /***** INSTANTIATE THE PrinterData INSTANCE "data" *****/
                    data = new PrinterData();

                    /** GET THE PRINTER_ID **/
                    if (cursor.getString(cursor.getColumnIndex(db.PRINTER_ID)) != null)	{
                        String PRINTER_ID = cursor.getString(cursor.getColumnIndex(db.PRINTER_ID));
                        data.setPrinterID(PRINTER_ID);
                    } else {
                        data.setPrinterID(null);
                    }

                    /** GET THE PRINTER_NAME **/
                    if (cursor.getString(cursor.getColumnIndex(db.PRINTER_NAME)) != null)	{
                        String PRINTER_NAME = cursor.getString(cursor.getColumnIndex(db.PRINTER_NAME));
                        data.setPrinterName(PRINTER_NAME);
                    } else {
                        data.setPrinterName(null);
                    }

                    /** GET THE PRINTER_IP **/
                    if (cursor.getString(cursor.getColumnIndex(db.PRINTER_IP)) != null)	{
                        String PRINTER_IP = cursor.getString(cursor.getColumnIndex(db.PRINTER_IP));
                        data.setPrinterIP(PRINTER_IP);
                    } else {
                        data.setPrinterIP(null);
                    }

                    /** GET THE PRINTER_SELECTED_NAME **/
                    if (cursor.getString(cursor.getColumnIndex(db.PRINTER_SELECTED_NAME)) != null)	{
                        String PRINTER_SELECTED_NAME = cursor.getString(cursor.getColumnIndex(db.PRINTER_SELECTED_NAME));
                        data.setPrinterSelectedName(PRINTER_SELECTED_NAME);
                    } else {
                        data.setPrinterSelectedName(null);
                    }

                    /** ADD THE COLLECTED DATA TO THE ARRAYLIST **/
                    arrPrinters.add(data);
                }

                /** SHOW THE RECYCLERVIEW  AND HIDE THE EMPTY CONTAINER **/
                Runnable run = new Runnable() {

                    @Override
                    public void run() {

                        /** SHOW THE RECYCLERVIEW **/
                        listPrinters.setVisibility(View.VISIBLE);

                        /** HIDE THE EMPTY DATA SET IMAGE AND TEXT **/
                        linlaEmpty.setVisibility(View.GONE);
                    }
                }; getActivity().runOnUiThread(run);
            } else {

                /** SHOW THE EMPTY CONTAINER AND HIDE THE RECYCLERVIEW **/
                Runnable run = new Runnable() {

                    @Override
                    public void run() {

                        /** HIDE THE RECYCLERVIEW **/
                        listPrinters.setVisibility(View.GONE);

                        /** SHOW THE EMPTY DATA SET IMAGE AND TEXT **/
                        linlaEmpty.setVisibility(View.VISIBLE);
                    }
                }; getActivity().runOnUiThread(run);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            /** CLOSE THE CURSOR **/
            if (cursor != null && !cursor.isClosed())	{
                cursor.close();
            }

            /** CLOSE THE DATABASE **/
            db.close();

            /** SET THE ADAPTER TO THE RECYCLERVIEW **/
            listPrinters.setAdapter(adapter);

            /** HIDE THE PROGRESS AFTER LOADING THE LIST OF PRINTERS **/
            linlaHeaderProgress.setVisibility(View.GONE);
        }
    }

    /***** CAST THE LAYOUT ELEMENTS *****/
    private void castLayoutElements() {
        linlaHeaderProgress = (LinearLayout) view.findViewById(R.id.linlaHeaderProgress);
        listPrinters = (RecyclerView) view.findViewById(R.id.listPrinters);
        linlaEmpty = (LinearLayout) view.findViewById(R.id.linlaEmpty);
        
        /* CONFIGURE THE RECYCLERVIEW */
        listPrinters.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        listPrinters.setLayoutManager(llm);
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {

        String strTitle = getResources().getString(R.string.printer_manager_title);
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(
                getActivity(), "RobotoCondensed-Regular.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(s);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.admin_printer_manager, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.newPrinter:

                /***** ADD A NEW PRINTER *****/
                Intent newPrinter = new Intent(getActivity(), PrinterDiscovery.class);
                startActivityForResult(newPrinter, ACTION_REQUEST_NEW_PRINTER);

                break;

            default:
                break;
        }

        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != android.app.Activity.RESULT_CANCELED)	{

            /** CLEAR THE ARRAYLIST **/
            arrPrinters.clear();

            /** FETCH THE PRINTERS FROM THE DATABASE **/
            new fetchPrinters().execute();

            /** RE-INSTANTIATE THE ADAPTER **/
            adapter = new PrintersAdapter(getActivity(), arrPrinters);
        }
    }

    /***** THE USER ACCOUNTS CUSTOM ADAPTER *****/
    private class PrintersAdapter extends RecyclerView.Adapter<PrintersAdapter.PrintersVH> {

        /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
        Activity activity;

        /***** ARRAYLIST TO GET DATA FROM THE ACTIVITY *****/
        ArrayList<PrinterData> arrAdapPrinters;

        public PrintersAdapter(Activity activity, ArrayList<PrinterData> arrAdapPrinters) {

            /** CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE **/
            this.activity = activity;

            /** CAST THE CONTENTS OF THE ARRAYLIST IN THE METHOD TO THE LOCAL INSTANCE **/
            this.arrAdapPrinters = arrAdapPrinters;
        }

        @Override
        public int getItemCount() {
            return arrAdapPrinters.size();
        }

        @Override
        public void onBindViewHolder(PrintersVH holder, final int position) {
            final PrinterData data = arrAdapPrinters.get(position);

            /** SET THE PRINTER NAME **/
            holder.txtPrinterName.setText(data.getPrinterSelectedName());

            /** SET THE PRINTER IP ADDRESS **/
            holder.txtPrinterIP.setText(data.getPrinterIP());

            /** SHOW THE POPUP MENU **/
            holder.imgvwMenuOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PopupMenu pm = new PopupMenu(activity, v);
                    pm.getMenuInflater().inflate(R.menu.pm_printers, pm.getMenu());
                    pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
//                            Toast.makeText(getActivity(), String.valueOf(item.getTitle()), Toast.LENGTH_SHORT).show();

                            switch (item.getItemId())   {
                                case R.id.menuDelete:

                                    /** DELETE THE PRINTER **/
                                    final String PRINTER_ID = data.getPrinterID();
                                    String PRINTER_NAME = data.getPrinterName();

                                        String strTitle = "DELETE \"" + PRINTER_NAME.toUpperCase() + "\"?";
                                        String strMessage = getResources().getString(R.string.printer_delete_prompt);
                                        String strYes = getResources().getString(R.string.generic_mb_yes);
                                        String strNo = getResources().getString(R.string.generic_mb_no);

                                        /** CONFIGURE THE ALERTDIALOG **/
                                        new MaterialDialog.Builder(activity)
                                                .icon(ContextCompat.getDrawable(activity, R.drawable.ic_info_outline_black_24dp))
                                                .title(strTitle)
                                                .content(strMessage)
                                                .positiveText(strYes)
                                                .negativeText(strNo)
                                                .theme(Theme.LIGHT)
                                                .typeface("HelveticaNeueLTW1G-MdCn.otf", "HelveticaNeueLTW1G-Cn.otf")
                                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                    @Override
                                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                        db = new DBResto(activity);
                                                        db.deletePrinter(PRINTER_ID);

                                                        /* CLEAR THE ARRAYLIST */
                                                        arrPrinters.clear();

                                                        /** REFRESH THE PRINTERS LIST  **/
                                                        new fetchPrinters().execute();
                                                    }
                                                })
                                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                                    @Override
                                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                        dialog.dismiss();
                                                    }
                                                }).show();

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
        public PrintersVH onCreateViewHolder(ViewGroup parent, int i) {

            View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.be_admin_printers_item, parent, false);

            return new PrintersVH(itemView);
        }

        public class PrintersVH extends RecyclerView.ViewHolder	{
            AppCompatTextView txtPrinterName;
            AppCompatTextView txtPrinterIP;
            AppCompatImageView imgvwMenuOptions;

            public PrintersVH(View v) {
                super(v);

                /*****	CAST THE LAYOUT ELEMENTS	*****/
                txtPrinterName = (AppCompatTextView) v.findViewById(R.id.txtPrinterName);
                txtPrinterIP = (AppCompatTextView) v.findViewById(R.id.txtPrinterIP);
                imgvwMenuOptions = (AppCompatImageView) v.findViewById(R.id.imgvwMenuOptions);
            }
        }
    }
}