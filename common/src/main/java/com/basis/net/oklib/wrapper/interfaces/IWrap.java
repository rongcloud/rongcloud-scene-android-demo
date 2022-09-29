package com.basis.net.oklib.wrapper.interfaces;

import com.google.gson.JsonElement;

import java.io.Serializable;

/**
 * @author: BaiCQ
 * @ClassName: DataInfo
 * @date: 2018/6/27
 * @Description: DataInfo Json解析实体
 */
public interface IWrap extends Serializable {

    int getCode();

    String getMessage();

    JsonElement getBody();

    IPage getPage();

}
