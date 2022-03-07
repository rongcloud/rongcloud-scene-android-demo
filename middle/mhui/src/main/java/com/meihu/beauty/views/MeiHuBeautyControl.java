package com.meihu.beauty.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.meihu.beauty.interfaces.OnBottomShowListener;
import com.meihu.beauty.interfaces.OnCaptureListener;
import com.meihu.beauty.utils.MhDataManager;

public class MeiHuBeautyControl extends RelativeLayout {

    private BeautyViewHolder beautyViewHolder;
    private OnCaptureListener mOnCaptureListener;

    private OnBottomShowListener mOnBottomShowListener;

    public MeiHuBeautyControl(@NonNull Context context) {
        super(context);
    }

    public MeiHuBeautyControl(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MeiHuBeautyControl(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnCaptureListener(OnCaptureListener onCaptureListener) {
        mOnCaptureListener = onCaptureListener;
    }

    public void setOnBottomShowListener(OnBottomShowListener onBottomShowListener) {
        mOnBottomShowListener = onBottomShowListener;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if (beautyViewHolder == null) {
            beautyViewHolder = new BeautyViewHolder(getContext(), this);
            MhDataManager.getInstance().setBeautyViewHolder(beautyViewHolder);
            beautyViewHolder.setOnCaptureListener(new OnCaptureListener() {
                @Override
                public void OnCapture() {
                    if (mOnCaptureListener != null) {
                        mOnCaptureListener.OnCapture();
                    }
                }
            });
            beautyViewHolder.setOnBottomShowListener(new OnBottomShowListener() {

                @Override
                public void onShow(boolean show) {
                    if (mOnBottomShowListener != null) {
                        mOnBottomShowListener.onShow(show);
                    }
                }
            });
            beautyViewHolder.addToParent();
        }
    }

    public View getCenterViewContainer() {
        return beautyViewHolder.getCenterViewContainer();
    }


    public ImageView getRecordView() {
        return beautyViewHolder.getRecordView();
    }


    public void showViewContainer(boolean isShow) {
        beautyViewHolder.showViewContainer(isShow);
    }

}
