/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rong.combusis.common.ui.widget.blurImpl;

import android.content.Context;
import android.graphics.Bitmap;


public class EmptyBlurImpl implements BlurImpl {
    @Override
    public boolean prepare(Context context, Bitmap buffer, float radius) {
        return false;
    }

    @Override
    public void release() {

    }

    @Override
    public void blur(Bitmap input, Bitmap output) {

    }
}
