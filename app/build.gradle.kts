plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp") version "1.9.22-1.0.17"
    id("kotlin-parcelize")
}

android {
    namespace = "com.capstone.catascan"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.capstone.catascan"
        minSdk = 24

        //noinspection OldTargetApi
        targetSdk = 35

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "API_KEY", "\"efe09bc5bfd3439090d672b6dcda3931\"")
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
        viewBinding = true
        buildConfig = true
        mlModelBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    // for navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // for circle imageView
    implementation(libs.circleimageview)
    
    // for set image on imageView
    implementation(libs.glide)

    // for camera
    implementation(libs.androidx.camera.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)

    // for tflite
    implementation(libs.tensorflow.lite.support)
    implementation(libs.tensorflow.lite.metadata)
    implementation(libs.tensorflow.lite.task.vision)

    // for datastore
    implementation(libs.androidx.datastore.preferences)

    // for live data
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // for view model
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // for work
    implementation(libs.androidx.work.runtime)

    // for retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // for room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler.v251)

    // for coroutines
    implementation(libs.kotlinx.coroutines.android)

    // okhttp
    implementation(libs.okhttp)
}