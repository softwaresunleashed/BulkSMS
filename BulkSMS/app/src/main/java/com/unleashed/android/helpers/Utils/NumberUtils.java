package com.unleashed.android.helpers.Utils;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;

import com.unleashed.android.helpers.crashreporting.CrashReportBase;

import java.text.DecimalFormat;

public class NumberUtils {

    public static final String currencySymbol = Character.toString((char) 0x20B1);

    public static Spannable getCurrency() {
        Spannable sp = new SpannableString(currencySymbol);
        sp.setSpan(new StrikethroughSpan(), 0, 1, 0);
        return sp;
    }

    public static Spannable formatCurrency(String value) {
        try {
            DecimalFormat formatter = new DecimalFormat("#,##0.00");
            Spannable sp = new SpannableString(currencySymbol + " " + formatter.format(Float.parseFloat(value)));
            //sp.setSpan(new StrikethroughSpan(), 0, 1, 0);
            return sp;
        } catch (Exception e) {
            CrashReportBase.sendCrashReport(e);
            return new SpannableString("");
        }
    }

    public static Spannable formatCurrency(double value) {
        return formatCurrency(value, false);
    }

    public static Spannable formatCurrency(double value, boolean noDecimal) {
        try {
            DecimalFormat formatter = new DecimalFormat(noDecimal ? "#,##0.########" : "#,##0.00");
            Spannable sp = new SpannableString(currencySymbol + " " + formatter.format(value));
            //sp.setSpan(new StrikethroughSpan(), 0, 1, 0);
            return sp;
        } catch (Exception e) {
            CrashReportBase.sendCrashReport(e);
            return new SpannableString("");
        }
    }

    public static String formatComma(int value) {
        try {
            DecimalFormat formatter = new DecimalFormat("#,##0");
            return formatter.format(value);
        } catch (Exception e) {
            CrashReportBase.sendCrashReport(e);
            return "";
        }
    }

    public static String formatComma(String value) {
        try {
            DecimalFormat formatter = new DecimalFormat("#,##0.00");
            return formatter.format(Float.parseFloat(value));
        } catch (Exception e) {
            CrashReportBase.sendCrashReport(e);
            return "";
        }
    }


    public static String formatComma(double value) {
        try {
            DecimalFormat formatter = new DecimalFormat("#,##0.00");
            return formatter.format(value);
        } catch (Exception e) {
            CrashReportBase.sendCrashReport(e);
            return "";
        }
    }

    public static String toString(double value) {
        try {
            DecimalFormat formatter = new DecimalFormat("#.#########");
            return formatter.format(value);
        } catch (Exception e) {
            CrashReportBase.sendCrashReport(e);
            return "";
        }
    }

    public static int parseWithDefault(String number, int defaultVal) {
        try {
            return Integer.parseInt(number);
        } catch (Exception e) {
            return defaultVal;
        }
    }

    public static float parseWithDefault(String number, float defaultVal) {
        try {
            return Float.parseFloat(number.trim());
        } catch (Exception e) {
            return defaultVal;
        }
    }

    public static double parseWithDefault(String number, double defaultVal) {
        try {
            return Double.parseDouble(number);
        } catch (Exception e) {
            return defaultVal;
        }
    }

    public static long parseWithDefault(String number, long defaultVal) {
        try {
            return Long.parseLong(number);
        } catch (Exception e) {
            return defaultVal;
        }
    }

    public static int random(int min, int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }


    // Return true if value if not zero and min/max range are not zero
    public static boolean isWithinRangeNotZero(int value, int min, int max) {

        if (value == 0 || (min == 0 && max == 0)) return true;

        if (min != 0 && value < min) return false;

        return !(max != 0 && value > max);

    }

}
