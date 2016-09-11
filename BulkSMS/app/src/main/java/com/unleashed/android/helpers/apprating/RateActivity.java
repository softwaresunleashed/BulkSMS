package com.unleashed.android.helpers.apprating;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.unleashed.android.bulksms1.R;
import com.unleashed.android.helpers.Preferences;
import com.unleashed.android.helpers.config.Config;


public class RateActivity extends AppCompatActivity {
    protected static final int DAYS_TO_RATE = 7;
    Context context;

    public static void startRateActivityIfDaysAchieved(Context context) {
        if (shouldStart(context)) {
            startRateActivity(context);
        }
    }

    public static void startRateActivity(Context context) {
        Intent intent = new Intent(context, RateActivity.class);
        context.startActivity(intent);
    }

    protected static boolean shouldStart(Context context) {
        return true;
//        Long daysDiff = getDaysFromInstallation(context);
//        Long now = System.currentTimeMillis();
//        return (!Config.isRateDialogDisplayed && !Preferences.containsKey(context, Preferences.getRatedKey(context)) && ((now - daysDiff) / 86400000) >= DAYS_TO_RATE);
    }

    protected static Long getDaysFromInstallation(Context context) {
        Long installationTime = Preferences.getAppPreference(context, Preferences.getRateKey(context), 0L);
        if (installationTime == 0) {
            installationTime = setRunTimeInPreferences(context);
        }
        return installationTime;
    }

    protected static long setRunTimeInPreferences(Context context) {
        Long installationTime = System.currentTimeMillis();
        Preferences.setAppPreference(context, Preferences.getRateKey(context), installationTime);
        return installationTime;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        getSupportActionBar().setTitle(getString(R.string.rate));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = this;
        if (savedInstanceState == null) {
            Config.isRateDialogDisplayed = true;
            setRunTimeInPreferences(context);
            addFragment();
        }
    }

    protected void addFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        RateFragment rateFragment = new RateFragment();
        ft.replace(R.id.container, rateFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}