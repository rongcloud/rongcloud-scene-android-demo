package cn.rongcloud.config.feedback;

import android.app.Activity;

import androidx.annotation.NonNull;

public interface IFeedback {
    String KEY_TIME = "score_time";
    String KEY_SCORE_FLAG = "score_falg";
    int LIMT = 3;
    String[] DEF_REASON = new String[]{
            "场景功能",
            "音质质量",
            "使用流程",
            "交互体验"
    };

    interface FeedbackListener {
        void onFeedback();
    }

    /**
     * 注销反馈监听
     */
    void unregisteObservice();

    /**
     * 注册反馈信息提示监听
     *
     * @param activity
     */
    void registeFeedbackObservice(@NonNull Activity activity);


    /**
     * 统计体验次数 累加
     */
    void statistics();
}
