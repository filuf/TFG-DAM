import java.util.Properties
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "com.raj.slotify"
    compileSdk = 36 // Actualizado a 36 para soportar las nuevas versiones de androidx.activity

    defaultConfig {
        applicationId = "com.raj.slotify"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        manifestPlaceholders += mapOf(
            "appAuthRedirectScheme" to "com.raj.slotify"
        )

        buildConfigField("Boolean", "DEBUG_MODE", "false")
        buildConfigField("String", "SPRING_BASE_URL", "\"http://api.10.0.2.2.nip.io/\"")
        buildConfigField("String", "SPRING_TEST_URL", "\"http://10.0.2.2:8080/\"")

        val mapsKey = localProperties.getProperty("GOOGLE_MAPS_KEY") ?: ""
        manifestPlaceholders["GOOGLE_MAPS_KEY"] = mapsKey
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
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
}

dependencies {
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.hbb20:ccp:2.7.3")
    implementation("net.openid:appauth:0.11.1")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.fatboyindustrial.gson-javatime-serialisers:gson-javatime-serialisers:1.1.2")
    
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.activity)

    // Retrofit
    val retrofit_version = "2.9.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofit_version")
    implementation("com.squareup.retrofit2:converter-gson:$retrofit_version")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Navigation
    val nav_version = "2.7.7"
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")

    // Lifecycle & KTX
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Room
    val room_version = "2.8.1"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    ksp("androidx.room:room-compiler:$room_version")

    // Material & UI
    implementation("com.google.android.material:material:1.11.0")
    implementation("io.coil-kt:coil:2.6.0")
    implementation("com.google.android.flexbox:flexbox:3.0.0") // Corregido: no es androidx
}
