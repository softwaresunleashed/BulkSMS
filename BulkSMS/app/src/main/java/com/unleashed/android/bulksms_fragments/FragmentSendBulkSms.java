package com.unleashed.android.bulksms_fragments;


import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.unleashed.android.application.SUApplication;
import com.unleashed.android.bulksms1.R;
import com.unleashed.android.bulksms_activities.ContactBook;
import com.unleashed.android.customadapter.PhoneBookRowItem;
import com.unleashed.android.datetimepicker.DateTimePicker;
import com.unleashed.android.datetimepicker.ScheduleClient;
import com.unleashed.android.helpers.Helpers;
import com.unleashed.android.helpers.PromotionalHelpers;
import com.unleashed.android.helpers.apprating.FeedbackPromptFragment;
import com.unleashed.android.helpers.constants.Constants;
import com.unleashed.android.helpers.crashreporting.CrashReportBase;
import com.unleashed.android.helpers.dbhelper.DBHelper;
import com.unleashed.android.helpers.logger.Logger;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


/**
 * Created by OLX - Sudhanshu on 06-10-2016.
 */

public class FragmentSendBulkSms extends PlaceholderFragment implements View.OnClickListener{

    // Handles to UI Controls on "Send Bulk SMS Tab"
    private Button btn_bulksms;
    private TextView lbl_sms_char_counter;
    private ImageButton imgbtn_selectcontacts;
    private ListView lv_PhnNums;        // List view to hold selected numbers.
    private EditText et_sms_msg;        // Edit box for holding sms message.
    private RadioGroup radgrp;          // Radio Group for holding options to send sms now or set a reminder
    private RadioButton radbtn_now, radbtn_set_reminder;


    private ArrayAdapter<String> mContactsSelectedAdapter;
    private ArrayList<String> mContactsSelectedList;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_send_bulk_sms, container, false);

        initSendBulkSMSTab(rootView);

        return rootView;

    }

    private void initSendBulkSMSTab(View localView) {

        //////////// Add to Fragment ///////////////
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment fragment = new Fragment();
        String fragTag = FRAGMENT_TAG_ + PlaceholderFragment.TABS.TAB_SEND_BULK_SMS.getValue();
        transaction.add(R.id.container_send_bulk_sms, fragment, fragTag);
        transaction.commit();
        ////////////////////////////////////////////

        if(getResources().getInteger(R.integer.host_ads)==1) {
            // Invoke AdView
            AdView mAdView = (AdView) localView.findViewById(R.id.adView_send_bulk_sms_tab);
            AdRequest adRequest = new AdRequest.Builder()
                    //.addTestDevice(String.valueOf(R.string.test_device_id))
                    .build();
            mAdView.loadAd(adRequest);
        }


        lv_PhnNums = (ListView)localView.findViewById(R.id.listView_PhnNums);
        lv_PhnNums.setLongClickable(true);

//            lv_PhnNums.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    invokeContactBookActivity();
//                }
//            });

        // This code allows ListView to scroll even when ListView is inside a ScrollView
        lv_PhnNums.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        lv_PhnNums.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {

                Context context = getActivity();//SUApplication.getContext();//getActivity(); //getBaseContext(); //getApplication();
                final int pos = position;

                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(context.getResources().getString(R.string.remove_contact_from_list_title))
                        .setContentText(context.getResources().getString(R.string.remove_contact_from_list_content))
                        .setConfirmText(context.getResources().getString(R.string.btn_text_delete))
                        .setCancelText(context.getResources().getString(R.string.btn_text_cancel))
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                // Remove contact from list.
                                mContactsSelectedList.remove(pos);
                                mContactsSelectedAdapter = getContactsSelectedAdapter();
                                mContactsSelectedAdapter.notifyDataSetChanged();
                                //lv_PhnNums.setAdapter(mContactsSelectedAdapter);
                                //mContactsSelectedAdapter.notifyDataSetChanged();

                                sDialog
                                        .setTitleText("Contact Removed!")
                                        .setContentText("Contact removed from list.")
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


//
//                AlertDialog.Builder builder = new AlertDialog.Builder(cntx);
//                builder.setIcon(R.drawable.bulksmsapplogo);
//                builder.setCancelable(true);
//                builder.setTitle("Action:");
//
//
//                builder.setItems(items, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int item) {
//                        //String strPhnRec = (String) lv_PhnNums.getItemAtPosition(position);
//
//                        switch (item) {
//
//                            case 0: // "Delete"
//                                mContactsSelectedList.remove(pos);
//                                mContactsSelectedAdapter = getContactsSelectedAdapter();
//                                mContactsSelectedAdapter.notifyDataSetChanged();
//                                //lv_PhnNums.setAdapter(mContactsSelectedAdapter);
//                                //mContactsSelectedAdapter.notifyDataSetChanged();
//                                break;
//
//                            case 1: // "Cancel"
//                                dialog.dismiss();
//                                //Sudhanshu
//                                //closeContextMenu();
//                                break;
//                        }
//                    }
//                });
//
//                AlertDialog alert = builder.create();
//                alert.show();

                return false;
            }
        });


        // Send bulk sms button
        btn_bulksms = (Button)localView.findViewById(R.id.imgbtn_SendBulkSMS);
        btn_bulksms.setOnClickListener(this);

        // Select contacts button
        imgbtn_selectcontacts = (ImageButton)localView.findViewById(R.id.imgbtn_SelectContacts);
        imgbtn_selectcontacts.setOnClickListener(this);


        lbl_sms_char_counter = (TextView)localView.findViewById(R.id.lbl_SMSCharCount);

        // Adding a text watcher to SMS text box
        // (How text changes in text box should behave).
        et_sms_msg = (EditText) localView.findViewById(R.id.et_SMS_msg);
        et_sms_msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //This sets a textview to the current length
                String smsNo;
                if(charSequence.length() == 0)
                    smsNo = "0";
                else
                    smsNo = String.valueOf(charSequence.length()/160 + 1);

                String smsLength = String.valueOf(160-(charSequence.length()%160));
                lbl_sms_char_counter.setText(smsLength+"/"+smsNo);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // Enabling scrollbars inside edit text view
        et_sms_msg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                view.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction()&MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_UP:
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }


                return false;
            }
        });

        // Radio Button Group and its member radio buttons
        radgrp = (RadioGroup)localView.findViewById(R.id.radgrp_reminder);
        radbtn_set_reminder = (RadioButton)localView.findViewById(R.id.radbtn_setreminder);
        radbtn_set_reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Code to shift to 'Set Reminder' tab.
                radbtn_set_reminder.setChecked(true);

                // go to set reminder tab
                mTabLayoutCallbacks.tabSelected(PlaceholderFragment.TABS.TAB_REMINDER_SMS.getValue());
            }
        });

        radbtn_now = (RadioButton)localView.findViewById(R.id.radbtn_sendnow);
        radbtn_now.setChecked(true);
        radbtn_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Nothing needs to be done i guess for this
                radbtn_now.setChecked(true);
                btn_bulksms.setText(R.string.btn_bulksms_send_now_text);
            }
        });
    }

    private void invokeContactBookActivity() {
        Intent startContactBookAct = new Intent(this.getContext(), ContactBook.class);
        startActivityForResult(startContactBookAct, Constants.RC_OPEN_CONTACTBOOK_ACT);
    }

    private void ComposeAndSendMessage(){
        Logger.push(Logger.LogType.LOG_INFO,  "MainActivity.java:ComposeAndSendMessage()");


        String phoneNumber = "";//et_RecieverPhoneNumber.getText().toString();
        String smsMesg = et_sms_msg.getText().toString();       // Pull in the SMS text
        final Context mContext = SUApplication.getContext();


        int totalPhoneNumbers = lv_PhnNums.getCount();
        if(totalPhoneNumbers == 0){
            Toast.makeText(SUApplication.getContext(), "Select minimum of one contact...", Toast.LENGTH_LONG).show();
            return;
        }

        if(smsMesg.length() == 0){
            Toast.makeText(SUApplication.getContext(), "Enter Text to Send...", Toast.LENGTH_LONG).show();
            return;
        }

        // Check if RadioButton "Set Reminder" is checked. Then store the mesg and recipient list along with time.
        if(radbtn_set_reminder.isChecked() && DateTimePicker.getInstance().isInitialized()) {

            // Set Reminder is not available in Free Version of App
//                if(getResources().getInteger(R.integer.free_version_code)==1){
//                    alert_dialog_buy_bulk_sms();
//                    return;
//                }

            // Nothing needs to be done as of now. Just create a job and return.
            String jobId = create_job_task(totalPhoneNumbers, smsMesg);

            // Clean Phone Contacts list view after sending sms.
            int count = mContactsSelectedList.size();
            for(int itr=0; itr < count; itr++){
                mContactsSelectedList.remove(itr);
            }
            mContactsSelectedAdapter = getContactsSelectedAdapter();
            mContactsSelectedAdapter.notifyDataSetChanged();
            et_sms_msg.setText("");        // clear Edit box ,holding sms message.
            lbl_sms_char_counter.setText("160/0"); // Reset the value of sms char counter

            // Select "Send Now" Radio button
            radbtn_now.setChecked(true);
            btn_bulksms.setText(R.string.btn_bulksms_send_now_text);

            Helpers.displayToast("New Job # " + jobId + " created. \nSMS will be sent on the choosen Date and Time.");
            return;
        }

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


            // Extract the phone numbers
            for(int i=0; i < totalPhoneNumbers; i++ ){

                // Get first phone number string from List View
                phoneNumber = (String)lv_PhnNums.getItemAtPosition(i);


                // The phoneNumber string contains "Name <phone number>".
                // Hence we need to extract only the numbers between '<' & '>'
                int startIndex = phoneNumber.indexOf('<');
                int endIndex = phoneNumber.indexOf('>');
                // startIndex+1 , because we need to capture from next character after '<'
                phoneNumber = phoneNumber.substring(startIndex+1, endIndex);



                // Finally Send SMS to all numbers in list view
                smsOperation.sendTextMessage(phoneNumber, null, smsMesg, sentPI, deliveredPI);

                //btn_bulksms.setVisibility(ImageButton.INVISIBLE);           // disable send button for some time, so that user doesnt click it again.


                if(getResources().getInteger(R.integer.free_version_code)==1){
                    // Show dialog box to request access to sending promotional email
                    PromotionalHelpers.show_dialog_box_to_request_promotional_email(getActivity());
                }

                // Display msg for "Sending SMS"
//                    Toast tstmsg = new Toast(MainActivity.this);
//                    tstmsg.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
//                    tstmsg.makeText(MainActivity.this, "Sending Messages...Please Wait.", Toast.LENGTH_LONG);
//                    tstmsg.show();

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
                            Logger.push(Logger.LogType.LOG_ERROR, "MainActivity.java:ComposeAndSendMessage() caught exception1");
                            CrashReportBase.sendCrashReport(ex);
                            //ex.printStackTrace();
                        }
                    }
                };
                thrButtonEnable.start();        // Start the thread to clear sms text mesg and phone list.

                try {
                    sms_sent.acquire();            // wait here till semaphore is released from thread

                    //btn_bulksms.setVisibility(ImageButton.VISIBLE);        // Enable the "Send SMS" button after 2secs

                    // Clean Phone Contacts list view after sending sms.
                    int count = mContactsSelectedList.size();
                    for(int itr=0; itr < count; itr++){
                        mContactsSelectedList.remove(itr);
                    }
                    mContactsSelectedAdapter = getContactsSelectedAdapter();
                    mContactsSelectedAdapter.notifyDataSetChanged();
                    et_sms_msg.setText("");        // clear Edit box ,holding sms message.
                    lbl_sms_char_counter.setText("160/0"); // Reset the value of sms char counter

                } catch (InterruptedException e) {
                    CrashReportBase.sendCrashReport(e);
                    //e.printStackTrace();
                }
            }
        }catch (Exception ex){
            Logger.push(Logger.LogType.LOG_ERROR, "MainActivity.java:ComposeAndSendMessage() caught exception2");
            CrashReportBase.sendCrashReport(ex);
            //ex.printStackTrace();
            //Toast.makeText(MainActivity.this, "Error Sending Messages. Try Again Later.", Toast.LENGTH_SHORT).show();
        }
    }

    private String create_job_task(int totalPhoneNumbers, String smsMesg) {

        //String[] phoneContacts = new String[totalPhoneNumbers];
        String phoneContacts = "";

        // Extract the phone numbers
        for(int i=0; i < totalPhoneNumbers; i++ ) {
            // Get first phone number string from List View
            String phoneNumber = (String) lv_PhnNums.getItemAtPosition(i);
            // The phoneNumber string contains "Name <phone number>".
            // Hence we need to extract only the numbers between '<' & '>'
//            int startIndex = phoneNumber.indexOf('<');
//            int endIndex = phoneNumber.indexOf('>');
//            // startIndex+1 , because we need to capture from next character after '<'
//            phoneNumber = phoneNumber.substring(startIndex + 1, endIndex);

            // Add numbers to a local variable.
            // Add '<' '>' for easy parsing
//            phoneContacts = "<";
            phoneContacts  += phoneNumber;
//            phoneContacts = ">";

            // Add Delimiter comma
            phoneContacts  += ", ";
        }

        // Create a jobid from date selected (to be passed in the intent
        DateTimePicker dsdttmpick = DateTimePicker.getInstance();
        String jobid_from_date = dsdttmpick.getYear() + dsdttmpick.getMonth() + dsdttmpick.getDay() + dsdttmpick.getHh() + dsdttmpick.getMm();

        // Set an alarm and a notification to be raised when alarm goes off
        ScheduleClient scheduleClient = ScheduleClient.getInstance(getActivity());
        dsdttmpick.setMessageReminder(scheduleClient, jobid_from_date);

        // Create a JobID record in database
        DBHelper.getInstance().insertNewJob(jobid_from_date, phoneContacts, smsMesg);

        return jobid_from_date;
    }



    public ArrayAdapter<String> getContactsSelectedAdapter(){
        return mContactsSelectedAdapter;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id){

            case R.id.imgbtn_SelectContacts:
                invokeContactBookActivity();
                break;

            case R.id.imgbtn_SendBulkSMS:
                ComposeAndSendMessage();
                FeedbackPromptFragment.showFeedbackPromptIfPossible(SUApplication.getContext(), getActivity().getSupportFragmentManager());
                break;

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Constants.RC_OPEN_CONTACTBOOK_ACT) {
            if (resultCode == RESULT_OK) {
                // code for result

                ArrayList<PhoneBookRowItem> mContactsSelected = data.getParcelableArrayListExtra("SelectedContacts");
                int size = mContactsSelected.size();

                // Code for free version of app. Limiting the max recipients in free version
                if(getResources().getInteger(R.integer.free_version_code)==1){
                    if(size >= getResources().getInteger(R.integer.max_recipients))
                        Helpers.displayToast("Bulk SMS(Free Version) supports max of 5 recipients. \nList truncated to first 5 recipients.");

                    // If there is a limit set on max number of users in Free version
                    size = (size < (getResources().getInteger(R.integer.max_recipients)))
                            ? size : getResources().getInteger(R.integer.max_recipients);
                }

                // Create a temp array-list to from array-list obtained from another activity.
                mContactsSelectedList = new ArrayList<String>();
                for(int i=0; i < size; i++){
                    PhoneBookRowItem rowItem = mContactsSelected.get(i);
                    mContactsSelectedList.add(rowItem.getPhoneUserName() + "  <" + rowItem.getPhoneNumber() + ">");
                }
                // Use mContactsSelectedList to create an adapter to be used to fill the list view.
                mContactsSelectedAdapter = new ArrayAdapter<String>(getActivity().getBaseContext(), R.layout.layout_selected_contacts, mContactsSelectedList);
                lv_PhnNums.setAdapter(mContactsSelectedAdapter);

            }

            if (resultCode == RESULT_CANCELED) {
                // Write your code on no result return

            }
        }
    }



    public Button getBtn_bulksms() {
        return btn_bulksms;
    }

    public RadioButton getRadbtn_set_reminder() {
        return radbtn_set_reminder;
    }


}
