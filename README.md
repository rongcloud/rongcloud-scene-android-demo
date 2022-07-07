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
* `mhui`：美颜模块，包含美颜处理和ui展示
* `music`：音乐选择模块，包含音乐选择，混音播放等逻辑，为语聊房和直播房提供播放音乐能力
* `pk`：pk模块，包含pk的处理流程和ui展示，为语聊房和直播房提供跨房间pk能力
* `roomkit`：房间展示相关模块，包含房间列表，房间内通用ui等，为语聊房和直播房提供列表展示和房间内通用ui展示
* `sight`：小视频浏览

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
   不使用可删除。

4. 测试环境下登录时，验证码输入任意6位数字即可。

```
developerEnvironment {
    buildConfigField("String", "BUSINESS_TOKEN", "\"这里是测试服务器token，需要申请 https://rcrtc-api.rongcloud.net/code\"")
    buildConfigField("String", "UM_APP_KEY", "\"这里是友盟AppKey\"")
    buildConfigField("String", "BUGLY_ID", "\"这里是Bugly的Id\"")
    buildConfigField("String", "MH_APP_KEY", "\"这里是美狐美颜SDK的appkey，要想体验美颜功能需要去美狐官网申请http://www.facegl.com/\"")
    manifestPlaceholders = [
                    // hifive音乐服务的 appid和servercode
                    HIFIVE_APPID     : "替换您hifive音乐服务的appid",//可选替换
                    HIFIVE_SERVERCODE: "替换您hifive音乐服务的servercode"//可选替换
            ]
}
```

