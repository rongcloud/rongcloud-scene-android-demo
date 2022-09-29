package cn.rongcloud.pk.widget;

import android.content.Context;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.basis.adapter.RcyHolder;
import com.basis.adapter.RcySAdapter;
import com.basis.utils.ImageLoader;
import com.basis.utils.Logger;
import com.basis.utils.UIKit;
import com.basis.wapper.IResultBack;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.config.provider.user.UserProvider;
import cn.rongcloud.pk.R;
import cn.rongcloud.pk.api.PKListener;
import io.rong.imlib.model.UserInfo;

public class PKView extends LinearLayout implements IPK {
    private final static String TAG = "PKView";
    private final static int MAX = 180;

    public PKView(Context context) {
        this(context, null, -1);
    }

    public PKView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public PKView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private PKProgressBar pkProgressBar;
    private RecyclerView rvSender, rvReceiver;
    private PKAdapter lAdapter, rAdapter;
    private Timer timer;
    private TextView tvTime;
    private ImageView ivVs;
    //pk者信息
    private ImageView ivLeft, ivRight, ivMute;
    private TextView tvLeft, tvRight;
    // pk结果
    private ImageView ivLeftResultTop, ivLeftResultDown, ivRightResultTop, ivRightResultDown;
    private int leftValue, rightValue;
    private PKListener pkListener;

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_pk, this);
        ivVs = view.findViewById(R.id.iv_vs);
        tvTime = view.findViewById(R.id.tv_time);
        tvLeft = view.findViewById(R.id.tv_sender_name);
        tvRight = view.findViewById(R.id.tv_receiver_name);
        ivLeft = view.findViewById(R.id.iv_left);
        ivRight = view.findViewById(R.id.iv_right);
        ivMute = view.findViewById(R.id.iv_right_mute);
        // pk结果
        ivLeftResultTop = view.findViewById(R.id.iv_left_result_up);
        ivLeftResultDown = view.findViewById(R.id.iv_left_result_down);
        ivRightResultTop = view.findViewById(R.id.iv_right_result_up);
        ivRightResultDown = view.findViewById(R.id.iv_right_result_down);
        ivLeftResultTop.setVisibility(GONE);
        ivLeftResultDown.setVisibility(GONE);
        ivRightResultTop.setVisibility(GONE);
        ivRightResultDown.setVisibility(GONE);
        ivVs.setImageResource(R.drawable.ic_vs);
        // sb pk
        pkProgressBar = view.findViewById(R.id.pk_sb);
        pkProgressBar.setBarResource(R.drawable.ic_sb_pk);
        pkProgressBar.setPKValue(0, 0);
        rvSender = view.findViewById(R.id.rv_sender);
        rvReceiver = view.findViewById(R.id.rv_receiver);
        lAdapter = new PKAdapter(context, false);
        rAdapter = new PKAdapter(context, true);
        rvSender.setAdapter(lAdapter);
        rvReceiver.setAdapter(rAdapter);
        rvSender.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        });
        rvReceiver.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, true) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        });
        //默认unmute
        ivMute.setSelected(false);
    }

    @Override
    public int getPKResult() {
        if (leftValue == rightValue) {
            return 0;
        } else if (leftValue > rightValue) {
            return 1;
        } else {
            return -1;
        }
    }

    /**
     * 刷新pk结果
     */
    void refreshPkResult() {
        if (leftValue == rightValue) {// 平局
            ivVs.setImageResource(R.drawable.ic_tie);
        } else {
            ivVs.setImageResource(R.drawable.ic_vs);
            if (leftValue > rightValue) {// left win
                // 左侧
                ivLeft.setBackgroundResource(R.drawable.shape_yellow_stroke);
                ivLeftResultTop.setVisibility(VISIBLE);
                ivLeftResultDown.setVisibility(VISIBLE);
                ivLeftResultDown.setImageResource(R.drawable.ic_pk_win);
                // 右侧
                ivRight.setBackgroundResource(R.drawable.shape_grad_stroke);
                ivRightResultTop.setVisibility(GONE);
                ivRightResultDown.setVisibility(VISIBLE);
                ivRightResultDown.setImageResource(R.drawable.ic_pk_fail);
            } else { // right win
                ivRight.setBackgroundResource(R.drawable.shape_yellow_stroke);
                ivRightResultTop.setVisibility(VISIBLE);
                ivRightResultDown.setVisibility(VISIBLE);
                ivRightResultDown.setImageResource(R.drawable.ic_pk_win);

                ivLeft.setBackgroundResource(R.drawable.shape_grad_stroke);
                ivLeftResultTop.setVisibility(GONE);
                ivLeftResultDown.setVisibility(VISIBLE);
                ivLeftResultDown.setImageResource(R.drawable.ic_pk_fail);
            }
        }
    }

    @Override
    public void reset(boolean isRoomOwner) {
        ivMute.setSelected(false);
        UIKit.setVisible(ivMute, isRoomOwner);
    }

    @Override
    public void setPKUserInfo(String localId, String pkId) {
        UserProvider.provider().getAsyn(localId, new IResultBack<UserInfo>() {
            @Override
            public void onResult(UserInfo userInfo) {
                if (null != userInfo) {
                    tvLeft.setText(userInfo.getName());
                    ImageLoader.loadUri(ivLeft, userInfo.getPortraitUri(), R.drawable.default_portrait, ImageLoader.Size.S_200);
                }
            }
        });
        UserProvider.provider().getAsyn(pkId, new IResultBack<UserInfo>() {
            @Override
            public void onResult(UserInfo userInfo) {
                if (null != userInfo) {
                    tvRight.setText(userInfo.getName());
                    ImageLoader.loadUri(ivRight, userInfo.getPortraitUri(), R.drawable.default_portrait, ImageLoader.Size.S_200);
                }
            }
        });
    }

    @Override
    public void setPKListener(PKListener pkListener) {
        this.pkListener = pkListener;
    }


    @Override
    public synchronized void pkStart(long timeDiff, OnTimerEndListener listener) {
        if (null != timer) {
            timer.cancel();
            timer = null;
        }
        // 开启 pk记时
        timer = new Timer(tvTime, timeDiff, listener);
        timer.start();

    }

    @Override
    public synchronized void pkPunish(long timeDiff, OnTimerEndListener listener) {
        if (null != timer) {
            timer.cancel();
            timer = null;
        }
        // 开启 惩罚记时  移除开启阶段的180s的差值
        // 已调整 惩罚时间未起点
        timer = new Timer(tvTime, timeDiff, listener);
        timer.start();
        refreshPkResult();
    }

    @Override
    public synchronized void pkStop() {
        ivLeftResultTop.setVisibility(GONE);
        ivLeftResultDown.setVisibility(GONE);
        ivRightResultTop.setVisibility(GONE);
        ivRightResultDown.setVisibility(GONE);
        ivVs.setImageResource(R.drawable.ic_vs);
        ivLeft.setBackgroundResource(R.drawable.shape_pink_stroke);
        ivRight.setBackgroundResource(R.drawable.shape_pink_stroke);
        if (null != timer) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void setPKScore(int left, int right) {
        leftValue = left;
        rightValue = right;
        pkProgressBar.setPKValue(leftValue, rightValue);
    }

    @Override
    public void setGiftSenderRank(List<String> lefts, List<String> rights) {
        int ls = null != lefts ? lefts.size() : 0;
        List<RankInfo> llis = new ArrayList<>();
        for (int i = 0; i < ls; i++) {
            llis.add(new RankInfo(lefts.get(i), i + 1));
        }
        int rs = null != rights ? rights.size() : 0;
        List<RankInfo> rlis = new ArrayList<>();
        for (int i = 0; i < rs; i++) {
            rlis.add(new RankInfo(rights.get(i), i + 1));
        }
        lAdapter.setData(llis, true);
        rAdapter.setData(rlis, true);
    }

    @Override
    public void setMute(boolean isMute) {
        ivMute.setSelected(isMute);
    }

    @Override
    public void setClickMuteListener(OnClickListener l) {
        ivMute.setOnClickListener(l);
    }


    public static class RankInfo {
        private String portrait;
        private int rank = 0;

        public RankInfo(String portrait, int rank) {
            this.portrait = portrait;
            this.rank = rank;
        }
    }

    public static class PKAdapter extends RcySAdapter<RankInfo, RcyHolder> {
        private boolean receiveFlag;
        private final static int COUNT = 3;

        public PKAdapter(Context context, boolean receiveFlag) {
            super(context, R.layout.layout_pk_view_member);
            this.receiveFlag = receiveFlag;
        }

        @Override
        public synchronized void setData(List<RankInfo> list, boolean refresh) {
            int size = null == list ? 0 : list.size();
            // 保证集合数量
            List<RankInfo> temps = new ArrayList();
            if (size != COUNT) {
                for (int i = 0; i < COUNT; i++) {
                    if (i < size) {
                        temps.add(list.get(i));
                    } else {
                        temps.add(new RankInfo("", i + 1));
                    }
                }
            } else {
                temps.addAll(list);
            }
            // 翻转集合
            List<RankInfo> datas = new ArrayList();
            for (int i = COUNT - 1; i > -1; i--) {
                datas.add(temps.get(i));
            }
            super.setData(datas, refresh);
        }

        @Override
        public void convert(RcyHolder holder, RankInfo info, int position) {
            holder.setText(R.id.tv_count, info.rank + "");
            ImageView imageView = holder.getView(R.id.iv_gift);
            if (!TextUtils.isEmpty(info.portrait)) {
                ImageLoader.loadUrl(imageView, info.portrait, R.drawable.ic_pk_none, ImageLoader.Size.S_100);
            } else {
                imageView.setImageResource(R.drawable.ic_pk_none);
            }
            holder.setSelected(R.id.tv_count, receiveFlag);
        }
    }

    public class Timer extends CountDownTimer {
        private WeakReference<TextView> reference;
        private OnTimerEndListener listener;

        Timer(TextView textView, long timeDiff, OnTimerEndListener listener) {
            super(timeDiff < 0 ? MAX * 1000 : MAX * 1000 - timeDiff, 500);
            this.reference = new WeakReference<>(textView);
            this.listener = listener;
        }

        @Override
        public void onTick(long l) {
//            Logger.e("Timer", "l = " + l);
            if (null != reference && reference.get() != null) {
                reference.get().setText(msToShow(l));
            }
        }

        @Override
        public void onFinish() {
            Logger.e("Timer", "onFinish");
            if (null != listener) {
                listener.onTimerEnd();
            }
        }

        private String msToShow(long ms) {
            long min = ms / 1000 / 60;
            long s = ms % (1000 * 60) / 1000;
            return "0" + min + ":" + (s < 10 ? "0" + s : "" + s);
        }
    }
}
