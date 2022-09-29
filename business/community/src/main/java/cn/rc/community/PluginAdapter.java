package cn.rc.community;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.basis.utils.UIKit;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import cn.rc.community.plugins.IPlugin;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/3/11
 * @time 6:02 下午
 * 功能面板适配器
 */
public class PluginAdapter extends BaseQuickAdapter<IPlugin, BaseViewHolder> {

    public PluginAdapter(@Nullable List<IPlugin> data) {
        super(R.layout.item_news_operation, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, IPlugin iPlugin) {
        baseViewHolder.setImageDrawable(R.id.iv_icon, iPlugin.obtainDrawable(UIKit.getContext()));
        baseViewHolder.setText(R.id.tv_name, iPlugin.obtainTitle(UIKit.getContext()));
    }
}
