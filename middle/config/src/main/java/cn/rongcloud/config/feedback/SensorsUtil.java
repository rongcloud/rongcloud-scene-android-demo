package cn.rongcloud.config.feedback;

import android.content.Context;

import com.sensorsdata.analytics.android.sdk.SAConfigOptions;
import com.sensorsdata.analytics.android.sdk.SensorsAnalyticsAutoTrackEventType;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 神策埋点工具类
 *
 * @author lihao
 * @Date 2022/05/09
 */
public class SensorsUtil {

    /*******************自定义事件-事件名*******************/
    public static final String GETCODECLICK = "getCodeClick";       //获取验证码
    public static final String REGISTER = "regButtonClick";       //注册
    public static final String LOGIN = "loginButtonClick"; //登陆
    public static final String JOINROOM = "joinRoom"; //加入房间
    public static final String LEAVEROOM = "quitRoom";//离开房间
    public static final String CREATEROOM = "creatRoom";//创建房间
    public static final String CLOSEROOM = "closeRoom";//关闭房间
    public static final String CONNECTREQUEST = "connectRequest";//发起连线
    public static final String RECALLREQUEST = "connectionWithdraw";//撤销连麦
    public static final String PKCLICK = "pkClick";//点击PK按钮
    public static final String TEXTCLICK = "textClick";//点击消息按钮时触发
    public static final String GIFTCLICK = "giftClick";//点击礼物图标，弹出礼物列表弹窗时触发。
    public static final String SETTINGCLICK = "settingClick";//点击设置功能时触发。
    public static final String DAILCLICK = "dailClick";//点击呼叫按钮时触发。
    public static final String FUNMODULEVIEWCLICK = "functionModuleView";//功能模块访问。
    /*********************************end****************************************/


    /***************全局常量*******************/
    private String demo_platform = "Android";
    private String demo_type = "RCRTC国内版";
    private String demo_type_id = "1";
    private String company_name = "";
    private String demo_version;

    /*********************************end****************************************/


    private SensorsUtil() {
    }

    static class Holder {
        final static SensorsUtil holder = new SensorsUtil();
    }

    public static SensorsUtil instance() {
        return Holder.holder;
    }

    public void init(Context application, String demo_version, String SA_SERVER_URL) {
        this.demo_version = demo_version;
        // 初始化配置
        SAConfigOptions saConfigOptions = new SAConfigOptions(SA_SERVER_URL);

        saConfigOptions
                // 开启全埋点
                .setAutoTrackEventType(SensorsAnalyticsAutoTrackEventType.APP_CLICK |
                        SensorsAnalyticsAutoTrackEventType.APP_START |
                        SensorsAnalyticsAutoTrackEventType.APP_END |
                        SensorsAnalyticsAutoTrackEventType.APP_VIEW_SCREEN)
                //开启崩溃记录
                .enableTrackAppCrash()
                //开启点击分析功能
                .enableHeatMap(true)
                //开启 Log
                .enableLog(true);
        /**
         * 其他配置，如开启可视化全埋点
         */
        // 需要在主线程初始化神策 SDK
        SensorsDataAPI.startWithConfigOptions(application, saConfigOptions);
    }


    /**
     * 记录设定的用户属性
     */
    public void setUserProperties(String phone) {
        try {
            JSONObject properties = new JSONObject();
            properties.put("mobile", phone);
            SensorsDataAPI.sharedInstance().profileSet(properties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 移除用户属性
     */
    public void removeUserProperties() {
        SensorsDataAPI.sharedInstance().profileUnset("mobile");
    }

    /**
     * 公共属性静态
     */
    public void registerSuperProperties(boolean islogin) {
        // 将应用名称作为事件公共属性，后续所有 track() 追踪的事件都会自动带上 "AppName" 属性
        try {
            JSONObject properties = new JSONObject();
            //平台类型
            properties.put("platform_type", demo_platform);
            //APP类型
            properties.put("app_type", demo_type);
            //是否登录
            properties.put("is_login", islogin);
            //demo类型id 
            properties.put("demo_type_id", demo_type_id);
            //demo版本
            properties.put("demo_version", demo_version);
            //企业名称
            properties.put("company_name", company_name);
            SensorsDataAPI.sharedInstance().registerSuperProperties(properties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param service_type 业务类型  登陆｜注册｜找回密码
     */
    public void getCode(String service_type) {
        try {
            JSONObject properties = new JSONObject();
            properties.put("service_type", service_type);
            SensorsDataAPI.sharedInstance().track(GETCODECLICK, properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 注册
     *
     * @param module_name  模块名称
     * @param reg_button   注册按钮名称
     * @param current_page 当前页面
     */
    public void register(String module_name, String reg_button, String current_page) {
        try {
            JSONObject properties = new JSONObject();
            properties.put("module_name", module_name);
            properties.put("reg_button", reg_button);
            properties.put("current_page", current_page);
            SensorsDataAPI.sharedInstance().track(REGISTER, properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 登录
     *
     * @param current_page PC注册页、H5注册页、demo注册页、以及活动注册页
     * @param login_method 密码登录｜验证码登录
     */
    public void login(String current_page, String login_method) {
        try {
            JSONObject properties = new JSONObject();
            properties.put("current_page", current_page);
            properties.put("login_method", login_method);
            SensorsDataAPI.sharedInstance().track(LOGIN, properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加入房间
     *
     * @param room_id       房间 ID
     * @param room_name     房间标题
     * @param is_private    是否加密
     * @param is_speaker_on 声音是否开启
     * @param is_camera_on  相机是否开启
     * @param rcEvent       语聊房｜视频直播｜电台
     */
    public void joinRoom(String room_id, String room_name, boolean is_private, boolean is_speaker_on,
                         boolean is_camera_on, RcEvent rcEvent) {
        try {
            JSONObject properties = new JSONObject();
            properties.put("room_id", room_id);
            properties.put("room_name", room_name);
            properties.put("is_private", is_private);
            properties.put("is_speaker_on", is_speaker_on);
            properties.put("is_camera_on", is_camera_on);
            properties.put("scenes", rcEvent.getName());
            SensorsDataAPI.sharedInstance().track(JOINROOM, properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 离开房间
     *
     * @param room_id       房间 ID
     * @param room_name     房间标题
     * @param is_private    是否加密
     * @param is_speaker_on 声音是否开启
     * @param is_camera_on  相机是否开启
     * @param rcEvent       语聊房｜视频直播｜电台
     * @param duraiton      时长
     */
    public void leaveRoom(String room_id, String room_name, boolean is_private, boolean is_speaker_on, boolean is_camera_on,
                          RcEvent rcEvent, String duraiton) {
        try {
            JSONObject properties = new JSONObject();
            properties.put("room_id", room_id);
            properties.put("room_name", room_name);
            properties.put("is_private", is_private);
            properties.put("is_speaker_on", is_speaker_on);
            properties.put("is_camera_on", is_camera_on);
            properties.put("scenes", rcEvent.getName());
            properties.put("duraiton", duraiton);
            SensorsDataAPI.sharedInstance().track(LEAVEROOM, properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 创建房间
     *
     * @param room_id       房间 ID
     * @param room_name     房间标题
     * @param is_private    是否加密
     * @param is_speaker_on 声音是否开启
     * @param is_camera_on  相机是否开启
     * @param rcEvent       语聊房｜视频直播｜电台
     */
    public void createRoom(String room_id, String room_name, boolean is_private, boolean is_speaker_on,
                           boolean is_camera_on, RcEvent rcEvent) {
        try {
            JSONObject properties = new JSONObject();
            properties.put("room_id", room_id);
            properties.put("room_name", room_name);
            properties.put("is_private", is_private);
            properties.put("is_speaker_on", is_speaker_on);
            properties.put("is_camera_on", is_camera_on);
            properties.put("scenes", rcEvent.getName());
            SensorsDataAPI.sharedInstance().track(CREATEROOM, properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭房间
     *
     * @param room_id   房间 ID
     * @param room_name 房间标题
     * @param duraiton  时长
     * @param rcEvent   语聊房｜视频直播｜电台
     */
    public void closeRoom(String room_id, String room_name, String duraiton, RcEvent rcEvent) {
        try {
            JSONObject properties = new JSONObject();
            properties.put("room_id", room_id);
            properties.put("room_name", room_name);
            properties.put("scenes", rcEvent.getName());
            properties.put("duraiton", duraiton);
            SensorsDataAPI.sharedInstance().track(CLOSEROOM, properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发起连麦
     *
     * @param room_id   房间 ID
     * @param room_name 房间标题
     * @param rcEvent   语聊房｜视频直播｜电台
     */
    public void connectRequest(String room_id, String room_name, RcEvent rcEvent) {
        try {
            JSONObject properties = new JSONObject();
            properties.put("room_id", room_id);
            properties.put("room_name", room_name);
            properties.put("scenes", rcEvent.getName());
            SensorsDataAPI.sharedInstance().track(CONNECTREQUEST, properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 撤销连麦
     *
     * @param room_id   房间 ID
     * @param room_name 房间标题
     * @param rcEvent   语聊房｜视频直播｜电台
     */
    public void recallConnect(String room_id, String room_name, RcEvent rcEvent) {
        try {
            JSONObject properties = new JSONObject();
            properties.put("room_id", room_id);
            properties.put("room_name", room_name);
            properties.put("scenes", rcEvent.getName());
            SensorsDataAPI.sharedInstance().track(RECALLREQUEST, properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 点击PK按钮
     *
     * @param room_id   房间 ID
     * @param room_name 房间标题
     * @param rcEvent   语聊房｜视频直播｜电台
     */
    public void pkClick(String room_id, String room_name, RcEvent rcEvent) {
        try {
            JSONObject properties = new JSONObject();
            properties.put("room_id", room_id);
            properties.put("room_name", room_name);
            properties.put("scenes", rcEvent.getName());
            SensorsDataAPI.sharedInstance().track(PKCLICK, properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 点击消息按钮
     *
     * @param room_id   房间 ID
     * @param room_name 房间标题
     * @param rcEvent   语聊房｜视频直播｜电台
     */
    public void textClick(String room_id, String room_name, RcEvent rcEvent) {
        try {
            JSONObject properties = new JSONObject();
            properties.put("room_id", room_id);
            properties.put("room_name", room_name);
            properties.put("scenes", rcEvent.getName());
            SensorsDataAPI.sharedInstance().track(TEXTCLICK, properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 点击礼物按钮
     *
     * @param room_id   房间 ID
     * @param room_name 房间标题
     * @param rcEvent   语聊房｜视频直播｜电台
     */
    public void giftClick(String room_id, String room_name, RcEvent rcEvent) {
        try {
            JSONObject properties = new JSONObject();
            properties.put("room_id", room_id);
            properties.put("room_name", room_name);
            properties.put("scenes", rcEvent.getName());
            SensorsDataAPI.sharedInstance().track(GIFTCLICK, properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 点击设置按钮
     *
     * @param room_id             房间 ID
     * @param room_name           房间标题
     * @param setting_function    设置功能
     * @param setting_function_id 设置功能id
     * @param rcEvent             语聊房｜视频直播｜电台
     */
    public void settingClick(String room_id, String room_name, String setting_function, String setting_function_id,
                             RcEvent rcEvent) {
        try {
            JSONObject properties = new JSONObject();
            properties.put("room_id", room_id);
            properties.put("room_name", room_name);
            properties.put("setting_function", setting_function);
            properties.put("setting_function_id", setting_function_id);
            properties.put("scenes", rcEvent.getName());
            SensorsDataAPI.sharedInstance().track(SETTINGCLICK, properties);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 呼叫点击
     *
     * @param rcEvent 语聊房｜视频直播｜电台
     */
    public void callClick(RcEvent rcEvent) {
        try {
            JSONObject properties = new JSONObject();
            properties.put("scenes", rcEvent.getName());
            SensorsDataAPI.sharedInstance().track(DAILCLICK, properties);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 点击demo 语聊房、视频直播、语音电台、语音通话、视频通话功能模块时触发
     *
     * @param rcEvent 语聊房、视频直播、语音电台、语音通话、视频通话
     */
    public void functionModuleViewClick(RcEvent rcEvent) {
        try {
            JSONObject properties = new JSONObject();
            properties.put("module_name", rcEvent.getName());
            SensorsDataAPI.sharedInstance().track(FUNMODULEVIEWCLICK, properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 记录激活事件
     */
    public static void trackAppInstall() {
        try {
            JSONObject properties = new JSONObject();
            // 触发激活事件
            // 如果您之前使用 trackInstallation() 触发的激活事件，需要继续保持原来的调用，无需改为 trackAppInstall()，否则会导致激活事件数据分离。
            SensorsDataAPI.sharedInstance().trackAppInstall(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
