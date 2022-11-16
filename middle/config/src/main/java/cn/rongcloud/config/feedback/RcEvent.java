package cn.rongcloud.config.feedback;

public enum RcEvent {
    NONE(""),
    VoiceRoom("语聊房"),//语聊房
    RadioRoom("语音电台"),//电台
    LiveRoom("视频直播"),//直播房
    VideoCall("视频通话"),// 视频
    AudioCall("语音通话"),// 语音
    AppraisalBanner("点赞"),//点个赞
    SettingBanner("设置-banner"),// 设置 banner
    SettingPackage("设置-套餐"),// 设置 套餐
    SettingDemoDownload("设置-demo下载"),// 设置 demo下载
    SettingCS("设置-在线客服"),// 设置 在线客服
    SettingAboutUs("设置-关于我们"), // 设置 关于我们
    SettingCallCM("设置-专属客户经理"), // 专属客户经理
    GameRoom("游戏房");

    private String name;

    RcEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}