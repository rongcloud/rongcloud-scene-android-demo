/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */
package cn.rongcloud.config;

public class ApiConfig {
    public final static String HOME_PAGE = "https://docs.rongcloud.cn/v4/5X/views/scene/voiceroom/android/intro/intro.html";
    public final static String DEFAULT_PORTRAIT_ULR = "https://cdn.ronghub.com/demo/default/rce_default_avatar.png";

    /**
     * 注册协议
     */
    public final static String REGISTER = "https://cdn.ronghub.com/term_of_service_zh.html";
    /**
     * 隐私政策
     */
    public final static String PRIVACY = "https://cdn.ronghub.com/Privacy_agreement_zh.html";
    public final static int REQUEST_SUCCESS_CODE = 10000;
    public static final String CUSTOMER_PHONE = "13161856839";
    public static String HOST = AppConfig.get().getBaseServerAddress();
    public static String FILE_URL = HOST + "file/show?path=";
    /**
     * 文件上传
     */
    public static String FILE_UPLOAD = HOST + "file/upload";

}