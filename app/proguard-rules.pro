# Groq API models - Gson uses reflection to deserialize these
-keep class com.mykaradainam.data.remote.groq.** { *; }

# Keep fields annotated with @SerializedName
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Retrofit
-keepattributes Signature
-keepattributes *Annotation*
-keep,allowobfuscation interface * extends retrofit2.Call
-dontwarn retrofit2.**

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
