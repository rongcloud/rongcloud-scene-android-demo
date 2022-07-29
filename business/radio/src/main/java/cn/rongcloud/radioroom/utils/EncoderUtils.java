package cn.rongcloud.radioroom.utils;

import android.text.TextUtils;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * 加密
 */
public class EncoderUtils {
    private final static String APP = "rcrtc";
    // 格式 host/app/stream
    private final static String PUSH_RTMP_HOST = "";
    private final static String PUll_RTMP_HOST = "";

    /**
     * 构建rtmp 地址，格式： host/app/stream
     *
     * @param roomId 房间Id
     * @param push   是否是推流，true：推流 false：拉流
     * @return rtmp address
     */
    public static String formatRtmpUrl(String roomId, boolean push) {
        if (TextUtils.isEmpty(PUll_RTMP_HOST) || TextUtils.isEmpty(PUSH_RTMP_HOST)){
            throw new IllegalArgumentException("请先设置推流和拉流域名！！！");
        }
        return (push ? PUSH_RTMP_HOST : PUll_RTMP_HOST) + "/" + APP + "/" + roomId;
    }

    public static String coverSteamID(String roomId, String appId) {
        String hex = null;
        try {
            String uniString = URLEncoder.encode(appId + "_" + roomId, "UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(uniString.getBytes(StandardCharsets.UTF_8));
            String base64 = Base64.encodeToString(digest, Base64.NO_WRAP);
            hex = convertByteArrayToHex(base64.getBytes());
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hex;
    }


    private static String convertByteArrayToHex(byte[] byteArray) {
        // 首先初始化一个字符数组，用来存放每个16进制字符
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        // new一个字符数组，这个就是用来组成结果字符串的（解释一下：一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方））
        char[] resultCharArray = new char[byteArray.length * 2];

        // 遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }

        // 字符数组组合成字符串返回
        return new String(resultCharArray);
    }


}
