package cn.rc.community.channel.TextChannel;

import android.widget.EditText;

import androidx.recyclerview.widget.RecyclerView;

import com.basis.ui.mvp.IBaseView;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/4/26
 * @time 11:54
 * 文字频道 view
 */
public interface ITextChannelView extends IBaseView {

    /**
     * 刷新未读消息数量View
     *
     * @param unReadCount
     */
    void refreshUnReadView(int unReadCount);

    /**
     * 刷新空布局
     */
    void refreshEmptyView();

    /**
     * 刷新禁言view
     */
    void refreshShutUpView();

    /**
     * 刷新输入在状态
     */
    void refreshEditStatusView(int visible, String hint);


    /**
     * 滚动到最底部
     */
    void scrollBottom();

    /**
     * 获取消息的列表
     */
    RecyclerView getRecyclerView();

    /**
     * 获取输入框
     */
    EditText getEditText();
}
