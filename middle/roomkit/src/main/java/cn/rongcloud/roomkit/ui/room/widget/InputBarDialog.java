package cn.rongcloud.roomkit.ui.room.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import cn.rongcloud.roomkit.R;


/**
 * 底部编辑弹框
 */
public class InputBarDialog extends Dialog {
    InputBar.InputBarListener inputBarListener;
    InputBar inputBar;

    public InputBarDialog(Context context, InputBar.InputBarListener inputBarListener) {
        super(context, R.style.InputBar_Dialog_Style);
        this.inputBarListener = inputBarListener;
        inputBar = new InputBar(context);
        inputBar.setInputBarListener(new InputBar.InputBarListener() {
            @Override
            public void onClickSend(String message) {
                dismiss();
                if (inputBarListener != null) {
                    inputBarListener.onClickSend(message);
                }
            }

            @Override
            public boolean onClickEmoji() {
                if (inputBarListener != null) {
                    return inputBarListener.onClickEmoji();
                }
                return false;
            }
        });
        setContentView(inputBar);
        Window window = getWindow();
        if (window != null) {
            //获取对话框当前的参数值
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.BOTTOM;
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(params);
        }
    }

    @Override
    public void dismiss() {
        inputBar.hideInputBar();
        super.dismiss();
    }

    @Override
    public void show() {
        inputBar.showInputBar();
        super.show();
    }
}