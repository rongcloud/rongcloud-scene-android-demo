package com.basis.net;

import com.basis.net.oklib.net.Page;

/**
 * 网络请求分页设置的参数
 */
public class DefauPage implements Page {
    private String keySize = "pageSize";
    private String keyPage = "pageNo";
    int size = 10;//每页显示的记录数据 默认15条
    int first = 1;//第一页的索引 0 或者 1

    @Override
    public int getFirstIndex() {
        return first;
    }

    @Override
    public int geSize() {
        return size;
    }

    @Override
    public String getKeyPage() {
        return keySize;
    }

    @Override
    public String getKeySize() {
        return keyPage;
    }
}
