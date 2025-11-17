# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep data classes
-keep class net.kibotu.hexqrapp.data.model.** { *; }
-keep class net.kibotu.hexqrapp.domain.** { *; }

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# ML Kit
-keep class com.google.mlkit.** { *; }

# CameraX
-keep class androidx.camera.** { *; }

