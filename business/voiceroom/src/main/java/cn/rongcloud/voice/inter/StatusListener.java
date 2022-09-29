package cn.rongcloud.voice.inter;

public interface StatusListener {
    /**
     * 网络延迟监听
     *
     * @param delay
     */
    void onStatus(int delay);

    /**
     * 接收私有消息
     */
    void onReceive(int unReadCount);

    /**
     * 说话状态回到
     *
     * @param index
     * @param speaking
     */
    void onSpeaking(int index, boolean speaking);
}