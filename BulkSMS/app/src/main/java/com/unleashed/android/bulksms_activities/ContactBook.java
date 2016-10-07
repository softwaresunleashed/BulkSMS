package com.unleashed.android.bulksms_activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.unleashed.android.bulksms1.R;
import com.unleashed.android.customadapter.CustomListViewAdapter;
import com.unleashed.android.customadapter.PhoneBookRowItem;
import com.unleashed.android.helpers.activities.BaseActivity;
import com.unleashed.android.helpers.crashreporting.CrashReportBase;
import com.unleashed.android.helpers.logger.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.Semaphore;

public class ContactBook extends BaseActivity implements  View.OnClickListener, AdapterView.OnItemClickListener, SearchView.OnQueryTextListener{

    private ImageButton imgbtn_done;
    private ImageButton imgbtn_reset;
    private ImageButton imgbtn_select_all;
    private ImageButton imgbtn_select_none;
    private ProgressDialog progress;


    private int mTotalRecords;
    private int mCurrentRecord;

    private ListView lv_phonerecords;



    public CustomListViewAdapter customListViewObj;
    public CustomListViewAdapter customListViewObjBackUpCopy;

    // Create an Array of Items and a ArrayAdapter around it.
    public static ArrayList<PhoneBookRowItem> phoneBookRowItems = null;

    // Create an Array of Items and a ArrayAdapter around it.
    public ArrayList<PhoneBookRowItem> selectedContacts = null;
    public int sizeOfSelectedContacts;

    private Thread thrPopulatePhoneContacts;
    public static final int MAX_AVAILABLE = 1;
    public final Semaphore available = new Semaphore(MAX_AVAILABLE, true);

    private SearchView mSearchView;     // Search View Edit Box for filtering out phone contacts.

    public static ArrayList<PhoneBookRowItem> getPhoneBookRowItemsHandle(){
        return phoneBookRowItems;
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    private void displayInterstitialAds(){
        // Display Interstitial ad when Opening contact book
        if(getResources().getInteger(R.integer.host_ads)==1) {
            Thread thrAdThread = new Thread() {
                @Override
                public void run() {
                    super.run();

                    try {
                        sleep(1 * 60 * 1000);  // sleep 1 mins
                        // Invoke AdView
                        AdView mAdView = (AdView) findViewById(R.id.adView_contact_book);
                        AdRequest adRequest = new AdRequest.Builder().build();
                        mAdView.loadAd(adRequest);

                    } catch (Exception ex) {
                        Logger.push(Logger.LogType.LOG_ERROR, "ContactBook.java:onCreate()");
                        //ex.printStackTrace();
                        CrashReportBase.sendCrashReport(ex);
                    }
                }
            };
            thrAdThread.start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_book);

        // Display Interstitial ad when Opening contact book
        displayInterstitialAds();

        imgbtn_done = (ImageButton)findViewById(R.id.imgbtn_selectiondone);
        imgbtn_done.setOnClickListener(this);

        imgbtn_reset = (ImageButton)findViewById(R.id.imgbtn_resetselection);
        imgbtn_reset.setOnClickListener(this);


        imgbtn_select_all = (ImageButton)findViewById(R.id.imageButton_selectall);
        imgbtn_select_all.setOnClickListener(this);

        imgbtn_select_none = (ImageButton)findViewById(R.id.imageButton_selectnone);
        imgbtn_select_none.setOnClickListener(this);

        // Start a thread to populate at the beginning of the activity
        thrPopulatePhoneContacts = new Thread(){
            @Override
            public void run() {
                // release the semaphore
                try {
                    available.acquire();
                } catch (InterruptedException e) {
                    CrashReportBase.sendCrashReport(e);
                    // e.printStackTrace();
                }

                if(phoneBookRowItems == null){
                    phoneBookRowItems = new ArrayList<PhoneBookRowItem>();
                    populateAdapter(phoneBookRowItems);

                }

                // release the semaphore
                available.release();
            }
        };
        thrPopulatePhoneContacts.start();

        // Acquire semaphore and wait till phonebook contacts are populated in background
        try {
            available.acquire();
        } catch (InterruptedException e) {
            CrashReportBase.sendCrashReport(e);
            //e.printStackTrace();
        }

        // This code is moved to thread implementation.
        // Populate Adapter with data provided (Just to be on the safer side)
        if(phoneBookRowItems == null){
            phoneBookRowItems = new ArrayList<PhoneBookRowItem>();
            populateAdapter(phoneBookRowItems);
        }


        // Initialize the Search View for phone contacts.
        mSearchView = (SearchView) findViewById(R.id.searchView);
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(false);
        mSearchView.setQueryHint(getString(R.string.contactbook_search_view_hint));

//        mSearchView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//
//
//            }
//        });
//        mSearchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//            }
//        });


        // Initialize the PhoneBook contacts Adapter
        customListViewObj = new CustomListViewAdapter(this, R.layout.custom_list_view_phone_records, phoneBookRowItems);

        // Set the Array Adapter containting items, to ListView
        lv_phonerecords = (ListView)findViewById(R.id.listView_custom_phn_records);
        lv_phonerecords.setAdapter(customListViewObj);

        // Implement the listener
        lv_phonerecords.setOnItemClickListener(this);
        lv_phonerecords.setItemsCanFocus(false);
        lv_phonerecords.setTextFilterEnabled(true);     // needed for search view for phone contacts.

        // Add OnScrollListener() : This was needed to address the strange problem :
        // that List View was not clickable once I scrolled it up / down.
        lv_phonerecords.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    lv_phonerecords.invalidateViews();
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        //Method for Setting the Height of the ListView dynamically.
        // caused phonebook activity to freeze (reason : unknown)
        //setListViewHeightBasedOnChildren(lv_phonerecords);

        // This code allows ListView to scroll even when ListView is inside a ScrollView
        lv_phonerecords.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String strPhoneContactToSearch) {
        if (TextUtils.isEmpty(strPhoneContactToSearch)) {
            lv_phonerecords.clearTextFilter();
        } else {
            lv_phonerecords.setFilterText(strPhoneContactToSearch.toString());
        }
        return true;
    }

    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/
//    private void setListViewHeightBasedOnChildren(ListView listView) {
//        ListAdapter listAdapter = listView.getAdapter();
//        if (listAdapter == null)
//            return;
//
//        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
//        int totalHeight = 0;
//        View view = null;
//        for (int i = 0; i < listAdapter.getCount(); i++) {
//            view = listAdapter.getView(i, view, listView);
//            if (i == 0)
//                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
//
//            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
//            totalHeight += view.getMeasuredHeight();
//        }
//        ViewGroup.LayoutParams params = listView.getLayoutParams();
//        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
//        listView.setLayoutParams(params);
//        //listView.requestLayout();
//    }

    public void populateAdapter(ArrayList<PhoneBookRowItem> RowItems) {
        String name;
        String phoneNumber;


        try{
            // Prepare to get the records in Phonebook.
            ContentResolver cr = getContentResolver();
            Cursor cur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

            mTotalRecords = cur.getCount();     // Get the total records
            if ( mTotalRecords > 0) {
                while (cur.moveToNext()) {

                    // Increment the current count by one.
                    mCurrentRecord++;


                    name = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    phoneNumber = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    // Ignore is Phone Number and Name are both null. (*Rare case*)
                    if(name==null && phoneNumber==null)
                        continue;

                    // Ignore is number is a Supplementry string.
                    // SS numbers start with a "*"
                    // We dont wish to send SMS to supplementry strings....Do we??
                    if(phoneNumber.startsWith("*"))
                        continue;

                    // Ignore the numbers that are less than 10 digits.
                    // A Mobile number is basically atleast minimum of 10 digits
                    if(phoneNumber.length() < 10)
                        continue;


                    // Add PhoneBook items to Adapter
                    PhoneBookRowItem item = new PhoneBookRowItem();// = new PhoneBookRowItem(name, phoneNumber);
                    item.setPhoneName(name);
                    item.setPhoneNumber(phoneNumber);
                    item.setState(false);
                  //  item.swtch = new CustomSwitch(getBaseContext());
                    RowItems.add(item);

                    // Reset all the variables to NULL before next iteration
                    name = null;
                    phoneNumber = null;
                }


                cur.close();

                /* Ref : http://beginnersbook.com/2013/12/java-arraylist-of-object-sort-example-comparable-and-comparator/ */
                /* Sort ascending order - all phone book contacts */
                Collections.sort(RowItems, NameComparator);

            }
        }catch (Exception ex){
            Logger.push(Logger.LogType.LOG_ERROR, "ContactBook.java:populateAdapter()");
            CrashReportBase.sendCrashReport(ex);
			//ex.printStackTrace();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contact_book, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        int viewid = view.getId();

        switch(viewid){
            case R.id.imgbtn_selectiondone:
                processSelectedPhoneList();
                Intent returnIntent = new Intent();
                // Sending back the selected contacts in a return-intent.
                returnIntent.putParcelableArrayListExtra("SelectedContacts", (ArrayList<PhoneBookRowItem>) selectedContacts);
                setResult(RESULT_OK, returnIntent);
                finish();

                break;

            case R.id.imgbtn_resetselection:
                setResult(RESULT_CANCELED, null);
                finish();
                break;

            case R.id.imageButton_selectall:
                selectAllContactBookEntries();
                break;

            case R.id.imageButton_selectnone:
                selectNoneContactBookEntries();
                break;

        }

    }

    private void selectAllContactBookEntries() {

       try{

           // Iterate thru all the selected phone contacts and store in another array list
           int TotalSizePhoneContacts = phoneBookRowItems.size();

           if(selectedContacts == null){
               selectedContacts = new ArrayList<PhoneBookRowItem>();
           }

           // Add PhoneBook items to Selected Contacts Adapter
           PhoneBookRowItem item;
           CheckBox chkbox_phnEntrySelected;
           for(int i=0; i < TotalSizePhoneContacts; i++){
               item = phoneBookRowItems.get(i);
               item.setState(true);        //ie. item is selected.

               if(lv_phonerecords.getChildAt(i) != null){
                   lv_phonerecords.getChildAt(i).setBackgroundColor(Color.LTGRAY);

                   chkbox_phnEntrySelected = (CheckBox)lv_phonerecords.getChildAt(i).findViewById(R.id.checkBox_phonebookcontact);
                   chkbox_phnEntrySelected.setChecked(true);

               }
               selectedContacts.add(item);
           }
           // Set the size of Contact List
           sizeOfSelectedContacts = selectedContacts.size();
       }catch (Exception ex){
           Logger.push(Logger.LogType.LOG_ERROR, "ContactBook.java:selectAllContactBookEntries()");
		   //ex.printStackTrace();
           CrashReportBase.sendCrashReport(ex);
       }

    }

    private void selectNoneContactBookEntries() {
        // Iterate thru all the selected phone contacts and store in another array list
        int TotalSizePhoneContacts = phoneBookRowItems.size();

        if(selectedContacts == null){
            selectedContacts = new ArrayList<PhoneBookRowItem>();
        }

        // Add PhoneBook items to Selected Contacts Adapter
        PhoneBookRowItem item;
        CheckBox chkbox_phnEntrySelected;

        for(int i=0; i < TotalSizePhoneContacts; i++){
            item = phoneBookRowItems.get(i);
            item.setState(false);        //ie. item is NOT selected.
            // Only update the entries which are visible
            if(lv_phonerecords.getChildAt(i) != null){
                lv_phonerecords.getChildAt(i).setBackgroundColor(Color.WHITE);

                chkbox_phnEntrySelected = (CheckBox)lv_phonerecords.getChildAt(i).findViewById(R.id.checkBox_phonebookcontact);
                chkbox_phnEntrySelected.setChecked(false);
            }
            selectedContacts.remove(item);
        }
        // Set the size of
        sizeOfSelectedContacts = selectedContacts.size();
    }

    private void processSelectedPhoneList() {

        // Iterate thru all the selected phone contacts and store in another array list
        int TotalSizePhoneContacts = phoneBookRowItems.size();

        if(selectedContacts == null) {
            selectedContacts = new ArrayList<PhoneBookRowItem>();
        }

        // Add PhoneBook items to Selected Contacts Adapter
        PhoneBookRowItem item;// = new PhoneBookRowItem();
        for(int i=0; i < TotalSizePhoneContacts; i++){
            item = phoneBookRowItems.get(i);
            if(item.getState()){
                // ie. the entry is selected.
                // add to selectedContacts adapter
                selectedContacts.add(item);
            }
        }
        // Set the size of
        sizeOfSelectedContacts = selectedContacts.size();
    }



    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        /*
            If any row item of list contains focusable or clickable view then OnItemClickListener won't work.
            The row item must have a param like android:descendantFocusability="blocksDescendants".
        */

        CheckBox chkbox_phnEntrySelected = (CheckBox)view.findViewById(R.id.checkBox_phonebookcontact);

        PhoneBookRowItem pbRowItem = (PhoneBookRowItem)adapterView.getItemAtPosition(position);
        boolean current_state = pbRowItem.toggleState();

        try{
            if(current_state){
                // The item has been selected
                //view.setBackgroundColor(Color.LTGRAY);
                chkbox_phnEntrySelected.setChecked(true);

            }else{

                //view.setBackgroundColor(Color.WHITE);       //Color.TRANSPARENT
                chkbox_phnEntrySelected.setChecked(false);
            }
        }catch (Exception ex){
            Logger.push(Logger.LogType.LOG_ERROR, ex.getStackTrace().toString());
            CrashReportBase.sendCrashReport(ex);
        }

    }


    public static Comparator<PhoneBookRowItem> NameComparator = new Comparator<PhoneBookRowItem>() {
        @Override
        public int compare(PhoneBookRowItem pbRI1, PhoneBookRowItem pbRI2) {
            String rowItem_Name1 = pbRI1.getPhoneUserName().toUpperCase();
            String rowItem_Name2 = pbRI2.getPhoneUserName().toUpperCase();

            // Ascending order
            return rowItem_Name1.compareTo(rowItem_Name2);
        }
    };


}
