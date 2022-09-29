package com.basis.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.LayoutRes;

import com.basis.R;
import com.basis.utils.Logger;
import com.basis.utils.ScreenUtil;


public class BottomDialog {
    protected final String TAG = this.getClass().getSimpleName();
    protected Activity mActivity;
    private Dialog mDialog;
    private View contentView;
    private DialogInterface.OnDismissListener onDismissListener;

    public BottomDialog(Activity activity) {
        mActivity = activity;
        mDialog = new Dialog(mActivity, R.style.Basis_Style_Bottom_Menu);
    }

    public BottomDialog(Activity activity, @LayoutRes int res) {
        this(activity);
        setContentView(res, -1);
    }

    /**
     * 高度占比屏幕高度 -1 ：不设置
     *
     * @param res     布局文件
     * @param percent 百分比
     */
    public BottomDialog setContentView(@LayoutRes int res, int percent) {
        int height = ScreenUtil.getScreenPoint().y * percent / 100;
        return setContentView(res, 0, height);
    }

    /**
     * @param res    布局文件
     * @param width  宽度，<=0全屏宽
     * @param height 高度
     * @return dialog
     */
    public BottomDialog setContentView(@LayoutRes int res, int width, int height) {
        contentView = mActivity.getLayoutInflater().inflate(res, null);
        mDialog.setContentView(contentView, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        Window window = mDialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        // 设置窗口大小和位置
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = ScreenUtil.getScreenPoint().y;
        if (width > 0) {
            wl.width = width;
        } else {
            wl.width = ScreenUtil.getScreenPoint().x;
        }
        if (height > 0) {
            wl.height = height;
        }
        Logger.e("y = " + wl.y + " height = " + wl.height);
        mDialog.onWindowAttributesChanged(wl);
        // 点击窗口以外区域，关闭窗�?
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (null != onDismissListener) onDismissListener.onDismiss(dialog);
            }
        });
        mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (null != onDismissListener) onDismissListener.onDismiss(dialog);
                }
                return false;
            }
        });
        return this;
    }

    public View getContentView() {
        return contentView;
    }

    public void show() {
        if (mDialog != null) {
            mDialog.show();
        }
    }

    public void dismiss() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    public Dialog getDialog() {
        return mDialog;
    }

    public <T extends BottomDialog> T setOnCancelListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
        return (T) this;
    }

    public interface OnItemClick {
        void onClick(int position);
    }
}
