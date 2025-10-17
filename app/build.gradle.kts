import org.gradle.kotlin.dsl.implementation

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("com.google.gms.google-services")
    alias(libs.plugins.hilt.android)
    id("kotlin-kapt")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.ruhaan.otakuclick"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.ruhaan.otakuclick"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.coil.compose)



    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    implementation(libs.androidx.material)

    implementation(libs.androidx.animation)
    implementation(libs.androidx.animation.graphics)

    implementation(libs.androidx.material.icons.extended)

    // Firebase BOM - manages all Firebase library versions
    implementation(platform(libs.firebase.bom))

    // Firebase Authentication
//    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.auth)

    // Firestore Database
//    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.firestore)

    // Firebase Analytics (optional but good for portfolio)
//    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.analytics)

//    // Firebase Cloud Messaging
//    implementation(libs.firebase.messaging.ktx)
//
//    // For notification handling
//    implementation(libs.androidx.work.runtime.ktx)

    // Firebase Cloud Messaging
    implementation(libs.firebase.messaging)

    // For notification handling
    implementation(libs.androidx.work.runtime)

    //new ones
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.play.services)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Room database for caching
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)






}