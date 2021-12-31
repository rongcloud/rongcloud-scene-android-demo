package com.meihu.beauty.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.meihu.beauty.utils.ClickUtil;

/**
 * Created by cxf on 2018/9/22.
 */

public abstract class AbsViewHolder {

    protected String mTag;
    protected Context mContext;
    protected ViewGroup mParentView;
    protected View mContentView;


    public AbsViewHolder(Context context, ViewGroup parentView) {
        mTag = getClass().getSimpleName();
        mContext = context;
        mParentView = parentView;
        mContentView = LayoutInflater.from(context).inflate(getLayoutId(), mParentView, false);

        init();
    }

    public AbsViewHolder(Context context, ViewGroup parentView, Object... args) {
        mTag = getClass().getSimpleName();
        processArguments(args);
        mContext = context;
        mParentView = parentView;
        mContentView = LayoutInflater.from(context).inflate(getLayoutId(), mParentView, false);

        init();
    }


    protected void processArguments(Object... args) {

    }

    protected abstract int getLayoutId();

    public abstract void init();

    protected <T extends View> T findViewById(int res) {
        return mContentView.findViewById(res);
    }

    public View getContentView() {
        return mContentView;
    }

    protected boolean canClick() {
        return ClickUtil.canClick();
    }

    public void addToParent() {
        if (mParentView != null && mContentView != null) {
            mParentView.addView(mContentView);
        }
    }

    public void removeFromParent() {
        ViewParent parent = mContentView.getParent();
        if (parent != null) {
            ((ViewGroup) parent).removeView(mContentView);
        }
    }


}
