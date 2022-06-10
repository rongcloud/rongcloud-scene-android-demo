package com.basis.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.basis.R;
import com.basis.utils.UIKit;

import java.lang.ref.WeakReference;


/**
 * 中心弹出的提示框
 */
public class VRCenterDialog extends Dialog {
    private WeakReference<Activity> reference;
    private FrameLayout container;
    private View divider;
    private TextView title, cancel, confirm;


    public boolean enable() {
        return null != reference.get();
    }

    public void showToast(String message) {
        if (null != reference.get()) {
            Toast.makeText(reference.get(), message, Toast.LENGTH_LONG).show();
        }
    }

    public boolean startActivity(Intent intent) {
        if (null != reference.get()) {
            reference.get().startActivity(intent);
            return true;
        }
        return false;
    }

    public VRCenterDialog(Activity activity, OnDismissListener onDismissListener) {
        super(activity, R.style.CustomDialog);
        setOnDismissListener(onDismissListener);
        reference = new WeakReference<>(activity);
        View view = getLayoutInflater().inflate(R.layout.layout_vrcenter_dialog, null, false);
        Point scPoint = new Point();
        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getSize(scPoint);
        setCanceledOnTouchOutside(false);
        addContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        // 设置窗口大小和位置
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = 0;
        wl.width = (int) (scPoint.x * 0.74);
        onWindowAttributesChanged(wl);
        init(view);
    }

    private void init(View view) {
        container = view.findViewById(R.id.container);
        title = view.findViewById(R.id.title);
        divider = view.findViewById(R.id.b_divider);
        cancel = view.findViewById(R.id.btn_cancel);
        confirm = view.findViewById(R.id.btn_confirm);
        divider.setVisibility(View.GONE);
        cancel.setVisibility(View.GONE);
        confirm.setVisibility(View.GONE);
    }

    public void replaceContent(@NonNull String title,
                               @Nullable String cancel, @Nullable View.OnClickListener cancelClick,
                               @Nullable String confirm, @Nullable View.OnClickListener confirmClick,
                               @NonNull @LayoutRes int layoutId) {
        View v = getLayoutInflater().inflate(layoutId, null);
        replaceContent(title, cancel, cancelClick, confirm, confirmClick, v);
    }

    public void replaceContent(String title, String cancel, View.OnClickListener cancelClick, String confirm, View.OnClickListener confirmClick, View view) {
        replaceContent(title, -1, cancel, -1, cancelClick, confirm, -1, confirmClick, view);
    }


    public void replaceContent(String title, int titleColor, String cancel, int cancelColor, View.OnClickListener cancelClick, String confirm, int confirmColor, View.OnClickListener confirmClick, View view) {
        if (titleColor > -1) this.title.setTextColor(UIKit.getResources().getColor(titleColor));
        if (cancelColor > -1) this.cancel.setTextColor(UIKit.getResources().getColor(cancelColor));
        if (confirmColor > -1)
            this.confirm.setTextColor(UIKit.getResources().getColor(confirmColor));
        this.title.setText(title);
        int btnCount = 0;
        divider.setVisibility(View.GONE);
        this.cancel.setVisibility(View.GONE);
        this.confirm.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(cancel)) {
            this.cancel.setText(cancel);
            this.cancel.setVisibility(View.VISIBLE);
            this.cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cancelClick != null) cancelClick.onClick(v);
                    dismiss();
                }
            });
            btnCount++;
        }
        if (!TextUtils.isEmpty(confirm)) {
            this.confirm.setText(confirm);
            this.confirm.setVisibility(View.VISIBLE);
            this.confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (confirmClick != null) confirmClick.onClick(v);
                    dismiss();
                }
            });
            btnCount++;
        }
        if (btnCount == 2) {
            divider.setVisibility(View.VISIBLE);
        }
        if (null != container) {
            if (null != view) {
                container.setVisibility(View.VISIBLE);
                container.removeAllViews();
                ViewParent p = view.getParent();
                if (null != p) ((ViewGroup) p).removeView(view);
                container.addView(view, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
            }else {
                container.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 更新标题
     *
     * @param newTitle
     */
    public void updateTitle(String newTitle) {
        title.setText(newTitle);
    }
}
