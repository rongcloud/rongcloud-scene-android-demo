package com.basis.net.oklib.api.core;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

public class Utils {
    private static Application mBaseContext;

    /**
     * 检测网络连接
     *
     * @return
     */
    protected static boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getBaseContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null == manager) return false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network[] networks = manager.getAllNetworks();
            for (Network network : networks) {
                NetworkCapabilities capabilities = manager.getNetworkCapabilities(network);
                if (capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                    return true;
                }
            }
        } else {
            NetworkInfo info = manager.getActiveNetworkInfo();
            if (info != null && info.isAvailable()) {
                return true;
            }
        }
        return false;
    }

    private static Application getBaseContext() {
        if (null != mBaseContext) {
            return mBaseContext;
        }
        try {
            mBaseContext = (Application) Class.forName("android.app.AppGlobals").getMethod("getInitialApplication").invoke(null);
            if (null == mBaseContext) {
                throw new IllegalStateException("Static initialization of Applications must be on main thread.");
            }
        } catch (final Exception e) {
            try {
                mBaseContext = (Application) Class.forName("android.app.ActivityThread").getMethod("currentApplication").invoke(null);
            } catch (final Exception ex) {
                e.printStackTrace();
            }
        }
        return mBaseContext;
    }
}
