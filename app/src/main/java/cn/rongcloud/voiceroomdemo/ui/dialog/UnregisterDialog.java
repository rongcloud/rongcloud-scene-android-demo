package cn.rongcloud.voiceroomdemo.ui.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import cn.rong.combusis.common.ui.dialog.BaseDialog;
import cn.rongcloud.voiceroomdemo.R;

/**
 * Created by hugo on 2021/11/3
 */
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
