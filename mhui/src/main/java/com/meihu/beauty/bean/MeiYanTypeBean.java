package com.meihu.beauty.bean;

import com.meihu.beautylibrary.bean.MHCommonBean;

public class MeiYanTypeBean extends MHCommonBean {

    private int mName;
    private boolean mChecked;

    public MeiYanTypeBean(int name, String key) {
        mName = name;
        mKey = key;
    }

    public int getName() {
        return mName;
    }

    public void setName(int name) {
        mName = name;
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
    }
}
