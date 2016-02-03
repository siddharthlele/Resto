package com.dzinesunlimited.resto.welcome;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.dzinesunlimited.resto.R;
import com.dzinesunlimited.resto.utils.TypefaceSpan;
import com.dzinesunlimited.resto.welcome.frags.WelcomeCountriesFrag;
import com.dzinesunlimited.resto.welcome.frags.WelcomeCurrencyFrag;
import com.dzinesunlimited.resto.welcome.frags.WelcomeLandingFrag;
import com.dzinesunlimited.resto.welcome.frags.WelcomeRestaurantFrag;
import com.dzinesunlimited.resto.welcome.frags.WelcomeStatusCheckFrag;
import com.dzinesunlimited.resto.welcome.frags.WelcomeTablesFrag;
import com.dzinesunlimited.resto.welcome.frags.WelcomeTaxesFrag;
import com.dzinesunlimited.resto.welcome.frags.WelcomeThanksFrag;

import java.util.List;
import java.util.Vector;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class WelcomeContainer extends AppCompatActivity {

    /** THE ADAPTER **/
    private SlidingAdapter adapter;

    /** THE PAGER **/
    ViewPager pager;

    /** THE FRAGMENTS **/
    final List<Fragment> fragments = new Vector<>();

    /** STRING ARRAY OF TITLES **/
    private String[] tabTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_container);

        tabTitles = new String[] {
                getResources().getString(R.string.welcome_screen_activity_title),
                getResources().getString(R.string.welcome_screen_rest_details_title),
                /*getResources().getString(R.string.welcome_screen_tables_title),*/
                /*getResources().getString(R.string.welcome_screen_countries_title),*/
                getResources().getString(R.string.welcome_screen_currency_title),
                /*getResources().getString(R.string.welcome_screen_taxes_title),*/
                /*getResources().getString(R.string.welcome_screen_status_title),*/
                getResources().getString(R.string.welcome_thanks_title)
        };

        fragments.add(Fragment.instantiate(this, WelcomeLandingFrag.class.getName()));
        fragments.add(Fragment.instantiate(this, WelcomeRestaurantFrag.class.getName()));
        /*fragments.add(Fragment.instantiate(this, WelcomeTablesFrag.class.getName()));*/
        /*fragments.add(Fragment.instantiate(this, WelcomeCountriesFrag.class.getName()));*/
        fragments.add(Fragment.instantiate(this, WelcomeCurrencyFrag.class.getName()));
        /*fragments.add(Fragment.instantiate(this, WelcomeTaxesFrag.class.getName()));*/
        /*fragments.add(Fragment.instantiate(this, WelcomeStatusCheckFrag.class.getName()));*/
        fragments.add(Fragment.instantiate(this, WelcomeThanksFrag.class.getName()));

        /** INSTANTIATE THE PAGER ADAPTER **/
        adapter = new SlidingAdapter(getSupportFragmentManager(), fragments);

        /** THE TAB LAYOUT **/
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);
        tabLayout.setBackgroundColor(getResources().getColor(R.color.primaryColorDark));

        /** CONFIGURE THE TOOLBAR **/
        Toolbar toolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);

        SpannableString s = new SpannableString(getResources().getString(R.string.welcome_container_tb));
        s.setSpan(new TypefaceSpan(
                this, "RobotoCondensed-Regular.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(s);
        getSupportActionBar().setSubtitle(null);
    }

    private class SlidingAdapter extends FragmentPagerAdapter {

        /* INSTANCE OF LIST OF FRAGMENTS TO DISPLAY IN THE VIEW PAGER */
        private final List<Fragment> fragment;

        public SlidingAdapter(FragmentManager supportFragmentManager, List<Fragment> fragments) {
            super(supportFragmentManager);

			/* CAST THE FRAGMENTS INSTANCE */
            this.fragment = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return this.fragment.get(position);
        }

        @Override
        public int getCount() {
            return this.fragment.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }

    public ViewPager getPager()	{
        return pager;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:

                this.finish();

                break;

            default:
                break;
        }

        return false;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}