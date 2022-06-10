package cn.rongcloud.roomkit;

import androidx.annotation.NonNull;

import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiProvider;
import com.vanniktech.emoji.emoji.EmojiCategory;
import com.vanniktech.emoji.ios.category.ActivitiesCategory;
import com.vanniktech.emoji.ios.category.AnimalsAndNatureCategory;
import com.vanniktech.emoji.ios.category.FoodAndDrinkCategory;
import com.vanniktech.emoji.ios.category.ObjectsCategory;
import com.vanniktech.emoji.ios.category.SmileysAndPeopleCategory;
import com.vanniktech.emoji.ios.category.SymbolsCategory;
import com.vanniktech.emoji.ios.category.TravelAndPlacesCategory;

import java.util.ArrayList;

import cn.rongcloud.config.init.IModule;
import cn.rongcloud.roomkit.message.RCAllBroadcastMessage;
import cn.rongcloud.roomkit.message.RCChatSeatRemove;
import cn.rongcloud.roomkit.message.RCChatroomAdmin;
import cn.rongcloud.roomkit.message.RCChatroomBarrage;
import cn.rongcloud.roomkit.message.RCChatroomEnter;
import cn.rongcloud.roomkit.message.RCChatroomFollow;
import cn.rongcloud.roomkit.message.RCChatroomGift;
import cn.rongcloud.roomkit.message.RCChatroomGiftAll;
import cn.rongcloud.roomkit.message.RCChatroomKickOut;
import cn.rongcloud.roomkit.message.RCChatroomLeave;
import cn.rongcloud.roomkit.message.RCChatroomLike;
import cn.rongcloud.roomkit.message.RCChatroomLocationMessage;
import cn.rongcloud.roomkit.message.RCChatroomSeats;
import cn.rongcloud.roomkit.message.RCChatroomUserBan;
import cn.rongcloud.roomkit.message.RCChatroomUserBlock;
import cn.rongcloud.roomkit.message.RCChatroomUserUnBan;
import cn.rongcloud.roomkit.message.RCChatroomUserUnBlock;
import cn.rongcloud.roomkit.message.RCChatroomVoice;
import cn.rongcloud.roomkit.message.RCFollowMsg;
import cn.rongcloud.roomkit.message.RCRRCloseMessage;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.MessageContent;

/**
 * @author gyn
 * @date 2022/2/16
 */
public class RoomKitInit implements IModule {

    @Override
    public void onInit() {
        EmojiManager.install(MyEmojiProvider.getEmojiProviderInstance());
        // // 初始化kit
        // RCSceneKitEngine.getInstance().initWithAppKey(UIKit.getContext(), "");
        // // 初始化HiFive
        // HFOpenApi.registerApp(this, "");
        // HFOpenApi.setVersion("V4.1.2");
    }

    @Override
    public void onUnInit() {

    }

    @Override
    public void onRegisterMessageType() {
        RongIMClient.registerMessageType(new ArrayList<Class<? extends MessageContent>>() {
            {
                add(RCAllBroadcastMessage.class);
                add(RCChatroomAdmin.class);
                add(RCChatroomBarrage.class);
                add(RCChatroomEnter.class);
                add(RCChatroomFollow.class);
                add(RCChatroomGift.class);
                add(RCChatroomGiftAll.class);
                add(RCChatroomKickOut.class);
                add(RCChatroomLeave.class);
                add(RCChatroomLike.class);
                add(RCChatroomLocationMessage.class);
                add(RCChatroomSeats.class);
                add(RCChatroomUserBan.class);
                add(RCChatroomUserBlock.class);
                add(RCChatroomUserUnBan.class);
                add(RCChatroomUserUnBlock.class);
                add(RCChatroomVoice.class);
                add(RCChatSeatRemove.class);
                add(RCFollowMsg.class);
                add(RCRRCloseMessage.class);
            }
        });
    }

    public static class MyEmojiProvider implements EmojiProvider {

        private static MyEmojiProvider myEmojiProvider = new MyEmojiProvider();

        public static MyEmojiProvider getEmojiProviderInstance() {
            return myEmojiProvider;
        }

        @NonNull
        @Override
        public EmojiCategory[] getCategories() {
            return new EmojiCategory[]{
                    new SmileysAndPeopleCategory(),
                    new AnimalsAndNatureCategory(),
                    new FoodAndDrinkCategory(),
                    new ActivitiesCategory(),
                    new TravelAndPlacesCategory(),
                    new ObjectsCategory(),
                    new SymbolsCategory()
            };
        }
    }
}
