package com.dzinesunlimited.resto.backend.admin.frags;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.dzinesunlimited.resto.R;
import com.dzinesunlimited.resto.utils.db.DBResto;
import com.dzinesunlimited.resto.utils.helpers.pojos.PrinterData;
import com.epson.epos2.Epos2Exception;
import com.epson.epos2.discovery.DeviceInfo;
import com.epson.epos2.discovery.Discovery;
import com.epson.epos2.discovery.DiscoveryListener;
import com.epson.epos2.discovery.FilterOption;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PrinterDiscovery extends AppCompatActivity {

    /** DECLARE THE LAYOUT ELEMENTS **/
    @Bind(R.id.linlaHeaderProgress)LinearLayout linlaHeaderProgress;
    @Bind(R.id.listPrinters)RecyclerView listPrinters;
    @Bind(R.id.linlaEmpty)LinearLayout linlaEmpty;
    @OnClick(R.id.btnSearchPrinters) protected void SearchPrinters() {
        /** SEARCH FOR PRINTERS ON THE NETWORK AGAIN **/
        restartDiscovery();
    }

    private PrinterDiscoveryAdapter adapter;
    private ArrayList<PrinterData> arrPrinter = new ArrayList<>();
    private FilterOption mFilterOption = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.be_printer_discovery);
        ButterKnife.bind(this);

        /** CONFIGURE THE RECYCLERVIEW **/
        configureRecyclerView();

        mFilterOption = new FilterOption();
        mFilterOption.setDeviceType(Discovery.TYPE_PRINTER);
        mFilterOption.setEpsonFilter(Discovery.FILTER_NAME);
        try {
            Discovery.start(this, mFilterOption, mDiscoveryListener);
        }
        catch (Exception e) {

            MaterialDialog dialog = new MaterialDialog.Builder(PrinterDiscovery.this)
                    .title("PROBLEM WITH PRINTER")
                    .content(e.toString())
                    .positiveText("OKAY")
                    .theme(Theme.LIGHT)
                    .icon(ContextCompat.getDrawable(PrinterDiscovery.this, R.drawable.ic_info_outline_white_24dp))
                    .typeface("HelveticaNeueLTW1G-MdCn.otf", "HelveticaNeueLTW1G-Cn.otf")
                    .show();
//            ShowMsg.showException(e, "start", PrinterDiscovery.this);
        }

        /** INSTANTIATE THE ADAPTER **/
        adapter = new PrinterDiscoveryAdapter(PrinterDiscovery.this, arrPrinter);
    }

    private DiscoveryListener mDiscoveryListener = new DiscoveryListener() {
        @Override
        public void onDiscovery(final DeviceInfo deviceInfo) {
            runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
                    linlaHeaderProgress.setVisibility(View.VISIBLE);
                    arrPrinter.clear();
                    PrinterData data = new PrinterData();
                    data.setPrinterName(deviceInfo.getDeviceName());
                    data.setPrinterIP(deviceInfo.getTarget());
                    Log.e("DEVICE NAME", deviceInfo.getDeviceName());
                    Log.e("TARGET", deviceInfo.getTarget());
                    Log.e("IP ADDRESS", deviceInfo.getIpAddress());
                    Log.e("MAC ADDRESS", deviceInfo.getMacAddress());
                    Log.e("PRINTER", String.valueOf(deviceInfo.getDeviceType()));

                    arrPrinter.add(data);
                    Log.e("NO OF PRINTERS", String.valueOf(arrPrinter.size()));
                    listPrinters.setAdapter(adapter);
                    linlaHeaderProgress.setVisibility(View.GONE);
                }
            });
        }
    };

    /** SEARCH FOR PRINTERS AGAIN **/
    private void restartDiscovery() {
        while (true) {
            try {
                Discovery.stop();
                break;
            }
            catch (Epos2Exception e) {
                if (e.getErrorStatus() != Epos2Exception.ERR_PROCESSING) {

                    MaterialDialog dialog = new MaterialDialog.Builder(PrinterDiscovery.this)
                            .title("PROBLEM WITH PRINTER")
                            .content(e.toString())
                            .positiveText("OKAY")
                            .theme(Theme.LIGHT)
                            .icon(ContextCompat.getDrawable(PrinterDiscovery.this, R.drawable.ic_info_outline_white_24dp))
                            .typeface("HelveticaNeueLTW1G-MdCn.otf", "HelveticaNeueLTW1G-Cn.otf")
                            .show();
//                    ShowMsg.showException(e, "stop", PrinterDiscovery.this);
                    return;
                }
            }
        }

        arrPrinter.clear();

        try {
            Discovery.start(this, mFilterOption, mDiscoveryListener);
        }
        catch (Exception e) {

            MaterialDialog dialog = new MaterialDialog.Builder(PrinterDiscovery.this)
                    .title("PROBLEM WITH PRINTER")
                    .content(e.toString())
                    .positiveText("OKAY")
                    .theme(Theme.LIGHT)
                    .icon(ContextCompat.getDrawable(PrinterDiscovery.this, R.drawable.ic_info_outline_white_24dp))
                    .typeface("HelveticaNeueLTW1G-MdCn.otf", "HelveticaNeueLTW1G-Cn.otf")
                    .show();
//            ShowMsg.showException(e, "stop", PrinterDiscovery.this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        while (true) {
            try {
                Discovery.stop();
                break;
            }
            catch (Epos2Exception e) {
                if (e.getErrorStatus() != Epos2Exception.ERR_PROCESSING) {
                    break;
                }
            }
        }

        mFilterOption = null;
    }

    /** CONFIGURE THE RECYCLERVIEW **/
    private void configureRecyclerView() {
        listPrinters.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(PrinterDiscovery.this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        listPrinters.setLayoutManager(llm);
    }

    /***** THE USER ACCOUNTS CUSTOM ADAPTER *****/
    private class PrinterDiscoveryAdapter extends RecyclerView.Adapter<PrinterDiscoveryAdapter.PrintersVH> {

        /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
        Activity activity;

        /***** ARRAYLIST TO GET DATA FROM THE ACTIVITY *****/
        ArrayList<PrinterData> arrAdapPrinters;

        /** BOOLEAN TO TRACK IF A PRINTER EXISTS **/
        boolean blnPrinter = false;

        public PrinterDiscoveryAdapter(Activity activity, ArrayList<PrinterData> arrAdapPrinters) {

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
            holder.txtPrinterName.setText(data.getPrinterName());

            /** SET THE PRINTER IP ADDRESS **/
            holder.txtPrinterIP.setText(data.getPrinterIP());

            /** CHECK IF THE PRINTER EXISTS IN THE DATABASE **/
            DBResto resto = new DBResto(activity);
            String s = "SELECT * FROM " + resto.PRINTERS + " WHERE " + resto.PRINTER_IP + " = '" + data.getPrinterIP() + "'";
            Cursor cursor = resto.selectAllData(s);
//            Log.e("PRINTERS", DatabaseUtils.dumpCursorToString(cursor));
            if (cursor.getCount() != 0) {
                blnPrinter = true;
                holder.txtStatus.setText("PRINTER ADDED");
                holder.txtStatus.setTextColor(ContextCompat.getColor(activity, android.R.color.holo_red_light));
            } else {
                blnPrinter = false;
                holder.txtStatus.setText("NEW");
                holder.txtStatus.setTextColor(ContextCompat.getColor(activity, android.R.color.holo_green_light));
            }

            /** ADD THE PRINTER TO THE DATABASE **/
            holder.crdvwContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!blnPrinter)    {

                        /** TAKE THE PRINTER NAME VIA INPUT DIALOG **/
                        new MaterialDialog.Builder(activity)
                                .title("Name The Printer")
                                .theme(Theme.LIGHT)
                                .typeface("HelveticaNeueLTW1G-MdCn.otf", "HelveticaNeueLTW1G-Cn.otf")
                                .content(
                                        "Please provide a name to this Printer. " +
                                                "This will help identifying and managing the Printers installed at this location. " +
                                                "\n\nThis will also help while configuring Printers that will print the KOT\'s and the " +
                                                "Printers that will print the Bills / Receipts")
                                .inputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                                .input("Name the printer", null, new MaterialDialog.InputCallback() {
                                    @Override
                                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                        Toast.makeText(activity, input, Toast.LENGTH_SHORT).show();

                                        /** CHECK IF THE PRINTER EXISTS IN THE DATABASE **/
                                        DBResto resto = new DBResto(activity);
                                        String s =
                                                "SELECT * FROM " + resto.PRINTERS +
                                                        " WHERE " + resto.PRINTER_IP + " = '" + data.getPrinterIP() + "'";
                                        Cursor cursor = resto.selectAllData(s);
                                        if (cursor.getCount() != 0) {
                                            Toast.makeText(
                                                    activity,
                                                    "The selected Printer has already been added. Please choose a different printer",
                                                    Toast.LENGTH_LONG).show();
                                        } else {
                                            ProgressDialog progressDialog = new ProgressDialog(activity);
                                            progressDialog.setMessage("Please wait while the Printer is being registered and added to the database.");
                                            progressDialog.setIndeterminate(false);
                                            progressDialog.setCancelable(false);
                                            progressDialog.show();
                                            DBResto dbResto = new DBResto(activity);
                                            String printerName = data.getPrinterName();
                                            String printerIP = data.getPrinterIP();
                                            Log.e("PRINTER NAME", printerName);
                                            Log.e("PRINTER IP", printerIP);
                                            long printerID = dbResto.addPrinter(printerName, printerIP, String.valueOf(input));
                                            Log.e("NEW PRINTER ID", String.valueOf(printerID));
                                            progressDialog.dismiss();

                                            /***** SET THE RESULT TO "RESULT_OK" AND FINISH THE ACTIVITY *****/
                                            Intent printerAdded = new Intent();
                                            printerAdded.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            setResult(RESULT_OK, printerAdded);

                                            /** FINISH THE ACTIVITY **/
                                            finish();
                                        }
                                    }
                                }).show();
                    } else {
                        MaterialDialog dialog = new MaterialDialog.Builder(activity)
                                .title("Printer Exists!")
                                .content("This Printer has already been added to your list of Printers. Please select a Printer that reads \"New\" at the bottom right of the listing")
                                .positiveText("OKAY")
                                .theme(Theme.LIGHT)
                                .icon(ContextCompat.getDrawable(activity, R.drawable.ic_info_outline_black_24dp))
                                .typeface("HelveticaNeueLTW1G-MdCn.otf", "HelveticaNeueLTW1G-Cn.otf")
                                .show();
                    }
                }
            });
        }

        @Override
        public PrintersVH onCreateViewHolder(ViewGroup parent, int i) {

            View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.be_printer_discovery_item_new, parent, false);

            return new PrintersVH(itemView);
        }

        public class PrintersVH extends RecyclerView.ViewHolder	{
            CardView crdvwContainer;
            AppCompatTextView txtPrinterName;
            AppCompatTextView txtPrinterIP;
            AppCompatTextView txtStatus;

            public PrintersVH(View v) {
                super(v);

                /*****	CAST THE LAYOUT ELEMENTS	*****/
                crdvwContainer = (CardView) v.findViewById(R.id.crdvwContainer);
                txtPrinterName = (AppCompatTextView) v.findViewById(R.id.txtPrinterName);
                txtPrinterIP = (AppCompatTextView) v.findViewById(R.id.txtPrinterIP);
                txtStatus = (AppCompatTextView) v.findViewById(R.id.txtStatus);
            }
        }
    }
}