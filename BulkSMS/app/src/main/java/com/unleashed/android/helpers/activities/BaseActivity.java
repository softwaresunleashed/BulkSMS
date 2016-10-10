package com.unleashed.android.helpers.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;

import com.unleashed.android.helpers.Helpers;
import com.unleashed.android.helpers.crashreporting.CrashReportBase;

import java.util.List;


public class BaseActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private boolean doubleBackToExitPressedOnce = false;


    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        CrashReportBase.sendLog("onCreate : " + this.getClass().getSimpleName() + " hash : " + this.hashCode());
    }

    @Override
    protected void onResume() {
        super.onResume();
        CrashReportBase.sendLog("onResume : " + this.getClass().getSimpleName() + " hash : " + this.hashCode());
    }

    @Override
    protected void onPause() {
        super.onPause();
        CrashReportBase.sendLog("onPause : " + this.getClass().getSimpleName() + " hash : " + this.hashCode());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CrashReportBase.sendLog("onDestroy : " + this.getClass().getSimpleName() + " hash : " + this.hashCode());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        CrashReportBase.sendLog("onRequestPermissionsResult : " + this.getClass().getSimpleName() + " hash : " + this.hashCode());

        List<Fragment> childFragments = getSupportFragmentManager().getFragments();
        if (childFragments != null) {
            for (Fragment childFragment : childFragments) {
                if (childFragment != null)
                    childFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Helpers.displayToast("Please click BACK again to exit");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}