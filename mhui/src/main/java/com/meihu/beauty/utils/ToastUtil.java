package com.meihu.beauty.utils;

import android.widget.Toast;

import androidx.annotation.Nullable;


/**
 * Created by cxf on 2017/8/3.
 */

public class ToastUtil {

    @Nullable
    private static Toast mToast;

//    public static final void show(final String message) {
//        BackgroundTasks.getInstance().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (mToast != null) {
//                    mToast.cancel();
//                    mToast = null;
//                }
//                mToast = Toast.makeText(MHSDK.getAppContext(), message, Toast.LENGTH_LONG);
//                mToast.show();
//            }
//        });
//    }

    public static void show(int res) {
        show(WordUtil.getString(MhDataManager.getInstance().getContext(), res));
    }

    public static final void show(final String message) {
        if (MhDataManager.getInstance().getContext() == null) {
            return;
        }
        BackgroundTasks.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mToast != null) {
                    mToast.cancel();
                    mToast = null;
                }
                mToast = Toast.makeText(MhDataManager.getInstance().getContext(), message, Toast.LENGTH_SHORT);
                mToast.show();
            }
        });
    }

}
