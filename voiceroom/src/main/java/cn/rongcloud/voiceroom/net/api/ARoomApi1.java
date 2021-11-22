/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroom.net.api;


/**
 * @author gusd
 * @Date 2021/08/06
 */
public interface ARoomApi1 {
    String KEY_ROOMID = "roomId";
    String ROOM_LIST = "/mic/room/list";
    String VMOOM_CREATE = "/mic/room/create";
    String DELETE_ROOM = "/mic/room/" + KEY_ROOMID + "/delete";
    String GET_MEMBERS = "/mic/room/" + KEY_ROOMID + "/members";
    String ROOM_INFO = "/mic/room/" + KEY_ROOMID;
    String MUTE_USER = "/mic/room/gag";
    String GET_MUTE_LIST = "/mic/room/" + KEY_ROOMID + "/gag/members";
    String SET_ROOM_PSWD = "/mic/room/private";
    String SET_ROOM_BG = "/mic/room/background";
    String GET_ADMIN_LIST = "/mic/room/" + KEY_ROOMID + "/manage/list";
    String SET_ADMIN = "/mic/room/manage";
    String ROOM_NAME = "/mic/room/name";
    String ROOM_SEETING = "/mic/room/setting";
    String USERS = "/user/batch";
    String GET_ROOM_SETTING = "/mic/room/" + KEY_ROOMID + "/setting";
}