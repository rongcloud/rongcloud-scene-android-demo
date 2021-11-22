package com.basis.net.oklib.wrapper.interfaces;

import java.io.Serializable;

/**
 * @author: BaiCQ
 * @ClassName: DataInfo
 * @date: 2018/6/27
 * @Description: DataInfo Json解析实体
 */
public interface IPage extends Serializable {
    int getPage();

    int getTotal();
}
