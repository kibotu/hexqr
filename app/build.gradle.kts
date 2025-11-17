plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    kotlin("kapt")
}

android {
    namespace = "net.kibotu.hexqrapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "net.kibotu.hexqrapp"
        minSdk = 23
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        getByName("debug") {
            storeFile = file("keystore/debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
        create("release") {
            val keystorePropertiesFile = rootProject.file("keystore.properties")
            val props = if (keystorePropertiesFile.exists()) {
                val propsMap = mutableMapOf<String, String>()
                keystorePropertiesFile.readLines().forEach { line ->
                    if (line.contains("=") && !line.trimStart().startsWith("#")) {
                        val parts = line.split("=", limit = 2)
                        if (parts.size == 2) {
                            propsMap[parts[0].trim()] = parts[1].trim()
                        }
                    }
                }
                propsMap
            } else {
                emptyMap()
            }
            
            storeFile = file("keystore/release.keystore")
            storePassword = props["storePassword"] ?: System.getenv("KEYSTORE_PASSWORD") ?: "release"
            keyAlias = props["keyAlias"] ?: System.getenv("KEY_ALIAS") ?: "release"
            keyPassword = props["keyPassword"] ?: System.getenv("KEY_PASSWORD") ?: "release"
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
    }
    
    buildFeatures {
        compose = true
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2025.11.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    
    // Compose
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.activity:activity-compose:1.11.0")
    
    // CameraX
    implementation("androidx.camera:camera-core:1.5.1")
    implementation("androidx.camera:camera-camera2:1.5.1")
    implementation("androidx.camera:camera-lifecycle:1.5.1")
    implementation("androidx.camera:camera-view:1.5.1")
    
    // ML Kit Barcode Scanning
    implementation("com.google.mlkit:barcode-scanning:17.3.0")
    
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.4")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.4")
    
    // Hilt
    implementation("com.google.dagger:hilt-android:2.57.2")
    kapt("com.google.dagger:hilt-compiler:2.57.2")
    implementation("androidx.hilt:hilt-navigation-compose:1.3.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.9.6")
    
    // Kotlin Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    
    // Accompanist Permissions
    implementation("com.google.accompanist:accompanist-permissions:0.37.3")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

