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
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dzinesunlimited.resto.R;
import com.dzinesunlimited.resto.backend.creators.TaxCreator;
import com.dzinesunlimited.resto.backend.modifiers.TaxModifier;
import com.dzinesunlimited.resto.utils.TypefaceSpan;
import com.dzinesunlimited.resto.utils.db.DBResto;
import com.dzinesunlimited.resto.utils.helpers.pojos.backend.TaxesData;

import java.util.ArrayList;

public class Taxes extends Fragment {

    /** GLOBAL VIEW INSTANCE **/
    private View view;

    /** THE DATABASE HELPER INSTANCE **/
    private DBResto db;

    /** DECLARE THE LAYOUT ELEMENTS **/
    /* THE PROGRESSBAR */
    private LinearLayout linlaHeaderProgress;
    private RecyclerView listTaxes;
    private LinearLayout linlaEmpty;

    /** THE ADAPTER  **/
    private TaxesAdminAdapter adapter;

    /** THE ARRAYLIST **/
    private ArrayList<TaxesData> arrTaxes = new ArrayList<>();

    /** NEW TAXES CREATOR REQUEST CODE **/
    private static final int ACTION_REQUEST_NEW_TAX = 100;
    private static final int ACTION_REQUEST_EDIT_TAX = 101;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /***** CAST THE LAYOUT TO A NEW VIEW INSTANCE *****/
        view = inflater.inflate(R.layout.be_admin_taxes, container, false);

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

        /** CLEAR THE ARRAY LIST **/
        arrTaxes.clear();

        /** RUN THE TASK TO FETCH ALL THE TAXES FROM THE DATABASE **/
        new fetchTaxes().execute();

        /***** INSTANTIATE THE ADAPTER *****/
        adapter = new TaxesAdminAdapter(getActivity(), arrTaxes);
    }

    /** TASK TO FETCH ALL THE TAXES FROM THE DATABASE **/
    private class fetchTaxes extends AsyncTask<Void, Void, Void> {

        /** A CURSOR INSTANCE **/
        Cursor cursor;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /** INSTANTIATE THE DATABASE HELPER CLASS **/
            db = new DBResto(getActivity());

            /** SHOW THE PROGRESSBAR WHILE FETCHING THE LIST OF TAXES **/
            linlaHeaderProgress.setVisibility(View.VISIBLE);

            /** CONSTRUCT A QUERY TO FETCH TAXES ON RECORD **/
            String strQueryData = "SELECT * FROM " + db.TAXES;

            /** CAST THE QUERY IN THE CURSOR TO FETCH THE RESULTS **/
            cursor = db.selectAllData(strQueryData);
        }

        @Override
        protected Void doInBackground(Void... params) {

            /** CHECK THAT THE DATABASE QUERY RETURNED SOME RESULTS **/
            if (cursor != null && cursor.getCount() != 0)	{

                /** SHOW THE LIST OF TAXES AND HIDE THE EMPTY CONTAINER **/
                Runnable run = new Runnable() {

                    @Override
                    public void run() {

                        /** SHOW THE LIST OF TAXES **/
                        listTaxes.setVisibility(View.VISIBLE);

                        /** HIDE THE EMPTY DATA SET IMAGE AND TEXT **/
                        linlaEmpty.setVisibility(View.GONE);
                    }
                }; getActivity().runOnUiThread(run);

				/* AN INSTANCE OF THE TaxesData HELPER CLASS */
                TaxesData taxes;

                /** LOOP THROUGH THE RESULT SET AND PARSE NECESSARY INFORMATION **/
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                    /***** INSTANTIATE THE TaxesData INSTANCE "taxes" *****/
                    taxes = new TaxesData();

                    /** GET THE TAX ID **/
                    if (cursor.getString(cursor.getColumnIndex(db.TAX_ID)) != null)	{
                        String TAX_ID = cursor.getString(cursor.getColumnIndex(db.TAX_ID));
                        taxes.setTaxID(TAX_ID);
                    } else {
                        taxes.setTaxID(null);
                    }

                    /** GET THE TAX NAME **/
                    if (cursor.getString(cursor.getColumnIndex(db.TAX_NAME)) != null)	{
                        String TAX_NAME = cursor.getString(cursor.getColumnIndex(db.TAX_NAME));
                        taxes.setTaxName(TAX_NAME);
                    } else {
                        taxes.setTaxName(null);
                    }

                    /** GET THE TAX PERCENTAGE **/
                    if (cursor.getString(cursor.getColumnIndex(db.TAX_PERCENTAGE)) != null)	{
                        String TAX_PERCENTAGE = cursor.getString(cursor.getColumnIndex(db.TAX_PERCENTAGE));
                        taxes.setTaxPercentage(TAX_PERCENTAGE);
                    } else {
                        taxes.setTaxPercentage(null);
                    }

                    /** GET THE TAX REGISTRATION NUMBER **/
                    if (cursor.getString(cursor.getColumnIndex(db.TAX_REGISTRATION)) != null)	{
                        String TAX_REGISTRATION_NUMBER = cursor.getString(cursor.getColumnIndex(db.TAX_REGISTRATION));
                        taxes.setTaxRegistration(TAX_REGISTRATION_NUMBER);
                    } else {
                        taxes.setTaxRegistration(null);
                    }

                    /** GET THE TAX APPLICABLE ENTIRE AMOUNT STATUS **/
                    if (cursor.getString(cursor.getColumnIndex(db.TAX_ENTIRE_AMOUNT)) != null)	{
                        String TAX_APPLICABLE_ENTIRE_AMOUNT = cursor.getString(cursor.getColumnIndex(db.TAX_ENTIRE_AMOUNT));
                        taxes.setTaxCompleteAmount(TAX_APPLICABLE_ENTIRE_AMOUNT);
                    } else {
                        taxes.setTaxCompleteAmount(null);
                    }

                    /** GET THE TAX_AMOUNT_PERCENTAGE **/
                    if (cursor.getString(cursor.getColumnIndex(db.TAX_TAXABLE_PERCENTAGE)) != null)	{
                        String TAX_AMOUNT_PERCENTAGE = cursor.getString(cursor.getColumnIndex(db.TAX_TAXABLE_PERCENTAGE));
                        taxes.setTaxPercentageAmount(TAX_AMOUNT_PERCENTAGE);
                    } else {
                        taxes.setTaxPercentageAmount(null);
                    }

                    /** ADD THE COLLECTED DATA TO THE ARRAYLIST **/
                    arrTaxes.add(taxes);
                }
            } else {

                /** SHOW THE EMPTY CONTAINER AND HIDE THE LIST OF TAXES **/
                Runnable run = new Runnable() {

                    @Override
                    public void run() {

                        /** SHOW THE EMPTY DATA SET IMAGE AND TEXT **/
                        linlaEmpty.setVisibility(View.VISIBLE);

                        /** HIDE THE LIST OF TAXES **/
                        listTaxes.setVisibility(View.GONE);
                    }
                }; getActivity().runOnUiThread(run);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            /** CLOSE THE CURSOR **/
            if (cursor != null && !cursor.isClosed())	{
                cursor.close();
            }

            /** CLOSE THE DATABASE **/
            db.close();

            /** SET THE ADAPTER TO THE LISTVIEW **/
            listTaxes.setAdapter(adapter);

            /** HIDE THE PROGRESSBAR AFTER FETCHING THE LIST OF TAXES **/
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

                /** NEW INTENT TO ADD A NEW TAX **/
                Intent addNewTable = new Intent(getActivity(), TaxCreator.class);
                startActivityForResult(addNewTable, ACTION_REQUEST_NEW_TAX);

                break;

            default:
                break;
        }

        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != android.app.Activity.RESULT_CANCELED)	{

            /** INVALIDATE THE LISTVIEW **/
            listTaxes.invalidate();

            /** NOTIFY ADAPTER **/
            adapter.notifyDataSetChanged();

            /** CLEAR THE ARRAYLIST **/
            arrTaxes.clear();

            /** REFRESH THE LIST OF TABLES **/
            new fetchTaxes().execute();
        }
    }

    /***** CAST THE LAYOUT ELEMENTS *****/
    private void castLayoutElements() {

        /* THE PROGRESSBAR */
        linlaHeaderProgress = (LinearLayout) view.findViewById(R.id.linlaHeaderProgress);
        listTaxes = (RecyclerView) view.findViewById(R.id.listTaxes);
        listTaxes.setHasFixedSize(false);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        listTaxes.setLayoutManager(llm);
        linlaEmpty = (LinearLayout) view.findViewById(R.id.linlaEmpty);
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {

        String strTitle = getResources().getString(R.string.taxes_manager_title);
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(
                getActivity(), "RobotoCondensed-Regular.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(s);
    }

    /***** THE CUSTOM RECYCLERVIEW (LISTVIEW) ADAPTER *****/
    private class TaxesAdminAdapter extends RecyclerView.Adapter<TaxesAdminAdapter.TaxesVH> {

        /* THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER */
        private final Activity activity;

        /* ARRAYLIST TO GET DATA FROM THE ACTIVITY */
        private ArrayList<TaxesData> arrAdapTaxes;

        public TaxesAdminAdapter(Activity activity, ArrayList<TaxesData> arrAdapTaxes) {

            /** CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE **/
            this.activity = activity;

            /** CAST THE CONTENTS OF THE ARRAYLIST IN THE METHOD TO THE LOCAL INSTANCE **/
            this.arrAdapTaxes = arrAdapTaxes;

        }

        @Override
        public int getItemCount() {
            return arrAdapTaxes.size();
        }

        @Override
        public void onBindViewHolder(TaxesAdminAdapter.TaxesVH holder, int position) {
            final TaxesData items = arrAdapTaxes.get(position);

            /** GET AND SET THE TAX NAME **/
            String strTaxName = items.getTaxName();
            if (strTaxName != null)	{
                holder.txtName.setText(strTaxName);
            }

            /** GET AND SET THE TAX PERCENTAGE **/
            String strTaxPercentage = items.getTaxPercentage();
            if (strTaxPercentage != null)	{
                holder.txtPercentage.setText(strTaxPercentage + "%");
            }

            /** GET AND SET THE TAX REGISTRATION NUMBER **/
            String strTaxNumber = items.getTaxRegistration();
            if (strTaxNumber != null)	{
                holder.txtNumber.setText(strTaxNumber);
            }

            /** GET THE STATUS OF TAX APPLICABLE ON AMOUNT **/
            boolean blnAppliesOn = Boolean.valueOf(items.getTaxCompleteAmount());
            if (blnAppliesOn)	{
                /* SET THE TAX APPLIES ON ENTIRE BILL AMOUNT */
                holder.txtAppliesOn.setText("This Tax " + "(\"" + strTaxName + "\")" + " applies on the entire Bill Amount");
            } else {

                /* GET THE PERCENTAGE OF AMOUNT */
                String strPercentageAmount = items.getTaxPercentageAmount();
                if (strPercentageAmount != null)	{

                    /* SET THE TAX APPLIES ON PERCENTAGE OF THE AMOUNT */
                    holder.txtAppliesOn.setText("This Tax " + "(\"" + strTaxName + "\")" +" applies on " + strPercentageAmount + "%" + " of the Bill Amount");
                }
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
                                    Intent editTable = new Intent(getActivity(), TaxModifier.class);
                                    editTable.putExtra("TAX_ID", items.getTaxID());
                                    startActivityForResult(editTable, ACTION_REQUEST_EDIT_TAX);

                                    break;

                                case R.id.menuDelete:

                                    /** DELETE THE TABLE **/
                                    final String TAX_ID = items.getTaxID();
                                    String strTitle = "Delete \"" + items.getTaxName() + "\"";
                                    String strMessage = "Are you sure you want to delete this Tax? Pressing \"Yes\" will confirm the deletion and will be <b>permanent</b>.";
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
                                            db.deleteTax(TAX_ID);

                                            /* INVALIDATE THE TABLES LISTVIEW (RECYCLERVIEW) */
                                            listTaxes.invalidate();

                                            /* NOTIFY THE ADAPTER */
                                            adapter.notifyDataSetChanged();

                                            /* CLEAR THE ARRAYLIST */
                                            arrTaxes.clear();

                                            /** REFRESH THE LIST OF TAXES **/
                                            new fetchTaxes().execute();
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
        public TaxesVH onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.be_admin_taxes_item, parent, false);

            return new TaxesVH(itemView);
        }

        public class TaxesVH extends RecyclerView.ViewHolder {

            /** POPUP OPTIONS **/
            ImageView imgvwMenuOptions;

            AppCompatTextView txtNameLabel;
            AppCompatTextView txtName;
            AppCompatTextView txtPercentageLabel;
            AppCompatTextView txtPercentage;
            AppCompatTextView txtNumberLabel;
            AppCompatTextView txtNumber;
            AppCompatTextView txtAppliesOn;

            public TaxesVH(View vi) {
                super(vi);

                /*****	CAST THE LAYOUT ELEMENTS	*****/
                imgvwMenuOptions = (ImageView) vi.findViewById(R.id.imgvwMenuOptions);
                txtNameLabel = (AppCompatTextView) vi.findViewById(R.id.txtNameLabel);
                txtName = (AppCompatTextView) vi.findViewById(R.id.txtName);
                txtPercentageLabel = (AppCompatTextView) vi.findViewById(R.id.txtPercentageLabel);
                txtPercentage = (AppCompatTextView) vi.findViewById(R.id.txtPercentage);
                txtNumberLabel = (AppCompatTextView) vi.findViewById(R.id.txtNumberLabel);
                txtNumber = (AppCompatTextView) vi.findViewById(R.id.txtNumber);
                txtAppliesOn = (AppCompatTextView) vi.findViewById(R.id.txtAppliesOn);
            }
        }
    }
}