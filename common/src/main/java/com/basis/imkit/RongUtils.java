//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.basis.imkit;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build.VERSION;
import android.provider.Settings.Secure;
import android.provider.Settings.SettingNotFoundException;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
//import io.rong.common.LibStorageUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RongUtils {
    private static final String TAG = RongUtils.class.getSimpleName();
    private static double RATIO = 0.85D;
    public static int screenWidth;
    public static int screenHeight;
    public static int screenMin;
    public static int screenMax;
    public static float density;
    public static float scaleDensity;
    public static float xdpi;
    public static float ydpi;
    public static int densityDpi;
    public static int dialogWidth;
    public static int statusbarheight;
    public static int navbarheight;
    private static String RONG_IM_KIT = "RONG_IM_KIT";
    private static String KEY_KEYBOARD_HEIGHT = "KEY_BROADCAST_HEIGHT";
    private static int TEMP_KEYBOARD_HEIGHT = -1;
    private static int TEMP_KEYBOARD_ORIENTATION = -1;

    public RongUtils() {
    }

    public static int dip2px(float dipValue) {
        return (int)(dipValue * density + 0.5F);
    }

    public static int px2dip(float pxValue) {
        return (int)(pxValue / density + 0.5F);
    }

    public static int getDialogWidth() {
        dialogWidth = (int)((double)screenMin * RATIO);
        return dialogWidth;
    }

    public static void init(Context context) {
        if (null != context) {
            DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
            screenWidth = dm.widthPixels;
            screenHeight = dm.heightPixels;
            screenMin = screenWidth > screenHeight ? screenHeight : screenWidth;
            density = dm.density;
            scaleDensity = dm.scaledDensity;
            xdpi = dm.xdpi;
            ydpi = dm.ydpi;
            densityDpi = dm.densityDpi;
        }
    }

    public static void GetInfo(Context context) {
        if (null != context) {
            DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
            screenWidth = dm.widthPixels;
            screenHeight = dm.heightPixels;
            screenMin = screenWidth > screenHeight ? screenHeight : screenWidth;
            screenMax = screenWidth < screenHeight ? screenHeight : screenWidth;
            density = dm.density;
            scaleDensity = dm.scaledDensity;
            xdpi = dm.xdpi;
            ydpi = dm.ydpi;
            densityDpi = dm.densityDpi;
            statusbarheight = getStatusBarHeight(context);
            navbarheight = getNavBarHeight(context);
            Log.d(TAG, "screenWidth=" + screenWidth + " screenHeight=" + screenHeight + " density=" + density);
        }
    }

    public static int getStatusBarHeight(Context context) {
        int sbar = 0;

        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            sbar = context.getResources().getDimensionPixelSize(x);
        } catch (Exception var7) {
            Log.e(TAG, "getStatusBarHeight", var7);
        }

        return sbar;
    }

    public static int getScreenWidth() {
        return screenWidth;
    }

    public static int getScreenHeight() {
        return screenHeight;
    }

    public static int getNavBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        return resourceId > 0 ? resources.getDimensionPixelSize(resourceId) : 0;
    }

    public static Drawable getDrawable(Context context, String resource) {
        InputStream is = null;

        try {
            Resources resources = context.getResources();
            Options options = new Options();
            options.inDensity = 240;
            options.inScreenDensity = resources.getDisplayMetrics().densityDpi;
            options.inTargetDensity = resources.getDisplayMetrics().densityDpi;
            is = context.getAssets().open(resource);
            Bitmap bitmap = BitmapFactory.decodeStream(is, new Rect(), options);
            BitmapDrawable var6 = new BitmapDrawable(context.getResources(), bitmap);
            return var6;
        } catch (Exception var16) {
            Log.e(TAG, "getDrawable", var16);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException var15) {
                    Log.e(TAG, "getDrawable", var15);
                }
            }

        }

        return null;
    }

    public static Uri getUriFromDrawableRes(Context context, int id) {
        Resources resources = context.getResources();
        String path = "android.resource://" + resources.getResourcePackageName(id) + "/" + resources.getResourceTypeName(id) + "/" + resources.getResourceEntryName(id);
        return Uri.parse(path);
    }

    public static Bitmap getResizedBitmap(Context context, Uri uri, int widthLimit, int heightLimit) throws IOException {
        String path;
        if (uri.getScheme().equals("file")) {
            path = uri.getPath();
        } else {
            if (!uri.getScheme().equals("content")) {
                return null;
            }

            Cursor cursor = context.getContentResolver().query(uri, new String[]{"_data"}, (String)null, (String[])null, (String)null);
            cursor.moveToFirst();
            path = cursor.getString(0);
            cursor.close();
        }

        ExifInterface exifInterface = new ExifInterface(path);
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int orientation = exifInterface.getAttributeInt("Orientation", 0);
        int width;
        if (orientation == 6 || orientation == 8 || orientation == 5 || orientation == 7) {
            width = widthLimit;
            widthLimit = heightLimit;
            heightLimit = width;
        }

        width = options.outWidth;
        int height = options.outHeight;
        int sampleW = 1;

        int sampleH;
        for(sampleH = 1; width / 2 > widthLimit; sampleW <<= 1) {
            width /= 2;
        }

        while(height / 2 > heightLimit) {
            height /= 2;
            sampleH <<= 1;
        }

        options = new Options();
        int sampleSize = Math.max(sampleW, sampleH);
        options.inSampleSize = sampleSize;

        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeFile(path, options);
        } catch (OutOfMemoryError var22) {
            Log.e(TAG, "getResizedBitmap", var22);
            options.inSampleSize <<= 1;
            bitmap = BitmapFactory.decodeFile(path, options);
        }

        Matrix matrix = new Matrix();
        if (bitmap == null) {
            return bitmap;
        } else {
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            if (orientation == 6 || orientation == 8 || orientation == 5 || orientation == 7) {
                int tmp = w;
                w = h;
                h = tmp;
            }

            switch(orientation) {
            case 2:
                matrix.preScale(-1.0F, 1.0F);
                break;
            case 3:
                matrix.setRotate(180.0F, (float)w / 2.0F, (float)h / 2.0F);
                break;
            case 4:
                matrix.preScale(1.0F, -1.0F);
                break;
            case 5:
                matrix.setRotate(90.0F, (float)w / 2.0F, (float)h / 2.0F);
                matrix.preScale(1.0F, -1.0F);
                break;
            case 6:
                matrix.setRotate(90.0F, (float)w / 2.0F, (float)h / 2.0F);
                break;
            case 7:
                matrix.setRotate(270.0F, (float)w / 2.0F, (float)h / 2.0F);
                matrix.preScale(1.0F, -1.0F);
                break;
            case 8:
                matrix.setRotate(270.0F, (float)w / 2.0F, (float)h / 2.0F);
            }

            float xS = (float)widthLimit / (float)bitmap.getWidth();
            float yS = (float)heightLimit / (float)bitmap.getHeight();
            matrix.postScale(Math.min(xS, yS), Math.min(xS, yS));

            try {
                Bitmap result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                return result;
            } catch (OutOfMemoryError var21) {
                Log.e(TAG, "getResizedBitmap", var21);
                Log.e("ResourceCompressHandler", "OOMHeight:" + bitmap.getHeight() + "Width:" + bitmap.getHeight() + "matrix:" + xS + " " + yS);
                return null;
            }
        }
    }

    public static int getVideoDuration(Context context, String videoPath) {
        try {
            MediaPlayer mp = MediaPlayer.create(context, Uri.parse(videoPath));
            int duration = mp.getDuration();
            mp.release();
            return duration;
        } catch (Exception var4) {
            Log.e(TAG, var4.toString());
            return 0;
        }
    }

    public static String md5(Object object) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(toByteArray(object));
        } catch (NoSuchAlgorithmException var7) {
            throw new RuntimeException("Huh, MD5 should be supported?", var7);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        byte[] var3 = hash;
        int var4 = hash.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            byte b = var3[var5];
            if ((b & 255) < 16) {
                hex.append("0");
            }

            hex.append(Integer.toHexString(b & 255));
        }

        return hex.toString();
    }

    private static byte[] toByteArray(Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();
            oos.close();
            bos.close();
        } catch (IOException var4) {
            Log.e(TAG, "toByteArray", var4);
        }

        return bytes;
    }

    /** @deprecated */
    @Deprecated
    public static String getAppName(Context context) {
        return "";
    }

    public static boolean isDestroy(Activity activity) {
        return activity == null || activity.isFinishing() || VERSION.SDK_INT >= 17 && activity.isDestroyed();
    }

    public static boolean isLocationServiceEnabled(Context context) {
        boolean isLocationServiceEnabled = false;
        if (VERSION.SDK_INT >= 28) {
            LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null) {
                isLocationServiceEnabled = locationManager.isLocationEnabled();
            }
        } else if (VERSION.SDK_INT >= 19) {
            try {
                isLocationServiceEnabled = Secure.getInt(context.getContentResolver(), "location_mode") != 0;
            } catch (SettingNotFoundException var3) {
            }
        } else {
            isLocationServiceEnabled = !TextUtils.isEmpty(Secure.getString(context.getContentResolver(), "location_providers_allowed"));
        }

        return isLocationServiceEnabled;
    }

    public static boolean phoneIsInUse(Context context) {
        if (context == null) {
            return false;
        } else {
            TelephonyManager mTelephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            int state = mTelephonyManager.getCallState();
            return state != 0;
        }
    }

    public static int getSaveKeyBoardHeight(Context context, int orientation) {
        if (TEMP_KEYBOARD_HEIGHT != -1 && orientation == TEMP_KEYBOARD_ORIENTATION) {
            return TEMP_KEYBOARD_HEIGHT;
        } else {
            SharedPreferences sharedPreferences = context.getSharedPreferences(RONG_IM_KIT, 0);
            int height = sharedPreferences.getInt(getKeyboardHeightKey(context, orientation), 0);
            TEMP_KEYBOARD_HEIGHT = height;
            TEMP_KEYBOARD_ORIENTATION = orientation;
            return height;
        }
    }

    public static void saveKeyboardHeight(Context context, int orientation, int height) {
        if (TEMP_KEYBOARD_HEIGHT != height || orientation != TEMP_KEYBOARD_ORIENTATION) {
            TEMP_KEYBOARD_HEIGHT = height;
            TEMP_KEYBOARD_ORIENTATION = orientation;
            SharedPreferences sharedPreferences = context.getSharedPreferences(RONG_IM_KIT, 0);
            sharedPreferences.edit().putInt(getKeyboardHeightKey(context, orientation), height).apply();
        }

    }

    private static String getKeyboardHeightKey(Context context, int orientation) {
        return KEY_KEYBOARD_HEIGHT + "_" + orientation;
    }
}
