package cn.rc.community;

import java.util.Arrays;

import cn.rc.community.conversion.RegisterHelper;
import cn.rc.community.message.sysmsg.ChannelNoticeMsg;
import cn.rc.community.message.sysmsg.CommunityChangeMsg;
import cn.rc.community.message.sysmsg.CommunityDeleteMsg;
import cn.rc.community.message.sysmsg.CommunitySysNoticeMsg;
import cn.rc.community.message.sysmsg.UserUpdateMsg;
import cn.rongcloud.config.init.IModule;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.message.SightMessage;

public class CommunityModule implements IModule {
    @Override
    public void onInit() {
        RegisterHelper.autoRegisterCovert();
    }

    @Override
    public void onUnInit() {

    }

    @Override
    public void onRegisterMessageType() {
        RongIMClient.registerMessageType(Arrays.asList(
                CommunityChangeMsg.class,
                CommunitySysNoticeMsg.class,
                CommunityDeleteMsg.class,
                ChannelNoticeMsg.class,
                SightMessage.class,
                UserUpdateMsg.class
        ));
    }

    @Override
    public void onReceivedMessage(Message message) {

    }
}
