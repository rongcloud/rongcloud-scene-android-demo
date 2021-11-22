package cn.rong.combusis.music.domain;

import android.text.TextUtils;

import com.rongcloud.common.net.ApiConstant;

import java.io.Serializable;

public class MusicBean implements Serializable {
    private String author;
    private Long createDt;
    private int id;
    private String name;
    private String roomId;
    private double size;// 单位M
    private int type = -1;
    private Long updateDt;
    private String url;
    // 业务状态 是否在play
    private boolean playing = false;
    // 业务状态 是否已经添加
    private boolean addAlready;


    public boolean isAddAlready() {
        return addAlready;
    }

    public void setAddAlready(boolean addAlready) {
        this.addAlready = addAlready;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Long getCreateDt() {
        return createDt;
    }

    public void setCreateDt(Long createDt) {
        this.createDt = createDt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        if (TextUtils.isEmpty(name)) {
            if (url.contains("/")) {
                return url.substring(url.lastIndexOf("/")).replace(" ", "_");
            }
            return System.currentTimeMillis() + "";
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Long getUpdateDt() {
        return updateDt;
    }

    public void setUpdateDt(Long updateDt) {
        this.updateDt = updateDt;
    }

    public String getUrl() {
        return url.startsWith("http") ? url : ApiConstant.INSTANCE.getFILE_URL() + url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MusicBean musicBean = (MusicBean) o;
        return url.equals(musicBean.url);
    }
}
