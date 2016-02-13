package com.dzinesunlimited.resto.backend.admin.frags;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dzinesunlimited.resto.R;
import com.dzinesunlimited.resto.ShowMsg;
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
//    private ArrayList<HashMap<String, String>> mPrinterList = null;
//    private SimpleAdapter mPrinterListAdapter = null;
    private FilterOption mFilterOption = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.be_printer_discovery);
        ButterKnife.bind(this);

        /** CONFIGURE THE RECYCLERVIEW **/
        configureRecyclerView();

//        mPrinterList = new ArrayList<>();
//        mPrinterListAdapter = new SimpleAdapter(this, mPrinterList, R.layout.be_printer_discovery_item,
//                new String[] { "PrinterName", "Target" },
//                new int[] { R.id.PrinterName, R.id.Target });

        mFilterOption = new FilterOption();
        mFilterOption.setDeviceType(Discovery.TYPE_PRINTER);
        mFilterOption.setEpsonFilter(Discovery.FILTER_NAME);
        try {
            Discovery.start(this, mFilterOption, mDiscoveryListener);
        }
        catch (Exception e) {
            ShowMsg.showException(e, "start", PrinterDiscovery.this);
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
                    arrPrinter.clear();
                    PrinterData data = new PrinterData();
                    data.setPrinterName(deviceInfo.getDeviceName());
                    data.setPrinterIP(deviceInfo.getTarget());
//                    Log.e("DEVICE NAME", deviceInfo.getDeviceName());
//                    Log.e("TARGET", deviceInfo.getTarget());
//                    Log.e("IP ADDRESS", deviceInfo.getIpAddress());
//                    Log.e("MAC ADDRESS", deviceInfo.getMacAddress());

                    arrPrinter.add(data);
                    Log.e("NO OF PRINTERS", String.valueOf(arrPrinter.size()));
                    listPrinters.setAdapter(adapter);
                }
            });
        }
    };

    private void restartDiscovery() {
        while (true) {
            try {
                Discovery.stop();
                break;
            }
            catch (Epos2Exception e) {
                if (e.getErrorStatus() != Epos2Exception.ERR_PROCESSING) {
                    ShowMsg.showException(e, "stop", PrinterDiscovery.this);
                    return;
                }
            }
        }

        arrPrinter.clear();

        try {
            Discovery.start(this, mFilterOption, mDiscoveryListener);
        }
        catch (Exception e) {
            ShowMsg.showException(e, "stop", PrinterDiscovery.this);
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
            resto.close();
            Log.e("PRINTERS", DatabaseUtils.dumpCursorToString(cursor));
            if (cursor.getCount() != 0) {
                holder.vwStatus.setBackgroundResource(R.drawable.printer_added);
            } else {
                holder.vwStatus.setBackgroundResource(R.drawable.printer_not_added);
            }

            /** ADD THE PRINTER TO THE DATABASE **/
            holder.crdvwContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

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
                        ProgressDialog dialog = new ProgressDialog(activity);
                        dialog.setMessage("Please wait while the Printer is being registered.");
                        dialog.setIndeterminate(false);
                        dialog.setCancelable(false);
                        dialog.show();
                        DBResto dbResto = new DBResto(activity);
                        String printerName = data.getPrinterName();
                        String printerIP = data.getPrinterIP();
                        Log.e("PRINTER NAME", printerName);
                        Log.e("PRINTER IP", printerIP);
                        dbResto.addPrinter(printerName, printerIP);
                        dbResto.close();
                        dialog.dismiss();
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
            View vwStatus;

            public PrintersVH(View v) {
                super(v);

                /*****	CAST THE LAYOUT ELEMENTS	*****/
                crdvwContainer = (CardView) v.findViewById(R.id.crdvwContainer);
                txtPrinterName = (AppCompatTextView) v.findViewById(R.id.txtPrinterName);
                txtPrinterIP = (AppCompatTextView) v.findViewById(R.id.txtPrinterIP);
                vwStatus = v.findViewById(R.id.vwStatus);
            }
        }
    }
}