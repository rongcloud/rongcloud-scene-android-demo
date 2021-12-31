package com.meihu.beauty.bean;

import com.meihu.beautylibrary.bean.MHCommonBean;

public class HaHaBean extends MHCommonBean {
    private int mId;
    private int mName;
    private int mThumb;
    private boolean mChecked;

    public HaHaBean(int id, int name, int thumb, String key) {
        mId = id;
        mName = name;
        mThumb = thumb;
        mKey = key;
    }

    public HaHaBean(int id, int name, int thumb, boolean checked, String key) {
        this(id, name, thumb, key);
        mChecked = checked;
    }


    public int getId() {
        return mId;
    }


    public int getName() {
        return mName;
    }


    public int getThumb() {
        return mThumb;
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
    }
}
