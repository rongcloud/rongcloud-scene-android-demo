package cn.rongcloud.roomkit.ui.room.dialog.shield;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author gyn
 * @date 2021/10/8
 */
public class Shield implements Serializable {
    private static final int DEFAULT_ID = -1;
    @SerializedName("id")
    private Integer id;
    @SerializedName("name")
    private String name;
    @SerializedName("createDt")
    private Long createDt;

    public static Shield buildDefault() {
        Shield shield = new Shield();
        shield.setId(DEFAULT_ID);
        return shield;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCreateDt() {
        return createDt;
    }

    public void setCreateDt(Long createDt) {
        this.createDt = createDt;
    }

    public boolean isDefault() {
        return DEFAULT_ID == this.id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Shield)) return false;
        Shield shield = (Shield) o;
        return getId().equals(shield.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
