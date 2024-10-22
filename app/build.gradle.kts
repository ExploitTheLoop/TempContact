plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.animetone.incognitocontact"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.animetone.incognitocontact"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.github.gabriel-TheCode:AestheticDialogs:1.3.8")
    implementation("com.google.code.gson:gson:2.8.8")
    implementation("com.github.ybq:Android-SpinKit:1.4.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("io.github.medyo:android-about-page:2.0.0")
    implementation("com.saadahmedev.popup-dialog:popup-dialog:2.0.0")
}