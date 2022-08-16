//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.basis.imkit;

import android.content.Context;
import android.content.res.Resources;
import android.net.http.SslCertificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FeatureConfig {
    private static final String TAG = "FeatureConfig";
    private static String KIT_VERSION = "4.1.0.98";
    private boolean isReferenceEnable = true;
    private boolean isDestructEnable = false;
    private boolean isQuickReplyEnable;
    private VoiceMessageType voiceMessageType;
    private int audioNBEncodingBitRate;
    private int audioWBEncodingBitRate;
    private int userCacheMaxCount;
    private int groupCacheMaxCount;
    private int groupMemberCacheMaxCount;
    private boolean preLoadUserCache = true;
    public boolean rc_wipe_out_notification_message = true;
    public boolean rc_set_java_script_enabled = true;
    public boolean rc_sound_in_foreground = true;
    private FeatureConfig.SSLInterceptor sSSLInterceptor;
    public String rc_translation_src_language;
    public String rc_translation_target_language;

    public FeatureConfig() {
        this.voiceMessageType = VoiceMessageType.HighQuality;
        this.isQuickReplyEnable = false;
        this.audioNBEncodingBitRate = 7950;
        this.audioWBEncodingBitRate = 12650;

    }

    public void initConfig(Context context) {
    }

    public boolean isReferenceEnable() {
        return this.isReferenceEnable;
    }

    public boolean isDestructEnable() {
        return this.isDestructEnable;
    }

    public boolean isQuickReplyEnable() {
        return this.isQuickReplyEnable;
    }

    public boolean isQuickReplyType() {
        return this.isQuickReplyEnable;
    }

    public void setAudioNBEncodingBitRate(int audioNBEncodingBitRate) {
        this.audioNBEncodingBitRate = audioNBEncodingBitRate;
    }

    public void setAudioWBEncodingBitRate(int audioWBEncodingBitRate) {
        this.audioWBEncodingBitRate = audioWBEncodingBitRate;
    }

    public void setVoiceMessageType(VoiceMessageType type) {
        this.voiceMessageType = type;
    }

    public int getAudioNBEncodingBitRate() {
        return this.audioNBEncodingBitRate;
    }

    public int getAudioWBEncodingBitRate() {
        return this.audioWBEncodingBitRate;
    }


    public VoiceMessageType getVoiceMessageType() {
        return this.voiceMessageType;
    }

    public static enum VoiceMessageType {
        Ordinary,
        HighQuality;
    }

    public void enableReference(Boolean value) {
        this.isReferenceEnable = value;
    }

    public void enableDestruct(Boolean value) {
        this.isDestructEnable = value;
    }


    public int getUserCacheMaxCount() {
        return this.userCacheMaxCount;
    }

    public void setUserCacheMaxCount(int userCacheMaxCount) {
        this.userCacheMaxCount = userCacheMaxCount;
    }

    public int getGroupCacheMaxCount() {
        return this.groupCacheMaxCount;
    }

    public void setGroupCacheMaxCount(int groupCacheMaxCount) {
        this.groupCacheMaxCount = groupCacheMaxCount;
    }

    public int getGroupMemberCacheMaxCount() {
        return this.groupMemberCacheMaxCount;
    }

    public void setGroupMemberCacheMaxCount(int groupMemberCacheMaxCount) {
        this.groupMemberCacheMaxCount = groupMemberCacheMaxCount;
    }

    public boolean isPreLoadUserCache() {
        return this.preLoadUserCache;
    }

    public void setPreLoadUserCache(boolean preLoadUserCache) {
        this.preLoadUserCache = preLoadUserCache;
    }

    public FeatureConfig.SSLInterceptor getSSLInterceptor() {
        return this.sSSLInterceptor;
    }

    public void setSSLInterceptor(FeatureConfig.SSLInterceptor sSSLInterceptor) {
        this.sSSLInterceptor = sSSLInterceptor;
    }

    public interface SSLInterceptor {
        boolean check(SslCertificate sslCertificate);
    }
}
