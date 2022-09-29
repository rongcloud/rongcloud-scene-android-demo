package io.rong.dial.dialpad;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class DialpadSlidingRelativeLayout extends RelativeLayout {

    public DialpadSlidingRelativeLayout(Context context) {
        super(context);
    }

    public DialpadSlidingRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DialpadSlidingRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public float getYFraction() {
        final int height = getHeight();
        if (height == 0) return 0;
        return getTranslationY() / height;
    }

    public void setYFraction(float yFraction) {
        setTranslationY(yFraction * getHeight());
    }
}