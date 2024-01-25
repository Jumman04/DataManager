plugins {
    id("com.android.application")
}

android {
    namespace = "com.jummania.datamanager"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.jummania.datamanager"
        minSdk = 16
        targetSdk = 34
        versionCode = 1
        versionName = "1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(project(":Library"))
    implementation("androidx.recyclerview:recyclerview:1.3.2")
}