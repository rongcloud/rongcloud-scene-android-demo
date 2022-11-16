package cn.rc.community.conversion.controller.interfaces;

import androidx.annotation.LayoutRes;

import com.basis.adapter.RcyHolder;

import io.rong.imlib.model.Message;


public interface IMessage {
    /**
     * @return 消息显示布局Id
     */
    @LayoutRes
    int getLayoutId();

    /**
     * @return 消息的标识
     */
    String getIdentifier();

    /**
     * 绑定数据
     */
    void convert(RcyHolder holder, int position);

    /**
     * 获取消息体
     */
    Message getMessage();

    /**
     * 设置长按监听事件，让每个item也可以去做特定的操作
     *
     * @param listener 监听
     */
    void setOnItemLongClickListener(IMessageAdapter.OnItemLongClickListener listener);

    /**
     * 设置点击监听事件
     *
     * @param listener 监听
     */
    void setOnItemClickListener(IMessageAdapter.OnItemClickListener listener);
}
