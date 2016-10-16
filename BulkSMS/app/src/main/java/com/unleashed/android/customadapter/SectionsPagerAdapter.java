package com.unleashed.android.customadapter;

/**
 * Created by OLX - Sudhanshu on 06-10-2016.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.unleashed.android.application.SUApplication;
import com.unleashed.android.bulksms1.R;
import com.unleashed.android.bulksms_fragments.PlaceholderFragment;

import java.util.Locale;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tab/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return PlaceholderFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        // Show 4 total pages.
        return PlaceholderFragment.TABS.values().length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return SUApplication.getContext().getResources().getString(R.string.title_section1).toUpperCase(l);
            case 1:
                return SUApplication.getContext().getResources().getString(R.string.title_section2).toUpperCase(l);
            case 2:
                return SUApplication.getContext().getResources().getString(R.string.title_section3).toUpperCase(l);
            case 3:
                return SUApplication.getContext().getResources().getString(R.string.title_section4).toUpperCase(l);
        }
        return null;
    }
}
