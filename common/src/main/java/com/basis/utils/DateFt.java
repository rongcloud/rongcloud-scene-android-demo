package com.basis.utils;

/**
 * 日期格式：
 * 分割符：日期格式定义三种分割符：S、L 和 H
 * S:  /
 * L:  -
 * H:汉字 年、月、日
 */
public enum DateFt {
    yMd("yyyyMMdd"),// 20160927
    ySMSd("yyyy/MM/dd"),// 2016/09/27
    yLMLd("yyyy-MM-dd"),// 2016-09-27
    yHMHdH("yyyy年MM月dd日"),//2016年09月27日
    yM("yyyyMM"),// 201609
    ySM("yyyy/MM"),// 2016/09
    yLM("yyyy-MM"),// 2016-09
    yHMH("yyyy年MM月"),//2016年09月
    Md("MMdd"),//0927
    MSd("MM/dd"),//09/27
    MLd("MM-dd"),//09-27
    MHdH("MM月dd日");//09月27日
    private String value;

    DateFt(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}