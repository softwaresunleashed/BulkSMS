package com.unleashed.android.helpers.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.unleashed.android.helpers.crashreporting.CrashReportBase;


public class BaseFragment extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CrashReportBase.sendLog("onCreate : "+this.getClass().getSimpleName() + " hash : "+this.hashCode());
    }

    @Override
    public void onResume() {
        super.onResume();
        CrashReportBase.sendLog("onResume : "+this.getClass().getSimpleName() + " hash : "+this.hashCode());
    }

    @Override
    public void onPause() {
        super.onPause();
        CrashReportBase.sendLog("onPause : "+this.getClass().getSimpleName() + " hash : "+this.hashCode());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        CrashReportBase.sendLog("onActivityCreated : "+this.getClass().getSimpleName() + " hash : "+this.hashCode());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CrashReportBase.sendLog("onDestroy : "+this.getClass().getSimpleName() + " hash : "+this.hashCode());
    }
}
