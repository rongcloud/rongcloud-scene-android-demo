package cn.rc.demo.fragment;

import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.airbnb.lottie.LottieAnimationView;
import com.basis.utils.GsonUtil;
import com.basis.utils.KToast;
import com.basis.utils.Logger;
import com.basis.utils.ResUtil;
import com.basis.utils.UiUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.rc.demo.R;
import cn.rongcloud.config.AppConfig;
import cn.rongcloud.config.feedback.RcEvent;
import cn.rongcloud.config.feedback.SensorsUtil;
import cn.rongcloud.config.router.RouterPath;

public class ModuleHelper {
    private final static int width = 334;
    private final static int height = 182;
    private final static int divider = 24;
    private final static int tip = 100;
    private final static int total = width * 2 + divider;

    public final static Map<String, Module> modules = new HashMap();

    static {
        //视频直播
        modules.put(AppConfig.MODE_LIVE,
                new Module(RcEvent.LiveRoom, "live_room.json",
                        R.drawable.ic_pro, new Frame(0, 0, total, height * 2 + divider),
                        new TextFrame(15, 10, 20, ResUtil.getColor(R.color.whiteTextColor), Typeface.BOLD, R.string.live_video_call),
                        new TextFrame(15, 40, 14, ResUtil.getColor(R.color.whiteTextColor), Typeface.NORMAL, R.string.live_video_des),
                        RouterPath.ROUTER_LIVE_LIST));
        //语聊房
        modules.put(AppConfig.MODE_VOICE,
                new Module(RcEvent.VoiceRoom, "voice_room.json",
                        R.drawable.ic_pro, new Frame(0, height * 2 + divider * 2, width, height * 2 + divider),
                        new TextFrame(15, 8, 20, ResUtil.getColor(R.color.whiteTextColor), Typeface.BOLD, R.string.voice_room),
                        new TextFrame(15, 38, 14, ResUtil.getColor(R.color.whiteTextColor), Typeface.NORMAL, R.string.voice_room_des),
                        RouterPath.ROUTER_VOICE_LIST));
        //语音通话
        modules.put(AppConfig.MODE_CALL + "audio", new Module(RcEvent.AudioCall, "call.json", -1,
                new Frame(width + divider, height * 2 + divider * 2, width, height),
                new TextFrame(12, 10, 16, ResUtil.getColor(R.color.whiteTextColor), Typeface.BOLD, R.string.audio_call),
                new TextFrame(12, 33, 10, ResUtil.getColor(R.color.whiteTextColor), Typeface.NORMAL, R.string.audio_call_des),
                RouterPath.ROUTER_CALL));
        //视频通话
        modules.put(AppConfig.MODE_CALL + "video",
                new Module(RcEvent.VideoCall, "video.json", -1,
                        new Frame(width + divider, height * 3 + divider * 3, width, height),
                        new TextFrame(12, 10, 16, ResUtil.getColor(R.color.whiteTextColor), Typeface.BOLD, R.string.video_call),
                        new TextFrame(12, 33, 10, ResUtil.getColor(R.color.whiteTextColor), Typeface.NORMAL, R.string.video_call_des),
                        RouterPath.ROUTER_CALL));
        //语音电台
        modules.put(AppConfig.MODE_RADIO,
                new Module(RcEvent.RadioRoom, "radio_room.json", -1, new Frame(0, height * 4 + divider * 4, width, height),
                        new TextFrame(12, 10, 16, ResUtil.getColor(R.color.whiteTextColor), Typeface.BOLD, R.string.radio_room),
                        new TextFrame(12, 33, 10, ResUtil.getColor(R.color.whiteTextColor), Typeface.NORMAL, R.string.radio_room_des),
                        RouterPath.ROUTER_RADIO_LIST));
        //未开发功能
        modules.put("SOON",
                new Module(null, "comingsoon.json", new Frame(0, height * 5 + divider * 5, width, height),
                        new TextFrame(12, 10, 16, ResUtil.getColor(R.color.whiteTextColor), Typeface.BOLD, R.string.coming_soon_room),
                        new TextFrame(12, 33, 10, ResUtil.getColor(R.color.whiteTextColor), Typeface.NORMAL, R.string.coming_soon_room_des),
                        RouterPath.ROUTER_RADIO_LIST));
        //游戏房
        modules.put(AppConfig.MODE_GAME,
                new Module(RcEvent.GameRoom, "game.json", new Frame(width + divider, height * 4 + divider * 4, width, height * 2 + divider),
                        new TextFrame(15, 10, 20, ResUtil.getColor(R.color.whiteTextColor), Typeface.BOLD, R.string.game_room),
                        new TextFrame(15, 40, 14, ResUtil.getColor(R.color.whiteTextColor), Typeface.NORMAL, R.string.game_room_des),
                        RouterPath.ROUTER_GAME_LIST));
    }

    static class Frame {
        int left;
        int top;
        int width;
        int height;

        Frame(int left, int top, int width, int height) {
            this.left = left;
            this.top = top;
            this.width = width;
            this.height = height;
        }
    }

    static class TextFrame {
        int left;
        int top;
        int textSize;
        int textColor;
        int textStyle;
        int text;

        public TextFrame(int left, int top, int textSize, int textColor, int textStyle, int text) {
            this.left = left;
            this.top = top;
            this.textSize = textSize;
            this.textColor = textColor;
            this.textStyle = textStyle;
            this.text = text;
        }
    }

    public static class Module {
        int tip;
        public String router;
        public RcEvent event;
        public Frame frame;
        public String fileName;//动画的链接名称
        public TextFrame title;
        public TextFrame des;

        public Module(RcEvent event, String fileName, int tip, Frame frame, TextFrame title, TextFrame des, String router) {
            this.event = event;
            this.tip = tip;
            this.frame = frame;
            this.router = router;
            this.fileName = fileName;
            this.title = title;
            this.des = des;
        }

        public Module(RcEvent event, String fileName, Frame frame, TextFrame title, TextFrame des, String router) {
            this(event, fileName, -1, frame, title, des, router);
        }
    }


    static List<Module> getModules() {
        List<Module> modes = new ArrayList<>(8);
        List<String> md1 = AppConfig.get().getModes();
        Logger.e("md1 : " + GsonUtil.obj2Json(md1));
        boolean call = md1.remove(AppConfig.MODE_CALL);
        for (String m : md1) {
            modes.add(modules.get(m));
        }
        if (call) {// call 依赖 对应audio 和 video
            modes.add(modules.get(AppConfig.MODE_CALL + "video"));
            modes.add(modules.get(AppConfig.MODE_CALL + "audio"));
        }
        //soon
        modes.add(modules.get("SOON"));
        return modes;
    }

    public static void inflateView(FrameLayout containt, OnModuleClickListener listener) {
        containt.post(new Runnable() {
            @Override
            public void run() {
                int w = containt.getMeasuredWidth();
                Logger.e("inflateView", "width = " + w);
                List<Module> modes = getModules();
                int count = modes.size();
                for (int i = 0; i < count; i++) {
                    Module m = modes.get(i);
                    CardView cardView = new CardView(containt.getContext());
                    FrameLayout.LayoutParams lp = layoutParams(m.frame, w);
                    cardView.setRadius(UiUtils.dp2px(7));
                    if (!TextUtils.isEmpty(m.fileName)) {
                        //有动画，添加动画
                        LottieAnimationView lottieAnimationView = new LottieAnimationView(containt.getContext());
                        lottieAnimationView.setImageAssetsFolder("images/");
                        lottieAnimationView.setAnimation(m.fileName);
                        lottieAnimationView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        lottieAnimationView.loop(true);
                        lottieAnimationView.playAnimation();
                        cardView.addView(lottieAnimationView);
                    }
                    if (m.tip != -1) {
                        //pro标签
                        ImageView news = new ImageView(containt.getContext());
                        news.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        FrameLayout.LayoutParams nl = new FrameLayout.LayoutParams(w * tip / total, w * tip / total);
                        nl.gravity = Gravity.TOP | Gravity.RIGHT;
                        nl.setMargins(0, 0, -10, 0);
                        news.setImageResource(m.tip);
                        cardView.addView(news, nl);
                    }
                    //标题
                    TextFrame title = m.title;
                    TextView textView = new TextView(containt.getContext());
                    textView.setText(title.text);
                    textView.setTextSize(title.textSize);
                    textView.setTextColor(title.textColor);
                    textView.setTypeface(Typeface.defaultFromStyle(title.textStyle));
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.topMargin = UiUtils.dp2px(title.top);
                    layoutParams.leftMargin = UiUtils.dp2px(title.left);
                    cardView.addView(textView, layoutParams);

                    //描述
                    TextFrame des = m.des;
                    TextView desTextView = new TextView(containt.getContext());
                    desTextView.setText(des.text);
                    desTextView.setTextSize(des.textSize);
                    desTextView.setTextColor(des.textColor);
                    desTextView.setTypeface(Typeface.defaultFromStyle(des.textStyle));
                    FrameLayout.LayoutParams layoutParams1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams1.topMargin = UiUtils.dp2px(des.top);
                    layoutParams1.leftMargin = UiUtils.dp2px(des.left);
                    cardView.addView(desTextView, layoutParams1);

                    if (m.event == RcEvent.LiveRoom) {
                        ImageView imageView = new ImageView(containt.getContext());
                        imageView.setImageResource(R.drawable.live_room_count);
                        imageView.setAdjustViewBounds(true);
                        FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(UiUtils.dp2px(162), UiUtils.dp2px(33));
                        layoutParams2.topMargin = UiUtils.dp2px(141);
                        layoutParams2.leftMargin = UiUtils.dp2px(11);
                        cardView.addView(imageView, layoutParams2);
                    }

                    containt.addView(cardView, lp);
                    cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SensorsUtil.instance().functionModuleViewClick(m.event);
                            if (null == m.event) {
                                KToast.show("新功能正在打磨中...");
                                return;
                            }
                            listener.onModuleClick(m);
                        }
                    });

                }
            }
        });
    }

    /**
     * @param frame
     * @param width 实际宽度
     * @return
     */
    static FrameLayout.LayoutParams layoutParams(Frame frame, int width) {
        int w = width * frame.width / total;
        int h = width * frame.height / total;
        int l = width * frame.left / total;
        int t = width * frame.top / total;
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(w, h);
        lp.setMargins(l, t, 0, 0);
        return lp;
    }

    public interface OnModuleClickListener {
        void onModuleClick(Module module);
    }
}
