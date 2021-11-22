package cn.rong.combusis;

import android.app.Application;

import com.basis.BasisHelper;
import com.basis.net.oklib.net.Page;
import com.basis.net.oklib.wrapper.OkHelper;
import com.basis.net.oklib.wrapper.interfaces.IHeader;
import com.kit.utils.Logger;
import com.rongcloud.common.utils.AccountStore;

import java.util.HashMap;
import java.util.Map;

import cn.rong.combusis.message.RCAllBroadcastMessage;
import cn.rong.combusis.message.RCChatSeatRemove;
import cn.rong.combusis.message.RCChatroomAdmin;
import cn.rong.combusis.message.RCChatroomBarrage;
import cn.rong.combusis.message.RCChatroomEnter;
import cn.rong.combusis.message.RCChatroomFollow;
import cn.rong.combusis.message.RCChatroomGift;
import cn.rong.combusis.message.RCChatroomGiftAll;
import cn.rong.combusis.message.RCChatroomKickOut;
import cn.rong.combusis.message.RCChatroomLeave;
import cn.rong.combusis.message.RCChatroomLike;
import cn.rong.combusis.message.RCChatroomLocationMessage;
import cn.rong.combusis.message.RCChatroomPK;
import cn.rong.combusis.message.RCChatroomPKGift;
import cn.rong.combusis.message.RCChatroomSeats;
import cn.rong.combusis.message.RCChatroomUserBan;
import cn.rong.combusis.message.RCChatroomUserBlock;
import cn.rong.combusis.message.RCChatroomUserUnBan;
import cn.rong.combusis.message.RCChatroomUserUnBlock;
import cn.rong.combusis.message.RCChatroomVoice;
import cn.rong.combusis.message.RCFollowMsg;
import cn.rong.combusis.message.RCRRCloseMessage;
import cn.rongcloud.messager.RCMessager;
import cn.rongcloud.voiceroom.utils.VMLog;
import okhttp3.Headers;

public class ComApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initBasis();
    }

    private void initBasis() {
        BasisHelper.setDefaultPage(new Page() {

            @Override
            public int getFirstIndex() {
                return 1;
            }

            @Override
            public int geSize() {
                return 10;
            }

            @Override
            public String getKeyPage() {
                return "page";
            }

            @Override
            public String getKeySize() {
                return "size";
            }
        });

        OkHelper.get().setHeadCacher(new IHeader() {
            @Override
            public Map<String, String> onAddHeader() {
                Map map = new HashMap<String, String>();
                map.put("Authorization", AccountStore.INSTANCE.getAuthorization());
                map.put("BusinessToken", "vStHYPdrQoImm-7Ur0ks1g");
                return map;
            }

            @Override
            public void onCacheHeader(Headers headers) {

            }
        });

        RCMessager.getInstance().addMessageTypes(
                RCChatroomAdmin.class,
                RCChatroomBarrage.class,
                RCChatroomEnter.class,
                RCChatroomLeave.class,
                RCChatroomFollow.class,
                RCChatroomGift.class,
                RCChatroomGiftAll.class,
                RCChatroomKickOut.class,
                RCChatroomLocationMessage.class,
                RCChatroomSeats.class,
                RCChatroomUserBan.class,
                RCChatroomUserBlock.class,
                RCChatroomUserUnBan.class,
                RCChatroomUserUnBlock.class,
                RCChatroomLike.class,
                RCChatroomVoice.class,
                RCChatroomPK.class,
                RCChatroomPKGift.class,
                RCAllBroadcastMessage.class,
                RCFollowMsg.class,
                RCRRCloseMessage.class,
                RCChatSeatRemove.class
        );
        Logger.setDebug(BuildConfig.DEBUG);
        VMLog.setDebug(BuildConfig.DEBUG);
    }
}


