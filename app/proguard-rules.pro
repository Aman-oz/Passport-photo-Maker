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

# Keep all data classes across layers
-keep class com.ots.aipassportphotomaker.data.model.** { *; }
-keep class com.ots.aipassportphotomaker.domain.model.** { *; }
-keep class com.ots.aipassportphotomaker.image_picker.model.** { *; }
-keep class com.ots.aipassportphotomaker.presentation.ui.mapper.** { *; }


# Keep Compose related classes
-keepclassmembers class **.ui.** {
    ** Kt*;
}

# Keep Room database entities and DAOs
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *

# Keep Retrofit and network related classes
-keep class com.ots.aipassportphotomaker.data.remote.** { *; }
-keep class com.ots.aipassportphotomaker.data.db.** { *; }

# Keep Kotlin Serialization classes
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep classes with @Serializable annotation
-keepclassmembers @kotlinx.serialization.Serializable class ** {
    *** Companion;
    kotlinx.serialization.KSerializer serializer(...);
}

# For enums used in data classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Preserve the special static methods that are required in all enumeration classes
-keepclassmembers class * extends java.lang.Enum {
    <fields>;
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep View Models
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Keep core application components
-keep class com.ots.aipassportphotomaker.App { *; }

# Keep important debugging information
-keepattributes SourceFile,LineNumberTable
-keepattributes Signature
-keepattributes *Annotation*

# Keep Hilt/Dagger related classes
-keep class javax.inject.** { *; }
-keep class dagger.** { *; }
-keep class * extends dagger.internal.Factory
-keep class * implements javax.inject.Provider

############################
# ================== Gson ==================
-keep class * extends com.google.gson.reflect.TypeToken { *; }
-keep class com.google.gson.Gson { *; }
-keep class com.google.gson.** { *; }
-keep class com.google.gson.GsonBuilder { *; }
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class com.google.gson.reflect.TypeToken
-keep class com.google.gson.stream.** { *; }
-keep,allowshrinking,allowobfuscation class * extends com.google.gson.reflect.TypeToken
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
    @com.google.gson.annotations.Expose <fields>;
}
###################Missing Rules#########################
# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE

# Keep all @Serializable classes and their serializers
-keep class ** implements kotlinx.serialization.KSerializer { *; }
-keep @kotlinx.serialization.Serializable class * { *; }
-keepclassmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}