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
    public static final int TAB_SEND_BULK_SMS = 1;
    public static final int TAB_REMINDER_SMS = 2;
    public static final int TAB_JOBS_SMS = 3;
    public static final int TAB_ABOUT_APP = 4;

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

    public static void registerFragmentCallbacks(IInitCallbacks cbListener){
        if(cbListener != null){
            callbackListener = cbListener;
        }
    }

    static IInitCallbacks callbackListener;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int TabNumber;
        Bundle args = getArguments();
        TabNumber = args.getInt(ARG_SECTION_NUMBER);

        View rootView = inflater.inflate(R.layout.fragment_send_bulk_sms, container, false);

        switch(TabNumber){
            case TAB_SEND_BULK_SMS:
                rootView = inflater.inflate(R.layout.fragment_send_bulk_sms, container, false);
                if(callbackListener != null)
                    callbackListener.initCallbacks(rootView, TAB_SEND_BULK_SMS);
                break;

            case TAB_REMINDER_SMS:
                rootView = inflater.inflate(R.layout.fragment_reminder_bulk_sms, container, false);
                if(callbackListener != null)
                    callbackListener.initCallbacks(rootView, TAB_REMINDER_SMS);
                break;

            case TAB_JOBS_SMS:
                rootView = inflater.inflate(R.layout.fragment_jobs_sms, container, false);
                if(callbackListener != null)
                    callbackListener.initCallbacks(rootView, TAB_JOBS_SMS);

                break;

            case TAB_ABOUT_APP:
                rootView = inflater.inflate(R.layout.fragment_about_app, container, false);
                if(callbackListener != null)
                    callbackListener.initCallbacks(rootView, TAB_ABOUT_APP);
                break;
        };

        return rootView;
    }

    public interface IInitCallbacks{
        public void initCallbacks(View view, int fragment_number);
    }


}