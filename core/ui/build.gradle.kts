plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.devtoolsKsp)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.egormelnikoff.schedulerutmiit.core.ui"
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
    implementation(projects.core.navigation)
    implementation(projects.core.settings.api)
    implementation(projects.core.common)

    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.bundles.compose)
    api(libs.coil.compose)

    androidTestImplementation(libs.bundles.androidx.ui.test)
    debugImplementation(libs.bundles.androidx.ui.debug)

    testImplementation(libs.bundles.test)

    ksp(libs.hilt.android.compiler)
}