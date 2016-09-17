package com.unleashed.android.application;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;

import com.unleashed.android.helpers.crashreporting.CrashReportBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sudhanshu on 10/09/16.
 */

public class SUApplication extends Application {
    private static Context context;
    public static final String DEFAULT_FONT = "Roboto-Regular.ttf";


    private static Map<String, Typeface> sTypefaces = new HashMap<String, Typeface>();
    public static Map<String, Typeface> getTypefaces() {
        return sTypefaces;
    }



    public SUApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        init();
    }

    public synchronized void init() {
        SUApplication.context = getApplicationContext();

        // Init Crash Report Sub-System
        CrashReportBase.initCrashReportModule();

    }

    public static Context getContext() {
        return context;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
