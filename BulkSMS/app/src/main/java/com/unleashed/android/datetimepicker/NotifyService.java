package com.unleashed.android.datetimepicker;

/**
 * Created by gupta on 8/4/2015.
 */

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;


import com.unleashed.android.bulksms2.MainActivity;
import com.unleashed.android.bulksms2.R;
import com.unleashed.android.dbhelper.DBHelper;

import java.sql.SQLException;
import java.util.concurrent.Semaphore;

/**
 * This service is started when an Alarm has been raised
 *
 * We pop a notification into the status bar for the user to click on
 * When the user clicks the notification a new activity is opened
 *
 * @author paul.blundell
 */

// To restart android device without pressing power button (No need for root priveledges).
// $ am broadcast -a android.intent.action.BOOT_COMPLETED

public class NotifyService extends Service {

    /**
     * Class for clients to access
     */
    public class ServiceBinder extends Binder {
        NotifyService getService() {
            return NotifyService.this;
        }
    }

    // Unique id to identify the notification.
    private static final int NOTIFICATION = 123;
    // Name of an intent extra we can use to identify if this service was started to create a notification
    public static final String INTENT_NOTIFY = "com.unleashed.android.datetimepicker.INTENT_NOTIFY";
    public static final String INTENT_JOBID = "com.unleashed.android.datetimepicker.INTENT_JOBID";
    // The system notification manager
    private NotificationManager mNM;

   // private String JOBID;

    @Override
    public void onCreate() {
        Log.i("Bulk SMS: ", "NotifyService : onCreate()");
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("Bulk SMS: ", "NotifyService :onStartCommand() - Received start id " + startId + ": " + intent);

        // Get the JOBID for which notification has been triggered.
        String JOBID = intent.getStringExtra(INTENT_JOBID);
        Log.i("Bulk SMS: ", "NotifyService :onStartCommand() - JOBID=" + JOBID);

        // If this service was started by out AlarmTask intent then we want to show our notification
        if(intent.getBooleanExtra(INTENT_NOTIFY, false))
            showNotification(JOBID);

        // We don't care if this service is stopped as we have already delivered our notification
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients
    private final IBinder mBinder = new ServiceBinder();

    /**
     * Creates a notification and shows it in the OS drag-down status bar
     */
    private void showNotification(String jobID) {

        Log.i("Bulk SMS: ", "NotifyService.java:showNotification()");

        // This is the 'title' of the notification
        CharSequence title = "Bulk SMS!!";
        // This is the icon to use on the notification
        int icon = R.drawable.bulksmsapplogo;
        // This is the scrolling text of the notification
        CharSequence text = "Scheduled Bulk SMS Job# " + jobID + " activated.";
        // What time to show on the notification
        long time = System.currentTimeMillis();

        Notification notification = new Notification(icon, text, time);

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, title, text, contentIntent);

        // Clear the notification when it is pressed
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        // Send the notification to the system.
        mNM.notify(NOTIFICATION, notification);

        // Process JOB ID after displaying notification
        process_job_id(jobID);

        // Stop the service when we are finished
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i("Bulk SMS: ", "NotifyService.java:onDestroy()");

        // Stop the service when we are finished
        //stopSelf();
    }

    private void process_job_id(final String jobid) {

        final DBHelper localDBHelperObj = new DBHelper(getApplicationContext());   //getBulkSMSDBobj();

        Thread thrSendBackGroundSMS = new Thread(){
            @Override
            public void run() {
                super.run();

                Cursor c = null;
                try {
                        c = localDBHelperObj.retrieveJob(jobid);
                        if (c != null) {
                    //        do{

                                String PhoneNumbers = c.getString(2);
                                String SMS_Message = c.getString(3);

                                // Start sending sms messages in background
                                delegateMessageSending(PhoneNumbers, SMS_Message);

                                Log.i("Bulk SMS: ", "NotifyService.java:process_job_id() - JOBID=" + jobid);

                                // Delete reference of JOBID already processed.
                                localDBHelperObj.deleteJob(jobid);
                //           }while(c.moveToNext());
                        }
                } catch (Exception e) {
                    Log.e("Bulk SMS: ", "NotifyService.java - process_job_id(): caught exception");
                    e.printStackTrace();
                }
            }
        };
        thrSendBackGroundSMS.start();


    }

    private void delegateMessageSending(String phoneNumbers, String sms_message) {

        try{

            final Context mContext = getApplicationContext();

            String smsSent = "SMS_SENT";
            String smsDelivered = "SMS_DELIVERED";

            Intent smsSentIntent = new Intent(smsSent);
            Intent smsDeliveredIntent = new Intent(smsDelivered);

//            smsSentIntent.setAction("com.unleashed.android.bulksms");
//            smsDeliveredIntent.setAction("com.unleashed.android.bulksms");

            PendingIntent sentPI = PendingIntent.getBroadcast(mContext, 0, smsSentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent deliveredPI = PendingIntent.getBroadcast(mContext, 0,smsDeliveredIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            mContext.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context arg0, Intent arg1) {
                    switch (getResultCode()) {
                        case Activity.RESULT_OK:
                            Toast.makeText(mContext, "Bulk SMS: SMS sent", Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            Toast.makeText(mContext, "Bulk SMS: Generic failure", Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_NO_SERVICE:
                            Toast.makeText(mContext, "Bulk SMS: No service", Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_NULL_PDU:
                            Toast.makeText(mContext, "Bulk SMS: Null PDU", Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            Toast.makeText(mContext, "Bulk SMS: Radio off", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }, new IntentFilter(smsSent));

            // Receiver for Delivered SMS.
            mContext.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context arg0, Intent arg1) {
                    switch (getResultCode()) {
                        case Activity.RESULT_OK:
                            Toast.makeText(mContext, "Bulk SMS: SMS delivered", Toast.LENGTH_SHORT).show();
                            break;

                        case Activity.RESULT_CANCELED:
                            Toast.makeText(mContext, "Bulk SMS: SMS Undelivered", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }, new IntentFilter(smsDelivered));


            // Get default SMS Manager's handler
            SmsManager smsOperation = SmsManager.getDefault();


            // Extract the phone numbers
            String phoneNumber = phoneNumbers;
            for(int i=0; i < phoneNumbers.length();  ){

                // The phoneNumber string contains "Name <phone number>".
                // Hence we need to extract only the numbers between '<' & '>'
                int startIndex = phoneNumber.indexOf('<');
                int endIndex = phoneNumber.indexOf('>');

                if(startIndex == -1){
                    // If you reach here, means you have reached the end of the string.
                    // break from loop
                    break;
                }

                // startIndex+1 , because we need to capture from next character after '<'
                phoneNumber = phoneNumber.substring(startIndex+1, endIndex);


                // Finally Send SMS to all numbers in list view
                smsOperation.sendTextMessage(phoneNumber, null, sms_message, sentPI, deliveredPI);

                i = endIndex;
            }
        }catch (Exception e){
            Log.e("Bulk SMS: ", "NotifyService.java:delegateMessageSending() caught exception");
            e.printStackTrace();
            //Toast.makeText(MainActivity.this, "Error Sending Messages. Try Again Later.", Toast.LENGTH_SHORT).show();
        }

    }


}
