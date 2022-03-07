package com.basis.utils;

import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.basis.R;


/**
 * @author: BaiCQ
 * @ClassName: KToast
 * @date: 2018/4/4
 */
public class KToast {
    private final static KToast ktIns = new KToast();
    private TextView showText;
    private View tipsView;
    private Toast toast;
    private final static int distance = ScreenUtil.getScreenWidth() / 5;


    private KToast() {
        tipsView = UIKit.inflate(R.layout.kit_layout_toast);
        showText = tipsView.findViewById(R.id.toast_content);
    }

    private Toast init() {
        Toast toast = new Toast(UIKit.getContext());
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, distance);//底部
        toast.setView(tipsView);
        toast.setDuration(Toast.LENGTH_LONG);
        return toast;
    }

    private KToast makeText(int resId) {
        showText.setText(resId);
        return this;
    }

    private KToast makeText(String msg) {
        showText.setText(msg);
        return this;
    }


    /**
     * @param cancelLast 是否取消上一个
     */
    private void show(boolean cancelLast) {
        if (cancelLast && null != toast) {
            toast.cancel();
        }
        toast = init();
        toast.show();
    }

    /**
     * 显示通知
     *
     * @param resouceId 显示的字符串 id
     */
    public static void show(final int resouceId) {
        if (null != Looper.myLooper()) {
            ktIns.makeText(resouceId).show(false);
        } else {
            UIKit.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ktIns.makeText(resouceId).show(false);
                }
            });
        }
    }

    public static void show(final String msg) {
        if (null != Looper.myLooper()) {
            ktIns.makeText(msg).show(true);
        } else {
            UIKit.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ktIns.makeText(msg).show(true);
                }
            });
        }
    }

    public static void show(final String msg, final boolean cancelLast) {
        if (null != Looper.myLooper()) {
            ktIns.makeText(msg).show(cancelLast);
        } else {
            UIKit.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ktIns.makeText(msg).show(cancelLast);
                }
            });
        }
    }


}
