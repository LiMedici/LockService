# This is a configuration file for ProGuard.
# http://proguard.sourceforge.net/index.html#manual/usage.html

#代码迭代优化的次数,取值范围0~7,默认为5
-optimizationpasses 5
#包名不使用大小写混合的方式,这样混合后都是小写字母的组合
-dontusemixedcaseclassnames
#混淆时不做预检验,预检验是Proguard四大功能给之一,在Android中一般不需要预检验,加快混淆速度
-dontpreverify
#混淆时是否记录日志
-verbose
# 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
#如果项目中适用到注解,需要保留注解属性
-keepattributes *Annotation*
-keepattributes *Annotation*,InnerClasses

#不混淆泛型
-keepattributes Signature
#保留代码行号,这在混淆厚代码运行中抛出异常信息时,有利于定位出问题的代码
-keepattributes SourceFile,LineNumberTable
#屏蔽警告
-ignorewarnings

# 保持 Android SDK 相关API类不被混淆
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.support.v4.**
-keep public class com.android.vending.licensing.ILicensingService

#support包下混淆配置
-dontwarn android.support.**
#-keep class android.support.**.{*;}

#如果有引用v4包可以添加下面这行
-keep public class * extends android.support.v4.app.Fragment

#保持自定义控件类不被混淆
-keepclasseswithmembers class * {
  public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
  public <init>(android.content.Context, android.util.AttributeSet,int);
}

#保持自定义控件类不被混淆
-keep public class * extends android.view.View {
  public <init>(android.content.Context);
  public <init>(android.content.Context, android.util.AttributeSet);
  public <init>(android.content.Context, android.util.AttributeSet, int);
  public void set*(...);
}

#保持Activity中参数是View类型的函数,保证Layout XML 文件中配置onClick属性的值能够正常调用到
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}

#保持 native 方法不被混淆
-keepclasseswithmembernames class * {
  native <methods>;
}

#保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

#保持 Serializable 不被混淆
-keepnames class * implements java.io.Serializable

#保持 Serializable 不被混淆并且enum 类也不被混淆
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

#保持枚举 enum 类不被混淆 如果混淆报错，建议直接使用上面的 -keepclassmembers class * implements java.io.Serializable即可
-keepclassmembers enum * {
      public static **[] values();
      public static ** valueOf(java.lang.String);
}

#不混淆资源类
-keepclassmembers class **.R$* {
      *;
}

# Understand the @Keep support annotation.
-keep class android.support.annotation.Keep

-keep @android.support.annotation.Keep class * {*;}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <methods>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <fields>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <init>(...);
}
