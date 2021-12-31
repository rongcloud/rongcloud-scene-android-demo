package com.meihu.beauty.bean;

import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.meihu.beauty.utils.MhDataManager;
import com.meihu.beautylibrary.MHSDK;
import com.meihu.beautylibrary.bean.MHCommonBean;

public class MakeupBean extends MHCommonBean {
    private int mName;
    private int mThumb0;
    private int mThumb1;
    private int mMakeupId;
    private boolean mChecked;
    private Drawable mDrawable0;
    private Drawable mDrawable1;

    public MakeupBean(int name, int thumb0, int thumb1, int makeupId, String key) {
        mName = name;
        mThumb0 = thumb0;
        mThumb1 = thumb1;
        mMakeupId = makeupId;
        mKey = key;
    }

    public MakeupBean(int name, int thumb0, int thumb1, boolean checked, String key) {
        this(name, thumb0, thumb1, MHSDK.MAKEUP_NONE, key);
        mChecked = checked;
    }

    public int getName() {
        return mName;
    }

    public int getThumb0() {
        return mThumb0;
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
    }

    public int getMakeupId() {
        return mMakeupId;
    }

    public Drawable getDrawable0() {
        if (mDrawable0 == null) {
            mDrawable0 = ContextCompat.getDrawable(MhDataManager.getInstance().getContext(), mThumb0);
        }
        return mDrawable0;
    }


    public Drawable getDrawable1() {
        if (mDrawable1 == null) {
            mDrawable1 = ContextCompat.getDrawable(MhDataManager.getInstance().getContext(), mThumb1);
        }
        return mDrawable1;
    }
}
