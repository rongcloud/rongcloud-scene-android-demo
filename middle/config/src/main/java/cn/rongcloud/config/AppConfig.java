package cn.rongcloud.config;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.config.init.ModuleManager;

public class AppConfig {
    private static class Holder {
        private static final AppConfig INSTANCE = new AppConfig();
    }

    public final static String MODE_VOICE = "voice";
    public final static String MODE_RADIO = "radio";
    public final static String MODE_CALL = "call";
    public final static String MODE_LIVE = "live";
    public final static String MODE_GAME = "game";

    public static AppConfig get() {
        return Holder.INSTANCE;
    }

    public void init(String appKey,
                     String umengAppKey,
                     String baseServerAddress,//host
                     String businessToken, // businesstoken
                     String dispatchChannel,// 分发渠道
                     boolean international,// 国际化标识
                     String[] busiModes//配置业务模块数组
    ) {
        this.appKey = appKey;
        this.umengAppKey = umengAppKey;
        this.baseServerAddress = baseServerAddress;
        this.businessToken = businessToken;
        this.dispatchChannel = dispatchChannel;
        this.international = international;
        if (null == modes) modes = new ArrayList();
        modes.clear();
        if (null != busiModes) {
            for (String mode : busiModes) {
                modes.add(mode);
            }
        }
        ModuleManager.manager().onInit();
    }

    private String appKey = "";
    private String umengAppKey = "";
    private String baseServerAddress = "";
    private String businessToken = "";
    private boolean international = false;
    private String dispatchChannel;
    private List<String> modes;

    public String getAppKey() {
        return appKey;
    }

    public String getUmengAppKey() {
        return umengAppKey;
    }

    public String getBaseServerAddress() {
        return baseServerAddress;
    }

    public String getBusinessToken() {
        return businessToken;
    }

    public boolean isInternational() {
        return international;
    }

    public String getDispatchChannel() {
        return null == dispatchChannel ? "" : dispatchChannel;
    }

    /**
     * 判断mode是否配置
     *
     * @param modeName 功能模块
     * @return 是否配置
     */
    public boolean hasMode(String modeName) {
        return modes.contains(modeName);
    }

    public List<String> getModes() {
        return new ArrayList<>(modes);
    }

}
