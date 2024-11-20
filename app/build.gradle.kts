plugins {
    id("com.android.application")
}

android {
    namespace = "com.jummania.datamanager"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.jummania.datamanager"
        minSdk = 26
        targetSdk = 35
        versionCode = 7
        versionName = "2.7"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
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