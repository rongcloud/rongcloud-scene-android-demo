package cn.rong.combusis.oklib;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import okhttp3.Response;

public class Wrapper implements Serializable {
    private int code;
    private JsonElement message;
    private JsonElement data;

    public Wrapper(Response response) {
        try {
            String string = response.body().string();
            Log.e("Wrapper", "string = " + string);
            if (!TextUtils.isEmpty(string)) {
                JsonObject result = JsonParser.parseString(string).getAsJsonObject();
                if (null != result) {
                    this.code = result.get("code").getAsInt();
                    this.message = result.get("msg");
                    this.data = result.get("data");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    public boolean ok() {
        return 10000 == code;
    }

    @NonNull
    public int getCode() {
        return code;
    }

    @NonNull
    public String getMessage() {
        return null != message ? message.getAsString() : "";
    }

    @Nullable
    public JsonElement getData() {
        return data;
    }

    @Nullable
    public <T> T get(Class<T> tClass) {
        if (null != data && !data.isJsonNull() && null != tClass) {
            return GsonUtil.json2Obj(data, tClass);
        }
        return null;
    }

    @Nullable
    public <T> List<T> getList(Class<T> tClass) {
        if (null != data && !data.isJsonNull() && null != tClass) {
            return GsonUtil.json2List(data, tClass);
        }
        return null;
    }
}
