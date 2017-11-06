package com.unleashed.android.bulksmsvendors;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.unleashed.android.helpers.Helpers;
import com.unleashed.android.helpers.crashreporting.CrashReportBase;
import com.unleashed.android.helpers.logger.Logger;

import java.util.concurrent.Semaphore;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by sudhanshu on 04/11/17.
 */

public class PhoneSMSManager extends BulkSMSVendorsBase {




    public void SendMessage(final Context mContext, final String[] phoneNumbers, final String message){

        try{
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
                        case RESULT_OK:
                            Toast.makeText(mContext, "SMS sent", Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            Toast.makeText(mContext, "Generic failure", Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_NO_SERVICE:
                            Toast.makeText(mContext, "No service", Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_NULL_PDU:
                            Toast.makeText(mContext, "Null PDU", Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            Toast.makeText(mContext, "Radio off", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }, new IntentFilter(smsSent));

            // Receiver for Delivered SMS.
            mContext.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context arg0, Intent arg1) {
                    switch (getResultCode()) {
                        case RESULT_OK:
                            Toast.makeText(mContext, "SMS delivered", Toast.LENGTH_SHORT).show();
                            break;

                        case RESULT_CANCELED:
                            Toast.makeText(mContext, "SMS Undelivered", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }, new IntentFilter(smsDelivered));


            // Get default SMS Manager's handler
            SmsManager smsOperation = SmsManager.getDefault();


            // Send SMS one by one by extracting all numbers.
            for(int i=0; i < phoneNumbers.length; i++ ){

                // Finally Send SMS to all numbers in list view
                smsOperation.sendTextMessage(phoneNumbers[i], null, message, sentPI, deliveredPI);

                Helpers.displayToast("Sending SMS(s)...Please Wait.");

                final int MAX_AVAILABLE = 0;
                final Semaphore sms_sent = new Semaphore(MAX_AVAILABLE, true);
                Thread thrButtonEnable = new Thread(){
                    @Override
                    public void run() {
                        super.run();

                        try{
                            sleep(3000);
                            // release the semaphore
                            sms_sent.release();
                        }catch (Exception ex){
                            Logger.push(Logger.LogType.LOG_ERROR, "PhoneSMSManager.java:SendMessage() thrButtonEnable thread caught exception1");
                            CrashReportBase.sendCrashReport(ex);
                            //ex.printStackTrace();
                        }
                    }
                };
                thrButtonEnable.start();        // Start the thread to clear sms text mesg and phone list.

                try {
                    sms_sent.acquire();            // wait here till semaphore is released from thread

                } catch (InterruptedException e) {
                    CrashReportBase.sendCrashReport(e);
                    //e.printStackTrace();
                }
            }

        }catch (Exception ex){
            Logger.push(Logger.LogType.LOG_ERROR, "PhoneSMSManager.java:SendMessage() caught exception");
            CrashReportBase.sendCrashReport(ex);
        }
    }


}
