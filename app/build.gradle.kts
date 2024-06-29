plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

apply(plugin = "kotlin-kapt")

android {
    namespace = "com.example.splitwise"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.splitwise"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        viewBinding = true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("com.google.android.gms:play-services-auth:21.0.0")
    implementation("androidx.viewpager2:viewpager2:1.1.0")
    implementation("io.realm:realm-android-library:10.8.0")
    implementation("io.realm.kotlin:library-base:1.16.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.21")
    implementation ("io.realm:realm-annotations-processor:10.11.0")
    implementation ("com.facebook.android:facebook-login:17.0.0")
    implementation ("com.github.bumptech.glide:glide:4.16.0")

}
