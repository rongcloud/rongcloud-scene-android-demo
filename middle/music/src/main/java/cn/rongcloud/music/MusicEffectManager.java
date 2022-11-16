package cn.rongcloud.music;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import cn.rongcloud.musiccontrolkit.bean.Effect;

/**
 * Created by gyn on 2021/11/29
 * 音乐氛围的管理类，这里在本地 assets 中固定了几个，
 * 用户可以根据需求从网络下载或者替换自己音乐
 */
public class MusicEffectManager {
    private static final String TAG = MusicEffectManager.class.getSimpleName();
    private static MusicEffectManager instance;
    private static LinkedHashMap<String, String> effectMap = new LinkedHashMap() {
        {
            put("进场", "intro_effect.mp3");
            put("鼓掌", "clap_effect.mp3");
            put("欢呼", "cheering_effect.mp3");
        }
    };
    private List<Effect> effectList = new ArrayList<>();

    public MusicEffectManager() {
        initEffects();
    }

    public static MusicEffectManager getInstance() {
        if (instance == null) {
            instance = new MusicEffectManager();
        }
        return instance;
    }

    private void initEffects() {
        effectList.clear();
        Effect effect;
        String[] keys = (String[]) effectMap.keySet().toArray(new String[]{});
        for (int i = 0; i < keys.length; i++) {
            effect = new Effect();
            effect.setEffectName(keys[i]);
            effect.setSoundId(i + "");
            effect.setFilePath("file:///android_asset/AudioEffect/" + effectMap.get(keys[i]));
            effectList.add(effect);
        }

    }

    public List<Effect> getEffectList() {
        return effectList;
    }
}
