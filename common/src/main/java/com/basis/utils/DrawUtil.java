package com.basis.utils;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.widget.TextView;

public class DrawUtil {

    public static <T extends TextView> void setShapeBackground(T t, int color, int strokeColor, int strokeWidth) {
        setShapeBackground(t, color, strokeColor, strokeWidth, 4);
    }

    public static <T extends TextView> void setShapeBackground(T t, int color, int strokeColor, int strokeWidth, int radius) {
        if (null == t) return;
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(color);
        drawable.setStroke(strokeWidth, strokeColor);
        drawable.setCornerRadius(radius);
        t.setBackground(drawable);
    }

    /**
     * @param color       填充色
     * @param strokeColor 边框颜色
     * @param strokeWidth 边框宽度
     * @param radius      圆角
     * @return Drawable
     */
    public static Drawable itemShapeDrawable(int color, int strokeColor, int strokeWidth, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(color);
        drawable.setStroke(strokeWidth, strokeColor);
        drawable.setCornerRadius(radius);
        return drawable;
    }
}
