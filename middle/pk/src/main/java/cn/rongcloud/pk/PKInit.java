package cn.rongcloud.pk;

import java.util.ArrayList;

import cn.rongcloud.config.init.IModule;
import cn.rongcloud.pk.message.RCChatroomPK;
import cn.rongcloud.pk.message.RCChatroomPKGift;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.MessageContent;

/**
 * @author gyn
 * @date 2022/2/17
 */
public class PKInit implements IModule {
    @Override
    public void onInit() {

    }

    @Override
    public void onUnInit() {

    }

    @Override
    public void onRegisterMessageType() {
        RongIMClient.registerMessageType(new ArrayList<Class<? extends MessageContent>>() {
            {
                add(RCChatroomPK.class);
                add(RCChatroomPKGift.class);
            }
        });
    }
}
