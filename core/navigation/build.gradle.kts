plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.devtoolsKsp)
    alias(libs.plugins.kotlin.serialization)
}
android {
    namespace = "com.egormelnikoff.schedulerutmiit.core.navigation"
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
}

dependencies {
    implementation(projects.core.common)

    api(libs.bundles.navigation)
    implementation(libs.bundles.android.core)
    implementation(libs.hilt.android)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.bundles.test)

    ksp(libs.hilt.android.compiler)
}