package cn.rongcloud.profile.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.basis.ui.BaseDialog;

import cn.rongcloud.profile.R;


public class UnregisterDialog extends BaseDialog {

    private View.OnClickListener listener;

    public UnregisterDialog(@NonNull Context context, View.OnClickListener listener) {
        super(context, R.layout.dialog_unregister, true);
        this.listener = listener;
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initView() {
        findViewById(R.id.tv_unregister).setOnClickListener(listener);
        findViewById(R.id.iv_close).setOnClickListener(v -> {
            dismiss();
        });
    }
}
