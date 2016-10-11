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
    public static final int TAB_SEND_BULK_SMS = 0;
    public static final int TAB_REMINDER_SMS = 1;
    public static final int TAB_JOBS_SMS = 2;
    public static final int TAB_ABOUT_APP = 3;

    // View obj variable to pass View instances
    //private View mView;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
//    public static PlaceholderFragment newInstance(int sectionNumber) {
//        PlaceholderFragment fragment = new PlaceholderFragment();
//        Bundle args = new Bundle();
//        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//        fragment.setArguments(args);
//
//        return fragment;
//    }

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


    public static PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = null;

        switch (sectionNumber){
            case TAB_SEND_BULK_SMS:
                fragment = new FragmentSendBulkSms();
                break;
            case TAB_REMINDER_SMS:
                fragment = new FragmentSMSReminderTab();
                break;
            case TAB_JOBS_SMS:
                fragment = new FragmentJobsSMSTab();
                break;
            case TAB_ABOUT_APP:
                fragment = new FragmentAboutAppTab();
                break;
        }

//        Bundle args = new Bundle();
//        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//        fragment.setArguments(args);

        return fragment;
    }


//    public static void registerFragmentCallbacks(IInitCallbacks cbListener){
//        if(cbListener != null){
//            callbackListener = cbListener;
//        }
//    }

//    static IInitCallbacks callbackListener;
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        int TabNumber;
//        Bundle args = getArguments();
//        TabNumber = args.getInt(ARG_SECTION_NUMBER);
//
//        View rootView = inflater.inflate(R.layout.fragment_send_bulk_sms, container, false);
//
//        switch(TabNumber){
//            case TAB_SEND_BULK_SMS:
//                rootView = inflater.inflate(R.layout.fragment_send_bulk_sms, container, false);
//                if(callbackListener != null)
//                    callbackListener.initCallbacks(rootView, TAB_SEND_BULK_SMS);
//                break;
//
//            case TAB_REMINDER_SMS:
//                rootView = inflater.inflate(R.layout.fragment_reminder_bulk_sms, container, false);
//                if(callbackListener != null)
//                    callbackListener.initCallbacks(rootView, TAB_REMINDER_SMS);
//                break;
//
//            case TAB_JOBS_SMS:
//                rootView = inflater.inflate(R.layout.fragment_jobs_sms, container, false);
//                if(callbackListener != null)
//                    callbackListener.initCallbacks(rootView, TAB_JOBS_SMS);
//
//                break;
//
//            case TAB_ABOUT_APP:
//                rootView = inflater.inflate(R.layout.fragment_about_app, container, false);
//                if(callbackListener != null)
//                    callbackListener.initCallbacks(rootView, TAB_ABOUT_APP);
//                break;
//        }
//
//        return rootView;
//    }



}