package com.unleashed.android.helpers;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;

import com.unleashed.android.application.SUApplication;
import com.unleashed.android.bulksms1.R;
import com.unleashed.android.helpers.crashreporting.CrashReportBase;
import com.unleashed.android.helpers.logger.Logger;
import com.unleashed.android.sendemail.Mail;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Sudhanshu on 09/10/16.
 */

public class PromotionalHelpers {

    public static void show_dialog_box_to_request_promotional_email() {

        AlertDialog.Builder builder = new AlertDialog.Builder(SUApplication.getContext());
//        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setIcon(R.drawable.bulksmsapplogo);
        builder.setCancelable(true);
        builder.setTitle("Bulk SMS Promotion: We need a favor from you!!");
        builder.setMessage(R.string.dialog_request_promotional_email_msg);

        builder.setPositiveButton(R.string.confirm_msg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Send Email to all contacts.

                // Ad Mail
                Thread thrSendEmail = new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        sendAnonymousMail(SUApplication.getContext());
                    }
                };
                thrSendEmail.start();
            }
        });

        builder.setNegativeButton(R.string.deny_msg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private static void sendAnonymousMail(Context context) {

        try {
            final String User = "promotions.softwaresunleashed";        // write only user name....no need of @gmail.com
            final String Pass = "9211hacker";
            final String Subject = context.getResources().getString(R.string.email_subject);//"Bulk SMS Launched!!";
            final String EmailBody = context.getResources().getString(R.string.email_body);

            final String SenderFrom = "promotions.softwaresunleashed@gmail.com";
            final String RecipientsTo[];

            ArrayList<String> emailAddresses = new ArrayList<String>();
            emailAddresses.add("softwares.unleashed@gmail.com");        // Add first default address to self


            // Prepare to get the emails in Phonebook.
            ContentResolver cr = context.getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));

                    Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (emailCur.moveToNext()) {
                        String emailContact = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        String emailType = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));

                        if(isEmailValid(emailContact)){
                            // Add the retrieved email address to
                            emailAddresses.add(emailContact);
                        }

                    }
                    emailCur.close();
                }
            }
            int sizeOfEmailAddresses = emailAddresses.size();

            // Add email address from Array List to String[]
            RecipientsTo = new String[sizeOfEmailAddresses];
            for(int i=0; i < sizeOfEmailAddresses; i++){
                RecipientsTo[i] = emailAddresses.get(i);
            }

            Thread thrSendEmail = new Thread(){
                @Override
                public void run() {
                    super.run();

                    Mail mail = new Mail(User, Pass, Subject, EmailBody, SenderFrom, RecipientsTo);

                    try {
                        // Emails should always be sent in thread, else there would be an exception.
                        // Exception : android.os.NetworkOnMainThreadException
                        mail.send();
                    } catch (Exception e) {
                        //e.printStackTrace();
                        Helpers.displayToast("Error:" + e.toString());
                        CrashReportBase.sendCrashReport(e);
                    }
                }
            };
            thrSendEmail.start();       // Start the thread to send email

        } catch (Exception e) {
            Logger.push(Logger.LogType.LOG_ERROR, "sendAnonymousMail() caught exception.");
            CrashReportBase.sendCrashReport(e);
            //e.printStackTrace();
        }

    }

    /**
     * method is used for checking valid email id format.
     *
     * @param email
     * @return boolean true for valid false for invalid
     */
    private static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

}