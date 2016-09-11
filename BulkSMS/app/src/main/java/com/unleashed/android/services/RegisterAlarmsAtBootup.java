package com.unleashed.android.services;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;


import com.unleashed.android.datetimepicker.AlarmTask;
import com.unleashed.android.datetimepicker.DateTimePicker;
import com.unleashed.android.datetimepicker.ScheduleClient;
import com.unleashed.android.helpers.dbhelper.DBHelper;

import java.util.Calendar;

public class RegisterAlarmsAtBootup extends Service{

    // This is a handle so that we can call methods on our service
    private ScheduleClient scheduleClient;

    public RegisterAlarmsAtBootup() {     }

    @Override
    public void onCreate() {
        super.onCreate();


    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        Log.v("Bulk SMS: ", "Service Started successfully");


        // Read DB file and register alarms according to job id
        final DBHelper localDBHelperObj = new DBHelper(getApplicationContext());   //getBulkSMSDBobj();

        DateTimePicker dsdttmpick = null;

        // Create a new calendar set to the date chosen
        // we set the time to time+date selected by user
        Calendar calendar_date = Calendar.getInstance();

        // Get all pending Jobs
        Cursor cur = localDBHelperObj.retrieveAllJobs();
        if (cur.moveToFirst()) {
            do {
                String jobId = cur.getString(1);        // Get Job ID


                // Decoding JobID to extract Date & Time details
                int year = Integer.valueOf(jobId.substring(0, 3+1));
                int month = strToIntMonth(jobId.substring(4, 6+1));
                int day = Integer.valueOf(jobId.substring(7, 8+1));
                int hour = Integer.valueOf(jobId.substring(9,10+1));
                int minute = Integer.valueOf(jobId.substring(11));

                Log.v("Bulk SMS: ", "Registering Alarm : " + year + "-" + String.valueOf(month+1) + "-" + day + "/" + hour + ":" + minute);

                calendar_date.set(year, month, day);
                calendar_date.set(Calendar.HOUR_OF_DAY, hour);
                calendar_date.set(Calendar.MINUTE, minute);
                calendar_date.set(Calendar.SECOND, 0);


                // This starts a new thread to set the alarm
                // You want to push off your tasks onto a new thread to free up the UI to carry on responding
                new AlarmTask(getApplicationContext(), calendar_date, jobId).run();


                // Ask our service to set an alarm for that date, this activity talks to the client that talks to the service
                //scheduleClient.setAlarmForNotification(calendar_date, jobId);

                //Toast.makeText(this, "\nEmployee ID: " + c.getString(0) + "\nEmployee Name: " + c.getString(1) + "\nEmployee Salary: " + c.getString(2), Toast.LENGTH_LONG).show();
            }
            while (cur.moveToNext());
        }

            // Set an alarm and a notification to be raised when alarm goes off
        //dsdttmpick.setMessageReminder(scheduleClient, jobid_from_date);


        // Stop the service when we are finished
        //stopSelf();

        // We don't care if this service is stopped as we have already delivered our notification
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

//        // When our activity is stopped ensure we also stop the connection to the service
//        // this stops us leaking our activity into the system *bad*
//        if(scheduleClient != null)
//            scheduleClient.doUnbindService();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private DateTimePicker extract_date_time_from_jobid(String jobId) {
        DateTimePicker localDtTmObj = new DateTimePicker(getApplicationContext());

        // Decoding JobID to extract Date & Time details
        int year = Integer.valueOf(jobId.substring(0, 3));
        int month = strToIntMonth(jobId.substring(4, 6));
        int day = Integer.valueOf(jobId.substring(7, 8));
        int hour = Integer.valueOf(jobId.substring(9,10));
        int minute = Integer.valueOf(jobId.substring(11,12));

        localDtTmObj.setYear(year);
        localDtTmObj.setMonth(month);
        localDtTmObj.setDay(day);
        localDtTmObj.setHh(hour);
        localDtTmObj.setMm(minute);


        return localDtTmObj;
    }

    private int strToIntMonth(String substring) {
        int month = -1;
        switch (substring){
            case "Jan":
                month = 0;
                break;
            case "Feb":
                month = 1;
                break;
            case "Mar":
                month = 2;
                break;
            case "Apr":
                month = 3;
                break;
            case "May":
                month = 4;
                break;
            case "Jun":
                month = 5;
                break;
            case "Jul":
                month = 6;
                break;
            case "Aug":
                month = 7;
                break;
            case "Sep":
                month = 8;
                break;
            case "Oct":
                month = 9;
                break;
            case "Nov":
                month = 10;
                break;
            case "Dec":
                month = 11;
                break;
        }


        return month;
    }

}

