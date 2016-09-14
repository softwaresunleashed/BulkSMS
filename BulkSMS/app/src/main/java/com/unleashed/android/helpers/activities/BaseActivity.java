package com.unleashed.android.helpers.activities;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;

import java.util.List;


public class BaseActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> childFragments = getSupportFragmentManager().getFragments();
        if (childFragments != null) {
            for (Fragment childFragment : childFragments) {
                if (childFragment != null)
                    childFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }
}