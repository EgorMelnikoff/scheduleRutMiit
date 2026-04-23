plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.devtoolsKsp)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.google.services)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "2.3.20"
}

android {
    namespace = "com.egormelnikoff.schedulerutmiit"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.egormelnikoff.schedulerutmiit"
        minSdk = 29
        targetSdk = 37
        versionCode = 74
        versionName = "2.4.2"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            manifestPlaceholders["firebaseAnalyticsDeactivated"] = true
            manifestPlaceholders["crashlyticsCollectionEnabled"] = false
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            manifestPlaceholders["firebaseAnalyticsDeactivated"] = false
            manifestPlaceholders["crashlyticsCollectionEnabled"] = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)

    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.work)
    ksp(libs.hilt.android.compiler)
    ksp(libs.androidx.hilt.compiler)

    implementation(libs.retrofit)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit2.kotlinx.serialization.converter)
    implementation(libs.jsoup)

    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.room.compiler)
    implementation(libs.androidx.datastore.preferences)

    implementation(libs.androidx.work.runtime.ktx)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.coil.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.paging.compose)

    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}