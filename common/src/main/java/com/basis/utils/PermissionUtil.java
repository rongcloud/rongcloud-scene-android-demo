package com.basis.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Size;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class PermissionUtil {
    public final static int REQUEST_CODE = 1000;
    public final static String[] PERMISSIONS = new String[]{
            Manifest.permission.READ_CALENDAR,//日历
            Manifest.permission.WRITE_CALENDAR,
            Manifest.permission.CAMERA,//相机
            Manifest.permission.READ_CONTACTS,//联系人
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.LOCATION_HARDWARE,//定位
            Manifest.permission.RECORD_AUDIO,//麦克相关

            Manifest.permission.CALL_PHONE,//手机状态
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.BODY_SENSORS, //传感器
            Manifest.permission.READ_EXTERNAL_STORAGE, //存储权限
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_SMS,//短信
            Manifest.permission.SEND_SMS,
    };

    public static boolean hasPermissions(Context context, @Size(min = 1L) @NonNull String... var1) {
        if (Build.VERSION.SDK_INT < 23) {
            Log.w("EasyPermissions", "hasPermissions: API version < M, returning true by default");
            return true;
        } else if (context == null) {
            throw new IllegalArgumentException("Can't check permissions for null context");
        } else {
            String[] var2 = var1;
            int var3 = var1.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                String var5 = var2[var4];
                if (ContextCompat.checkSelfPermission(context, var5) != 0) {
                    return false;
                }
            }

            return true;
        }
    }

    public static boolean checkPermissions(Activity activity, String[] permissions) {
        return checkPermissions(activity, permissions, REQUEST_CODE);
    }

    public static boolean checkPermissions(Activity activity, String[] permissions, int requestCode) {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                ArrayList<String> requestPerssions = new ArrayList<>();
                int len = permissions.length;
                for (String permission : permissions) {
                    if (PackageManager.PERMISSION_GRANTED != activity.checkSelfPermission(permission)) {
                        requestPerssions.add(permission);
                    }
                }
                int size = requestPerssions.size();
                if (size > 0) {
                    activity.requestPermissions(requestPerssions.toArray(new String[size]), requestCode);
                    return false;
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean checkPermissions(Fragment fragment, String[] permissions) {
        return checkPermissions(fragment, permissions, REQUEST_CODE);
    }

    public static boolean checkPermissions(Fragment fragment, String[] permissions, int requestCode) {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                ArrayList<String> requestPerssions = new ArrayList<>();
                int len = permissions.length;
                for (String permission : permissions) {
                    if (PackageManager.PERMISSION_GRANTED != fragment.getActivity().checkSelfPermission(permission)) {
                        requestPerssions.add(permission);
                    }
                }
                int size = requestPerssions.size();
                if (size > 0) {
                    fragment.requestPermissions(requestPerssions.toArray(new String[size]), requestCode);
                    return false;
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public static void checkPermission(Activity activity, String permission, int requestCode) {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (PackageManager.PERMISSION_GRANTED != activity.checkSelfPermission(permission)) {
                    activity.requestPermissions(new String[]{permission}, requestCode);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取拒绝权限
     *
     * @param context
     * @param permissions
     * @return
     */
    public static String[] getDeniedPermissions(Context context, String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> deniedPermissionList = new ArrayList<>();
            for (String permission : permissions) {
                if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    deniedPermissionList.add(permission);
                }
            }
            int size = deniedPermissionList.size();
            if (size > 0) {
                return deniedPermissionList.toArray(new String[size]);
            }
        }
        return null;
    }
}
