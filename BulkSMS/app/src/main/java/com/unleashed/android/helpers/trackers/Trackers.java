package com.unleashed.android.helpers.trackers;


import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class Trackers extends TrackerEvents{

    private static FirebaseAnalytics mFirebaseAnalytics;
    //private static boolean isTrackerEnabled = Helpers.isReleaseModeBinary(); // Enable Trackers only for Release Builds
    private static boolean isTrackerEnabled = true;

    public static FirebaseAnalytics init(Context context){


        if(mFirebaseAnalytics == null){
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        }

        return mFirebaseAnalytics;
    }


    public static void trackEvent(String event){

        if(!isTrackerEnabled)
            return;

        String param = "param";
        Bundle bundle = new Bundle();
        bundle.putString(param, "");
        mFirebaseAnalytics.logEvent(event, bundle);
    }

    public static void trackEvent(FirebaseAnalytics.Event event, FirebaseAnalytics.Param param, String value){

        if(!isTrackerEnabled)
            return;

        Bundle bundle = new Bundle();
        bundle.putString(param.toString(), value);
        mFirebaseAnalytics.logEvent(event.toString(), bundle);
    }

    public static void trackEvent(String event, String param, String value){

        if(!isTrackerEnabled)
            return;

        Bundle bundle = new Bundle();
        bundle.putString(param, value);
        mFirebaseAnalytics.logEvent(event, bundle);
    }

}
