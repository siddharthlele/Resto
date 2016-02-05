package com.dzinesunlimited.resto.backend.admin.frags;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.dzinesunlimited.resto.R;
import com.dzinesunlimited.resto.backend.creators.AccountCreator;
import com.dzinesunlimited.resto.backend.details.AccountDetails;
import com.dzinesunlimited.resto.backend.modifiers.AccountModifier;
import com.dzinesunlimited.resto.utils.TypefaceSpan;
import com.dzinesunlimited.resto.utils.db.DBResto;
import com.dzinesunlimited.resto.utils.helpers.adapters.backend.AccountsRolesAdapter;
import com.dzinesunlimited.resto.utils.helpers.pojos.backend.AccountsData;
import com.dzinesunlimited.resto.utils.helpers.pojos.backend.AccountsRolesData;

import java.util.ArrayList;
import java.util.List;

public class Accounts extends Fragment {

    /** GLOBAL VIEW INSTANCE **/
    private View view;

    /** THE DATABASE HELPER INSTANCE **/
    private DBResto db;

    /***** DECLARE THE LAYOUT ELEMENTS *****/
    private AppCompatEditText edtSearchAccounts;
    private AppCompatSpinner spnUserRoles;
    private LinearLayout linlaHeaderProgress;
    private RecyclerView gridUsers;
    private LinearLayout linlaEmpty;

    /** ARRAYLIST FOR THE SPINNER (USER ROLES) **/
    private ArrayList<AccountsRolesData> arrRoles = new ArrayList<>();

    /** STRING TO HOLD THE SELECTED USER ROLE **/
    private String SELECTED_USER_ROLE;

    /** ADAPTER AND ARRAYLIST FOR THE GRIDVIEW (USERS) **/
    private AccountsUsersAdapter adapUsers;
    private ArrayList<AccountsData> arrUsers = new ArrayList<>();

    /** NEW ACCOUNT REQUEST CODE **/
    private static final int ACTION_REQUEST_NEW_ACCOUNT = 100;
    private static final int ACTION_REQUEST_EDIT_ACCOUNT = 101;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /** CAST THE LAYOUT TO A NEW VIEW INSTANCE **/
        view = inflater.inflate(R.layout.be_admin_accounts, container, false);

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

        /** FETCH ALL USER ROLES **/
        new fetchAllUserRoles().execute();

        /** CHANGE THE USER ROLES AND SHOW IT'S USERS **/
        spnUserRoles.setOnItemSelectedListener(selectRole);
    }

    private AdapterView.OnItemSelectedListener selectRole = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parentView, View v, int position, long id) {
            SELECTED_USER_ROLE = arrRoles.get(position).getRoleID();

            /** INSTANTIATE THE ADAPTER **/
            adapUsers = new AccountsUsersAdapter(getActivity(), arrUsers);

            /** CLEAR THE CURRENT LISTVIEW AND THE ARRAYADAPTER **/
            gridUsers.invalidate();
            adapUsers.notifyDataSetChanged();
            arrUsers.clear();

            /** FETCH THE USERS FROM THE SELECTED USER ROLES **/
            new fetchUsers().execute();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parentView) {

        }
    };

    /***** FETCH THE USERS / ACCOUNTS FROM THE SLECETD USER ROLES *****/
    private class fetchUsers extends AsyncTask<Void, Void, Void> {

        /** A CURSOR INSTANCE **/
        Cursor cursor;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /** INSTANTIATE THE DATABASE HELPER CLASS **/
            db = new DBResto(getActivity());

            /** CONSTRUCT THE QUERY TO FETCH ALL USER FROM THE SELECTED USER ROLE **/
            String strQueryData = "SELECT * FROM " + db.STAFF + " WHERE " + db.STAFF_ROLE_ID + " = " + SELECTED_USER_ROLE;

            /** CAST THE QUERY IN THE CURSOR TO FETCH THE RESULTS **/
            cursor = db.selectAllData(strQueryData);
        }

        @Override
        protected Void doInBackground(Void... params) {

            /** CHECK THAT THE DATABASE QUERY RETURNED SOME RESULTS **/
            if (cursor != null && cursor.getCount() != 0)	{

                /** SHOW THE LISTVIEW AND HIDE THE EMPTY CONTAINER **/
                Runnable run = new Runnable() {

                    @Override
                    public void run() {

                        /** SHOW THE GRIDVIEW **/
                        gridUsers.setVisibility(View.VISIBLE);

                        /** HIDE THE EMPTY DATA SET IMAGE AND TEXT **/
                        linlaEmpty.setVisibility(View.GONE);
                    }
                }; getActivity().runOnUiThread(run);

				/* AN INSTANCE OF THE AccountsData POJO CLASS */
                AccountsData accUsers;

                /** LOOP THROUGH THE RESULT SET AND PARSE NECESSARY INFORMATION **/
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                    /***** INSTANTIATE THE AccountsData INSTANCE "accUsers" *****/
                    accUsers = new AccountsData();

                    /** GET THE STAFF_ROLE_ID **/
                    if (cursor.getString(cursor.getColumnIndex(db.STAFF_ROLE_ID)) != null)	{
                        String STAFF_ROLE_ID = cursor.getString(cursor.getColumnIndex(db.STAFF_ROLE_ID));
                        accUsers.setStaffRoleID(STAFF_ROLE_ID);
                    } else {
                        accUsers.setStaffRoleID(null);
                    }

                    /** GET THE STAFF_ID **/
                    if (cursor.getString(cursor.getColumnIndex(db.STAFF_ID)) != null)	{
                        String STAFF_ID = cursor.getString(cursor.getColumnIndex(db.STAFF_ID));
                        accUsers.setStaffID(STAFF_ID);
                    } else {
                        accUsers.setStaffID(null);
                    }

                    /** GET THE STAFF_FULL_NAME **/
                    if (cursor.getString(cursor.getColumnIndex(db.STAFF_FULL_NAME)) != null)	{
                        String STAFF_FULL_NAME = cursor.getString(cursor.getColumnIndex(db.STAFF_FULL_NAME));
                        accUsers.setStaffFullName(STAFF_FULL_NAME);
                    } else {
                        accUsers.setStaffFullName(null);
                    }

                    /** GET THE STAFF_PROFILE_PICTURE **/
                    if (cursor.getBlob(cursor.getColumnIndex(db.STAFF_PROFILE_PICTURE)) != null)	{
                        byte[] STAFF_PROFILE_PICTURE = cursor.getBlob(cursor.getColumnIndex(db.STAFF_PROFILE_PICTURE));
                        accUsers.setStaffProfilePicture(STAFF_PROFILE_PICTURE);
                    } else {
                        accUsers.setStaffProfilePicture(null);
                    }

                    /** GET THE STAFF USERNAME **/
                    if (cursor.getString(cursor.getColumnIndex(db.STAFF_USER_NAME)) != null)    {
                        String STAFF_USER_NAME = cursor.getString(cursor.getColumnIndex(db.STAFF_USER_NAME));
                        accUsers.setStaffUserName(STAFF_USER_NAME);
                    } else {
                        accUsers.setStaffUserName(null);
                    }

                    /** ADD THE COLLECTED DATA TO THE ARRAYLIST **/
                    arrUsers.add(accUsers);
                }

            } else {

                /** SHOW THE EMPTY CONTAINER AND HIDE THE LISTVIEW **/
                Runnable run = new Runnable() {

                    @Override
                    public void run() {

                        /** SHOW THE EMPTY DATA SET IMAGE AND TEXT **/
                        linlaEmpty.setVisibility(View.VISIBLE);

                        /** HIDE THE LISTVIEW **/
                        gridUsers.setVisibility(View.GONE);
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

            /** SET THE ADAPTER TO THE GRIDVIEW **/
            gridUsers.setAdapter(adapUsers);

            /** HIDE THE PROGRESS AFTER FETCHING THE DATA **/
            linlaHeaderProgress.setVisibility(View.GONE);
        }
    }

    /** FETCH THE DEFAULT USER ROLES **/
    private class fetchAllUserRoles extends AsyncTask<Void, Void, Void> {

        /** A CURSOR INSTANCE **/
        Cursor cursor;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /** CLEAR THE ARRAY LIST **/
            arrRoles.clear();

            /** SHOW THE PROGRESS WHILE FETCHING THE DATA **/
            linlaHeaderProgress.setVisibility(View.VISIBLE);

            /** INSTANTIATE THE DATABASE HELPER CLASS **/
            db = new DBResto(getActivity());

            /** CONSTRUCT THE QUERY TO FETCH ALL TWEETS FROM THE DATABASE **/
            String strQueryData = "SELECT * FROM " + db.STAFF_ROLES;

            /** CAST THE QUERY IN THE CURSOR TO FETCH THE RESULTS **/
            cursor = db.selectAllData(strQueryData);
//			Log.e("ROLES", DatabaseUtils.dumpCursorToString(cursor));
        }

        @Override
        protected Void doInBackground(Void... params) {

            /** CHECK THAT THE DATABASE QUERY RETURNED SOME RESULTS **/
            if (cursor != null && cursor.getCount() != 0)	{

				/* AN INSTANCE OF THE AccountsUserRolesData POJO CLASS */
                AccountsRolesData accRoles;

                /** LOOP THROUGH THE RESULT SET AND PARSE NECESSARY INFORMATION **/
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                    /***** INSTANTIATE THE AccountsUserRolesData INSTANCE "accRoles" *****/
                    accRoles = new AccountsRolesData();

                    /** GET THE ROLE_ID **/
                    if (cursor.getString(cursor.getColumnIndex(db.ROLE_ID)) != null)	{
                        String ROLE_ID = cursor.getString(cursor.getColumnIndex(db.ROLE_ID));
                        accRoles.setRoleID(ROLE_ID);
                    } else {
                        accRoles.setRoleID(null);
                    }

                    /** GET THE ROLE_CODE **/
                    if (cursor.getString(cursor.getColumnIndex(db.ROLE_CODE)) != null)	{
                        String ROLE_CODE = cursor.getString(cursor.getColumnIndex(db.ROLE_CODE));
                        accRoles.setRoleCode(ROLE_CODE);
                    } else {
                        accRoles.setRoleCode(null);
                    }

                    /** GET THE ROLE_TEXT **/
                    if (cursor.getString(cursor.getColumnIndex(db.ROLE_TEXT)) != null)	{
                        String ROLE_TEXT = cursor.getString(cursor.getColumnIndex(db.ROLE_TEXT));
                        accRoles.setRoleText(ROLE_TEXT);
                    } else {
                        accRoles.setRoleText(null);
                    }

                    /** GET THE ROLE_DESCRIPTION **/
                    if (cursor.getString(cursor.getColumnIndex(db.ROLE_DESCRIPTION)) != null)	{
                        String ROLE_DESCRIPTION = cursor.getString(cursor.getColumnIndex(db.ROLE_DESCRIPTION));
                        accRoles.setRoleDescription(ROLE_DESCRIPTION);
                    } else {
                        accRoles.setRoleDescription(null);
                    }

                    /** ADD THE COLLECTED DATA TO THE ARRAYLIST **/
                    arrRoles.add(accRoles);
                }

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

            /** SET THE ADAPTER TO THE GRIDVIEW **/
            spnUserRoles.setAdapter(new AccountsRolesAdapter(
                    getActivity(),
                    R.layout.custom_spinner_row,
                    arrRoles));

            /** HIDE THE PROGRESS AFTER FETCHING THE DATA **/
            linlaHeaderProgress.setVisibility(View.GONE);
        }
    }

    /***** CAST THE LAYOUT ELEMENTS *****/
    private void castLayoutElements() {

        /** WIDGETS **/
        edtSearchAccounts = (AppCompatEditText) view.findViewById(R.id.edtSearchAccounts);
        spnUserRoles = (AppCompatSpinner) view.findViewById(R.id.spnUserRoles);
        linlaHeaderProgress = (LinearLayout) view.findViewById(R.id.linlaHeaderProgress);
        gridUsers = (RecyclerView) view.findViewById(R.id.gridUsers);
        linlaEmpty = (LinearLayout) view.findViewById(R.id.linlaEmpty);

        /** CONFIGURE THE RECYCLER VIEW **/
        int intOrientation = getActivity().getResources().getConfiguration().orientation;
        gridUsers.setHasFixedSize(true);
        GridLayoutManager glm = null;
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isTablet)   {
            if (intOrientation == 1)	{
                glm = new GridLayoutManager(getActivity(), 2);
            } else if (intOrientation == 2) {
                glm = new GridLayoutManager(getActivity(), 3);
            }
        } else {
            if (intOrientation == 1)    {
                glm = new GridLayoutManager(getActivity(), 1);
            } else if (intOrientation == 2) {
                glm = new GridLayoutManager(getActivity(), 2);
            }
        }
        gridUsers.setLayoutManager(glm);

        /** SEARCH FOR AN ACCOUNT **/
        edtSearchAccounts.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapUsers.getFilter().filter(s.toString().toLowerCase());
                adapUsers.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {

        String strTitle = getResources().getString(R.string.account_manager_title);
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
        inflater.inflate(R.menu.admin_account_manager, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.newAccount:

                /***** CREATE A NEW USER / ACCOUNT *****/
                Intent newAccount = new Intent(getActivity(), AccountCreator.class);
                startActivityForResult(newAccount, ACTION_REQUEST_NEW_ACCOUNT);

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
            arrUsers.clear();

            /** RESET THE SPINNER **/
            spnUserRoles.setOnItemSelectedListener(selectRole);

            /** FETCH THE USERS FROM THE SELECTED USER ROLES **/
            new fetchUsers().execute();

            /** RE-INSTANTIATE THE ADAPTER **/
            adapUsers = new AccountsUsersAdapter(getActivity(), arrUsers);
        }
    }

    /***** THE USER ACCOUNTS CUSTOM ADAPTER *****/
    private class AccountsUsersAdapter extends RecyclerView.Adapter<AccountsUsersAdapter.AccountsVH> implements Filterable {

        /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
        Activity activity;

        /***** ARRAYLIST TO GET DATA FROM THE ACTIVITY *****/
        ArrayList<AccountsData> arrAdapUsers;
        List<AccountsData> mOriginalNames;

        public AccountsUsersAdapter(Activity activity, ArrayList<AccountsData> arrAdapUsers) {

            /** CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE **/
            this.activity = activity;

            /** CAST THE CONTENTS OF THE ARRAYLIST IN THE METHOD TO THE LOCAL INSTANCE **/
            this.arrAdapUsers = arrAdapUsers;
        }

        @Override
        public int getItemCount() {
            return arrAdapUsers.size();
        }

        @Override
        public void onBindViewHolder(AccountsVH holder, final int position) {
            final AccountsData account = arrAdapUsers.get(position);

            /** SET THE ACCOUNT NAME **/
            holder.txtUserName.setText(account.getStaffFullName());

            /** SET THE ACCOUNT PROFILE PICTURE **/
            byte[] STAFF_PICTURE = account.getStaffProfilePicture();
            Bitmap bmpThumb = BitmapFactory.decodeByteArray(STAFF_PICTURE, 0, STAFF_PICTURE.length);
            holder.imgvwProfilePicture.setImageBitmap(bmpThumb);
            holder.imgvwProfilePicture.setScaleType(AppCompatImageView.ScaleType.CENTER_CROP);

            /** EDIT THE ACCOUNT **/
            holder.imgvwMenuOptions.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    PopupMenu pm = new PopupMenu(activity, v);
                    pm.getMenuInflater().inflate(R.menu.pm_accounts_item, pm.getMenu());
                    pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
//                            Toast.makeText(getActivity(), String.valueOf(item.getTitle()), Toast.LENGTH_SHORT).show();

                            switch (item.getItemId())   {

                                case R.id.menuEdit:

                                    /** EDIT THE ACCOUNT **/
                                    Intent editStaff = new Intent(getActivity(), AccountModifier.class);
                                    editStaff.putExtra("STAFF_ID", account.getStaffID());
                                    startActivityForResult(editStaff, ACTION_REQUEST_EDIT_ACCOUNT);

                                    break;

                                case R.id.menuDetails:

                                    /** SHOW THE ACCOUNT DETAILS **/
                                    Intent showStaff = new Intent(getActivity(), AccountDetails.class);
                                    showStaff.putExtra("STAFF_ID", account.getStaffID());
                                    startActivityForResult(showStaff, ACTION_REQUEST_EDIT_ACCOUNT);

                                    break;

                                case R.id.menuDelete:

                                    /** DELETE THE ACCOUNT **/
                                    final String STAFF_ID = account.getStaffID();
                                    String STAFF_USER_NAME = account.getStaffUserName();

                                    if (STAFF_USER_NAME.equals("admin"))   {
                                        Toast.makeText(getActivity(), "You cannot delete the default \"Admin\" account", Toast.LENGTH_LONG).show();
                                    } else {
                                        String strTitle = "DELETE \"" + STAFF_USER_NAME.toUpperCase() + "\"?";
                                        String strMessage = getResources().getString(R.string.account_delete_prompt);
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
                                                        db.deleteStaff(STAFF_ID);

                                                        /* CLEAR THE ARRAYLIST */
                                                        arrUsers.clear();

                                                        /** REFRESH THE SUPPLIERS LIST  **/
                                                        new fetchUsers().execute();
                                                    }
                                                })
                                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                                    @Override
                                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                        dialog.dismiss();
                                                    }
                                                }).show();
                                    }

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
        public AccountsVH onCreateViewHolder(ViewGroup parent, int i) {

            View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.be_admin_accounts_item, parent, false);

            return new AccountsVH(itemView);
        }

        public class AccountsVH extends RecyclerView.ViewHolder	{

            /* PRIMARY ELEMENTS */
            AppCompatImageView imgvwProfilePicture;
            AppCompatTextView txtUserName;

            /* THE POPUP MENU */
            AppCompatImageView imgvwMenuOptions;

            public AccountsVH(View v) {
                super(v);

                /*****	CAST THE LAYOUT ELEMENTS	*****/
				/* PRIMARY ELEMENTS */
                imgvwProfilePicture = (AppCompatImageView) v.findViewById(R.id.imgvwProfilePicture);
                txtUserName = (AppCompatTextView) v.findViewById(R.id.txtUserName);

                /* THE POPUP MENU */
                imgvwMenuOptions = (AppCompatImageView) v.findViewById(R.id.imgvwMenuOptions);
            }
        }

        @Override
        public Filter getFilter() {

            Filter filter = new Filter() {

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                    arrAdapUsers = (ArrayList<AccountsData>) results.values;
                    notifyDataSetChanged();
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {

                    FilterResults results = new FilterResults();
                    ArrayList<AccountsData> FilteredArrayNames = new ArrayList<>();

                    if (mOriginalNames == null) {
                        mOriginalNames = new ArrayList<>(arrAdapUsers);
                    }
                    if (constraint == null || constraint.length() == 0) {
                        results.count = mOriginalNames.size();
                        results.values = mOriginalNames;
                    } else {
                        constraint = constraint.toString().toLowerCase();
                        for (int i = 0; i < mOriginalNames.size(); i++) {
                            AccountsData dataNames = mOriginalNames.get(i);
                            if (dataNames.getStaffFullName().toLowerCase().contains(constraint.toString())) {
                                FilteredArrayNames.add(dataNames);
                            }
                        }
                        results.count = FilteredArrayNames.size();
                        results.values = FilteredArrayNames;
                    }

                    return results;
                }
            };

            return filter;
        }
    }
}