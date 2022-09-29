package cn.rongcloud.roomkit.widget;

import android.content.Context;
import android.text.InputFilter;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.basis.ui.BaseDialog;
import com.basis.widget.ChineseLengthFilter;

import cn.rongcloud.roomkit.R;

/**
 * @author gyn
 * @date 2022/2/14
 */
public class EditDialog extends BaseDialog {

    private String title;
    private String hint;
    private String defaultText;
    private int maxLength;
    private boolean isNumber;
    private OnClickEditDialog onClickEditDialog;
    private ConstraintLayout clRootView;
    private AppCompatTextView tvTitle;
    private AppCompatEditText etContent;
    private View vDivider;
    private LinearLayout llButtons;
    private AppCompatTextView btnCancel;
    private AppCompatTextView btnConfirm;

    public EditDialog(@NonNull Context context, String title, String hint, String defaultText, int maxLength, boolean isNumber, OnClickEditDialog onClickEditDialog) {
        super(context, R.layout.layout_edit_dialog, false);
        this.title = title;
        this.hint = hint;
        this.defaultText = defaultText;
        this.maxLength = maxLength;
        this.isNumber = isNumber;
        this.onClickEditDialog = onClickEditDialog;
    }

    @Override
    public void initView() {

        clRootView = (ConstraintLayout) findViewById(R.id.cl_root_view);
        tvTitle = (AppCompatTextView) findViewById(R.id.tv_title);
        etContent = (AppCompatEditText) findViewById(R.id.et_content);
        vDivider = (View) findViewById(R.id.v_divider);
        llButtons = (LinearLayout) findViewById(R.id.ll_buttons);
        btnCancel = (AppCompatTextView) findViewById(R.id.btn_cancel);
        btnConfirm = (AppCompatTextView) findViewById(R.id.btn_confirm);

        tvTitle.setText(title);
        etContent.setHint(hint);
        etContent.setText(defaultText);
        if (defaultText != null) {
            etContent.setSelection(defaultText.length());
        }
        etContent.setFilters(new InputFilter[]{new ChineseLengthFilter(maxLength * 2)});
        if (isNumber) {
            etContent.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        }
    }


    @Override
    public void initListener() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (onClickEditDialog != null) onClickEditDialog.clickCancel();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickEditDialog != null) {
                    String text = "";
                    if (etContent.getText() != null) {
                        text = etContent.getText().toString().trim();
                    }
                    onClickEditDialog.clickConfirm(text);
                }
            }
        });
    }

    @Override
    public void show() {
        super.show();

    }

    public interface OnClickEditDialog {
        void clickCancel();

        void clickConfirm(String text);
    }
}
