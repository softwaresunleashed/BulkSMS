package com.unleashed.android.helpers.navigationdrawer;


import android.content.Context;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.unleashed.android.bulksms1.R;

public class NavDrawer {
    private static NavDrawer navDrawerInstance;

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg;
    private ImageView imgProfilePic;
    private TextView txtName, txtWebsite;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    public static NavDrawer getInstance() {
        if(navDrawerInstance == null)
            navDrawerInstance = new NavDrawer();

        return navDrawerInstance;
    }

    private NavDrawer() {
    }

    public void initNavigationDrawer(Context appContext, View rootView){


        drawer = (DrawerLayout) rootView.findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) rootView.findViewById(R.id.nav_view);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        txtWebsite = (TextView) navHeader.findViewById(R.id.website);
        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        imgProfilePic = (ImageView) navHeader.findViewById(R.id.img_profile);

        // load toolbar titles from string resources
        activityTitles = appContext.getResources().getStringArray(R.array.nav_item_activity_titles);

        // load nav menu header data
        loadNavHeader();


//        if (savedInstanceState == null) {
//            navItemIndex = 0;
//            CURRENT_TAG = TAG_HOME;
//            loadHomeFragment();
//        }

    }

    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */
    private void loadNavHeader() {
        // name, website
        txtName.setText("Sudhanshu");
        txtWebsite.setText("www.softwaresunleashed.com");

        // loading header background image
//        Glide.with(this).load(urlNavHeaderBg)
//                .crossFade()
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(imgNavHeaderBg);

        // Loading profile image
//        Glide.with(this).load(urlProfileImg)
//                .crossFade()
//                .thumbnail(0.5f)
//                .bitmapTransform(new CircleTransform(this))
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(imgProfile);

        // showing dot next to notifications label
//        navigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    public void setUpNavigationView(NavigationView.OnNavigationItemSelectedListener navDrawerItemSelectListener) {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        if(navDrawerItemSelectListener != null){
            navigationView.setNavigationItemSelectedListener(navDrawerItemSelectListener);
        }

    }

    public void closeDrawers(){
        drawer.closeDrawers();
    }



    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
//        if (shouldLoadHomeFragOnBackPress) {
//            // checking if user is on other navigation menu
//            // rather than home
//            if (navItemIndex != 0) {
//                navItemIndex = 0;
//                CURRENT_TAG = TAG_HOME;
//                loadHomeFragment();
//                return;
//            }
//        }

    }






}
