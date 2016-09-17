package com.unleashed.android.helpers;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.DrawableRes;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.unleashed.android.bulksms1.BuildConfig;
import com.unleashed.android.bulksms1.R;
import com.unleashed.android.helpers.crashreporting.CrashReportBase;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Helpers {
    private static final String PERSON_PATTERN = "[`~!@#$%^&*()_|+\\=?;:\",.<>\\{\\}\\[\\]\\\\\\/]+";
    static final String CHARS = "0123456789qwertyuiopQWERTYUIOPasdfghjklASDFGHJKLzxcvbnmZXCVBNM";
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    public static int MAX_IMAGE_DIMENSION = 1200;
    static Random rnd = new Random();

    private static final String FACEBOOK_PACKAGE_NAME = "com.facebook.katana";
    private static final String WHATSAPP_PACKAGE_NAME = "com.whatsapp";
    private static final String TWITTER_PACKAGE_NAME = "com.twitter.android";

    public static final int ACTION_RETRY_MESSAGE = 1;
    public static final int ACTION_RETRY_BLOCK = 2;
    public static final int ACTION_RETRY_DELETE = 3;
    public static final int ACTION_RETRY_RESTORE = 4;
    public static final int ACTION_RETRY_SAFETY_TIPS = 5;



    /**
     * Generate a value suitable for use in @link View.setId.
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    public static int generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }


//
//    public static boolean shouldLaunchLoginExperiment(Context context) {
//        if (UserNameManager.isUserLoggedIn())
//            return false;
//
//        if (Preferences.getAppPreference(context, Preferences.SHARED_PREF_HIDE_MR2_LOGIN_EXPERIMENT, false))
//            return false;
//
//        ApptimizeABTest.MandatoryRegistrationValues mandatoryRegistration2Value = ApptimizeABTest.getMandatoryRegistration2ABTestValues();
//        Preferences.setAppPreference(context, Preferences.SHARED_PREF_MR2_EXPERIMENT_STARTED, true);
//        if (mandatoryRegistration2Value == ApptimizeABTest.MandatoryRegistrationValues.ORIGINAL) {
//            return false;
//        }
//
//        return true;
//    }
//
//    public static void launchFacebookShare(Context context, String title, String url) {
//        ShareDialog shareDialog = new ShareDialog((Activity) context);
//
//        if (ShareDialog.canShow(ShareLinkContent.class) && url != null) {
//            ShareLinkContent content = new ShareLinkContent.Builder()
//                    .setContentTitle(context.getResources().getString(R.string.posted_ad_on_olx))
//                    .setContentDescription(title)
//                    .setContentUrl(Uri.parse(url))
//                    .build();
//            shareDialog.show(content);
//        }
//    }
//
//    public static void showEnableNotificationDialog(final Context context, final int comingFrom) {
//        final Dialog dialog = new Dialog(context);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        dialog.setContentView(R.layout.custom_permission_layout);
//        dialog.setCanceledOnTouchOutside(true);
//        dialog.setCancelable(true);
//
//        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialog) {
//
//            }
//        });
//
//        // set title, image and button text based on coming from
//
//        TextView tvTitle =  (TextView) dialog.findViewById(R.id.permission_dialog_title);
//        ImageView ivDialog = (ImageView) dialog.findViewById(R.id.permission_dialog_image);
//        TextView tvOkButton = (TextView) dialog.findViewById(R.id.permission_button_ok);
//
//        tvOkButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                switch (comingFrom) {
//                    case 1: // from chat conversation
//                        Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
//                        context.startActivity(intent);
//                        break;
//                    case 2:
//                        break;
//                }
//
//            }
//        });
//
//        dialog.show();
//    }
//
//    public static void launchFacebookShareFromAdDetail(Context context, String title, String url) {
//        ShareDialog shareDialog = new ShareDialog((Activity) context);
//
//        if (ShareDialog.canShow(ShareLinkContent.class) && url != null) {
//            ShareLinkContent content = new ShareLinkContent.Builder()
//                    .setContentTitle(context.getResources().getString(R.string.share_ad_title))
//                    .setContentDescription(title)
//                    .setContentUrl(Uri.parse(url))
//                    .build();
//            shareDialog.show(content);
//        }
//    }

    // make the keyboard hide on clicking anywhere in the activity
    public static void hideSoftKeyboard(Activity activity) {

        if (activity != null && activity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    // make the keyboard hide on clicking anywhere in the activity
    public static void setupUI(final Activity activity, View view) {
        if (activity != null && view != null) {
            if (!(view instanceof EditText)) {
                view.setOnTouchListener(new View.OnTouchListener() {

                    public boolean onTouch(View v, MotionEvent event) {
                        hideSoftKeyboard(activity);
                        return false;
                    }
                });
            }

            if (view instanceof ViewGroup) {
                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                    View innerView = ((ViewGroup) view).getChildAt(i);
                    setupUI(activity, innerView);
                }
            }
        }
    }

//    public static void launchWhatsAppShare(Context context, String title, String url) {
//
//        Intent sendIntent = new Intent();
//        sendIntent.setAction(Intent.ACTION_SEND);
//        sendIntent.setType("text/plain");
//        sendIntent.putExtra(Intent.EXTRA_TEXT, title + " " + url);
//        sendIntent.setPackage(WHATSAPP_PACKAGE_NAME);
//
//        try {
//            context.startActivity(sendIntent);
//        } catch (ActivityNotFoundException e) {
//            Toast.makeText(context, "Application not installed", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    public static void launchTwitterShare(Context context, String title, String url) {
//
//        Intent sendIntent = new Intent();
//        sendIntent.setAction(Intent.ACTION_SEND);
//        sendIntent.setType("text/plain");
//        sendIntent.putExtra(Intent.EXTRA_TEXT, title + " " + url);
//        sendIntent.setPackage(TWITTER_PACKAGE_NAME);
//
//        try {
//            context.startActivity(sendIntent);
//        } catch (ActivityNotFoundException e) {
//            Toast.makeText(context, "Application not installed", Toast.LENGTH_SHORT).show();
//        }
//    }

    public static HashMap<String, String> splitQuery(Uri uri) throws UnsupportedEncodingException {
        HashMap<String, String> query_pairs = new LinkedHashMap<String, String>();
        String query = uri.getQuery();
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
            }
        }
        return query_pairs;
    }

    public static int generateRandomNo() {
        int random = (int) (Math.random() * 100 + 1);
        return random;
    }

    public static int generateRandomTime() {
        Random r = new Random();
        int low = 2;
        int high = 6;
        int result = r.nextInt(high - low) + low;
        return result;
    }

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }


    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }


    public static String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(CHARS.charAt(rnd.nextInt(CHARS.length())));
        }
        return sb.toString();
    }

//    public static String getDeviceToken(Context context) {
//        String deviceToken = Preferences.getAppPreference(context, Preferences.SHARED_PREF_DEVICE_TOKEN, "");
//        if (deviceToken.equals("")) {
//            deviceToken = Helpers.generateToken();
//            Preferences.setAppPreference(context, Preferences.SHARED_PREF_DEVICE_TOKEN, deviceToken);
//        }
//        return deviceToken;
//    }
//
//    public static String generateToken() {
//        String token = Helpers.randomString(48);
//        String sha1;
//        try {
//            sha1 = AeSimpleSHA1.SHA1(token);
//        } catch (Exception e) {
//            sha1 = "aa";
//        }
//        return token + sha1.substring(0, 2);
//    }

//    public static String getUserAgent(Context context) {
//        return Config.USER_AGENT + " " + Config.getAppVersion(context) + " (" + AndroidVer.getVersion(Build.VERSION.SDK_INT) + ";)";
//        // Debug
//        // return Config.USER_AGENT + " " + Config.getAppVersion(context) +  " (" + AndroidVer.getVersion(android.os.Build.VERSION.SDK_INT) + ";) Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36 O/x.d3v0.12";
//    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = getExtension(url);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static String getExtension(String url) {
        String ext = null;
        if (url.contains("?")) {
            ext = url.substring(0, url.indexOf("?"));
        }
        if (url.contains(".")) {
            ext = url.substring(url.lastIndexOf(".") + 1).toLowerCase();
        }
        if (url.charAt(url.length() - 1) == '/') {
            ext = url.substring(0, url.length() - 1);
        }
        return ext;
    }

    public static String getFileName(String url) {
        String ext = null;
        if (url.contains("/")) {
            ext = url.substring(url.lastIndexOf("/") + 1).toLowerCase();
        }
        return ext;
    }

    public static String getPathFromMediaStore(Context context, Uri uri) {
//        String[]  currentCategory = { MediaStore.Images.Media.DATA };
//        CursorLoader loader = new CursorLoader(context, uri, currentCategory, null, null, null);
//        Cursor cursor = loader.loadInBackground();
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        cursor.moveToFirst();
//        return cursor.getString(column_index);

        String fileName = "unknown";//default fileName
        Uri filePathUri = uri;
        if (uri.getScheme().toString().compareTo("content") == 0) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);//Instead of "MediaStore.Images.Media.DATA" can be used "_data"
                filePathUri = Uri.parse(cursor.getString(column_index));
                fileName = filePathUri.getLastPathSegment().toString();
            }
        } else if (uri.getScheme().compareTo("file") == 0) {
            fileName = filePathUri.getLastPathSegment().toString();
        } else {
            fileName = fileName + "_" + filePathUri.getLastPathSegment();
        }
        return fileName;
    }

    public static String getFullPathFromMediaStore(Context context, Uri uri) {

        String fileName = null;//default fileName

        Uri filePathUri = uri;
        if (uri.getScheme().toString().compareTo("content") == 0) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                    try {
                        fileName = getContentPathFromDocument(context, uri);
                    } catch (Exception e) {

                        CrashReportBase.sendCrashReport(e);

                        if (fileName == null) {
                            fileName = getFilePathFromMediaStoreContentResolver(context, uri);
                        }
                    }

                    if (fileName == null) {
                        fileName = getFilePathFromMediaStoreContentResolver(context, uri);
                    }
                } else {
                    fileName = getFilePathFromMediaStoreContentResolver(context, uri);
                }
            } catch (Exception e) {
                CrashReportBase.sendCrashReport(e);
                fileName = null;
            }

            if (fileName != null && fileName.startsWith("http")) {
                fileName = null;
            }
        } else if (uri.getScheme().compareTo("file") == 0) {
            fileName = filePathUri.toString();
        } else {
            fileName = fileName + "_" + filePathUri.toString();
        }
        return fileName;
    }

    private static String getFilePathFromMediaStoreContentResolver(Context context, Uri uri) {
        String fileName = null;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);//Instead of "MediaStore.Images.Media.DATA" can be used "_data"
            Uri filePathUri = Uri.parse(cursor.getString(column_index));
            fileName = filePathUri.toString();
        }

        return fileName;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static String getContentPathFromDocument(Context context, Uri uri) {
        String wholeID = DocumentsContract.getDocumentId(uri);

        String id = wholeID.split(":")[1];
        String[] column = {MediaStore.Images.Media.DATA};

        String sel = MediaStore.Images.Media._ID + "=?";
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{id}, null);

        String filePath = null;

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }

        return filePath;
    }

//    public static String prepareApiUrl(String url) {
//        url = stripUriFragment(url);
//        //url = attachLanguage(url);
//        url = attachToken(url);
//        url = attachApiVersion(url);
//        return url;
//    }

    public static Uri generateUriFromUrl(String url) {
        Uri uri = Uri.parse(url);
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme(uri.getScheme()).path(uri.getPath()).authority(uri.getAuthority()).appendQueryParameter("json", "1");
        return uri;
    }

//    public static String attachLanguage(String url) {
//        String lang = I18n.getLang();
//        return lang.equals("") ? url : url.replace(Config.getDomain() + "i2", Config.getDomain() + lang + "/i2");
//    }
//
//    public static String attachToken(String url) {
//        url = attachApiVersion(url);
//        if (!url.contains(Config.getHost())) {
//            return url;
//        }
//        if (url.contains("token=" + Config.deviceToken)) {
//            return url + "&app_android=1";
//        }
//        return url + (url.contains("?") ? ("&token=" + Config.deviceToken) : ("?token=" + Config.deviceToken)) + "&app_android=1";
//    }
//
//    public static String attachApiVersion(String url) {
//
//        //TODO When country is changed , Correct headers are not apppended, We need to check all valid host name instead of current one
//        if (!url.contains(Config.getHost())) {
//            return url;
//        }
//        if (url.contains("version=" + Config.API_VERSION)) {
//            return url;
//        }
//        return url + (url.contains("?") ? ("&version=" + Config.API_VERSION) : ("?version=" + Config.API_VERSION));
//    }

    public static String getDisplayNameForUri(Context context, Uri uri) {
        Cursor returnCursor = context.getContentResolver().query(uri, null, null, null, null);
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);

        String fileDisplayName = null;

        if (returnCursor != null) {
            if (returnCursor.moveToFirst()) {
                fileDisplayName = returnCursor.getString(nameIndex);
            }
        }

        return fileDisplayName;
    }

    public static Long getFileSizeInBytesForUri(Context context, Uri uri) {
        Cursor returnCursor = context.getContentResolver().query(uri, null, null, null, null);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);

        Long sizeOfFile = null;

        if (returnCursor != null) {
            if (returnCursor.moveToFirst()) {
                sizeOfFile = returnCursor.getLong(sizeIndex);
            }
        }

        return sizeOfFile;
    }

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static boolean isEmpty(Collection coll) {
        return (coll == null || coll.isEmpty());
    }

    public static boolean isNotEmpty(Collection coll) {
        return !isEmpty(coll);
    }

    public static boolean shouldEnableCacheOnMemory() {
        if (Build.MODEL.equalsIgnoreCase("GT-N7100") || (Build.MANUFACTURER.equalsIgnoreCase("SAMSUNG") && Build.VERSION.RELEASE.equalsIgnoreCase("4.4.2"))) {
            return false;
        }
        return true;
    }

    public static boolean isLimitImageSize() {
        if (Build.MODEL.equalsIgnoreCase("GT-N7100")) {
            return true;
        }
        return false;
    }

    public static String getTimeDisplayForChat(String timeIn) {
        SimpleDateFormat dateFormatIn = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateFormatOut = new SimpleDateFormat("hh:mma EEE d MMM");
        try {
            Date date = dateFormatIn.parse(timeIn);
            return dateFormatOut.format(date).replace("AM", " am").replace("PM", " pm");
        } catch (ParseException e) {
            CrashReportBase.sendCrashReport(e);
            return timeIn;//returning input itself for now
        }
    }

    public static String getTimeDisplayForMyChats(String timeIn) {
        SimpleDateFormat dateFormatIn = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateFormatOut = new SimpleDateFormat("EEE d, hh:mma");
        try {
            Date date = dateFormatIn.parse(timeIn);
            return dateFormatOut.format(date).replace("AM", " am").replace("PM", " pm");
        } catch (ParseException e) {
            CrashReportBase.sendCrashReport(e);
            return timeIn;//returning input itself for now
        }
    }

    public static String getTimeForMyChats(String timeIn) {
        SimpleDateFormat dateFormatIn = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateFormatOut = new SimpleDateFormat("EEE d, hh:mma");
        try {
            Date date = dateFormatIn.parse(timeIn);
            String dateTime = dateFormatOut.format(date).replace("AM", " am").replace("PM", " pm");
            String[] split = dateTime.split(",");
            return split[1].trim();
        } catch (ParseException e) {
            CrashReportBase.sendCrashReport(e);
            return timeIn;//returning input itself for now
        }
    }

    public static String getNewTimeForMyChats(String timeIn) {
        String[] split = timeIn.split(",");

        if ("Today".equalsIgnoreCase(split[0])) {
            return split[1].trim();
        } else if ("Yesterday".equalsIgnoreCase(split[0])) {
            return split[0].trim();
        } else {
            return split[0].trim();
        }
    }

//    public static String getHostFromUrl(String domain) {
//        Uri uri = Uri.parse(domain);
//        Logger.push(Logger.LogType.LOG_INFO, "getHostFromUrl " + uri.getAuthority());
//        return uri.getAuthority();
//    }

    private static String stripUriFragment(String uri) {
        // Strip any #fragment after first #fragment.
        if (!android.text.TextUtils.isEmpty(uri) && uri.contains("#")) {
            uri = uri.substring(0, uri.indexOf("#"));
        }
        return uri;
    }

    public static String formatPrice(String label) {
        if (android.text.TextUtils.isEmpty(label))
            return label;
        label = label.replace("from", "").replace("\n", " ");
        return label.trim();
    }


    public static String getMyAdUrl(String ad_url) {
        if (!android.text.TextUtils.isEmpty(ad_url)) {
            Uri.Builder builder = Uri.parse(ad_url).buildUpon();
            builder.appendQueryParameter("extradata", "1");
            return builder.build().toString();
        }
        return null;
    }

    public static boolean isValidName(String name) {
        Pattern mPersonPattern = Pattern.compile(PERSON_PATTERN);
        Matcher matcher = mPersonPattern.matcher(name);
        if (matcher.find()) {
            return false;
        }
        return true;
    }

    public static String getModelInfo() {
        return Build.MANUFACTURER + " - " + Build.MODEL + ", v" + Build.VERSION.RELEASE;
    }



    public static void setSvgAsSource(Context context, ImageView view, @DrawableRes int resID) {
        Drawable drawable = getSvgAsDrawable(context, resID);
        if (drawable != null) view.setImageDrawable(drawable);
    }

    public static Drawable getSvgAsDrawable(Context context, @DrawableRes int resID) {
        VectorDrawableCompat vectorDrawableCompat = VectorDrawableCompat.create(context.getResources(), resID, context.getTheme());
        return vectorDrawableCompat != null ? vectorDrawableCompat.getCurrent() : null;
    }

    public static boolean isRooted() {
        return findBinary("su");
    }

    public static boolean findBinary(String binaryName) {
        boolean found = false;
        if (!found) {
            String[] places = {"/sbin/", "/system/bin/", "/system/xbin/", "/data/local/xbin/",
                    "/data/local/bin/", "/system/sd/xbin/", "/system/bin/failsafe/", "/data/local/"};
            for (String where : places) {
                if (new File(where + binaryName).exists()) {
                    found = true;
                    break;
                }
            }
        }
        return found;
    }




    public static String getIMEI(Context context) {
        String imei = null;
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            imei = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        }
        return imei;
    }

    public static void rateApp(Context context) {
        String packageName = context.getPackageName();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + packageName));
        if (openActivity(context, intent) == false) {
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + packageName));
            if (openActivity(context, intent) == false) {
                Toast.makeText(context, context.getResources().getString(R.string.play_app_not_found), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static boolean openActivity(Context context, Intent aIntent) {
        try {
            context.startActivity(aIntent);
            return true;
        } catch (ActivityNotFoundException e) {
            CrashReportBase.sendCrashReport(e);
            return false;
        }
    }

//    public static boolean isPlayServicesAvailable(Context context) {
//        return PlayServicesUtils.isGooglePlayServicesAvailable(context)
//                == ConnectionResult.SUCCESS;
//    }

    public static boolean isMandatoryRegEnabled(Context context) {
        /*if (context != null)
            return Preferences.getAppPreference(context, Preferences.SHARED_PREF_MAND_REG, false);
        return false;*/
        return true;
    }


    public static boolean isDebugModeBinary(){
        return (BuildConfig.DEBUG == true);
    }

    public static boolean isReleaseModeBinary(){
        return !(BuildConfig.DEBUG == true);
    }

}