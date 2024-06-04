buildscript {
    // For Android projects
    val compileSdkVersion by extra(33) /* Android 13 (TIRAMISU) */
    val targetSdkVersion by extra(33) /* Android 13 (TIRAMISU) */

    // Kotlin version
    val kotlinVersion by extra("1.9.23")

    dependencies {
        classpath("com.android.tools.build:gradle:8.1.0") // For Android projects
        // Note: when updating make sure to update coroutines dependency to match. (For Kotlin projects)
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")
        classpath("io.objectbox:objectbox-gradle-plugin:3.8.0")
    }

    repositories {
        mavenCentral()
        google()
    }
}

allprojects {
    repositories {
        maven("https://jitpack.io")
        mavenCentral()
        google()
    }
}