package com.basis.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

public class NetUtil {

    public static boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) UIKit.getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null == manager) return false;
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            return true;
        }
        return false;
    }

    public static boolean isWifi() {
        ConnectivityManager manager = (ConnectivityManager) UIKit.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null == manager) return false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network networks = manager.getActiveNetwork();
            NetworkCapabilities networkCapabilities = manager.getNetworkCapabilities(networks);
            if (networkCapabilities != null) {
                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    return true;
                }
            }
        } else {
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isMobile() {
        ConnectivityManager manager = (ConnectivityManager) UIKit.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null == manager) return false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network networks = manager.getActiveNetwork();
            NetworkCapabilities networkCapabilities = manager.getNetworkCapabilities(networks);
            if (networkCapabilities != null) {
                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    return true;
                }
            }
        } else {
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    return true;
                }
            }
        }
        return false;
    }
}
