# 语聊房Demo介绍

## Demo下载地址

https://www.rongcloud.cn/solution/audio_social
![](https://tva1.sinaimg.cn/large/008i3skNly1gruboi2yg9j31000nealm.jpg)

## 基础架构

融云场景Demo的主要代码模块有以下几类。

* 常规业务： `RxJava` 实现 `MVP` 架构。
* 静态资源：使用 `DataStore` 和 `Room` 配合 `RxJava` 持久化本地数据。
* 网络请求： `Retrofit` 和 `OkHttp` 进行 Http 请求。
* 列表数据源：使用 `RecyclerView` 进行数据列表展示。
* 核心业务开发： 为了提高兼容性，语聊房的核心 `SDK` 基于 Java 1.7 开始，上层业务以 `java` 为主。

## 目录结构

主体目录结构如下。

### 语聊房目录结构

* `basis`：包含一些应用的基础组件
* `combusis`：封装上层UI模块通用的UI样式和基础类
* `avcall`：视频通话模块相关业务逻辑及UI
* `radioroom`：语音电台模块相关业务逻辑及UI
* `voiceroom`：语聊房模块相关业务逻辑及UI

### 语聊房核心业务类

* `登录页`：LoginActivity.kt
* `首页`：HomeActivity.kt,HomePresenter.kt
* `语聊房列表`：VoiceRoomListActivity.java
* `语聊房`：VoiceRoomActivity.java,VoiceRoomPresenter.java,VoiceRoomModel.java

## 如何运行？

1. 为了方便您快速运行项目，我们为您预置了融云 `appkey` 和对应的测试服务器 `url`，您不需要自己部署测试服务器即可运行。

2. 申请 BusinessToken

- BusinessToken 主要是防止滥用 demo 里的测试 `appKey`，我们为接口做了限制，一个 BusinessToken
  最多可以支持10个用户注册，20天使用时长。点击此处 [获取BusinessToken](https://rcrtc-api.rongcloud.net/code)

3. 申请完 BusinessToken 后,请在项目 `app` 下 `build.gradle` 文件中替换即可，其中友盟 `UM_APP_KEY` 和 Bugly `BUGLY_ID`
   不使用可删除。

```
developerEnvironment {
    buildConfigField("String", "BUSINESS_TOKEN", "\"这里是测试服务器token，需要申请\"")
    buildConfigField("String", "UM_APP_KEY", "\"这里是友盟AppKey\"")
    buildConfigField("String", "BUGLY_ID", "\"这里是Bugly的Id\"")
}
```

