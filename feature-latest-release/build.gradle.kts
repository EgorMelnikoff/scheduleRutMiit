plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.devtoolsKsp)
}

android {
    namespace = "com.egormelnikoff.schedulerutmiit.latest_release"
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
    implementation(projects.coreNetwork)

    implementation(libs.hilt.android)

    testImplementation(libs.bundles.test)

    ksp(libs.hilt.android.compiler)
}