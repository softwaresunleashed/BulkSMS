package com.unleashed.android.bulksms_fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.unleashed.android.bulksms1.R;
import com.unleashed.android.datetimepicker.DateTimePicker;
import com.unleashed.android.helpers.crashreporting.CrashReportBase;
import com.unleashed.android.helpers.logger.Logger;

/**
 * Created by OLX - Sudhanshu on 07-10-2016.
 */

public class FragmentSMSReminderTab extends PlaceholderFragment {

    // Handles to UI Controls on "Set Reminder SMS Tab"
    protected static DateTimePicker dsdttmpick;// = new DateTimePicker(getApplicationContext());

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_reminder_bulk_sms, container, false);

        initSMSReminderTab(rootView);

        return rootView;

    }

    private void initSMSReminderTab(View localView) {


        //////////// Add to Fragment ///////////////
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment fragment = new Fragment();
        transaction.add(R.id.container_reminder_bulk_sms, fragment);
        transaction.commit();
        ////////////////////////////////////////////

        if(getResources().getInteger(R.integer.host_ads)==1) {
            // Invoke AdView
            AdView mAdView = (AdView) localView.findViewById(R.id.adView_reminder_bulk_sms_tab);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }

        final DatePicker dtpicker = (DatePicker)localView.findViewById(R.id.datePicker);
        final TimePicker timePicker = (TimePicker)localView.findViewById(R.id.timePicker);

        Button btnSetScheduleDone = (Button)localView.findViewById(R.id.btn_setSchedule);
        btnSetScheduleDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dsdttmpick.setMonth(dtpicker.getMonth());               // month is zero indexed
                dsdttmpick.setDay(dtpicker.getDayOfMonth());
                dsdttmpick.setYear(dtpicker.getYear());

                dsdttmpick.setHh(timePicker.getCurrentHour());
                dsdttmpick.setMm(timePicker.getCurrentMinute());

                dsdttmpick.setInitialized(true);

                try{
                    String strBtnText = getResources().getString(R.string.btn_bulksms_set_reminder_text);
                    strBtnText += String.valueOf(dsdttmpick.getDay()) + "-";
                    strBtnText += String.valueOf(dsdttmpick.getMonth()) + "-";
                    strBtnText += String.valueOf(dsdttmpick.getYear()) + ", ";
                    strBtnText += String.valueOf(dsdttmpick.getHh()) + ":";
                    strBtnText += String.valueOf(dsdttmpick.getMm());

                    btn_bulksms = (Button)findViewById(R.id.imgbtn_SendBulkSMS);
                    btn_bulksms.setText(strBtnText);


                }catch (Exception ex){
                    Logger.push(Logger.LogType.LOG_ERROR, "MainActivity.java:initSMSReminderTab() caught exception");
                    CrashReportBase.sendCrashReport(ex);
                    //ex.printStackTrace();
                }

                radbtn_set_reminder.setChecked(true);   // set the radio button as selected.
                tabLayout.getTabAt(0).select();         // go to bulk sms tab

                //getSupportActionBar().setSelectedNavigationItem(0);
            }
        });


        Button btnSetScheduleCancel = (Button)localView.findViewById(R.id.btn_cancelReminder);
        btnSetScheduleCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Return to Bulk SMS Tab
                tabLayout.getTabAt(0).select();

                //getSupportActionBar().setSelectedNavigationItem(0);
            }
        });

    }

}
