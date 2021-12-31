package com.meihu.beauty.bean;

import android.text.TextUtils;

import com.alibaba.fastjson.annotation.JSONField;
import com.meihu.beauty.utils.MhDataManager;

public class TieZhiBean {

    private String mName;
    private String mThumb;
    private String mResource;
    private boolean mChecked;
    private boolean mDownLoading;
    private Boolean mDownLoaded;
    private String mKey;


    @JSONField(name = "name")
    public String getName() {
        return mName;
    }

    @JSONField(name = "name")
    public void setName(String name) {
        mName = name;
    }

    @JSONField(name = "thumb")
    public String getThumb() {
        return mThumb;
    }

    @JSONField(name = "thumb")
    public void setThumb(String thumb) {
        mThumb = thumb;
    }

    @JSONField(name = "resource")
    public String getResource() {
        return mResource;
    }

    @JSONField(name = "resource")
    public void setResource(String resource) {
        mResource = resource;
    }


    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
    }

    public boolean isDownLoading() {
        return mDownLoading;
    }

    public void setDownLoading(boolean downLoading) {
        mDownLoading = downLoading;
    }

    public boolean isDownLoaded() {
        return mDownLoaded;
    }

    public void setDownLoaded(boolean downLoaded) {
        mDownLoaded = downLoaded;
    }

    public void checkDownloaded() {
        mDownLoaded = TextUtils.isEmpty(mName) ? true : MhDataManager.isTieZhiDownloaded(mName);
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        mKey = key;
    }
}
