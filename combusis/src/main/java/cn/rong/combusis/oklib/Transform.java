package cn.rong.combusis.oklib;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * ok core 转换工具类
 */
public class Transform {
    public final static MediaType json = MediaType.parse("application/json; charset=utf-8");

    /**
     * @param params Map参数
     * @return RequestBody
     */
    public static RequestBody param2Body(Map<String, Object> params) {
        String jsonParams = GsonUtil.obj2Json(params);
        Log.e("Transform", "params : " + jsonParams);
        return RequestBody.create(json, jsonParams);
    }

    public static RequestBody param2Body(Object params) {
        String jsonParams = GsonUtil.obj2Json(params);
        Log.e("Transform", "params : " + jsonParams);
        return RequestBody.create(json, jsonParams);
    }

    /**
     * get请求时：在url上拼接参数
     *
     * @param url    原url
     * @param params 参数map
     * @return 拼接参数后的url
     */
    public static String urlAppendParam(String url, Map<String, Object> params) {
        if (null == url || null == params || params.isEmpty()) return url;
        Uri.Builder builder = Uri.parse(url).buildUpon();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            builder.appendQueryParameter(entry.getKey(), entry.getValue().toString());
        }
        return builder.build().toString();
    }

}
