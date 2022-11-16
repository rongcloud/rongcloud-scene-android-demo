package com.basis.utils;

/**
 * 时间格式
 * 分割符：时间格式定义两种分割符C和H
 * C:  :
 * H:汉字 年、月、日
 */
public enum TimeFt {
    HmsS("HHmmssSSS"),//150334999
    Hms("HHmmss"),//150334
    HHmHsH("HH时mm分ss秒"),//15时30分21秒
    HCmCs("HH:mm:ss"),//15:03:34
    Hm("HHmm"),//1503
    HHmH("HH时mm分"),//15时30分
    HCm("HH:mm");//15:03
    //定义值
    private String value;

    TimeFt(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}