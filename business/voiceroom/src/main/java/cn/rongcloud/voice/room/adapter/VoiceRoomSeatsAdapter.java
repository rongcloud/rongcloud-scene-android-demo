package cn.rongcloud.voice.room.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.basis.utils.ImageLoader;
import com.basis.wapper.IResultBack;
import com.rc.voice.R;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.config.provider.user.UserProvider;
import cn.rongcloud.roomkit.ui.room.model.MemberCache;
import cn.rongcloud.roomkit.ui.room.widget.WaveView;
import cn.rongcloud.voice.model.UiSeatModel;
import io.rong.imlib.model.UserInfo;

/**
 * 语聊房麦位适配器
 */
public class VoiceRoomSeatsAdapter extends RecyclerView.Adapter<VoiceRoomSeatsAdapter.SeatItemViewHolder> {

    private OnClickVoiceRoomSeatsListener onClickVoiceRoomSeatsListener;
    private Context context;

    //数据源
    private ArrayList<UiSeatModel> seatData = new ArrayList<>();

    public VoiceRoomSeatsAdapter(Context context, OnClickVoiceRoomSeatsListener onClickVoiceRoomSeatsListener) {
        this.onClickVoiceRoomSeatsListener = onClickVoiceRoomSeatsListener;
        this.context = context;
    }

    /**
     * 刷新数据源
     *
     * @param seatList
     */
    public void refreshData(List<UiSeatModel> seatList) {
        // TODO: 2021/6/21 后期需添加上 DiffUtil 避免出现每次都刷全部麦位的情况，可以实现局部刷新
        seatData.clear();
        seatData.addAll(seatList);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public SeatItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.layout_seat_item, parent, false);
        return new SeatItemViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull SeatItemViewHolder holder, @SuppressLint("RecyclerView") int position) {
        UiSeatModel uiSeatModel = seatData.get(position);
        switch (uiSeatModel.getSeatStatus()) {
            case RCSeatStatusUsing:
                holder.wv_seat_background.setVisibility(View.VISIBLE);
                holder.iv_user_portrait.setVisibility(View.VISIBLE);
                holder.tv_member_name.setVisibility(View.VISIBLE);
                holder.tv_member_name.setVisibility(View.VISIBLE);
                holder.tv_gift_count.setVisibility(View.VISIBLE);
                holder.iv_user_portrait.setBackgroundResource(R.drawable.bg_voice_room_portrait);
                if (uiSeatModel.isSpeaking() && !uiSeatModel.isMute()) {
                    holder.wv_seat_background.start();
                } else {
                    holder.wv_seat_background.stop();
                }
                holder.iv_user_portrait.setTag(uiSeatModel.getPortrait());
                holder.iv_is_mute.setVisibility(uiSeatModel.isMute() ? View.VISIBLE : View.GONE);
                holder.iv_seat_status.setVisibility(View.GONE);
                //TODO 这里一下子如果拿不到怎么办
                if (!TextUtils.isEmpty(uiSeatModel.getUserId())) {
                    UserProvider.provider().getAsyn(uiSeatModel.getUserId(), new IResultBack<UserInfo>() {
                        @Override
                        public void onResult(UserInfo userInfo) {
                            if (userInfo != null && !TextUtils.isEmpty(userInfo.getName())) {
                                holder.tv_member_name.setText(userInfo.getName());
                            } else {
                                holder.tv_member_name.setText("");
                            }
                            ImageLoader.loadUri(holder.iv_user_portrait,
                                    userInfo.getPortraitUri(), R.drawable.default_portrait);
                        }
                    });

                }
                holder.tv_gift_count.setText(uiSeatModel.getGiftCount() + "");
                if (MemberCache.getInstance().isAdmin(uiSeatModel.getUserId())) {
                    holder.tv_member_name.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_is_admin,
                            0,
                            0,
                            0
                    );
                } else {
                    holder.tv_member_name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
                break;
            case RCSeatStatusEmpty:
                holder.iv_user_portrait.setVisibility(View.VISIBLE);
                holder.tv_member_name.setVisibility(View.VISIBLE);
                holder.tv_member_name.setGravity(Gravity.CENTER);
                holder.iv_seat_status.setVisibility(View.VISIBLE);
                holder.wv_seat_background.setVisibility(View.GONE);
                holder.tv_gift_count.setVisibility(View.INVISIBLE);
                holder.iv_is_mute.setVisibility(uiSeatModel.isMute() ? View.VISIBLE : View.GONE);
                holder.tv_member_name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                holder.iv_seat_status.setImageDrawable(context.getDrawable(R.drawable.ic_seat_status_enter));
                holder.tv_member_name.setText((position + 1) + " 号麦位");
                holder.iv_user_portrait.setImageDrawable(context.getDrawable(R.drawable.bg_seat_status));
                holder.iv_user_portrait.setBackground(null);
                break;
            case RCSeatStatusLocking:
                holder.iv_user_portrait.setVisibility(View.VISIBLE);
                holder.tv_member_name.setVisibility(View.VISIBLE);
                holder.iv_seat_status.setVisibility(View.VISIBLE);
                holder.wv_seat_background.setVisibility(View.GONE);
                holder.tv_gift_count.setVisibility(View.INVISIBLE);
                holder.iv_is_mute.setVisibility(uiSeatModel.isMute() ? View.VISIBLE : View.GONE);
                holder.tv_member_name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                holder.iv_seat_status.setImageDrawable(context.getDrawable(R.drawable.ic_seat_status_locked));
                holder.tv_member_name.setText((position + 1) + " 号麦位");
                holder.iv_user_portrait.setImageDrawable(context.getDrawable(R.drawable.bg_seat_status));
                holder.iv_user_portrait.setBackground(null);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + uiSeatModel.getSeatStatus());
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickVoiceRoomSeatsListener.clickVoiceRoomSeats(uiSeatModel, position);
            }
        });
        holder.itemView.setTag(uiSeatModel.toString());
    }

    @Override
    public int getItemCount() {
        return seatData.size();
    }

    public interface OnClickVoiceRoomSeatsListener {
        void clickVoiceRoomSeats(UiSeatModel uiSeatModel, int position);
    }

    class SeatItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView iv_user_portrait;
        private WaveView wv_seat_background;
        private TextView tv_member_name;
        private ImageView iv_seat_status;
        private ImageView iv_is_mute;
        private TextView tv_gift_count;

        public SeatItemViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_user_portrait = itemView.findViewById(R.id.iv_user_portrait);
            wv_seat_background = itemView.findViewById(R.id.wv_seat_background);
            tv_member_name = itemView.findViewById(R.id.tv_member_name);
            iv_seat_status = itemView.findViewById(R.id.iv_seat_status);
            iv_is_mute = itemView.findViewById(R.id.iv_is_mute);
            tv_gift_count = itemView.findViewById(R.id.tv_gift_count);
        }
    }
}


