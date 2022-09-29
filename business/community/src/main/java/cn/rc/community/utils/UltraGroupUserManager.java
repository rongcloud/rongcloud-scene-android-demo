package cn.rc.community.utils;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.api.core.Dispatcher;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.utils.GsonUtil;
import com.basis.utils.Logger;
import com.basis.wapper.IResultBack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.rc.community.CommunityAPI;
import cn.rc.community.bean.UltraGroupUserBean;
import cn.rc.community.helper.CommunityHelper;
import cn.rongcloud.config.ApiConfig;
import cn.rongcloud.config.provider.wrapper.AbsProvider;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/5/13
 * @time 12:10
 * 社区用户信息缓存 ，key为用户ID+社区ID，保持每个社区的唯一性
 */
public class UltraGroupUserManager extends AbsProvider<UltraGroupUserBean> {

    private final static UltraGroupUserManager manager = new UltraGroupUserManager();

    public UltraGroupUserManager() {
        super(-1);
    }

    public static UltraGroupUserManager getInstance() {
        return manager;
    }

    @Override
    public void update(List<UltraGroupUserBean> updates) {
        super.update(updates);
    }

    @Override
    public void provideFromService(@NonNull List<String> keys, @Nullable IResultBack<List<UltraGroupUserBean>> resultBack) {
        if (null == keys || keys.isEmpty()) {
            if (null != resultBack) resultBack.onResult(new ArrayList<>());
            return;
        }
        // 拿到用户ID
        String userId = keyToId(keys.get(0));
        Map<String, Object> params = new HashMap<>(4);
        params.put("userUid", userId);
        params.put("communityUid", CommunityHelper.getInstance().getCommunityUid());
        OkApi.post(CommunityAPI.Community_User_info, params, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                Log.e(TAG, GsonUtil.obj2Json(result));
                List<UltraGroupUserBean> beans = result.getList(UltraGroupUserBean.class);
                if (null != resultBack) {
                    if (beans != null) {
                        //从服务器拿到了，可以去更新结果并且返回去
                        resultBack.onResult(beans);
                    } else {
                        //从服务器没有拿到，尝试再次从缓存中取,如果缓存也没取出来，可以稍等一会在再次去取
                        UltraGroupUserBean sync = getSync(userId);
                        if (sync == null) {
                            Dispatcher.get().dispatch(new Runnable() {
                                @Override
                                public void run() {
                                    UltraGroupUserBean bean = getSync(userId);
                                    if (beans != null) {
                                        resultBack.onResult(Arrays.asList(bean));
                                    } else {
                                        //未取到值
                                        Logger.e("UltraGroupUserBean:null");
                                    }
                                }
                            }, 100);
                        } else {
                            resultBack.onResult(Arrays.asList(sync));
                        }
                    }
                }

            }

            @Override
            public void onError(int code, String msg) {
                if (null != resultBack) resultBack.onResult(null);
            }
        });
    }

    @Override
    public boolean contains(String id) {
        return super.contains(idToKey(id));
    }

    @Override
    public UltraGroupUserBean getSync(String id) {
        return super.getSync(idToKey(id));
    }

    @Override
    public void getAsyn(@NonNull String id, IResultBack<UltraGroupUserBean> resultBack) {
        super.getAsyn(idToKey(id), resultBack);
    }

    @Override
    public void batchGetAsyn(@NonNull List<String> ids, @NonNull IResultBack<List<UltraGroupUserBean>> resultBack) {
        ArrayList<String> keys = new ArrayList<>();
        for (String id : ids) {
            keys.add(idToKey(id));
        }
        super.batchGetAsyn(keys, resultBack);
    }

    @Override
    public void observeSingle(@NonNull String id, @NonNull IResultBack<UltraGroupUserBean> resultBack) {
        super.observeSingle(idToKey(id), resultBack);
    }

    @Override
    public void removeSingleObserver(String id) {
        super.removeSingleObserver(idToKey(id));
    }

    /**
     * 将ID转换为key
     *
     * @param id
     * @return
     */
    public String idToKey(String id) {
        return id + "-%-" + CommunityHelper.getInstance().getCommunityUid();
    }

    /**
     * 将key转为id
     *
     * @param key
     * @return
     */
    public String keyToId(String key) {
        int i = key.indexOf("-%-");
        if (i > -1) {
            return key.substring(0, i);
        }
        return key;
    }
}
