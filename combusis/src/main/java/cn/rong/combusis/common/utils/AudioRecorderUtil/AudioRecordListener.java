package cn.rong.combusis.common.utils.AudioRecorderUtil;

import java.io.File;

public interface AudioRecordListener {
    /**
     * 开始
     */
    void startAudioRecord();

    /**
     * 结束
     */
    void endAudioRecord(File file);
}
