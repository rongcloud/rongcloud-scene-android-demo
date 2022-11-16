package cn.rc.community;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import cn.rc.community.bean.NewsOperation;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/3/9
 * @time 6:30 下午
 * 消息操作适配器
 */
public class NewsOperationAdapter extends BaseQuickAdapter<NewsOperation, BaseViewHolder> {


    public NewsOperationAdapter() {
        super(R.layout.item_news_operation);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, NewsOperation newsOperation) {
        baseViewHolder.setImageResource(R.id.iv_icon, newsOperation.getIcon());
        baseViewHolder.setText(R.id.tv_name, newsOperation.getName());
    }
}
