package com.unleashed.android.bulksms_fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.unleashed.android.bulksms1.AboutApp;
import com.unleashed.android.bulksms1.R;

/**
 * Created by OLX - Sudhanshu on 07-10-2016.
 */

public class FragmentAboutAppTab extends PlaceholderFragment{

    // Handles to UI Controls on "About App -  Bulk SMS Tab"
    private TextView lbl_version_number;
    private TextView lbl_version_history;
    private TextView lbl_about_app;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_about_app, container, false);

        initAboutAppTab(rootView);

        return rootView;

    }


    @Override
    public void onResume() {
        super.onResume();

        // Start Animations on opening About Tab
        YoYo.with(Techniques.SlideInUp).duration(2000).playOn(lbl_version_number);
        YoYo.with(Techniques.SlideInUp).duration(2000).playOn(lbl_version_history);
        YoYo.with(Techniques.SlideInUp).duration(2000).playOn(lbl_about_app);
    }

    private void initAboutAppTab(View localView) {

        //////////// Add to Fragment ///////////////
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment fragment = new Fragment();
        String fragTag = FRAGMENT_TAG_ + PlaceholderFragment.TABS.TAB_ABOUT_APP.getValue();
        transaction.add(R.id.container_about_app, fragment, fragTag);
        transaction.commit();
        ////////////////////////////////////////////

        if(getResources().getInteger(R.integer.host_ads)==1) {
            // Invoke AdView
            AdView mAdView = (AdView) localView.findViewById(R.id.adView_about_app_tab);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }

        AboutApp objAbtApp = new AboutApp(getActivity());

        lbl_version_number = (TextView)localView.findViewById(R.id.lbl_app_version_number);
        lbl_version_number.setText(objAbtApp.getVersionNumber());

        lbl_version_history = (TextView)localView.findViewById(R.id.lbl_app_version_history);
        lbl_version_history.setText(objAbtApp.getVersionHistory());

        lbl_about_app = (TextView)localView.findViewById(R.id.lbl_app_about);
        lbl_about_app.setText(objAbtApp.getAboutApp());
    }
}
