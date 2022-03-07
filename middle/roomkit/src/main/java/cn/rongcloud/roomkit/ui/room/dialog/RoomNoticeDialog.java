package cn.rongcloud.roomkit.ui.room.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.utils.KToast;
import com.basis.utils.ScreenUtil;
import com.basis.widget.ChineseLengthFilter;

import cn.rongcloud.roomkit.R;
import cn.rongcloud.roomkit.api.VRApi;

/**
 * @author gyn
 * @date 2021/9/26
 */
public class RoomNoticeDialog extends Dialog {
    private View contentView;
    private EditText mNoticeView;
    private TextView mTitleView;
    private Button mCancelView;
    private Button mConfirmView;

    public RoomNoticeDialog(@NonNull Context context) {
        super(context, R.style.CustomDialog);
        contentView = LayoutInflater.from(context).inflate(R.layout.dialog_room_notice, null, false);
        setContentView(contentView);
        mTitleView = contentView.findViewById(R.id.tv_title);
        mNoticeView = contentView.findViewById(R.id.et_notice);
        mCancelView = contentView.findViewById(R.id.btn_cancel);
        mConfirmView = contentView.findViewById(R.id.btn_confirm);
        WindowManager.LayoutParams wl = getWindow().getAttributes();
        wl.height = ScreenUtil.getScreenHeight() / 2;
        onWindowAttributesChanged(wl);
    }

    /**
     * 公告信息与展示分离
     *
     * @param notice
     */
    public void setNotice(String notice) {
        mNoticeView.setText(notice);
    }

    public void show(String notice, boolean isEdit, OnSaveNoticeListener confirmListener) {
        if (!TextUtils.isEmpty(notice)) {
            mNoticeView.setText(notice);
        }
        if (isEdit) {
            mTitleView.setText("修改房间公告");
            mNoticeView.setFocusableInTouchMode(true);
            mNoticeView.setCursorVisible(true);
            mNoticeView.requestFocus();
            if (!TextUtils.isEmpty(notice)) {
                mNoticeView.setSelection(notice.length());
            }
            mNoticeView.setFilters(new InputFilter[]{new ChineseLengthFilter(200)});
            mCancelView.setVisibility(View.VISIBLE);
            mConfirmView.setVisibility(View.VISIBLE);
            mConfirmView.setOnClickListener(v -> {
                Editable e = mNoticeView.getText();
                if (confirmListener != null && e != null) {
                    audit(e.toString().trim(), confirmListener);
                }
                dismiss();
            });
            mCancelView.setOnClickListener(v -> dismiss());
        } else {
            mTitleView.setText("房间公告");
            mNoticeView.setFocusableInTouchMode(false);
            mNoticeView.setCursorVisible(false);
            mCancelView.setVisibility(View.GONE);
            mConfirmView.setVisibility(View.GONE);
        }
        show();
    }

    void audit(String notice, OnSaveNoticeListener confirmListener) {
        OkApi.post(VRApi.AUDIT + notice, null, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    if (confirmListener != null) {
                        confirmListener.saveNotice(notice);
                    }
                } else {
                    String message = result.getMessage();
                    KToast.show(!TextUtils.isEmpty(message) ? message : "修改失败");
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                KToast.show(!TextUtils.isEmpty(msg) ? msg : "修改失败");
            }
        });
    }

    public interface OnSaveNoticeListener {
        void saveNotice(String notice);
    }
}
