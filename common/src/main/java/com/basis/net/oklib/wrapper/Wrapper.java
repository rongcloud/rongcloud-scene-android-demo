package com.basis.net.oklib.wrapper;

import androidx.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.basis.net.oklib.wrapper.interfaces.IPage;
import com.basis.net.oklib.wrapper.interfaces.IWrap;

public class Wrapper implements IWrap {
    private int code;
    //net info
    private String message;
    //数据集
    private JsonElement body;
    //页码索引
    private int page = -1;
    //总页
    private int total = 0;

    public void setPage(int page, int total) {
        this.page = page;
        this.total = total;
    }

    @Override
    public int getCode() {
        return code;
    }

    public Wrapper setCode(int code) {
        this.code = code;
        return this;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public JsonElement getBody() {
        return body;
    }

    public void setBody(JsonElement body) {
        this.body = body;
    }

    @Override
    public IPage getPage() {
        if (page < 0) return null;
        return new Page(page, total);
    }

    public boolean ok() {
        return code == 10000 || code == 0;
    }

    @Nullable
    public <T> T get(Class<T> tClass) {
        if (null != body && !body.isJsonNull() && null != tClass) {
            return OkUtil.json2Obj(body, tClass);
        }
        return null;
    }

    @Nullable
    public <T> List<T> getList(Class<T> tClass) {
        if (null != body && !body.isJsonNull() && null != tClass) {
            return OkUtil.json2List(body, tClass);
        }
        return null;
    }

    @Nullable
    public <T> T get(String key, Class<T> tClass) {
        if (null != body && null != tClass && body.isJsonObject() && body.getAsJsonObject().has(key)) {
            return OkUtil.json2Obj(body.getAsJsonObject().get(key), tClass);
        }
        return null;
    }

    @Nullable
    public <T> List<T> getList(String key, Class<T> tClass) {
        if (null != body && null != tClass && body.isJsonObject() && body.getAsJsonObject().has(key)) {
            return OkUtil.json2List(body.getAsJsonObject().get(key), tClass);
        }
        return null;
    }

    /**
     * [{"aaa":1},{"bbb":2}] 解析这种数据
     *
     * @param
     * @return
     */
    @Nullable
    public Map<String, String> getMap() {
        if (null != body && body.isJsonArray()) {
            HashMap<String, String> map = new HashMap<>();
            for (JsonElement element : (JsonArray) body) {
                map.putAll(OkUtil.json2Map(element, new TypeToken<HashMap<String, String>>() {
                }.getType()));
            }
            return map;
        }
        return null;
    }
}

