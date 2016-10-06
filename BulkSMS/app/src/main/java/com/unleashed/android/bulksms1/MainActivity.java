package com.unleashed.android.bulksms1;

import android.app.Activity;
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
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
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

import com.appszoom.appszoomsdk.AppsZoom;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.purplebrain.adbuddiz.sdk.AdBuddiz;
import com.unleashed.android.application.SUApplication;
import com.unleashed.android.bulksms_activities.ContactBook;
import com.unleashed.android.bulksms_fragments.PlaceholderFragment;
import com.unleashed.android.customadapter.PhoneBookRowItem;
import com.unleashed.android.customadapter.SectionsPagerAdapter;
import com.unleashed.android.datetimepicker.DateTimePicker;
import com.unleashed.android.datetimepicker.ScheduleClient;
import com.unleashed.android.expandablelistview.ExpandableListAdapter;
import com.unleashed.android.helpers.Helpers;
import com.unleashed.android.helpers.SplashScreen.SplashScreen;
import com.unleashed.android.helpers.activities.BaseActivity;
import com.unleashed.android.helpers.apprating.FeedbackPromptFragment;
import com.unleashed.android.helpers.crashreporting.CrashReportBase;
import com.unleashed.android.helpers.dbhelper.DBHelper;
import com.unleashed.android.helpers.logger.Logger;
import com.unleashed.android.helpers.navigationdrawer.NavDrawer;
import com.unleashed.android.sendemail.Mail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.unleashed.android.bulksms_fragments.PlaceholderFragment.TAB_ABOUT_APP;
import static com.unleashed.android.bulksms_fragments.PlaceholderFragment.TAB_JOBS_SMS;
import static com.unleashed.android.bulksms_fragments.PlaceholderFragment.TAB_REMINDER_SMS;
import static com.unleashed.android.bulksms_fragments.PlaceholderFragment.TAB_SEND_BULK_SMS;


public class MainActivity extends BaseActivity implements ActionBar.TabListener, View.OnClickListener, PlaceholderFragment.IInitCallbacks {

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

    // Handles to UI Controls on "About App -  Bulk SMS Tab"
    private TextView lbl_version_number;
    private TextView lbl_version_history;
    private TextView lbl_about_app;

    // Handles to UI Controls on "Send Bulk SMS Tab"
    private Button btn_bulksms;
    private TextView lbl_sms_char_counter;
    private ImageButton imgbtn_selectcontacts;

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
                        //e.printStackTrace();
                        Toast.makeText(getBaseContext(), "Error:" + e.toString(), Toast.LENGTH_LONG).show();
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
        if(getResources().getInteger(R.integer.free_version_code) == 1){
            // This is a free software, prompt user to buy paid version.

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
        }

    }

    public void display_toast(String Msg){
        Toast.makeText(MainActivity.this, Msg, Toast.LENGTH_SHORT).show();
    }


    private int navItemIndex = 0;

    private NavDrawer navDrawer;
    private TabLayout tabLayout;
    private View rootView;
    private NavigationView.OnNavigationItemSelectedListener navDrawerItemSelectListener = new NavigationView.OnNavigationItemSelectedListener(){
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            //Check to see which item was being clicked and perform appropriate action
            switch (item.getItemId()) {
                //Replacing the main content with ContentFragment Which is our Inbox View;
                case R.id.nav_home:
                    navItemIndex = 0;
                    break;

                case R.id.nav_tell_a_friend:
                    navItemIndex = 1;
                    break;

                case R.id.nav_promote_app:
                    navItemIndex = 2;
                    break;

                case R.id.nav_rate_us:
                    navItemIndex = 3;
                    break;

                case R.id.nav_settings:
                    navItemIndex = 4;
                    break;

                case R.id.nav_exit:
                    navItemIndex = 4;
                    break;

                case R.id.nav_about_us:
                    // launch new intent instead of loading fragment
                    //startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                    navDrawer.closeDrawers();
                    return true;

                case R.id.nav_privacy_policy:
                    // launch new intent instead of loading fragment
                    //startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
                    navDrawer.closeDrawers();
                    return true;

                default:
                    navItemIndex = 0;
            }
            return true;
        }
    };

    private NavigationView.OnNavigationItemSelectedListener navDrawerItemSelectLiActionstener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Helpers.printAppSignatures(SUApplication.getContext());

        // Display Splash Screen on Application Load
        SplashScreen.display_splash_screen(MainActivity.this);

        // Mandate User Login
        //LoginScreen.showLoginActivity(MainActivity.this);

        setContentView(R.layout.activity_main);

        //////////////////////////// Navigation Drawer Code ////////////////////////////////////////



        // Invoke Navigation Drawer
        rootView = findViewById(R.id.drawer_layout);
        navDrawer = NavDrawer.getInstance();
        navDrawer.initNavigationDrawer(SUApplication.getContext(), this, rootView);
        navDrawer.setUpNavigationView(navDrawerItemSelectLiActionstener);

        // Set up the ActionBar(Old Implementation) / ToolBar(New Implementation)
        navDrawer.setupToolbar(this, rootView);

        ///////////////////////////////////////////////////////////////////////////////////////////


        // Setup Database
        bulksmsdb = new DBHelper(SUApplication.getContext());

        // Create a new service client and bind our activity to this service
        scheduleClient = new ScheduleClient(this);
        scheduleClient.doBindService();

        dsdttmpick = new DateTimePicker(SUApplication.getContext());

        // Register for callbacks from the fragments
        PlaceholderFragment.registerFragmentCallbacks(this);

        displayAds();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Create the TabLayout, And Fill the Tabs as per ViewPager
        tabLayout = (TabLayout) findViewById(R.id.tabs_bulksms);
        tabLayout.addOnTabSelectedListener(tabSelectedListener);
        tabLayout.setupWithViewPager(mViewPager);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.getTabAt(position).select();

            }
        });

//        // For each of the sections in the app, add a tab to the action bar.
//        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
//            // Create a tab with text corresponding to the page title defined by
//            // the adapter. Also specify this Activity object, which implements
//            // the TabListener interface, as the callback (listener) for when
//            // this tab is selected.
//
////            actionBar.addTab(
////                    actionBar.newTab()
////                            .setText(mSectionsPagerAdapter.getPageTitle(i))
////                            .setTabListener(this));
//        }
    }

    private void displayAds() {
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
                            CrashReportBase.sendCrashReport(e);
                            //e.printStackTrace();
                        }
                    }
                }
            };
            thrAdThread.start();

        }
    }

    TabLayout.OnTabSelectedListener tabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            int position = tab.getPosition();
            // Switch to view for this tab
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };


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

        // Show Rate App Dialog
        FeedbackPromptFragment.showFeedbackPromptIfPossible(this, getSupportFragmentManager());

        // Old School Code
//        final String appPackageName = getPackageName();
//        final Intent openPlayStore = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
//        if (hasHandlerForIntent(openPlayStore))
//            startActivity(openPlayStore);
//        else
//            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));

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
//        if(tab.getPosition() == 2){
//
//            if(getResources().getInteger(R.integer.free_version_code) == 1){
//                alert_dialog_buy_bulk_sms();
//            }
//
//        }

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

    @Override
    public void initCallbacks(View view, int fragment_number) {
        // Call corresponding initialization routines, as per the tab selected.
        switch(fragment_number){
            case TAB_SEND_BULK_SMS:
                initSendBulkSMSTab(view);
                break;

            case TAB_REMINDER_SMS:
                initSMSReminderTab(view);
                break;

            case TAB_JOBS_SMS:
                initJobsSMSTab(view);
                break;

            case TAB_ABOUT_APP:
                initAboutAppTab(view);
                break;
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
                    if(size >= getResources().getInteger(R.integer.max_recipients))
                        display_toast("Bulk SMS(Free Version) supports max of 5 recipients. \nList truncated to first 5 recipients.");

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
                mContactsSelectedAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, mContactsSelectedList);
                lv_PhnNums.setAdapter(mContactsSelectedAdapter);

            }

            if (resultCode == RESULT_CANCELED) {
                // Write your code on no result return

            }
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        // Show Rate App Dialog
        FeedbackPromptFragment.showFirstTimeFeedbackPromptIfPossible(this, getSupportFragmentManager());
    }

    public ArrayAdapter<String> getContactsSelectedAdapter(){
        return mContactsSelectedAdapter;
    }


    private int RC_OPEN_CONTACTBOOK_ACT = 10;

    private void invokeContactBookActivity() {
        Intent startContactBookAct = new Intent(this, ContactBook.class);
        startActivityForResult(startContactBookAct, RC_OPEN_CONTACTBOOK_ACT);
    }

    private void ComposeAndSendMessage(){
        Logger.push(Logger.LogType.LOG_INFO,  "MainActivity.java:ComposeAndSendMessage()");


        String phoneNumber = "";//et_RecieverPhoneNumber.getText().toString();
        String smsMesg = et_sms_msg.getText().toString();       // Pull in the SMS text
        final Context mContext = SUApplication.getContext();


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
                            Logger.push(Logger.LogType.LOG_ERROR, "MainActivity.java:ComposeAndSendMessage() caught exception1");
                            CrashReportBase.sendCrashReport(ex);
                            //ex.printStackTrace();
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
            Logger.push(Logger.LogType.LOG_ERROR, "MainActivity.java:refresh_job_list() caught exception");
            CrashReportBase.sendCrashReport(ex);
            //ex.printStackTrace();
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
                    Logger.push(Logger.LogType.LOG_ERROR, "MainActivity.java:initSMSReminderTab() caught exception");
                    CrashReportBase.sendCrashReport(ex);
                    //ex.printStackTrace();
                }

                radbtn_set_reminder.setChecked(true);   // set the radio button as selected.
                tabLayout.getTabAt(0).select();         // go to bulk sms tab

                //getSupportActionBar().setSelectedNavigationItem(0);
            }
        });


        Button btnSetScheduleCancel = (Button)localView.findViewById(R.id.btn_cancelReminder);
        btnSetScheduleCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Return to Bulk SMS Tab
                tabLayout.getTabAt(0).select();

                //getSupportActionBar().setSelectedNavigationItem(0);
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

                Context cntx = SUApplication.getContext();//getActivity(); //getBaseContext(); //getApplication();
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

                tabLayout.getTabAt(1).select();                     // go to set reminder tab
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

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id){

            case R.id.imgbtn_SelectContacts:
                invokeContactBookActivity();
                break;

            case R.id.imgbtn_SendBulkSMS:
                ComposeAndSendMessage();
                FeedbackPromptFragment.showFeedbackPromptIfPossible(this, getSupportFragmentManager());
                break;

        }
    }

    @Override
    public void onBackPressed() {

        // Call Nav drawer onBackPressed to handle Nav Drawer related stuff
        navDrawer.onBackPressed();

        //super.onBackPressed();
    }
}

