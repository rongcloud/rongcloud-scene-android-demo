package cn.rong.combusis.music;

import android.os.Environment;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.api.body.FileBody;
import com.basis.net.oklib.api.callback.FileIOCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.kit.UIKit;
import com.kit.utils.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.rong.combusis.api.VRApi;
import cn.rong.combusis.common.net.IResultBack;
import cn.rong.combusis.music.domain.MusicBean;

public class MusicApi {
    // POST
    private final static String Add_MUSIC = VRApi.HOST + "mic/room/music/add";
    // POST
    private final static String MOVE_MUSIC = VRApi.HOST + "mic/room/music/move";
    // POST
    private final static String MUSICS = VRApi.HOST + "mic/room/music/list";
    // POST
    private final static String DELETE_MUSIC = VRApi.HOST + "mic/room/music/delete";

    public static void loadMusics(String roomId, int type, IResultBack<List<MusicBean>> resultBack) {
        Map<String, Object> params = new HashMap<>(8);
        params.put("roomId", roomId);
        params.put("type", type);//类型 0 官方，1 自定义。
        OkApi.post(MUSICS, params, new WrapperCallBack() {

            @Override
            public void onResult(Wrapper result) {
                List<MusicBean> temp = result.getList(MusicBean.class);
                Logger.e("MusicApi", "result count = " + temp.size() + " type = " + type);
                if (null != resultBack) resultBack.onResult(temp);
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                if (null != resultBack) resultBack.onResult(null);
            }
        });
    }


    /**
     * 添加
     *
     * @param roomId
     * @param music
     * @param resultBack
     */
    public static void addMusic(String roomId, MusicBean music, IResultBack<Boolean> resultBack) {
        Map<String, Object> params = new HashMap<>(8);
        params.put("author", music.getAuthor());
        params.put("name", music.getName());
        params.put("roomId", roomId);
        params.put("type", music.getType() == MusicManager.MUSIC_TYPE_SYS ? 2 : 1);//1 本地添加 2 从官方添加
        params.put("url", music.getUrl());
        params.put("size", (int) (music.getSize() * 1024));//单位：转换 kb
        OkApi.post(Add_MUSIC, params, new WrapperCallBack() {

            @Override
            public void onResult(Wrapper result) {
                if (null != result && result.ok() && null != resultBack) {
                    resultBack.onResult(true);
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                if (null != resultBack) resultBack.onResult(false);
            }
        });
    }

    /**
     * 删除
     *
     * @param roomId
     * @param musicId
     * @param resultBack
     */
    public static void deleteMusic(String roomId, int musicId, IResultBack<Boolean> resultBack) {
        Map<String, Object> params = new HashMap<>(8);
        params.put("roomId", roomId);
        params.put("id", musicId);
        OkApi.post(DELETE_MUSIC, params, new WrapperCallBack() {

            @Override
            public void onResult(Wrapper result) {
                if (null != result && result.ok() && null != resultBack) {
                    resultBack.onResult(true);
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                if (null != resultBack) resultBack.onResult(false);
            }
        });
    }

    /**
     * 移动
     *
     * @param roomId
     * @param fromId
     * @param toId
     * @param resultBack
     */
    public static void moveMusic(String roomId, int fromId, int toId, IResultBack<Boolean> resultBack) {
        Map<String, Object> params = new HashMap<>(8);
        params.put("roomId", roomId);
        params.put("fromId", fromId);
        params.put("toId", toId);
        OkApi.post(MOVE_MUSIC, params, new WrapperCallBack() {

            @Override
            public void onResult(Wrapper result) {
                if (null != result && result.ok() && null != resultBack) {
                    resultBack.onResult(true);
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                if (null != resultBack) resultBack.onResult(false);
            }
        });
    }

    /**
     * 下载音频文件
     *
     * @param url
     * @param cacheName
     * @param resultBack
     */
    public static void downloadMusic(String url, String cacheName, IResultBack<File> resultBack) {
        String dir = UIKit.getContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath();
        File file = new File(dir, cacheName);
        if (null != file && file.exists()) {
            if (null != resultBack) resultBack.onResult(file);
            return;
        }
        OkApi.download(url, null, new FileIOCallBack(dir, cacheName) {
            @Override
            public void onResult(File file) {
                if (null != resultBack) resultBack.onResult(file);
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                if (null != resultBack) resultBack.onResult(null);
            }
        });
    }

    public static void uploadMusicFile(String path, IResultBack<String> resultBack) {
        FileBody body = new FileBody("multipart/form-data", new File(path));
        OkApi.file(VRApi.FILE_UPLOAD, "file", body, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                String url = result.getBody().getAsString();
                if (null != resultBack) resultBack.onResult(url);
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                if (null != resultBack) resultBack.onResult("");
            }
        });
    }
}
