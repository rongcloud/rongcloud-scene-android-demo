/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package io.rong.callkit.net.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author gusd
 * @Date 2021/08/02
 */
public class UserInfoModel {

    @SerializedName("code")
    private Integer code;
    @SerializedName("data")
    private UserInfo data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public UserInfo getData() {
        return data;
    }

    public void setData(UserInfo data) {
        this.data = data;
    }

    public static class UserInfo {
        @SerializedName("uid")
        private String uid;
        @SerializedName("name")
        private String name;
        @SerializedName("portrait")
        private String portrait;
        @SerializedName("mobile")
        private String mobile;
        @SerializedName("type")
        private Integer type;
        @SerializedName("deviceId")
        private String deviceId;
        @SerializedName("createDt")
        private Long createDt;
        @SerializedName("updateDt")
        private Long updateDt;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPortrait() {
            return portrait;
        }

        public void setPortrait(String portrait) {
            this.portrait = portrait;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public Long getCreateDt() {
            return createDt;
        }

        public void setCreateDt(Long createDt) {
            this.createDt = createDt;
        }

        public Long getUpdateDt() {
            return updateDt;
        }

        public void setUpdateDt(Long updateDt) {
            this.updateDt = updateDt;
        }
    }
}
