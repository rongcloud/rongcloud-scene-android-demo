package cn.rongcloud.voiceroomdemo.webview;

/**
 * 会议控制监听
 */
public interface OnQlListener {

    /**
     * 标题切换回调
     *
     * @param title 标题
     */
    void onTitle(String title);

    /**
     * 加入会议回调
     *
     * @param conferenceId 会议Id
     * @param title        会议标题
     */
    void onJoinConference(String conferenceId, String title);

    /**
     * 重新登录回调，如：token 失效等导致。
     */
    void onRelogin();

    /**
     * 点击回退回调
     */
    void onFinish();
}