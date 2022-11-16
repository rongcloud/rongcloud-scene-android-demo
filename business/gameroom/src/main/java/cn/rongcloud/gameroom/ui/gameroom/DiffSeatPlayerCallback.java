package cn.rongcloud.gameroom.ui.gameroom;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.basis.utils.GsonUtil;
import com.basis.utils.Logger;

import cn.rongcloud.gameroom.model.SeatPlayer;

/**
 * @author gyn
 * @date 2022/5/7
 */
public class DiffSeatPlayerCallback extends DiffUtil.ItemCallback<SeatPlayer> {

    /**
     * 判断是否是同一个item
     *
     * @param oldItem New data
     * @param newItem old Data
     * @return
     */
    @Override
    public boolean areItemsTheSame(@NonNull SeatPlayer oldItem, @NonNull SeatPlayer newItem) {
        return TextUtils.equals(oldItem.userId, newItem.userId);
    }

    /**
     * 当是同一个item时，再判断内容是否发生改变
     *
     * @param oldItem New data
     * @param newItem old Data
     * @return
     */
    @Override
    public boolean areContentsTheSame(@NonNull SeatPlayer oldItem, @NonNull SeatPlayer newItem) {
        Logger.e("===========" + GsonUtil.obj2Json(oldItem) + "\n" + GsonUtil.obj2Json(newItem));
        return oldItem.equals(newItem);
    }
}