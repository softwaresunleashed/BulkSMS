package com.unleashed.android.helpers.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;


import com.unleashed.android.bulksms1.R;
import com.unleashed.android.helpers.activities.BaseActivity;

import java.util.Arrays;


public class SignInTabActivity extends BaseActivity {

    public static final int REQUEST_CODE = EmailSignInFragment.REQUEST_CODE;
    private TabLayout signInTabLayout;
    private ViewPager signInPager;

    private SignInPagerAdapter pagerAdapter;

    private static String[] signInOptions = new String[]{"Email Login"};
    //private static String[] signInOptions = new String[]{"Mobile Number", "Email"};

    public static void startSignInActivityForResult(Fragment fragment) {
        Intent intent = new Intent(fragment.getActivity(), SignInTabActivity.class);
        fragment.startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signin);

        signInTabLayout = (TabLayout) findViewById(R.id.tabs_signin);
        signInPager = (ViewPager) findViewById(R.id.pager_signin);

        pagerAdapter = new SignInPagerAdapter(getSupportFragmentManager(), Arrays.asList(signInOptions));
        signInPager.setAdapter(pagerAdapter);
        signInTabLayout.setupWithViewPager(signInPager);

        signInPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            Fragment fragment;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                /*fragment = pagerAdapter.getRegisteredFragment(position);

                if (fragment instanceof EmailSignInFragment) {
                    ((EmailSignInFragment) fragment).resetViews();
                    Trackers.trackEvent(SignInTabActivity.this, Trackers.EVENT_LOGIN_EMAIL_SELECT);
                }

                if (fragment instanceof MobileSignInFragment)
                    ((MobileSignInFragment) fragment).resetViews();*/

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.login_now);
        actionBar.setElevation(0);
        actionBar.setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
