package com.unleashed.android.datetimepicker;

import android.content.Context;
import android.util.Log;


import java.util.Calendar;

/**
 * Created by gupta on 7/31/2015.
 */
public class DateTimePicker {

    private int day;
    private int month;
    private  int year;

    private int hh;
    private  int mm;

    private boolean initialized;
    private Context ctx;



    public DateTimePicker(Context applicationContext) {

        day = this.day;
        month = this.month;
        year = this.year;

        hh = this.hh;
        mm = this.mm;
        initialized = false;
        ctx = applicationContext;
    }


    public void setMessageReminder(ScheduleClient scheduleClient, String jobId) {

        try{
            // Create a new calendar set to the date chosen
            // we set the time to time+date selected by user
            Calendar calendar_date = Calendar.getInstance();
            calendar_date.set(year, month, day);
            calendar_date.set(Calendar.HOUR_OF_DAY, hh);
            calendar_date.set(Calendar.MINUTE, mm);
            calendar_date.set(Calendar.SECOND, 0);

            // Ask our service to set an alarm for that date, this activity talks to the client that talks to the service
            scheduleClient.setAlarmForNotification(calendar_date, jobId);

        }catch (Exception ex){
            Log.e("Bulk SMS: " , "DateTimePicker.java:setMessageReminder()");
			ex.printStackTrace();
        }


    }


    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getHh() {
        return String.format("%02d", hh);
    }

    public void setHh(int hh) {
        this.hh = hh;
    }

    public String getMm() {
        return String.format("%02d", mm);
    }

    public void setMm(int mm) {
        this.mm = mm;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public Context getCtx() {
        return ctx;
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }

    public String getDay() {

        return String.format("%02d", day);
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonthInt() {
        // This is zero indexed
        return month;
    }

    public String getMonth() {
        String tmpStrMonth = "";
        switch (month){
            case 0:
                tmpStrMonth = "Jan";
                break;
            case 1:
                tmpStrMonth = "Feb";
                break;
            case 2:
                tmpStrMonth = "Mar";
                break;
            case 3:
                tmpStrMonth = "Apr";
                break;
            case 4:
                tmpStrMonth = "May";
                break;
            case 5:
                tmpStrMonth = "Jun";
                break;
            case 6:
                tmpStrMonth = "Jul";
                break;
            case 7:
                tmpStrMonth = "Aug";
                break;
            case 8:
                tmpStrMonth = "Sep";
                break;
            case 9:
                tmpStrMonth = "Oct";
                break;
            case 10:
                tmpStrMonth = "Nov";
                break;
            case 11:
                tmpStrMonth = "Dec";
                break;
        }
        return tmpStrMonth;
    }




}
