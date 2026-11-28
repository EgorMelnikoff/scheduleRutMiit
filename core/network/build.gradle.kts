plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.devtoolsKsp)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.egormelnikoff.schedulerutmiit.core.network"
    compileSdk = 37

    defaultConfig {
        minSdk = 29

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            consumerProguardFiles("proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    implementation(projects.core.common)

    api(libs.retrofit)
    api(libs.jsoup)
    api(libs.kotlinx.serialization.json)

    implementation(libs.hilt.android)
    implementation(libs.retrofit2.kotlinx.serialization.converter)

    testImplementation(libs.bundles.test)

    ksp(libs.hilt.android.compiler)
}