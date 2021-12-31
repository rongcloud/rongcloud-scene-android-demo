package com.meihu.beauty.views;

import android.content.Context;
import android.view.ViewGroup;

/**
 * Created by cxf on 2018/10/26.
 */

public abstract class AbsCommonViewHolder extends AbsViewHolder {

    protected boolean mFirstLoadData = true;
    private boolean mShowed;

    public AbsCommonViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    public AbsCommonViewHolder(Context context, ViewGroup parentView, Object... args) {
        super(context, parentView, args);
    }

    public void loadData() {
    }

    protected boolean isFirstLoadData() {
        if (mFirstLoadData) {
            mFirstLoadData = false;
            return true;
        }
        return false;
    }

    public boolean isShowed() {
        return mShowed;
    }

    public void setShowed(boolean showed) {
        mShowed = showed;
    }
}
