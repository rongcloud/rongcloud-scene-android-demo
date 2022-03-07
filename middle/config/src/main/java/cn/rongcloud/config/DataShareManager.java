package cn.rongcloud.config;

/**
 * 模块数据共享管理类
 */
public class DataShareManager {
    private final static DataShareManager manager = new DataShareManager();
    /**
     * 是否忽略音视频呼叫和接收
     */
    private boolean ignoreIncomingCall = false;

    private DataShareManager() {
    }

    public static DataShareManager get() {
        return manager;
    }

    /**
     * @return 是否忽略音视频呼叫
     */
    public boolean isIgnoreIncomingCall() {
        return ignoreIncomingCall;
    }

    /**
     * 设置是否忽略音视频呼叫
     *
     * @param ignoreIncomingCall 是否忽略音视频呼叫
     */
    public void setIgnoreIncomingCall(boolean ignoreIncomingCall) {
        this.ignoreIncomingCall = ignoreIncomingCall;
    }
}
