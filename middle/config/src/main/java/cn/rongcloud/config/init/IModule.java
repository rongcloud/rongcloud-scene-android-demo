package cn.rongcloud.config.init;

public interface IModule extends OnRegisterMessageTypeListener {
    void onInit();

    void onUnInit();

    @Override
    default void onRegisterMessageType() {
    }
}