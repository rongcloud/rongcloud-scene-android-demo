package cn.rongcloud.roomkit.widget;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.basis.ui.BaseDialog;

import cn.rongcloud.roomkit.R;

/**
 * @author gyn
 * @date 2022/2/15
 */
public class InputPasswordDialog extends BaseDialog {
    private AppCompatTextView tvLabelDialogTitle;
    private VerifyEditText etPassword;
    private View vDivider;
    private AppCompatTextView btnCancel;
    private AppCompatTextView btnConfirm;
    private OnClickListener clickListener;
    private boolean isSettingPassword;


    public InputPasswordDialog(@NonNull Context context, boolean isSettingPassword, OnClickListener onClickListener) {
        super(context, R.layout.layout_input_password_dialog, false);
        this.isSettingPassword = isSettingPassword;
        this.clickListener = onClickListener;
    }

    @Override
    public void initView() {

        tvLabelDialogTitle = (AppCompatTextView) findViewById(R.id.tv_label_dialog_title);
        etPassword = (VerifyEditText) findViewById(R.id.et_password);
        vDivider = (View) findViewById(R.id.v_divider);
        btnCancel = (AppCompatTextView) findViewById(R.id.btn_cancel);
        btnConfirm = (AppCompatTextView) findViewById(R.id.btn_confirm);

        if (isSettingPassword) {
            tvLabelDialogTitle.setText(R.string.please_setting_four_number_password);
        } else {
            tvLabelDialogTitle.setText(R.string.please_input_four_number_password);
        }
    }

    @Override
    public void initListener() {
        btnCancel.setOnClickListener(v -> {
            if (clickListener != null) clickListener.clickCancel();
            dismiss();
        });

        btnConfirm.setOnClickListener(v -> {
            if (clickListener != null) clickListener.clickConfirm(etPassword.getContent());
        });
    }

    public interface OnClickListener {
        void clickCancel();

        void clickConfirm(String password);
    }
}
