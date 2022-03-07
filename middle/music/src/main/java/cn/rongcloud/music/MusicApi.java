package cn.rongcloud.music;

import android.os.Environment;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.OkParams;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.api.body.FileBody;
import com.basis.net.oklib.api.callback.FileIOCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.utils.Logger;
import com.basis.utils.UIKit;
import com.basis.wapper.IResultBack;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.rongcloud.config.ApiConfig;

public class MusicApi {
    public final static int MUSIC_TYPE_SYS = 0;// 系统内置列表
    public final static int MUSIC_TYPE_USER = 1;// 用户播放列表

    public final static int MUSIC_TYPE_LOCAL = 1;// 用户从本地添加
    public final static int MUSIC_TYPE_OFFICIAL = 2;// 用户从官方列表添加
    public final static int MUSIC_TYPE_HI_FIVE = 3;// 用户从HIFIVE添加
    private final static String HOST = ApiConfig.HOST;
    // POST
    private final static String Add_MUSIC = HOST + "mic/room/music/add";
    // POST
    private final static String MOVE_MUSIC = HOST + "mic/room/music/move";
    // POST
    private final static String MUSICS = HOST + "mic/room/music/list";
    // POST
    private final static String DELETE_MUSIC = HOST + "mic/room/music/delete";
    // POST 播放或暂停音乐，同步给服务端
    private final static String PLAY_PAUSE_MUSIC = HOST + "mic/room/music/play";
    // GET 从服务端获取正在播放的音乐
    private final static String PLAYING_MUSIC = HOST + "mic/room/music/play/";

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
        params.put("backgroundUrl", music.getBackgroundUrl());
        params.put("name", music.getName());
        params.put("roomId", roomId);
        params.put("type", music.getType());//1 本地添加 2 从官方添加 3hifive添加
        params.put("thirdMusicId", music.getThirdMusicId());
        params.put("url", music.getUrl());
        params.put("size", (int) (music.getSize()));//单位：转换 kb
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
        OkApi.file(ApiConfig.FILE_UPLOAD, "file", body, new WrapperCallBack() {
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

    /**
     * 请求接口判断是否正在播放音乐
     *
     * @param roomId
     * @param resultBack
     */
    public static void getPlayingMusic(String roomId, IResultBack<MusicBean> resultBack) {
        OkApi.get(PLAYING_MUSIC + roomId, null, new WrapperCallBack() {

            @Override
            public void onResult(Wrapper result) {
                if (result != null && result.ok()) {
                    MusicBean musicBean = result.get(MusicBean.class);
                    if (musicBean != null) {
                        resultBack.onResult(musicBean);
                    } else {
                        resultBack.onResult(null);
                    }
                } else {
                    resultBack.onResult(null);
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                resultBack.onResult(null);
            }
        });
    }

    /**
     * 播放或暂停时告诉服务端状态
     *
     * @param roomId  房间id
     * @param musicId 音乐id，传null代表暂停
     */
    public static void playOrPauseMusic(String roomId, Integer musicId, IResultBack<Boolean> resultBack) {
        OkParams params = new OkParams();
        params.add("roomId", roomId);
        if (musicId != null) {
            params.add("id", musicId);
        }
        OkApi.post(PLAY_PAUSE_MUSIC, params.build(), new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (resultBack != null) {
                    resultBack.onResult(result.ok());
                }
            }
        });
    }
}
