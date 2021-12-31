package cn.rongcloud.voice.pk;

import android.app.Activity;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.basis.adapter.interfaces.IAdapte;
import com.basis.adapter.recycle.RcyHolder;
import com.basis.adapter.recycle.RcySAdapter;
import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.widget.BottomDialog;
import com.bcq.refresh.XRecyclerView;
import com.kit.UIKit;
import com.kit.cache.GsonUtil;
import com.kit.utils.ImageLoader;
import com.kit.utils.KToast;
import com.kit.utils.Logger;
import com.kit.wapper.IResultBack;

import java.util.List;

import cn.rong.combusis.api.VRApi;
import cn.rong.combusis.provider.voiceroom.VoiceRoomBean;
import cn.rong.combusis.provider.voiceroom.VoiceRoomProvider;
import cn.rong.combusis.sdk.VoiceRoomApi;
import cn.rong.combusis.sdk.event.wrapper.EToast;
import cn.rongcloud.voice.R;
import cn.rongcloud.voice.pk.domain.PKResult;

/**
 * pk在线房主弹框
 */
public class RoomOwerDialog extends BottomDialog {
    private XRecyclerView rcyOwner;
    private IAdapte adapter;
    private IResultBack resultBack;

    public RoomOwerDialog(Activity activity, IResultBack<Boolean> resultBack) {
        super(activity);
        this.resultBack = resultBack;
        setContentView(R.layout.layout_owner_dialog, 60);
        initView();
        requestOwners();
    }

    private void initView() {
        rcyOwner = UIKit.getView(getContentView(), R.id.rcy_owner);
        rcyOwner.setLayoutManager(new LinearLayoutManager(mActivity));

        adapter = new RcySAdapter<VoiceRoomBean, RcyHolder>(mActivity, R.layout.layout_owner_item) {

            @Override
            public void convert(RcyHolder holder, VoiceRoomBean item, int position) {
                holder.setText(R.id.tv_name, item.getCreateUser().getUserName());
                ImageLoader.loadUrl(holder.getView(R.id.head),
                        item.getCreateUser().getPortraitUrl(),
                        R.drawable.default_portrait,
                        ImageLoader.Size.SZ_200);
                holder.rootView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String roomId = item.getRoomId();
                        isInPk(roomId, new IResultBack<Boolean>() {
                            @Override
                            public void onResult(Boolean aBoolean) {
                                if (!aBoolean) {// 没有正在pk
                                    dismiss();
                                    VoiceRoomApi.getApi().sendPKInvitation(item.getRoomId(), item.getCreateUser().getUserId(),
                                            new IResultBack<Boolean>() {
                                                @Override
                                                public void onResult(Boolean aBoolean) {
                                                    KToast.show(aBoolean ? "已邀请PK,等待对方接受" : "PK邀请失败");
                                                    if (null != resultBack)
                                                        resultBack.onResult(aBoolean);
                                                }
                                            });
                                } else {
                                    EToast.showToast("对方正在PK中");
                                }
                            }
                        });
                    }
                });
            }
        };
        adapter.setRefreshView(rcyOwner);
        rcyOwner.enableRefresh(false);
        rcyOwner.enableRefresh(false);
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
        OkApi.get(VRApi.ONLINE_CREATER, null, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                Logger.e(TAG, "requestOwners#onResult:" + GsonUtil.obj2Json(result));
                List<VoiceRoomBean> rooms = result.getList(VoiceRoomBean.class);
                adapter.setData(rooms, true);
                VoiceRoomProvider.provider().update(rooms);
            }

            @Override
            public void onAfter() {
                if (null != rcyOwner) {
                    rcyOwner.loadComplete();
                    rcyOwner.refreshComplete();
                }
            }
        });
    }

}
