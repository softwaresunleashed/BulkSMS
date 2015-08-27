package com.unleashed.android.bulksms2;

import android.content.Context;
import android.content.res.Resources;

import com.unleashed.android.bulksms2.R;

/**
 * Created by gupta on 7/19/2015.
 */
public final class AboutApp {

    private String mVersionNumber;
    private String mAboutApp;
    private String mVersionHistory;
    private Resources resources;


    public AboutApp(Context ctx) {
        resources = ctx.getResources();

    }

    public String getVersionNumber(){

        mVersionNumber = resources.getText(R.string.app_version).toString();
        return mVersionNumber;
    }

    public String getVersionHistory(){
        mVersionHistory = resources.getText(R.string.version_history).toString();
        return mVersionHistory;
    }

    public String getAboutApp(){
        mAboutApp = resources.getText(R.string.app_about).toString();
        return mAboutApp;
    }

    public Resources getResources() {
        return resources;
    }
}
