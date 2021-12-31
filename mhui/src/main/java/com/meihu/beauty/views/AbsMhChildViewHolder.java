package com.meihu.beauty.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.meihu.beauty.R;
import com.meihu.beauty.interfaces.IBeautyClickListener;
import com.meihu.beauty.interfaces.OnBottomHideListener;
import com.meihu.beauty.interfaces.OnCaptureListener;
import com.meihu.beauty.interfaces.OnTieZhiActionClickListener;
import com.meihu.beauty.interfaces.OnTieZhiActionListener;
import com.meihu.beauty.interfaces.OnTieZhiClickListener;
import com.meihu.beauty.utils.MhDataManager;

public abstract class AbsMhChildViewHolder extends AbsCommonViewHolder {

    protected IBeautyClickListener mIBeautyClickListener;
    protected OnCaptureListener mOnCaptureListener;
    protected OnBottomHideListener mOnBottomHideListener;
    protected OnTieZhiClickListener mOnTieZhiClickListener;
    protected OnTieZhiActionClickListener mOnTieZhiActionClickListener;
    protected OnTieZhiActionListener mOnTieZhiActionListener;


    protected View mBottomView;
    protected ImageView mCapture;
    protected View mHide;
    protected View mContentView;
    protected View mBottomControl;
    protected View mLine;


    public AbsMhChildViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
        mHide = findViewById(R.id.hide);
        mCapture = findViewById(R.id.capture);
        mBottomView = findViewById(R.id.bottom);
        mContentView = findViewById(R.id.content);
        mBottomControl = findViewById(R.id.bottom_control);
        mLine = findViewById(R.id.line);

        if (mHide != null) {
            mHide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideBottom();
                }
            });
        }

        if (mCapture != null) {
            mCapture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnCaptureListener != null) {
                        mOnCaptureListener.OnCapture();
                    }
                }
            });
        }

        if (MhDataManager.getInstance().getShowCapture()) {

            if (mBottomView != null) {
                int height = mContext.getResources().getDimensionPixelSize(R.dimen.view_beauty_height);
                mBottomView.setTranslationY(height);
                mBottomView.getLayoutParams().height = height;
            }

            if (mContentView != null) {
                int height = mContext.getResources().getDimensionPixelSize(R.dimen.view_beauty_viewpager);
                mContentView.getLayoutParams().height = height;
            }

            if (mBottomControl != null) {
                mBottomControl.setVisibility(View.VISIBLE);
            }

            if (mCapture != null) {
                mCapture.setVisibility(View.VISIBLE);
            }


        } else {

            if (mBottomView != null) {
                int resId = 0;
                if (!(this instanceof MhTieZhiViewHolder)) {
                    resId = R.dimen.view_beauty_height_2;
                } else {
                    resId = R.dimen.view_beauty_height;
                }
                int height = mContext.getResources().getDimensionPixelSize(resId);
                mBottomView.setTranslationY(height);
                mBottomView.getLayoutParams().height = height;
            }

            if (mContentView != null) {
                int resId = 0;
                if (!(this instanceof MhTieZhiViewHolder)) {
                    resId = R.dimen.view_beauty_height_2;
                } else {
                    resId = R.dimen.view_beauty_height;
                }
                int height = mContext.getResources().getDimensionPixelSize(resId);
                mContentView.getLayoutParams().height = height;
            }

            if (mBottomControl != null) {
                mBottomControl.setVisibility(View.GONE);
            }

            if (mCapture != null) {
                mCapture.setVisibility(View.GONE);
            }

        }

    }

    public void showBottom() {
        if (mBottomView != null) {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mBottomView, "translationY", mBottomView.getHeight(), 0);
            objectAnimator.setDuration(300);
            objectAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (mOnBottomHideListener != null) {
                        mOnBottomHideListener.OnBottomShow();
                    }
                }
            });
            objectAnimator.start();
        }
    }


    public void hideBottom() {
        if (mBottomView != null) {
            if (mOnBottomHideListener != null) {
                mOnBottomHideListener.OnBottomStartHide();
            }
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mBottomView, "translationY", 0, mBottomView.getHeight());
            objectAnimator.setDuration(300);
            objectAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (mOnBottomHideListener != null) {
                        mOnBottomHideListener.OnBottomHide();
                    }
                }
            });
            objectAnimator.start();
        } else {
            if (mOnBottomHideListener != null) {
                mOnBottomHideListener.OnBottomHide();
            }
        }
    }


    public void setIBeautyClickListener(IBeautyClickListener IBeautyClickListener) {
        mIBeautyClickListener = IBeautyClickListener;
    }


    public void setOnBottomHideListener(OnBottomHideListener onBottomHideListener) {
        mOnBottomHideListener = onBottomHideListener;
    }

    public void setOnCaptureListener(OnCaptureListener onCaptureListener) {
        mOnCaptureListener = onCaptureListener;
    }

    public void setOnTieZhiClickListener(OnTieZhiClickListener onTieZhiClickListener) {
        mOnTieZhiClickListener = onTieZhiClickListener;
    }

    public void setOnTieZhiActionClickListener(OnTieZhiActionClickListener onTieZhiActionClickListener) {
        mOnTieZhiActionClickListener = onTieZhiActionClickListener;
    }

    public void setOnTieZhiActionListener(OnTieZhiActionListener onTieZhiActionListener) {
        mOnTieZhiActionListener = onTieZhiActionListener;
    }

    public abstract void showSeekBar();

    public abstract void hideSeekBar();

}
