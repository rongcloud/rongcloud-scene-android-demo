package cn.rongcloud.config.provider.wrapper;

import java.io.Serializable;

/**
 * 可提供接口
 */
public interface Provide extends Serializable {
    String getKey();
}
