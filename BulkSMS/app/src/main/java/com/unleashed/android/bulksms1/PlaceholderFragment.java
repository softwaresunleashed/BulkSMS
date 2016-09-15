package com.unleashed.android.bulksms1;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by sudhanshu on 14/09/16.
 */

public class PlaceholderFragment extends Fragment{
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

    // View obj variable to pass View instances
    private View mView;




    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        return fragment;
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



}