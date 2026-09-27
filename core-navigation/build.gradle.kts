plugins {
    alias(libs.plugins.android.library)
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
    implementation(projects.coreCommon)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.navigation3.ui)

    api(libs.bundles.android.core)

    testImplementation(libs.bundles.test)
}