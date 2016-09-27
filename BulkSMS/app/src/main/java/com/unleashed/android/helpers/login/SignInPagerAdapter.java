package com.unleashed.android.helpers.login;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.List;


public class SignInPagerAdapter extends FragmentStatePagerAdapter {

    private List<String> items;
    private static SparseArray<Fragment> registeredFragments = new SparseArray<>();

    public SignInPagerAdapter(FragmentManager fm, List<String> items) {
        super(fm);
        this.items = items;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            // ORIG CODE
//            case 0:
//                return MobileSignInFragment.newInstance();
//            case 1:
//                return EmailSignInFragment.newInstance();

            // Sudhanshu Modified
            case 0:
                return EmailSignInFragment.newInstance();
        }

        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return items.get(position);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}
