package com.rc.live.roomlist;


import android.app.AlertDialog;
import android.content.DialogInterface;

import androidx.fragment.app.Fragment;

import com.basis.ui.mvp.BasePresenter;
import com.rc.live.helper.LiveEventHelper;

import java.util.List;

import cn.rongcloud.config.bean.VoiceRoomBean;
import cn.rongcloud.liveroom.api.model.RCLiveInfo;
import cn.rongcloud.roomkit.ui.RoomType;
import cn.rongcloud.roomkit.ui.roomlist.AbsRoomListFragment;


/**
 * @author gyn
 * @date 2021/9/14
 */
public class LiveRoomListFragment extends AbsRoomListFragment {

    public static Fragment getInstance() {
        return new LiveRoomListFragment();
    }

    @Override
    public RoomType getRoomType() {
        return RoomType.LIVE_ROOM;
    }

    @Override
    public BasePresenter createPresent() {
        return null;
    }

    @Override
    public void initListener() {

    }

    @Override
    public boolean onLongClickItem(VoiceRoomBean item, int position, boolean isCreate, List<VoiceRoomBean> list) {
        new AlertDialog.Builder(getContext())
                .setItems(new String[]{"订阅MCU", "订阅融云CDN", "订阅三方CDN"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                LiveEventHelper.getInstance().setLiveType(RCLiveInfo.RCLiveType.MCU);
                                break;
                            case 1:
                                LiveEventHelper.getInstance().setLiveType(RCLiveInfo.RCLiveType.INNER_CDN);
                                break;
                            case 2:
                                LiveEventHelper.getInstance().setLiveType(RCLiveInfo.RCLiveType.THIRD_CDN);
                                break;
                        }
                        clickItem(item, position, isCreate, list);
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
        return super.onLongClickItem(item, position, isCreate, list);
    }
}
