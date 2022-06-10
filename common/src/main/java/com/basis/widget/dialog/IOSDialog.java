package com.basis.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.basis.R;
import com.basis.utils.ResUtil;
import com.basis.widget.percent.PercentLinearLayout;


/**
 * ios风格弹框
 */
public class IOSDialog implements IDialog {
    private View divider;
    private BasisDialog basisDialog;
    private TextView title;//标题
    private PercentLinearLayout llContent;//文本内容和添加view的父布局
    private TextView message;//内容
    private TextView confirm, cancel;//按钮
    private IBuilder builder;

    protected IOSDialog(Activity activity, IBuilder builder) {
        basisDialog = new BasisDialog(activity, R.style.Basis_Style_WX_Dialog, Gravity.CENTER)
                .setContentView(R.layout.basis_layout_dialog_ios, 74, -1);
        initView();
        builder.callback = new IBuilder.OnRefreshCallback() {
            @Override
            public void onRefresh() {
                handleRefresh();
            }
        };
        this.builder = builder;
    }

    private void initView() {
        confirm = basisDialog.getView(R.id.btn_confirm);
        cancel = basisDialog.getView(R.id.btn_cancel);
        llContent = basisDialog.getView(R.id.ll_content);
        message = basisDialog.getView(R.id.tv_message);
        title = basisDialog.getView(R.id.tv_title);
        divider = basisDialog.getView(R.id.b_divider);
    }

    void handleRefresh() {
        title.setVisibility(builder.enableTitle ? View.VISIBLE : View.GONE);
        if (!TextUtils.isEmpty(builder.title)) {
            title.setText(builder.title);
        }
        // message
        message.setText(builder.message);
        // cancel
        cancel.setVisibility(builder.enableCancel ? View.VISIBLE : View.GONE);
        if (builder.cancelColor > 0) cancel.setTextColor(ResUtil.getColor(builder.cancelColor));
        if (!TextUtils.isEmpty(builder.cancelText)) cancel.setText(builder.cancelText);
        if (builder.cancelBg > 0) cancel.setBackground(ResUtil.getDrawable(builder.cancelBg));
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != builder.cancelClick) builder.cancelClick.onClick(v);
                dismiss();
            }
        });
        //sure
        confirm.setVisibility(builder.enableSure ? View.VISIBLE : View.GONE);
        if (builder.sureColor > 0) confirm.setTextColor(ResUtil.getColor(builder.sureColor));
        if (!TextUtils.isEmpty(builder.sureText)) confirm.setText(builder.sureText);
        if (builder.sureBg > 0) confirm.setBackground(ResUtil.getDrawable(builder.sureBg));
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != builder.sureClick) builder.sureClick.onClick(v);
                if (builder.sureClickDismiss) dismiss();
            }
        });
        divider.setVisibility(builder.enableCancel && builder.enableSure ? View.VISIBLE : View.GONE);
        //out cancel
        basisDialog.setCanceledOnTouchOutside(builder.outsideCanceled);
        // customer
        if (null != builder.customerView) {
            llContent.removeAllViews();
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            llContent.addView(builder.customerView, params);
        }
    }


    @Override
    public IBuilder getBuilder() {
        return builder;
    }

    @Override
    public Dialog getDialog() {
        return basisDialog;
    }

    @Override
    public IOSDialog setOnDismissListener(DialogInterface.OnDismissListener dismissListener) {
        basisDialog.observeDismiss(dismissListener);
        return this;
    }

    @Override
    public void dismiss() {
        if (basisDialog != null) {
            basisDialog.dismiss();
            basisDialog = null;
        }
    }

    @Override
    public void show() {
        if (null != basisDialog && !basisDialog.isShowing()) {
            if (null != builder) builder.refresh();
            basisDialog.show();
        }
    }
}
