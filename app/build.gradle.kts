plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlinx-serialization")
    id("io.objectbox")
}

android {
    // Gets the latest git commit hash for autoversioning
    val gitCommitHash = providers.exec {
        commandLine("git", "rev-parse", "--short", "HEAD")
    }.standardOutput.asText.get().trim()

    namespace = "anubhav.github.finder"
    compileSdk = 34

    defaultConfig {
        applicationId = "anubhav.github.finder"
        minSdk = 30
        targetSdk = 34
        versionCode = 101
        versionName = "1.01"
        // Latest commit hash as BuildConfig.COMMIT_HASH
        buildConfigField("String", "COMMIT_HASH", "\"$gitCommitHash\"")

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
        // Flag to enable support for the new language APIs: For AGP 4.0
        //coreLibraryDesugaringEnabled true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
        dataBinding = true
    }

    packaging {
        resources.excludes.add("META-INF/*")
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.preference:preference:1.2.1")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("androidx.navigation:navigation-fragment:2.7.5")
    implementation("androidx.navigation:navigation-ui:2.7.5")
    implementation("com.google.android.material:material:1.10.0")

    // kotlin bom
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.9.23"))

    // lifecycle
    val lifecycleVersion = "2.6.0-alpha02"
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")

    // ktor
    val ktorVersion = "1.4.1"
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-serialization-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-auth-jvm:$ktorVersion")

    // retrofit
    val retrofitVersion = "2.9.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")

    // glide
    implementation("com.github.bumptech.glide:glide:4.13.2")

    // rxjava
    implementation("io.reactivex.rxjava3:rxjava:3.1.8")

    // extra
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.intuit.sdp:sdp-android:1.1.1")
    implementation("com.intuit.ssp:ssp-android:1.1.1")
    implementation("com.hivemq:hivemq-mqtt-client:1.3.3")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.maltaisn:icondialog:3.3.0")
    implementation("com.maltaisn:iconpack-default:1.1.0")
    implementation("com.github.skydoves:colorpickerview:2.3.0")
    //implementation("ru.superjob:kotlin-permissions:1.0.3")

    // testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

}