package cn.rongcloud.voice.pk;

import android.app.Activity;
import android.view.View;

import com.basis.widget.BottomDialog;
import com.kit.UIKit;
import com.kit.wapper.IResultBack;

import cn.rong.combusis.sdk.VoiceRoomApi;
import cn.rongcloud.voice.R;

/**
 * 取消PK邀请弹框
 */
public class CancelPKDialog extends BottomDialog implements View.OnClickListener {
    private IResultBack<Boolean> resultBack;

    public CancelPKDialog(Activity activity, IResultBack<Boolean> resultBack) {
        super(activity);
        this.resultBack = resultBack;
        setContentView(R.layout.layout_cancelpk_dialog, 25);
        initView();
    }

    private void initView() {
        UIKit.getView(getContentView(), R.id.cancele_pk).setOnClickListener(this);
        UIKit.getView(getContentView(), R.id.cancel_dialog).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (R.id.cancele_pk == id) {
            VoiceRoomApi.getApi().cancelPKInvitation(resultBack);
            dismiss();
        } else if (R.id.cancel_dialog == id) {
            dismiss();
            if (null != resultBack) resultBack.onResult(false);
        }
    }
}
