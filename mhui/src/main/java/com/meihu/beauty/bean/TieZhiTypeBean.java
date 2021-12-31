package com.meihu.beauty.bean;

import com.meihu.beautylibrary.bean.MHCommonBean;

public class TieZhiTypeBean extends MHCommonBean {

    private int mId;
    private String mName;
    private boolean mAdvance;
    private boolean mChecked;
    private String mUrl;

    public TieZhiTypeBean(int mId) {
        this.mId = mId;
    }

    public TieZhiTypeBean(int id, String name, String key) {
        mId = id;
        mName = name;
        mKey = key;
    }

    public TieZhiTypeBean(int id, String name, boolean advance, String key) {
        this(id, name, key);
        mAdvance = advance;
    }

    public TieZhiTypeBean(int id, String name, boolean advance, String key, String url) {
        this(id, name, advance, key);
        mUrl = url;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public boolean isAdvance() {
        return mAdvance;
    }

    public void setAdvance(boolean advance) {
        mAdvance = advance;
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }
}
