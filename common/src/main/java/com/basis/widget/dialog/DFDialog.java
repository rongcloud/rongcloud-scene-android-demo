package com.basis.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import java.lang.ref.WeakReference;

/**
 * DialogFragment的封装
 */
public class DFDialog extends DialogFragment implements IDialog {
    private WeakReference<FragmentActivity> weakReference;
    private IBuilder builder;
    private DialogInterface.OnDismissListener dismissListener;

    protected DFDialog(Activity activity, IBuilder builder) {
        if (activity instanceof FragmentActivity) {
            weakReference = new WeakReference<>((FragmentActivity) activity);
        }
        this.builder = builder;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = builder.build().getDialog();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (null != dismissListener) dismissListener.onDismiss(dialog);
            }
        });
        builder.refresh();
        return dialog;
    }


    @Override
    public IBuilder getBuilder() {
        return builder;
    }

    @Override
    public IDialog setOnDismissListener(DialogInterface.OnDismissListener dismissListener) {
        this.dismissListener = dismissListener;
        return this;
    }

    @Override
    public void show() {
        if (null != weakReference && null != weakReference.get()) {
            if (null != getDialog() && null != builder) {
                builder.refresh();
            }
            super.show(weakReference.get().getSupportFragmentManager(), this.getClass().getSimpleName());
        }
    }
}
