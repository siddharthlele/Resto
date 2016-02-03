package com.dzinesunlimited.resto.backend.admin.frags;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.dzinesunlimited.resto.R;
import com.dzinesunlimited.resto.frontend.tables.TableSelector;
import com.dzinesunlimited.resto.utils.TypefaceSpan;

public class Dashboard extends Fragment {

    /** GLOBAL VIEW INSTANCE **/
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /** CAST THE LAYOUT TO A NEW VIEW INSTANCE **/
        view = inflater.inflate(R.layout.be_admin_dashboard, container, false);

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
    }

    /***** CAST THE LAYOUT ELEMENTS *****/
    private void castLayoutElements() {
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {

        String strTitle = getResources().getString(R.string.dashboard_title);
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(
                getActivity(), "RobotoCondensed-Regular.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(s);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.admin_dashboard, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.orders:

                /** START TAKING ORDERS (SHOW THE TABLE SELECTOR) **/
                Intent tableSelector = new Intent(getActivity(), TableSelector.class);
                startActivity(tableSelector);

                break;

            default:
                break;
        }

        return false;
    }
}