package cn.rongcloud.thirdcdn;

/**
 * @author gyn
 * @date 2022/9/7
 */
public class ThirdCDNConstant {
    // 推流地址
    private static String PUSH_URL = "";
    // 拉流地址
    private static String PULL_URL = "";

    public static void setPushAndPullUrl(String pushUrl, String pullUrl) {
        PUSH_URL = pushUrl;
        PULL_URL = pullUrl;
    }


    public static String getPushUrl(String roomId) {
        return PUSH_URL + roomId;
    }

    public static String getPullUrl(String roomId) {
        return PULL_URL + roomId;
    }
}
