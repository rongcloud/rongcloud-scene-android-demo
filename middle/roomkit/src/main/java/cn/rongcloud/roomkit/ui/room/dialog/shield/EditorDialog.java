package cn.rongcloud.roomkit.ui.room.dialog.shield;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.basis.utils.UIKit;
import com.basis.wapper.IResultBack;

import cn.rongcloud.roomkit.R;


/**
 * 底部编辑弹框
 */
public class EditorDialog extends Dialog {
    IResultBack<String> resultBack;
    private InputMethodManager inputMethodManager;
    private EditText editText;
    private View complete;

    public EditorDialog(Activity activity, IResultBack<String> resultBack) {
        super(activity, R.style.Basis_Style_Bottom_Menu);
        this.resultBack = resultBack;
        View v = LayoutInflater.from(activity).inflate(R.layout.dialog_editor, null);
        setContentView(v);
        Window window = getWindow();
        if (window != null) {
            //获取对话框当前的参数值
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.BOTTOM;
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(params);
        }
        initView(v);
    }

    @Override
    public void dismiss() {
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        super.dismiss();
    }

    private void initView(View v) {
        editText = UIKit.getView(v, R.id.et_editor);
        complete = UIKit.getView(v, R.id.complete);
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = editText.getText().toString().trim();
                dismiss();
                editText.setText("");
                if (null != resultBack) resultBack.onResult(text);
            }
        });
        editText.requestFocus();
        editText.setFocusable(true);
        inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public void show() {
        editText.postDelayed(new Runnable() {
            @Override
            public void run() {
                inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
            }
        }, 300);
        super.show();
    }
}