package com.dzinesunlimited.resto.welcome.frags;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.dzinesunlimited.resto.R;
import com.dzinesunlimited.resto.backend.admin.AdminLanding;
import com.dzinesunlimited.resto.utils.AppPrefs;

public class WelcomeThanksFrag extends Fragment {

    /** GLOBAL VIEW INSTANCE **/
    private View view;

    /** DECLARE THE LAYOUT ELEMENTS **/
    private AppCompatButton btnAccept;

    private AppPrefs getApp()	{
        return (AppPrefs) getActivity().getApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /** CAST THE LAYOUT TO A NEW VIEW INSTANCE **/
        view = inflater.inflate(R.layout.welcome_thanks, container, false);

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

        /** CAST LAYOUT ELEMENTS **/
        castLayoutElements();
    }

    /** CAST LAYOUT ELEMENTS **/
    private void castLayoutElements() {
        btnAccept = (AppCompatButton) view.findViewById(R.id.btnAccept);

        /** FINISH THE TUTORIAL **/
        btnAccept.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                /** CHECK IF A RESTAURANT, COUNTRY AND CURRENCY ARE SET**/
                boolean blnRestaurantSet = getApp().getRestaurantSet();
//                boolean blnCountrySet = getApp().getCountrySelection();
                boolean blnCurrencySet = getApp().getCurrencySelection();

				/* RESTAURANT NOT SET */
                if (!blnRestaurantSet)	{
                    String strRest = getResources().getString(R.string.tut_rest_details_missing);
                    Toast.makeText(getActivity(), strRest, Toast.LENGTH_LONG).show();

				/* COUNTRY NOT SET */
//                } else if (!blnCountrySet) {
//                    String strCountry = getResources().getString(R.string.tut_country_details_missing);
//                    Toast.makeText(getActivity(), strCountry, Toast.LENGTH_LONG).show();

				/* CURRENCY NOT SET */
                } else if (!blnCurrencySet) {
                    String strCurrency = getResources().getString(R.string.tut_currency_details_missing);
                    Toast.makeText(getActivity(), strCurrency, Toast.LENGTH_LONG).show();
                }

				/* ALL ARE SET, CLOSE THE TUTORIAL */
                if (blnRestaurantSet && /*blnCountrySet &&*/ blnCurrencySet) {

                    Intent startMainPage = new Intent(getActivity(), AdminLanding.class);
                    startMainPage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(startMainPage);

					/* FINISH THE CURRENT ACTIVITY */
                    getActivity().finish();

                    /** SET THE TUTORIAL COMPLETE **/
                    getApp().setTutorialCompleted();
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putString("WORKAROUND_FOR_BUG_19917_KEY",  "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }
}