package com.unleashed.android.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootupReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        Intent service = new Intent(context, RegisterAlarmsAtBootup.class);
        context.startService(service);

        Log.v("Bulk SMS: ", "BootupReceiver.java:onReceive() - Alarm Register Service loaded at bootup");

    }
}
