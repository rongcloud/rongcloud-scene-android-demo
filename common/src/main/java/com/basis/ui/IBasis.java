package com.basis.ui;

/**
 * @author: BaiCQ
 * @ClassName: IBasis
 * @Description: UI的接口
 */
public interface IBasis {

    /**
     * 设置布局
     */
    int setLayoutId();

    /**
     * 初始化
     */
    void init();

    /**
     * 刷新UI回调接口 供fragment刷数据使用
     *
     * @param cmd
     */
    default void onRefresh(ICmd cmd) {
    }

    /**
     * 网络变化回调
     */
    default void onNetChange() {
    }

    default void onLogout() {
    }

    interface ICmd{
        String getKey();
        <T>T getObject();
    }
    /**
     * 刷新cmd 接口
     */
    class RefreshCmd implements ICmd{
        private String key;
        private Object obj;

        public RefreshCmd(String key, Object obj) {
            this.key = key;
            this.obj = obj;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public <T> T getObject() {
            return (T) obj;
        }
    }
}
