package com.unleashed.android.helpers.crashreporting;

import com.google.firebase.crash.FirebaseCrash;

/**
 * Created by sudhanshu on 17/09/16.
 */

public class CrashReportBase {

    public static void initCrashReportModule(){
        // Nothing to do here as of now.
    }

    public static void sendCrashReport(Exception e){
        // Firebase Crash SDK
        FirebaseCrash.report(e);

        // Print stack trace locally
        e.printStackTrace();
    }

}
