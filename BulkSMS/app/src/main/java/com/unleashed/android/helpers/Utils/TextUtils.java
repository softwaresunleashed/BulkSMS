package com.unleashed.android.helpers.Utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.unleashed.android.application.SUApplication;
import com.unleashed.android.bulksms1.R;


public class TextUtils {


    public static String convertNumberToShortString(long count) {
        return withSuffix(count);
    }

    public static String withSuffix(long count) {
        if (count < 1000) return "" + count;
        int exp = (int) (Math.log(count) / Math.log(1000));
        return String.format("%.1f %c",
                count / Math.pow(1000, exp),
                "kMGTPE".charAt(exp - 1));
    }

    public static String capitalize(String s) {
        if (s.length() == 0) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    public static String titleCase(String string) {
        if (string == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        String[] words = string.split("\\s");
        for (String s : words) {
            builder.append(capitalize(s));
            builder.append(" ");
        }
        return builder.toString().trim();
    }

    public static void setFace(TextView view, Context context, AttributeSet attrs, int defStyle) {
        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.Typeface);
        if (array != null) {
            String typefaceAssetPath = array.getString(R.styleable.Typeface_customTypeface);
            int[] attrsArray = new int[]{android.R.attr.textStyle};
            final TypedArray array1 = context.obtainStyledAttributes(attrs, attrsArray);
            int style = array1.getInt(0, -1);  // returns 1 for bold, 2 for italic
            if (typefaceAssetPath == null) {
                //default font
                typefaceAssetPath = SUApplication.DEFAULT_FONT;
            }
            setTypography(view, context, typefaceAssetPath, style);
            array1.recycle();
            array.recycle();
        }

    }

    public static void setTypography(TextView view, Context context, String typefaceAssetPath, int defStyle) {
        Typeface typeface = null;

        if (SUApplication.getTypefaces().containsKey(typefaceAssetPath)) {
            typeface = SUApplication.getTypefaces().get(typefaceAssetPath);
        } else {
            AssetManager assets = context.getAssets();
            typeface = Typeface.createFromAsset(assets, typefaceAssetPath);
            SUApplication.getTypefaces().put(typefaceAssetPath, typeface);
        }

        view.setTypeface(typeface, defStyle);
    }

    public static boolean isNumeric(String number) {
        try {
            Long.parseLong(number);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Removes any symbols or text from string and returns last 10 digits assuming rest is country code.
     *
     * @param number
     * @return
     */
    public static String formatTo10DigitMobileNo(String number) {
        String digits = null;
        try {
            digits = number.replaceAll("[^0-9]", "");
            if (digits.length() > 10) {
                digits = digits.substring(digits.length() - 10);
            }
            return digits;
        } catch (Exception e) {
            return digits;
        }
    }

    public static float convertPixelsToSp(float px, Context context) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return px/scaledDensity;
    }

    public static String convertStringArrayToCommaSepratedString(String[] strArray){
        StringBuilder builder = new StringBuilder();
        for(String s : strArray) {
            builder.append(s);
            builder.append(",");
        }
        return builder.toString();
    }

}
