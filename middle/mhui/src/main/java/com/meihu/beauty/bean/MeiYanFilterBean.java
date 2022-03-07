package com.meihu.beauty.bean;

import com.meihu.beautylibrary.bean.MHCommonBean;

public class MeiYanFilterBean extends MHCommonBean {
    private int mName;
    private int mThumb;
    private int mFilterRes;
    private boolean mChecked;

    public MeiYanFilterBean(int name, int thumb, int filterRes, String key) {
        mName = name;
        mThumb = thumb;
        mFilterRes = filterRes;
        mKey = key;
    }

    public MeiYanFilterBean(int name, int thumb, int filterRes, boolean checked, String key) {
        this(name, thumb, filterRes, key);
        mChecked = checked;
    }


    public int getName() {
        return mName;
    }

    public int getThumb() {
        return mThumb;
    }

    public int getFilterRes() {
        return mFilterRes;
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
    }


}
