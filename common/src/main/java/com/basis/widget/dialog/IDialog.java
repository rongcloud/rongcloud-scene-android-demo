package com.basis.widget.dialog;

import android.app.Dialog;
import android.content.DialogInterface;

public interface IDialog {

    /**
     * 设置消失监听
     *
     * @param dismissListener 消失监听
     */
    IDialog setOnDismissListener(DialogInterface.OnDismissListener dismissListener);

    void dismiss();


    void show();

    Dialog getDialog();

    IBuilder getBuilder();

}
