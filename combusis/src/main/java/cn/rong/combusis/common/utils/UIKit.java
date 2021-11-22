package cn.rong.combusis.common.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import java.io.Serializable;

/**
 * @author: BaiCQ
 * @ClassName: UIKit
 * @date: 2018/8/17
 * @Description: 常用简单api工具封装
 */
public class UIKit {
    public final static String KEY_BASE = "key_basis";
    public final static String KEY_OBJ = "key_obj";
    private final static Handler mainHand = new Handler(Looper.getMainLooper());
    private static Application mBaseContext;

    public static Context getContext() {
        if (null == mBaseContext) {
            mBaseContext = getInstanceFromReflexG();
        }
        return mBaseContext;
    }

    public static Resources getResources() {
        return getContext().getResources();
    }

    public static void postDelayed(Runnable r, long delay) {
        mainHand.postDelayed(r, delay);
    }

    public static void removeTask(Runnable r) {
        if (null != r) mainHand.removeCallbacks(r);
    }

    public static void runOnUiTherad(Runnable r) {
        if (null != r) mainHand.post(r);
    }

    public static AssetManager getAssets() {
        return getContext().getAssets();
    }

    public static View inflate(int layoutId) {
        return View.inflate(getContext(), layoutId, null);
    }

    public static <T extends View> T getView(Activity activity, int viewId) {
        return (T) activity.findViewById(viewId);
    }

    public static <T extends View> T getView(View parent, int viewId) {
        return (T) parent.findViewById(viewId);
    }

    public static <T extends View> void setVisiable(T t, boolean visiable) {
        if (null == t) return;
        t.setVisibility(visiable ? View.VISIBLE : View.GONE);
    }

    public static <T extends Activity> void startActivity(Activity actx, Class<T> activityClass) {
        actx.startActivity(new Intent(actx, activityClass));
    }

    public static <T extends Activity> void startActivityByObj(Activity actx, Class<T> activityClass, Serializable serializable) {
        actx.startActivity(new Intent(actx, activityClass).putExtra(KEY_OBJ, serializable));
    }

    public static <T extends Activity> void startActivityByBasis(Activity actx, Class<T> activityClass, Serializable serializable) {
        actx.startActivity(new Intent(actx, activityClass).putExtra(KEY_BASE, serializable));
    }

    private static Application getInstanceFromReflexG() {
        Application application = null;
        try {
            application = (Application) Class.forName("android.app.AppGlobals").getMethod("getInitialApplication").invoke(null);
            if (application == null) {
                throw new IllegalStateException("Static initialization of Applications must be on main thread.");
            }
        } catch (final Exception e) {
            try {
                application = (Application) Class.forName("android.app.ActivityThread").getMethod("currentApplication").invoke(null);
            } catch (final Exception ex) {
                e.printStackTrace();
            }
        }
        return application;
    }

    public static String getCurrentProcessName() {
        int pid = android.os.Process.myPid();
        String processName = "";
        ActivityManager manager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo process : manager.getRunningAppProcesses()) {
            if (process.pid == pid) {
                processName = process.processName;
            }
        }
        return processName;
    }

    public static String getVerName() {
        String verName = "";
        try {
            verName = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }
}
