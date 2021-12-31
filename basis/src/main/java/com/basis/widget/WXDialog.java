package com.basis.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.basis.R;
import com.basis.percent.PercentLinearLayout;
import com.basis.widget.interfaces.IDialog;
import com.kit.utils.ResUtil;
import com.kit.utils.ScreenUtil;

/**
 * @author: BaiCQ
 * @createTime: 2016/12/22 18:04
 * @className: WXDialog
 * @Description: 最新标准微信风格弹框
 */
public class WXDialog implements IDialog<WXDialog> {
    private Activity context;
    private View contentView;
    private Dialog dialog;
    private TextView title;//标题
    private PercentLinearLayout llContent;//文本内容和添加view的父布局
    private TextView message;//内容
    private TextView confirm, cancel;//按钮
    private DialogInterface.OnDismissListener dismissListener;

    /**
     * @param activity Activity
     * @param message  提示消息 cancel：知道了
     */
    public static void showCancelDialog(Activity activity, String message) {
        new WXDialog(activity)
                .setMessage(message)
                .cancelStyle(true)
                .show();
    }

    public WXDialog() {
    }

    /**
     * 默认布局构建
     *
     * @param activity
     */
    public WXDialog(final Activity activity) {
        this.context = activity;
        View contentView = LayoutInflater.from(context).inflate(R.layout.basis_layout_wx, null);
        wxstyle(contentView);
        initView();
    }

    public WXDialog setCustomBuilder(Activity activity, CustomBuilder builder) {
        this.context = activity;
        View content = null == builder ? null : builder.onBuild();
        wxstyle(content);
        return this;
    }

    /**
     * 构建WXstyle 风格的dialog
     *
     * @param content
     */
    protected void wxstyle(View content) {
        if (null == content) {
            throw new IllegalArgumentException("contentView can not null !");
        }
        contentView = content;
        dialog = new Dialog(context, R.style.Basis_Style_WX_Dialog);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        dialog.addContentView(contentView, params);
        dialog.setCanceledOnTouchOutside(false);
        dialog.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
        dialog.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
        Window window = dialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        // 设置窗口大小和位置
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = 0;
        wl.width = (int) (ScreenUtil.getScreemWidth() * 0.74);
        dialog.onWindowAttributesChanged(wl);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (null != dismissListener) dismissListener.onDismiss(dialog);
            }
        });
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (KeyEvent.KEYCODE_BACK == keyCode) {
                    if (null != dismissListener) dismissListener.onDismiss(dialog);
                }
                //back 只是回调 不做额外处理
                return false;
            }
        });
    }

    private void initView() {
        if (null == contentView) return;
        confirm = contentView.findViewById(R.id.btn_confirm);
        cancel = contentView.findViewById(R.id.btn_cancel);
        llContent = (PercentLinearLayout) contentView.findViewById(R.id.ll_content);
        message = (TextView) contentView.findViewById(R.id.tv_message);
        title = (TextView) contentView.findViewById(R.id.tv_title);
        title.setVisibility(View.GONE);
        confirm.setVisibility(View.GONE);
        cancel.setVisibility(View.GONE);
    }

    @Override
    public WXDialog setOnDismissListener(DialogInterface.OnDismissListener dismissListener) {
        this.dismissListener = dismissListener;
        return this;
    }

    @Override
    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    @Override
    public void show() {
        if (null != dialog && !dialog.isShowing()) dialog.show();
    }

    public static void showSureDialog(Activity activity, String message, View.OnClickListener sureClick) {
        new WXDialog(activity)
                .setMessage(message)
                .sureStyle(true, sureClick)
                .show();
    }

    @Override
    public WXDialog setOutsideCanceled(boolean outsideCancele) {
        if (null != dialog) dialog.setCanceledOnTouchOutside(outsideCancele);
        return this;
    }

    @SuppressLint("ResourceType")
    @Override
    public WXDialog setTitle(@StringRes int titleRes) {
        if (titleRes > 0) {
            this.title.setVisibility(View.VISIBLE);
            this.title.setText(titleRes);
        }
        return this;
    }

    @Override
    public WXDialog setMessage(@StringRes int msgId) {
        return setMessage(ResUtil.getString(msgId));
    }

    @Override
    public WXDialog setMessage(CharSequence message) {
        if (!TextUtils.isEmpty(message)) this.message.setText(message);
        return this;
    }

    @Override
    public WXDialog setSureButton(@StringRes int text, // 按钮文本
                                  View.OnClickListener onclick) {
        return setSureButton(text, -1, -1, true, onclick);
    }

    @Override
    public WXDialog setSureButton(@StringRes int text, // 按钮文本
                                  @ColorRes int color, // 文本颜色
                                  @DrawableRes int bg, // 按钮背景
                                  View.OnClickListener onclick) {
        return setSureButton(text, color, bg, true, onclick);
    }

    @SuppressLint("ResourceType")
    @Override
    public WXDialog setSureButton(@StringRes int text, // 按钮文本
                                  @ColorRes int color, // 文本颜色
                                  @DrawableRes int bg, // 按钮背景
                                  boolean clickDismiss, // 点击是否消失 处理一些特殊需求 点击确定按钮后弹框不消失
                                  final View.OnClickListener onclick) {
        confirm.setVisibility(View.VISIBLE);
        if (color > 0) confirm.setTextColor(ResUtil.getColor(color));
        if (text > 0) confirm.setText(text);
        if (bg > 0) confirm.setBackground(ResUtil.getDrawable(bg));
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onclick) onclick.onClick(v);
                if (clickDismiss) dismiss();
            }
        });
        return this;
    }

    @Override
    public WXDialog setCancelButton(@StringRes int text) {
        return setCancelButton(text, -1, -1);
    }

    @SuppressLint("ResourceType")
    @Override
    public WXDialog setCancelButton(@StringRes int text, // 按钮文本
                                    @ColorRes int color, // 文本颜色
                                    @DrawableRes int bg) {// 按钮背景
        cancel.setVisibility(View.VISIBLE);
        if (color > 0) cancel.setTextColor(ResUtil.getColor(color));
        if (text > 0) cancel.setText(text);
        if (bg > 0) cancel.setBackground(ResUtil.getDrawable(bg));
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return this;
    }

    @Override
    public WXDialog setCancelButton(@StringRes int text, View.OnClickListener onclick) {
        if (text > 0) cancel.setText(text);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return this;
    }

    @Override
    public WXDialog addCustomContentView(View customContentView) {
        if (null != llContent && null != customContentView) {
            llContent.removeAllViews();
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            llContent.addView(customContentView, params);
        }
        return this;
    }

    @Override
    public WXDialog hideCancelButton() {
        cancel.setVisibility(View.GONE);
        return this;
    }

    @Override
    public WXDialog hideSureButton() {
        confirm.setVisibility(View.GONE);
        return this;
    }

    @Override
    public WXDialog defalutStyle(boolean title, View.OnClickListener sureClick) {
        return title ? setTitle(R.string.basis_tip)
                .setCancelButton(R.string.basis_cancle)
                .setSureButton(R.string.basis_ok, sureClick)
                : setCancelButton(R.string.basis_cancle)
                .setSureButton(R.string.basis_ok, sureClick);
    }

    @Override
    public WXDialog cancelStyle(boolean title) {
        return title ? setTitle(R.string.basis_tip)
                .setCancelButton(R.string.basis_cancle)
                : setCancelButton(R.string.basis_cancle);
    }

    @Override
    public WXDialog sureStyle(boolean title, View.OnClickListener sureClick) {
        return title ? setTitle(R.string.basis_tip)
                .setSureButton(R.string.basis_ok, sureClick)
                : setSureButton(R.string.basis_ok, sureClick);
    }

    @Override
    public WXDialog deleteStyle(boolean title, View.OnClickListener sureClick) {
        return title ? setTitle(R.string.basis_tip)
                .setCancelButton(R.string.basis_cancle)
                .setSureButton(R.string.basis_delete, android.R.color.white, R.drawable.selector_red_solid, sureClick)
                : setCancelButton(R.string.basis_cancle)
                .setSureButton(R.string.basis_delete, android.R.color.white, R.drawable.selector_red_solid, sureClick);
    }

    /********************* 封装的工具类 **************************/

    public static void showDeleteDialog(Activity activity, String message, View.OnClickListener sureClick) {
        new WXDialog(activity)
                .setMessage(message)
                .deleteStyle(false, sureClick)
                .show();
    }

    public static void showDefaultDialog(Activity activity, String message, View.OnClickListener sureClick) {
        new WXDialog(activity)
                .setMessage(message)
                .defalutStyle(true, sureClick)
                .show();
    }

    public static void showCustomDialog(Activity activity, View custom, View.OnClickListener sureClick) {
        new WXDialog(activity)
                .defalutStyle(true, sureClick)
                .addCustomContentView(custom)
                .show();
    }

    @Override
    public boolean isShowing() {
        return null != dialog && dialog.isShowing();
    }

    public interface CustomBuilder {
        View onBuild();
    }
}
