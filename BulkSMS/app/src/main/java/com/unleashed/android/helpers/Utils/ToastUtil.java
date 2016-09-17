package com.unleashed.android.helpers.Utils;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.Toast;


public class ToastUtil {

    public static Toast show(Context context, String message) {
        return show(context, message, false);
    }

    public static Toast show(Context context, int message) {
        return show(context, message, false);
    }


    public static Toast show(Fragment fragment, String message) {
        return show(fragment, message, false);
    }

    public static Toast show(Fragment fragment, String message, boolean longLength) {
        Context context = fragment.getActivity();
        if (context != null) {
            return show(context, message, longLength);
        }
        return null;
    }

    public static Toast show(Fragment fragment, int messageRes) {
        Context context = fragment.getActivity();
        if (context != null) {
            return show(context, messageRes, false);
        }
        return null;
    }

    public static Toast show(Context context, int messageRes, boolean longLength) {
        String message = context.getString(messageRes);
        return show(context, message, longLength);
    }

    public static Toast show(Context context, String message, boolean longLength) {
        final Toast toast;
        if (longLength) {
            toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        } else {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        }
        toast.show();
        return toast;
    }
}
