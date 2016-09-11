package com.unleashed.android.application;

import android.app.Application;
import android.content.Context;

/**
 * Created by sudhanshu on 10/09/16.
 */

public class SUApplication extends Application {
    private static Context context;


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