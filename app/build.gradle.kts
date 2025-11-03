plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    id ("kotlin-parcelize")
    alias(libs.plugins.serialization)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

val applicationName = "AI Passport Photo Maker"
val versionMajor = 1
val versionMinor = 0
val versionPatch = 6

android {
    namespace = "com.ots.aipassportphotomaker"
    compileSdk = 36

    signingConfigs {
        create("release") {
            storeFile = file("./keystore/PhotoIDMaker.jks")
            storePassword = "123456"
            keyAlias = "key0"
            keyPassword = "123456"
        }
    }

    defaultConfig {
        applicationId = "com.tas.passport.id.maker"
        minSdk = 26
        targetSdk = 36

        versionCode = versionMajor * 10000 + versionMinor * 1000 + versionPatch * 100 + versionPatch
        versionName = "${versionMajor}.${versionMinor}.${versionPatch}"

        setProperty("archivesBaseName", "${applicationName}_vc${versionCode}_vn${versionName}")

        resourceConfigurations.addAll(listOf("en", "ar", "de", "es", "fr", "hi", "it", "ja", "nl", "pl", "pt","tr"))

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            resValue("string","app_id","ca-app-pub-3940256099942544~3347511713")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            
            signingConfig = signingConfigs.getByName("release")

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            resValue("string","app_id","ca-app-pub-6412217023250030~2393763735")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.13"
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }

    lint {
        disable += "NullSafeMutableLiveData"
    }

    androidResources {
        generateLocaleConfig = true
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

    implementation(libs.coil.compose.video)
    implementation(libs.coil.compose.gif)

    implementation(libs.media.downloader)
    implementation(libs.accompanist.permission)
    implementation(libs.compose.capturable)
    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.pager.indicator)

    implementation(libs.androidx.constraintlayout.compose)
//    implementation(libs.compose.colorpicker)
    implementation (libs.colorpicker)

    implementation("androidx.window:window:1.5.0")

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.messaging.platform)
    implementation(libs.firebase.config)

    implementation(libs.android.gms)
//    implementation(libs.android.gms.base)

    implementation(libs.lottie.compose)

    implementation(libs.android.billingclient)
    implementation(libs.android.inapp.billing.v3)

    // AndroidX
    implementation(libs.appcompat)
    implementation(libs.lifecycle.viewmodel.ktx)

    // Extensions
    implementation(libs.lifecycle.extensions)

    // okHttp
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    // GSON
    implementation(libs.gson)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.retrofit2.kotlin.coroutines.adapter)

    // Room
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)

    implementation(libs.room.ktx)
    implementation(libs.room.paging)
    implementation(libs.paging.common.ktx)

    // Hilt
    implementation(libs.hilt.dagger.android)
    implementation(libs.hilt.work)
    ksp(libs.hilt.dagger.compiler)
    ksp(libs.hilt.compiler)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Paging
    implementation(libs.paging.compose)

    // WorkManager
    implementation(libs.work.runtime.ktx)

    // Coil
    implementation(libs.coil.compose)

    // Navigation
    implementation(libs.navigation.compose)
    implementation(libs.hilt.navigation.compose)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.accompanist.systemuicontroller)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    implementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}