package cn.rongcloud.pk.dialog;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.basis.adapter.RcyHolder;
import com.basis.adapter.RcySAdapter;
import com.basis.adapter.interfaces.IAdapte;
import com.basis.utils.ImageLoader;
import com.basis.utils.KToast;
import com.basis.utils.Logger;
import com.basis.utils.UIKit;
import com.basis.wapper.IResultBack;
import com.basis.widget.dialog.BottomDialog;

import java.util.List;

import cn.rongcloud.config.bean.VoiceRoomBean;
import cn.rongcloud.pk.R;
import cn.rongcloud.pk.api.PKApi;
import cn.rongcloud.pk.bean.PKResult;

/**
 * pk在线房主弹框
 */
public class OnlineCreatorDialog extends BottomDialog {
    private OnSendPkCallback onSendPkCallback;

    private RecyclerView rcyOwner;
    private FrameLayout emptyLayout;
    private IAdapte adapter;
    private int roomType;

    public OnlineCreatorDialog(Activity activity, int roomType, OnSendPkCallback onSendPkCallback) {
        super(activity);
        this.onSendPkCallback = onSendPkCallback;
        this.roomType = roomType;
        setContentView(R.layout.layout_owner_dialog, 60);
        initView();
        requestOwners();
    }

    private void initView() {
        emptyLayout = getContentView().findViewById(R.id.fl_empty);
        rcyOwner = UIKit.getView(getContentView(), R.id.rcy_owner);
        rcyOwner.setLayoutManager(new LinearLayoutManager(mActivity));

        adapter = new RcySAdapter<VoiceRoomBean, RcyHolder>(mActivity, R.layout.layout_owner_item) {

            @Override
            public void convert(RcyHolder holder, VoiceRoomBean item, int position) {
                holder.setText(R.id.tv_name, item.getCreateUser().getUserName());
                ImageLoader.loadUrl(holder.getView(R.id.head),
                        item.getCreateUser().getPortraitUrl(),
                        R.drawable.default_portrait,
                        ImageLoader.Size.S_200);
                holder.rootView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String roomId = item.getRoomId();
                        isInPk(roomId, new IResultBack<Boolean>() {
                            @Override
                            public void onResult(Boolean aBoolean) {
                                if (!aBoolean) {// 没有正在pk
                                    dismiss();
                                    if (null != onSendPkCallback)
                                        onSendPkCallback.sendPkInvitation(roomId, item.getCreateUserId());
                                } else {
                                    KToast.show("对方正在PK中");
                                }
                            }
                        });
                    }
                });
            }
        };
        adapter.setRefreshView(rcyOwner);
    }

    /**
     * 判断是否正在pk
     *
     * @param roomId     房间id
     * @param resultBack 回调
     */
    void isInPk(String roomId, IResultBack<Boolean> resultBack) {
        PKApi.getPKInfo(roomId, new IResultBack<PKResult>() {
            @Override
            public void onResult(PKResult pkResult) {
                if (null == pkResult || pkResult.getStatusMsg() == -1 || pkResult.getStatusMsg() == 2) {
                    Logger.e(TAG, "init: Not In PK");
                    resultBack.onResult(false);
                } else {
                    resultBack.onResult(true);
                }
            }
        });
    }

    private void requestOwners() {
        PKApi.getOnlineCreator(roomType, new IResultBack<List<VoiceRoomBean>>() {
            @Override
            public void onResult(List<VoiceRoomBean> voiceRoomBeans) {
                adapter.setData(voiceRoomBeans, true);
                if (voiceRoomBeans != null && voiceRoomBeans.size() > 0) {
                    emptyLayout.setVisibility(View.GONE);
                } else {
                    emptyLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public interface OnSendPkCallback {
        void sendPkInvitation(String inviteeRoomId, String inviteeUserId);
    }
}
