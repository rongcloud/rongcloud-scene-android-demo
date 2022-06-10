package cn.rongcloud.pk.dialog;

import android.app.Activity;
import android.view.View;

import com.basis.utils.UIKit;
import com.basis.wapper.IResultBack;
import com.basis.widget.dialog.BottomDialog;

import cn.rongcloud.pk.R;

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
            dismiss();
            if (null != resultBack) resultBack.onResult(true);
        } else if (R.id.cancel_dialog == id) {
            dismiss();
            if (null != resultBack) resultBack.onResult(false);
        }
    }
}
