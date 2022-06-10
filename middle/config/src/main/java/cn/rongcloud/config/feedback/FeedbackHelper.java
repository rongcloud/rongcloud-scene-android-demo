/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.config.feedback;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.utils.SharedPreferUtil;
import com.basis.utils.UIKit;
import com.basis.wapper.IResultBack;
import com.basis.widget.dialog.DialogBuilder;
import com.basis.widget.dialog.IBuilder;
import com.basis.widget.dialog.IDialog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.rongcloud.config.ApiConfig;
import cn.rongcloud.config.R;

public class FeedbackHelper implements IFeedback {
    private final static String TAG = "FeedbackHelper";
    private final static IFeedback helper = new FeedbackHelper();
    private FeedbackListener feedbackListener;


    private FeedbackHelper() {
    }

    public static IFeedback getHelper() {
        return helper;
    }

    /**
     * 注销反馈监听
     */
    public void unregisteObservice() {
        this.feedbackListener = null;
        dismissDialog();
        selectedDowns.clear();
    }

    /**
     * 注册反馈信息提示监听
     *
     * @param activity
     */
    public void registeFeedbackObservice(@NonNull Activity activity) {
        final WeakReference<Activity> con = new WeakReference(activity);
        this.feedbackListener = new FeedbackListener() {
            @Override
            public void onFeedback() {
                if (null != con && null != con.get()) {
                    showFeedbackDialog(con.get());
                }
            }
        };
        //注册后立即检测状态
        if (enableScore() && null != feedbackListener) {
            feedbackListener.onFeedback();
        }
    }

    /**
     * 统计次数 累加
     */
    public void statistics() {
        int last = SharedPreferUtil.get(KEY_TIME, 0);
        SharedPreferUtil.set(KEY_TIME, last + 1);
        if (enableScore() && null != feedbackListener) {
            feedbackListener.onFeedback();
        }
    }

    /**
     * 是否显示统计打分
     *
     * @return
     */
    private boolean enableScore() {
        //未打分 且次数大于limt
//        return !SharedPreferUtil.getBoolean(KEY_SCORE_FLAG) &&
//                SharedPreferUtil.get(KEY_TIME, 0) >= LIMT;
        return
                SharedPreferUtil.get(KEY_TIME, 0) >= LIMT;
    }


    /**
     * 清空统计：取消评价后清空
     */
    private void clearStatistics() {
        SharedPreferUtil.set(KEY_TIME, 0);
    }

    /**
     * 设置已打分：评价后设置
     */
    private void alreadyScore() {
        SharedPreferUtil.set(KEY_SCORE_FLAG, true);
    }

    private void dismissDialog() {
        if (null != centerDialog) {
            centerDialog.dismiss();
        }
        centerDialog = null;
    }

    private IDialog centerDialog;

    private void showFeedbackDialog(Activity activity) {
//        if (null == centerDialog || !centerDialog.enable()) {
//            centerDialog = new VRCenterDialog(activity, new DialogInterface.OnDismissListener() {
//                @Override
//                public void onDismiss(DialogInterface dialog) {
//                    centerDialog = null;
//                }
//            });
//        }
//        centerDialog.replaceContent("请留下您的使用感受吧", R.color.basis_color_primary,
//                "稍后再说", R.color.basis_color_secondary, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dismissDialog();
//                        clearStatistics();
//                    }
//                }, "", -1, null, initScoreView());
//        centerDialog.show();
        if (null == centerDialog) {
            centerDialog = new DialogBuilder(activity)
                    .setTitle("请留下您的使用感受吧")
                    .setEnableSure(false)
                    .setCustomerView(initScoreView())
                    .setCancelBtnStyle("稍后再说", R.color.basis_color_secondary, -1, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dismissDialog();
                            clearStatistics();
                        }
                    }).build();
        }
        centerDialog.show();
    }

    private View initScoreView() {
        View view = UIKit.inflate(R.layout.layout_score_tip);
        View up_selected = view.findViewById(R.id.iv_up_selected);
        View down_selected = view.findViewById(R.id.iv_down_selected);
        view.findViewById(R.id.cl_up).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    up_selected.setVisibility(View.VISIBLE);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    up_selected.setVisibility(View.GONE);
                }
                return false;
            }
        });
        view.findViewById(R.id.cl_down).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    down_selected.setVisibility(View.VISIBLE);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    down_selected.setVisibility(View.GONE);
                }
                return false;
            }
        });
        view.findViewById(R.id.cl_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UmengHelper.get().event(RcUmEvent.AppraisalBanner);
                sendFovLikes();
            }
        });
        view.findViewById(R.id.cl_down).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shwoDownDialog();
            }
        });
        return view;
    }

    /**
     * 点赞
     */
    private void sendFovLikes() {
        reportFeedback(true, "", new IResultBack<Wrapper>() {
            @Override
            public void onResult(Wrapper feedResult) {
                boolean success = null != feedResult && feedResult.getCode() == 10000;
                Toast.makeText(UIKit.getContext(), success ? "点赞成功" : "点赞失败", Toast.LENGTH_LONG).show();
                alreadyScore();
                dismissDialog();
            }
        });
    }

    /**
     * 提交反馈
     */
    private void sendReport() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < selectedDowns.size(); i++) {
            int index = selectedDowns.get(i);
            builder.append(DEF_REASON[index]);
            builder.append(",");
        }
        String releason = "";
        if (builder.length() > 1) {
            releason = builder.substring(0, builder.length() - 1);
        }
        Log.e(TAG, "releason = " + releason);
        reportFeedback(false, releason, new IResultBack<Wrapper>() {
            @Override
            public void onResult(Wrapper feedResult) {
                boolean success = null != feedResult && feedResult.getCode() == 10000;
                Toast.makeText(UIKit.getContext(), success ? "反馈成功" : "反馈失败", Toast.LENGTH_LONG).show();
                alreadyScore();
                // 提交成功后显示推荐活动
                showLastPromotion();
            }
        });
    }

    /**
     * 显示吐槽弹框
     */
    private void shwoDownDialog() {
        if (null == centerDialog) return;
        IBuilder builder = centerDialog.getBuilder();
        builder.setTitle("请问哪个方面需要改进呢")
                .setEnableTitle(true)
                .setCustomerView(initDownView())
                .setCancelBtnStyle("提交反馈", R.color.basis_color_secondary, -1, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (null != selectedDowns && !selectedDowns.isEmpty()) {
                            sendReport();
                        }
                    }
                })
                .setSureBtnStyle("我再想想", R.color.basis_color_secondary, -1, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismissDialog();
                    }
                }).refresh();
//        centerDialog.replaceContent("请问哪个方面需要改进呢？",
//                R.color.basis_color_primary,
//                "提交反馈", R.color.basis_color_secondary, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (null != selectedDowns && !selectedDowns.isEmpty()) {
//                            sendReport();
//                        }
//                    }
//                }, "我再想想", R.color.basis_color_secondary, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dismissDialog();
//                    }
//                }, initDownView());
    }

    private final List<Integer> selectedDowns = new ArrayList();

    private View initDownView() {
        View view = UIKit.inflate(R.layout.layout_score_down);
        View[] views = new View[4];
        View[] selectVs = new View[4];
        views[0] = view.findViewById(R.id.cl_first);
        views[1] = view.findViewById(R.id.cl_second);
        views[2] = view.findViewById(R.id.cl_third);
        views[3] = view.findViewById(R.id.cl_fourth);
        // 右下角图标
        selectVs[0] = view.findViewById(R.id.iv_select_first);
        selectVs[1] = view.findViewById(R.id.iv_select_second);
        selectVs[2] = view.findViewById(R.id.iv_select_third);
        selectVs[3] = view.findViewById(R.id.iv_select_fourth);

        for (int i = 0; i < 4; i++) {
            final int index = i;
            views[index].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean selected = selectedDowns.contains(index);
                    if (selected) {
                        selectedDowns.remove((Integer) index);
                    } else {
                        selectedDowns.add(index);
                    }
                    v.setSelected(!selected);
                    selectVs[index].setVisibility(!selected ? View.VISIBLE : View.GONE);
                }
            });
        }
        return view;
    }

    /**
     * 显示最新活动
     */
    private void showLastPromotion() {
        if (null == centerDialog) return;
//        centerDialog.replaceContent("融云最近活动，了解一下？",
//                R.color.basis_color_primary,
//                "我想了解", R.color.basis_color_secondary, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        jumpPromotionPage();
//                    }
//                }, "我再想想", R.color.basis_color_primary, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dismissDialog();
//                    }
//                }, centerDialog.getLayoutInflater().inflate(R.layout.layout_promotion, null));
        IBuilder builder = centerDialog.getBuilder();
        builder.setTitle("融云最近活动，了解一下？")
                .setEnableTitle(true)
                .setCustomerView(initDownView())
                .setCancelBtnStyle("我想了解", R.color.basis_color_secondary, -1, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        jumpPromotionPage();
                    }
                })
                .setSureBtnStyle("我再想想", R.color.basis_color_primary, -1, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismissDialog();
                    }
                }).refresh();
    }

    private void jumpPromotionPage() {
        if (null != centerDialog) {
            Intent intent = new Intent("io.rong.intent.action.commonwebpage");
            intent.putExtra("key_url", "https://m.rongcloud.cn/activity/rtc20");
            intent.putExtra("key_basic", "最近活动");
            UIKit.getContext().startActivity(intent);
        }
        dismissDialog();
    }

    /**
     * 反馈信息
     *
     * @param goodFeedback 是否是点赞
     * @param reason       原因
     * @param resultBack
     */
    private void reportFeedback(boolean goodFeedback, String reason, IResultBack<Wrapper> resultBack) {
        Map<String, Object> params = new HashMap<>(4);
        params.put("isGoodFeedback", goodFeedback);
        params.put("reason", reason);
        String url = ApiConfig.HOST + "feedback/create";
        OkApi.post(url, params, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                Log.e(TAG, "code = " + result.getCode());
                resultBack.onResult(result);
            }

            @Override
            public void onError(int code, String msg) {
                resultBack.onResult(null);
            }
        });
    }
}
