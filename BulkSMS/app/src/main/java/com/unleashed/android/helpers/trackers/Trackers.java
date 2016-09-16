package com.unleashed.android.helpers.trackers;


import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class Trackers extends TrackerEvents{

    private static FirebaseAnalytics mFirebaseAnalytics;

    public static FirebaseAnalytics init(Context context){

        if(mFirebaseAnalytics == null){
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        }

        return mFirebaseAnalytics;
    }


    public static void trackEvent(String event){
        String param = "param";
        Bundle bundle = new Bundle();
        bundle.putString(param, "");
        mFirebaseAnalytics.logEvent(event, bundle);
    }

    public static void trackEvent(FirebaseAnalytics.Event event, FirebaseAnalytics.Param param, String value){
        Bundle bundle = new Bundle();
        bundle.putString(param.toString(), value);
        mFirebaseAnalytics.logEvent(event.toString(), bundle);
    }

    public static void trackEvent(String event, String param, String value){
        Bundle bundle = new Bundle();
        bundle.putString(param, value);
        mFirebaseAnalytics.logEvent(event, bundle);
    }

}
