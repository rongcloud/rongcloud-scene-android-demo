package cn.rongcloud.pk.dialog;

import android.content.Context;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.basis.ui.BaseDialog;

import cn.rongcloud.pk.R;
import cn.rongcloud.pk.bean.PKResponse;

/**
 * @author gyn
 * @date 2022/1/13
 */
public class RequestPKDialog extends BaseDialog {

    private AppCompatTextView tvMessage;
    private AppCompatTextView btnCancel;
    private AppCompatTextView btnConfirm;
    private OnClickAction onClickAction;

    private Timer timer;

    public RequestPKDialog(@NonNull Context context, OnClickAction onClickAction) {
        super(context, R.layout.layout_request_pk_dialog, false);
        this.onClickAction = onClickAction;
        setCancelable(false);
    }

    @Override
    public void initListener() {
        btnCancel.setOnClickListener(v -> {
            dismiss();
            if (onClickAction != null) onClickAction.onAction(PKResponse.reject);
        });
        btnConfirm.setOnClickListener(v -> {
            dismiss();
            if (onClickAction != null) onClickAction.onAction(PKResponse.accept);
        });
    }

    @Override
    public void initView() {
        tvMessage = (AppCompatTextView) findViewById(R.id.tv_message);
        btnCancel = (AppCompatTextView) findViewById(R.id.btn_cancel);
        btnConfirm = (AppCompatTextView) findViewById(R.id.btn_confirm);
    }

    public void show(String message) {
        super.show();
        timer = new Timer(message);
        timer.start();
    }

    @Override
    public void dismiss() {
        if (timer != null) timer.cancel();
        super.dismiss();
    }

    public interface OnClickAction {
        void onAction(PKResponse pkResponse);
    }

    public class Timer extends CountDownTimer {
        private String message;

        public Timer(String message) {
            super(11 * 1000, 1000);
            this.message = message;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (null != tvMessage) {
                tvMessage.setText(message + " (" + (millisUntilFinished / 1000) + "秒)");
            }
            if (millisUntilFinished < 1000) {
                dismiss();
                if (onClickAction != null) onClickAction.onAction(PKResponse.ignore);
            }
        }

        @Override
        public void onFinish() {

        }
    }
}
