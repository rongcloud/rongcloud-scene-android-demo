package cn.rongcloud.roomkit.ui.room.fragment.seatsetting;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import cn.rongcloud.config.provider.user.User;


/**
 * @author lihao
 * @project RongRTCDemo
 * @date 2021/11/26
 * @time 4:55 下午
 */
public class DIffUserCallBack extends DiffUtil.ItemCallback<User> {
    @Override
    public boolean areItemsTheSame(@NonNull User oldItem, @NonNull User newItem) {
        return oldItem.getUserId().equals(newItem.getUserId());
    }

    @Override
    public boolean areContentsTheSame(@NonNull User oldItem, @NonNull User newItem) {
        return oldItem.getUserId().equals(newItem.getUserId())
                && oldItem.getUserName().equals(newItem.getUserName())
                && oldItem.getPortraitUrl().equals(newItem.getPortraitUrl())
                && oldItem.getKey().equals(newItem.getKey())
                && oldItem.getPortrait().equals(newItem.getPortrait());
    }

    @Nullable
    @Override
    public Object getChangePayload(@NonNull User oldItem, @NonNull User newItem) {
        return super.getChangePayload(oldItem, newItem);
    }
}
