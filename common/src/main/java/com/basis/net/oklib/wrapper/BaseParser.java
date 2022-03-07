package com.basis.net.oklib.wrapper;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;

import com.basis.net.oklib.wrapper.interfaces.IParse;


/**
 * 默认解析器
 */
public class BaseParser implements IParse<Wrapper> {
    @Override
    public Wrapper parse(int httpcode, String json) {
        Wrapper info = new Wrapper();
        info.setCode(httpcode);
        OkUtil.e("BaseParser", "json = " + json);
        JsonElement result = JsonParser.parseString(json);
        if (result instanceof JsonObject) {
            JsonObject resulObj = (JsonObject) result;
            info.setCode(resulObj.get("code").getAsInt());
            if (resulObj.has("msg")) {
                info.setMessage(resulObj.get("msg").getAsString());
            }
            JsonElement data = resulObj.get("data");
            if (data != null && data.isJsonObject()) {
                JsonObject dataJson = (JsonObject) data;
                if (dataJson.has("list")) {//列表数据 必有list
                    if (dataJson.has("total")) { //设置page
                        info.setPage(0, dataJson.get("total").getAsInt());
                    } else {
                        info.setPage(0, 1);
                    }
                    info.setBody(dataJson.get("list")); //body
                } else {
                    info.setBody(data);
                }
            } else if (data != null && data.isJsonArray()) {// data[]
                info.setBody(resulObj.get("data"));
            } else if (data != null) {
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(data);
                info.setBody(JsonParser.parseString(jsonArray.toString()));
            }
        }
        return info;
    }

    @Override
    public boolean ok(int code) {
        return code == 10000 || code == 200 || code == 201;
    }
}
