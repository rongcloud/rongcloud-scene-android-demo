package cn.rc.community.dialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.basis.adapter.RcyHolder;
import com.basis.adapter.RcySAdapter;
import com.vanniktech.emoji.ios.IosEmoji;

import java.util.List;

import cn.rc.community.R;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/3/15
 * @time 4:54 下午
 * 表情适配器
 */
public class EmojiAdapter extends RcySAdapter<IosEmoji, RcyHolder> {


    public EmojiAdapter(@Nullable List<IosEmoji> data) {
        super(null, R.layout.cmu_item_emoji);
        setData(data, true);
    }

    @Override
    public void convert(@NonNull RcyHolder baseViewHolder, IosEmoji iosEmoji, int position) {
        baseViewHolder.setText(R.id.tv_emoji, iosEmoji.getUnicode());
    }
}
