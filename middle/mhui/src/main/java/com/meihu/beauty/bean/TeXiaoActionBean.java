package com.meihu.beauty.bean;

import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.meihu.beauty.utils.MhDataManager;
import com.meihu.beautylibrary.bean.MHCommonBean;

public class TeXiaoActionBean extends MHCommonBean {

    private int mName;
    private int mThumb0;
    private int mThumb1;
    private boolean mChecked;
    private Drawable mDrawable0;
    private Drawable mDrawable1;
    private String mUrl;
    private int mAction;
    private String mStickerName;
    private String mResouce;

    public TeXiaoActionBean(int name, int thumb0, int thumb1, String url, int action, String key, String stickerName) {
        mName = name;
        mThumb0 = thumb0;
        mThumb1 = thumb1;
        mUrl = url;
        mAction = action;
        mKey = key;
        mStickerName = stickerName;
    }

    public TeXiaoActionBean(int name, int thumb0, int thumb1, boolean checked, String key, String stickerName) {
        this(name, thumb0, thumb1, "", 0, key, stickerName);
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


    public String getResouce() {
        return mResouce;
    }

    public void setResouce(String resouce) {
        mResouce = resouce;
    }

    public String getUrl() {
        return mUrl;
    }

    public int getAction() {
        return mAction;
    }

    public String getStickerName() {
        return mStickerName;
    }

    public void setStickerName(String stickerName) {
        this.mStickerName = stickerName;
    }
}
