package com.unleashed.android.bulksms_fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageButton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.unleashed.android.bulksms1.MainActivity;
import com.unleashed.android.bulksms1.R;
import com.unleashed.android.expandablelistview.ExpandableListAdapter;
import com.unleashed.android.helpers.crashreporting.CrashReportBase;
import com.unleashed.android.helpers.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by OLX - Sudhanshu on 07-10-2016.
 */

public class FragmentJobsSMSTab extends PlaceholderFragment {

    private ExpandableListAdapter listAdapter;
    private ExpandableListView exLV;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private int refresh_list_flag = 0;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_jobs_sms, container, false);

        initJobsSMSTab(rootView);

        return rootView;

    }

    private void initJobsSMSTab(View localView) {

        //////////// Add to Fragment ///////////////
        FragmentManager manager = getActivity().getSupportFragmentManager();
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


}
