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

# --- SUPABASE & KTOR RULES ---

# Keep Ktor (Networking engine)
-keep class io.ktor.** { *; }
-keepnames class io.ktor.** { *; }

# Keep Kotlinx Serialization (JSON parsing)
-keep class kotlinx.serialization.** { *; }
-keepattributes *Annotation*, InnerClasses, EnclosingMethod, Signature

# Keep Supabase Client
-keep class io.github.jan.supabase.** { *; }
-dontwarn io.github.jan.supabase.**

# If you are using Coroutines deeply
-keepnames class kotlinx.coroutines.** { *; }