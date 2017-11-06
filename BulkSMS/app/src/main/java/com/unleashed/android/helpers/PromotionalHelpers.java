package com.unleashed.android.helpers;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.unleashed.android.application.SUApplication;
import com.unleashed.android.bulksms1.R;
import com.unleashed.android.helpers.crashreporting.CrashReportBase;
import com.unleashed.android.helpers.logger.Logger;
import com.unleashed.android.helpers.sendemail.Mail;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Sudhanshu on 09/10/16.
 */

public class PromotionalHelpers {

    public static void show_dialog_box_to_request_promotional_email(Context context) {

        try{
            new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Bulk SMS Promotion")
                    .setContentText(context.getResources().getString(R.string.dialog_request_promotional_email_msg))
                    .setConfirmText(context.getResources().getString(R.string.confirm_msg))
                    .setCancelText(context.getResources().getString(R.string.deny_msg))
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {

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

                            sDialog
                                    .setTitleText("Sending Promo Email!")
                                    .setContentText("Thanks for taking time out to share our good work with your friends.")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(null)
                                    .showCancelButton(false)
                                    .setCancelClickListener(null)
                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        }
                    })
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.cancel();
                        }
                    })
                    .show();
        }catch (Exception ex){
            Logger.push(Logger.LogType.LOG_ERROR, "PromotionalHelpers.java:show_dialog_box_to_request_promotional_email() caught exception");
            CrashReportBase.sendCrashReport(ex);
        }


    }

    public static void tell_a_friend_via_personal_email(Context context){

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_EMAIL, "softwares.unleashed@gmail.com");
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.email_subject));
        intent.putExtra(Intent.EXTRA_TEXT, context.getResources().getString(R.string.email_body));
        context.startActivity(Intent.createChooser(intent, "Send Email"));

    }

    private static void sendAnonymousMail(Context context) {

        try {
            final String User = "promotions.softwaresunleashed";        // write only user name....no need of @gmail.com
            final String Pass = "!9211hacker";
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
