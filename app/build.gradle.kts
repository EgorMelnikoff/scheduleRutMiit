plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.devtoolsKsp)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.google.services)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.egormelnikoff.schedulerutmiit"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.egormelnikoff.schedulerutmiit"
        minSdk = 29
        targetSdk = 37
        versionCode = 90
        versionName = "2.7.4"
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
    implementation(projects.coreUi)
    implementation(projects.coreNetwork)
    implementation(projects.featureSchedule)
    implementation(projects.featureSearch)
    implementation(projects.featureCurriculum)
    implementation(projects.featureLatestRelease)
    implementation(projects.featureNews)
    implementation(projects.featureExport)
    implementation(projects.featureTasks)

    implementation(platform(libs.firebase.bom))
    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom))

    implementation(libs.bundles.android.core)
    implementation(libs.bundles.work)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.navigation)
    implementation(libs.bundles.glance)
    implementation(libs.firebase.analytics)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.hilt.android)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.bundles.test)
    androidTestImplementation(libs.bundles.androidx.ui.test)
    debugImplementation(libs.bundles.androidx.ui.debug)

    ksp(libs.hilt.android.compiler)
    ksp(libs.androidx.hilt.compiler)
}