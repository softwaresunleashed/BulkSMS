package com.unleashed.android.bulksmsvendors;

import android.util.Log;

import com.unleashed.android.helpers.crashreporting.CrashReportBase;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by sudhanshu on 29/10/17.
 * Visit Site : https://msg91.com/sms-for-developers
 * U : sudhanshugupta
 * P : 9211hacker
 */

public class Msg91Com {

    String AUTH_KEY_MSG91 = "181244AV2b1RFKNE59f5949d";


    //public void SendMessage(String username, String password, String message){
    public void SendMessage(){

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {

                //Android SMS API integration code

                //Your authentication key
                String authkey = AUTH_KEY_MSG91;
                //Multiple mobiles numbers separated by comma
                String mobiles = "8800588188";
                //Sender ID,While using route4 sender id should be 6 characters long.
                String senderId = "SOFTUN";
                //Your message to send, Add URL encoding here.
                String message = "Test message";
                //define route
                String route="default";

                URLConnection myURLConnection = null;
                URL myURL = null;
                BufferedReader reader = null;

                //encoding message
                String encoded_message = URLEncoder.encode(message);

                //Send SMS API
                String mainUrl = "https://control.msg91.com/api/sendhttp.php?";

                //Prepare parameter string
                StringBuilder sbPostData= new StringBuilder(mainUrl);
                sbPostData.append("authkey=" + authkey);
                sbPostData.append("&mobiles=" + mobiles);
                sbPostData.append("&message=" + encoded_message);
                sbPostData.append("&route=" + route);
                sbPostData.append("&sender=" + senderId);

                //final string
                mainUrl = sbPostData.toString();
                try
                {
                    //prepare connection
                    myURL = new URL(mainUrl);
                    myURLConnection = myURL.openConnection();
                    myURLConnection.connect();
                    reader = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));

                    //reading response
                    String response;
                    while ((response = reader.readLine()) != null)
                        //print response
                        Log.d("RESPONSE : ", ""+response);

                    //finally close connection
                    reader.close();
                }
                catch (Exception e)
                {
                    CrashReportBase.sendCrashReport(e);
                    e.printStackTrace();
                }

            }
        });

        thread.start();

    }


}
