// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        jcenter()
        mavenCentral()
    }
    dependencies {
        classpath ("com.google.gms:google-services:4.4.0")
    }
}
plugins {
    id("com.android.application") version "8.1.1" apply false
}