package com.basis.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import com.basis.R;

/**
 * @author gyn
 * @date 2022/2/14
 */
public abstract class BaseDialog extends Dialog {
    protected View rootView;
    private int layoutId;
    private boolean touchOutSideDismiss;

    public BaseDialog(@NonNull Context context, @LayoutRes int layoutId, boolean touchOutSideDismiss) {
        super(context, R.style.CustomDialog);
        this.layoutId = layoutId;
        this.touchOutSideDismiss = touchOutSideDismiss;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(touchOutSideDismiss);
        rootView = LayoutInflater.from(getContext()).inflate(layoutId, null, false);
        setContentView(rootView);
        initView();
        initListener();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Window window = getWindow();
        if (hasFocus && window != null) {
            View decorView = window.getDecorView();
            if (decorView.getHeight() == 0 || decorView.getWidth() == 0) {
                decorView.requestLayout();
                Log.d("TAG", "布局异常，重新布局");
            }
        }
    }


    public abstract void initView();

    public abstract void initListener();

}
