# 融云 RTC

## 简介

本仓库是融云 RTC 项目开源代码，为开发者提供接入场景化 SDK 代码示例。

## 基础架构

融云 RTC 开发语言以 Java 为主，

* 常规业务： 复杂页面使用 `MVP` 架构，简单页面使用 `MVC` 架构。
* 网络请求： 使用 `OkHttp` 进行 Http 请求。
* 列表数据源：使用 `RecyclerView` 进行数据列表展示。
* 编译环境：请用 java 11 进行项目编译

## 模块结构

主体目录结构如下。

![](https://tva1.sinaimg.cn/large/e6c9d24ely1h0bumg2ki0j20np0b274n.jpg)

### common：通用层

包含了 网络、工具类、公共widget、BaseUI等基础功能和组件

### middle：中间层

* `config`：配置信息模块，包含网络基地址，静态链接，三方SDK的key，ARouter路由地址，用户个人信息等功能
* `beauty`：美颜模块，包含美颜处理和ui展示
* `music`：音乐选择模块，包含音乐选择，混音播放等逻辑，为语聊房和直播房提供播放音乐能力
* `pk`：pk模块，包含pk的处理流程和ui展示，为语聊房和直播房提供跨房间pk能力
* `roomkit`：房间展示相关模块，包含房间列表，房间内通用ui等，为语聊房和直播房提供列表展示和房间内通用ui展示
* `sight`：小视频浏览
* `thirdcdn`：三方 CDN 播放器

### business：业务层

* `profile`：用户信息模块，包括用户登录，信息修改，设置等功能
* `live`：视频直播模块，包含房间列表，创建/加入直播，房间内连麦，跨房间PK，音乐播放等功能
* `voiceroom`：语聊房模块，包含房间列表，创建/加入直播，房间内连麦，跨房间PK，音乐播放等功能
* `radio`：电台房模块，包含房间列表，创建/加入直播，音乐播放等功能
* `call`：音视频通话模块，包含语音拨打视频拨打等功能
* `community`：实时社区模块
* `gameroom`：游戏房模块，包含房间列表，创建/加入游戏房，快速游戏，游戏房间内操作等功能

### app：app入口

包含首页展示等功能

## 模块依赖关系

单独使用某些业务模块时，可以依据依赖关系分离项目

![](https://tva1.sinaimg.cn/large/e6c9d24ely1h0bxojva2jj20j30e4t97.jpg)

## 如何运行？

1. 为了方便您快速运行项目，我们为您预置了融云 `appkey` 和对应的测试服务器 `url`，您不需要自己部署测试服务器即可运行。

2. 申请 BusinessToken

- BusinessToken 主要是防止滥用 demo 里的测试 `appKey`，我们为接口做了限制，一个 BusinessToken
  最多可以支持10个用户注册，20天使用时长。点击此处 [获取BusinessToken](https://rcrtc-api.rongcloud.net/code)

3. 申请完 BusinessToken 后,请在项目 `app` 下 `build.gradle` 文件中替换即可，其中友盟 `UM_APP_KEY` 和 Bugly `BUGLY_ID`
   等不使用可删除。

```
develop {
    dimension 'environment'
    // 包名
    applicationId "cn.rongcloud.voiceroomdemo.dev"
    
    // 融云demo的AppKey，开发者可用来测试
    buildConfigField("String", "APP_KEY", "\"pvxdm17jpw7ar\"")
    // 融云demo对应服务器的测试地址，开发者可用来测试
    buildConfigField("String", "BASE_SERVER_ADDRES", "\"https://rcrtc-api.rongcloud.net/\"")
    
    // 这里是连接测试服务器的token，需要到 https://rcrtc-api.rongcloud.net/code 申请
    buildConfigField("String", "BUSINESS_TOKEN", "\"xxxxx\"")
    
    // 可选,神策地址
    buildConfigField("String", "SENSORS_URL", "\"神策地址\"")
    // 可选，友盟AppKey
    buildConfigField("String", "UM_APP_KEY", "\"这里是友盟AppKey\"")
    // 可选，bugly id
    buildConfigField("String", "BUGLY_ID", "\"这里是Bugly的Id\"")
    // 可选，视频直播三方CDN推流地址，不用三方CDN推拉流可空着
    buildConfigField("String", "THIRD_CDN_PUSH_URL", "\"\"")
    // 可选，视频直播三方CDN拉流地址，不用三方CDN推拉流可空着
    buildConfigField("String", "THIRD_CDN_PULL_URL", "\"\"")
    // 区分国内/国际 国内环境不显示UI 选择地区
    buildConfigField("Boolean", "INTERIAL", "false")
    manifestPlaceholders = [
            APP_NAME         : "@string/app_name_test",
            // hifive音乐服务的 appid和servercode
            HIFIVE_APPID     : "替换您hifive音乐服务的appid",//可选替换
            HIFIVE_SERVERCODE: "替换您hifive音乐服务的servercode"//可选替换
    ]
}
```

4. 测试环境下登录时，验证码输入任意6位数字即可。

5. 项目内置美颜为相芯美颜，如果需要使用美颜功能，需要到相芯美颜申请 `authpack.java` 文件，并放到 `live` 模块包名路径下，替换 authpack 文件。

6. 项目内有三方 CDN 直播示例，如果想体验，可在 `app` 下 `build.gradle` 下增加三方 CDN
   推拉流地址。运行app后在进入视频直播模块，直接创建直播房间，用另一个手机进入房间列表，长按房间会弹出弹框选择订阅方式，可选择加入房间的订阅方式：直播MCU流、融云CDN流、三方CDN流。默认是融云CDN流。

7. 如果编译不通过，改为 JDK 11 。

