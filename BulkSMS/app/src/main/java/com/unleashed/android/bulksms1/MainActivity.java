package com.unleashed.android.bulksms1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.appszoom.appszoomsdk.AppsZoom;
import com.purplebrain.adbuddiz.sdk.AdBuddiz;
import com.unleashed.android.application.SUApplication;
import com.unleashed.android.bulksms_fragments.PlaceholderFragment;
import com.unleashed.android.bulksms_interfaces.IFragToFragDataPass;
import com.unleashed.android.bulksms_interfaces.ITabLayoutCallbacks;
import com.unleashed.android.customadapter.SectionsPagerAdapter;
import com.unleashed.android.datetimepicker.ScheduleClient;
import com.unleashed.android.helpers.Helpers;
import com.unleashed.android.helpers.PromotionalHelpers;
import com.unleashed.android.helpers.SplashScreen.SplashScreen;
import com.unleashed.android.helpers.activities.BaseActivity;
import com.unleashed.android.helpers.apprating.FeedbackPromptFragment;
import com.unleashed.android.helpers.crashreporting.CrashReportBase;
import com.unleashed.android.helpers.navigationdrawer.NavDrawer;

import static com.unleashed.android.bulksms_fragments.PlaceholderFragment.FRAGMENT_TAG_;



public class MainActivity extends BaseActivity
        implements ITabLayoutCallbacks, IFragToFragDataPass {

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


    // This is a handle so that we can call methods on our service
    private ScheduleClient scheduleClient;


    @Override
    protected void onDestroy() {
        super.onDestroy();

//        //To show an Ad, call the showAd() method. You can select the most suitable moment to display interstitials.
//        //AppsZoom.showAd(this);
//        AppsZoom.fetchAd(null, new AppsZoom.OnAdFetchedListener() {
//            @Override
//            public void onAdFetched() {
//                AppsZoom.showAd(MainActivity.this);
//            }
//        });
    }

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

    @Override
    protected void onPostResume() {
        super.onPostResume();

//        //To show an Ad, call the showAd() method. You can select the most suitable moment to display interstitials.
//        //AppsZoom.showAd(this);
//        AppsZoom.fetchAd(null, new AppsZoom.OnAdFetchedListener() {
//            @Override
//            public void onAdFetched() {
//                AppsZoom.showAd(MainActivity.this);
//            }
//        });
    }


    // Nav Drawer UI Components
    private NavDrawer navDrawer;
    private TabLayout tabLayout;
    private View rootView;

    private NavigationView.OnNavigationItemSelectedListener navDrawerItemSelectLiActionstener = new NavigationView.OnNavigationItemSelectedListener(){
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            int itemId = item.getItemId();

            //Check to see which item was being clicked and perform appropriate action
            switch (itemId) {
                //Replacing the main content with ContentFragment Which is our Inbox View;
                case R.id.nav_home:
                    tabSelected(PlaceholderFragment.TABS.TAB_SEND_BULK_SMS.getValue());         // Switch to first tab
                    break;

                case R.id.nav_tell_a_friend:
                    PromotionalHelpers.tell_a_friend_via_personal_email(MainActivity.this);
                    break;

                case R.id.nav_promote_app:
                    PromotionalHelpers.show_dialog_box_to_request_promotional_email(MainActivity.this);
                    break;

                case R.id.nav_rate_us:
                    rate_app_on_google_play_store();
                    break;

                case R.id.nav_settings:
                    break;

                case R.id.nav_exit:
                    finish();
                    break;

                case R.id.nav_about_us:
                    tabSelected(PlaceholderFragment.TABS.TAB_ABOUT_APP.getValue());
                    // launch new intent instead of loading fragment
                    //startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                    break;

                default:

            }
            navDrawer.closeDrawers();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Print App Hash Code (Signatures) in ADB logs. Used for App Hash on FB.
        Helpers.printAppSignatures(SUApplication.getContext());

        // Display Splash Screen on Application Load
        SplashScreen.display_splash_screen(MainActivity.this);

        // Mandate User Login
        //LoginScreen.showLoginActivity(MainActivity.this);

        setContentView(R.layout.activity_main);

        // Navigation Drawer Initialization
        initNavDrawerAndToolbar();

        // Create a new service client and bind our activity to this service
        scheduleClient = ScheduleClient.getInstance(this);
        scheduleClient.doBindService();

        // Display Ads
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

//        initSwipeMenu();

    }

//    private void initSwipeMenu() {
////        https://github.com/SpecialCyCi/AndroidResideMenu
//        // attach to current activity;
//        ResideMenu resideMenu = new ResideMenu(this);
//        resideMenu.setBackground(R.drawable.nav_header_bg);
//        resideMenu.attachToActivity(this);
//
//        // create menu items;
//        String titles[] = { "Home", "Profile", "Calendar", "Settings" };
//        int icon[] = { R.drawable.ic_action_person, R.drawable.ic_action_person, R.drawable.ic_action_person, R.drawable.ic_action_person };
//
//        for (int i = 0; i < titles.length; i++){
//            ResideMenuItem item = new ResideMenuItem(this, icon[i], titles[i]);
//            item.setOnClickListener(this);
//            resideMenu.addMenuItem(item,  ResideMenu.DIRECTION_LEFT); // or  ResideMenu.DIRECTION_RIGHT
//        }
//    }

    private void initNavDrawerAndToolbar(){
        // Invoke Navigation Drawer
        rootView = findViewById(R.id.drawer_layout);
        navDrawer = NavDrawer.getInstance();
        navDrawer.initNavigationDrawer(SUApplication.getContext(), this, rootView);
        navDrawer.setUpNavigationView(navDrawerItemSelectLiActionstener);


        // Set up the ActionBar(Old Implementation) / ToolBar(New Implementation)
        navDrawer.setupToolbar(this, rootView);
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
            //int position = tab.getPosition();

            hideVirtualKeyboard();

            // Tab - Jobs Reminder
//            if(tab.getPosition() == 2){
//                if(getResources().getInteger(R.integer.free_version_code) == 1){
//                    alert_dialog_buy_bulk_sms();
//                }
//            }

        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            hideVirtualKeyboard();
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
            hideVirtualKeyboard();
        }
    };


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//
//        switch (id){
//            case R.id.action_tell_a_friend_via_email:
//                PromotionalHelpers.tell_a_friend_via_personal_email(this);
//                break;
//
//            case R.id.action_send_promotional_email:
//                PromotionalHelpers.show_dialog_box_to_request_promotional_email();
//                break;
//
//            case R.id.action_rate_app_on_google_play_store:
//                rate_app_on_google_play_store();
//                break;
//
//            case R.id.action_exit:
//                finish();
//                break;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


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


    private void hideVirtualKeyboard(){
        // Code to hide virtual keyboard
        View focus = getCurrentFocus();
        if(focus != null){
            InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.hideSoftInputFromWindow(focus.getWindowToken(), 0);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Call super.onActivityResult() to invoke onActivityResult of respective Fragments.
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Show Rate App Dialog
        FeedbackPromptFragment.showFirstTimeFeedbackPromptIfPossible(this, getSupportFragmentManager());
    }

    @Override
    public void onBackPressed() {

        // Call Nav drawer onBackPressed to handle Nav Drawer related stuff
        navDrawer.onBackPressed();

        // Call BaseActivity's onBackPressed() to handle double backkey press to exit app
        super.onBackPressed();
    }

    @Override
    public void tabSelected(int tabNumb) {
        tabLayout.getTabAt(tabNumb).select();
    }


    @Override
    public void setStringSendBulkSMS(String strBtnText) {
        // Set the text of button in FragmentSendBulkSMS
        // Find Fragments via Tag and then getView() and setText() on it.
        Fragment frag = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_ + PlaceholderFragment.TABS.TAB_SEND_BULK_SMS.getValue());
        //((Button) frag.getView().findViewById(R.id.imgbtn_SendBulkSMS)).setText(strBtnText);
    }

    @Override
    public void setRadioButtonState(boolean rdbtnEnabled) {
        // Set the text of button in FragmentSendBulkSMS
        // Find Fragments via Tag and then getView() and setChecked() on it.
        Fragment frag = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_ + PlaceholderFragment.TABS.TAB_SEND_BULK_SMS.getValue());
        //((RadioButton) frag.getView().findViewById(R.id.radbtn_setreminder)).setChecked(true);
    }

//    @Override
//    public void onClick(View v) {
//        int id = v.getId();
//
//
//        switch (id){
//            case R.id.action_tell_a_friend_via_email:
//                Intent intent = new Intent(Intent.ACTION_SEND);
//                intent.setType("text/html");
//                intent.putExtra(Intent.EXTRA_EMAIL, "softwares.unleashed@gmail.com");
//                intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.email_subject));
//                intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.email_body));
//                startActivity(Intent.createChooser(intent, "Send Email"));
//                break;
//
//            case R.id.action_send_promotional_email:
//                PromotionalHelpers.show_dialog_box_to_request_promotional_email(this);
//                break;
//
//            case R.id.action_rate_app_on_google_play_store:
//                rate_app_on_google_play_store();
//                break;
//
//            case R.id.action_exit:
//                finish();
//                break;
//        }
//    }
}

