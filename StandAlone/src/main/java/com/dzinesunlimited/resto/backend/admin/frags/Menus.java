package com.dzinesunlimited.resto.backend.admin.frags;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.dzinesunlimited.resto.R;
import com.dzinesunlimited.resto.backend.creators.MenuCreator;
import com.dzinesunlimited.resto.backend.details.MenuDetails;
import com.dzinesunlimited.resto.backend.modifiers.MenuModifier;
import com.dzinesunlimited.resto.utils.TypefaceSpan;
import com.dzinesunlimited.resto.utils.db.DBResto;
import com.dzinesunlimited.resto.utils.helpers.pojos.backend.MenuAdminData;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Menus extends AppCompatActivity {

    /** THE INCOMING CATEGORY ID AND NAME **/
    String INCOMING_CATEGORY_ID = null;
    String INCOMING_CATEGORY_NAME = null;

    /** THE DATABASE HELPER INSTANCE **/
    private DBResto db;

    private LinearLayout linlaHeaderProgress;
    private RecyclerView gridDishes;
    private LinearLayout linlaEmpty;

    /** ADAPTER AND ARRAYLIST FOR THE GRIDVIEW **/
    private MenuAdapter adapMenu;
    private ArrayList<MenuAdminData> arrMenu = new ArrayList<>();

    /** CREATOR REQUEST CODES **/
    private static final int ACTION_REQUEST_NEW_MENU = 101;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.be_admin_menus);

        /***** CAST THE LAYOUT ELEMENTS *****/
        castLayoutElements();

        /** FETCH THE INCOMING DATA **/
        getIncomingData();

        /**INSTANTIATE THE ADAPTER **/
        adapMenu = new MenuAdapter(Menus.this, arrMenu);
    }

    /** FETCH THE INCOMING DATA **/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("CATEGORY_ID") && bundle.containsKey("CATEGORY_NAME"))   {
            INCOMING_CATEGORY_ID = bundle.getString("CATEGORY_ID");
            INCOMING_CATEGORY_NAME = bundle.getString("CATEGORY_NAME");

            if (INCOMING_CATEGORY_NAME != null) {
                /***** CONFIGURE THE ACTIONBAR *****/
                configAB();
            }

            if (INCOMING_CATEGORY_ID != null)   {

                /** CLEAR THE ARRAYLIST **/
                arrMenu.clear();

                /** FETCH THE LIST OF MENUS IN THE SELECTED CATEGORY **/
                new fetchMenus().execute();

            } else {
                //TODO: SHOW AN ERROR
            }
        } else {
            //TODO: SHOW AN ERROR
        }
    }

    /***** FETCH THE MENU FROM THE SELECTED CATEGORY *****/
    private class fetchMenus extends AsyncTask<Void, Void, Void> {

        /** A CURSOR INSTANCE **/
        Cursor cursor;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /***** SHOW THE PROGRESS WHILE LOADING THE CATEGORIES AND THE MENU IN THEM *****/
            linlaHeaderProgress.setVisibility(View.VISIBLE);

            /** INSTANTIATE THE DATABASE HELPER CLASS **/
            db = new DBResto(Menus.this);

            /** CONSTRUCT THE QUERY TO FETCH ALL TWEETS FROM THE DATABASE **/
            String strQueryData =
                    "SELECT * FROM " + db.MENU + " WHERE " + db.MENU_CATEGORY_ID + " = " + "'" + INCOMING_CATEGORY_ID + "'";

            /** CAST THE QUERY IN THE CURSOR TO FETCH THE RESULTS **/
            cursor = db.selectAllData(strQueryData);
            Log.e("MENUS", DatabaseUtils.dumpCursorToString(cursor));
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            /** CHECK THAT THE DATABASE QUERY RETURNED SOME RESULTS **/
            if (cursor != null && cursor.getCount() != 0)	{

				/* AN INSTANCE OF THE MenuAdminData POJO CLASS */
                MenuAdminData menu;

                /** LOOP THROUGH THE RESULT SET AND PARSE NECESSARY INFORMATION **/
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                    /***** INSTANTIATE THE MenuAdminData INSTANCE "menu" *****/
                    menu = new MenuAdminData();

                    /** GET THE MEAL ID **/
                    if (cursor.getString(cursor.getColumnIndex(db.MENU_ID)) != null)	{
                        String strMealNo = cursor.getString(cursor.getColumnIndex(db.MENU_ID));
                        menu.setMealNo(strMealNo);
//						Log.e("MEAL NO.", strMealNo);
                    } else {
                        menu.setMealNo(null);
                    }

                    /** GET THE MEAL NAME **/
                    if (cursor.getString(cursor.getColumnIndex(db.MENU_NAME)) != null)	{
                        String strMealName = cursor.getString(cursor.getColumnIndex(db.MENU_NAME));
                        menu.setMealName(strMealName);
//						Log.e("MEAL NAME", strMealName);
                    } else {
                        menu.setMealName(null);
                    }

                    /** GET THE MEAL DESCRIPTION **/
                    if (cursor.getString(cursor.getColumnIndex(db.MENU_DESCRIPTION)) != null)	{
                        String strMealDesc = cursor.getString(cursor.getColumnIndex(db.MENU_DESCRIPTION));
                        menu.setMealDescription(strMealDesc);
//						Log.e("MEAL DESCRIPTION", strMealDesc);
                    } else {
                        menu.setMealDescription(null);
                    }

                    /** GET THE MEAL IMAGE **/
                    if (cursor.getBlob(cursor.getColumnIndex(db.MENU_IMAGE)) != null)	{
                        byte[] bytImage = cursor.getBlob(cursor.getColumnIndex(db.MENU_IMAGE));
                        Bitmap bmpThumb = BitmapFactory.decodeByteArray(bytImage, 0, bytImage.length);
                        int nh = (int) (bmpThumb.getHeight() * (512.0 / bmpThumb.getWidth()));
                        Bitmap bmpScaled = Bitmap.createScaledBitmap(bmpThumb, 512, nh, true);
                        menu.setMealImage(bmpScaled);
                    } else {
                        menu.setMealImage(null);
                    }

                    /** GET THE MEAL PRICE **/
                    if (cursor.getString(cursor.getColumnIndex(db.MENU_PRICE)) != null)	{
                        String strMealPrice = cursor.getString(cursor.getColumnIndex(db.MENU_PRICE));
                        menu.setMealPrice(strMealPrice);
//						Log.e("MEAL PRICE", strMealPrice);
                    } else {
                        menu.setMealPrice(null);
                    }

                    /** GET THE MEAL CATEGORY ID **/
                    if (cursor.getString(cursor.getColumnIndex(db.MENU_CATEGORY_ID)) != null)	{
                        String strMealCatID = cursor.getString(cursor.getColumnIndex(db.MENU_CATEGORY_ID));
                        menu.setMealCatID(strMealCatID);
//						Log.e("MEAL CATEGORY", strMealCatID);
                    } else {
                        menu.setMealCatID(null);
                    }

                    /** THE MENU TYPE **/
                    if (cursor.getString(cursor.getColumnIndex(db.MENU_TYPE)) != null)	{
                        String strMealType = cursor.getString(cursor.getColumnIndex(db.MENU_TYPE));
                        menu.setMealType(strMealType);
                    } else {
                        menu.setMealType("NA");
                    }

                    /** ADD THE COLLECTED DATA TO THE ARRAYLIST **/
                    arrMenu.add(menu);
                }

                /** SHOW THE GRIDVIEW  AND HIDE THE EMPTY CONTAINER **/
                Runnable run = new Runnable() {

                    @Override
                    public void run() {

                        /** SHOW THE GRIDVIEW **/
                        gridDishes.setVisibility(View.VISIBLE);

                        /** HIDE THE EMPTY DATA SET IMAGE AND TEXT **/
                        linlaEmpty.setVisibility(View.GONE);
                    }
                }; runOnUiThread(run);

            } else {

                /** SHOW THE EMPTY CONTAINER AND HIDE THE LISTVIEW **/
                Runnable run = new Runnable() {

                    @Override
                    public void run() {

                        /** HIDE THE GRID VIEW **/
                        gridDishes.setVisibility(View.GONE);

                        /** SHOW THE EMPTY DATA SET IMAGE AND TEXT **/
                        linlaEmpty.setVisibility(View.VISIBLE);
                    }
                }; runOnUiThread(run);
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

			/* SET THE ADAPTER TO THE GRIDVIEW */
            gridDishes.setAdapter(adapMenu);

            /***** HIDE THE PROGRESS AFTER LOADING THE CATEGORIES AND THE MENU IN THEM *****/
            linlaHeaderProgress.setVisibility(View.GONE);
        }
    }

    /***** CAST THE LAYOUT ELEMENTS *****/
    private void castLayoutElements() {

        /**** DECLARE THE LAYOUT ELEMENTS */
        AppCompatEditText edtSearchMenu = (AppCompatEditText) findViewById(R.id.edtSearchMenu);
        linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
        gridDishes = (RecyclerView) findViewById(R.id.gridDishes);
        linlaEmpty = (LinearLayout) findViewById(R.id.linlaEmpty);

		/* THE RECYCLER VIEW (GRID VIEW AND EMPTY DATA ELEMENTS */
        int intOrientation = Menus.this.getResources().getConfiguration().orientation;
        gridDishes.setHasFixedSize(true);
        GridLayoutManager glm = null;
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isTablet)   {
            if (intOrientation == 1)	{
                glm = new GridLayoutManager(Menus.this, 2);
            } else if (intOrientation == 2) {
                glm = new GridLayoutManager(Menus.this, 3);
            }
        } else {
            if (intOrientation == 1)    {
                glm = new GridLayoutManager(Menus.this, 1);
            } else if (intOrientation == 2) {
                glm = new GridLayoutManager(Menus.this, 2);
            }
        }
        gridDishes.setLayoutManager(glm);

        /** SEARCH FOR A MENU **/
        edtSearchMenu.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapMenu.getFilter().filter(s.toString().toLowerCase());
                adapMenu.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {

        Toolbar myToolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);

//        String strTitle = getResources().getString(R.string.cst_ab_menu_admin_title);
        String strTitle = "Dishes in \"" + INCOMING_CATEGORY_NAME + "\"";
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(
                this, "RobotoCondensed-Regular.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(s);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(Menus.this);
        inflater.inflate(R.menu.admin_new_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:

                this.finish();

                break;
            case R.id.newMenu:

                /***** CHECK THE DISH LIMIT BEFORE SAVING A NEW MENU *****/
                new checkDishLimit().execute();

                break;

            default:
                break;
        }

        return false;
    }

    /***** CHECK THE MENU / DISH LIMIT BEFORE ALLOWING THE CREATION OF A NEW MENU / DISH *****/
    private class checkDishLimit extends AsyncTask<Void, Void, Void>	{

        /** A CURSOR INSTANCE **/
        Cursor cursor;

        /** INTEGER TO HOLD THE NUMBER OF DISHES **/
        int DISHES_COUNT;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /** INSTANTIATE THE DATABASE HELPER CLASS **/
            db = new DBResto(Menus.this);

            /** CONSTRUCT THE QUERY TO FETCH MENUS FROM THE SELECTED CATEGORY  **/
            String strQueryData = "SELECT * FROM " + db.MENU;

            /** CAST THE QUERY IN THE CURSOR TO FETCH THE RESULTS **/
            cursor = db.selectAllData(strQueryData);
        }

        @Override
        protected Void doInBackground(Void... params) {

            /** GET THE NUMBER OF DISHES IN THE DATABASE **/
            DISHES_COUNT = cursor.getCount();
//            Log.e("COUNT", String.valueOf(DISHES_COUNT));

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

            if (DISHES_COUNT == 100 || DISHES_COUNT >= 100)	{
                /** SHOW THE UPGRADE PROMPT **/
                String title = "Would You Like To Upgrade?";
                String content = "You have reached the limit on the number of Dishes allowed in the Demo version of the \"Resto\" Restaurant Manager. To add all your Dishes / Menus and unlock the full potential of Resto, consider purchasing the Full Version.";
                String yes = "Upgrade";
                String no = "Maybe Later";

                /** CONFIGURE THE ALERTDIALOG **/
                new MaterialDialog.Builder(Menus.this)
                        .icon(getResources().getDrawable(R.drawable.ic_info_outline_white_24dp))
                        .title(title)
                        .content(content)
                        .positiveText(yes)
                        .negativeText(no)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                //TODO: REDIRECT USER TO THE PAID VERSION OF RESTO ON GOOGLE PLAY
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        }).show();

            } else if (DISHES_COUNT < 100) {
                Intent newMenu = new Intent(Menus.this, MenuCreator.class);
                startActivityForResult(newMenu, ACTION_REQUEST_NEW_MENU);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Menus.this.RESULT_CANCELED)    {

            /** CLEAR THE ARRAYLIST **/
            arrMenu.clear();

            /** REFRESH THE GRIDVIEW CONTENTS **/
            new fetchMenus().execute();
        }
    }

    /***** THE PRIMARY MENU CUSTOM ADAPTER *****/
    private class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuVH> implements Filterable {

        /* AN ACTIVITY INSTANCE */
        Activity activity;

        /* ARRAYLIST TO GET DATA FROM THE ACTIVITY */
        ArrayList<MenuAdminData> arrSubMenu;
        List<MenuAdminData> mOriginalNames;

        public MenuAdapter(Activity activity, ArrayList<MenuAdminData> arrSubMenu) {

            /** CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE **/
            this.activity = activity;

            /** CAST THE CONTENTS OF THE ARRAYLIST IN THE METHOD TO THE LOCAL INSTANCE **/
            this.arrSubMenu = arrSubMenu;
        }

        @Override
        public int getItemCount() {
            return arrSubMenu.size();
        }

        @Override
        public void onBindViewHolder(MenuVH holder, final int position) {

            final MenuAdminData md = arrSubMenu.get(position);

			/* GET THE SUB MENU NAME */
            final String SUB_MENU_NAME = md.getMealName();
            holder.txtName.setText(SUB_MENU_NAME);

			/* GET THE SUB MENU THUMBNAIL */
            Bitmap MEAL_IMAGE = md.getMealImage();
            holder.imgvwThumb.setImageBitmap(MEAL_IMAGE);

			/* GET THE SUB MENU PRICE */
            final String SUB_MENU_PRICE = md.getMealPrice();
            holder.txtPrice.setText("Rs. " + SUB_MENU_PRICE);

            /* SHOW THE RATING */
            holder.rtbarDishRating.setRating(4.2f);

            /** SHOW THE POPUP MENU **/
            holder.imgvwMenuOptions.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    PopupMenu pm = new PopupMenu(activity, v);
                    pm.getMenuInflater().inflate(R.menu.pm_admin_meal_editor, pm.getMenu());
                    pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
//                            Toast.makeText(Menus.this, String.valueOf(item.getTitle()), Toast.LENGTH_SHORT).show();

                            switch (item.getItemId())   {

                                case R.id.menuEdit:
                                    String strMenuNo = md.getMealNo();
                                    Intent editDish = new Intent(Menus.this, MenuModifier.class);
                                    editDish.putExtra("MENU_ID", strMenuNo);
                                    editDish.putExtra("MENU_NAME", md.getMealName());
                                    startActivityForResult(editDish, 110);
                                    break;

                                case R.id.menuView:
                                    Intent showDetails = new Intent(Menus.this, MenuDetails.class);
                                    showDetails.putExtra("MENU_ID", md.getMealNo());
                                    startActivity(showDetails);
                                    break;

                                case R.id.menuDelete:
                                    final String MENU_ID = md.getMealNo();
                                    String strTitle = "DELETE \"" + md.getMealName().toUpperCase() + "\"?";
                                    String strMessage = getResources().getString(R.string.delete_menu_message);
                                    String strYes = getResources().getString(R.string.generic_mb_yes);
                                    String strNo = getResources().getString(R.string.generic_mb_no);

                                    /** CONFIGURE THE DIALOG **/
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
                                                    db.deleteMenu(MENU_ID);

                                                    /** CLEAR THE ARRAYLIST **/
                                                    arrMenu.clear();

                                                    /** REFRESH THE LIST OF MENUS / DISHES **/
                                                    new fetchMenus().execute();
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
                    }); pm.show();
                }
            });
        }

        @Override
        public MenuVH onCreateViewHolder(ViewGroup parent, int i) {

            View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.be_admin_menus_item, parent, false);

            return new MenuVH(itemView);
        }

        public class MenuVH extends RecyclerView.ViewHolder	{

            /* TOP (PRIMARY) ELEMENTS */
            AppCompatImageView imgvwThumb;
            AppCompatTextView txtName;
            AppCompatTextView txtPrice;

            /* BOTTOM ELEMENTS */
            AppCompatRatingBar rtbarDishRating;
            AppCompatImageView imgvwMenuOptions;

            public MenuVH(View v) {
                super(v);

                /*****	CAST THE LAYOUT ELEMENTS	*****/
				/* TOP (PRIMARY) ELEMENTS */
                imgvwThumb = (AppCompatImageView) v.findViewById(R.id.imgvwThumb);
                txtName = (AppCompatTextView) v.findViewById(R.id.txtName);
                txtPrice = (AppCompatTextView) v.findViewById(R.id.txtPrice);

				/* BOTTOM ELEMENTS */
                rtbarDishRating = (AppCompatRatingBar) v.findViewById(R.id.rtbarDishRating);
                imgvwMenuOptions = (AppCompatImageView) v.findViewById(R.id.imgvwMenuOptions);
            }
        }

        @Override
        public Filter getFilter() {

            Filter filter = new Filter() {

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                    arrSubMenu = (ArrayList<MenuAdminData>) results.values;
                    notifyDataSetChanged();
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {

                    FilterResults results = new FilterResults();
                    ArrayList<MenuAdminData> FilteredArrayNames = new ArrayList<>();

                    if (mOriginalNames == null) {
                        mOriginalNames = new ArrayList<>(arrSubMenu);
                    }
                    if (constraint == null || constraint.length() == 0) {
                        results.count = mOriginalNames.size();
                        results.values = mOriginalNames;
                    } else {
                        constraint = constraint.toString().toLowerCase();
                        for (int i = 0; i < mOriginalNames.size(); i++) {
                            MenuAdminData dataNames = mOriginalNames.get(i);
                            if (dataNames.getMealName().toLowerCase().contains(constraint.toString())) {
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

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}