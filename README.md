# 语聊房Demo介绍

## Demo下载地址
https://www.rongcloud.cn/solution/audio_social
![](https://tva1.sinaimg.cn/large/008i3skNly1gruboi2yg9j31000nealm.jpg)

## 基础架构

融云场景Demo的主要代码模块有以下几类。

* 常规业务： `RxJava` 实现 `MVP` 架构。
* 静态资源：使用 `DataStore` 和 `Room` 配个 `RxJava` 持久化本地数据。
* 网络请求： `Retrofit` 和 `OkHttp` 进行 Http 请求。
* 列表数据源：使用 `RecyclerView` 进行数据列表展示。
* 核心业务开发： 为了提高兼容性，语聊房的核心 `SDK` 基于 Java 1.7 开始，上层业务以 `kotlin` 为主。


## 目录结构

主体目录结构如下。


### 语聊房目录结构

* `common`：包含一些应用的基础组件，如 `BaseActivity`
* `mvp`：项目的核心业务相关代码
* `net`：提供 http 请求能力和接口
* `throwable`：自定义的异常
* `ui`：包含自定义的控件和基础组件
* `utils`：包含一些工具类

### 语聊房核心业务类

* `登录页`：LoginActivity.kt
* `首页`：HomeActivity.kt,HomePresenter.kt
* `语聊房列表`：VoiceRoomListActivity.kt,VoiceRoomListPresenter.kt
* `语聊房`：VoiceRoomActivity.kt,VoiceRoomPresenter.kt,VoiceRoomModel.kt


## 项目配置

首先，确保融云的appkey中，已经开启了`音视频功能` 和 `直播` 功能。
如果没有开启会出现 `join rtc room failed` 的错误。
项目正确运行需要配置`url` 与融云的 `appkey`.
在项目的 `MyApp`文件中。填写设置您的 `appkey`
```kotlin
const val APP_KEY: String = "" 
```
项目中使用到了友盟统计相关功能，如果要集成相关功能，请在 MyApp 中设置相关的 key。如果不用，则直接删除相关代码即可。

```kotlin
 UMConfigure.init(
            this,
            "",
            "",
            UMConfigure.DEVICE_TYPE_PHONE,
            null
        )
```

在项目的 `ApiConstant.kt` 类中添加您的应用服务器 url 相关信息

配置融云的key需要设置文件中

```kotlin
// 服务器地址
const val BASE_URL = ""
// 文件服务器地址
const val FILE_URL = ""
// 融云官网
const val HOME_PAGE = "https://docs.rongcloud.cn/v4/5X/views/scene/voiceroom/android/intro/intro.html"
// 默认头像的地址
const val DEFAULT_PORTRAIT_ULR = "https://cdn.ronghub.com/demo/default/rce_default_avatar.png"
```
## voiceroom1.5 更新
#### setting.gradle个module功能简介
```java
 include ':app'

 include ':callkit'
 // 语聊房
 include ':mvoiceroom'
 // module形式依赖 语聊房SDK
 include ':rcvoiceroom'

 //公共业务模块:评价反馈
 include ':combusis'
 // 公共模块
 include ':common'

 // 编译模块
 include ':processor'
 include ':annotation'
```
- 详情见setting.gradle
#### app/build.gradle修改配置注意事项
特别注意：
- 1.替换key是 一定保留两侧的转义符"",
- 2.替换base url是格式是以"/"结尾
- 3.key不可修改
- 详情见app/build.gradle下的productFlavors节点
```java
productFlavors {
        // 特别注意：
        // 1.替换key是 一定保留两侧的转义符"",
        // 2.替换base url是格式是以"/"结尾
        // 3. key不可修改

        //开发环境
        developerEnvironment {
            dimension 'environment'
            applicationId "cn.rongcloud.voiceroomdemo.dev" // 包名，确保切换环境时不会因为缓存导致出现问题
            buildConfigField("String", "APP_KEY", "\"替换你的融云appkey\"")// 融云appkey
            buildConfigField("String", "UM_APP_KEY", "\"替换你的友盟统计appkey\"")// 盟统计appkey
            //http://192.168.1.9:8080/ 注意："/"结尾
            buildConfigField("String", "BASE_SERVER_ADDRES", "\"替换你的服务器地址\"")
        }

        releaseEnvironment {// 正式环境
            dimension 'environment'
            applicationId "cn.rongcloud.voiceroomdemo" //包名
            buildConfigField("String", "APP_KEY", "\"替换你的融云appkey\"") // 融云appkey
            buildConfigField("String", "UM_APP_KEY", "\"替换你的友盟统计appkey\"")// 友盟统计key
            //格式：http://192.168.1.9:8080/ 注意："/"结尾
            buildConfigField("String", "BASE_SERVER_ADDRES", "\"替换你的服务器地址\"")
        }
    }
```
