package com.unleashed.android.helpers.login;

import android.app.Activity;

import com.unleashed.android.helpers.trackers.Trackers;

/**
 * Created by sudhanshu on 17/09/16.
 */

public class LoginScreen {

    public static void showLoginActivity(Activity activity) {

        Trackers.trackEvent(Trackers.EVENT_LOGIN_PAGE_OPEN);

        SocialLoginActivity.startActivityForResult(activity);
    }
}
