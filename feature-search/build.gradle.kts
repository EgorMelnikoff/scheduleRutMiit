plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.devtoolsKsp)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.egormelnikoff.schedulerutmiit.search"
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
    implementation(projects.scheduleFeature.api)
    implementation(projects.coreUi)
    implementation(projects.coreNavigation)
    implementation(projects.coreNetwork)
    

    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.room.ktx)
    implementation(libs.hilt.android)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.navigation)

    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.androidx.ui.test)
    debugImplementation(libs.bundles.androidx.ui.debug)

    ksp(libs.hilt.android.compiler)
    ksp(libs.androidx.room.compiler)
}