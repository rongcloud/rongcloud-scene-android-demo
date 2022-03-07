package cn.rongcloud.profile.webview;

/**
 * 会议控制监听
 */
public interface OnLoadListener {

    /**
     * 标题切换回调
     *
     * @param title 标题
     */
    void onTitle(String title);

    /**
     * 点击回退回调
     */
    void onFinish();
}