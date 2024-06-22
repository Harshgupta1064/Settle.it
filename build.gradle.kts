
buildscript{
    dependencies{
        classpath("com.android.tools.build:gradle:7.0.0") // Adjust version as necessary
        classpath("io.realm:realm-gradle-plugin:10.8.0")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.21")
    }
}// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.realm) apply false
    alias(libs.plugins.google.gms.google.services) apply false
}

