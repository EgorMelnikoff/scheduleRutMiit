plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.devtoolsKsp)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.egormelnikoff.schedulerutmiit.news"
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
    implementation(projects.coreUi)
    implementation(projects.coreNetwork)

    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.hilt.android)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.navigation)
    implementation(libs.androidx.paging.compose)

    testImplementation(libs.bundles.test)
    androidTestImplementation(libs.bundles.androidx.ui.test)
    debugImplementation(libs.bundles.androidx.ui.debug)

    ksp(libs.hilt.android.compiler)
}