package com.basis.utils;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;

public class SelectorBuilder {
    //pressed, focused, normal, unable
    private final static int[] pressed = new int[]{android.R.attr.state_enabled, android.R.attr.state_pressed};
    private final static int[] selected = new int[]{android.R.attr.state_enabled, android.R.attr.state_selected};
    private final static int[] normal = new int[]{-android.R.attr.state_enabled};

    // shape 填充设
    private int color;
    private int selectedColor;
    private int pressedColor;
    // 边框颜色
    private int strokeColor;
    private int selectedStrokeColor;
    private int pressedStrokeColor;
    // 边框宽度
    int strokeWidth;
    // 圆角
    int radius;

    private SelectorBuilder() {
    }

    public static SelectorBuilder get(int strokeWidth, int radius) {
        SelectorBuilder builder = new SelectorBuilder();
        builder.strokeWidth = strokeWidth;
        builder.radius = radius;
        return builder;
    }

    public void setColor(int color, int selectedColor, int pressedColor) {
        this.color = color;
        this.selectedColor = selectedColor;
        this.pressedColor = pressedColor;
    }

    public void setStrokeColor(int color, int selectedColor, int pressedColor) {
        this.strokeColor = color;
        this.selectedStrokeColor = selectedColor;
        this.pressedStrokeColor = pressedColor;
    }

    StateListDrawable create() {
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(normal, shapeItem(color, strokeColor));
        drawable.addState(selected, shapeItem(selectedColor, selectedStrokeColor));
        drawable.addState(pressed, shapeItem(pressedColor, pressedStrokeColor));
        return drawable;
    }


    private Drawable shapeItem(int color, int strokeColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(color);
        drawable.setStroke(strokeWidth, strokeColor);
        drawable.setCornerRadius(radius);
        return drawable;
    }
}
