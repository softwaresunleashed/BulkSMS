package com.unleashed.android.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.unleashed.android.bulksms1.MainActivity;

public class Preferences {
    public static final String SHARED_PREF_NAME = "BulkSMSPrefs";

    //Rating
    public static final String SHOW_RATING_PROMPT = "show_rating_prompt";
    public static final String REMIND_RATING_PROMPT_LATER = "remind_rating_prompt_later";
    public static final String RATING_PROMPT_TIME_STAMP = "remind_rating_prompt_time_stamp";
    public static final String SHARED_PREF_RATE_INSTALL_TIME = "instalation_time";

    // UserManager
    public static final String SHARED_PREF_USERNAME = "userName";
    public static final String SHARED_PREF_USER_ID = "user_id";
    public static final String SHARED_PREF_USER_TYPE = "user_type";
    public static final String SHARED_PREF_USER_STATUS = "user_status";
    public static final String SHARED_PREF_USER_PHONE = "user_phone";
    public static final String SHARED_PREF_USER_PHONE_CONFIRMED = "user_phone_confirmed";
    public static final String SHARED_PREF_USER_EMAIL = "user_email";
    public static final String SHARED_PREF_MISSED_CALL_NO = "missed_call_no";
    public static final String SHARED_PREF_ALREADY_FACEBOOK = "already_facebook";


    public static String getLocationPreference(Context context) {
        if (context != null) {
            SharedPreferences sp = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sp.getString("location", null);
        } else
            return null;
    }

    public static void setAppPreference(Context context, String key, String value) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
            sharedPreferencesEditor.putString(key, value);
            sharedPreferencesEditor.apply();
        }
    }


    public static String getAppPreference(Context context, String key, String defaultValue) {
        if (context != null) {
            SharedPreferences sp = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sp.getString(key, defaultValue);
        } else
            return defaultValue;
    }

    public static void setAppPreference(Context context, String key, boolean value) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
            sharedPreferencesEditor.putBoolean(key, value);
            sharedPreferencesEditor.apply();
        }
    }

    public static boolean getAppPreference(Context context, String key, boolean defaultValue) {
        if (context != null) {
            SharedPreferences sp = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sp.getBoolean(key, defaultValue);
        } else
            return defaultValue;
    }

    public static void setAppPreference(Context context, String key, int value) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
            sharedPreferencesEditor.putInt(key, value);
            sharedPreferencesEditor.apply();
        }
    }

    public static int getAppPreference(Context context, String key, int defaultValue) {
        if (context != null) {
            SharedPreferences sp = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sp.getInt(key, defaultValue);
        } else
            return defaultValue;
    }

    public static void setAppPreference(Context context, String key, long value) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
            sharedPreferencesEditor.putLong(key, value);
            sharedPreferencesEditor.apply();
        }
    }

    public static long getAppPreference(Context context, String key, long defaultValue) {
        if (context != null) {
            SharedPreferences sp = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sp.getLong(key, defaultValue);
        } else
            return defaultValue;
    }

    public static void setAppPreference(Context context, String key, float value) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
            sharedPreferencesEditor.putFloat(key, value);
            sharedPreferencesEditor.apply();
        }
    }

    public static float getAppPreference(Context context, String key, float defaultValue) {
        if (context != null) {
            SharedPreferences sp = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sp.getFloat(key, defaultValue);
        } else
            return defaultValue;
    }


//    public static void setAppPreference(Context context, String key, double value) {
//        setAppPreference(context, key, (float) value);
//    }
//
//    public static double getAppPreference(Context context, String key, double defaultValue) {
//        return getAppPreference(context, key, (float) defaultValue);
//    }


    public static void removeKey(Context context, String key) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
            sharedPreferencesEditor.remove(key);
            sharedPreferencesEditor.apply();
        }
    }

    public static boolean containsKey(Context context, String key) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.contains(key);
        }
        return false;
    }


    public static void setPreference(Context context, String key, int value) {
        final SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);

    }

    ////
//    public static SharedPreferences getPreferences(Context context)
//    {
//        return context.getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
//    }

    public static String getRateKey(Context context) {
        return SHARED_PREF_RATE_INSTALL_TIME;
    }

    public static String getRatedKey(Context context) {
        return getRateKey(context) + "_rated";
    }

}
