package cn.rong.combusis.common.utils.AudioRecorderUtil;

/**
 * 获取录音的音频流,用于拓展的处理
 * @author 李浩
 * @version V001R001C01B001
 */
public interface RecordStreamListener {
    void recordOfByte(byte[] data, int begin, int end);

    /**
     * 音量大小监听
     * @param db
     */
    void recordDb(Double db);
}
