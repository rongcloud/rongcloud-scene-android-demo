package com.basis.utils;

import java.util.concurrent.CountDownLatch;

/**
 * @author: BaiCQ
 * @ClassName: SynUtil
 * @date: 2018/8/17
 * @Description: 异步执行变同步的工具类
 */
public class SynUtil {

    public interface OnExecuteListeren<T> {
        /**
         * 任务执行子线回调
         *
         * @param latch
         */
        T onExecute(CountDownLatch latch);

        /**
         * 任务结束主线程回调
         */
        void onEnd(T t);
    }

    /**
     * 异步任务变同步回调结束
     *
     * @param onExecuteListeren
     */
    public static <T> void synTask(final OnExecuteListeren<T> onExecuteListeren) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                CountDownLatch latch = new CountDownLatch(1);
                T t = null;
                if (null != onExecuteListeren) {
                    t = onExecuteListeren.onExecute(latch);
                }
                try {
                    latch.await();
                } catch (Exception e) {
                }
                if (null != onExecuteListeren) {
                    final T result = t;
                    UIKit.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onExecuteListeren.onEnd(result);
                        }
                    });
                }
            }
        }).start();
    }
}
