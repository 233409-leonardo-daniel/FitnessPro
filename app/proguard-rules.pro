# ── Stack traces legibles ────────────────────────────────────────────────────
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ── Kotlin ───────────────────────────────────────────────────────────────────
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**

# ── Hilt / Dagger ────────────────────────────────────────────────────────────
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }
-keepclasseswithmembers class * {
    @javax.inject.Inject <fields>;
}
-keepclasseswithmembers class * {
    @javax.inject.Inject <init>(...);
}

# ── Retrofit + Gson ──────────────────────────────────────────────────────────
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keepclassmembernames interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**

# Gson: mantener DTOs del paquete data
-keep class com.alilopez.kt_demohilt.**.data.**.dto.** { *; }
-keep class com.alilopez.kt_demohilt.**.data.**.model.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# ── Room ─────────────────────────────────────────────────────────────────────
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
-dontwarn androidx.room.**

# ── Coil ─────────────────────────────────────────────────────────────────────
-dontwarn coil.**

# ── Google Sign-In / Credentials ─────────────────────────────────────────────
-keep class com.google.android.libraries.identity.googleid.** { *; }
-dontwarn com.google.android.libraries.identity.googleid.**

# ── AdMob ────────────────────────────────────────────────────────────────────
-keep public class com.google.android.gms.ads.** { *; }
-dontwarn com.google.android.gms.ads.**

# ── Firebase / FCM ───────────────────────────────────────────────────────────
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**
-keep class com.alilopez.kt_demohilt.core.notifications.** { *; }

# ── WorkManager ──────────────────────────────────────────────────────────────
-keep class androidx.work.** { *; }
-keep class * extends androidx.work.Worker
-keep class * extends androidx.work.CoroutineWorker
-dontwarn androidx.work.**

# ── MPAndroidChart ───────────────────────────────────────────────────────────
-keep class com.github.mikephil.charting.** { *; }
