package com.basis.utils;


import com.google.gson.reflect.TypeToken;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: BaiCQ
 * @ClassName: ObjToSP
 * @Description: 实体Entity缓存sharepreference 基类
 */
public class ObjToSP<T> {
    protected final String TAG = this.getClass().getSimpleName();
    /**
     * 保持实例的sp文件名称
     */
    private String spFileName;
    private Class<T> tcalss;
    private TypeToken<T> token;

    public ObjToSP(String spFileName) {
        this.spFileName = spFileName;
        //获取第一个泛型
        tcalss = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public ObjToSP(String spFileName, TypeToken token) {
        this.spFileName = spFileName;
        this.token = token;
    }

    private void saveJson(String jsonK, String jsonV) {
        SharedPreferUtil.set(spFileName, jsonK, jsonV);
    }

    private String getJson(String jsonK) {
        return SharedPreferUtil.get(spFileName, jsonK, "");
    }

    private String deleteJson(String jsonK) {
        String deleteJson = SharedPreferUtil.get(spFileName, jsonK, "");
        SharedPreferUtil.remove(spFileName, jsonK);
        return deleteJson;
    }

    private void fastDelete(String jsonK) {
        SharedPreferUtil.remove(spFileName, jsonK);
    }

    /**
     * 获取指定key的实体信息
     *
     * @param keyId
     * @return
     */
    protected final T getEntity(String keyId) {
        if (null != token) {
            return GsonUtil.json2Obj(getJson(keyId), token);
        } else {
            return GsonUtil.json2Obj(getJson(keyId), tcalss);
        }
    }

    /**
     * 获取所有实体信息
     *
     * @return
     */
    protected final List<T> getAllEntity() {
        List<T> tList = new ArrayList<>();
        Map<String, String> allMap = (Map<String, String>) SharedPreferUtil.getAll(spFileName);
        if (null != allMap) {
            for (String objson : allMap.values()) {
                if (null != token) {
                    tList.add(GsonUtil.json2Obj(objson, token));
                } else {
                    tList.add(GsonUtil.json2Obj(objson, tcalss));
                }
            }
        }
        return tList;
    }


    /**
     * 实体信息保存指定key
     *
     * @param keyId key
     * @param t     实体
     */
    protected final void saveEntity(String keyId, T t) {
        if (null == t) return;
        saveJson(keyId, GsonUtil.obj2Json(t));
    }

    /**
     * 删除指定key的实体 并返回该实体
     *
     * @param keyId key
     * @return 被删除的实体
     */
    protected final T deleteEntity(String keyId) {
        if (null != token) {
            return GsonUtil.json2Obj(deleteJson(keyId), token);
        } else {
            return GsonUtil.json2Obj(deleteJson(keyId), tcalss);
        }
    }

    /**
     * 删除指定key的实体 快速删除 不返回被删除的实体
     *
     * @param keyId key
     */
    protected final void deleteFast(String keyId) {
        fastDelete(keyId);
    }

}
