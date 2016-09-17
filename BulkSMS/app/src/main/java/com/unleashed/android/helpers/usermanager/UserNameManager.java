package com.unleashed.android.helpers.usermanager;

import android.content.Context;
import android.text.TextUtils;

import com.facebook.AccessToken;
import com.unleashed.android.helpers.Preferences;
import com.unleashed.android.helpers.config.Config;


/**
 * Created by michal.luszczuk on 2014-10-20.
 */
public class UserNameManager {
    public static final String PREF_KEY_USER_NAME = "userName";
    public static final String PREF_KEY_NUMERIC_USER_ID = "numericUserId";

    public static String getFaceBookUserId() {
        return faceBookUserId;
    }

    public static void setFaceBookUserId(String faceBookUserId) {
        UserNameManager.faceBookUserId = faceBookUserId;
    }

    protected static String faceBookUserId;// dummy to be removed

    protected static String userId;
    protected static String userType;
    protected static String userStatus;
    protected static String userPhone;
    protected static boolean userPhoneConfirmed;
    protected static String userEmail;
    protected static String username;
    protected static String missedCallNumber;
    private static boolean isAlreadyFacebookUser;

    protected static Context context;
    protected static int pendingAdsCount;

    public static synchronized void init(Context cxt) {
        context = cxt;
        username = Preferences.getAppPreference(cxt, Preferences.SHARED_PREF_USERNAME, null);
        userId = Preferences.getAppPreference(cxt, Preferences.SHARED_PREF_USER_ID, null);
        userType = Preferences.getAppPreference(cxt, Preferences.SHARED_PREF_USER_TYPE, null);
        userPhone = Preferences.getAppPreference(cxt, Preferences.SHARED_PREF_USER_PHONE, null);
        userEmail = Preferences.getAppPreference(cxt, Preferences.SHARED_PREF_USER_EMAIL, null);
        userStatus = Preferences.getAppPreference(cxt, Preferences.SHARED_PREF_USER_STATUS, null);
        userPhoneConfirmed = Preferences.getAppPreference(cxt, Preferences.SHARED_PREF_USER_PHONE_CONFIRMED, false);
        missedCallNumber = Preferences.getAppPreference(cxt, Preferences.SHARED_PREF_MISSED_CALL_NO, null);
        isAlreadyFacebookUser = Preferences.getAppPreference(cxt, Preferences.SHARED_PREF_ALREADY_FACEBOOK, false);
    }


    public static synchronized void clear() {
        setUserId(null);
        setUserType(null);
        setUserStatus(null);
        setUserPhone(null);
        setUserPhoneConfirmed(false);
        setAlreadyFacebookLogin(false);
        setUserEmail(null);
        setUsername(null);
        setPendingAdsCount(-1);
        Config.isLogged = false;
    }

    public static synchronized String getUsername() {
        return username;
    }

    public static synchronized void setUsername(String username) {
        UserNameManager.username = username;
        Preferences.setAppPreference(context, Preferences.SHARED_PREF_USERNAME, username);
//        MixpanelPeopleProfile2.onUserNameChanged(SUApplication.getContext(), username);
    }

    public static synchronized String getUserId() {
        return userId;
    }

    public static synchronized void setUserId(String userId) {
        UserNameManager.userId = userId;
        Preferences.setAppPreference(context, Preferences.SHARED_PREF_USER_ID, userId);
//        MixpanelPeopleProfile2.onUserIdChanged(TablicaApplication.getContext(),userId);
    }

    public static synchronized String getUserType() {
        return userType;
    }

    public static synchronized void setUserType(String userType) {
        UserNameManager.userType = userType;
        Preferences.setAppPreference(context, Preferences.SHARED_PREF_USER_TYPE, userType);
    }

    public static synchronized String getUserPhone() {
        return userPhone;
    }

    public static synchronized void setUserPhone(String userPhone) {
        UserNameManager.userPhone = userPhone;
        Preferences.setAppPreference(context, Preferences.SHARED_PREF_USER_PHONE, userPhone);
//        MixpanelPeopleProfile2.onUserPhoneChanged(TablicaApplication.getContext(),userPhone);
    }

    public static synchronized String getUserEmail() {
        return userEmail;
    }

    public static synchronized void setUserEmail(String userEmail) {
        UserNameManager.userEmail = userEmail;
        Preferences.setAppPreference(context, Preferences.SHARED_PREF_USER_EMAIL, userEmail);
//        MixpanelPeopleProfile2.onUserEmailChanged(TablicaApplication.getContext(),isEmailAccount()?userEmail:"");
    }

//    public static synchronized void setUserData(LoginResponse data){
//        UserNameManager.setUsername(data.getName());
//        UserNameManager.setUserId(data.getUserId());
//        UserNameManager.setUserPhone(data.getPhone());
//        UserNameManager.setAlreadyFacebookLogin(data.isFacebookUser());
//    }

    public static synchronized String getUserStatus() {
        return userStatus;
    }

    public static synchronized void setUserStatus(String userStatus) {
        UserNameManager.userStatus = userStatus;
        Preferences.setAppPreference(context, Preferences.SHARED_PREF_USER_STATUS, userStatus);
    }

    public static synchronized boolean isUserPhoneConfirmed() {
        return userPhoneConfirmed;
    }

    public static synchronized boolean isUnconfirmedPhonePoster() {
        return isPhonePoster() && !isUserPhoneConfirmed();
    }

    public static synchronized void setUserPhoneConfirmed(boolean userPhoneConfirmed) {
        UserNameManager.userPhoneConfirmed = userPhoneConfirmed;
        Preferences.setAppPreference(context, Preferences.SHARED_PREF_USER_PHONE_CONFIRMED, userPhoneConfirmed);
    }

    public static synchronized boolean isAnon() {
        return UserType.anon.toString().equals(UserNameManager.getUserType());
    }

    public static synchronized boolean isPhonePoster() {
        return UserType.phoneposter.toString().equals(UserNameManager.getUserType());
    }

    public static synchronized boolean isUserLoggedIn() {
        return isPhonePoster() ? isUserPhoneConfirmed() : !TextUtils.isEmpty(getUserId());
    }

    public static synchronized boolean isEmailAccount() {
        return UserType.email.toString().equals(UserNameManager.getUserType());
    }

    public static synchronized boolean isFacebookAccount() {
        return AccessToken.getCurrentAccessToken() != null || isAlreadyFacebookUser();
    }

    public static int getPendingAdsCount() {
        return pendingAdsCount;
    }

    public static void setPendingAdsCount(int pendingAdsCount) {
        UserNameManager.pendingAdsCount = pendingAdsCount;
    }

    public static synchronized String getMissedCallNumber() {
        return missedCallNumber;
    }

    public static synchronized void setMissedCallNumber(String missedCallNumber) {
        UserNameManager.missedCallNumber = missedCallNumber;
        Preferences.setAppPreference(context, Preferences.SHARED_PREF_MISSED_CALL_NO, missedCallNumber);
    }

    public static void logout() {
//        if (UserNameManager.isUserLoggedIn()) {
//            Preferences.clearLocationPrefrences(context);
//        }
//        clear();
//        FacebookLoginActivity.callFacebookLogout(context);
//        FileCache.removeCache(context, "postingad");

    }

    public static boolean isDummyFacebookUser() {
        return UserType.fb_dummy.toString().equals(UserNameManager.getUserType());
    }

    public static void setAlreadyFacebookLogin(boolean isAlreadyFacebookUser) {
        UserNameManager.isAlreadyFacebookUser = isAlreadyFacebookUser;
        Preferences.setAppPreference(context, Preferences.SHARED_PREF_ALREADY_FACEBOOK, isAlreadyFacebookUser);
//        MixpanelPeopleProfile2.onFacebookStatusChanged(TablicaApplication.getContext(),isAlreadyFacebookUser);
    }

    public static boolean isAlreadyFacebookUser() {
        return UserNameManager.isAlreadyFacebookUser;
    }

    public enum UserType {
        anon,
        email,
        phoneposter,
        fb_dummy,
        unknown
    }

    public enum UserStatus {
        confirmed,
        banned,
        deleted,
        unconfirmed,
        device_user,
        unknown
    }

}
