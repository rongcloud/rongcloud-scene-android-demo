package cn.rongcloud.roomkit.ui.room;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.basis.utils.Logger;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.roomkit.ui.RoomListIdsCache;


/**
 * @author gyn
 * @date 2021/9/17
 */
public class RoomVPAdapter extends FragmentStateAdapter {

    private ArrayList<String> mRoomList = new ArrayList<>();
    private AbsRoomActivity mFragmentActivity;

    public RoomVPAdapter(@NonNull AbsRoomActivity fragmentActivity) {
        super(fragmentActivity);
        mFragmentActivity = fragmentActivity;
    }

    public ArrayList<String> getData() {
        return mRoomList;
    }

    public void addData(List<String> roomList) {
        if (roomList != null) {
            this.mRoomList.addAll(roomList);
            notifyDataSetChanged();
            RoomListIdsCache.get().update(roomList);
        }
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Logger.d("=================createFragment:" + position);
        return mFragmentActivity.getFragment(getItemData(position));
    }

    @Override
    public int getItemCount() {
        return mRoomList.size();
    }

    @Override
    public long getItemId(int position) {
        return mRoomList.get(position).hashCode();
    }

    public String getItemData(int position) {
        if (position >= 0 && position < mRoomList.size()) {
            return mRoomList.get(position);
        }
        return null;
    }

    public int getItemPosition(String roomId) {
        return mRoomList.indexOf(roomId);
    }

    public void setData(List<String> roomList) {
        if (roomList != null) {
            this.mRoomList.clear();
            this.mRoomList.addAll(roomList);
            notifyDataSetChanged();
            RoomListIdsCache.get().update(roomList);
        }
    }
}
