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
-renamesourcefileattribute SourceFile



#-dontoptimize
#As the name suggest, it specifies not to run any optimizations like code inlining, merge classes and class members.
#-dontusemixedcaseclassnames
#By default, obfuscated class names can contain a mix of upper-case characters and lower-case characters. This tells not to use mixed case in class names.
#-dontskipnonpubliclibraryclasses
#It simply tells not to ignore any non-public library classes.
-verbose
#It helps in printing a proper explanation of the R8 Shrinking process. For example if an exception occurs, this option will print out the entire stack trace, instead of just the exception message.
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
#It helps in preserving some attributes that may be required for reflection. For example, the Exceptions, InnerClasses, and Signature attributes are used when processing a library. SourceFile and LineNumberTable attributes for producing obfuscated stack traces with source file name and line number of method. Annotation to keep all the annotations used.
#-keep public class com.google.vending.licensing.ILicensingService
#-keep public class com.android.vending.licensing.ILicensingService
#-keep public class com.google.android.vending.licensing.ILicensingService
#-dontnote com.android.vending.licensing.ILicensingService
#-dontnote com.google.vending.licensing.ILicensingService
#-dontnote com.google.android.vending.licensing.ILicensingService
#Here ILicensingService is nothing but a normal service class which Google Play offers that lets you enforce licensing policies for applications that you publish on Google Play. The Google Play Licensing service is primarily intended for paid applications that wish to verify that the current user did in fact pay for the application on Google Play. dontnote specifies not to print any details about classes with matching names.
#-keepclasseswithmembernames class * {
#    native <methods>;
#}
#keepclasseswithmembernames will preserve both class and its member method names after the shrinking process is over. native method is a Java method whose implementation is also written in another programming language such as C/C++. So this rule is preserving names of native methods and its class name.
#-keepclassmembers public class * extends android.view.View {
#    void set*(***);
#    *** get*();
#}
#keepclassmembers will prevent class fields and methods from being removed or renamed. Some animation api’s like ObjectAnimator uses reflection to change the properties of a view by using its setters and getters. For example ObjectAnimator.ofFloat(icon, "x", 0f, 100f) in this x property of icon changes through reflection. We can avoid this keep rule by using the View.X property instead of hardcoding the property name.
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}
#This rule is helping us in preserving the methods which we used to invoke from android:onClick in xml file in our old Android development days. Otherwise, R8 will mark it as unused method and remove it.
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
#We all use enum somewhere or the other in our project. This rule is preserving the methods like values() & valueOf() of all the enums present in the project.
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}
#Creator is an interface that must be implemented and provided as a public CREATOR field that generates instances of your Parcelable class from a Parcel. This rule is preserving Creator field in each class which implements Parcelable to be accessible in case of reflection.
-keepclassmembers class **.R$* {
    public static <fields>;
}
#When your application is compiled, aapt generates the R class, which contains resource IDs for all the resources in your res/ directory. For each type of resource, there is an R subclass (for example, R.drawable for all drawable resources). The $ here is to include inner classes (drawable, id, etc). For each resource of that type, there is a static integer (for example, R.drawable.icon). This rule is preserving those static fields so that it can be used to retrieve the resource.
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
#@JavascriptInterface annotation which allows exposing methods to JavaScript. Starting from API level 17 and above, only methods explicitly marked with this annotation are available to the Javascript code. This rule is preserving the methods which are annotated with @JavascriptInterface annotation.
-dontnote android.support.**
-dontnote androidx.**
-dontwarn android.support.**
-dontwarn androidx.**
#dontwarn specifies not to print any warning messages about unresolved references and other important problems. As the support libraries contains references to newer platform versions. For example Warning: android.support.v4.app.DialogFragment: can't find referenced class android.support.v4.app.DialogFragment$DialogStyle
-dontwarn android.util.FloatMath
#This rule if for deprecated class FloatMath, but remains for backward compatibility. https://developer.android.com/reference/android/util/FloatMath
#-keep class android.support.annotation.Keep
-keep class androidx.annotation.Keep
#This rule is preserving the @Keep annotation class named Keep in the respective package. It has a fully classified classname.
#-keep @android.support.annotation.Keep class * {*;}
-keep @androidx.annotation.Keep class * {*;}
#This rule specifies that the @Keep annotated elements like methods and classes that are accessed only via reflection should not be removed when the code is minified at build time. As a compiler may think that the code is unused.
#-keepclasseswithmembers class * {
#    @android.support.annotation.Keep <methods>;
#}
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <methods>;
}
#keepclasseswithmembers will prevent both classes and its member fields and methods from being removed or renamed only if all class members are present. This rule will preserve class and its methods which are annotated with @Keep annotation from being removed or renamed.
#-keepclasseswithmembers class * {
#    @android.support.annotation.Keep <fields>;
#}
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <fields>;
}
#Similar to previous keep rule, this rule will preserve class and its fields which are annotated with @Keep annotation from being removed or renamed.
#-keepclasseswithmembers class * {
#    @android.support.annotation.Keep <init>(...);
#}
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <init>(...);
}
#<init> matches any type of constructor of a class. Similar to previous keep rule, this rule will preserve class and its constructors which are annotated with @Keep annotation from being removed or renamed.
-dontnote org.apache.http.**
-dontnote android.net.http.**
#This rule specifies not to print any details for these classes as they are duplicated between android.jar and org.apache.http.legacy.jar.
-dontnote java.lang.invoke.**
#This rule specifies not to print any details for these classes as they are duplicated between android.jar and core-lambda-stubs.jar.

# OkHttp
-keepattributes Signature
-keepattributes *Annotation*
#-keep class com.squareup.okhttp.** { *; }
#-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**


# Retrofit
-keepattributes *Annotation*
-keepattributes Signature
-dontwarn com.squareup.okhttp.*
-dontwarn rx.**
-dontwarn javax.xml.stream.**
-dontwarn com.google.appengine.**
-dontwarn java.nio.file.**
-dontwarn org.codehaus.**



-dontwarn retrofit2.**
-dontwarn org.codehaus.mojo.**
-keepattributes Exceptions
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes RuntimeInvisibleParameterAnnotations

-keepattributes EnclosingMethod
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keepclasseswithmembers interface * {
    @retrofit2.* <methods>;
}
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on RoboVM on iOS. Will not be used at runtime.
#-dontnote retrofit2.Platform$IOS$MainThreadExecutor
# Platform used when running on Java 8 VMs. Will not be used at runtime.
#-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions



# Add any classes the interact with gson
# the following line is for illustration purposes
-keep class com.bonjour.messenger.data.remote.AppService
-keep class com.bonjour.messenger.data.entities** { *; }
-keep class com.bonjour.messenger.data.remote.responce** { *; }




# To support Enum type of class members
-keepclassmembers enum * { *; }

-keepclassmembers class * extends android.app.Activity {
     public void *(android.view.View);
 }
#  firebase crashlatic
 -keepattributes SourceFile,LineNumberTable        # Keep file names and line numbers.
 -keep public class * extends java.lang.Exception  # Optional: Keep custom exceptions.
# google map
 -keep class com.google.maps** { *; }
#  room database
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**
