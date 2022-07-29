package cn.rongcloud.radioroom.room;

public enum StreamType {
    @Deprecated
    live,// mcu合流
    rong,// 融云内置cdn流
    /**
     * 注意：
     * 1、观众如需订阅自定义cdn流，主播流类型必须是自定义cdn
     */
    customer// 第三方cdn
}