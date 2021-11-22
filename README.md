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

## 项目配置

首先，确保融云的appkey中，已经开启了`音视频功能` 和 `直播` 功能。 如果没有开启会出现 `join rtc room failed` 的错误。

项目正确运行需要配置`url` 与融云的 `appkey`. 在 `app` 项目的 `build.gradle`文件中。填写设置您的 `APP_KEY` ，`UM_APP_KEY `
，`BASE_SERVER_ADDRES ` ，`BUGLY_ID `等，其中友盟和Bugly的配置项目中不使用可移除。

```
buildConfigField("String", "APP_KEY", "\"这里填写AppKey\"")
buildConfigField("String", "BASE_SERVER_ADDRES", "\"这里是您服务器地址\"")
buildConfigField("String", "UM_APP_KEY", "\"这里是友盟AppKey\"")
buildConfigField("String", "BUGLY_ID", "\"这里是Bugly的Id\"")
```

