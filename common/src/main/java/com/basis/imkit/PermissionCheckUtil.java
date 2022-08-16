//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.basis.imkit;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.res.Resources.NotFoundException;
import android.media.AudioRecord;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.AppOpsManagerCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;

import com.basis.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class PermissionCheckUtil {
    private static final String TAG = PermissionCheckUtil.class.getSimpleName();
    public static final int REQUEST_CODE_ASK_PERMISSIONS = 100;
    public static final int REQUEST_CODE_LOCATION_SHARE = 101;
    private static PermissionCheckUtil.IRequestPermissionListListener listener;
    private static final String PROMPT = "prompt";
    private static final String IS_PROMPT = "isPrompt";

    public PermissionCheckUtil() {
    }

    public static boolean requestPermissions(Fragment fragment, String[] permissions) {
        return requestPermissions((Fragment)fragment, permissions, 0);
    }

    public static boolean requestPermissions(final Fragment fragment, String[] permissions, final int requestCode) {
        if (permissions.length == 0) {
            return true;
        } else {
            final List<String> permissionsNotGranted = new ArrayList();
            boolean result = false;
            String[] var5 = permissions;
            int var6 = permissions.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                String permission = var5[var7];
                if ((isFlyme() || VERSION.SDK_INT < 23) && permission.equals("android.permission.RECORD_AUDIO")) {
                    final SharedPreferences sharedPreferences = fragment.getContext().getSharedPreferences("prompt", 0);
                    boolean isPrompt = sharedPreferences.getBoolean("isPrompt", true);
                    if (isPrompt) {
                        showPermissionAlert(fragment.getContext(), fragment.getString(R.string.rc_permission_grant_needed) + fragment.getString(R.string.rc_permission_microphone), new OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (-1 == which) {
                                    fragment.startActivity(new Intent("android.settings.MANAGE_APPLICATIONS_SETTINGS"));
                                } else if (-3 == which) {
                                    Editor editor = sharedPreferences.edit().putBoolean("isPrompt", false);
                                    editor.commit();
                                }

                            }
                        });
                    }

                    return false;
                }

                if (!hasPermission(fragment.getActivity(), permission)) {
                    permissionsNotGranted.add(permission);
                }
            }

            if (permissionsNotGranted.size() > 0) {
                final int size = permissionsNotGranted.size();
                if (listener != null) {
                    listener.onRequestPermissionList(fragment.getActivity(), permissionsNotGranted, new PermissionCheckUtil.IPermissionEventCallback() {
                        public void confirmed() {
                            fragment.requestPermissions((String[])permissionsNotGranted.toArray(new String[size]), requestCode);
                        }

                        public void cancelled() {
                        }
                    });
                } else {
                    fragment.requestPermissions((String[])permissionsNotGranted.toArray(new String[size]), requestCode);
                }
            } else {
                result = true;
            }

            return result;
        }
    }

    public static boolean requestPermissions(final Activity activity, @NonNull String[] permissions) {
        return requestPermissions((Activity)activity, permissions, 0);
    }

    @TargetApi(23)
    public static boolean requestPermissions(final Activity activity, @NonNull final String[] permissions, final int requestCode) {
        if (VERSION.SDK_INT < 23) {
            return true;
        } else if (permissions.length == 0) {
            return true;
        } else {
            final List<String> permissionsNotGranted = new ArrayList();
            boolean result = false;
            String[] var5 = permissions;
            int var6 = permissions.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                String permission = var5[var7];
                if (!hasPermission(activity, permission)) {
                    permissionsNotGranted.add(permission);
                }
            }

            if (permissionsNotGranted.size() > 0) {
                final int size = permissionsNotGranted.size();
                if (listener != null) {
                    listener.onRequestPermissionList(activity, permissionsNotGranted, new PermissionCheckUtil.IPermissionEventCallback() {
                        public void confirmed() {
                            activity.requestPermissions((String[])permissionsNotGranted.toArray(new String[size]), requestCode);
                        }

                        public void cancelled() {
                        }
                    });
                } else {
                    activity.requestPermissions((String[])permissionsNotGranted.toArray(new String[size]), requestCode);
                }
            } else {
                result = true;
            }

            return result;
        }
    }

    public static boolean checkPermissions(Context context, @NonNull String[] permissions) {
        if (permissions.length == 0) {
            return true;
        } else {
            String[] var2 = permissions;
            int var3 = permissions.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                String permission = var2[var4];
                if ((isFlyme() || VERSION.SDK_INT < 23) && permission.equals("android.permission.RECORD_AUDIO")) {
                    Log.i(TAG, "Build.MODEL = " + Build.MODEL);
                    if (Build.BRAND.toLowerCase().equals("meizu")) {
                        if (!hasPermission(context, permission) && !hasRecordPermision(context)) {
                            return false;
                        }
                    } else if (!hasRecordPermision(context)) {
                        return false;
                    }
                } else if (!hasPermission(context, permission)) {
                    return false;
                }
            }

            return true;
        }
    }

    private static boolean isFlyme() {
        String osString = "";

        try {
            Class<?> clz = Class.forName("android.os.SystemProperties");
            Method get = clz.getMethod("get", String.class, String.class);
            osString = (String)get.invoke(clz, "ro.build.display.id", "");
        } catch (Exception var3) {
            Log.e(TAG, "isFlyme", var3);
        }

        return osString != null && osString.toLowerCase().contains("flyme");
    }

    private static boolean hasRecordPermision(Context context) {
        boolean hasPermission = false;
        int bufferSizeInBytes = AudioRecord.getMinBufferSize(44100, 12, 2);
        if (bufferSizeInBytes < 0) {
            Log.e(TAG, "bufferSizeInBytes = " + bufferSizeInBytes);
            return false;
        } else {
            try {
                AudioRecord audioRecord = new AudioRecord(1, 44100, 12, 2, bufferSizeInBytes);
                audioRecord.startRecording();
                if (audioRecord.getRecordingState() == 3) {
                    hasPermission = true;
                    audioRecord.stop();
                }

                audioRecord.release();
            } catch (Exception var5) {
                Log.e(TAG, "Audio record exception.");
            }

            return hasPermission;
        }
    }

    private static String getNotGrantedPermissionMsg(Context context, String[] permissions, int[] grantResults) {
        if (checkPermissionResultIncompatible(permissions, grantResults)) {
            return "";
        } else {
            try {
                List<String> permissionNameList = new ArrayList(permissions.length);

                for(int i = 0; i < permissions.length; ++i) {
                    if (grantResults[i] == -1) {
                        String permissionName = context.getString(context.getResources().getIdentifier("rc_" + permissions[i], "string", context.getPackageName()), new Object[]{0});
                        if (!permissionNameList.contains(permissionName)) {
                            permissionNameList.add(permissionName);
                        }
                    }
                }

                StringBuilder builder = new StringBuilder(context.getResources().getString(R.string.rc_permission_grant_needed));
                return builder.append("(").append(TextUtils.join(" ", permissionNameList)).append(")").toString();
            } catch (NotFoundException var6) {
                Log.e(TAG, "One of the permissions is not recognized by SDK." + Arrays.toString(permissions));
                return "";
            }
        }
    }

    private static String getNotGrantedPermissionMsg(Context context, List<String> permissions) {
        if (permissions != null && permissions.size() != 0) {
            HashSet permissionsValue = new HashSet();

            try {
                Iterator var4 = permissions.iterator();

                while(var4.hasNext()) {
                    String permission = (String)var4.next();
                    String permissionValue = context.getString(context.getResources().getIdentifier("rc_" + permission, "string", context.getPackageName()), new Object[]{0});
                    permissionsValue.add(permissionValue);
                }
            } catch (NotFoundException var7) {
                Log.e(TAG, "one of the permissions is not recognized by SDK." + permissions.toString());
                return "";
            }

            StringBuilder result = new StringBuilder("(");
            Iterator var9 = permissionsValue.iterator();

            while(var9.hasNext()) {
                String value = (String)var9.next();
                result.append(value).append(" ");
            }

            result = new StringBuilder(result.toString().trim() + ")");
            return result.toString();
        } else {
            return "";
        }
    }

    @TargetApi(11)
    private static void showPermissionAlert(Context context, String content, OnClickListener listener) {
        (new Builder(context)).setMessage(content).setPositiveButton(R.string.rc_confirm, listener).setNegativeButton(R.string.rc_cancel, listener).setNeutralButton(R.string.rc_not_prompt, listener).setCancelable(false).create().show();
    }

    @TargetApi(19)
    public static boolean canDrawOverlays(Context context) {
        return canDrawOverlays(context, true);
    }

    @TargetApi(19)
    public static boolean canDrawOverlays(final Context context, boolean needOpenPermissionSetting) {
        boolean result = true;
        if (VERSION.SDK_INT >= 23) {
            try {
                boolean booleanValue = (Boolean)Settings.class.getDeclaredMethod("canDrawOverlays", Context.class).invoke((Object)null, context);
                if (!booleanValue && needOpenPermissionSetting) {
                    ArrayList<String> permissionList = new ArrayList();
                    permissionList.add("android.settings.action.MANAGE_OVERLAY_PERMISSION");
                    showPermissionAlert(context, context.getString(R.string.rc_permission_grant_needed) + getNotGrantedPermissionMsg(context, permissionList), new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (-1 == which) {
                                Intent intent = new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + context.getPackageName()));
                                context.startActivity(intent);
                            }

                        }
                    });
                }

                Log.i(TAG, "isFloatWindowOpAllowed allowed: " + booleanValue);
                return booleanValue;
            } catch (Exception var7) {
                Log.e(TAG, String.format("getDeclaredMethod:canDrawOverlays! Error:%s, etype:%s", var7.getMessage(), var7.getClass().getCanonicalName()));
                return true;
            }
        } else if (VERSION.SDK_INT < 19) {
            return true;
        } else {
            Object systemService = context.getSystemService(Context.APP_OPS_SERVICE);

            Method method;
            try {
                method = Class.forName("android.app.AppOpsManager").getMethod("checkOp", Integer.TYPE, Integer.TYPE, String.class);
            } catch (NoSuchMethodException var8) {
                Log.e(TAG, String.format("NoSuchMethodException method:checkOp! Error:%s", var8.getMessage()));
                method = null;
            } catch (ClassNotFoundException var9) {
                Log.e(TAG, "canDrawOverlays", var9);
                method = null;
            }

            if (method != null) {
                try {
                    Integer tmp = (Integer)method.invoke(systemService, 24, context.getApplicationInfo().uid, context.getPackageName());
                    result = tmp != null && tmp == 0;
                } catch (Exception var10) {
                    Log.e(TAG, String.format("call checkOp failed: %s etype:%s", var10.getMessage(), var10.getClass().getCanonicalName()));
                }
            }

            Log.i(TAG, "isFloatWindowOpAllowed allowed: " + result);
            return result;
        }
    }

    private static boolean hasPermission(Context context, String permission) {
        String opStr = AppOpsManagerCompat.permissionToOp(permission);
        if (opStr == null && VERSION.SDK_INT < 23) {
            return true;
        } else {
            return context != null && context.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_DENIED;
        }
    }

    public static void showRequestPermissionFailedAlter(final Context context, @NonNull String[] permissions, @NonNull int[] grantResults) {
        String content = getNotGrantedPermissionMsg(context, permissions, grantResults);
        if (!TextUtils.isEmpty(content)) {
            OnClickListener listener = new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    switch(which) {
                    case -1:
                        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                        Uri uri = Uri.fromParts("package", context.getPackageName(), (String)null);
                        intent.setData(uri);
                        context.startActivity(intent);
                    case -2:
                    default:
                    }
                }
            };
            if (VERSION.SDK_INT >= 21) {
                (new Builder(context, 16974394)).setMessage(content).setPositiveButton(R.string.rc_confirm, listener).setNegativeButton(R.string.rc_cancel, listener).setCancelable(false).create().show();
            } else {
                (new Builder(context)).setMessage(content).setPositiveButton(R.string.rc_confirm, listener).setNegativeButton(R.string.rc_cancel, listener).setCancelable(false).create().show();
            }

        }
    }

    public static boolean checkPermissionResultIncompatible(String[] permissions, int[] grantResults) {
        return grantResults == null || grantResults.length == 0 || permissions == null || permissions.length != grantResults.length;
    }

    public static void setRequestPermissionListListener(PermissionCheckUtil.IRequestPermissionListListener listener) {
        if (listener != null) {
            PermissionCheckUtil.listener = listener;
        }
    }

    public interface IPermissionEventCallback {
        void confirmed();

        void cancelled();
    }

    public interface IRequestPermissionListListener {
        void onRequestPermissionList(Context activity, List<String> permissionsNotGranted, PermissionCheckUtil.IPermissionEventCallback callback);
    }
}
