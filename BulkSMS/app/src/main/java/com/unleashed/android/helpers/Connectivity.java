package com.unleashed.android.helpers;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.unleashed.android.bulksms1.BuildConfig;


public class Connectivity {

    public static NetworkInfo getNetworkInfo(Context context) {
        if(context==null)
            return null;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    public static boolean isConnected(Context context) {
        NetworkInfo info = Connectivity.getNetworkInfo(context);
        return (info != null && info.isConnected());
    }

    public static boolean isConnectedWifi(Context context) {
        NetworkInfo info = Connectivity.getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
    }

    public static boolean isConnectedMobile(Context context) {
        NetworkInfo info = Connectivity.getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE);
    }

    public static boolean isConnectedFast(Context context) {
        NetworkInfo info = Connectivity.getNetworkInfo(context);
        return (info != null && info.isConnected() && Connectivity.isConnectionFast(context, info.getType(), info.getSubtype()));
    }

    public static boolean isConnectionFast(Context context, int type, int subType) {
        if(BuildConfig.DEBUG){
            Logger.push("gagandeep inside isConnectionFast:: network type ->"+type+" || subtype -> "+subType);
        }
        if (type == ConnectivityManager.TYPE_WIFI) {
//            uncomment below code if minimum bandwidth is required for wifi to be considered fast enough
            /**WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
             WifiInfo connectionInfo = wifiManager.getConnectionInfo();
             // link speed is in mbps so convert it to kbps by dividing it by 1000
             return connectionInfo.getLinkSpeed() / 1000 > 400; // link speed should be > 400 kbps
             */
            if(BuildConfig.DEBUG){
                Logger.push("gagandeep inside isConnectionFast:: device connected to wifi returning true");
            }
            return true;
        } else if (type == ConnectivityManager.TYPE_MOBILE) {
            switch (subType) {
                case TelephonyManager.NETWORK_TYPE_1xRTT: // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA: // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE: // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS: // ~ 100 kbps
                case TelephonyManager.NETWORK_TYPE_IDEN: // ~25 kbps // API level 8
                    if(BuildConfig.DEBUG){
                        Logger.push("gagandeep inside isConnectionFast:: device connected to mobile with subtype-> "+subType+" returning false");
                    }
                    return false;
                case TelephonyManager.NETWORK_TYPE_EVDO_0: // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A: // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_HSDPA: // ~ 2-14 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA: // ~ 700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA: // ~ 1-23 Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS: // ~ 400-7000 kbps
                case TelephonyManager.NETWORK_TYPE_EHRPD:  // ~ 1-2 Mbps // API level 11
                case TelephonyManager.NETWORK_TYPE_EVDO_B: // ~ 5 Mbps // API level 9
                case TelephonyManager.NETWORK_TYPE_HSPAP: // ~ 10-20 Mbps // API level 13
                case TelephonyManager.NETWORK_TYPE_LTE: // ~ 10+ Mbps // API level 11
                    if(BuildConfig.DEBUG){
                        Logger.push("gagandeep inside isConnectionFast:: device connected to mobile with subtype-> "+subType+" returning true");
                    }
                    return true;
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                default:
                    if(BuildConfig.DEBUG){
                        Logger.push("gagandeep inside isConnectionFast:: device connected to unknown network  with subtype-> "+subType+" returning false");
                    }
                    return false;
            }
        } else {
            if(BuildConfig.DEBUG){
                Logger.push("gagandeep inside isConnectionFast:: device connected to unknown network type  returning false");
            }
            return false;
        }
    }

}