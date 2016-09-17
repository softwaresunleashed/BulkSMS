package com.unleashed.android.helpers.logger;

import android.util.Log;

import com.unleashed.android.bulksms1.BuildConfig;


public class Logger {

    public final static String LOG_INFO = "INFORMATION";
    public final static String LOG_WARNING = "WARNING";
    public final static String LOG_VERBOSE = "VERBOSE";
    public final static String LOG_DEBUG = "DEBUG";
    public final static String LOG_ERROR = "ERROR";

    public final static String PREFIX = "BulkSMS";
    public final static String LOG_API = "API";
    public final static String LOG_CACHE = "CACHE";
    public final static String LOG_CONNECTION = "CONNECTION";
    public final static String LOG_IMAGES = "IMAGES";
    public final static String LOG_WEBVIEW = "WEBVIEW";
    public final static String LOG_PROFILER = "PROFILER";

    private final static String SPACE = " ";
    private static boolean logEnable = checkMode();
    private static boolean apiLogEnable = checkMode();
    private static boolean cacheLogEnable = checkMode();
    private static boolean connectionLogEnable = checkMode();
    private static boolean imagesLogEnable = checkMode();
    private static boolean webviewLogEnable = checkMode();
    private static boolean profilerLogEnable = checkMode();
    private static boolean debugLogEnable = checkMode();
    private static boolean warningLogEnable = checkMode();
    private static boolean errorLogEnable = checkMode();

    /**
     * method used to check the mode of the app
     * DEBUG = true
     * RELEASE = false
     */
    private static boolean checkMode() {
        return (BuildConfig.DEBUG == true);
    }

    /**
     * @param value : value to be print for the log
     */
    public static void push(String value) {
        push(LogType.LOG_INFO, "N/A", value);
    }

    /**
     * @param logType : type of the log
     * @param value   : value to be print for the log
     */
    public static void push(LogType logType, String value) {
        push(logType, "N/A", value);
    }

    /**
     * @param logType : type of the log
     * @param type    : Tag name
     * @param value   : value to be print for the log
     */
    public static void push(LogType logType, String type, int value) {
        push(logType, type, String.valueOf(value));
    }

    /**
     * This method print the log report.
     *
     * @param logType : type of the log
     * @param tagName : Tag name
     * @param value   : value to be print for the log
     */
    public static void push(LogType logType, String tagName, String value) {
        LogType mLogType = logType;
        boolean logThis = false;
        if (logEnable) {
            switch (mLogType) {
                case LOG_IMAGES:
                    if (imagesLogEnable && logType.toString().equals("LOG_IMAGES")) {
                        logThis = true;
                    }
                    break;
                case LOG_API:
                    if (apiLogEnable && logType.toString().equals("LOG_API")) {
                        logThis = true;
                    }
                    break;
                case LOG_CACHE:
                    if (cacheLogEnable && logType.toString().equals("LOG_CACHE")) {
                        logThis = true;
                    }
                    break;
                case LOG_CONNECTION:
                    if (connectionLogEnable && logType.toString().equals("LOG_CONNECTION")) {
                        logThis = true;
                    }
                    break;
                case LOG_WEBVIEW:
                    if (webviewLogEnable && logType.toString().equals("LOG_WEBVIEW")) {
                        logThis = true;
                    }
                    break;
                case LOG_PROFILER:
                    if (profilerLogEnable && logType.toString().equals("LOG_PROFILER")) {
                        logThis = true;
                    }
                    break;
                case LOG_INFO:
                    if (logEnable && logType.toString().equals("LOG_INFO")) {
                        logThis = true;
                    }
                    break;

                default:
                    System.out.println("Logger push() Default case");
                    break;
            }

            if (logThis) {
                Log.i(PREFIX + SPACE + logType.toString() + SPACE + tagName, value);
                return;
            }

            if (debugLogEnable && logType.toString().equals("LOG_DEBUG")) {
                Log.d(PREFIX + SPACE + logType.toString() + SPACE + tagName, value);
                return;
            }

            if (warningLogEnable && logType.toString().equals("LOG_WARNING")) {
                Log.w(PREFIX + SPACE + logType.toString() + SPACE + tagName, value);
                return;
            }

            if (errorLogEnable && logType.toString().equals("LOG_ERROR")) {
                Log.e(PREFIX + SPACE + logType.toString() + SPACE + tagName, value);
                if (!android.text.TextUtils.isEmpty(value)) {
                    Log.e(PREFIX + SPACE + logType.toString() + SPACE + tagName, value);
                }
            }
        }
    }

    public enum LogType {
        LOG_INFO, LOG_WARNING, LOG_VERBOSE, LOG_DEBUG, LOG_ERROR, LOG_PROFILER, LOG_WEBVIEW, LOG_IMAGES, LOG_CACHE, LOG_CONNECTION, LOG_API
    }
}
