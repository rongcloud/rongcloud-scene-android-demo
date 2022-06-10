package com.meihu.beauty.fragment;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.basis.utils.Logger;
import com.basis.widget.dialog.BottomDialog;
import com.meihu.beauty.R;
import com.meihu.beauty.interfaces.IBeautyHideTipCallBack;
import com.meihu.beauty.interfaces.OnTieZhiActionClickListener;
import com.meihu.beauty.interfaces.OnTieZhiActionListener;
import com.meihu.beauty.interfaces.OnTieZhiClickListener;
import com.meihu.beauty.utils.MhDataManager;
import com.meihu.beauty.utils.WordUtil;
import com.meihu.beauty.views.AbsMhChildViewHolder;
import com.meihu.beauty.views.MhHaHaViewHolder;
import com.meihu.beauty.views.MhMakeupViewHolder;
import com.meihu.beauty.views.MhMeiYanViewHolder;
import com.meihu.beauty.views.MhTeXiaoViewHolder;
import com.meihu.beauty.views.MhTieZhiViewHolder;
import com.meihu.beautylibrary.bean.MHConfigConstants;
import com.meihu.beautylibrary.manager.MHBeautyManager;


/**
 * lihao
 * 构建底部美颜弹窗
 */
public class BeautyDialogFragment extends BottomDialog implements IBeautyHideTipCallBack {
    private final static Object lock = new Object();
    private String beautyType;
    private FrameLayout parent;
    private AbsMhChildViewHolder vh = null;
    private TextView mTip;

    public BeautyDialogFragment(Activity activity, String beautyType) {
        super(activity, R.layout.fragment_beauty);
        this.beautyType = beautyType;
        initView();
    }

//    private static BeautyDialogFragment instance;

//    private BeautyDialogFragment(Activity activity) {
//        super(activity, R.layout.fragment_beauty);
//    }
//
//    public static BeautyDialogFragment get(Activity activity, String beautyType) {
//        if (null == instance) {
//            synchronized (lock) {
//                if (null == instance || activity != instance.mActivity) {
//                    instance = new BeautyDialogFragment(activity);
//                }
//            }
//        }
//        instance.setBeautyType(beautyType);
//        return instance;
//    }
//
//    private void setBeautyType(String beautyType) {
//        this.beautyType = beautyType;
//        initView();
//    }
//
//    public static void hint() {
//        if (null != instance) instance.dismiss();
//    }

    public String getBeautyType() {
        return beautyType;
    }

    public void initView() {
        Logger.e("BeautyDialogFragment", "initView");
        mTip = (TextView) getDialog().findViewById(R.id.tip);
        parent = (FrameLayout) getDialog().findViewById(R.id.fl_content);
        switch (beautyType) {
            case MHConfigConstants.TIE_ZHI:
                vh = new MhTieZhiViewHolder(mActivity, parent);
                break;
            case MHConfigConstants.MEI_YAN:
                vh = new MhMeiYanViewHolder(mActivity, parent);
                break;
            case MHConfigConstants.MEI_ZHUANG:
                vh = new MhMakeupViewHolder(mActivity, parent);
                break;
            case MHConfigConstants.TE_XIAO:
                vh = new MhTeXiaoViewHolder(mActivity, parent);
                break;
            case MHConfigConstants.HA_HA_JING:
                vh = new MhHaHaViewHolder(mActivity, parent);
                break;
        }
        vh.addToParent();
        if (vh != null) {
            vh.loadData();
        }
        vh.setOnTieZhiClickListener(new OnTieZhiClickListener() {
            @Override
            public void OnTieZhiClick() {
                if (vh instanceof MhTeXiaoViewHolder) {
                    MhTeXiaoViewHolder mhTeXiaoViewHolder = (MhTeXiaoViewHolder) vh;
                    mhTeXiaoViewHolder.setActionItemClick(0);
                    showActionTip(0);
                }

            }
        });
        vh.setOnTieZhiActionClickListener(new OnTieZhiActionClickListener() {
            @Override
            public void OnTieZhiActionClick(int action) {
                enableUseFace(null, action);
                if (vh instanceof MhTieZhiViewHolder) {
                    MhTieZhiViewHolder mhTieZhiViewHolder = (MhTieZhiViewHolder) vh;
                    mhTieZhiViewHolder.clearCheckedPosition();
                }
            }
        });
        vh.setOnTieZhiActionListener(new OnTieZhiActionListener() {
            @Override
            public void OnTieZhiAction(int action) {
                showActionTip(action);
            }
        });
        MhDataManager.getInstance().setIBeautyHideTipCallBack(this::hideTip);
    }

    private void showActionTip(int action) {
        if (action == 0) {
            mTip.setText("");
            mTip.setVisibility(View.INVISIBLE);
        } else {
            mTip.setVisibility(View.VISIBLE);
            mTip.setText(getTipText(action));
        }
    }

    private String getTipText(int action) {
        int tipRes = 0;
        if (action == 1) {
            tipRes = com.meihu.beauty.R.string.beauty_mh_texiao_action_head_tip;
        } else if (action == 2) {
            tipRes = com.meihu.beauty.R.string.beauty_mh_texiao_action_mouth_tip;
        } else {
            tipRes = com.meihu.beauty.R.string.beauty_mh_texiao_action_eye_tip;
        }
        return WordUtil.getString(mActivity, tipRes);
    }

    private void enableUseFace(String stickerName, int action) {
        int useFace;
        if (TextUtils.isEmpty(stickerName)) {
            useFace = 0;
        } else {
            useFace = 1;
        }
        MHBeautyManager mhBeautyManager = MhDataManager.getInstance().getMHBeautyManager();
        if (mhBeautyManager != null) {
            int[] useFaces = mhBeautyManager.getUseFaces();
            useFaces[0] = useFace;
            mhBeautyManager.setUseFaces(useFaces);
        }
        MhDataManager.getInstance().setTieZhi(stickerName, action);
    }

    @Override
    public void hideTip() {
        parent.post(new Runnable() {
            @Override
            public void run() {
                if (mTip != null) {
                    mTip.setText("");
                    mTip.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

}
