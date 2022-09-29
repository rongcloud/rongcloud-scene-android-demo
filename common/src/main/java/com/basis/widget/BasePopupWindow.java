/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package com.basis.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

public class BasePopupWindow extends PopupWindow {

    protected void initView(@NonNull View content) {

    }

    public BasePopupWindow(Context context, @LayoutRes int layoutId, int width, int height, boolean b) {
        this(LayoutInflater.from(context).inflate(layoutId, null), width, height, false);
    }

    public BasePopupWindow(View contentView, int width, int height, boolean b) {
        super(contentView, width, height, b);
        initView(contentView);
    }


    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
    }

    public void showAsDropDownFill(View anchor) {
        if (Build.VERSION.SDK_INT >= 24) {
            Rect rect = new Rect();
            anchor.getGlobalVisibleRect(rect);
            int heightPixels = anchor.getResources().getDisplayMetrics().heightPixels;
            int h = heightPixels - rect.bottom;
            setHeight(h);
        }
        super.showAsDropDown(anchor);
    }

    public @NonNull
    int[] getLocation(View view) {
        int[] location = new int[2];
        if (Build.VERSION.SDK_INT >= 24) {
            Rect rect = new Rect();
            view.getGlobalVisibleRect(rect);
            location[0] = rect.left;
            location[1] = rect.top;
        } else {
            view.getLocationOnScreen(location);
        }
        Log.e("getLocation", "x = " + location[0]);
        Log.e("getLocation", "y = " + location[1]);
        return location;
    }

    /**
     * @param anchor     锚定视图
     * @param intervalPx 间距 px
     */
    public void showAsDropUp(View anchor, int intervalPx) {
        int[] location = getLocation(anchor);
        super.showAtLocation(anchor, Gravity.NO_GRAVITY, location[0], location[1] - getHeight() - intervalPx);
    }

    public void showAsDropLeft(View anchor, int intervalPx) {
        int[] location = getLocation(anchor);
        super.showAtLocation(anchor, Gravity.NO_GRAVITY, location[0] - getWidth() - intervalPx, location[1]);
    }

    public void showAsDropRight(View anchor, int intervalPx) {
        int[] location = getLocation(anchor);
        super.showAtLocation(anchor, Gravity.NO_GRAVITY, location[0] + getWidth() + intervalPx, location[1]);
    }
}