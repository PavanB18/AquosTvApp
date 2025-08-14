plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.aquostvapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.aquostvapp"
        minSdk = 33
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.fragment:fragment:1.6.2")

<<<<<<< HEAD
    // Media3 / ExoPlayer
=======
    // Media3 / ExoPlayer (using consistent versions)
>>>>>>> f6e3d9509fad048079cdaf9a02fb5cae020da94f
    implementation("androidx.media3:media3-exoplayer:1.3.1")
    implementation("androidx.media3:media3-ui:1.3.1")
    implementation("androidx.media3:media3-exoplayer-ima:1.3.1")

    // Optional for Android TV UI
    implementation("androidx.leanback:leanback:1.0.0")

    // Optional: Lifecycle support
    implementation("androidx.lifecycle:lifecycle-runtime:2.7.0")
<<<<<<< HEAD
=======

    // Use a compatible version of the IMA SDK with Media3 1.3.1
    // The version 3.27.0 is too old. A newer version is more likely to be compatible.
    implementation("com.google.ads.interactivemedia.v3:interactivemedia:3.33.0")
>>>>>>> f6e3d9509fad048079cdaf9a02fb5cae020da94f
}