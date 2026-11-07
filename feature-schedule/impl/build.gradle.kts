plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.devtoolsKsp)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.egormelnikoff.schedulerutmiit.feature_schedule"
    compileSdk = 37

    defaultConfig {
        minSdk = 29
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.featureSchedule.api)
    implementation(projects.core.ui)
    implementation(projects.core.settings.api)
    implementation(projects.core.navigation)
    implementation(projects.core.network)

    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.room.ktx)
    implementation(libs.hilt.android)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.glance)
    implementation(libs.bundles.navigation)
    implementation(libs.bundles.work)

    testImplementation(libs.bundles.test)
    androidTestImplementation(libs.bundles.androidx.ui.test)
    debugImplementation(libs.bundles.androidx.ui.debug)

    ksp(libs.hilt.android.compiler)
    ksp(libs.androidx.hilt.compiler)
    ksp(libs.androidx.room.compiler)
}