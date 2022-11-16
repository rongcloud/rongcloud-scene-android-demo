package cn.rongcloud.beauty.entity;

import android.text.TextUtils;

import java.io.File;
import java.io.Serializable;

import cn.rongcloud.beauty.data.StickerDataManager;

/**
 * @author gyn
 * @date 2022/10/14
 */
public class StickerBean implements Serializable {
    private int id;
    private String name;
    private String previewImgPath;
    private String resPath;
    private String localPath;
    private boolean isLoading;
    private int category;

    public StickerBean() {
    }

    public StickerBean(String imgUrl, String bundleUrl) {
        this.previewImgPath = imgUrl;
        this.resPath = bundleUrl;
    }

    public String getPreviewImgPath() {
        return StickerDataManager.HOST + previewImgPath;
    }

    public void setPreviewImgPath(String previewImgPath) {
        this.previewImgPath = previewImgPath;
    }

    public String getResPath() {
        return resPath;
    }

    public void setResPath(String resPath) {
        this.resPath = resPath;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public boolean hasDownload() {
        return !TextUtils.isEmpty(localPath);
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void initLocalPath(String rootPath) {
        File file = new File(rootPath, getFileName());
        if (file.exists()) {
            localPath = file.getAbsolutePath();
        }
    }

    public String getFileName() {
        return name + ".bundle";
    }
}
