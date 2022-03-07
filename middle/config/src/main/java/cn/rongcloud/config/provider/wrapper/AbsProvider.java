package cn.rongcloud.config.provider.wrapper;

import android.text.TextUtils;
import android.util.LruCache;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.basis.wapper.IResultBack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbsProvider<T extends Provide> implements IProvider<T> {
    private final static int MAX_MEMORY = 2 * 1024 * 1024;
    protected final String TAG = getClass().getSimpleName();
    Map<String, IResultBack> singleObservers = new HashMap(4);
    private final LruCache<String, T> lruCache;

    public AbsProvider(int max) {
        lruCache = new LruCache<>(Math.max(max, MAX_MEMORY));
    }

    @Override
    public void update(T t) {
        updateCache(Collections.singletonList(t));
    }


    @Override
    public void update(List<T> updates) {
        updateCache(updates);
    }

    @Override
    public void getAsyn(@NonNull String id, IResultBack<T> resultBack) {
        getAsynWithNeed(id, resultBack, true);
    }

    public T getSync(String key) {
        return lruCache.get(key);
    }

    /**
     * @param id
     * @param resultBack
     * @param needServiceBack 来着observice 需处理两次回调的问题
     */
    private void getAsynWithNeed(@NonNull String id, IResultBack<T> resultBack, boolean needServiceBack) {
        T t = lruCache.get(id);
        if (null != t && null != resultBack) {
            resultBack.onResult(t);
            return;
        }
        provideFromService(Collections.singletonList(id), new IResultBack<List<T>>() {
            @Override
            public void onResult(List<T> ts) {
                if (null != ts && 1 == ts.size()) {
                    T temp = ts.get(0);
                    if (needServiceBack) {//来至observice不需要执行次回调
                        resultBack.onResult(temp);
                    }
                    updateCache(Arrays.asList(temp));
                }
            }
        });
    }

    @Override
    public void batchGetAsyn(@NonNull List<String> ids, @NonNull IResultBack<List<T>> resultBack) {
        final List<T> result = new ArrayList<>();
        int count = ids.size();
        List<String> needNetIds = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String id = ids.get(i);
            T temp = lruCache.get(id);
            if (null != temp) {
                result.add(temp);
            } else {
                needNetIds.add(id);
            }
        }
        if (!needNetIds.isEmpty()) {
            provideFromService(needNetIds, new IResultBack<List<T>>() {
                @Override
                public void onResult(List<T> ts) {
                    if (null != ts) {
                        updateCache(ts);//跟新
                        result.addAll(ts);
                    }
                    if (null != resultBack) resultBack.onResult(result);
                }
            });
        }
    }

    protected final void updateCache(List<T> ts) {
        int count = null == ts ? 0 : ts.size();
        for (int i = 0; i < count; i++) {
            T temp = ts.get(i);
            if (temp != null && !TextUtils.isEmpty(temp.getKey())) {
                lruCache.remove(temp.getKey());
                lruCache.put(temp.getKey(), temp);
                IResultBack<T> singleBack = singleObservers.get(temp.getKey());
                if (null != singleBack) singleBack.onResult(temp);
            }
        }
        if (count > 0) onUpdateComplete(ts);
    }

    /**
     * 清除所有缓存数据
     */
    public void clear() {
        lruCache.evictAll();
    }

    /**
     * 处理关联实体的跟新
     * 比如 VoiceRoom的creater 关联着User， 在跟新VoiceRoom时 顺便跟新一个关联的User实体
     *
     * @param ts
     */
    protected void onUpdateComplete(List<T> ts) {
    }

    public boolean contains(String key) {
        return lruCache.snapshot().containsKey(key);
    }

    @Override
    public void observeSingle(@NonNull String id, @NonNull IResultBack<T> resultBack) {
        if (!singleObservers.containsKey(id)) {
            singleObservers.put(id, resultBack);
        }
        getAsynWithNeed(id, resultBack, false);
    }

    @Override
    public void removeSingleObserver(String key) {
        if (null != key) singleObservers.remove(key);
    }

    @Override
    public abstract void provideFromService(@NonNull List<String> ids, @Nullable IResultBack<List<T>> resultBack);
}
