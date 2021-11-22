package com.basis.net;

import com.basis.adapter.interfaces.IAdapte;
import com.basis.adapter.interfaces.IHolder;
import com.basis.net.oklib.net.IOpe;

import java.util.List;

/**
 * Model 处理抽象接口
 *
 * @param <ND> net data 接口数据类型
 * @param <AD> adapter data 适配器数据类型 一般情况：和ND类型一致
 * @param <VH> 适配器的view holder类型
 */
public interface IOperator<ND, AD, VH extends IHolder> extends IOpe<ND> {

    @Override
    void onCustomerRequestAgain(boolean refresh);


    @Override
    void onError(int status, String errMsg);

    /**
     * 数据转换
     * Fix：问题1：适配数据和网络数据类型不一致，如：网络文件列表 和本地文件列表共存在列表中
     *
     * @param netData 当次（当前页）网络数据
     */
    List<AD> onTransform(List<ND> netData);

    /**
     * 设置adapter数据前回调
     * 一般分页排序功能获取拼接数据时使用,或在在没有实时数据时切换到缓存数据等
     *
     * @param netData 设置给adapter的所有（包含所有页码）数据
     * @return 返回数据集合直接设置给adapter，会执行showViewType()修改ui
     */
    List<AD> onPreSetData(List<AD> netData);

    IAdapte<AD, VH> onSetAdapter();
}