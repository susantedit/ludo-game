# Ludora Production Proguard and R8 Optimization Rules

# Preserve line numbers and source file names for crash symbolication
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Domain Models and Engine State
-keep class game.ludora.core.model.** { *; }
-keep class game.ludora.engine.core.** { *; }
-keep class game.ludora.engine.ludo.** { *; }
-keep class game.ludora.engine.snake.** { *; }
-keep class game.ludora.engine.remix.** { *; }
-keep class game.ludora.engine.ai.** { *; }

# Kotlinx Serialization Rules
-keepattributes *Annotation*, InnerClasses, EnclosingMethod
-keepclassmembers class * {
    companion object *;
}
-keepclasseswithmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}
-keepclassmembers class **$$serializer {
    *;
}
-keepclasseswithmembers class * {
    *** Companion;
}
-keepclassmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}

# Room Database Rules
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Dao interface * { *; }
-keep @androidx.room.Entity class * { *; }
-keep class * extends androidx.room.migration.Migration
-dontwarn androidx.room.paging.**

# Jetpack Compose and UI Rules
-keep class androidx.compose.ui.** { *; }
-keep class androidx.compose.material3.** { *; }
-keep class androidx.compose.runtime.** { *; }
-dontwarn androidx.compose.**

# Coroutines and Concurrency
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}
-dontwarn kotlinx.coroutines.**

# AdProvider and Billing Contracts
-keep class game.ludora.core.model.ad.** { *; }
