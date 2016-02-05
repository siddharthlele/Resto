package com.dzinesunlimited.resto.utils.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.dzinesunlimited.resto.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DBResto {

    /** ASSETMANAGER INSTANCE TO GRAB THE THUMBNAILS FROM THE ASSETS FOLDER **/
    AssetManager asstMgr;

    /** INPUTSTREAM INSTANCE TO FETCH THE IMAGE PATH FROM THE ASSETS FOLDER **/
    InputStream inStream;

    /** BITMAP INSTANCE TO CONVERT ASSETS INTO BITMAPS **/
    Bitmap bmpThumb;

    /** byte[] INSTANCE FOR CONVERTING ASSETS INTO BITMAPS AND BITMAPS INTO BLOBS **/
    byte[] bArray;
	
	/** A CONTEXT INSTANCE **/
    private Context context;
	
	/** A REFERENCE TO THE DATABASE USED BY FACEBOOK NOTIFICATIONS **/
    private SQLiteDatabase db;
	
	/** THE HELPER **/
    private DBSQLHelper helper = null;

	/** DATABASE GLOBAL NAME AND VERSION **/
	private final String DB_NAME = "resto.db";
	private final int DB_VERSION = 1;

	/*****	THE TABLE NAMES	*****/
	public final String RESTAURANT = "restaurant";
    public final String STAFF_ROLES = "roles";
    public final String STAFF = "staff";
    public final String CATEGORY = "category";
    public final String MENU = "menu";
    public final String TABLES = "tables";
    public final String TAXES = "taxes";
    public final String COUNTRIES = "countries";
    public final String CURRENCY = "currency";
    public final String ORDER_CART = "orderCart";

    /* RESTAURANT */
    public final String RESTAURANT_ID = "restID";
    public final String RESTAURANT_NAME = "restName";
    public final String RESTAURANT_ADDRESS_1 = "restAddress1";
    public final String RESTAURANT_ADDRESS_2 = "restAddress2";
    public final String RESTAURANT_CITY = "restCity";
    public final String RESTAURANT_STATE = "restState";
    public final String RESTAURANT_COUNTRY = "restCountry";
    public final String RESTAURANT_ZIP = "restZip";
    public final String RESTAURANT_PHONE = "restPhone";
    public final String RESTAURANT_EMAIL = "restEmail";
    public final String RESTAURANT_WEBSITE = "restWebsite";
    public final String RESTAURANT_LOGO = "restLogo";

    /* STAFF ROLES TABLE */
    public final String ROLE_ID = "roleID";
    public final String ROLE_CODE = "roleCode";
    public final String ROLE_TEXT = "roleText";
    public final String ROLE_DESCRIPTION = "roleDescription";

    /* STAFF */
    public final String STAFF_ID = "staffID";
    public final String STAFF_ROLE_ID = "roleID";
    public final String STAFF_FULL_NAME = "staffFullName";
    public final String STAFF_PHONE = "staffPhone";
    public final String STAFF_USER_NAME = "staffUserName";
    public final String STAFF_PASSWORD = "staffPassword";
    public final String STAFF_PROFILE_PICTURE = "staffProfilePicture";

    /* MEAL TYPE */
    public final String CATEGORY_ID = "categoryID";
    public final String CATEGORY_NAME = "categoryName";
    public final String CATEGORY_IMAGE = "categoryImage";

    /* MENU */
    public final String MENU_ID = "menuID";
    public final String MENU_CATEGORY_ID = "categoryID";
    public final String MENU_NAME = "menuName";
    public final String MENU_DESCRIPTION = "menuDescription";
    public final String MENU_PRICE = "menuPrice";
    public final String MENU_TYPE = "menuType";
    public final String MENU_IMAGE = "menuImage";
    public final String MENU_SERVES = "menuServes";

    /* TABLES */
    public final String TABLE_ID = "tableID";
    public final String TABLE_SEATS = "tableSeats";
    public final String TABLE_OCCUPANCY = "tableOccupancy";

    /* ORDER CART */
    public final String ORDER_CART_ID = "orderID";
    public final String ORDER_TABLE_ID = "tableID";
    public final String ORDER_MENU_ID = "menuID";
    public final String ORDER_QUANTITY = "orderQuantity";
    public final String ORDER_STATUS = "orderStatus";
    public final String ORDER_TIMESTAMP = "orderTimestamp";

    /* TAXES */
    public final String TAX_ID = "taxID";
    public final String TAX_NAME = "taxName";
    public final String TAX_REGISTRATION = "taxRegistration";
    public final String TAX_PERCENTAGE = "taxPercentage";
    public final String TAX_ENTIRE_AMOUNT = "taxEntireAmount";
    public final String TAX_TAXABLE_PERCENTAGE = "taxTaxablePercentage";

    /* THE COUNTRIES TABLE */
    public final String COUNTRY_ID = "countryID";
    public final String COUNTRY_NAME = "countryName";

    /* CURRENCY */
    public final String CURRENCY_ID = "currencyID";
    public final String CURRENCY_NAME = "currencyName";
    public final String CURRENCY_ISO_CODE = "currencyISOCode";
    public final String CURRENCY_SYMBOL = "currencySymbol";

    /** GET ALL THE DATA IN THE DATABASE BASED ON THE QUERY **/
    public Cursor selectAllData(String strQueryData) {

        Cursor cursor/* = null*/;

        if (this.db.isOpen())	{
            this.db.close();
        }

        this.db = helper.getReadableDatabase();
        cursor = db.rawQuery(strQueryData, null);

        return cursor;
    }

    /** ADD A COUNTRY TO THE DATABASE **/
    public void addCountry(String strCountry)	{

		/* OPEN THE DATABASE AGAIN */
        this.db = helper.getWritableDatabase();

        /** ADD AND CREATE KEY VALUE PAIRS FOR ADDING A NEW COUNTRY TO THE DATABASE **/
        ContentValues valNewCountry = new ContentValues();
        valNewCountry.put(COUNTRY_NAME, strCountry);

		/* INSERT THE COLLECTED DATA TO THE COUNTRY TABLE */
        db.insert(COUNTRIES, null, valNewCountry);
    }

    /** ADD A RESTAURANT TO THE DATABASE **/
    public void addRestaurantDetails(
            int intID, String strName, byte[] bytLogo, String strAdd1, String strAdd2,
            String strCity, String strState, String strCountry, String strZIP,
            String strPhone, String strEmail, String strWebsite)	{

		/* OPEN THE DATABASE AGAIN */
        this.db = helper.getWritableDatabase();

        /** ADD AND CREATE KEY VALUE PAIRS FOR ADDING A NEW COUNTRY TO THE DATABASE **/
        ContentValues valRestaurantDetails = new ContentValues();
        valRestaurantDetails.put(RESTAURANT_ID, intID);
        valRestaurantDetails.put(RESTAURANT_NAME, strName);
        valRestaurantDetails.put(RESTAURANT_LOGO, bytLogo);
        valRestaurantDetails.put(RESTAURANT_ADDRESS_1, strAdd1);
        valRestaurantDetails.put(RESTAURANT_ADDRESS_2, strAdd2);
        valRestaurantDetails.put(RESTAURANT_CITY, strCity);
        valRestaurantDetails.put(RESTAURANT_STATE, strState);
        valRestaurantDetails.put(RESTAURANT_COUNTRY, strCountry);
        valRestaurantDetails.put(RESTAURANT_ZIP, strZIP);
        valRestaurantDetails.put(RESTAURANT_PHONE, strPhone);
        valRestaurantDetails.put(RESTAURANT_EMAIL, strEmail);
        valRestaurantDetails.put(RESTAURANT_WEBSITE, strWebsite);

		/* INSERT THE COLLECTED DATA TO THE TABLES TABLE */
        db.insert(RESTAURANT, null, valRestaurantDetails);
    }

    /** ADD A NEW ACCOUNT TO THE STAFF TABLE **/
    public void addStaff(
            String roleId, String fullName, String phone,
            String userName, String password, byte[] profilePicture) {

		/* OPEN THE DATABASE AGAIN */
        this.db = helper.getWritableDatabase();

        /** ADD AND CREATE KEY VALUE PAIRS FOR CREATING A NEW ACCOUNT **/
        ContentValues valNewAccount = new ContentValues();
        valNewAccount.put(STAFF_ROLE_ID, roleId);
        valNewAccount.put(STAFF_FULL_NAME, fullName);
        valNewAccount.put(STAFF_PHONE, phone);
        valNewAccount.put(STAFF_USER_NAME, userName);
        valNewAccount.put(STAFF_PASSWORD, password);
        valNewAccount.put(STAFF_PROFILE_PICTURE, profilePicture);

		/* INSERT THE COLLECTED DATA TO THE STAFF TABLE */
        db.insert(STAFF, null, valNewAccount);
    }

    /** ADD A NEW TABLE **/
    public void createNewTable(
            String strTableNumber, String strTableSeats,
            String strOccupationStatus)	{

		/* OPEN THE DATABASE AGAIN */
        this.db = helper.getWritableDatabase();

        /** ADD AND CREATE KEY VALUE PAIRS FOR CREATING A NEW TABLE **/
        ContentValues valNewTable = new ContentValues();
        valNewTable.put(TABLE_ID, strTableNumber);
        valNewTable.put(TABLE_SEATS, strTableSeats);
        valNewTable.put(TABLE_OCCUPANCY, strOccupationStatus);

		/* INSERT THE COLLECTED DATA TO THE TABLES TABLE */
        db.insert(TABLES, null, valNewTable);
    }

    /** ADD A NEW TAX TO THE TAXES TABLE **/
    public void addTax(
            String strTaxName, String strTaxPercentage, String strTaxRegistrationNumber,
            boolean blnTaxEntireAmount, String strTaxAmountPercentage)	{

		/* OPEN THE DATABASE AGAIN */
        this.db = helper.getWritableDatabase();

        /** ADD AND CREATE KEY VALUE PAIRS FOR ADDING A NEW TAX TO THE DATABASE **/
        ContentValues valTaxes = new ContentValues();
        valTaxes.put(TAX_NAME, strTaxName);
        valTaxes.put(TAX_PERCENTAGE, strTaxPercentage);
        valTaxes.put(TAX_REGISTRATION, strTaxRegistrationNumber);
        valTaxes.put(TAX_ENTIRE_AMOUNT, String.valueOf(blnTaxEntireAmount));
        valTaxes.put(TAX_TAXABLE_PERCENTAGE, strTaxAmountPercentage);

		/* INSERT THE COLLECTED DATA TO THE TAXES TABLE */
        db.insert(TAXES, null, valTaxes);
    }

    /***** CREATE A NEW DISH / MENU / MEAL *****/
    public void newDishItem(
            String strCategoryID,
            String strMealName,
            String strMealDescription,
            String strMealPrice ,
            String strMealType,
            String strServes,
            byte[] bytMealImage) {

		/* OPEN THE DATABASE AGAIN */
        this.db = helper.getWritableDatabase();

        /** ADD AND CREATE KEY VALUE PAIRS FOR ADDING A DISH / MENU TO THE DATABASE **/
        ContentValues valNewMenu = new ContentValues();
        valNewMenu.put(MENU_CATEGORY_ID, strCategoryID);
        valNewMenu.put(MENU_NAME, strMealName);
        valNewMenu.put(MENU_DESCRIPTION, strMealDescription);
        valNewMenu.put(MENU_PRICE, strMealPrice);
        valNewMenu.put(MENU_TYPE, strMealType);
        valNewMenu.put(MENU_SERVES, strServes);
        valNewMenu.put(MENU_IMAGE, bytMealImage);

		/* INSERT THE COLLECTED DATA TO THE MENU TABLE */
        db.insert(MENU, null, valNewMenu);
    }

    /***** ADD A NEW ORDER *****/
    public void addOrder(
            int tableID,
            int mealID,
            int mealQuantity,
            boolean orderStatus) {

		/* OPEN THE DATABASE AGAIN */
        this.db = helper.getWritableDatabase();

        /** ADD AND CREATE KEY VALUE PAIRS FOR ADDING A DISH / MENU TO THE DATABASE **/
        ContentValues valNewOrder = new ContentValues();
        valNewOrder.put(ORDER_TABLE_ID, tableID);
        valNewOrder.put(ORDER_MENU_ID, mealID);
        valNewOrder.put(ORDER_QUANTITY, mealQuantity);
        valNewOrder.put(ORDER_STATUS, orderStatus);

		/* INSERT THE COLLECTED DATA TO THE ORDERS TABLE */
        db.insert(ORDER_CART, null, valNewOrder);
    }

    /***** CREATE / ADD A NEW MENU CATEGORY *****/
    public void newMenuCategory(String strMealName, byte[] bytMealThumb) {

		/* OPEN THE DATABASE AGAIN */
        this.db = helper.getWritableDatabase();

        /** ADD AND CREATE KEY VALUE PAIRS FOR ADDING A NEW CATEGORY TO THE DATABASE **/
        ContentValues valNewCategory = new ContentValues();
        valNewCategory.put(CATEGORY_NAME, strMealName);
        valNewCategory.put(CATEGORY_IMAGE, bytMealThumb);

		/* INSERT THE COLLECTED DATA TO THE CATEGORIES TABLE */
        db.insert(CATEGORY, null, valNewCategory);
    }

    /********************   UPDATES     ********************/

    // UPDATE CATEGORY
    public void updateCategory(String catID, String catName, byte[] catImage)  {

        /* OPEN THE DATABASE AGAIN */
        this.db = helper.getWritableDatabase();

        /* ADD AND CREATE KEY VALUE PAIRS FOR UPDATING AN EXISTING CATEGORY */
        ContentValues valUpdateCategory = new ContentValues();
        valUpdateCategory.put(CATEGORY_NAME, catName);
        valUpdateCategory.put(CATEGORY_IMAGE, catImage);

        /* UPDATE THE COLLECTED DATA TO THE CATEGORY TABLE */
        db.update(CATEGORY, valUpdateCategory, CATEGORY_ID + "=" + catID, null);
    }

    // UPDATE AN ORDER
    public void updateOrderQuantity(String strOrderID, int intOrderQuantity)    {

        /* OPEN THE DATABASE AGAIN */
        this.db = helper.getWritableDatabase();

        /* ADD AND CREATE KEY VALUE PAIRS FOR UPDATING AN EXISTING TAX */
        ContentValues valUpdateOrder = new ContentValues();
        valUpdateOrder.put(ORDER_QUANTITY, intOrderQuantity);

        /* INSERT THE COLLECTED DATA TO THE ORDER CART TABLE */
        db.update(ORDER_CART, valUpdateOrder, ORDER_CART_ID + "=" + strOrderID, null);
    }

    // UPDATE AN EXISTING TAX
    public void updateTax(
            String taxID, String taxName, String taxPercentage, String taxRegistration,
            boolean completeAmount, String taxPercentOfAmount) {

        /* OPEN THE DATABASE AGAIN */
        this.db = helper.getWritableDatabase();

        /* ADD AND CREATE KEY VALUE PAIRS FOR UPDATING AN EXISTING TAX */
        ContentValues valUpdateTax = new ContentValues();
        valUpdateTax.put(TAX_NAME, taxName);
        valUpdateTax.put(TAX_PERCENTAGE, taxPercentage);
        valUpdateTax.put(TAX_REGISTRATION, taxRegistration);
        valUpdateTax.put(TAX_ENTIRE_AMOUNT, completeAmount);
        valUpdateTax.put(TAX_TAXABLE_PERCENTAGE, taxPercentOfAmount);

        /* INSERT THE COLLECTED DATA TO THE TAXES TABLE */
        db.update(TAXES, valUpdateTax, TAX_ID + "=" + taxID, null);
    }

    /** UPDATE THE TABLE OCCUPATION STATUS **/
    public void updateTableStatus(String tableNo, String tableStatus) {
        this.db = helper.getWritableDatabase();
        ContentValues valUpdateTable = new ContentValues();
        valUpdateTable.put(TABLE_OCCUPANCY, tableStatus);
        db.update(TABLES, valUpdateTable, TABLE_ID + "=" + tableNo, null);
    }

    /** UPDATE EXISTING MENU ITEM **/
    public void updateMenu(
            String menuID, String menuCatID, String menuName,
            String menuDesc, String menuPrice, String menuType,
            String menuServes, byte[] menuImage) {
        this.db = helper.getWritableDatabase();
        ContentValues valUpdateMenu = new ContentValues();
        valUpdateMenu.put(MENU_CATEGORY_ID, menuCatID);
        valUpdateMenu.put(MENU_NAME, menuName);
        valUpdateMenu.put(MENU_DESCRIPTION, menuDesc);
        valUpdateMenu.put(MENU_PRICE, menuPrice);
        valUpdateMenu.put(MENU_TYPE, menuType);
        valUpdateMenu.put(MENU_SERVES, menuServes);
        valUpdateMenu.put(MENU_IMAGE, menuImage);
        db.update(MENU, valUpdateMenu, MENU_ID + "=" + menuID, null);
    }

    /***** UPDATE AND STAFF ACCOUNT *****/
    public void updateStaff(
            String staffID, String strRoleID, String strFName,
            String strPhone, String strUsername, String strPassword, byte[] bytProfile) {

		/* OPEN THE DATABASE AGAIN */
        this.db = helper.getWritableDatabase();

        /** ADD AND CREATE KEY VALUE PAIRS FOR ADDING A NEW ACCOUNT TO THE DATABASE **/
        ContentValues valUpdateStaff = new ContentValues();
        valUpdateStaff.put(STAFF_ROLE_ID, strRoleID);
        valUpdateStaff.put(STAFF_FULL_NAME, strFName);
        valUpdateStaff.put(STAFF_PHONE, strPhone);
        valUpdateStaff.put(STAFF_USER_NAME, strUsername);
        valUpdateStaff.put(STAFF_PASSWORD, strPassword);
        valUpdateStaff.put(STAFF_PROFILE_PICTURE, bytProfile);

		/* UPDATE THE ROW WITH THE NEW COLLECTED DATA IN THE STAFF TABLE */
        db.update(STAFF, valUpdateStaff, STAFF_ID + "=" + staffID, null);
    }

    /***** DELETE A ACCOUNT / STAFF *****/
    public void deleteStaff(String strStaffID) {
        db.delete(STAFF, STAFF_ID + "=" + strStaffID, null);
    }

    /***** DELETE A TABLE *****/
    public void deleteTable(String strTableID) {
        db.delete(TABLES, TABLE_ID + "=" + strTableID, null);
    }

    /** DELETE THE MENU CATEGORY **/
    public void deleteCategory(String catId) {
        db.delete(CATEGORY, CATEGORY_ID + "=" + catId, null);
    }

    /** DELETE AN EXISTING TAX **/
    public void deleteTax(String taxId) {
        db.delete(TAXES, TAX_ID + "=" + taxId , null);
    }

    /***** DELETE AN ORDER *****/
    public void deleteOrder(String strOrderID) {
        db.delete(ORDER_CART, ORDER_CART_ID + "=" + strOrderID, null);
    }


    /***************** CLOSE THE DATABASE FROM VARIOUS ACTIVITES WHEN DONE WITH IT ****************/
	public void close() {
		helper.close();
	}
	
	/***** CREATE OR OPEN THE DATABASE *****/
	public DBResto(Context context) {
		this.context = context;
		
		// CREATE OR OPEN THE DATABASE
		helper = new DBSQLHelper(context);
		this.db = helper.getWritableDatabase();
	}

    /*****	PRIVATE CLASS TO CREATE AND OR OPEN DATABASE	*****/
	private class DBSQLHelper extends SQLiteOpenHelper {

		public DBSQLHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {

			/** CREATE THE RESTAURANT TABLE **/
            createRestaurantTable(db);

			/** CREATE THE STAFF ROLES TABLE **/
            createStaffRolesTable(db);

			/** CREATE THE STAFF TABLE **/
            createStaffTable(db);

			/** CREATE THE MEAL TYPE TABLE **/
            createMealTypesTable(db);

            /** CREATE THE MENU TABLE **/
            createMealsTable(db);

            /** CREATE THE TABLES (COVERS) TABLE **/
            createTablesTable(db);

            /** CREATE THE TAXES TABLE **/
            createTaxesTable(db);

            /** CREATE THE COUNTRIES TABLE **/
            createCountriesTable(db);

            /** CREATE THE CURRENCY TABLE **/
            createCurrencyTable(db);

			/** CREATE THE ORDER CART TABLE **/
            createOrderCartTable(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}

    private void createStaffRolesTable(SQLiteDatabase db) {

        String strCreateStaffRolesTable = "create table " + STAFF_ROLES +
                " (" +
                ROLE_ID + " integer primary key, " +
                ROLE_CODE + " text, " +
                ROLE_TEXT + " text, " +
                ROLE_DESCRIPTION + " text, " +
                "UNIQUE" + " (" + ROLE_CODE + " )" + ");";

        // EXECUTE THE strCreateStaffRolesTable TO CREATE THE TABLE
        db.execSQL(strCreateStaffRolesTable);

        /***** ADD A FEW DUMMY VALUES *****/
        ContentValues cv = new ContentValues();

        /** Administrator **/
        cv.put(ROLE_ID, 1);
        cv.put(ROLE_CODE, "admin");
        cv.put(ROLE_TEXT, "Administrator");
        cv.put(ROLE_DESCRIPTION, "For Users who need Administrative Privileges.");
        db.insert(STAFF_ROLES, null, cv);

        /** Stewards / Captains **/
        cv.put(ROLE_ID, 2);
        cv.put(ROLE_CODE, "steward");
        cv.put(ROLE_TEXT, "Steward");
        cv.put(ROLE_DESCRIPTION, "For Users who need Privileges granted for Steward / Captains.");
        db.insert(STAFF_ROLES, null, cv);

        /** Servers / Waiters **/
        cv.put(ROLE_ID, 3);
        cv.put(ROLE_CODE, "server");
        cv.put(ROLE_TEXT, "Server / Waiter");
        cv.put(ROLE_DESCRIPTION, "For Users who need Privileges granted for Servers / Waiters.");
        db.insert(STAFF_ROLES, null, cv);
    }

    /** CREATE A NEW STAFF TABLE **/
    private void createStaffTable(SQLiteDatabase db) {

        String strCreateStaffTable = "create table " + STAFF +
                " (" +
                STAFF_ID + " integer primary key autoincrement, " +
                STAFF_ROLE_ID + " text, " +
                STAFF_FULL_NAME + " text, " +
                STAFF_PHONE + " text, " +
                STAFF_USER_NAME + " text, " +
                STAFF_PASSWORD + " text, " +
                STAFF_PROFILE_PICTURE + " BLOB, " +
                "UNIQUE" + " (" + STAFF_USER_NAME + " )" + ");";

        // EXECUTE THE strCreateStaffTable TO CREATE THE TABLE
        db.execSQL(strCreateStaffTable);

        /***** ADD A FEW DUMMY VALUES *****/
        ContentValues cv = new ContentValues();

        /** SUPER ADMIN **/
        cv.put(STAFF_ROLE_ID, "1");
        cv.put(STAFF_FULL_NAME, "Admin");
        cv.put(STAFF_USER_NAME, "admin");
        cv.put(STAFF_PASSWORD, "1234");
        bArray = PNGConverter("default_admin.png");
        cv.put(STAFF_PROFILE_PICTURE, bArray);
        db.insert(STAFF, null, cv);
    }

    private void createOrderCartTable(SQLiteDatabase db) {

        String strCreateOrderCartTable = "create table " + ORDER_CART +
                " (" +
                ORDER_CART_ID + " integer primary key, " +
                ORDER_TABLE_ID + " integer, " +
                ORDER_MENU_ID + " integer, " +
                ORDER_QUANTITY + " integer, " +
                ORDER_STATUS + " boolean, " +
                ORDER_TIMESTAMP + " timestamp default current_timestamp," +
                "UNIQUE" + " (" + ORDER_CART_ID + " )" + ");";

        // EXECUTE THE strCreateOrderCartTable TO CREATE THE TABLE
        db.execSQL(strCreateOrderCartTable);
    }

    private void createRestaurantTable(SQLiteDatabase db) {

        String strCreateRestaurantTable = "create table " + RESTAURANT +
                " (" +
                RESTAURANT_ID + " integer primary key, " +
                RESTAURANT_NAME + " text, " +
                RESTAURANT_LOGO + " blob, " +
                RESTAURANT_ADDRESS_1 + " text, " +
                RESTAURANT_ADDRESS_2 + " text, " +
                RESTAURANT_CITY + " text, " +
                RESTAURANT_STATE + " text, " +
                RESTAURANT_COUNTRY + " text, " +
                RESTAURANT_ZIP + " text, " +
                RESTAURANT_PHONE + " text, " +
                RESTAURANT_EMAIL + " text, " +
                RESTAURANT_WEBSITE + " text, " +
                "UNIQUE" + " (" + RESTAURANT_ID + " )" + ");";

        // EXECUTE THE strCreateRestaurantTable TO CREATE THE TABLE
        db.execSQL(strCreateRestaurantTable);
    }

    private void createMealTypesTable(SQLiteDatabase db) {

        String strCreateMealTypesTable = "create table " + CATEGORY +
                " (" +
                CATEGORY_ID + " integer primary key autoincrement, " +
                CATEGORY_NAME + " text, " +
                CATEGORY_IMAGE + " BLOB, " +
                "UNIQUE" + " (" + CATEGORY_NAME + " )" + ");";

        // EXECUTE THE strCreateMealTypesTable TO CREATE THE TABLE
        db.execSQL(strCreateMealTypesTable);

//        /***** ADD A FEW DUMMY VALUES *****/
//        ContentValues cv = new ContentValues();
//
//        /** STARTERS **/
//        cv.put(CATEGORY_ID, Integer.valueOf("01"));
//        cv.put(CATEGORY_NAME, "Starters");
//        bArray = PNGConverter("Categories/starters.png");
//        cv.put(CATEGORY_IMAGE, bArray);
//        db.insert(CATEGORY, null, cv);
//
//        /** SALADS **/
//        cv.put(CATEGORY_ID, Integer.valueOf("02"));
//        cv.put(CATEGORY_NAME, "Salads");
//        bArray = PNGConverter("Categories/salads.png");
//        cv.put(CATEGORY_IMAGE, bArray);
//        db.insert(CATEGORY, null, cv);
//
//        /** SOUPS **/
//        cv.put(CATEGORY_ID, Integer.valueOf("03"));
//        cv.put(CATEGORY_NAME, "Soup");
//        bArray = PNGConverter("Categories/soups.png");
//        cv.put(CATEGORY_IMAGE, bArray);
//        db.insert(CATEGORY, null, cv);
//
//        /** VEGETARIAN **/
//        cv.put(CATEGORY_ID, Integer.valueOf("04"));
//        cv.put(CATEGORY_NAME, "Vegetarian");
//        bArray = PNGConverter("Categories/vegetarian.png");
//        cv.put(CATEGORY_IMAGE, bArray);
//        db.insert(CATEGORY, null, cv);
//
//        /** PASTA **/
//        cv.put(CATEGORY_ID, Integer.valueOf("05"));
//        cv.put(CATEGORY_NAME, "Pasta");
//        bArray = PNGConverter("Categories/pasta.png");
//        cv.put(CATEGORY_IMAGE, bArray);
//        db.insert(CATEGORY, null, cv);
//
//        /** PRAWNS **/
//        cv.put(CATEGORY_ID, Integer.valueOf("06"));
//        cv.put(CATEGORY_NAME, "Prawns");
//        bArray = PNGConverter("Categories/prawns.png");
//        cv.put(CATEGORY_IMAGE, bArray);
//        db.insert(CATEGORY, null, cv);
//
//        /** FISH **/
//        cv.put(CATEGORY_ID, Integer.valueOf("07"));
//        cv.put(CATEGORY_NAME, "Fish");
//        bArray = PNGConverter("Categories/fish.png");
//        cv.put(CATEGORY_IMAGE, bArray);
//        db.insert(CATEGORY, null, cv);
//
//        /** CHICKEN **/
//        cv.put(CATEGORY_ID, Integer.valueOf("08"));
//        cv.put(CATEGORY_NAME, "Chicken");
//        bArray = PNGConverter("Categories/chicken.png");
//        cv.put(CATEGORY_IMAGE, bArray);
//        db.insert(CATEGORY, null, cv);
//
//        /** TURKEY **/
//        cv.put(CATEGORY_ID, Integer.valueOf("09"));
//        cv.put(CATEGORY_NAME, "Turkey");
//        bArray = PNGConverter("Categories/turkey.png");
//        cv.put(CATEGORY_IMAGE, bArray);
//        db.insert(CATEGORY, null, cv);
//
//        /** LAMB **/
//        cv.put(CATEGORY_ID, Integer.valueOf("10"));
//        cv.put(CATEGORY_NAME, "Lamb");
//        bArray = PNGConverter("Categories/lamb.png");
//        cv.put(CATEGORY_IMAGE, bArray);
//        db.insert(CATEGORY, null, cv);
//
//        /** ORIENTAL **/
//        cv.put(CATEGORY_ID, Integer.valueOf("11"));
//        cv.put(CATEGORY_NAME, "Oriental");
//        bArray = PNGConverter("Categories/oriental.png");
//        cv.put(CATEGORY_IMAGE, bArray);
//        db.insert(CATEGORY, null, cv);
//
//        /** ITALIAN **/
//        cv.put(CATEGORY_ID, Integer.valueOf("12"));
//        cv.put(CATEGORY_NAME, "Italian");
//        bArray = PNGConverter("Categories/italian.png");
//        cv.put(CATEGORY_IMAGE, bArray);
//        db.insert(CATEGORY, null, cv);
//
//        /** SUSHI **/
//        cv.put(CATEGORY_ID, Integer.valueOf("13"));
//        cv.put(CATEGORY_NAME, "Sushi");
//        bArray = PNGConverter("Categories/sushi.png");
//        cv.put(CATEGORY_IMAGE, bArray);
//        db.insert(CATEGORY, null, cv);
//
//        /** CONTINENTAL **/
//        cv.put(CATEGORY_ID, Integer.valueOf("14"));
//        cv.put(CATEGORY_NAME, "Continental");
//        bArray = PNGConverter("Categories/continental.png");
//        cv.put(CATEGORY_IMAGE, bArray);
//        db.insert(CATEGORY, null, cv);
//
//        /** MEXICAN **/
//        cv.put(CATEGORY_ID, Integer.valueOf("15"));
//        cv.put(CATEGORY_NAME, "Mexican");
//        bArray = PNGConverter("Categories/mexican.png");
//        cv.put(CATEGORY_IMAGE, bArray);
//        db.insert(CATEGORY, null, cv);
//
//        /** MOCKTAILS **/
//        cv.put(CATEGORY_ID, Integer.valueOf("16"));
//        cv.put(CATEGORY_NAME, "Mocktails");
//        bArray = PNGConverter("Categories/mocktails.png");
//        cv.put(CATEGORY_IMAGE, bArray);
//        db.insert(CATEGORY, null, cv);
//
//        /** COCKTAILS **/
//        cv.put(CATEGORY_ID, Integer.valueOf("17"));
//        cv.put(CATEGORY_NAME, "Cocktails");
//        bArray = PNGConverter("Categories/cocktails.png");
//        cv.put(CATEGORY_IMAGE, bArray);
//        db.insert(CATEGORY, null, cv);
//
//        /** BEVERAGES **/
//        cv.put(CATEGORY_ID, Integer.valueOf("18"));
//        cv.put(CATEGORY_NAME, "Beverages");
//        bArray = PNGConverter("Categories/beverages.png");
//        cv.put(CATEGORY_IMAGE, bArray);
//        db.insert(CATEGORY, null, cv);
//
//        /** DESSERTS **/
//        cv.put(CATEGORY_ID, Integer.valueOf("19"));
//        cv.put(CATEGORY_NAME, "Desserts");
//        bArray = PNGConverter("Categories/desserts.png");
//        cv.put(CATEGORY_IMAGE, bArray);
//        db.insert(CATEGORY, null, cv);
    }

    private void createMealsTable(SQLiteDatabase db) {

        String strCreateMealsTable = "create table " + MENU +
                " (" +
                MENU_ID + " integer primary key autoincrement, " +
                MENU_CATEGORY_ID + " integer, " +
                MENU_NAME + " text, " +
                MENU_DESCRIPTION + " text, " +
                MENU_PRICE + " text, " +
                MENU_TYPE + " text, " +
                MENU_SERVES + " text, " +
                MENU_IMAGE + " BLOB, " +
                "UNIQUE" + " (" + MENU_NAME + " )" + ");";

        // EXECUTE THE strCreateMealsTable TO CREATE THE TABLE
        db.execSQL(strCreateMealsTable);

//        /***** ADD A FEW DUMMY VALUES *****/
//        ContentValues cv = new ContentValues();
//
//		/* STARTERS */
//        /** Stuffed Mushrooms **/
//        cv.put(MENU_NAME, "Stuffed Mushrooms");
//        cv.put(MENU_DESCRIPTION, "These delicious mushrooms taste just like restaurant-style stuffed mushrooms and are my guy's absolute favorite.");
//        bArray = JPGConverter("Starters/stuffed_mushrooms.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "200.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("01"));
//        db.insert(MENU, null, cv);
//
//        /** Double Tomato Bruschetta **/
//        cv.put(MENU_NAME, "Double Tomato Bruschetta");
//        cv.put(MENU_DESCRIPTION, "A delicious and easy appetizer. The balsamic vinegar gives it a little bite. Dried basil can be substituted but it is best with fresh.");
//        bArray = JPGConverter("Starters/double_tomato_bruschetta.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "200.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("01"));
//        db.insert(MENU, null, cv);
//
//        /** Marinated Grilled Shrimp **/
//        cv.put(MENU_NAME, "Marinated Grilled Shrimp");
//        cv.put(MENU_DESCRIPTION, "A very simple and easy marinade that makes your shrimp so yummy you don't even need cocktail sauce! Don't let the cayenne pepper scare you, you don't even taste it. Try it with a salad, baked potato, and garlic bread. You will not be disappointed!");
//        bArray = JPGConverter("Starters/marinated_grilled_shrimp.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "470.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("01"));
//        db.insert(MENU, null, cv);
//
//        /** Cocktail Meatballs **/
//        cv.put(MENU_NAME, "Cocktail Meatballs");
//        cv.put(MENU_DESCRIPTION, "These tasty meatballs will disappear quickly from your plates!");
//        bArray = JPGConverter("Starters/cocktail_meatballs.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "300.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("01"));
//        db.insert(MENU, null, cv);
//
//        /** Brown Sugar Smokies **/
//        cv.put(MENU_NAME, "Brown Sugar Smokies");
//        cv.put(MENU_DESCRIPTION, "Bacon-wrapped yummies!");
//        bArray = JPGConverter("Starters/brown_sugar_smokies.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "380.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("01"));
//        db.insert(MENU, null, cv);
//
//		/* SALADS */
//        /** Asian Salad **/
//        cv.put(MENU_NAME, "Asian Salad");
//        cv.put(MENU_DESCRIPTION, "This salad is appreciated by everyone because of its unique blend of flavors.");
//        bArray = JPGConverter("Salads/asian_salad.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "200.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("02"));
//        db.insert(MENU, null, cv);
//
//        /** Authentic German Potato Salad **/
//        cv.put(MENU_NAME, "Authentic German Potato Salad");
//        cv.put(MENU_DESCRIPTION, "");
//        bArray = JPGConverter("Salads/authentic_german_potato_salad.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "290.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("02"));
//        db.insert(MENU, null, cv);
//
//        /** Strawberry Avocado Salad **/
//        cv.put(MENU_NAME, "Strawberry Avocado Salad");
//        cv.put(MENU_DESCRIPTION, "This salad is appreciated by everyone because of its unique blend of flavors.");
//        bArray = JPGConverter("Salads/strawberry_avocado_salad.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "250.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("02"));
//        db.insert(MENU, null, cv);
//
//        /** Spinach and Orzo Salad **/
//        cv.put(MENU_NAME, "Spinach and Orzo Salad");
//        cv.put(MENU_DESCRIPTION, "A light, easy to make salad that's pleasing to the palate.");
//        bArray = JPGConverter("Salads/spinach_and_orzo_salad.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "210.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("02"));
//        db.insert(MENU, null, cv);
//
//        /** Creamy Spiced Coleslaw **/
//        cv.put(MENU_NAME, "Creamy Spiced Coleslaw");
//        cv.put(MENU_DESCRIPTION, "A creamy coleslaw with lots of flavor. The seasonings complement each other and the cabbage.");
//        bArray = JPGConverter("Salads/creamy_spiced_coleslaw.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "350.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("02"));
//        db.insert(MENU, null, cv);
//
//		/* SOUPS */
//        /** Absolutely Ultimate Potato Soup **/
//        cv.put(MENU_NAME, "Absolutely Ultimate Potato Soup");
//        cv.put(MENU_DESCRIPTION, "");
//        bArray = JPGConverter("Soups/absolutely_ultimate_potato_soup.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "110.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("03"));
//        db.insert(MENU, null, cv);
//
//        /** Bean Soup With Kale **/
//        cv.put(MENU_NAME, "Bean Soup With Kale");
//        cv.put(MENU_DESCRIPTION, "");
//        bArray = JPGConverter("Soups/bean_soup_with_kale.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "110.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("03"));
//        db.insert(MENU, null, cv);
//
//        /** Cream of Mushroom Soup **/
//        cv.put(MENU_NAME, "Cream of Mushroom Soup");
//        cv.put(MENU_DESCRIPTION, "");
//        bArray = JPGConverter("Soups/cream_of_mushroom_soup.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "150.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("03"));
//        db.insert(MENU, null, cv);
//
//        /** Broccoli Cheese Soup **/
//        cv.put(MENU_NAME, "Broccoli Cheese Soup");
//        cv.put(MENU_DESCRIPTION, "This is a great, very flavorful soup. Good for serving at luncheons or special gatherings with a quiche.");
//        bArray = JPGConverter("Soups/broccoli_cheese_soup.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "160.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("03"));
//        db.insert(MENU, null, cv);
//
//        /** Potato Leek Soup **/
//        cv.put(MENU_NAME, "Potato Leek Soup");
//        cv.put(MENU_DESCRIPTION, "A creamy soup with a strong leek flavor. Goes wonderfully with sourdough bread.");
//        bArray = JPGConverter("Soups/potato_leek_soup.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "140.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("03"));
//        db.insert(MENU, null, cv);
//
//		/* Vegetarian */
//        /** Skillet Zucchini **/
//        cv.put(MENU_NAME, "Skillet Zucchini");
//        cv.put(MENU_DESCRIPTION, "A one-skillet dish that makes a wonderful side or meal in itself.");
//        bArray = JPGConverter("Vegetarian/skillet_zucchini.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "230.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("04"));
//        db.insert(MENU, null, cv);
//
//        /** Old Fashioned Mac and Cheese **/
//        cv.put(MENU_NAME, "Old Fashioned Mac and Cheese");
//        cv.put(MENU_DESCRIPTION, "This is a classic recipe for macaroni and cheese. The kids will love this!");
//        bArray = JPGConverter("Vegetarian/old_fashioned_mac_and_cheese.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "250.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("04"));
//        db.insert(MENU, null, cv);
//
//        /** Lemon Orzo Primavera **/
//        cv.put(MENU_NAME, "Lemon Orzo Primavera");
//        cv.put(MENU_DESCRIPTION, "Colorful vegetables and the flavors of lemon and thyme make this orzo dish great.");
//        bArray = JPGConverter("Vegetarian/lemon_orzo_primavera.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "265.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("04"));
//        db.insert(MENU, null, cv);
//
//        /** Eggplant Parmesan **/
//        cv.put(MENU_NAME, "Eggplant Parmesan");
//        cv.put(MENU_DESCRIPTION, "");
//        bArray = JPGConverter("Vegetarian/eggplant_parmesan.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "245.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("04"));
//        db.insert(MENU, null, cv);
//
//        /** Hearty Vegetable Lasagna **/
//        cv.put(MENU_NAME, "Hearty Vegetable Lasagna");
//        cv.put(MENU_DESCRIPTION, "");
//        bArray = JPGConverter("Vegetarian/hearty_vegetable_lasagna.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "350.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("04"));
//        db.insert(MENU, null, cv);
//
//		/* Pasta */
//        /** Baked Spaghetti **/
//        cv.put(MENU_NAME, "Baked Spaghetti");
//        cv.put(MENU_DESCRIPTION, "");
//        bArray = JPGConverter("Pasta/baked_spaghetti.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "360.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("05"));
//        db.insert(MENU, null, cv);
//
//        /** Macaroni and Cheese **/
//        cv.put(MENU_NAME, "Macaroni and Cheese");
//        cv.put(MENU_DESCRIPTION, "");
//        bArray = JPGConverter("Pasta/macaroni_and_cheese.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "315.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("05"));
//        db.insert(MENU, null, cv);
//
//        /** Spinach Tomato Tortellini **/
//        cv.put(MENU_NAME, "Spinach Tomato Tortellini");
//        cv.put(MENU_DESCRIPTION, "");
//        bArray = JPGConverter("Pasta/spinach_tomato_tortellini.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "360.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("05"));
//        db.insert(MENU, null, cv);
//
//        /** Baked Ziti **/
//        cv.put(MENU_NAME, "Baked Ziti");
//        cv.put(MENU_DESCRIPTION, "");
//        bArray = JPGConverter("Pasta/baked_ziti.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "380.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("05"));
//        db.insert(MENU, null, cv);
//
//        /** Bow Ties with Sausage, Tomatoes and Cream **/
//        cv.put(MENU_NAME, "Bow Ties with Sausage, Tomatoes and Cream");
//        cv.put(MENU_DESCRIPTION, "");
//        bArray = JPGConverter("Pasta/bow_ties_with_sausage_tomatoes_and_cream.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "400.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("05"));
//        db.insert(MENU, null, cv);
//
//		/* Prawns */
//        /** Shrimp Lemon Pepper Linguini **/
//        cv.put(MENU_NAME, "Shrimp Lemon Pepper Linguini");
//        cv.put(MENU_DESCRIPTION, "So easy to make, done in minutes, and absolutely delicious. ");
//        bArray = JPGConverter("Prawns/shrimp_lemon_pepper_linguini.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "360.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("06"));
//        db.insert(MENU, null, cv);
//
//        /** Ginger Shrimp and Broccoli with Garlic **/
//        cv.put(MENU_NAME, "Ginger Shrimp and Broccoli with Garlic");
//        cv.put(MENU_DESCRIPTION, "Parchment packets filled with shrimp and broccoli florets and seasoned with an Asian-inspired ginger sauce are baked for a delicious and easy-clean-up dinner.");
//        bArray = JPGConverter("Prawns/ginger_shrimp_and_broccoli_with_garlic.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "405.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("06"));
//        db.insert(MENU, null, cv);
//
//        /** Shrimp Scampi **/
//        cv.put(MENU_NAME, "Shrimp Scampi");
//        cv.put(MENU_DESCRIPTION, "Easy version of this classic with the wonderful 'zip' of Dijon-style mustard.");
//        bArray = JPGConverter("Prawns/shrimp_scampi.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "470.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("06"));
//        db.insert(MENU, null, cv);
//
//        /** Shrimp and Cheese Grits **/
//        cv.put(MENU_NAME, "Shrimp and Cheese Grits");
//        cv.put(MENU_DESCRIPTION, "This is a quick, delicious recipe that showcases shrimp AND grits at their best.");
//        bArray = JPGConverter("Prawns/shrimp_and_cheese_grits.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "405.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("06"));
//        db.insert(MENU, null, cv);
//
//        /** Shrimp and Tomato Broil **/
//        cv.put(MENU_NAME, "Shrimp and Tomato Broil");
//        cv.put(MENU_DESCRIPTION, "This is a fast recipe that always gets great reviews.");
//        bArray = JPGConverter("Prawns/shrimp_and_tomato_broil.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "400.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("06"));
//        db.insert(MENU, null, cv);
//
//		/* Fish */
//        /** Ginger Glazed Mahi Mahi **/
//        cv.put(MENU_NAME, "Ginger Glazed Mahi Mahi");
//        cv.put(MENU_DESCRIPTION, "This Ginger Glazed Mahi Mahi is bursting with flavor and combines both sweet and sour taste sensations.");
//        bArray = JPGConverter("Fish/ginger_glazed_mahi_mahi.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "560.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("07"));
//        db.insert(MENU, null, cv);
//
//        /** Baked Salmon **/
//        cv.put(MENU_NAME, "Baked Salmon");
//        cv.put(MENU_DESCRIPTION, "");
//        bArray = JPGConverter("Fish/baked_salmon.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "405.00");
//        cv.put(CATEGORY_ID, Integer.valueOf("07"));
//        db.insert(MENU, null, cv);
//
//        /** Savory Crab Stuffed Mushrooms **/
//        cv.put(MENU_NAME, "Savory Crab Stuffed Mushrooms");
//        cv.put(MENU_DESCRIPTION, "Baked mushroom caps filled with a deliciously cheesy crabmeat mixture.");
//        bArray = JPGConverter("Fish/savory_crab_stuffed_mushrooms.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "470.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("07"));
//        db.insert(MENU, null, cv);
//
//        /** Clams And Garlic **/
//        cv.put(MENU_NAME, "Clams And Garlic");
//        cv.put(MENU_DESCRIPTION, "So simple, but so good - steamed clams served in their own liqueur. Tastes even better when served with a crusty Italian bread, or over pasta.");
//        bArray = JPGConverter("Fish/clams_and_garlic.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "465.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("07"));
//        db.insert(MENU, null, cv);
//
//        /** Pasta with Scallops, Zucchini, and Tomatoes **/
//        cv.put(MENU_NAME, "Pasta with Scallops, Zucchini, and Tomatoes");
//        cv.put(MENU_DESCRIPTION, "");
//        bArray = JPGConverter("Fish/pasta_with_scallops_zucchini_and_tomatoes.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "410.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("07"));
//        db.insert(MENU, null, cv);
//
//		/* Chicken */
//        /** Spicy Rapid Roast Chicken **/
//        cv.put(MENU_NAME, "Spicy Rapid Roast Chicken");
//        cv.put(MENU_DESCRIPTION, "");
//        bArray = JPGConverter("Chicken/spicy_rapid_roast_chicken.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "470.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("08"));
//        db.insert(MENU, null, cv);
//
//        /** Chicken Marsala **/
//        cv.put(MENU_NAME, "Chicken Marsala");
//        cv.put(MENU_DESCRIPTION, "");
//        bArray = JPGConverter("Chicken/chicken_marsala.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "380.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("08"));
//        db.insert(MENU, null, cv);
//
//        /** Buffalo Chicken Wings **/
//        cv.put(MENU_NAME, "Buffalo Chicken Wings");
//        cv.put(MENU_DESCRIPTION, "");
//        bArray = JPGConverter("Chicken/buffalo_chicken_wings.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "470.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("08"));
//        db.insert(MENU, null, cv);
//
//        /** Caramelized Baked Chicken **/
//        cv.put(MENU_NAME, "Caramelized Baked Chicken");
//        cv.put(MENU_DESCRIPTION, "");
//        bArray = JPGConverter("Chicken/caramelized_baked_chicken.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "480.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("08"));
//        db.insert(MENU, null, cv);
//
//        /** Rosemary Chicken with Orange-Maple Glaze **/
//        cv.put(MENU_NAME, "Rosemary Chicken with Orange-Maple Glaze");
//        cv.put(MENU_DESCRIPTION, "");
//        bArray = JPGConverter("Chicken/rosemary_chicken_with_orange_maple_glaze.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "480.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("08"));
//        db.insert(MENU, null, cv);
//
//		/* Turkey */
//        /** Maple Basted Roast Turkey with Cranberry Pan Gravy **/
//        cv.put(MENU_NAME, "Maple Basted Roast Turkey with Cranberry Pan Gravy");
//        cv.put(MENU_DESCRIPTION, "Roast turkey is basted with maple syrup for a sweet glaze; cranberry juice and sweetened dried cranberries bring a sweet-tart fruit dimension to the pan gravy.");
//        bArray = JPGConverter("Turkey/maple_basted_roast_turkey_with_cranberry_pan_gravy.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "560.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("09"));
//        db.insert(MENU, null, cv);
//
//        /** Grilled Turkey Cuban Sandwiches **/
//        cv.put(MENU_NAME, "Grilled Turkey Cuban Sandwiches");
//        cv.put(MENU_DESCRIPTION, "Grilled turkey breast, ham, cheese and pickles in a panini-style sandwich.");
//        bArray = JPGConverter("Turkey/grilled_turkey_cuban_sandwiches.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "500.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("09"));
//        db.insert(MENU, null, cv);
//
//        /** Smoked Turkey **/
//        cv.put(MENU_NAME, "Smoked Turkey");
//        cv.put(MENU_DESCRIPTION, "");
//        bArray = JPGConverter("Turkey/smoked_turkey.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "520.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("09"));
//        db.insert(MENU, null, cv);
//
//        /** Roast Turkey With Tasty Chestnut Stuffing **/
//        cv.put(MENU_NAME, "Roast Turkey With Tasty Chestnut Stuffing");
//        cv.put(MENU_DESCRIPTION, "Simple but seriously yummy.");
//        bArray = JPGConverter("Turkey/roast_turkey_with_tasty_chestnut_stuffing.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "650.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("09"));
//        db.insert(MENU, null, cv);
//
//        /** Cola Roast Turkey **/
//        cv.put(MENU_NAME, "Cola Roast Turkey");
//        cv.put(MENU_DESCRIPTION, "");
//        bArray = JPGConverter("Turkey/cola_roast_turkey.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "575.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("09"));
//        db.insert(MENU, null, cv);
//
//		/* Lamb */
//        /** Butter Lamb Gravy **/
//        cv.put(MENU_NAME, "Butter Lamb Gravy");
//        cv.put(MENU_DESCRIPTION, "Lamb is simmered in a spicy tomato and cream sauce. This is a very mouthwatering dish that is easy to make.");
//        bArray = JPGConverter("Lamb/butter_lamb_gravy.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "360.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("10"));
//        db.insert(MENU, null, cv);
//
//        /** Baked Lamb Chops **/
//        cv.put(MENU_NAME, "Baked Lamb Chops");
//        cv.put(MENU_DESCRIPTION, "These are very tasty, and make for an easy main meal to prepare. Try serving them with mashed potatoes, peas and pumpkin.");
//        bArray = JPGConverter("Lamb/baked_lamb_chops.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "300.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("10"));
//        db.insert(MENU, null, cv);
//
//        /** Stout-Braised Lamb Shanks **/
//        cv.put(MENU_NAME, "Stout Braised Lamb Shanks");
//        cv.put(MENU_DESCRIPTION, "");
//        bArray = JPGConverter("Lamb/stout_braised_lamb_shanks.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "360.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("10"));
//        db.insert(MENU, null, cv);
//
//        /** Roast Leg of Lamb with Rosemary **/
//        cv.put(MENU_NAME, "Roast Leg of Lamb with Rosemary");
//        cv.put(MENU_DESCRIPTION, "This leg of lamb is marinated overnight with fresh rosemary, garlic, mustard, honey and lemon zest.");
//        bArray = JPGConverter("Lamb/roast_leg_of_lamb_with_rosemary.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "470.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("10"));
//        db.insert(MENU, null, cv);
//
//        /** Grilled Lamb with Brown Sugar Glaze **/
//        cv.put(MENU_NAME, "Grilled Lamb with Brown Sugar Glaze");
//        cv.put(MENU_DESCRIPTION, "Sweet and savory, perfect for a spring meal with noodles and a green vegetable.");
//        bArray = JPGConverter("Lamb/grilled_lamb_with_brown_sugar_glaze.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "380.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("10"));
//        db.insert(MENU, null, cv);
//
//		/* Chinese */
//        /** Chinese Spareribs **/
//        cv.put(MENU_NAME, "Chinese Spareribs");
//        cv.put(MENU_DESCRIPTION, "");
//        bArray = JPGConverter("Chinese/chinese_spareribs.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "300.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("11"));
//        db.insert(MENU, null, cv);
//
//        /** Chinese Broccoli Slaw **/
//        cv.put(MENU_NAME, "Chinese Broccoli Slaw");
//        cv.put(MENU_DESCRIPTION, "");
//        bArray = JPGConverter("Chinese/chinese_broccoli_slaw.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "290.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("11"));
//        db.insert(MENU, null, cv);
//
//        /** Sweet and Sour Chicken **/
//        cv.put(MENU_NAME, "Sweet and Sour Chicken");
//        cv.put(MENU_DESCRIPTION, "Pan fried chicken cubes served with a sweet and sour sauce.");
//        bArray = JPGConverter("Chinese/sweet_and_sour_chicken.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "250.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("11"));
//        db.insert(MENU, null, cv);
//
//        /** Szechwan Shrimp **/
//        cv.put(MENU_NAME, "Szechwan Shrimp");
//        cv.put(MENU_DESCRIPTION, "");
//        bArray = JPGConverter("Chinese/szechwan_shrimp.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "365.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("11"));
//        db.insert(MENU, null, cv);
//
//        /** Kung Pao Chicken **/
//        cv.put(MENU_NAME, "Kung Pao Chicken");
//        cv.put(MENU_DESCRIPTION, "");
//        bArray = JPGConverter("Chinese/kung_pao_chicken.jpg");
//        cv.put(MENU_IMAGE, bArray);
//        cv.put(MENU_PRICE, "250.00");
//        cv.put(MENU_CATEGORY_ID, Integer.valueOf("11"));
//        db.insert(MENU, null, cv);
    }

    private void createTablesTable(SQLiteDatabase db) {

        String strCreateTablesTable = "create table " + TABLES +
                " (" +
                TABLE_ID + " integer primary key, " +
                TABLE_SEATS + " text, " +
                TABLE_OCCUPANCY + " text, " +
                "UNIQUE" + " (" + TABLE_ID + " )" + ");";

        // EXECUTE THE strCreateTablesTable TO CREATE THE TABLE
        db.execSQL(strCreateTablesTable);
    }

    private void createTaxesTable(SQLiteDatabase db) {

        String strCreateTaxesTable = "create table " + TAXES +
                " (" +
                TAX_ID + " integer primary key autoincrement, " +
                TAX_NAME + " text, " +
                TAX_REGISTRATION + " text, " +
                TAX_PERCENTAGE + " text, " +
                TAX_ENTIRE_AMOUNT + " text, " +
                TAX_TAXABLE_PERCENTAGE + " text, " +
                "UNIQUE" + " (" + TAX_ID + " )" + ");";

        // EXECUTE THE strCreateTaxesTable TO CREATE THE TABLE
        db.execSQL(strCreateTaxesTable);
    }

    private void createCountriesTable(SQLiteDatabase db) {

        String strCreateCountriesTable = "create table " + COUNTRIES +
                " (" +
                COUNTRY_ID + " integer primary key autoincrement, " +
                COUNTRY_NAME + " text, " +
                "UNIQUE" + " (" + COUNTRY_NAME + " )" + ");";

        // EXECUTE THE strCreateCountriesTable TO CREATE THE TABLE
        db.execSQL(strCreateCountriesTable);
    }

    private void createCurrencyTable(SQLiteDatabase db) {

        String strCreateCurrencyTable = "create table " + CURRENCY +
                " (" +
                CURRENCY_ID + " integer primary key, " +
                CURRENCY_NAME + " text, " +
                CURRENCY_ISO_CODE + " text, " +
                CURRENCY_SYMBOL + " text, " +
                "UNIQUE" + " (" + CURRENCY_ISO_CODE + " )" + ");";

        // EXECUTE THE strCreateCurrencyTable TO CREATE THE TABLE
        db.execSQL(strCreateCurrencyTable);

        /***** ADD A FEW DUMMY VALUES *****/
        ContentValues cv = new ContentValues();

        cv.put(CURRENCY_NAME, "Albania Lek");
        cv.put(CURRENCY_ISO_CODE, "ALL");
        String strAlbaniaLek =
                context.getResources().getString(R.string.albania_lek_1) +
                        context.getResources().getString(R.string.albania_lek_2) +
                        context.getResources().getString(R.string.albania_lek_3);
        cv.put(CURRENCY_SYMBOL, strAlbaniaLek);
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Afghanistan Afghani");
        cv.put(CURRENCY_ISO_CODE, "AFN");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.afghanistan_afghani));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Argentina Peso");
        cv.put(CURRENCY_ISO_CODE, "ARS");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.argentina_peso));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Aruba Guilder");
        cv.put(CURRENCY_ISO_CODE, "AWG");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.aruba_guilder));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Australia Dollar");
        cv.put(CURRENCY_ISO_CODE, "AUD");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.australia_dollar));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Azerbaijan New Manat");
        cv.put(CURRENCY_ISO_CODE, "AZN");
        String strAzerbaijanNewManat =
                context.getResources().getString(R.string.azerbaijan_new_manat_1) +
                        context.getResources().getString(R.string.azerbaijan_new_manat_2) +
                        context.getResources().getString(R.string.azerbaijan_new_manat_3);
        cv.put(CURRENCY_SYMBOL, strAzerbaijanNewManat);
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Bahamas Dollar");
        cv.put(CURRENCY_ISO_CODE, "BSD");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.bahamas_dollar));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Barbados Dollar");
        cv.put(CURRENCY_ISO_CODE, "BBD");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.barbados_dollar));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Belarus Ruble");
        cv.put(CURRENCY_ISO_CODE, "BYR");
        String strBelarusRuble =
                context.getResources().getString(R.string.belarus_ruble_1) +
                        context.getResources().getString(R.string.belarus_ruble_2);
        cv.put(CURRENCY_SYMBOL, strBelarusRuble);
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Belize Dollar");
        cv.put(CURRENCY_ISO_CODE, "BZD");
        String strBelizeDollar =
                context.getResources().getString(R.string.belize_dollar_1) +
                        context.getResources().getString(R.string.belize_dollar_2) +
                        context.getResources().getString(R.string.belize_dollar_3);
        cv.put(CURRENCY_SYMBOL, strBelizeDollar);
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Bermuda Dollar");
        cv.put(CURRENCY_ISO_CODE, "BMD");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.bermuda_dollar));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Bolivia Boliviano");
        cv.put(CURRENCY_ISO_CODE, "BOB");
        String strBoliviaBoliviano =
                context.getResources().getString(R.string.bolivia_boliviano_1) +
                        context.getResources().getString(R.string.bolivia_boliviano_2);
        cv.put(CURRENCY_SYMBOL, strBoliviaBoliviano);
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Bosnia and Herzegovina Convertible Marka");
        cv.put(CURRENCY_ISO_CODE, "BAM");
        String strBosniaAndHerzegovinaConvertibleMarka =
                context.getResources().getString(R.string.bosnia_and_herzegovina_convertible_marka_1) +
                        context.getResources().getString(R.string.bosnia_and_herzegovina_convertible_marka_2);
        cv.put(CURRENCY_SYMBOL, strBosniaAndHerzegovinaConvertibleMarka);
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Botswana Pula");
        cv.put(CURRENCY_ISO_CODE, "BWP");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.botswana_pula));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Bulgaria Lev");
        cv.put(CURRENCY_ISO_CODE, "BGN");
        String strBulgariaLev =
                context.getResources().getString(R.string.bulgaria_lev_1) +
                        context.getResources().getString(R.string.bulgaria_lev_2);
        cv.put(CURRENCY_SYMBOL, strBulgariaLev);
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Brazil Real");
        cv.put(CURRENCY_ISO_CODE, "BRL");
        String strBrazilReal =
                context.getResources().getString(R.string.brazil_real_1) +
                        context.getResources().getString(R.string.brazil_real_2);
        cv.put(CURRENCY_SYMBOL, strBrazilReal);
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Brunei Darussalam Dollar");
        cv.put(CURRENCY_ISO_CODE, "BND");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.brunei_darussalam_dollar));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Cambodia Riel");
        cv.put(CURRENCY_ISO_CODE, "KHR");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.cambodia_riel));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Canada Dollar");
        cv.put(CURRENCY_ISO_CODE, "CAD");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.canada_dollar));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Cayman Islands Dollar");
        cv.put(CURRENCY_ISO_CODE, "KYD");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.cayman_islands_dollar));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Chile Peso");
        cv.put(CURRENCY_ISO_CODE, "CLP");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.chile_peso));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "China Yuan Renminbi");
        cv.put(CURRENCY_ISO_CODE, "CNY");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.china_yuan_renminbi));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Colombia Peso");
        cv.put(CURRENCY_ISO_CODE, "COP");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.colombia_peso));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Costa Rica Colon");
        cv.put(CURRENCY_ISO_CODE, "CRC");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.costa_rica_colon));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Croatia Kuna");
        cv.put(CURRENCY_ISO_CODE, "HRK");
        String strCroatiaKuna =
                context.getResources().getString(R.string.croatia_kuna_1) +
                        context.getResources().getString(R.string.croatia_kuna_2);
        cv.put(CURRENCY_SYMBOL, strCroatiaKuna);
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Cuba Peso");
        cv.put(CURRENCY_ISO_CODE, "CUP");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.cuba_peso));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Czech Republic Koruna");
        cv.put(CURRENCY_ISO_CODE, "CZK");
        String strCzechRepublicKoruna =
                context.getResources().getString(R.string.czech_republic_koruna_1) +
                        context.getResources().getString(R.string.czech_republic_koruna_2);
        cv.put(CURRENCY_SYMBOL, strCzechRepublicKoruna);
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Denmark Krone");
        cv.put(CURRENCY_ISO_CODE, "DKK");
        String strDenmarkKrone =
                context.getResources().getString(R.string.denmark_krone_1) +
                        context.getResources().getString(R.string.denmark_krone_2);
        cv.put(CURRENCY_SYMBOL, strDenmarkKrone);
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Dominican Republic Peso");
        cv.put(CURRENCY_ISO_CODE, "DOP");
        String strDominicanRepublicPeso =
                context.getResources().getString(R.string.dominican_republic_peso_1) +
                        context.getResources().getString(R.string.dominican_republic_peso_2) +
                        context.getResources().getString(R.string.dominican_republic_peso_3);
        cv.put(CURRENCY_SYMBOL, strDominicanRepublicPeso);
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "East Caribbean Dollar");
        cv.put(CURRENCY_ISO_CODE, "XCD");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.east_caribbean_dollar));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Egypt Pound");
        cv.put(CURRENCY_ISO_CODE, "EGP");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.egypt_pound));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "El Salvador Colon");
        cv.put(CURRENCY_ISO_CODE, "SVC");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.el_salvador_colon));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Estonia Kroon");
        cv.put(CURRENCY_ISO_CODE, "EEK");
        String strEstoniaKroon =
                context.getResources().getString(R.string.estonia_kroon_1) +
                        context.getResources().getString(R.string.estonia_kroon_2);
        cv.put(CURRENCY_SYMBOL, strEstoniaKroon);
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Euro Member Countries");
        cv.put(CURRENCY_ISO_CODE, "EUR");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.euro_member_countries));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Falkland Islands (Malvinas) Pound");
        cv.put(CURRENCY_ISO_CODE, "FKP");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.falkland_islands_malvinas_pound));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Fiji Dollar");
        cv.put(CURRENCY_ISO_CODE, "FJD");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.fiji_dollar));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Ghana Cedis");
        cv.put(CURRENCY_ISO_CODE, "GHC");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.ghana_cedis));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Gibraltar Pound");
        cv.put(CURRENCY_ISO_CODE, "GIP");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.gibraltar_pound));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Guatemala Quetzal");
        cv.put(CURRENCY_ISO_CODE, "GTQ");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.guatemala_quetzal));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Guernsey Pound");
        cv.put(CURRENCY_ISO_CODE, "GGP");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.guernsey_pound));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Guyana Dollar");
        cv.put(CURRENCY_ISO_CODE, "GYD");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.guyana_dollar));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Honduras Lempira");
        cv.put(CURRENCY_ISO_CODE, "HNL");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.honduras_lempira));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Hong Kong Dollar");
        cv.put(CURRENCY_ISO_CODE, "HKD");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.hong_kong_dollar));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Hungary Forint");
        cv.put(CURRENCY_ISO_CODE, "HUF");
        String strHungaryForint =
                context.getResources().getString(R.string.hungary_forint_1) +
                        context.getResources().getString(R.string.hungary_forint_2);
        cv.put(CURRENCY_SYMBOL, strHungaryForint);
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Iceland Krona");
        cv.put(CURRENCY_ISO_CODE, "ISK");
        String strIcelandKrona =
                context.getResources().getString(R.string.iceland_krona_1) +
                        context.getResources().getString(R.string.iceland_krona_2);
        cv.put(CURRENCY_SYMBOL, strIcelandKrona);
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "India Rupee");
        cv.put(CURRENCY_ISO_CODE, "INR");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.india_rupee));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Indonesia Rupiah");
        cv.put(CURRENCY_ISO_CODE, "IDR");
        String strIndonesiaRupiah =
                context.getResources().getString(R.string.indonesia_rupiah_1) +
                        context.getResources().getString(R.string.indonesia_rupiah_2);
        cv.put(CURRENCY_SYMBOL, strIndonesiaRupiah);
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Iran Rial");
        cv.put(CURRENCY_ISO_CODE, "IRR");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.iran_rial));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Isle of Man Pound");
        cv.put(CURRENCY_ISO_CODE, "IMP");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.isle_of_man_pound));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Israel Shekel");
        cv.put(CURRENCY_ISO_CODE, "ILS");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.israel_shekel));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Jamaica Dollar");
        cv.put(CURRENCY_ISO_CODE, "JMD");
        String strJamaicaDollar =
                context.getResources().getString(R.string.jamaica_dollar_1) +
                        context.getResources().getString(R.string.jamaica_dollar_2);
        cv.put(CURRENCY_SYMBOL, strJamaicaDollar);
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Japan Yen");
        cv.put(CURRENCY_ISO_CODE, "JPY");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.japan_yen));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Jersey Pound");
        cv.put(CURRENCY_ISO_CODE, "JEP");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.jersey_pound));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Kazakhstan Tenge");
        cv.put(CURRENCY_ISO_CODE, "KZT");
        String strKazakhstanTenge =
                context.getResources().getString(R.string.kazakhstan_tenge_1) +
                        context.getResources().getString(R.string.kazakhstan_tenge_2);
        cv.put(CURRENCY_SYMBOL, strKazakhstanTenge);
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Korea (North) Won");
        cv.put(CURRENCY_ISO_CODE, "KPW");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.korea_north_won));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Korea (South) Won");
        cv.put(CURRENCY_ISO_CODE, "KRW");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.korea_south_won));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Kyrgyzstan Som");
        cv.put(CURRENCY_ISO_CODE, "KGS");
        String strKyrgyzstanSom =
                context.getResources().getString(R.string.kyrgyzstan_som_1) +
                        context.getResources().getString(R.string.kyrgyzstan_som_2);
        cv.put(CURRENCY_SYMBOL, strKyrgyzstanSom);
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Laos Kip");
        cv.put(CURRENCY_ISO_CODE, "LAK");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.laos_kip));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Latvia Lat");
        cv.put(CURRENCY_ISO_CODE, "LVL");
        String strLatviaLat =
                context.getResources().getString(R.string.latvia_lat_1) +
                        context.getResources().getString(R.string.latvia_lat_2);
        cv.put(CURRENCY_SYMBOL, strLatviaLat);
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Lebanon Pound");
        cv.put(CURRENCY_ISO_CODE, "LBP");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.lebanon_pound));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Liberia Dollar");
        cv.put(CURRENCY_ISO_CODE, "LRD");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.liberia_dollar));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Lithuania Litas");
        cv.put(CURRENCY_ISO_CODE, "LTL");
        String strLithuaniaLitas =
                context.getResources().getString(R.string.lithuania_litas_1) +
                        context.getResources().getString(R.string.lithuania_litas_2);
        cv.put(CURRENCY_SYMBOL, strLithuaniaLitas);
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Macedonia Denar");
        cv.put(CURRENCY_ISO_CODE, "MKD");
        String strMacedoniaDenar =
                context.getResources().getString(R.string.macedonia_denar_1) +
                        context.getResources().getString(R.string.macedonia_denar_2) +
                        context.getResources().getString(R.string.macedonia_denar_3);
        cv.put(CURRENCY_SYMBOL, strMacedoniaDenar);
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Malaysia Ringgit");
        cv.put(CURRENCY_ISO_CODE, "MYR");
        String strMalaysiaRinggit =
                context.getResources().getString(R.string.malaysia_ringgit_1) +
                        context.getResources().getString(R.string.malaysia_ringgit_2);
        cv.put(CURRENCY_SYMBOL, strMalaysiaRinggit);
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Mauritius Rupee");
        cv.put(CURRENCY_ISO_CODE, "MUR");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.mauritius_rupee));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Mexico Peso");
        cv.put(CURRENCY_ISO_CODE, "MXN");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.mexico_peso));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Mongolia Tughrik");
        cv.put(CURRENCY_ISO_CODE, "MNT");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.mongolia_tughrik));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Mozambique Metical");
        cv.put(CURRENCY_ISO_CODE, "MZN");
        String strMozambiqueMetical =
                context.getResources().getString(R.string.mozambique_metical_1) +
                        context.getResources().getString(R.string.mozambique_metical_2);
        cv.put(CURRENCY_SYMBOL, strMozambiqueMetical);
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Namibia Dollar");
        cv.put(CURRENCY_ISO_CODE, "NAD");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.namibia_dollar));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Nepal Rupee");
        cv.put(CURRENCY_ISO_CODE, "NPR");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.nepal_rupee));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Netherlands Antilles Guilder");
        cv.put(CURRENCY_ISO_CODE, "ANG");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.netherlands_antilles_guilder));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "New Zealand Dollar");
        cv.put(CURRENCY_ISO_CODE, "NZD");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.new_zealand_dollar));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Nicaragua Cordoba");
        cv.put(CURRENCY_ISO_CODE, "NIO");
        String strNicaraguaCordoba =
                context.getResources().getString(R.string.nicaragua_cordoba_1) +
                        context.getResources().getString(R.string.nicaragua_cordoba_2);
        cv.put(CURRENCY_SYMBOL, strNicaraguaCordoba);
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Nigeria Naira");
        cv.put(CURRENCY_ISO_CODE, "NGN");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.nigeria_naira));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Norway Krone");
        cv.put(CURRENCY_ISO_CODE, "NOK");
        String strNorwayKrone =
                context.getResources().getString(R.string.norway_krone_1) +
                        context.getResources().getString(R.string.norway_krone_2);
        cv.put(CURRENCY_SYMBOL, strNorwayKrone);
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Oman Rial");
        cv.put(CURRENCY_ISO_CODE, "OMR");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.oman_rial));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Pakistan Rupee");
        cv.put(CURRENCY_ISO_CODE, "PKR");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.pakistan_rupee));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Panama Balboa");
        cv.put(CURRENCY_ISO_CODE, "PAB");
        String strPanamaBalboa =
                context.getResources().getString(R.string.panama_balboa_1) +
                        context.getResources().getString(R.string.panama_balboa_2) +
                        context.getResources().getString(R.string.panama_balboa_3);
        cv.put(CURRENCY_SYMBOL, strPanamaBalboa);
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Paraguay Guarani");
        cv.put(CURRENCY_ISO_CODE, "PYG");
        String strParaguayGuarani =
                context.getResources().getString(R.string.paraguay_guarani_1) +
                        context.getResources().getString(R.string.paraguay_guarani_2);
        cv.put(CURRENCY_SYMBOL, strParaguayGuarani);
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Peru Nuevo Sol");
        cv.put(CURRENCY_ISO_CODE, "PEN");
        String strPeruNuevoSol =
                context.getResources().getString(R.string.peru_nuevo_sol_1) +
                        context.getResources().getString(R.string.peru_nuevo_sol_2) +
                        context.getResources().getString(R.string.peru_nuevo_sol_3);
        cv.put(CURRENCY_SYMBOL, strPeruNuevoSol);
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Philippines Peso");
        cv.put(CURRENCY_ISO_CODE, "PHP");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.philippines_peso));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Poland Zloty");
        cv.put(CURRENCY_ISO_CODE, "PLN");
        String strPolandZloty =
                context.getResources().getString(R.string.poland_zloty_1) +
                        context.getResources().getString(R.string.poland_zloty_2);
        cv.put(CURRENCY_SYMBOL, strPolandZloty);
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Qatar Riyal");
        cv.put(CURRENCY_ISO_CODE, "QAR");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.qatar_riyal));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Romania New Leu");
        cv.put(CURRENCY_ISO_CODE, "RON");
        String strRomaniaNewLeu =
                context.getResources().getString(R.string.romania_new_leu_1) +
                        context.getResources().getString(R.string.romania_new_leu_2) +
                        context.getResources().getString(R.string.romania_new_leu_3);
        cv.put(CURRENCY_SYMBOL, strRomaniaNewLeu);
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Russia Ruble");
        cv.put(CURRENCY_ISO_CODE, "RUB");
        String strRussiaRuble =
                context.getResources().getString(R.string.russia_ruble_1) +
                        context.getResources().getString(R.string.russia_ruble_2) +
                        context.getResources().getString(R.string.russia_ruble_3);
        cv.put(CURRENCY_SYMBOL, strRussiaRuble);
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Saint Helena Pound");
        cv.put(CURRENCY_ISO_CODE, "SHP");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.saint_helena_pound));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Saudi Arabia Riyal");
        cv.put(CURRENCY_ISO_CODE, "SAR");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.saudi_arabia_riyal));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Serbia Dinar");
        cv.put(CURRENCY_ISO_CODE, "RSD");
        String strSerbiaDinar =
                context.getResources().getString(R.string.serbia_dinar_1) +
                        context.getResources().getString(R.string.serbia_dinar_2) +
                        context.getResources().getString(R.string.serbia_dinar_3) +
                        context.getResources().getString(R.string.serbia_dinar_4);
        cv.put(CURRENCY_SYMBOL, strSerbiaDinar);
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Seychelles Rupee");
        cv.put(CURRENCY_ISO_CODE, "SCR");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.seychelles_rupee));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Singapore Dollar");
        cv.put(CURRENCY_ISO_CODE, "SGD");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.singapore_dollar));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Solomon Islands Dollar");
        cv.put(CURRENCY_ISO_CODE, "SBD");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.solomon_islands_dollar));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Somalia Shilling");
        cv.put(CURRENCY_ISO_CODE, "SOS");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.somalia_shilling));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "South Africa Rand");
        cv.put(CURRENCY_ISO_CODE, "ZAR");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.south_africa_rand));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Sri Lanka Rupee");
        cv.put(CURRENCY_ISO_CODE, "LKR");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.sri_lanka_rupee));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Sweden Krona");
        cv.put(CURRENCY_ISO_CODE, "SEK");
        String strSwedenKrona =
                context.getResources().getString(R.string.sweden_krona_1) +
                        context.getResources().getString(R.string.sweden_krona_2);
        cv.put(CURRENCY_SYMBOL, strSwedenKrona);
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Switzerland Franc");
        cv.put(CURRENCY_ISO_CODE, "CHF");
        String strSwitzerlandFranc =
                context.getResources().getString(R.string.switzerland_franc_1) +
                        context.getResources().getString(R.string.switzerland_franc_2) +
                        context.getResources().getString(R.string.switzerland_franc_3);
        cv.put(CURRENCY_SYMBOL, strSwitzerlandFranc);
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Suriname Dollar");
        cv.put(CURRENCY_ISO_CODE, "SRD");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.suriname_dollar));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Syria Pound");
        cv.put(CURRENCY_ISO_CODE, "SYP");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.syria_pound));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Taiwan New Dollar");
        cv.put(CURRENCY_ISO_CODE, "TWD");
        String strTaiwanNewDollar =
                context.getResources().getString(R.string.taiwan_new_dollar_1) +
                        context.getResources().getString(R.string.taiwan_new_dollar_2) +
                        context.getResources().getString(R.string.taiwan_new_dollar_3);
        cv.put(CURRENCY_SYMBOL, strTaiwanNewDollar);
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Thailand Baht");
        cv.put(CURRENCY_ISO_CODE, "THB");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.thailand_baht));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Trinidad And Tobago Dollar");
        cv.put(CURRENCY_ISO_CODE, "TTD");
        String strTrinidadAndTobagoDollar =
                context.getResources().getString(R.string.trinidad_and_tobago_dollar_1) +
                        context.getResources().getString(R.string.trinidad_and_tobago_dollar_2) +
                        context.getResources().getString(R.string.trinidad_and_tobago_dollar_3);
        cv.put(CURRENCY_SYMBOL, strTrinidadAndTobagoDollar);
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Turkey Lira");
        cv.put(CURRENCY_ISO_CODE, "TRY");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.turkey_lira));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Tuvalu Dollar");
        cv.put(CURRENCY_ISO_CODE, "TVD");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.tuvalu_dollar));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Ukraine Hryvna");
        cv.put(CURRENCY_ISO_CODE, "UAH");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.ukraine_hryvna));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "United Kingdom Pound");
        cv.put(CURRENCY_ISO_CODE, "GBP");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.united_kingdom_pound));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "United States Dollar");
        cv.put(CURRENCY_ISO_CODE, "USD");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.united_states_dollar));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Uruguay Peso");
        cv.put(CURRENCY_ISO_CODE, "UYU");
        String strUruguayPeso =
                context.getResources().getString(R.string.uruguay_peso_1) +
                        context.getResources().getString(R.string.uruguay_peso_2);
        cv.put(CURRENCY_SYMBOL, strUruguayPeso);
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Uzbekistan Som");
        cv.put(CURRENCY_ISO_CODE, "UZS");
        String strUzbekistanSom =
                context.getResources().getString(R.string.uzbekistan_som_1) +
                        context.getResources().getString(R.string.uzbekistan_som_2);
        cv.put(CURRENCY_SYMBOL, strUzbekistanSom);
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Venezuela Bolivar");
        cv.put(CURRENCY_ISO_CODE, "VEF");
        String strVenezuelaBolivar =
                context.getResources().getString(R.string.venezuela_bolivar_1) +
                        context.getResources().getString(R.string.venezuela_bolivar_2);
        cv.put(CURRENCY_SYMBOL, strVenezuelaBolivar);
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Viet Nam Dong");
        cv.put(CURRENCY_ISO_CODE, "VND");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.viet_nam_dong));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Yemen Rial");
        cv.put(CURRENCY_ISO_CODE, "YER");
        cv.put(CURRENCY_SYMBOL, context.getResources().getString(R.string.yemen_rial));
        db.insert(CURRENCY, null, cv);

        cv.put(CURRENCY_NAME, "Zimbabwe Dollar");
        cv.put(CURRENCY_ISO_CODE, "ZWD");
        String strZimbabweDollar =
                context.getResources().getString(R.string.zimbabwe_dollar_1) +
                        context.getResources().getString(R.string.zimbabwe_dollar_2);
        cv.put(CURRENCY_SYMBOL, strZimbabweDollar);
        db.insert(CURRENCY, null, cv);
    }

    /***** CONVERTING PNG IMAGES INTO byte[] *****/
    private byte[] PNGConverter(String imgPath) {

        asstMgr = context.getAssets();
        bmpThumb = null;
        byte[] brImgThumb;

        try {
			/* CONVERT ASSETS IMAGE PATH TO A BITMAP */
            inStream = asstMgr.open(imgPath);
            bmpThumb = BitmapFactory.decodeStream(inStream);

			/* CONVERT TO byte[] */
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bmpThumb.compress(Bitmap.CompressFormat.PNG, 100, bos);
            brImgThumb = bos.toByteArray();

            /** SET THE THUMBNAIL IN THE DATABASE **/
            return brImgThumb;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /***** CONVERTING JPEG || JPG IMAGES INTO byte[] *****/
    private byte[] JPGConverter(String imgPath) {

        asstMgr = context.getAssets();
        bmpThumb = null;
        byte[] brImgThumb;

        try {
			/* CONVERT ASSETS IMAGE PATH TO A BITMAP */
            inStream = asstMgr.open(imgPath);
            bmpThumb = BitmapFactory.decodeStream(inStream);

			/* CONVERT TO byte[] */
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bmpThumb.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            brImgThumb = bos.toByteArray();

            /** SET THE THUMBNAIL IN THE DATABASE **/
            return brImgThumb;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}