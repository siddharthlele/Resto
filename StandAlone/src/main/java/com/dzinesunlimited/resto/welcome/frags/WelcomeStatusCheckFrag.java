package com.dzinesunlimited.resto.welcome.frags;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.dzinesunlimited.resto.R;
import com.dzinesunlimited.resto.utils.AppPrefs;
import com.dzinesunlimited.resto.welcome.WelcomeContainer;

public class WelcomeStatusCheckFrag extends Fragment {

    /** GLOBAL VIEW INSTANCE **/
    private View view;

    /** DECLARE THE LAYOUT ELEMENTS **/
    private AppCompatTextView txtFinalStatusLabel;

    private RelativeLayout rellaRestaurantDetails;
    private AppCompatTextView txtRestaurantDetails;
    private AppCompatImageView imgvwRestaurantDetails;

    private RelativeLayout rellaCountryDetails;
    private AppCompatTextView txtCountryDetails;
    private AppCompatImageView imgvwCountryDetails;

    private RelativeLayout rellaCurrencyDetails;
    private AppCompatTextView txtCurrencyDetails;
    private AppCompatImageView imgvwCurrencyDetails;

    private AppPrefs getApp()	{
        return (AppPrefs) getActivity().getApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /** CAST THE LAYOUT TO A NEW VIEW INSTANCE **/
        view = inflater.inflate(R.layout.welcome_status_check, container, false);

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

        /** CAST THE LAYOUT ELEMENTS **/
        castLayoutElements();
    }

    private void castLayoutElements() {
        txtFinalStatusLabel = (AppCompatTextView) view.findViewById(R.id.txtFinalStatusLabel);
        txtFinalStatusLabel.setText(Html.fromHtml(getActivity().getResources().getString(R.string.status_final_check_label)));

        rellaRestaurantDetails = (RelativeLayout) view.findViewById(R.id.rellaRestaurantDetails);
        txtRestaurantDetails = (AppCompatTextView) view.findViewById(R.id.txtRestaurantDetails);
        imgvwRestaurantDetails = (AppCompatImageView) view.findViewById(R.id.imgvwRestaurantDetails);

        rellaCountryDetails = (RelativeLayout) view.findViewById(R.id.rellaCountryDetails);
        txtCountryDetails = (AppCompatTextView) view.findViewById(R.id.txtCountryDetails);
        imgvwCountryDetails = (AppCompatImageView) view.findViewById(R.id.imgvwCountryDetails);

        rellaCurrencyDetails = (RelativeLayout) view.findViewById(R.id.rellaCurrencyDetails);
        txtCurrencyDetails = (AppCompatTextView) view.findViewById(R.id.txtCurrencyDetails);
        imgvwCurrencyDetails = (AppCompatImageView) view.findViewById(R.id.imgvwCurrencyDetails);

        /** SWITCH TO THE RESTAURANT DETAILS SECTION **/
        rellaRestaurantDetails.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

				/* SWITCH FRAGMENT */
                ((WelcomeContainer) getActivity()).getPager().setCurrentItem(1);
            }
        });

        /** SWITCH TO THE COUNTRY DETAILS SECTION **/
        rellaCountryDetails.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

				/* SWITCH FRAGMENT */
                ((WelcomeContainer) getActivity()).getPager().setCurrentItem(3);
            }
        });

        /** SWITCH TO THE CURRENCY DETAILS SECTION **/
        rellaCurrencyDetails.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

				/* SWITCH FRAGMENT */
                ((WelcomeContainer) getActivity()).getPager().setCurrentItem(4);
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser)	{

            /** FETCH THE COUNTRY AND CURRENCY DEFAULT **/
            checkStatus();
        }
    }

    /** CHECK IF A RESTAURANT, COUNTRY AND CURRENCY ARE SET**/
    private void checkStatus() {

        boolean blnRestaurantSet = getApp().getRestaurantSet();
//        boolean blnCountrySet = getApp().getCountrySelection();
        boolean blnCurrencySet = getApp().getCurrencySelection();

		/* RESTAURANT */
        if (!blnRestaurantSet)	{
            imgvwRestaurantDetails.setImageResource(R.drawable.ic_signal_red);
        } else {
            imgvwRestaurantDetails.setImageResource(R.drawable.ic_signal_green);
        }

		/* COUNTRY */
//        if (!blnCountrySet)	{
//            imgvwCountryDetails.setImageResource(R.drawable.ic_signal_red);
//        } else {
//            imgvwCountryDetails.setImageResource(R.drawable.ic_signal_green);
//        }

		/* CURRENCY */
        if (!blnCurrencySet)	{
            imgvwCurrencyDetails.setImageResource(R.drawable.ic_signal_red);
        } else {
            imgvwCurrencyDetails.setImageResource(R.drawable.ic_signal_green);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putString("WORKAROUND_FOR_BUG_19917_KEY",  "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }
}