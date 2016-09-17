package com.unleashed.android.helpers.SplashScreen;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;

import com.unleashed.android.bulksms1.R;
import com.unleashed.android.helpers.crashreporting.CrashReportBase;
import com.unleashed.android.helpers.logger.Logger;

/**
 * Created by sudhanshu on 17/09/16.
 */

public class SplashScreen {

    private static final String TAG = SplashScreen.class.getSimpleName();

    public static void display_splash_screen(Activity activity) {

        try{
            // custom dialog
//            final Dialog dialog = new Dialog(SUApplication.getContext());
            final Dialog dialog = new Dialog(activity);
            dialog.setContentView(R.layout.welcome_splash_screen);
            dialog.setTitle("Bulk SMS");
            dialog.setCancelable(true);
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    Thread thrDialogClose = new Thread(){
                        @Override
                        public void run() {
                            super.run();

                            try {
                                sleep(3000);        // Let the dialog be displayed for 3 secs
                            } catch (InterruptedException e) {
                                CrashReportBase.sendCrashReport(e);
                                //e.printStackTrace();
                            }
                            dialog.dismiss();
                        }
                    };
                    thrDialogClose.start();
                }
            });
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();


        }catch (Exception ex){
            Logger.push(Logger.LogType.LOG_ERROR, TAG + " MainActivity.java:display_splash_screen()");
            CrashReportBase.sendCrashReport(ex);
            //ex.printStackTrace();
        }
    }


}
