/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;

public class KFileProvider extends FileProvider {
    public final static String AUTHORITY_PATH = ".uikit.fileprovider";

    /**
     * @param file
     * @return Uri
     */
    public static Uri getUriForFile(Context context, File file) {
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String authority = context.getApplicationInfo().packageName + AUTHORITY_PATH;
            uri = getUriForFile(context, authority, file);
        } else {
            uri = Uri.fromFile(file);
        }
        Log.e("KFileProvider", "uri = " + (null == uri ? "null" : uri.toString()));
        return uri;
    }

}