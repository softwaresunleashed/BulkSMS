package com.unleashed.android.bulksms_fragments;


import android.content.Context;

import com.unleashed.android.bulksms_interfaces.ITabLayoutCallbacks;
import com.unleashed.android.helpers.crashreporting.CrashReportBase;
import com.unleashed.android.helpers.fragments.BaseFragment;
import com.unleashed.android.helpers.logger.Logger;


/**
 * Created by sudhanshu on 14/09/16.
 */

public class PlaceholderFragment extends BaseFragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String FRAGMENT_TAG_ = "fragment_tag_";
    protected ITabLayoutCallbacks mTabLayoutCallbacks;

    // Static Numbers assiged to Tab(s)
    public static enum TABS{
        TAB_SEND_BULK_SMS(0),
        TAB_REMINDER_SMS(1),
        TAB_JOBS_SMS(2),
        TAB_ABOUT_APP(3);

        private int value;
        TABS(final int value){
            this.value = value;
        }

        public int getValue(){
            return value;
        }

    }


//    public static final int TAB_SEND_BULK_SMS = 0;
//    public static final int TAB_REMINDER_SMS = 1;
//    public static final int TAB_JOBS_SMS = 2;
//    public static final int TAB_ABOUT_APP = 3;


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = null;

        if(TABS.TAB_SEND_BULK_SMS.getValue() == sectionNumber){
            fragment = new FragmentSendBulkSms();
        } else if(TABS.TAB_REMINDER_SMS.getValue() == sectionNumber){
            fragment = new FragmentSMSReminderTab();
        } else if(TABS.TAB_JOBS_SMS.getValue() == sectionNumber){
            fragment = new FragmentJobsSMSTab();
        } else if(TABS.TAB_ABOUT_APP.getValue() == sectionNumber){
            fragment = new FragmentAboutAppTab();
        }


        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Make sure that container activity implement the callback interface
        try {
            mTabLayoutCallbacks = (ITabLayoutCallbacks)context;
        } catch (ClassCastException ex) {
            Logger.push(Logger.LogType.LOG_ERROR, "PlaceholderFragment.java:onAttach() caught exception");
            CrashReportBase.sendCrashReport(ex);
            throw new ClassCastException(context.toString() + " must implement ITabLayoutCallbacks");
        }

    }

}