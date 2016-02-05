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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.dzinesunlimited.resto.R;
import com.dzinesunlimited.resto.backend.creators.CategoryCreator;
import com.dzinesunlimited.resto.backend.modifiers.CategoryModifier;
import com.dzinesunlimited.resto.utils.TypefaceSpan;
import com.dzinesunlimited.resto.utils.db.DBResto;
import com.dzinesunlimited.resto.utils.helpers.pojos.CategoriesData;

import java.util.ArrayList;
import java.util.List;

public class Categories extends Fragment {

    /** GLOBAL VIEW INSTANCE **/
    private View view;

    /** THE DATABASE HELPER INSTANCE **/
    private DBResto db;

    /** DECLARE THE LAYOUT ELEMENTS **/
    AppCompatEditText edtSearchCategories;
    LinearLayout linlaHeaderProgress;
    RecyclerView gridCategories;
    LinearLayout linlaEmpty;

    /** THE ADAPTER AND THE ARRAYLIST **/
    CategoriesAdminAdapter adapter;
    ArrayList<CategoriesData> arrCategories = new ArrayList<>();

    /** THE ACTION DECLARATIONS **/
    private static final int ACTION_NEW_CATEGORY = 101;
    private static final int ACTION_EDIT_CATEGORY = 102;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /** CAST THE LAYOUT TO A NEW VIEW INSTANCE **/
        view = inflater.inflate(R.layout.be_admin_categories, container, false);

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

        /** FETCH ALL MENU CATEGORIES **/
        new fetchAllCategories().execute();

        /** INSTANTIATE THE ADAPTER **/
        adapter = new CategoriesAdminAdapter(getActivity(), arrCategories);
    }

    private class fetchAllCategories extends AsyncTask<Void, Void, Void> {

        /** A CURSOR INSTANCE **/
        Cursor cursor;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /** CLEAR THE ARRAY LIST **/
            arrCategories.clear();

            /** SHOW THE PROGRESS WHILE FETCHING THE DATA **/
            linlaHeaderProgress.setVisibility(View.VISIBLE);

            /** INSTANTIATE THE DATABASE HELPER CLASS **/
            db = new DBResto(getActivity());

            /** CONSTRUCT THE QUERY TO FETCH ALL TWEETS FROM THE DATABASE **/
            String strQueryData = "SELECT * FROM " + db.CATEGORY;

            /** CAST THE QUERY IN THE CURSOR TO FETCH THE RESULTS **/
            cursor = db.selectAllData(strQueryData);
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

            /** SET THE ADAPTER TO THE RECYCLERVIEW **/
            gridCategories.setAdapter(adapter);

            /** HIDE THE PROGRESS AFTER FETCHING THE DATA **/
            linlaHeaderProgress.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... params) {

            /** CHECK THAT THE DATABASE QUERY RETURNED SOME RESULTS **/
            if (cursor != null && cursor.getCount() != 0)	{

				/* AN INSTANCE OF THE CategoriesData POJO CLASS */
                CategoriesData data;

                /** LOOP THROUGH THE RESULT SET AND PARSE NECESSARY INFORMATION **/
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                    /***** INSTANTIATE THE CategoriesData INSTANCE "data" *****/
                    data = new CategoriesData();

                    /** GET THE CATEGORY_ID **/
                    if (cursor.getString(cursor.getColumnIndex(db.CATEGORY_ID)) != null)	{
                        String CATEGORY_ID = cursor.getString(cursor.getColumnIndex(db.CATEGORY_ID));
                        data.setCatID(CATEGORY_ID);
                    } else {
                        data.setCatID(null);
                    }

                    /** GET THE CATEGORY_NAME **/
                    if (cursor.getString(cursor.getColumnIndex(db.CATEGORY_NAME)) != null)	{
                        String CATEGORY_NAME = cursor.getString(cursor.getColumnIndex(db.CATEGORY_NAME));
                        data.setCatName(CATEGORY_NAME);
                    } else {
                        data.setCatName(null);
                    }

                    /** GET THE CATEGORY_IMAGE **/
                    if (cursor.getBlob(cursor.getColumnIndex(db.CATEGORY_IMAGE)) != null)	{
                        byte[] CATEGORY_IMAGE = cursor.getBlob(cursor.getColumnIndex(db.CATEGORY_IMAGE));
                        data.setCatPicture(CATEGORY_IMAGE);
                    } else {
                        data.setCatPicture(null);
                    }

                    /** ADD THE COLLECTED DATA TO THE ARRAYLIST **/
                    arrCategories.add(data);
                }

                /** SHOW THE GRIDVIEW  AND HIDE THE EMPTY CONTAINER **/
                Runnable run = new Runnable() {

                    @Override
                    public void run() {

                        /** SHOW THE GRIDVIEW **/
                        gridCategories.setVisibility(View.VISIBLE);

                        /** HIDE THE EMPTY DATA SET IMAGE AND TEXT **/
                        linlaEmpty.setVisibility(View.GONE);
                    }
                }; getActivity().runOnUiThread(run);

            } else {
                /** SHOW THE EMPTY CONTAINER AND HIDE THE GRIDVIEW **/
                Runnable run = new Runnable() {

                    @Override
                    public void run() {

                        /** HIDE THE GRID VIEW **/
                        gridCategories.setVisibility(View.GONE);

                        /** SHOW THE EMPTY DATA SET IMAGE AND TEXT **/
                        linlaEmpty.setVisibility(View.VISIBLE);
                    }
                }; getActivity().runOnUiThread(run);
            }

            return null;
        }
    }

    /***** CAST THE LAYOUT ELEMENTS *****/
    private void castLayoutElements() {

        /* TOP WIDGETS */
        edtSearchCategories = (AppCompatEditText) view.findViewById(R.id.edtSearchCategories);
        linlaHeaderProgress = (LinearLayout) view.findViewById(R.id.linlaHeaderProgress);
        gridCategories = (RecyclerView) view.findViewById(R.id.gridCategories);
        linlaEmpty = (LinearLayout) view.findViewById(R.id.linlaEmpty);

        /* CONFIGURE THE RECYCLER VIEW */
        int intOrientation = getActivity().getResources().getConfiguration().orientation;
        gridCategories.setHasFixedSize(true);
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
        gridCategories.setLayoutManager(glm);

        /** SEARCH FOR A MENU CATEGORY **/
        edtSearchCategories.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString().toLowerCase());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {

        String strTitle = getResources().getString(R.string.category_manager_title);
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
        inflater.inflate(R.menu.admin_new_category, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.newCategory:

                /***** CREATE A NEW USER / ACCOUNT *****/
                Intent newAccount = new Intent(getActivity(), CategoryCreator.class);
                startActivityForResult(newAccount, ACTION_NEW_CATEGORY);

                break;

            default:
                break;
        }

        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_CANCELED)    {

            switch (requestCode)    {
                case ACTION_NEW_CATEGORY:
                    /** CLEAR THE ARRAYLIST **/
                    arrCategories.clear();

                    /** FETCH THE CATEGORIES AGAIN **/
                    new fetchAllCategories().execute();

                    break;
                case ACTION_EDIT_CATEGORY:
                    /** CLEAR THE ARRAYLIST **/
                    arrCategories.clear();

                    /** FETCH THE CATEGORIES AGAIN **/
                    new fetchAllCategories().execute();

                    break;

                default:
                    break;
            }
        }
    }

    /***** THE USER ACCOUNTS CUSTOM ADAPTER *****/
    private class CategoriesAdminAdapter extends RecyclerView.Adapter<CategoriesAdminAdapter.CategoriesVH> implements Filterable {

        /** AN ACTIVITY INSTANCE **/
        Activity activity;

        /** ARRAYLIST TO GET DATA FROM THE ACTIVITY **/
        ArrayList<CategoriesData> arrAdapCategories;
        List<CategoriesData> mOriginalNames;

        public CategoriesAdminAdapter(Activity activity, ArrayList<CategoriesData> arrAdapCategories) {

            /** CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE **/
            this.activity = activity;

            /** CAST THE CONTENTS OF THE ARRAYLIST IN THE METHOD TO THE LOCAL INSTANCE **/
            this.arrAdapCategories = arrAdapCategories;
        }

        @Override
        public int getItemCount() {
            return arrCategories.size();
        }

        @Override
        public void onBindViewHolder(CategoriesVH holder, final int position) {
            final CategoriesData md = arrAdapCategories.get(position);

            /** GET THE SUB MENU NAME **/
            final String SUB_MENU_NAME = md.getCatName();
            holder.txtCategoryName.setText(SUB_MENU_NAME);

            /** GET THE SUB MENU THUMBNAIL **/
            byte[] CATEGORY_THUMB = md.getCatPicture();
            if (CATEGORY_THUMB != null)	{
                Bitmap bmpThumb = BitmapFactory.decodeByteArray(CATEGORY_THUMB, 0, CATEGORY_THUMB.length);
                holder.imgvwCategory.setImageBitmap(bmpThumb);
            }

            /** SHOW THE POPUP MENU **/
            holder.imgvwCategoryOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PopupMenu pm = new PopupMenu(activity, v);
                    pm.getMenuInflater().inflate(R.menu.pm_category_admin, pm.getMenu());
                    pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId())   {
                                case R.id.catEdit:
                                    /** EDIT THE CATEGORY **/
                                    Intent editCategory = new Intent(getActivity(), CategoryModifier.class);
                                    editCategory.putExtra("CATEGORY_ID", md.getCatID());
                                    editCategory.putExtra("CATEGORY_NAME", md.getCatName());
                                    startActivityForResult(editCategory, ACTION_EDIT_CATEGORY);
                                    break;
                                case R.id.catDelete:
                                    /** CHECK IF THE CATEGORY CONTAINS ANY DISHES / MEALS **/
                                    db = new DBResto(getActivity());
                                    String s = "SELECT * FROM " + db.MENU + " WHERE " + db.MENU_CATEGORY_ID + "=" + md.getCatID();
                                    Cursor cursor = db.selectAllData(s);

                                    if (cursor.getCount() != 0) {
                                        String message = "\"" + md.getCatName().toUpperCase() + "\"" + " CONTAINS DISHES. YOU WILL NEED TO EITHER DELETE THE DISHES OR CHANGE THEIR CATEGORIES BEFORE DELETING THIS CATEGORY";
                                        String title = "Cannot Delete " + md.getCatName();
                                        String okay = getResources().getString(R.string.generic_mb_ok);

                                        /** CONFIGURE THE DIALOG **/
                                        new MaterialDialog.Builder(activity)
                                                .icon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_info_outline_black_24dp))
                                                .title(title)
                                                .content(message)
                                                .neutralText(okay)
                                                .theme(Theme.LIGHT)
                                                .typeface("HelveticaNeueLTW1G-MdCn.otf", "HelveticaNeueLTW1G-Cn.otf")
                                                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                                                    @Override
                                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                        dialog.dismiss();
                                                    }
                                                }).show();

                                    } else {
                                        /** DELETE THE CATEGORY **/
                                        final String CAT_ID = md.getCatID();
                                        String strTitle = "DELETE \"" + md.getCatName().toUpperCase() + "\"?";
                                        String strMessage = getResources().getString(R.string.delete_category_message);
                                        String strYes = getResources().getString(R.string.generic_mb_yes);
                                        String strNo = getResources().getString(R.string.generic_mb_no);

                                        /** CONFIGURE THE DIALOG **/
                                        new MaterialDialog.Builder(activity)
                                                .icon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_info_outline_black_24dp))
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
                                                        db.deleteCategory(CAT_ID);

                                                        /** CLEAR THE ARRAYLIST **/
                                                        arrCategories.clear();

                                                        /** REFRESH THE LIST OF MENU CATEGORIES **/
                                                        new fetchAllCategories().execute();
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

            /** SHOW THE LIST OF DISHES IN THIS CATEGORY **/
            holder.imgvwCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent showMenu = new Intent(activity, Menus.class);
                    showMenu.putExtra("CATEGORY_ID", md.getCatID());
                    showMenu.putExtra("CATEGORY_NAME", md.getCatName());
                    activity.startActivity(showMenu);
                }
            });
        }

        @Override
        public CategoriesVH onCreateViewHolder(ViewGroup parent, int i) {

            View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.be_admin_categories_item, parent, false);

            return new CategoriesVH(itemView);
        }

        public class CategoriesVH extends RecyclerView.ViewHolder	{

//            RelativeLayout mainContainer;
            AppCompatImageView imgvwCategory;
            AppCompatTextView txtCategoryName;
            AppCompatImageView imgvwCategoryOptions;

            public CategoriesVH(View v) {
                super(v);

                /*****	CAST THE LAYOUT ELEMENTS	*****/
//                mainContainer = (RelativeLayout) v.findViewById(R.id.mainContainer);
                imgvwCategory = (AppCompatImageView) v.findViewById(R.id.imgvwCategory);
                txtCategoryName = (AppCompatTextView) v.findViewById(R.id.txtCategoryName);
                imgvwCategoryOptions = (AppCompatImageView) v.findViewById(R.id.imgvwCategoryOptions);
            }

        }

        @Override
        public Filter getFilter() {

            Filter filter = new Filter() {

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                    arrAdapCategories = (ArrayList<CategoriesData>) results.values;
                    notifyDataSetChanged();
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {

                    FilterResults results = new FilterResults();
                    ArrayList<CategoriesData> FilteredArrayNames = new ArrayList<>();

                    if (mOriginalNames == null) {
                        mOriginalNames = new ArrayList<>(arrAdapCategories);
                    }
                    if (constraint == null || constraint.length() == 0) {
                        results.count = mOriginalNames.size();
                        results.values = mOriginalNames;
                    } else {
                        constraint = constraint.toString().toLowerCase();
                        for (int i = 0; i < mOriginalNames.size(); i++) {
                            CategoriesData data = mOriginalNames.get(i);
                            if (data.getCatName().toLowerCase().contains(constraint.toString())) {
                                FilteredArrayNames.add(data);
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