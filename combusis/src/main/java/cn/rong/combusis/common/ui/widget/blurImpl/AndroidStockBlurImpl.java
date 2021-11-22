/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rong.combusis.common.ui.widget.blurImpl;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;


@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
public class AndroidStockBlurImpl implements BlurImpl {
    // android:debuggable="true" in AndroidManifest.xml (auto set by build tool)
    static Boolean DEBUG = null;
    private RenderScript mRenderScript;
    private ScriptIntrinsicBlur mBlurScript;
    private Allocation mBlurInput, mBlurOutput;

    static boolean isDebug(Context ctx) {
        if (DEBUG == null && ctx != null) {
            DEBUG = (ctx.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        }
        return DEBUG == Boolean.TRUE;
    }

    @Override
    public boolean prepare(Context context, Bitmap buffer, float radius) {
        if (mRenderScript == null) {
            try {
                mRenderScript = RenderScript.create(context);
                mBlurScript = ScriptIntrinsicBlur.create(mRenderScript, Element.U8_4(mRenderScript));
            } catch (android.renderscript.RSRuntimeException e) {
                if (isDebug(context)) {
                    throw e;
                } else {
                    // In release mode, just ignore
                    release();
                    return false;
                }
            }
        }
        if (null != mBlurScript) mBlurScript.setRadius(radius);
        mBlurInput = Allocation.createFromBitmap(mRenderScript, buffer,
                Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
        mBlurOutput = Allocation.createTyped(mRenderScript, mBlurInput.getType());

        return true;
    }

    @Override
    public void release() {
        if (mBlurInput != null) {
            mBlurInput.destroy();
            mBlurInput = null;
        }
        if (mBlurOutput != null) {
            mBlurOutput.destroy();
            mBlurOutput = null;
        }
        if (mBlurScript != null) {
            mBlurScript.destroy();
            mBlurScript = null;
        }
        if (mRenderScript != null) {
            mRenderScript.destroy();
            mRenderScript = null;
        }
    }

    @Override
    public void blur(Bitmap input, Bitmap output) {
        if (null != mBlurInput && null != mBlurScript && null != mBlurOutput) {
            mBlurInput.copyFrom(input);
            mBlurScript.setInput(mBlurInput);
            mBlurScript.forEach(mBlurOutput);
            mBlurOutput.copyTo(output);
        }
    }
}
