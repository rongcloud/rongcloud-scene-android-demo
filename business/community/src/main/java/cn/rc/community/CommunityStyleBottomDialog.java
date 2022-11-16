package cn.rc.community;

import android.app.Activity;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.basis.utils.Logger;
import com.basis.utils.SoftBoardUtil;
import com.basis.utils.UIKit;
import com.basis.widget.dialog.BasisDialog;

/**
 * 社区样式的底部弹框
 * title bar : title + cancel button + sure button
 * content: customer view
 */
public class CommunityStyleBottomDialog implements View.OnClickListener {
    protected BasisDialog dialog;
    private OnTitleClickListener listener;

    public CommunityStyleBottomDialog(Activity activity) {
        this(activity, -1);
    }

    public CommunityStyleBottomDialog(Activity activity, int percentY) {
        dialog = BasisDialog.bottom(activity, R.layout.layout_community_style_bottom_dialog, percentY);
        dialog.setCanceledOnTouchOutside(false);
        initView();
    }

    public CommunityStyleBottomDialog setOnDismissListener(DialogInterface.OnDismissListener listener) {
        if (null != dialog) {
            dialog.observeDismiss(listener);
        }
        return this;
    }

    public void show() {
        if (null != dialog) {
            dialog.show();
        }
        if (null != softInputView) {
            UIKit.postDelayed(new Runnable() {
                @Override
                public void run() {
                    SoftBoardUtil.showKeyboard(softInputView);
                }
            }, 100);
        }
    }

    public void dismiss() {
        if (null != dialog) {
            if (null != softInputView) SoftBoardUtil.hideKeyboard(softInputView);
            dialog.dismiss();
            dialog = null;
        }
    }

    public CommunityStyleBottomDialog setTitleClickListener(OnTitleClickListener listener) {
        this.listener = listener;
        return this;
    }

    public CommunityStyleBottomDialog setTitle(String title) {
        if (null != tvTitle && !TextUtils.isEmpty(title)) tvTitle.setText(title);
        return this;
    }

    public CommunityStyleBottomDialog setCustomerContent(View view) {
        if (null != view && null != customContent) {
            ViewParent vp = view.getParent();
            if (null != vp) {
                ((ViewGroup) vp).removeView(view);
            }
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            if (null == lp) {
                lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            } else {
                lp = new FrameLayout.LayoutParams(lp.width, lp.height);
            }
            customContent.addView(view, lp);
            softInputView = UIKit.findChildFromTreeByTypeClass(customContent, EditText.class, 1, 10);
        }
        return this;
    }

    protected TextView tvTitle, tvCancel, tvSure;
    protected FrameLayout customContent;
    private View softInputView;

    void initView() {
        tvTitle = dialog.getView(R.id.csbd_title);
        tvCancel = dialog.getView(R.id.csbd_cancel);
        tvSure = dialog.getView(R.id.csbd_sure);
        UIKit.setBoldText(tvTitle, true);
        customContent = dialog.getView(R.id.custom_content);
        tvCancel.setOnClickListener(this);
        tvSure.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (R.id.csbd_cancel == id) {
            dismiss();
            if (null != listener) listener.onCancelClick();
        } else if (R.id.csbd_sure == id) {
            if (null != listener) listener.onSureClick(this);
        }
    }

    public interface OnTitleClickListener {
        default void onCancelClick() {
            Logger.d("CommunityStyleBottomDialog", "onCancelClick");
        }

        void onSureClick(CommunityStyleBottomDialog dialog);
    }
}
