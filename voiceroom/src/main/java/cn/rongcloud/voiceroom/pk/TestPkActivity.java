//package cn.rongcloud.voiceroom.pk;
//
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.View;
//import android.widget.TextView;
//
//import com.basis.net.oklib.OkApi;
//import com.basis.net.oklib.WrapperCallBack;
//import com.basis.net.oklib.wrapper.Wrapper;
//import com.basis.ui.BaseActivity;
//import com.kit.UIKit;
//import com.kit.cache.GsonUtil;
//import com.kit.utils.Logger;
//import com.kit.wapper.IResultBack;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import cn.rong.combusis.api.VRApi;
//import cn.rong.combusis.music.MusicDialog;
//import cn.rong.combusis.provider.voiceroom.VoiceRoomBean;
//import cn.rong.combusis.sdk.StateUtil;
//import cn.rong.combusis.sdk.VoiceRoomApi;
//import cn.rong.combusis.sdk.event.EventHelper;
//import cn.rong.combusis.sdk.event.wrapper.IEventHelp;
//import cn.rong.combusis.ui.room.dialog.shield.ShieldDialog;
//import cn.rongcloud.voiceroom.R;
//import cn.rongcloud.voiceroom.model.RCVoiceRoomInfo;
//import cn.rongcloud.voiceroom.pk.widget.PKView;
//
//public class TestPkActivity extends BaseActivity implements View.OnClickListener {
//    @Override
//    public int setLayoutId() {
//        return R.layout.activity_test_pk;
//    }
//
//    private VoiceRoomBean voiceRoomBean;
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        PKStateManager.get().unInit();
//    }
//
//    @Override
//    public void init() {
//        getWrapBar().setTitle(R.string.app_name).setBackHide(true).work();
//        String json = getIntent().getStringExtra(UIKit.KEY_BASE);
//        voiceRoomBean = GsonUtil.json2Obj(json, VoiceRoomBean.class);
//        Logger.e(TAG, "json = " + json);
//        if (null == voiceRoomBean) {
//            return;
//        }
//        initData();
//    }
//
//    TextView pkButton;
//    PKView pkVew;
//    View voice_room;
//
//    private void initData() {
//        voice_room = getView(R.id.voice_room);
//        pkVew = getView(R.id.pk_view);
//        pkButton = getView(R.id.send_pk);
//        pkButton.setText(EventHelper.helper().getPKState() == IEventHelp.Type.PK_INVITE ? "取消PK" : "邀请PK");
//        join();
//        PKStateManager.get().init(voiceRoomBean.getRoomId(), pkVew, new IPKState.VRStateListener() {
//            @Override
//            public void onPkStart() {
//                PKStateManager.get().enterPkWithAnimation(voice_room, pkVew, 200);
//            }
//
//            @Override
//            public void onPkStop() {
//                PKStateManager.get().quitPkWithAnimation(pkVew, voice_room, 200);
//            }
//
//            @Override
//            public void onPkState() {
//
//            }
//
//            @Override
//            public void onSendPKMessage(String content) {
//
//            }
//        });
//        // click
//        pkButton.setOnClickListener(this);
//        getView(R.id.leave).setOnClickListener(this);
//        getView(R.id.refresh).setOnClickListener(this);
//        getView(R.id.quitpk).setOnClickListener(this);
//        getView(R.id.fill).setOnClickListener(this);
//        getView(R.id.music).setOnClickListener(this);
//    }
//
//    @Override
//    public void onClick(View view) {
//        int id = view.getId();
//        if (R.id.leave == id) {
//            leave();
//        } else if (R.id.refresh == id) {
//            PKStateManager.get().refreshPKGiftRank();
//        } else if (R.id.fill == id) {//屏蔽词
//            ShieldDialog dialog = new ShieldDialog(this, "", 3);
//            dialog.show();
//        } else if (R.id.music == id) {//音乐
//            MusicDialog dialog = new MusicDialog(voiceRoomBean.getRoomId());
//            dialog.show(getSupportFragmentManager());
//        } else if (R.id.quitpk == id) {
//            PKStateManager.get().quitPK(activity);
//        } else if (R.id.send_pk == id) {
//            IEventHelp.Type state = EventHelper.helper().getPKState();
//            if (!StateUtil.enableInvite()) {
//                return;
//            }
//            Logger.e(TAG, "state = " + state);
//            if (state == IEventHelp.Type.PK_INVITE) {
//                PKStateManager.get().cancelPkInvitation(activity, new IResultBack<Boolean>() {
//                    @Override
//                    public void onResult(Boolean aBoolean) {
//                        if (aBoolean) pkButton.setText("邀请PK");
//                    }
//                });
//            } else {
//                PKStateManager.get().sendPkInvitation(activity, new IResultBack<Boolean>() {
//                    @Override
//                    public void onResult(Boolean aBoolean) {
//                        if (aBoolean) pkButton.setText("取消PK");
//                    }
//                });
//            }
//        }
//    }
//
//    private void join() {
//        RCVoiceRoomInfo roomInfo = VoiceRoomApi.getApi().getRoomInfo();
//        roomInfo.setSeatCount(8);
//        roomInfo.setRoomName(voiceRoomBean.getRoomName());
//        roomInfo.setMuteAll(false);
//        roomInfo.setLockAll(false);
//        VoiceRoomApi.getApi().createAndJoin(voiceRoomBean.getRoomId(), roomInfo, new IResultBack<Boolean>() {
//            @Override
//            public void onResult(Boolean aBoolean) {
//                Log.e(TAG, "加入房间:" + aBoolean);
//                synToService(voiceRoomBean.getRoomId());
//                VoiceRoomApi.getApi().enterSeat(1, null);
//            }
//        });
//        VoiceRoomApi.getApi().joinRoom(voiceRoomBean.getRoomId(), new IResultBack<Boolean>() {
//            @Override
//            public void onResult(Boolean aBoolean) {
//                Log.e(TAG, "加入房间:" + aBoolean);
//                VoiceRoomApi.getApi().enterSeat(1, null);
//                synToService(voiceRoomBean.getRoomId());
//            }
//        });
//    }
//
//    private void leave() {
//        VoiceRoomApi.getApi().leaveRoom(new IResultBack<Boolean>() {
//            @Override
//            public void onResult(Boolean aBoolean) {
//                Log.e(TAG, "加入房间:" + aBoolean);
//                synToService("");
//                PKStateManager.get().unInit();
//            }
//        });
//    }
//
//    private void synToService(String roomId) {
//        //add 进房间标识
//        Map<String, Object> params = new HashMap<>(2);
//        params.put("roomId", roomId);
//        OkApi.get(VRApi.USER_ROOM_CHANGE, params, new WrapperCallBack() {
//            @Override
//            public void onResult(Wrapper result) {
//                if (TextUtils.isEmpty(roomId)) {
//                    onBackCode();
//                }
//            }
//        });
//    }
//}
