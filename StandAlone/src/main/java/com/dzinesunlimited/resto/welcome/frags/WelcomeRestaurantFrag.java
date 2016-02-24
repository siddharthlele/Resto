package com.dzinesunlimited.resto.welcome.frags;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.dzinesunlimited.resto.R;
import com.dzinesunlimited.resto.utils.AppPrefs;
import com.dzinesunlimited.resto.utils.db.DBResto;
import com.dzinesunlimited.resto.welcome.WelcomeContainer;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class WelcomeRestaurantFrag extends Fragment {

    /** GLOBAL VIEW INSTANCE **/
    private View view;

    /** THE DATABASE HELPER INSTANCE **/
    private DBResto db;

    /** THE CURSOR INSTANCE **/
    private Cursor cursor;

    /** DECLARE THE LAYOUT ELEMENTS **/
    /* RESTAURANT NAME */
    private AppCompatEditText edtRestaurantName;

    /* FORM FIELDS */
//    private AppCompatEditText edtAddress1;
//    private AppCompatEditText edtAddress2;
//    private AppCompatEditText edtCity;
//    private AppCompatEditText edtState;
//    private AppCompatEditText edtCountry;
//    private AppCompatEditText edtZIP;
//    private AppCompatEditText edtPhone;
//    private AppCompatEditText edtEmail;
//    private AppCompatEditText edtWebsite;

    /* RESTAURANT LOGO */
    private ImageView imgvwRestaurantLogo;

    private String NAME;
//    private String ADDRESS_1;
//    private String ADDRESS_2;
//    private String CITY;
//    private String STATE;
//    private String COUNTRY;
//    private String ZIP;
//    private String CONTACT_NO;
//    private String EMAIL_ADDRESS;
//    private String WEBSITE;
    private byte[] LOGO;

    private AppPrefs getApp()	{
        return (AppPrefs) getActivity().getApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /** CAST THE LAYOUT TO A NEW VIEW INSTANCE **/
        view = inflater.inflate(R.layout.welcome_restaurant_details, container, false);

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

        /** PREVENT THE FRAGMENT TO PASS THE FOCUS TO THE EDITTEXT **/
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /** INSTANTIATE THE DATABASE HELPER CLASS **/
        db = new DBResto(getActivity());

        /** CAST THE LAYOUT ELEMENTS **/
        castLayoutElements();
    }

    private class showRestaurantDetails extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /** SHOW THE PROGRESSBAR WHILE FETCHING THE LIST OF TABLES **/
//			linlaHeaderProgress.setVisibility(View.VISIBLE);

			/* CONSTRUCT A QUERY TO FETCH TABLES ON RECORD */
            String strQueryData = "SELECT * FROM " + db.RESTAURANT;
//			Log.e("TABLE QUERY", strQueryData);

			/* CAST THE QUERY IN THE CURSOR TO FETCH THE RESULTS */
            cursor = db.selectAllData(strQueryData);
//			Log.e("CURSOR", DatabaseUtils.dumpCursorToString(cursor));
        }

        @Override
        protected Void doInBackground(Void... params) {

            /** CHECK THAT THE DATABASE QUERY RETURNED SOME RESULTS **/
            if (cursor != null && cursor.getCount() != 0)	{

                /** LOOP THROUGH THE RESULT SET AND PARSE NECESSARY INFORMATION **/
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                    /** GET THE RESTAURANT ID **/
                    /* STRING INSTANCES FOR VARIOUS FORM FIELDS */
//                    String ID;
//                    if (cursor.getString(cursor.getColumnIndex(db.RESTAURANT_ID)) != null)	{
//                        ID = cursor.getString(cursor.getColumnIndex(db.RESTAURANT_ID));
//                    } else {
//                        ID = null;
//                    }

                    /** GET THE RESTAURANT NAME **/
                    if (cursor.getString(cursor.getColumnIndex(db.RESTAURANT_NAME)) != null)	{
                        NAME = cursor.getString(cursor.getColumnIndex(db.RESTAURANT_NAME));
                    } else {
                        NAME = null;
                    }

                    /** GET THE RESTAURANT LOGO **/
                    if (cursor.getBlob(cursor.getColumnIndex(db.RESTAURANT_LOGO)) != null)	{
                        LOGO = cursor.getBlob(cursor.getColumnIndex(db.RESTAURANT_LOGO));
                    } else {
                        LOGO = null;
                    }

//                    /** GET THE RESTAURANT ADDRESS 1 **/
//                    if (cursor.getString(cursor.getColumnIndex(db.RESTAURANT_ADDRESS_1)) != null)	{
//                        ADDRESS_1 = cursor.getString(cursor.getColumnIndex(db.RESTAURANT_ADDRESS_1));
//                    } else {
//                        ADDRESS_1 = null;
//                    }
//
//                    /** GET THE RESTAURANT ADDRESS 2 **/
//                    if (cursor.getString(cursor.getColumnIndex(db.RESTAURANT_ADDRESS_2)) != null)	{
//                        ADDRESS_2 = cursor.getString(cursor.getColumnIndex(db.RESTAURANT_ADDRESS_2));
//                    } else {
//                        ADDRESS_2 = null;
//                    }
//
//                    /** GET THE RESTAURANT CITY **/
//                    if (cursor.getString(cursor.getColumnIndex(db.RESTAURANT_CITY)) != null)	{
//                        CITY = cursor.getString(cursor.getColumnIndex(db.RESTAURANT_CITY));
//                    } else {
//                        CITY = null;
//                    }
//
//                    /** GET THE RESTAURANT STATE **/
//                    if (cursor.getString(cursor.getColumnIndex(db.RESTAURANT_STATE)) != null)	{
//                        STATE = cursor.getString(cursor.getColumnIndex(db.RESTAURANT_STATE));
//                    } else {
//                        STATE = null;
//                    }
//
//                    /** GET THE RESTAURANT COUNTRY **/
//                    if (cursor.getString(cursor.getColumnIndex(db.RESTAURANT_COUNTRY)) != null)	{
//                        COUNTRY = cursor.getString(cursor.getColumnIndex(db.RESTAURANT_COUNTRY));
//                    } else {
//                        COUNTRY = null;
//                    }
//
//                    /** GET THE RESTAURANT ZIP **/
//                    if (cursor.getString(cursor.getColumnIndex(db.RESTAURANT_ZIP)) != null)	{
//                        ZIP = cursor.getString(cursor.getColumnIndex(db.RESTAURANT_ZIP));
//                    } else {
//                        ZIP = null;
//                    }
//
//                    /** GET THE RESTAURANT PHONE **/
//                    if (cursor.getString(cursor.getColumnIndex(db.RESTAURANT_PHONE)) != null)	{
//                        CONTACT_NO = cursor.getString(cursor.getColumnIndex(db.RESTAURANT_PHONE));
//                    } else {
//                        CONTACT_NO = null;
//                    }
//
//                    /** GET THE RESTAURANT EMAIL **/
//                    if (cursor.getString(cursor.getColumnIndex(db.RESTAURANT_EMAIL)) != null)	{
//                        EMAIL_ADDRESS = cursor.getString(cursor.getColumnIndex(db.RESTAURANT_EMAIL));
//                    } else {
//                        EMAIL_ADDRESS = null;
//                    }
//
//                    /** GET THE RESTAURANT WEBSITE **/
//                    if (cursor.getString(cursor.getColumnIndex(db.RESTAURANT_WEBSITE)) != null)	{
//                        WEBSITE = cursor.getString(cursor.getColumnIndex(db.RESTAURANT_WEBSITE));
//                    } else {
//                        WEBSITE = null;
//                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            /** SET THE COLLECTED RESTAURANT DETAILS **/
			/* NAME */
            if (NAME != null)	{
                edtRestaurantName.setText(NAME);
            }

//			/* ADDRESS 1 */
//            if (ADDRESS_1 != null)	{
//                edtAddress1.setText(ADDRESS_1);
//            }
//
//			/* ADDRESS 2 */
//            if (ADDRESS_2 != null)	{
//                edtAddress2.setText(ADDRESS_2);
//            }
//
//			/* CITY */
//            if (CITY != null)	{
//                edtCity.setText(CITY);
//            }
//
//			/* STATE */
//            if (STATE != null)	{
//                edtState.setText(STATE);
//            }
//
//			/* COUNTRY */
//            if (COUNTRY != null)	{
//                edtCountry.setText(COUNTRY);
//            }
//
//			/* ZIP */
//            if (ZIP != null)	{
//                edtZIP.setText(ZIP);
//            }
//
//			/* PHONE */
//            if (CONTACT_NO != null)	{
//                edtPhone.setText(CONTACT_NO);
//            }
//
//			/* EMAIL */
//            if (EMAIL_ADDRESS != null)	{
//                edtEmail.setText(EMAIL_ADDRESS);
//            }
//
//			/* WEBSITE */
//            if (WEBSITE != null)	{
//                edtWebsite.setText(WEBSITE);
//            }

			/* LOGO */
            if (LOGO != null)	{
                Bitmap bmpThumb = BitmapFactory.decodeByteArray(LOGO, 0, LOGO.length);
                int nh = (int) (bmpThumb.getHeight() * (512.0 / bmpThumb.getWidth()));
                Bitmap bmpScaled = Bitmap.createScaledBitmap(bmpThumb, 512, nh, true);
                imgvwRestaurantLogo.setImageBitmap(bmpScaled);
            }
            /*****/

            // CLOSE THE CURSOR
            if (cursor != null && !cursor.isClosed())	{
                cursor.close();
            }

            // CLOSE THE DATABASE
            db.close();
        }
    }

    /** CAST THE LAYOUT ELEMENTS **/
    private void castLayoutElements() {

		/* RESTAURANT NAME */
        edtRestaurantName = (AppCompatEditText) view.findViewById(R.id.edtRestaurantName);

		/* FORM FIELDS */
//        edtAddress1 = (AppCompatEditText) view.findViewById(R.id.edtAddress1);
//        edtAddress2 = (AppCompatEditText) view.findViewById(R.id.edtAddress2);
//        edtCity = (AppCompatEditText) view.findViewById(R.id.edtCity);
//        edtState = (AppCompatEditText) view.findViewById(R.id.edtState);
//        edtCountry = (AppCompatEditText) view.findViewById(R.id.edtCountry);
//        edtZIP = (AppCompatEditText) view.findViewById(R.id.edtZIP);
//        edtPhone = (AppCompatEditText) view.findViewById(R.id.edtPhone);
//        edtEmail = (AppCompatEditText) view.findViewById(R.id.edtEmail);
//        edtWebsite = (AppCompatEditText) view.findViewById(R.id.edtWebsite);

		/* RESTAURANT LOGO */
        imgvwRestaurantLogo = (ImageView) view.findViewById(R.id.imgvwRestaurantLogo);

        /** CHECK IF DETAILS EXIST **/
        boolean blnRestaurantSet = getApp().getRestaurantSet();
        if (blnRestaurantSet)	{
            /** SHOW THE CURRENT RESTAURANT DETAILS **/
            new showRestaurantDetails().execute();
        }

        /** GET THE RESTAURANT LOGO **/
        imgvwRestaurantLogo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EasyImage.openChooser(WelcomeRestaurantFrag.this, "Pick Image Source", true);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        Log.e("DATA", data.toString());

        EasyImage.handleActivityResult(requestCode, resultCode, data, getActivity(), new DefaultCallback() {

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source) {
                super.onImagePicked(imageFile, source);
                onPhotoReturned(imageFile);
            }

            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source) {
                super.onImagePickerError(e, source);
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source) {
                super.onCanceled(source);
            }
        });
    }

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//            imgvwRestaurantLogo.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imgvwRestaurantLogo.setImageBitmap(bitmap);

            /** CONVERT THE BITMAP TO A BYTE ARRAY **/
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            LOGO = bos.toByteArray();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    private void onPhotoReturned(File photoFile) {
        Picasso.with(getActivity())
                .load(photoFile)
                .resize(800, 800)
                .centerInside()
                .into(target);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.intro_rest_details, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//		return super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.save:

				/* SAVE THE RESTAURANT DETAILS **/
                new saveRestaurant().execute();

                break;

            case R.id.clear:

				/* CLEAR ALL FIELDS IN THE RESTAURANT DETAILS FORM */
                clearFields();

                break;

            default:
                break;
        }

        return false;
    }

    /** SAVE THE RESTAURANT DETAILS **/
    private class saveRestaurant extends AsyncTask<Void, Void, Void> {

        /** PROGRESSDIALOG INSTANCE **/
        ProgressDialog pdNewRestaurant;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /** INSTANTIATE AND CONFIGURE THE PROGRESSDIALOG **/
            pdNewRestaurant = new ProgressDialog (getActivity());

            String strRestDtlProgress = getResources().getString(R.string.welcome_rest_save_progress);
            pdNewRestaurant.setMessage(strRestDtlProgress);
            pdNewRestaurant.setIndeterminate(false);
            pdNewRestaurant.setCancelable(true);
            pdNewRestaurant.show();

            /** FETCH VARIOUS FORM FIELDS **/
			/* RESTAURANT NAME */
            if (edtRestaurantName.getText().toString().length() != 0)	{
                NAME = edtRestaurantName.getText().toString();
            } else {
                NAME = null;
            }

//			/* ADDRESS 1 */
//            if (edtAddress1.getText().toString().length() != 0)	{
//                ADDRESS_1 = edtAddress1.getText().toString();
//            } else {
//                ADDRESS_1 = null;
//            }
//
//			/* ADDRESS 2 */
//            if (edtAddress2.getText().toString().length() != 0)	{
//                ADDRESS_2 = edtAddress2.getText().toString();
//            } else {
//                ADDRESS_2 = null;
//            }
//
//			/* CITY */
//            if (edtCity.getText().toString().length() != 0)	{
//                CITY = edtCity.getText().toString();
//            } else {
//                CITY = null;
//            }
//
//			/* STATE */
//            if (edtState.getText().toString().length() != 0)	{
//                STATE = edtState.getText().toString();
//            } else {
//                STATE = null;
//            }
//
//			/* COUNTRY */
//            if (edtCountry.getText().toString().length() != 0)	{
//                COUNTRY = edtCountry.getText().toString();
//            } else {
//                COUNTRY = null;
//            }
//
//			/* ZIP CODE */
//            if (edtZIP.getText().toString().length() != 0)	{
//                ZIP = edtZIP.getText().toString();
//            } else {
//                ZIP = null;
//            }
//
//			/* CONTACT NUMBER */
//            if (edtPhone.getText().toString().length() != 0)	{
//                CONTACT_NO = edtPhone.getText().toString();
//            } else {
//                CONTACT_NO = null;
//            }
//
//			/* EMAIL */
//            if (edtEmail.getText().toString().length() != 0)	{
//                EMAIL_ADDRESS = edtEmail.getText().toString();
//            } else {
//                EMAIL_ADDRESS = null;
//            }
//
//			/* WEBSITE */
//            if (edtWebsite.getText().toString().length() != 0)	{
//                WEBSITE = edtWebsite.getText().toString();
//            } else {
//                WEBSITE = null;
//            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            /***** VALIDATE THE FORM FIELDS *****/
			/* RESTAURANT NAME */
            if (NAME == null)	{
                edtRestaurantName.setError("The Restaurant Name cannot be empty!");
            }

//			/* ADDRESS 1 */
//            if (ADDRESS_1 == null)	{
//                edtAddress1.setError("The field \"Address 1\" cannot be empty!");
//            }
//
//			/* CITY */
//            if (CITY == null)	{
//                edtCity.setError("The City cannot be empty!");
//            }
//
//			/* STATE */
//            if (STATE == null)	{
//                edtState.setError("The State cannot be empty!");
//            }
//
//			/* COUNTRY */
//            if (COUNTRY == null)	{
//                edtCountry.setError("The Country cannot be empty!");
//            }
//
//			/* ZIP */
//            if (ZIP == null)	{
//                edtZIP.setError("Please enter a valid ZIP Code!");
//            }
//
//			/* EMAIL ADDRESS */
//            if (EMAIL_ADDRESS == null)	{
//                edtEmail.setError("Please enter a valid Email Address!");
//            }

            /** ADD THE COLLECTED DETAILS TO THE RESTAURANT TABLE **/
            if (NAME != null /*&&
                    ADDRESS_1 != null && CITY != null && STATE != null && COUNTRY != null && ZIP != null &&
                    CONTACT_NO != null && EMAIL_ADDRESS != null*/ && LOGO != null)	{

                db.addRestaurantDetails(
                        1, NAME, LOGO/*,
                        ADDRESS_1, ADDRESS_2, CITY, STATE, COUNTRY, ZIP,
                        CONTACT_NO, EMAIL_ADDRESS, WEBSITE*/);

                /** SET RESTAURANT NAME **/
//                getApp().setRestaurantDetails(NAME, ADDRESS_1, ADDRESS_2, CITY, STATE, COUNTRY, ZIP,
//                        CONTACT_NO, EMAIL_ADDRESS, WEBSITE);

                /** SET RESTAURANT DETAILS FILLED **/
                getApp().setRestaurantSet();
            } else {
                String strErrorMessage = getResources().getString(R.string.welcome_rest_details_missing);
                Toast.makeText(getActivity(), strErrorMessage, Toast.LENGTH_SHORT).show();
            }

            /** CLOSE THE CURSOR **/
            if (cursor != null && !cursor.isClosed())	{
                cursor.close();
            }

            /** CLOSE THE DATABASE **/
            db.close();

            /** DISMISS THE DIALOG **/
            pdNewRestaurant.dismiss();

            if (getApp().getRestaurantSet())	{

                /** BRING IN THE NEXT SCREEN **/
                ((WelcomeContainer) getActivity()).getPager().setCurrentItem(2);
            }
        }
    }

    /** CLEAR RESTAURANT DETAILS FIELDS **/
    private void clearFields() {

        edtRestaurantName.setText("");
        edtRestaurantName.setHint(getActivity().getResources().getString(R.string.welcome_rest_name_hint));

//        edtAddress1.setText("");
//        edtAddress1.setHint(getActivity().getResources().getString(R.string.nw_acc_add1));
//
//        edtAddress2.setText("");
//        edtAddress2.setHint(getActivity().getResources().getString(R.string.nw_acc_add2));
//
//        edtCity.setText("");
//        edtCity.setHint(getActivity().getResources().getString(R.string.nw_acc_city));
//
//        edtState.setText("");
//        edtState.setHint(getActivity().getResources().getString(R.string.nw_acc_state));
//
//        edtCountry.setText("");
//        edtCountry.setHint(getActivity().getResources().getString(R.string.nw_acc_country));
//
//        edtZIP.setText("");
//        edtZIP.setHint(getActivity().getResources().getString(R.string.nw_acc_zip));
//
//        edtPhone.setText("");
//        edtPhone.setHint(getActivity().getResources().getString(R.string.nw_acc_phone));
//
//        edtEmail.setText("");
//        edtEmail.setHint(getActivity().getResources().getString(R.string.nw_acc_email));
//
//        edtWebsite.setText("");
//        edtWebsite.setHint(getActivity().getResources().getString(R.string.nw_acc_website));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }
}