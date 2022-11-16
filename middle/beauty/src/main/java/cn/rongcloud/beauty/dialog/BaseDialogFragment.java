package cn.rongcloud.beauty.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public abstract class BaseDialogFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            Bundle savedInstanceState) {
        View dialogView = createDialogView(inflater, container);
        getDialog().setCanceledOnTouchOutside(setCancelOnTouchOutside()); // 点击屏幕不消失
        if (!setCancelOnBackClicked()) {
            getDialog()
                    .setOnKeyListener(
                            new DialogInterface.OnKeyListener() { // 点击返回键不消失
                                @Override
                                public boolean onKey(
                                        DialogInterface dialog, int keyCode, KeyEvent event) {
                                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                                        return true;
                                    }
                                    return false;
                                }
                            });
        }

        initWindowParams();
        return dialogView;
    }

    protected boolean setCancelOnTouchOutside() {
        return true;
    }

    protected boolean setCancelOnBackClicked() {
        return true;
    }

    /**
     * 创建 dialog view
     *
     * @param inflater
     * @param container
     * @return
     */
    protected abstract View createDialogView(
            LayoutInflater inflater, @Nullable ViewGroup container);

    protected int getDialogWidth() {
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    protected int getDialogHeight() {
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    private void initWindowParams() {
        Dialog dialog = getDialog();
        if (dialog != null) {

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Window window = dialog.getWindow();
            if (window != null) {
                window.getDecorView().setPadding(0, 0, 0, 0);
                window.setBackgroundDrawableResource(android.R.color.transparent);
                window.setDimAmount(0);
                WindowManager.LayoutParams windowAttributes = window.getAttributes();
                windowAttributes.gravity = setContentGravity();
                windowAttributes.width = getDialogWidth();
                windowAttributes.height = getDialogHeight();
                window.setAttributes(windowAttributes);
                int animation = setWindowAnimations();
                if (animation != 0) {
                    window.setWindowAnimations(animation);
                }
            }
        }
    }

    protected int setContentGravity() {
        return Gravity.CENTER;
    }

    protected int setWindowAnimations() {
        return 0;
    }

    /**
     * 获取屏幕高度,不包括状态栏
     */
    public int getScreenHeight() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 获取屏幕宽度
     */
    public int getScreenWidth() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getRealMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    public interface OnClickListener {
        /**
         * 确认
         */
        void onConfirm();

        /**
         * 取消
         */
        void onCancel();
    }

    public interface OnDismissListener {
        /**
         * 消失
         */
        void onDismiss();
    }
}
