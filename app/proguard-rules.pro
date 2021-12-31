# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# 表示不要提示警告
-dontwarn
-dontoptimize
# 代码循环优化次数，0-7，默认为5
-optimizationpasses 5
# 包名不使用大小写混合 aA Aa
-dontusemixedcaseclassnames
# 不混淆第三方引用的库
-dontskipnonpubliclibraryclasses
# 不做预校验
-dontpreverify
#忽略警告
-ignorewarnings
#混淆时所采用的优化规则
#-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*,!class/unboxing/enum

# 混淆后生产映射文件 map 类名->转化后类名的映射
# 存放在app\build\outputs\mapping\release中
-verbose
# 混淆前后的映射
-printmapping mapping.txt
# apk 包内所有 class 的内部结构
-dump class_files.txt
# 未混淆的类和成员
-printseeds seeds.txt
# 列出从 apk 中删除的代码
-printusage unused.txt

# 抛出异常时保留代码行号
# 这个最后release的时候关闭掉
-keepattributes SourceFile,LineNumberTable

# 保护注解
-keepattributes *Annotation*
-keep class * extends java.lang.annotation.Annotation {*;}

# 泛型与反射
-keepattributes Signature
-keepattributes EnclosingMethod

# 不混淆内部类
-keepattributes InnerClasses

# 不混淆异常类
-keepattributes Exceptions

-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * extends android.os.IInterface{*;}
-keep class * extends android.os.Binder{*;}

# 所有View的子类及其子类的get、set方法都不进行混淆
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

#不混淆Activity中参数类型为View的所有方法
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

#不混淆Parcelable和它的子类，还有Creator成员变量
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
# Serializable
-keepnames class * implements java.io.Serializable
-keep public class * implements java.io.Serializable {
   public *;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#不混淆R类里及其所有内部static类中的所有static变量字段
-keepclassmembers class **.R$* {
    public static <fields>;
}

# 添加白名单 ------------------------

# 友盟
-keep class com.umeng.** {*;}
-keep class com.uc.** {*;}
-keep class com.efs.** {*;}

-keep class com.zui.**{*;}
-keep class com.miui.**{*;}
-keep class com.heytap.**{*;}
-keep class a.**{*;}
-keep class com.vivo.**{*;}

-keep class android.support.** {*;}
-keep class cn.rongcloud.rtc.core.** {*;}
-keep class cn.rongcloud.rtc.api.** {*;}
-keep class cn.rongcloud.rtc.base.** {*;}
-keep class cn.rongcloud.rtc.utils.** {*;}
-keep class cn.rongcloud.rtc.media.http.** {*;}
-keep class cn.rongcloud.rtc.engine.view** {*;}
-keep class cn.rongcloud.rtc.proxy.message.** {*;}
-keep class cn.rongcloud.rtc.RongRTCExtensionModule {*;}
-keep class cn.rongcloud.rtc.RongRTCMessageRouter {*;}
# voiceroom 保留api相关保
-keep class cn.rongcloud.voice.api.** {*;}
-keep class cn.rongcloud.voice.model.** {*;}
-keep class cn.rongcloud.voice.utils.** {*;}
-keep class cn.rongcloud.messager.** {*;}

# test
#-keep class com.** {*;}
#modle
-keep class cn.rongcloud.voiceroomdemo.net.api.bean.** {*;}
# bugly start
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}
# bugly end