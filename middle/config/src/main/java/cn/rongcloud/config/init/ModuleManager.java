package cn.rongcloud.config.init;

import com.basis.utils.Logger;

import java.util.ArrayList;
import java.util.List;

import io.rong.imlib.model.Message;

/**
 * 模块初始化
 */
public class ModuleManager implements OnRegisterMessageTypeListener {
    private final static ModuleManager _manager = new ModuleManager();

    private List<BaseModule> modules = new ArrayList<>(8);
    private boolean initialize = false;
    private boolean registerMessageType = false;

    private ModuleManager() {
        modules.add(new BaseModule(new OKModule()));
        modules.add(new BaseModule(new ConnectModule(this)));
    }

    public static ModuleManager manager() {
        return _manager;
    }

    public void onInit() {
        handleInit();
        initialize = true;
    }

    void handleInit() {
        for (BaseModule m : modules) {
            if (!m.initialize) {
                m.onInit();
            }
        }
    }

    public void register(IModule... modules) {
        if (null == modules) return;
        for (IModule m : modules) {
            this.modules.add(new BaseModule(m));
        }
        if (initialize) {
            handleInit();
        }
        if (registerMessageType) {
            for (IModule m : modules) {
                m.onRegisterMessageType();
            }
        }
    }

    public void unregister() {
        for (BaseModule m : modules) {
            if (m.initialize) {
                m.onUnInit();
            }
        }
    }

    @Override
    public void onRegisterMessageType() {
        for (IModule m : modules) {
            m.onRegisterMessageType();
        }
        registerMessageType = true;
    }

    @Override
    public void onReceivedMessage(Message message) {
        for (IModule m : modules) {
            m.onReceivedMessage(message);
        }
    }

    public static class BaseModule implements IModule {
        private IModule module;
        private boolean initialize = false;

        BaseModule(IModule module) {
            this.module = module;
        }

        @Override
        public void onInit() {
            initialize = true;
            Logger.e("ModuleManager","onInit: "+module.getClass().getSimpleName());
            if (null != module) module.onInit();
        }

        @Override
        public void onUnInit() {
            initialize = false;
            if (null != module) module.onUnInit();
        }

        @Override
        public void onRegisterMessageType() {
            if (null != module) module.onRegisterMessageType();
        }
    }
}
