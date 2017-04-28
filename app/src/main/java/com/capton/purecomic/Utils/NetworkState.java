package com.capton.purecomic.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by capton on 2017/4/1.
 */

public class NetworkState {
    public static boolean isNetWorkAvailable(Context context){
        ConnectivityManager connectivityManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo_wifi=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo networkInfo_mobile=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return networkInfo_wifi.isConnected()||networkInfo_mobile.isConnected();
    }
    public static boolean isWifiConnected(Context context){
        ConnectivityManager connectivityManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo_wifi=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return networkInfo_wifi.isConnected() ;
    }
    public static boolean isMobileConnected(Context context){
        ConnectivityManager connectivityManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo_mobile=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return networkInfo_mobile.isConnected();
    }
}
