package cn.rc.demo.fragment;

import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.basis.utils.GsonUtil;
import com.basis.utils.KToast;
import com.basis.utils.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.rc.demo.R;
import cn.rongcloud.config.AppConfig;
import cn.rongcloud.config.feedback.RcUmEvent;
import cn.rongcloud.config.router.RouterPath;

public class ModuleHelper {
    private final static int width = 167;
    private final static int height = 103;
    private final static int divider = 15;
    private final static int tip = 54;
    private final static int total = width + divider + width;

    public final static Map<String, Module> modules = new HashMap();

    static {
        modules.put(AppConfig.MODE_VOICE,
                new Module(RcUmEvent.VoiceRoom, R.drawable.ic_voice, R.drawable.ic_pro, new Frame(0, 0, width, height + height + divider), RouterPath.ROUTER_VOICE_LIST));
        modules.put(AppConfig.MODE_CALL + "audio",
                new Module(RcUmEvent.AudioCall, R.drawable.ic_audio_call, new Frame(width + divider, 0, width, height), RouterPath.ROUTER_CALL));
        modules.put(AppConfig.MODE_CALL + "video",
                new Module(RcUmEvent.VideoCall, R.drawable.ic_video_call, new Frame(width + divider, height + divider, width, height), RouterPath.ROUTER_CALL));
        modules.put(AppConfig.MODE_LIVE,
                new Module(RcUmEvent.LiveRoom, R.drawable.ic_live, R.drawable.ic_new, new Frame(0, height * 2 + divider * 2, total, height * 2), RouterPath.ROUTER_LIVE_LIST));
        modules.put(AppConfig.MODE_RADIO,
                new Module(RcUmEvent.RadioRoom, R.drawable.ic_radio, new Frame(0, height * 4 + divider * 3, width, height), RouterPath.ROUTER_RADIO_LIST));
        modules.put("SOON",
                new Module(null, R.drawable.ic_comming_soon, new Frame(width + divider, height * 4 + divider * 3, width, height), RouterPath.ROUTER_RADIO_LIST));
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

    public static class Module {
        int icon;
        int tip;
        public String router;
        public RcUmEvent event;
        public Frame frame;

        public Module(RcUmEvent event, int icon, int tip, Frame frame, String router) {
            this.event = event;
            this.icon = icon;
            this.tip = tip;
            this.frame = frame;
            this.router = router;
        }

        public Module(RcUmEvent event, int icon, Frame frame, String router) {
            this(event, icon, -1, frame, router);
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
                    ImageView image = new ImageView(containt.getContext());
                    FrameLayout.LayoutParams lp = layoutParams(m, w);
                    if (m.tip != -1) {
                        FrameLayout view = new FrameLayout(containt.getContext());
                        view.addView(image, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                        containt.addView(view, lp);

                        ImageView news = new ImageView(containt.getContext());
                        FrameLayout.LayoutParams nl = new FrameLayout.LayoutParams(w * tip / total, w * tip / total);
                        nl.gravity = Gravity.TOP | Gravity.RIGHT;
                        nl.setMargins(0, 0, 0, 0);
                        news.setImageResource(m.tip);
                        view.addView(news, nl);
                    } else {
                        containt.addView(image, lp);
                    }
                    image.setBackgroundResource(m.icon);
                    image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (null == m.event) {
                                KToast.show("新功能正在打磨中...");
                                return;
                            }
                            listener.onModuleClick(m);
                        }
                    });

                }
//                for (Module m : modes) {
//                    ImageView v = new ImageView(containt.getContext());
//                    int w = width * m.frame.width / total;
//                    int h = width * m.frame.height / total;
//                    int l = width * m.frame.left / total;
//                    int t = width * m.frame.top / total;
//                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(w, h);
//                    lp.setMargins(l, t, 0, 0);
//                    containt.addView(v, lp);
//                    v.setBackgroundResource(m.icon);
//                    v.setImageResource(R.drawable.ic_new);
//                    v.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            if (null == m.event) {
//                                KToast.show("新功能正在打磨中...");
//                                return;
//                            }
//                            listener.onModuleClick(m);
//                        }
//                    });
//                }
            }
        });
    }

    static FrameLayout.LayoutParams layoutParams(Module m, int width) {
        int w = width * m.frame.width / total;
        int h = width * m.frame.height / total;
        int l = width * m.frame.left / total;
        int t = width * m.frame.top / total;
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(w, h);
        lp.setMargins(l, t, 0, 0);
        return lp;
    }

    public interface OnModuleClickListener {
        void onModuleClick(Module module);
    }
}
